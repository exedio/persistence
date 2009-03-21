/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.util;

import java.util.ArrayList;

public final class Pool<E>
{
	public interface Factory<E>
	{
		E create();
		boolean isValidOnGet(E e);
		boolean isValidOnPut(E e);
		void dispose(E e);
	}

	// TODO: allow changing pool size
	// TODO: implement idle timeout
	//       ensure, that idle connections in the pool do
	//       not stay idle for a indefinite time,
	//       but are closed after a certain time to avoid
	//       running into some idle timeout implemented by the
	//       jdbc driver or the database itself.
	//       maybe then no ring buffer is needed.
	
	private final Factory<E> factory;

	private final E[] idle;
	private int idleCount, idleFrom, idleTo;
	private final Object lock = new Object();
	
	private final PoolCounter counter;
	private volatile int invalidOnGet = 0;
	private volatile int invalidOnPut = 0;
	
	public Pool(final Factory<E> factory, final int idleLimit, final int idleInitial, final PoolCounter counter)
	{
		if(factory==null)
			throw new NullPointerException("factory must not be null");
		if(idleLimit<0)
			throw new IllegalArgumentException("idleLimit must not be negative, but was " + idleLimit);
		if(idleInitial<0)
			throw new IllegalArgumentException("idleInitial must not be negative, but was " + idleInitial);
		if(idleInitial>idleLimit)
			throw new IllegalArgumentException("idleInitial must not be greater than idleLimit, but was " + idleInitial + " and " + idleLimit);
		
		this.factory = factory;
		
		this.idle = idleLimit>0 ? cast(new Object[idleLimit]) : null;
		
		this.idleCount = idleInitial;
		this.idleFrom = 0;
		this.idleTo = idleInitial;
		for(int i = 0; i<idleInitial; i++)
			idle[i] = factory.create();
		
		this.counter = counter;
	}
	
	@SuppressWarnings("unchecked") // OK: no generic arrays
	private E[] cast(final Object[] o)
	{
		return (E[])o;
	}
	
	private int inc(int pos)
	{
		pos++;
		return (pos==idle.length) ? 0 : pos;
	}
	
	public E get()
	{
		if(counter!=null)
			counter.incrementGet();

		E result = null;

		do
		{
			synchronized(lock)
			{
				if(idle!=null && idleCount>0)
				{
					result = idle[idleFrom];
					idle[idleFrom] = null; // do not reference active connections
					idleCount--;
					idleFrom = inc(idleFrom);
				}
			}
			if(result==null)
				break;
			
			// Important to do this outside the synchronized block!
			if(factory.isValidOnGet(result))
				break;
			
			invalidOnGet++;
			
			result = null;
		}
		while(true);

		// Important to do this outside the synchronized block!
		if(result==null)
			result = factory.create();
		return result;
	}
	
	/**
	 * TODO: If we want to implement changing connection parameters on-the-fly
	 * somewhere in the future, it's important, that client return connections
	 * to exactly the same instance of ConnectionPool.
	 */
	public void put(final E e)
	{
		if(e==null)
			throw new NullPointerException();
		
		if(counter!=null)
			counter.incrementPut();

		// IMPORTANT:
		// Do not let a closed connection be put back into the pool.
		if(!factory.isValidOnPut(e))
		{
			invalidOnPut++;
			throw new IllegalArgumentException("invalid on put");
		}
			
		synchronized(lock)
		{
			if(idle!=null && idleCount<idle.length)
			{
				idle[idleTo] = e;
				idleCount++;
				idleTo = inc(idleTo);
				return;
			}
		}
		
		// Important to do this outside the synchronized block!
		factory.dispose(e);
	}
	
	public void flush()
	{
		if(idle!=null)
		{
			// make a copy of idle to avoid closing idle connections
			// inside the synchronized block
			final ArrayList<E> copyOfIdle = new ArrayList<E>(idle.length);
	
			synchronized(lock)
			{
				if(idleCount==0)
					return;
	
				int f = idleFrom;
				for(int i = 0; i<idleCount; i++)
				{
					copyOfIdle.add(idle[f]);
					idle[f] = null; // do not reference closed connections
					f = inc(f);
				}
				idleCount = 0;
				idleFrom = idleTo;
			}
			
			for(final E e : copyOfIdle)
			{
				try
				{
					factory.dispose(e);
				}
				catch(Exception ex)
				{
					System.err.println("warning: exception on flushing connection pool");
					ex.printStackTrace();
					System.err.println("/warning: exception on flushing connection pool");
				}
			}
		}
	}
	
	public Info getInfo()
	{
		return new Info(idleCount, invalidOnGet, invalidOnPut, counter!=null ? new PoolCounter(counter) : counter);
	}
	
	public static final class Info
	{
		private final int idleCount;
		private final int invalidOnGet;
		private final int invalidOnPut;
		private final PoolCounter counter;
		
		public Info(
				final int idleCount,
				final int invalidOnGet,
				final int invalidOnPut,
				final PoolCounter counter)
		{
			if(counter==null)
				throw new NullPointerException();
			
			this.idleCount = idleCount;
			this.invalidOnGet = invalidOnGet;
			this.invalidOnPut = invalidOnPut;
			this.counter = counter;
		}
		
		public int getIdleCounter()
		{
			return idleCount;
		}
		
		public int getInvalidOnGet()
		{
			return invalidOnGet;
		}
		
		public int getInvalidOnPut()
		{
			return invalidOnPut;
		}
		
		public PoolCounter getCounter()
		{
			return counter;
		}
	}
}

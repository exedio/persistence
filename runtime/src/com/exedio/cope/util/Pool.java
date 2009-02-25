/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
		boolean isValidFromIdle(E e);
		boolean isValidIntoIdle(E e);
		void dispose(E e);
	}

	// TODO: allow changing pool size
	// TODO: implement idle timout
	//       ensure, that idle connections in the pool do
	//       not stay idle for a indefinite time,
	//       but are closed after a certain time to avoid
	//       running into some idle timeout implemented by the
	//       jdbc driver or the database itself.
	//       maybe then no ring buffer is needed.
	
	private final Factory<E> factory;
	private final PoolCounter counter;

	private final E[] idle;
	private int idleCount, idleFrom, idleTo;
	private final Object lock = new Object();
	
	private volatile int invalidFromIdle = 0;
	private volatile int invalidIntoIdle = 0;
	
	public Pool(final Factory<E> factory, final int idleLimit, final int idleInitial)
	{
		// TODO throw nice exceptions
		assert factory!=null;
		assert idleLimit>=0;
		assert idleInitial>=0;
		assert idleInitial<=idleLimit;
		
		this.factory = factory;
		
		// TODO: make this customizable and disableable
		this.counter = new PoolCounter();

		this.idle = idleLimit>0 ? cast(new Object[idleLimit]) : null;
		
		this.idleCount = idleInitial;
		this.idleFrom = 0;
		this.idleTo = idleInitial;
		for(int i = 0; i<idleInitial; i++)
			idle[i] = factory.create();
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
			if(factory.isValidFromIdle(result))
				break;
			
			invalidFromIdle++;
			
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
		
		counter.incrementPut();

		// IMPORTANT:
		// Do not let a closed connection be put back into the pool.
		if(!factory.isValidIntoIdle(e))
		{
			invalidIntoIdle++;
			throw new IllegalArgumentException("unexpected closed connection");
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
		return new Info(idleCount, invalidFromIdle, invalidIntoIdle, new PoolCounter(counter));
	}
	
	public static final class Info
	{
		private final int idleCount;
		private final int invalidFromIdle;
		private final int invalidIntoIdle;
		private final PoolCounter counter;
		
		public Info(
				final int idleCount,
				final int invalidFromIdle,
				final int invalidIntoIdle,
				final PoolCounter counter)
		{
			if(counter==null)
				throw new NullPointerException();
			
			this.idleCount = idleCount;
			this.invalidFromIdle = invalidFromIdle;
			this.invalidIntoIdle = invalidIntoIdle;
			this.counter = counter;
		}
		
		public int getIdleCounter()
		{
			return idleCount;
		}
		
		public int getInvalidFromIdle()
		{
			return invalidFromIdle;
		}
		
		public int getInvalidIntoIdle()
		{
			return invalidIntoIdle;
		}
		
		public PoolCounter getCounter()
		{
			return counter;
		}
	}
}

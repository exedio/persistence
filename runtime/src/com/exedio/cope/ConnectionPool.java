/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import java.util.ArrayList;

import com.exedio.cope.util.ConnectionPoolInfo;
import com.exedio.cope.util.PoolCounter;

final class ConnectionPool<E>
{
	interface Factory<E>
	{
		E createConnection();
		boolean isValid(E e);
		boolean isValidOnPut(E e);
		void dispose(E e);
	}

	// TODO: allow changing pool size
	// TODO: gather pool effectivity statistics
	// TODO: use a ring buffer instead of a stack
	//       to avoid connections at the bottom of the stack
	//       staying idle for a very long time and possibly
	//       running into some idle timeout implemented by the
	//       jdbc driver or the database itself.
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
	private int idleCount;
	private final Object lock = new Object();
	
	ConnectionPool(final Factory<E> factory, final int idleLimit, final int idleInitial)
	{
		assert factory!=null;
		assert idleLimit>=0;
		assert idleInitial>=0;
		
		this.factory = factory;
		
		// TODO: make this customizable and disableable
		this.counter = new PoolCounter(new int[]{0,1,2,4,6,8,10,15,20,25,30,40,50,60,70,80,90,100});

		this.idle = idleLimit>0 ? cast(new Object[idleLimit]) : null;
		
		assert idleInitial<=idleLimit;
		this.idleCount = idleInitial;
		for(int i = 0; i<idleInitial; i++)
			idle[i] = factory.createConnection();
	}
	
	@SuppressWarnings("unchecked") // OK: no generic arrays
	private E[] cast(final Object[] o)
	{
		return (E[])o;
	}
	
	//private static long timeInChecks = 0;
	//private static long numberOfChecks = 0;

	public E getConnection()
	{
		counter.incrementGet();

		E result = null;

		do
		{
			synchronized(lock)
			{
				if(idle!=null && idleCount>0)
				{
					//System.out.println("connection pool: fetch "+(size-1));
					result = idle[--idleCount];
					idle[idleCount] = null; // do not reference active connections
				}
			}
			if(result==null)
				break;
			
			// Important to do this outside the synchronized block!
			if(factory.isValid(result))
				break;
			result = null;
		}
		while(true);
		//System.out.println("connection pool: CREATE");

		// Important to do this outside the synchronized block!
		if(result==null)
			result = factory.createConnection();
		return result;
	}
	
	/**
	 * TODO: If we want to implement changing connection parameters on-the-fly
	 * somewhere in the future, it's important, that client return connections
	 * to exactly the same instance of ConnectionPool.
	 */
	public void putConnection(final E connection)
	{
		if(connection==null)
			throw new NullPointerException();
		
		counter.incrementPut();

		// IMPORTANT:
		// Do not let a closed connection be put back into the pool.
		if(!factory.isValidOnPut(connection))
			throw new IllegalArgumentException("unexpected closed connection");
			
		synchronized(lock)
		{
			if(idle!=null && idleCount<idle.length)
			{
				//System.out.println("connection pool: store "+idleCount);
				idle[idleCount++] = connection;
				return;
			}
		}
		
		//System.out.println("connection pool: CLOSE ");

		// Important to do this outside the synchronized block!
		factory.dispose(connection);
	}
	
	void flush()
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
	
				//System.out.println("connection pool: FLUSH "+size);
				for(int i = 0; i<idleCount; i++)
				{
					copyOfIdle.add(idle[i]);
					idle[i] = null; // do not reference closed connections
				}
				idleCount = 0;
			}
			
			for(final E c : copyOfIdle)
				factory.dispose(c);
		}
	}
	
	ConnectionPoolInfo getInfo()
	{
		return new ConnectionPoolInfo(idleCount, new PoolCounter(counter));
	}
}

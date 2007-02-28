/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Simulates the behaviour of a set of pools with different pool sizes,
 * and collects statistics about the efficiency of such pools.
 * Useful for determining pool sizes.
 *
 * @author Ralf Wiebicke
 */
public final class PoolCounter
{
	private final long start;
	private final Object lock = new Object();
	private final Pool[] pools;

	private int getCounter = 0;
	private int putCounter = 0;

	public PoolCounter(final int... sizes)
	{
		if(sizes.length<1)
			throw new IllegalArgumentException("number of sizes must be at least 1");
		
		for(int s : sizes)
		{
			if(s<=0)
				throw new IllegalArgumentException("sizes must be greater than zero");
		}
		
		for(int i=1; i<sizes.length; i++)
		{
			if(sizes[i-1]>=sizes[i])
				throw new IllegalArgumentException("sizes must be strictly monotonic increasing");
		}
		
		this.start = System.currentTimeMillis();
		final Pool[] pools = new Pool[sizes.length];
		for(int i = 0; i<sizes.length; i++)
			pools[i] = new Pool(sizes[i]);
		this.pools = pools;
	}

	public PoolCounter(final PoolCounter source)
	{
		this.start = source.start;
		final Pool[] pools = new Pool[source.pools.length];
		for(int i = 0; i<pools.length; i++)
			pools[i] = new Pool(source.pools[i]);
		this.pools = pools;
		this.getCounter = source.getCounter;
		this.putCounter = source.putCounter;
	}

	public final void incrementGet()
	{
		synchronized(lock)
		{
			getCounter++;
			for(int i = 0; i<pools.length; i++)
				pools[i].get();
		}
	}

	public final void incrementPut()
	{
		synchronized(lock)
		{
			putCounter++;
			for(int i = 0; i<pools.length; i++)
				pools[i].put();
		}
	}
	
	public List<Pool> getPools()
	{
		return Collections.unmodifiableList(Arrays.asList(pools));
	}
	
	public Date getStart()
	{
		return new Date(start);
	}

	public final int getGetCounter()
	{
		return getCounter;
	}
	
	public final int getPutCounter()
	{
		return putCounter;
	}
	
	public final class Pool
	{
		private final int size;

		private int idleCount = 0;
		private int idleCountMax = 0;

		private int createCounter = 0;
		private int destroyCounter = 0;
		
		private Pool(final int size)
		{
			this.size = size;

			assert size>0;
		}

		private Pool(final Pool source)
		{
			this.size = source.size;
			this.idleCount = source.idleCount;
			this.idleCountMax = source.idleCountMax;
			this.createCounter = source.createCounter;
			this.destroyCounter = source.destroyCounter;
		}

		private final void get()
		{
			if(idleCount>0)
				idleCount--;
			else
				createCounter++;
		}

		private final void put()
		{
			if(idleCount<size)
			{
				if((++idleCount)>idleCountMax)
					idleCountMax = idleCount;
			}
			else
				destroyCounter++;
		}
		
		public final int getSize()
		{
			return size;
		}
		
		public final int getIdleCount()
		{
			return idleCount;
		}
		
		public final int getIdleCountMax()
		{
			return idleCountMax;
		}
		
		public final int getCreateCounter()
		{
			return createCounter;
		}
		
		public final int getDestroyCounter()
		{
			return destroyCounter;
		}
		
		public final boolean isConsistent()
		{
			return (getCounter - putCounter) == (createCounter - destroyCounter - idleCount);
		}

		public final int getLoss()
		{
			final int getCounter = PoolCounter.this.getCounter;
			return (getCounter==0) ? 0 : ((100*destroyCounter)/getCounter);
		}
	}
}

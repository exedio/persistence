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
	
	private final int[] size;
	private final int[] idleCount;
	private final int[] idleCountMax;
	private final int[] createCounter;
	private final int[] destroyCounter;

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
		this.size = sizes;
		this.idleCount      = new int[sizes.length];
		this.idleCountMax   = new int[sizes.length];
		this.createCounter  = new int[sizes.length];
		this.destroyCounter = new int[sizes.length];
	}

	public PoolCounter(final PoolCounter source)
	{
		this.start = source.start;
		this.size = source.size;
		this.idleCount      = copy(source.idleCount);
		this.idleCountMax   = copy(source.idleCountMax);
		this.createCounter  = copy(source.createCounter);
		this.destroyCounter = copy(source.destroyCounter);
		this.getCounter = source.getCounter;
		this.putCounter = source.putCounter;
	}
	
	private static final int[] copy(final int[] array)
	{
		final int[] result = new int[array.length];
		for(int i = 0; i<array.length; i++)
			result[i] = array[i];
		return result;
	}

	public final void incrementGet()
	{
		synchronized(lock)
		{
			getCounter++;
			for(int i = 0; i<size.length; i++)
			{
				if(idleCount[i]>0)
					idleCount[i]--;
				else
					createCounter[i]++;
			}
		}
	}

	public final void incrementPut()
	{
		synchronized(lock)
		{
			putCounter++;
			for(int i = 0; i<size.length; i++)
			{
				if(idleCount[i]<size[i])
				{
					if((++idleCount[i])>idleCountMax[i])
						idleCountMax[i] = idleCount[i];
				}
				else
					destroyCounter[i]++;
			}
		}
	}
	
	public List<Pool> getPools()
	{
		final Pool[] result = new Pool[size.length];
		for(int i = 0; i<size.length; i++)
			result[i] = new Pool(size[i], idleCount[i], idleCountMax[i], createCounter[i], destroyCounter[i]);
		return Collections.unmodifiableList(Arrays.asList(result));
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

		private final int idleCount;
		private final int idleCountMax;

		private final int createCounter;
		private final int destroyCounter;
		
		private Pool(final int size, final int idleCount, final int idleCountMax, final int createCounter, final int destroyCounter)
		{
			this.size = size;
			this.idleCount = idleCount;
			this.idleCountMax = idleCountMax;
			this.createCounter = createCounter;
			this.destroyCounter = destroyCounter;

			assert size>0;
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

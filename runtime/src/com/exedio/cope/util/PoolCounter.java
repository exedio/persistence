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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Simulates the behaviour of a set of pools with different pool idle limits,
 * and collects statistics about the efficiency of such pools.
 * Useful for determining pool idle limits.
 *
 * @author Ralf Wiebicke
 */
public final class PoolCounter
{
	private final long start;
	private final Object lock = new Object();
	
	private final int[] idleLimit;
	private final int[] idle;
	private final int[] idleMax;
	private final int[] create;
	private final int[] destroy;
	private int count;

	private int get = 0;
	private int put = 0;

	public PoolCounter()
	{
		this(1,2,4,6,8,10,15,20,25,30,40,50,60,70,80,90,100,150,200,250,300,400,500,600,700,800,900,1000);
	}
	
	public PoolCounter(final int... idleLimits)
	{
		if(idleLimits.length<1)
			throw new IllegalArgumentException("number of idleLimits must be at least 1");
		
		for(int s : idleLimits)
		{
			if(s<=0)
				throw new IllegalArgumentException("idleLimits must be greater than zero");
		}
		
		for(int i=1; i<idleLimits.length; i++)
		{
			if(idleLimits[i-1]>=idleLimits[i])
				throw new IllegalArgumentException("idleLimits must be strictly monotonic increasing");
		}
		
		this.start = System.currentTimeMillis();
		this.idleLimit = idleLimits;
		this.idle    = new int[idleLimits.length];
		this.idleMax = new int[idleLimits.length];
		this.create  = new int[idleLimits.length];
		this.destroy = new int[idleLimits.length];
		this.count = 1;
	}

	public PoolCounter(final PoolCounter source)
	{
		this.start = source.start;
		this.idleLimit = source.idleLimit;
		this.idle    = copy(source.idle);
		this.idleMax = copy(source.idleMax);
		this.create  = copy(source.create);
		this.destroy = copy(source.destroy);
		this.get = source.get;
		this.put = source.put;
		this.count = source.count;
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
			get++;
			
			final int count = this.count;
			for(int i = 0; i<count; i++)
			{
				final int idleI = idle[i];
				
				if(idleI>0)
					idle[i] = idleI-1;
				else
					create[i]++;
			}
		}
	}

	public final void incrementPut()
	{
		synchronized(lock)
		{
			put++;

			int count = this.count;
			for(int i = 0; i<count; i++)
			{
				int idleI = idle[i];
				
				if(idleI<idleLimit[i])
				{
					idle[i] = ++idleI;

					if(idleI>idleMax[i])
						idleMax[i] = idleI;
				}
				else
				{
					final int destroyI = destroy[i];
					
					if(destroyI==0 && count<idleLimit.length)
					{
						assert i==(count-1);
						idle   [count] = idleI;
						idleMax[count] = idleMax[i];
						create [count] = create[i];
						destroy[count] = 0/*equals to destroy[i]*/;
						count++; // causes another iteration
						this.count = count;
					}
					destroy[i] = destroyI+1;
				}
			}
		}
	}
	
	public List<Pool> getPools()
	{
		final ArrayList<Pool> result = new ArrayList<Pool>(idleLimit.length);
		synchronized(lock)
		{
			final int count = this.count;
			for(int i = 0; i<count; i++)
				result.add(new Pool(idleLimit[i], idle[i], idleMax[i], create[i], destroy[i]));
		}
		return Collections.unmodifiableList(result);
	}
	
	public Date getStart()
	{
		return new Date(start);
	}

	public final int getGetCounter()
	{
		return get;
	}
	
	public final int getPutCounter()
	{
		return put;
	}
	
	public final class Pool
	{
		private final int idleLimit;
		private final int idle;
		private final int idleMax;
		private final int create;
		private final int destroy;
		
		private Pool(final int idleLimit, final int idle, final int idleMax, final int create, final int destroy)
		{
			this.idleLimit = idleLimit;
			this.idle = idle;
			this.idleMax = idleMax;
			this.create = create;
			this.destroy = destroy;

			assert idleLimit>0;
			assert idle>=0;
			assert idle<=idleLimit;
			assert idleMax>=0;
			assert idleMax>=idle;
			assert idleMax<=idleLimit;
			assert create>=0;
			assert destroy>=0;
		}

		/**
		 * @deprecated renamed to {@link #getIdleLimit()}.
		 */
		@Deprecated
		public final int getSize()
		{
			return getIdleLimit();
		}
		
		public final int getIdleLimit()
		{
			return idleLimit;
		}
		
		public final int getIdleCount()
		{
			return idle;
		}
		
		public final int getIdleCountMax()
		{
			return idleMax;
		}
		
		public final int getCreateCounter()
		{
			return create;
		}
		
		public final int getDestroyCounter()
		{
			return destroy;
		}
		
		public final boolean isConsistent()
		{
			return (get - put) == (create - destroy - idle);
		}

		public final int getLoss()
		{
			final int getCounter = PoolCounter.this.get;
			return (getCounter==0) ? 0 : ((100*destroy)/getCounter);
		}
	}
}

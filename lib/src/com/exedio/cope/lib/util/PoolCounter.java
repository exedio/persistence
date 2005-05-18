/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.lib.util;

import java.util.ArrayList;
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
	private final long start = System.currentTimeMillis();
	private final Object lock = new Object();
	private final Pool[] pools;

	private int getCounter = 0;
	private int putCounter = 0;

	public PoolCounter()
	{
		final ArrayList pools = new ArrayList();
		pools.add(new Pool(2));
		pools.add(new Pool(5));
		pools.add(new Pool(8));
		pools.add(new Pool(10));
		pools.add(new Pool(15));
		pools.add(new Pool(20));
		pools.add(new Pool(30));
		pools.add(new Pool(50));
		this.pools = (Pool[])pools.toArray(new Pool[pools.size()]);
	}

	public final void get()
	{
		synchronized(lock)
		{
			getCounter++;
			for(int i = 0; i<pools.length; i++)
				pools[i].get();
		}
	}

	public final void put()
	{
		synchronized(lock)
		{
			putCounter++;
			for(int i = 0; i<pools.length; i++)
				pools[i].put();
		}
	}
	
	public List getPools()
	{
		return Collections.unmodifiableList(Arrays.asList(pools));
	}
	
	public Date getStart()
	{
		return new Date(start);
	}

	public final class Pool
	{
		final int size;

		private int level = 0;
		private int maxLevel = 0;

		private int createCounter = 0;
		private int destroyCounter = 0;
		
		private Pool(final int size)
		{
			this.size = size;
		}

		private final void get()
		{
			if(level>0)
				level--;
			else
				createCounter++;
		}

		private final void put()
		{
			if(level<size)
			{
				if((++level)>maxLevel)
					maxLevel = level;
			}
			else
				destroyCounter++;
		}
		
		public final int getSize()
		{
			return size;
		}
		
		public final int getLevel()
		{
			return level;
		}
		
		public final int getMaxLevel()
		{
			return maxLevel;
		}
		
		public final int getGetCounter()
		{
			return getCounter;
		}
		
		public final int getPutCounter()
		{
			return putCounter;
		}
		
		public final int getCreateCounter()
		{
			return createCounter;
		}
		
		public final int getDestroyCounter()
		{
			return destroyCounter;
		}

		public final int getEfficiency()
		{
			final int getCounter = PoolCounter.this.getCounter;
			return (getCounter==0) ? 0 : (100 - ((100*destroyCounter)/getCounter));
		}

	}

}

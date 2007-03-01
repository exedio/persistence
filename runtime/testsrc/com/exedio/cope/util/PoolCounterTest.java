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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;


public class PoolCounterTest extends CopeAssert
{
	public void testIt()
	{
		final Date before = new Date();
		final PoolCounter c = new PoolCounter(1,2);
		final Date after = new Date();
		assertWithin(before, after, c.getStart());
		
		final Iterator pi = c.getPools().iterator();
		PoolCounter.Pool p1 = (PoolCounter.Pool)pi.next();
		PoolCounter.Pool p2 = null;
		assertFalse(pi.hasNext());
		assertIt(c, 0, 0);
		assertIt(p1, 1, 0, 0, 0, 0, 0);
		
		c.incrementGet();
		assertEquals(1, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = null;
		assertIt(c, 1, 0); assertIt(p1,1, 0, 0, 1, 0,  0);
		
		c.incrementGet();
		assertEquals(1, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = null;
		assertIt(c, 2, 0); assertIt(p1,1, 0, 0, 2, 0,  0);
		
		c.incrementPut();
		assertEquals(1, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = null;
		assertIt(c, 2, 1); assertIt(p1,1, 1, 1, 2, 0,  0);
		
		c.incrementPut();
		assertEquals(2, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = c.getPools().get(1);
		assertIt(c, 2, 2); assertIt(p1,1, 1, 1, 2, 1, 50); assertIt(p2,2, 2, 2, 2, 0, 0);
		
		c.incrementPut();
		assertEquals(2, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = c.getPools().get(1);
		assertIt(c, 2, 3); assertIt(p1,1, 1, 1, 2, 2,100); assertIt(p2,2, 2, 2, 2, 1,50);
		
		c.incrementGet();
		assertEquals(2, c.getPools().size());
		p1 = c.getPools().get(0);
		p2 = c.getPools().get(1);
		assertIt(c, 3, 3); assertIt(p1,1, 0, 1, 2, 2, 66); assertIt(p2,2, 1, 2, 2, 1,33);
		
		final PoolCounter c2 = new PoolCounter(c);
		final Iterator p2i = c2.getPools().iterator();
		final PoolCounter.Pool p21 = (PoolCounter.Pool)p2i.next();
		final PoolCounter.Pool p22 = (PoolCounter.Pool)p2i.next();
		assertFalse(p2i.hasNext());
		assertIt(c2, 3, 3); assertIt(p21,1, 0, 1, 2, 2, 66); assertIt(p22,2, 1, 2, 2, 1,33);
	}
	
	public void testExtend()
	{
		final PoolCounter c = new PoolCounter(1,2,4,6);
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 0, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 1, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 2, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 3, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 4, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 5, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 6, 0, 0);
		}
		c.incrementGet();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 0, 0, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(1, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(2, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 1, 14);
			assertIt(ps.get(1), 2, 2, 2, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(3, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 2, 28);
			assertIt(ps.get(1), 2, 2, 2, 7, 1, 14);
			assertIt(ps.get(2), 4, 3, 3, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(3, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 3, 42);
			assertIt(ps.get(1), 2, 2, 2, 7, 2, 28);
			assertIt(ps.get(2), 4, 4, 4, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(4, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 4, 57);
			assertIt(ps.get(1), 2, 2, 2, 7, 3, 42);
			assertIt(ps.get(2), 4, 4, 4, 7, 1, 14);
			assertIt(ps.get(3), 6, 5, 5, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(4, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 5, 71);
			assertIt(ps.get(1), 2, 2, 2, 7, 4, 57);
			assertIt(ps.get(2), 4, 4, 4, 7, 2, 28);
			assertIt(ps.get(3), 6, 6, 6, 7, 0, 0);
		}
		c.incrementPut();
		{
			final List<PoolCounter.Pool> ps = c.getPools();
			assertEquals(4, ps.size());
			assertIt(ps.get(0), 1, 1, 1, 7, 6, 85);
			assertIt(ps.get(1), 2, 2, 2, 7, 5, 71);
			assertIt(ps.get(2), 4, 4, 4, 7, 3, 42);
			assertIt(ps.get(3), 6, 6, 6, 7, 1, 14);
		}
	}

	static final void assertIt(final PoolCounter p, final int getCounter, final int putCounter)
	{
		assertEquals(getCounter, p.getGetCounter());
		assertEquals(putCounter, p.getPutCounter());
	}

	static final void assertIt(
			final PoolCounter.Pool p, final int size,
			final int idleCount, final int idleCountMax,
			final int createCounter, final int destroyCounter,
			final int loss)
	{
		assertEquals("size", size, p.getSize());
		assertEquals("idleCount", idleCount, p.getIdleCount());
		assertEquals("idleCountMax", idleCountMax, p.getIdleCountMax());
		assertEquals("createCounter", createCounter, p.getCreateCounter());
		assertEquals("destroyCounter", destroyCounter, p.getDestroyCounter());
		assertEquals("loss", loss, p.getLoss());
		assertTrue("isConsistent", p.isConsistent());
	}
	
	public void testFail()
	{
		try
		{
			new PoolCounter((int[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			new PoolCounter(new int[0]);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("number of sizes must be at least 1", e.getMessage());
		}
		try
		{
			new PoolCounter(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("sizes must be greater than zero", e.getMessage());
		}
		try
		{
			new PoolCounter(1,1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("sizes must be strictly monotonic increasing", e.getMessage());
		}
	}
}

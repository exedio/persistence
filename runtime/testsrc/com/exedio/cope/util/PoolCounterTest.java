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

import com.exedio.cope.junit.CopeAssert;


public class PoolCounterTest extends CopeAssert
{
	public void testIt()
	{
		final Date before = new Date();
		final PoolCounter c = new PoolCounter(new int[]{0,2});
		final Date after = new Date();
		assertWithin(before, after, c.getStart());
		
		final Iterator pi = c.getPools().iterator();
		final PoolCounter.Pool p0 = (PoolCounter.Pool)pi.next();
		final PoolCounter.Pool p2 = (PoolCounter.Pool)pi.next();
		assertFalse(pi.hasNext());
		assertIt(c, 0, 0);
		assertIt(p0, 0, 0, 0, 0, 0, 0);
		assertIt(p2, 2, 0, 0, 0, 0, 0);
		
		c.incrementGet();
		assertIt(c, 1, 0); assertIt(p0,0, 0, 0, 1, 0,  0); assertIt(p2,2, 0, 0, 1, 0, 0);
		
		c.incrementGet();
		assertIt(c, 2, 0); assertIt(p0,0, 0, 0, 2, 0,  0); assertIt(p2,2, 0, 0, 2, 0, 0);
		
		c.incrementPut();
		assertIt(c, 2, 1); assertIt(p0,0, 0, 0, 2, 1, 50); assertIt(p2,2, 1, 1, 2, 0, 0);
		
		c.incrementPut();
		assertIt(c, 2, 2); assertIt(p0,0, 0, 0, 2, 2,100); assertIt(p2,2, 2, 2, 2, 0, 0);
		
		c.incrementPut();
		assertIt(c, 2, 3); assertIt(p0,0, 0, 0, 2, 3,150); assertIt(p2,2, 2, 2, 2, 1,50);
		
		c.incrementGet();
		assertIt(c, 3, 3); assertIt(p0,0, 0, 0, 3, 3,100); assertIt(p2,2, 1, 2, 2, 1,33);
		
		final PoolCounter c2 = new PoolCounter(c);
		final Iterator p2i = c2.getPools().iterator();
		final PoolCounter.Pool p20 = (PoolCounter.Pool)p2i.next();
		final PoolCounter.Pool p22 = (PoolCounter.Pool)p2i.next();
		assertFalse(p2i.hasNext());
		assertIt(c2, 3, 3); assertIt(p20,0, 0, 0, 3, 3,100); assertIt(p22,2, 1, 2, 2, 1,33);
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
		assertEquals(size, p.getSize());
		assertEquals(idleCount, p.getIdleCount());
		assertEquals(idleCountMax, p.getIdleCountMax());
		assertEquals(createCounter, p.getCreateCounter());
		assertEquals(destroyCounter, p.getDestroyCounter());
		assertEquals(loss, p.getLoss());
		assertTrue(p.isConsistent());
	}

}

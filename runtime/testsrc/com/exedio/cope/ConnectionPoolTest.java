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

import java.util.Iterator;
import java.util.List;

import com.exedio.cope.junit.CopeAssert;

public class ConnectionPoolTest extends CopeAssert
{

	public void testCp()
	{
		final Conn c1 = new Conn();
		final Factory f = new Factory(listg(c1));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 0);
		c1.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		f.assertV(1);
		
		// put into idle
		cp.put(c1);
		c1.assertV(1, 0);
		f.assertV(1);

		// get from idle
		assertSame(c1, cp.get());
		c1.assertV(1, 0);
		f.assertV(1);
		
		// put into idle
		cp.put(c1);
		c1.assertV(2, 0);
		f.assertV(1);

		// get from idle with other autoCommit
		assertSame(c1, cp.get());
		c1.assertV(2, 0);
		f.assertV(1);
	}
	
	public void testOverflow()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// get and create (2)
		assertSame(c2, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(2);
		
		// put into idle
		cp.put(c1);
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(2);
		
		// put and close
		cp.put(c2);
		c1.assertV(1, 0);
		c2.assertV(1, 1);
		f.assertV(2);
	}
	
	public void testPrecendence()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 2, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// get and create (2)
		assertSame(c2, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(2);
		
		// put into idle
		cp.put(c1);
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(2);
		
		// put into idle (2)
		cp.put(c2);
		c1.assertV(1, 0);
		c2.assertV(1, 0);
		f.assertV(2);

		// get from idle, fifo
		assertSame(c1, cp.get());
		c1.assertV(1, 0);
		c2.assertV(1, 0);
		f.assertV(2);

		// get from idle, fifo
		assertSame(c2, cp.get());
		c1.assertV(1, 0);
		c2.assertV(1, 0);
		f.assertV(2);

		// put into idle
		cp.put(c2);
		c1.assertV(1, 0);
		c2.assertV(2, 0);
		f.assertV(2);

		// get from idle
		assertSame(c2, cp.get());
		c1.assertV(1, 0);
		c2.assertV(2, 0);
		f.assertV(2);

		// put into idle
		cp.put(c1);
		c1.assertV(2, 0);
		c2.assertV(2, 0);
		f.assertV(2);

		// get from idle
		assertSame(c1, cp.get());
		c1.assertV(2, 0);
		c2.assertV(2, 0);
		f.assertV(2);

		// put into idle
		cp.put(c2);
		c1.assertV(2, 0);
		c2.assertV(3, 0);
		f.assertV(2);

		// get from idle
		assertSame(c2, cp.get());
		c1.assertV(2, 0);
		c2.assertV(3, 0);
		f.assertV(2);
	}
	
	public void testIdleInitial()
	{
		final Conn c1 = new Conn();
		final Factory f = new Factory(listg(c1));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 1);
		c1.assertV(0, 0);
		f.assertV(1); // already created
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		f.assertV(1);
	}
	
	public void testIsClosed()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// dont put into idle, because its closed
		c1.isClosed = true;
		try
		{
			cp.put(c1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("unexpected closed connection", e.getMessage());
		}
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(1);

		// create new because no idle available
		assertSame(c2, cp.get());
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(2);
	}
	
	public void testFlush()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// put into idle
		cp.put(c1);
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(1);

		// flush closes c1
		cp.flush();
		c1.assertV(1, 1);
		c2.assertV(0, 0);
		f.assertV(1);

		// create new because flushed
		assertSame(c2, cp.get());
		c1.assertV(1, 1);
		c2.assertV(0, 0);
		f.assertV(2);
	}
	
	public void testNoPool()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 0, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// put and close because no idle
		cp.put(c1);
		c1.assertV(1, 1);
		c2.assertV(0, 0);
		f.assertV(1);

		// create new because no idle
		assertSame(c2, cp.get());
		c1.assertV(1, 1);
		c2.assertV(0, 0);
		f.assertV(2);
	}
	
	public void testTimeout()
	{
		final Conn c1 = new Conn();
		final Conn c2 = new Conn();
		final Factory f = new Factory(listg(c1, c2));
		f.assertV(0);

		final Pool<Conn> cp = new Pool<Conn>(f, 1, 0);
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(0);
		
		// get and create
		assertSame(c1, cp.get());
		c1.assertV(0, 0);
		c2.assertV(0, 0);
		f.assertV(1);
		
		// put into idle
		cp.put(c1);
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(1);

		// create new because c1 timed out
		c1.timeout = true;
		assertSame(c2, cp.get());
		c1.assertV(1, 0);
		c2.assertV(0, 0);
		f.assertV(2);
	}
	
	static class Factory implements Pool.Factory<Conn>
	{
		final Iterator<Conn> connections;
		int createCount = 0;
		
		Factory(final List<Conn> connections)
		{
			this.connections = connections.iterator();
		}

		void assertV(final int createCount)
		{
			assertEquals(createCount, this.createCount);
		}
		
		public Conn create()
		{
			createCount++;
			return connections.next();
		}
		
		public boolean isValidOnGet(final Conn e)
		{
			return e.isValid();
		}
		
		public boolean isValidOnPut(final Conn e)
		{
			return e.isValidOnPut();
		}
		
		public void dispose(final Conn e)
		{
			e.dispose();
		}
	}
	
	static class Conn
	{
		boolean isClosed = false;
		int isClosedCount = 0;
		int closedCount = 0;
		boolean timeout = false;
		
		void assertV(final int isClosedCount, final int closedCount)
		{
			assertEquals(isClosedCount, this.isClosedCount);
			assertEquals(closedCount, this.closedCount);
		}
		
		boolean isValid()
		{
			return !timeout;
		}

		boolean isValidOnPut()
		{
			isClosedCount++;
			return !isClosed;
		}

		void dispose()
		{
			closedCount++;
		}
	}
}

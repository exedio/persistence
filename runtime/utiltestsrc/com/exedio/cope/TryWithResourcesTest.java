/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

/**
 * A characterization test for for try-with-resources.
 */
public class TryWithResourcesTest extends CopeAssert
{
	public void testSuccess()
	{
		System.out.println();
		Res res2;
		try(Res res = new Res())
		{
			res2 = res;
		}
		res2.assertClosed();
	}

	public void testSuccessFinally()
	{
		Res res2 = null;
		try(Res res = new Res())
		{
			res2 = res;
		}
		finally
		{
			res2.assertClosed();
		}
		res2.assertClosed();
	}

	public void testFail()
	{
		Res res2 = null;
		try
		{
			try(Res res = new Res())
			{
				res2 = res;
				if(returnTrue())
					throw new RuntimeException("expectionMessage");
			}
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("expectionMessage", e.getMessage());
			res2.assertClosed();
		}
	}

	public void testFailWithCatch()
	{
		Res res2 = null;
		try(Res res = new Res())
		{
			res2 = res;
			if(returnTrue())
				throw new RuntimeException("expectionMessage");
		}
		catch(final RuntimeException e)
		{
			assertEquals("expectionMessage", e.getMessage());
			res2.assertClosed();
		}
	}

	public void testFailWithFinally()
	{
		Res res2 = null;
		try
		{
			try(Res res = new Res())
			{
				res2 = res;
				if(returnTrue())
					throw new RuntimeException("expectionMessage");
			}
			finally
			{
				res2.assertClosed();
			}
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("expectionMessage", e.getMessage());
			res2.assertClosed();
		}
	}

	public void testFailWithCatchAndFinally()
	{
		Res res2 = null;
		try(Res res = new Res())
		{
			res2 = res;
			if(returnTrue())
				throw new RuntimeException("expectionMessage");
		}
		catch(final RuntimeException e)
		{
			assertEquals("expectionMessage", e.getMessage());
			res2.assertClosed();
		}
		finally
		{
			res2.assertClosed();
		}
	}

	class Res implements AutoCloseable
	{
		private boolean closed = false;

		public void assertClosed()
		{
			assertTrue(closed);
		}

		@Override
		public void close()
		{
			assertFalse(closed);
			closed = true;
		}
	}

	private static boolean returnTrue()
	{
		return true;
	}
}

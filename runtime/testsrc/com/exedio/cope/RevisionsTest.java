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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class RevisionsTest
{
	@Test void testNumberNegative()
	{
		try
		{
			new Revisions(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("number must not be negative, but was -1", e.getMessage());
		}
	}
	@Test void testRevisionsNull()
	{
		try
		{
			new Revisions((Revision[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("revisions", e.getMessage());
		}
	}
	@Test void testRevisionsEmpty()
	{
		try
		{
			new Revisions(new Revision[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("revisions must not be empty", e.getMessage());
		}
	}
	@Test void testRevisionsElementNull()
	{
		final Revision r = new Revision(1, "revision1", "nonsensesql1");
		try
		{
			new Revisions(r, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("revisions[1]", e.getMessage());
		}
	}
	@Test void testRevisionsInconsistent()
	{
		final Revision r8 = new Revision(8, "revision8", "nonsensesql8");
		final Revision r6 = new Revision(6, "revision6", "nonsensesql6");
		try
		{
			new Revisions(r8, r6);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent revision number at index 1, expected 7, but was 6", e.getMessage());
		}
	}

	@Test void testRevisionsCopy()
	{
		final Revision r2 = new Revision(2, "revision2", "nonsensesql2");
		final Revision r1 = new Revision(1, "revision1", "nonsensesql1");
		final Revision[] ra = {r2, r1};
		final Revisions rs = new Revisions(ra);
		assertEquals(list(r2, r1), rs.getList());

		ra[0] = r1;
		assertEquals(list(r2, r1), rs.getList());
	}

	@Test void testToRun()
	{
		final Revision r8 = new Revision(8, "revision8", "nonsensesql8");
		final Revision r7 = new Revision(7, "revision7", "nonsensesql7");
		final Revision r6 = new Revision(6, "revision6", "nonsensesql6");
		final Revisions rs = new Revisions(r8, r7, r6);
		assertEquals(8, rs.getNumber());
		assertEqualsUnmodifiable(list(r8, r7, r6), rs.getList());
		assertEquals("Revisions(8-6)", rs.toString());

		try
		{
			rs.getListToRun(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 0 to 8, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 1 to 8, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 3 to 8, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 4 to 8, but declared revisions allow from 5 only", e.getMessage());
		}
		assertEqualsUnmodifiable(list(r6, r7, r8), rs.getListToRun(5));
		assertEqualsUnmodifiable(list(    r7, r8), rs.getListToRun(6));
		assertEqualsUnmodifiable(list(        r8), rs.getListToRun(7));
		assertEqualsUnmodifiable(list(          ), rs.getListToRun(8));
		try
		{
			rs.getListToRun(9);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 8, but was 9", e.getMessage());
		}
		try
		{
			rs.getListToRun(10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 8, but was 10", e.getMessage());
		}
	}

	@Test void testToRunZero()
	{
		final Revision r2 = new Revision(2, "revision2", "nonsensesql2");
		final Revision r1 = new Revision(1, "revision1", "nonsensesql1");
		final Revisions rs = new Revisions(r2, r1);
		assertEquals(2, rs.getNumber());
		assertEqualsUnmodifiable(list(r2, r1), rs.getList());
		assertEquals("Revisions(2-1)", rs.toString());

		assertEqualsUnmodifiable(list(r1, r2), rs.getListToRun(0));
		assertEqualsUnmodifiable(list(    r2), rs.getListToRun(1));
		assertEqualsUnmodifiable(list(      ), rs.getListToRun(2));
		try
		{
			rs.getListToRun(3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 2, but was 3", e.getMessage());
		}
		try
		{
			rs.getListToRun(4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 2, but was 4", e.getMessage());
		}
	}

	@Test void testToRunSingle()
	{
		final Revision r3 = new Revision(3, "revision2", "nonsensesql2");
		final Revisions rs = new Revisions(r3);
		assertEquals(3, rs.getNumber());
		assertEqualsUnmodifiable(list(r3), rs.getList());
		assertEquals("Revisions(3-3)", rs.toString());

		try
		{
			rs.getListToRun(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 1 to 3, but declared revisions allow from 2 only", e.getMessage());
		}
		assertEqualsUnmodifiable(list(r3), rs.getListToRun(2));
		assertEqualsUnmodifiable(list(  ), rs.getListToRun(3));
		try
		{
			rs.getListToRun(4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 3, but was 4", e.getMessage());
		}
	}

	@Test void testToRunNumber()
	{
		final Revisions rs = new Revisions(5);
		assertEquals(5, rs.getNumber());
		assertEqualsUnmodifiable(list(), rs.getList());
		assertEquals("Revisions(5)", rs.toString());

		try
		{
			rs.getListToRun(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 0 to 5, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 1 to 5, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(3);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 3 to 5, but declared revisions allow from 5 only", e.getMessage());
		}
		try
		{
			rs.getListToRun(4);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("attempt to revise from 4 to 5, but declared revisions allow from 5 only", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), rs.getListToRun(5));
		try
		{
			rs.getListToRun(6);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 5, but was 6", e.getMessage());
		}
		try
		{
			rs.getListToRun(7);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 5, but was 7", e.getMessage());
		}
	}

	@Test void testToRunNumberZero()
	{
		final Revisions rs = new Revisions(0);
		assertEquals(0, rs.getNumber());
		assertEqualsUnmodifiable(list(), rs.getList());
		assertEquals("Revisions(0)", rs.toString());

		assertEqualsUnmodifiable(list(), rs.getListToRun(0));
		try
		{
			rs.getListToRun(1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 0, but was 1", e.getMessage());
		}
		try
		{
			rs.getListToRun(2);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("cannot revise backwards, expected 0, but was 2", e.getMessage());
		}
	}
}

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

import static com.exedio.cope.QueryRangeTest.AnItem.TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class QueryRangeTest
{
	static final Model MODEL = new Model(TYPE);

	@Test public void testLimitSimple()
	{
		final Query<?> q = q5533();
		q.setLimit(5);
		assertEquals(5, q.getOffset());
		assertEquals(-1, q.getLimit());
		assertEquals("select this from AnItem offset '5'", q.toString());
	}
	@Test public void testLimitSimpleZero()
	{
		final Query<?> q = q5533();
		q.setLimit(0);
		assertEquals(0, q.getOffset());
		assertEquals(-1, q.getLimit());
		assertEquals("select this from AnItem", q.toString());
	}
	@Test public void testLimitSimpleOffsetNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setLimit(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getOffset());
			assertEquals(33, q.getLimit());
		}
	}

	@Test public void testLimitFull()
	{
		final Query<?> q = q5533();
		q.setLimit(5, 3);
		assertEquals(5, q.getOffset());
		assertEquals(3, q.getLimit());
		assertEquals("select this from AnItem offset '5' limit '3'", q.toString());
	}
	@Test public void testLimitFullZero()
	{
		final Query<?> q = q5533();
		q.setLimit(0, 0);
		assertEquals(0, q.getOffset());
		assertEquals(0, q.getLimit());
		assertEquals("select this from AnItem limit '0'", q.toString());
	}
	@Test public void testLimitFullOffsetNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setLimit(-1, 10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getOffset());
			assertEquals(33, q.getLimit());
		}
	}
	@Test public void testLimitFullLimitNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setLimit(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getOffset());
			assertEquals(33, q.getLimit());
		}
	}

	private static Query<AnItem> q5533()
	{
		final Query<AnItem> result = TYPE.newQuery(null);
		result.setLimit(55, 33);
		assertEquals(55, result.getOffset());
		assertEquals(33, result.getLimit());
		return result;
	}

	@com.exedio.cope.instrument.WrapperIgnore // TODO use import, but this is not accepted by javac
	static final class AnItem extends Item
	{
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final com.exedio.cope.ActivationParameters ap){ super(ap); }
	}
}

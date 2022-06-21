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

import static com.exedio.cope.QueryPageTest.AnItem.TYPE;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class QueryPageTest
{
	@SuppressWarnings("unused") // OK: Model that is never connected
	static final Model MODEL = new Model(TYPE);

	@Test void testLimitSimple()
	{
		final Query<?> q = q5533();
		q.setPageUnlimited(5);
		assertEquals(5, q.getPageOffset());
		assertEquals(-1, q.getPageLimitOrMinusOne());
		assertEquals("select this from AnItem offset '5'", q.toString());
	}
	@Test void testLimitSimpleZero()
	{
		final Query<?> q = q5533();
		q.setPageUnlimited(0);
		assertEquals(0, q.getPageOffset());
		assertEquals(-1, q.getPageLimitOrMinusOne());
		assertEquals("select this from AnItem", q.toString());
	}
	@Test void testLimitSimpleOffsetNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setPageUnlimited(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getPageOffset());
			assertEquals(33, q.getPageLimitOrMinusOne());
		}
	}

	@Test void testLimitFull()
	{
		final Query<?> q = q5533();
		q.setPage(5, 3);
		assertEquals(5, q.getPageOffset());
		assertEquals(3, q.getPageLimitOrMinusOne());
		assertEquals("select this from AnItem offset '5' limit '3'", q.toString());
	}
	@Test void testLimitFullZero()
	{
		final Query<?> q = q5533();
		q.setPage(0, 0);
		assertEquals(0, q.getPageOffset());
		assertEquals(0, q.getPageLimitOrMinusOne());
		assertEquals("select this from AnItem limit '0'", q.toString());
	}
	@Test void testLimitFullOffsetNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setPage(-1, 10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getPageOffset());
			assertEquals(33, q.getPageLimitOrMinusOne());
		}
	}
	@Test void testLimitFullLimitNegative()
	{
		final Query<?> q = q5533();
		try
		{
			q.setPage(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
			assertEquals(55, q.getPageOffset());
			assertEquals(33, q.getPageLimitOrMinusOne());
		}
	}

	private static Query<AnItem> q5533()
	{
		final Query<AnItem> result = TYPE.newQuery(null);
		result.setPage(55, 33);
		assertEquals(55, result.getPageOffset());
		assertEquals(33, result.getPageLimitOrMinusOne());
		return result;
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.QueryRangeTest.AttributeItem.TYPE;

import com.exedio.cope.junit.CopeAssert;

public class QueryRangeTest extends CopeAssert
{
	static final Model MODEL = new Model(TYPE);

	public void testIt()
	{
		final Query<AttributeItem> q = TYPE.newQuery(null);
		try
		{
			q.setLimit(-1, 10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(0, q.getOffset());
			assertEquals(-1, q.getLimit());
		}
		try
		{
			q.setLimit(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("offset must not be negative, but was -1", e.getMessage());
			assertEquals(0, q.getOffset());
			assertEquals(-1, q.getLimit());
		}
		try
		{
			q.setLimit(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("limit must not be negative, but was -1", e.getMessage());
			assertEquals(0, q.getOffset());
			assertEquals(-1, q.getLimit());
		}
	}

	static final class AttributeItem extends Item
	{
		static final Type<AttributeItem> TYPE = TypesBound.newType(AttributeItem.class);
		private static final long serialVersionUID = 1l;
		private AttributeItem(final com.exedio.cope.ActivationParameters ap){ super(ap); }
	}
}

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

import static com.exedio.cope.Query.newQuery;

import com.exedio.cope.junit.CopeAssert;
import java.util.List;

public class QuerySelectTest extends CopeAssert
{
	public void testSetSelect()
	{
		final Query<AnItem> q = AnItem.TYPE.newQuery(null);
		assertEquals(AnItem.TYPE.getThis(), q.getSelectSingle());
		assertEquals("select this from AnItem", q.toString());

		try
		{
			q.setSelects(new Selectable<?>[]{AnItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + AnItem.day + "]", e.getMessage());
		}
		assertEquals(AnItem.TYPE.getThis(), q.getSelectSingle());
		assertEquals("select this from AnItem", q.toString());

		try
		{
			q.setSelects(new Selectable<?>[]{AnItem.TYPE.getThis(), AnItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("use setSelect instead", e.getMessage());
		}
		assertEquals(AnItem.TYPE.getThis(), q.getSelectSingle());
		assertEquals("select this from AnItem", q.toString());
	}

	public void testSetSelects()
	{
		try
		{
			newQuery(new Selectable<?>[]{AnItem.day}, AnItem.TYPE, null);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + AnItem.day + "]", e.getMessage());
		}

		final Query<List<Object>> q = newQuery(new Selectable<?>[]{AnItem.day, AnItem.optionalDay}, AnItem.TYPE, null);
		assertEquals("select day,optionalDay from AnItem", q.toString());

		q.setSelects(new Selectable<?>[]{AnItem.TYPE.getThis(), AnItem.day});
		assertEquals("select this,day from AnItem", q.toString());

		try
		{
			q.setSelects(new Selectable<?>[]{AnItem.day});
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("must have at least 2 selects, but was [" + AnItem.day + "]", e.getMessage());
		}
		assertEquals("select this,day from AnItem", q.toString());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad api usage
	public void testSetSelectsUnchecked()
	{
		final Query q = newQuery(new Selectable[]{AnItem.day, AnItem.optionalDay}, AnItem.TYPE, null);
		try
		{
			q.getSelectSingle();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("use getSelectMulti instead", e.getMessage());
		}
		try
		{
			q.setSelect(AnItem.TYPE.getThis());
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("use setSelects instead", e.getMessage());
		}
	}

	static class AnItem extends Item
	{
		static final DayField day = new DayField();
		static final DayField optionalDay = new DayField();
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	static
	{
		new Model(AnItem.TYPE);
	}
}

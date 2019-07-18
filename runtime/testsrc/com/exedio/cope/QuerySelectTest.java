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
import static com.exedio.cope.QuerySelectTest.AnItem.TYPE;
import static com.exedio.cope.QuerySelectTest.AnItem.field1;
import static com.exedio.cope.QuerySelectTest.AnItem.field2;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.util.Day;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QuerySelectTest
{
	@Test void testSetSelectsCheck()
	{
		final Query<List<Object>> q = newQuery(new Function<?>[]{field1, field2}, TYPE, null);
		try
		{
			q.setSelects((Selectable[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			q.setSelects();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("must have at least 2 selects, but was []", e.getMessage());
		}
		try
		{
			q.setSelects(field1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("must have at least 2 selects, but was [" + field1 + "]", e.getMessage());
		}
		try
		{
			q.setSelects(field1, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("selects[1]", e.getMessage());
		}
	}

	@Test void testSetSelect()
	{
		final Query<AnItem> q = TYPE.newQuery(null);
		assertEquals(TYPE.getThis(), q.getSelectSingle());
		assertEqualsUnmodifiable(asList(TYPE.getThis()), q.getSelects());
		assertEquals("select this from AnItem", q.toString());

		try
		{
			q.setSelects(field1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("must have at least 2 selects, but was [" + field1 + "]", e.getMessage());
		}
		assertEquals(TYPE.getThis(), q.getSelectSingle());
		assertEqualsUnmodifiable(asList(TYPE.getThis()), q.getSelects());
		assertEquals("select this from AnItem", q.toString());

		try
		{
			q.setSelects(TYPE.getThis(), field1);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("use setSelect instead", e.getMessage());
		}
		assertEquals(TYPE.getThis(), q.getSelectSingle());
		assertEqualsUnmodifiable(asList(TYPE.getThis()), q.getSelects());
		assertEquals("select this from AnItem", q.toString());
	}

	@Test void testSetSelects()
	{
		try
		{
			newQuery(new Selectable<?>[]{field1}, TYPE, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("must have at least 2 selects, but was [" + field1 + "]", e.getMessage());
		}

		final Query<List<Object>> q = newQuery(new Selectable<?>[]{field1, field2}, TYPE, null);
		assertEquals("select field1,field2 from AnItem", q.toString());

		q.setSelects(TYPE.getThis(), field1);
		assertEquals("select this,field1 from AnItem", q.toString());

		try
		{
			q.setSelects(field1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("must have at least 2 selects, but was [" + field1 + "]", e.getMessage());
		}
		assertEquals("select this,field1 from AnItem", q.toString());
	}

	@SuppressWarnings({"unchecked", "rawtypes", "RawTypeCanBeGeneric"}) // OK: test bad api usage
	@Test void testSetSelectsUnchecked()
	{
		final Query q = newQuery(new Selectable[]{field1, field2}, TYPE, null);
		try
		{
			q.getSelectSingle();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("use getSelects instead", e.getMessage());
		}
		assertEqualsUnmodifiable(asList(field1, field2), q.getSelects());
		try
		{
			q.setSelect(TYPE.getThis());
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("use setSelects instead", e.getMessage());
		}
		assertEqualsUnmodifiable(asList(field1, field2), q.getSelects());
	}

	@Test void testSetHaving()
	{
		final Query<AnItem> q = TYPE.newQuery(null);
		assertSame(null, q.getHaving());
		assertEquals("select this from AnItem", q.toString());

		final Condition having1 = field1.equal(new Day(2008,3,14));
		q.setHaving(having1);
		assertSame(having1, q.getHaving());
		assertEquals("select this from AnItem having field1='2008/3/14'", q.toString());

		final Condition having2 = field1.equal(new Day(2010,12,5));
		q.setHaving(having2);
		assertSame(having2, q.getHaving());
		assertEquals("select this from AnItem having field1='2010/12/5'", q.toString());

		q.setHaving(Condition.TRUE);
		assertSame(null, q.getHaving());
		assertEquals("select this from AnItem", q.toString());

		q.setHaving(having2);
		assertSame(having2, q.getHaving());
		assertEquals("select this from AnItem having field1='2010/12/5'", q.toString());

		q.setHaving(null);
		assertSame(null, q.getHaving());
		assertEquals("select this from AnItem", q.toString());
	}

	@com.exedio.cope.instrument.WrapperIgnore // TODO use import, but this is not accepted by javac
	static final class AnItem extends Item
	{
		static final DayField field1 = new DayField();
		static final DayField field2 = new DayField();
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	static
	{
		new Model(TYPE);
	}
}

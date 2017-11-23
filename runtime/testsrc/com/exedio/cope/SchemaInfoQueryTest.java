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

import static com.exedio.cope.SchemaInfo.search;
import static com.exedio.cope.SchemaInfo.total;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaInfoQueryTest
{
	private static final Type<?> TYPE = MyItem.TYPE;

	@Test void testConditionNone()
	{
		final Query<?> q = TYPE.newQuery(null);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE), search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testConditionTrue()
	{
		final Query<?> q = TYPE.newQuery(Condition.TRUE);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE), search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testConditionFalse()
	{
		final Query<?> q = TYPE.newQuery(Condition.FALSE);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem where FALSE", q.toString());
		assertEquals("skipped because condition==false: select this from " + TYPE + " where FALSE", search(q));
		assertEquals("skipped because condition==false: select this from " + TYPE + " where FALSE", total(q));
	}

	@Test void testLimitZero()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(0, 0);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem limit '0'", q.toString());
		assertEquals("skipped because limit==0: select this from " + TYPE + " limit '0'", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testOffsetSetLimitZero()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55, 0);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55' limit '0'", q.toString());
		assertEquals("skipped because limit==0: select this from " + TYPE + " offset '55' limit '0'", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testLimitSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(0, 66);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem limit '66'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 0 LIMIT 66", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testOffsetSetLimitSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55, 66);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55' limit '66'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 55 LIMIT 66", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test void testOffsetSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 55", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}



	@BeforeEach void setUp()
	{
		model.startTransaction(SchemaInfoQueryTest.class.getName());
	}

	@AfterEach void tearDown()
	{
		model.rollbackIfNotCommitted();
	}

	@BeforeAll static void setUpClass()
	{
		model.connect(ConnectProperties.create(TestSources.minimal()));
		model.tearDownSchema();
		model.createSchema();
	}

	@AfterAll static void tearDownClass()
	{
		model.tearDownSchema();
		model.disconnect();
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(MyItem.TYPE);
}

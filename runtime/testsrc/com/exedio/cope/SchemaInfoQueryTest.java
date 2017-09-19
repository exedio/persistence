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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.tojunit.TestSources;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchemaInfoQueryTest
{
	private static final Type<?> TYPE = MyItem.TYPE;

	@Test public void testConditionNone()
	{
		final Query<?> q = TYPE.newQuery(null);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE), search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testConditionTrue()
	{
		final Query<?> q = TYPE.newQuery(Condition.TRUE);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE), search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testConditionFalse()
	{
		final Query<?> q = TYPE.newQuery(Condition.FALSE);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem where FALSE", q.toString());
		// TODO
		try
		{
			search(q);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("FALSE", e.getMessage());
		}
		try
		{
			total(q);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("FALSE", e.getMessage());
		}
	}

	@Test public void testLimitZero()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(0, 0);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem limit '0'", q.toString());
		assertEquals("skipped because limit==0: select this from " + TYPE + " limit '0'", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testOffsetSetLimitZero()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55, 0);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55' limit '0'", q.toString());
		assertEquals("skipped because limit==0: select this from " + TYPE + " offset '55' limit '0'", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testLimitSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(0, 66);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem limit '66'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 0 LIMIT 66", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testOffsetSetLimitSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55, 66);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55' limit '66'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 55 LIMIT 66", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}

	@Test public void testOffsetSet()
	{
		final Query<?> q = TYPE.newQuery();
		q.setLimit(55);

		assertEquals(asList(), q.search());
		assertEquals(0, q.total());
		assertEquals("select this from MyItem offset '55'", q.toString());
		assertEquals("SELECT " + SI.pk(TYPE) + " FROM " + SI.tab(TYPE) + " OFFSET 55", search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), total(q));
	}



	@Before public void setUp()
	{
		model.startTransaction(SchemaInfoQueryTest.class.getName());
	}

	@After public void tearDown()
	{
		model.rollbackIfNotCommitted();
	}

	@BeforeClass public static void setUpClass()
	{
		model.connect(ConnectProperties.create(TestSources.minimal()));
		model.tearDownSchema();
		model.createSchema();
	}

	@AfterClass public static void tearDownClass()
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

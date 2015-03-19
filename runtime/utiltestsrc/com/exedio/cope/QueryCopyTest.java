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

import static com.exedio.cope.QueryCopyTest.AnItem.TYPE;
import static com.exedio.cope.QueryCopyTest.AnItem.date;
import static com.exedio.cope.QueryCopyTest.AnItem.intx;
import static com.exedio.cope.QueryCopyTest.AnItem.string;
import static java.util.Arrays.asList;

import com.exedio.cope.junit.CopeAssert;
import java.util.List;

public class QueryCopyTest extends CopeAssert
{
	public void testSimple()
	{
		final Query<?> query = TYPE.newQuery();
		query.setSearchSizeLimit(77);
		query.setSearchSizeCacheLimit(66);

		assertEquals(false, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(), query.getJoins());
		assertSame(null, query.getCondition());
		assertEquals(list(), query.getOrderByFunctions());
		assertEquals(list(), query.getOrderByAscending());
		assertEquals(0, query.getOffset());
		assertEquals(-1, query.getLimit());
		assertEquals("select this from AnItem", query.toString());
		assertEquals(77, query.getSearchSizeLimit());
		assertEquals(66, query.getSearchSizeCacheLimit());

		final Query<?> copy = new Query<>(string, query);
		assertEquals(false, copy.isDistinct());
		assertSame(TYPE, copy.getType());
		assertEquals(list(), copy.getJoins());
		assertSame(null, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());
		assertEquals("select string from AnItem", copy.toString());
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(date, false);
		query.setLimit(33, 44);
		query.setSearchSizeLimit(177);
		query.setSearchSizeCacheLimit(166);

		assertEquals(true, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
		assertEquals(
				"select distinct this from AnItem " +
				"join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				query.toString());
		assertEquals(177, query.getSearchSizeLimit());
		assertEquals(166, query.getSearchSizeCacheLimit());

		assertIt(
				false, TYPE, null, null, null, null, 0, -1,
				"select string from AnItem",
				copy);
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());
	}

	public void testAdvanced()
	{
		final Query<?> query = TYPE.newQuery();
		assertIt(
				false, TYPE, null, null, null, null, 0, -1,
				"select this from AnItem",
				query);

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(date, false);
		query.setLimit(33, 44);

		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				asList(date), asList(false),
				33, 44,
				"select distinct this " +
				"from AnItem join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				query);

		final Query<?> copy = new Query<>(string, query);
		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				asList(date), asList(false),
				33, 44,
				"select distinct string " +
				"from AnItem join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				copy);

		copy.setDistinct(false);
		final Join joinCopy = copy.join(TYPE);
		final Condition conditionCopy = intx.equal(1);
		copy.setCondition(conditionCopy);
		copy.resetOrderBy();
		copy.setLimit(0);

		assertIt(
				false, TYPE,
				asList(joinQuery, joinCopy), conditionCopy,
				null, null,
				0, -1,
				"select string from AnItem " +
				"join AnItem a1 " +
				"join AnItem a2 " +
				"where intx='1'",
				copy);

		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				asList(date), asList(false),
				33, 44,
				"select distinct this " +
				"from AnItem join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				query);
	}

	static class AnItem extends Item
	{
		static final DayField date = new DayField();
		static final IntegerField intx = new IntegerField();
		static final StringField string = new StringField();
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	static
	{
		new Model(TYPE);
	}

	void assertIt(
			final boolean distinct, final Type<?> type, final List<Join> joins,
			final Condition condition,
			final List<? extends Function<?>> orderBy, final List<Boolean> orderByAscending,
			final int offset, final int limit,
			final String toString,
			final Query<?> query)
	{
		assertEquals(distinct, query.isDistinct());
		assertSame(type, query.getType());
		assertEquals(joins           !=null ? joins            : asList(), query.getJoins());
		assertSame(condition, query.getCondition());
		assertEquals(orderBy         !=null ? orderBy          : asList(), query.getOrderByFunctions());
		assertEquals(orderByAscending!=null ? orderByAscending : asList(), query.getOrderByAscending());
		assertEquals(offset, query.getOffset());
		assertEquals(limit, query.getLimit());
		assertEquals(toString, query.toString());
	}
}

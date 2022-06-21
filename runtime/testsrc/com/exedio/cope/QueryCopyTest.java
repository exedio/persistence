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
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class QueryCopyTest
{
	@Test void testSimple()
	{
		final Query<?> query = TYPE.newQuery();
		query.setSearchSizeLimit(77);
		query.setSearchSizeCacheLimit(66);

		assertIt(
				false, TYPE, null, null, null, null, null, 0, -1,
				"select this from AnItem",
				query);
		assertEquals(77, query.getSearchSizeLimit());
		assertEquals(66, query.getSearchSizeCacheLimit());

		final Query<?> copy = new Query<>(string, query);
		assertIt(
				false, TYPE, null, null, null, null, null, 0, -1,
				"select string from AnItem",
				copy);
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(date, false);
		query.setPage(33, 44);
		query.setSearchSizeLimit(177);
		query.setSearchSizeCacheLimit(166);

		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				null, asList(date), asList(false),
				33, 44,
				"select distinct this from AnItem " +
				"join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				query);
		assertEquals(177, query.getSearchSizeLimit());
		assertEquals(166, query.getSearchSizeCacheLimit());

		assertIt(
				false, TYPE, null, null, null, null, null, 0, -1,
				"select string from AnItem",
				copy);
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());
	}

	@Test void testAdvanced()
	{
		final Query<?> query = TYPE.newQuery();
		assertIt(
				false, TYPE, null, null, null, null, null, 0, -1,
				"select this from AnItem",
				query);

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(date, false);
		query.setPage(33, 44);

		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				null, asList(date), asList(false),
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
				null, asList(date), asList(false),
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
		copy.setPageUnlimited(0);

		assertIt(
				false, TYPE,
				asList(joinQuery, joinCopy), conditionCopy,
				null, null, null,
				0, -1,
				"select string from AnItem " +
				"join AnItem a1 " +
				"join AnItem a2 " +
				"where intx='1'",
				copy);

		assertIt(
				true, TYPE,
				asList(joinQuery), conditionQuery,
				null, asList(date), asList(false),
				33, 44,
				"select distinct this " +
				"from AnItem join AnItem a1 where string='zack' " +
				"order by date desc " +
				"offset '33' limit '44'",
				query);
	}

	@Test void testMulti()
	{
		final Query<?> query = Query.newQuery(new Selectable<?>[]{string, date}, TYPE, null);
		query.setGroupBy(date, intx);
		query.setHaving(string.equal("haveItOrNot"));

		assertIt(
				false, TYPE, null, null, asList(date, intx), null, null, 0, -1,
				"select string,date from AnItem " +
				"group by date,intx " +
				"having string='haveItOrNot'",
				query);

		{
			final Query<?> copy = new Query<>(query);
			assertIt(
					false, TYPE, null, null, asList(date, intx), null, null, 0, -1,
					"select string,date from AnItem " +
					"group by date,intx " +
					"having string='haveItOrNot'",
					copy);
		}
		{
			final Query<?> copy = new Query<>(intx, query);
			assertIt(
					false, TYPE, null, null, asList(date, intx), null, null, 0, -1,
					"select intx from AnItem " +
					"group by date,intx " +
					"having string='haveItOrNot'",
					copy);
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItem extends Item
	{
		@WrapperIgnore
		static final DayField date = new DayField();
		@WrapperIgnore
		static final IntegerField intx = new IntegerField();
		@WrapperIgnore
		static final StringField string = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static
	{
		new Model(TYPE);
	}

	void assertIt(
			final boolean distinct, final Type<?> type, final List<Join> joins,
			final Condition condition,
			final List<? extends Function<?>> groupBy,
			final List<? extends Function<?>> orderBy, final List<Boolean> orderByAscending,
			final int offset, final int limit,
			final String toString,
			final Query<?> query)
	{
		assertEquals(distinct, query.isDistinct());
		assertSame(type, query.getType());
		assertEquals(nullToEmpty(joins), query.getJoins());
		assertSame(condition, query.getCondition());
		assertEquals(nullToEmpty(orderBy), query.getOrderBys());
		assertEquals(nullToEmpty(orderByAscending), query.getOrderByAscending());
		assertEquals(nullToEmpty(groupBy), query.getGroupBys());
		assertEquals(offset, query.getPageOffset());
		assertEquals(limit, query.getPageLimitOrMinusOne());
		assertEquals(toString, query.toString());
	}

	private static <E> List<E> nullToEmpty(final List<E> l)
	{
		return l!=null ? l : Collections.emptyList();
	}
}

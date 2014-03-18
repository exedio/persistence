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

import static com.exedio.cope.CompareConditionItem.TYPE;

import com.exedio.cope.junit.CopeAssert;

public class QueryCopyTest extends CopeAssert
{
	@SuppressWarnings("unused")
	private static final Model MODEL = CompareConditionTest.MODEL; // mount types

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
		assertEquals(77, query.getSearchSizeLimit());
		assertEquals(66, query.getSearchSizeCacheLimit());

		final Query<?> copy = new Query<>(CompareConditionItem.string, query);
		assertEquals(false, copy.isDistinct());
		assertSame(TYPE, copy.getType());
		assertEquals(list(), copy.getJoins());
		assertSame(null, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = CompareConditionItem.string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(CompareConditionItem.date, false);
		query.setLimit(33, 44);
		query.setSearchSizeLimit(177);
		query.setSearchSizeCacheLimit(166);

		assertEquals(true, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
		assertEquals(177, query.getSearchSizeLimit());
		assertEquals(166, query.getSearchSizeCacheLimit());

		assertEquals(false, copy.isDistinct());
		assertSame(TYPE, copy.getType());
		assertEquals(list(), copy.getJoins());
		assertSame(null, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());
		assertEquals(77, copy.getSearchSizeLimit());
		assertEquals(66, copy.getSearchSizeCacheLimit());
	}

	public void testAdvanced()
	{
		final Query<?> query = TYPE.newQuery();
		assertEquals(false, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(), query.getJoins());
		assertSame(null, query.getCondition());
		assertEquals(list(), query.getOrderByFunctions());
		assertEquals(list(), query.getOrderByAscending());
		assertEquals(0, query.getOffset());
		assertEquals(-1, query.getLimit());

		query.setDistinct(true);
		final Join joinQuery = query.join(TYPE);
		final Condition conditionQuery = CompareConditionItem.string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(CompareConditionItem.date, false);
		query.setLimit(33, 44);

		assertEquals(true, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());

		final Query<?> copy = new Query<>(CompareConditionItem.string, query);
		assertEquals(true, copy.isDistinct());
		assertSame(TYPE, copy.getType());
		assertEquals(list(joinQuery), copy.getJoins());
		assertSame(conditionQuery, copy.getCondition());
		assertEquals(list(CompareConditionItem.date), copy.getOrderByFunctions());
		assertEquals(list(false), copy.getOrderByAscending());
		assertEquals(33, copy.getOffset());
		assertEquals(44, copy.getLimit());

		copy.setDistinct(false);
		final Join joinCopy = copy.join(TYPE);
		final Condition conditionCopy = CompareConditionItem.intx.equal(1);
		copy.setCondition(conditionCopy);
		copy.resetOrderBy();
		copy.setLimit(0);

		assertEquals(false, copy.isDistinct());
		assertSame(TYPE, copy.getType());
		assertEquals(list(joinQuery, joinCopy), copy.getJoins());
		assertSame(conditionCopy, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());

		assertEquals(true, query.isDistinct());
		assertSame(TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
	}
}

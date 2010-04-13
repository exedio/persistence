/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

public class QueryCopyTest extends CopeAssert
{
	@SuppressWarnings("unused")
	private static final Model MODEL = CompareConditionTest.MODEL; // mount types
	
	public void testSimple()
	{
		final Query query = CompareConditionItem.TYPE.newQuery();
		assertEquals(false, query.isDistinct());
		assertSame(CompareConditionItem.TYPE, query.getType());
		assertEquals(list(), query.getJoins());
		assertSame(null, query.getCondition());
		assertEquals(list(), query.getOrderByFunctions());
		assertEquals(list(), query.getOrderByAscending());
		assertEquals(0, query.getOffset());
		assertEquals(-1, query.getLimit());
		
		final Query copy = new Query<String>(CompareConditionItem.string, query);
		assertEquals(false, copy.isDistinct());
		assertSame(CompareConditionItem.TYPE, copy.getType());
		assertEquals(list(), copy.getJoins());
		assertSame(null, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());
		
		query.setDistinct(true);
		final Join joinQuery = query.join(CompareConditionItem.TYPE);
		final Condition conditionQuery = CompareConditionItem.string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(CompareConditionItem.date, false);
		query.setLimit(33, 44);
		
		assertEquals(true, query.isDistinct());
		assertSame(CompareConditionItem.TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
		
		assertEquals(false, copy.isDistinct());
		assertSame(CompareConditionItem.TYPE, copy.getType());
		assertEquals(list(), copy.getJoins());
		assertSame(null, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());
	}
	
	public void testAdvanced()
	{
		final Query query = CompareConditionItem.TYPE.newQuery();
		assertEquals(false, query.isDistinct());
		assertSame(CompareConditionItem.TYPE, query.getType());
		assertEquals(list(), query.getJoins());
		assertSame(null, query.getCondition());
		assertEquals(list(), query.getOrderByFunctions());
		assertEquals(list(), query.getOrderByAscending());
		assertEquals(0, query.getOffset());
		assertEquals(-1, query.getLimit());
		
		query.setDistinct(true);
		final Join joinQuery = query.join(CompareConditionItem.TYPE);
		final Condition conditionQuery = CompareConditionItem.string.equal("zack");
		query.setCondition(conditionQuery);
		query.addOrderBy(CompareConditionItem.date, false);
		query.setLimit(33, 44);
		
		assertEquals(true, query.isDistinct());
		assertSame(CompareConditionItem.TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
		
		final Query copy = new Query<String>(CompareConditionItem.string, query);
		assertEquals(true, copy.isDistinct());
		assertSame(CompareConditionItem.TYPE, copy.getType());
		assertEquals(list(joinQuery), copy.getJoins());
		assertSame(conditionQuery, copy.getCondition());
		assertEquals(list(CompareConditionItem.date), copy.getOrderByFunctions());
		assertEquals(list(false), copy.getOrderByAscending());
		assertEquals(33, copy.getOffset());
		assertEquals(44, copy.getLimit());
		
		copy.setDistinct(false);
		final Join joinCopy = copy.join(CompareConditionItem.TYPE);
		final Condition conditionCopy = CompareConditionItem.intx.equal(1);
		copy.setCondition(conditionCopy);
		copy.resetOrderBy();
		copy.setLimit(0);

		assertEquals(false, copy.isDistinct());
		assertSame(CompareConditionItem.TYPE, copy.getType());
		assertEquals(list(joinQuery, joinCopy), copy.getJoins());
		assertSame(conditionCopy, copy.getCondition());
		assertEquals(list(), copy.getOrderByFunctions());
		assertEquals(list(), copy.getOrderByAscending());
		assertEquals(0, copy.getOffset());
		assertEquals(-1, copy.getLimit());

		assertEquals(true, query.isDistinct());
		assertSame(CompareConditionItem.TYPE, query.getType());
		assertEquals(list(joinQuery), query.getJoins());
		assertSame(conditionQuery, query.getCondition());
		assertEquals(list(CompareConditionItem.date), query.getOrderByFunctions());
		assertEquals(list(false), query.getOrderByAscending());
		assertEquals(33, query.getOffset());
		assertEquals(44, query.getLimit());
	}
}

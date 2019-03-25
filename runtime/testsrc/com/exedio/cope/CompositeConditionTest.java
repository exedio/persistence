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

import static com.exedio.cope.CompareConditionItem.TYPE;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.RuntimeAssert.assertCondition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompositeConditionTest extends TestWithEnvironment
{
	public CompositeConditionTest()
	{
		super(CompareConditionTest.MODEL);
	}

	CompareConditionItem item, itemA, itemB, itemAB, itemAX, itemXB;

	@BeforeEach final void setUp()
	{
		item   = new CompareConditionItem(null,    1,   1l, null, null, null, null);
		itemA  = new CompareConditionItem(null,    2,   1l, null, null, null, null);
		itemB  = new CompareConditionItem(null,    1,   2l, null, null, null, null);
		itemAB = new CompareConditionItem(null,    2,   2l, null, null, null, null);
		itemAX = new CompareConditionItem(null,    3, null, null, null, null, null);
		itemXB = new CompareConditionItem(null, null,   3l, null, null, null, null);
		         new CompareConditionItem(null, null, null, null, null, null, null);
	}

	@Test void testNot()
	{
		final Condition conditionA = intx .greater(1);
		final Condition conditionB = longx.greater(1l);

		assertCondition(itemA, itemAB, itemAX, TYPE, conditionA);
		assertCondition(itemB, itemAB, itemXB, TYPE, conditionB);

		final Condition conditionAnd = conditionA.and(conditionB);
		final Condition conditionOr  = conditionA.or (conditionB);

		assertCondition(              itemAB                , TYPE, conditionAnd);
		assertCondition(itemA, itemB, itemAB, itemAX, itemXB, TYPE, conditionOr );

		assertCondition(item, itemA, itemB, TYPE, conditionAnd.not());
		assertCondition(item,               TYPE, conditionOr .not());
	}
}

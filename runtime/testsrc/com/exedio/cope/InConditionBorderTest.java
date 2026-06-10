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
import static com.exedio.cope.CompareConditionItem.YEnum.V1;
import static com.exedio.cope.CompareConditionItem.YEnum.V2;
import static com.exedio.cope.CompareConditionItem.YEnum.V3;
import static com.exedio.cope.CompareConditionItem.enumx;
import static com.exedio.cope.CompareConditionTest.MODEL;
import static com.exedio.cope.RuntimeAssert.assertCondition;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CompareConditionItem.YEnum;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InConditionBorderTest extends TestWithEnvironment
{
	public InConditionBorderTest()
	{
		super(MODEL);
	}

	private CompareConditionItem i1, i2, i3, iN;

	@BeforeEach final void setUp()
	{
		i1 = new CompareConditionItem(V1);
		i2 = new CompareConditionItem(V2);
		i3 = new CompareConditionItem(V3);
		iN = new CompareConditionItem(null);
	}

	@Test void test()
	{
		assertIn(asList(i1, i2    ), asList(        i3    ), asList(V1, V2      ));
		assertIn(asList(i1, i2, iN), asList(        i3    ), asList(V1, V2, null));
		assertIn(asList(i1        ), asList(    i2, i3    ), asList(V1          ));
		assertIn(asList(i1,     iN), asList(    i2, i3    ), asList(V1,     null));
		assertIn(asList(          ), asList(i1, i2, i3, iN), asList(            )); // TODO COND iN is wrong because of bug: ofFalse.not() with null
		assertIn(asList(        iN), asList(i1, i2, i3    ), asList( (YEnum)null));
	}

	private static void assertIn(
			final List<CompareConditionItem> expected,
			final List<CompareConditionItem> expectedNot,
			final List<YEnum> actualL)
	{
		final YEnum[] actualA = actualL.toArray(YEnum[]::new);

		final Condition conditionL = enumx.in(actualL);
		final Condition conditionA = enumx.in(actualA);
		final Condition conditionNotL = conditionL.not();
		final Condition conditionNotA = conditionA.not();
		assertCondition(expected,    TYPE, conditionL);
		assertCondition(expected,    TYPE, conditionA);
		assertCondition(expectedNot, TYPE, conditionNotL);
		assertCondition(expectedNot, TYPE, conditionNotA);

		assertEquals(conditionA, conditionL);
		assertEquals(conditionNotA, conditionNotL);
		assertEquals(conditionL, conditionNotL.not());
		assertEquals(conditionA, conditionNotA.not());
	}
}

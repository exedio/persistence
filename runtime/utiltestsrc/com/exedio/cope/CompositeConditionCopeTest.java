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

import static com.exedio.cope.CompositeCondition.Operator.AND;
import static com.exedio.cope.CompositeCondition.Operator.OR;
import static com.exedio.cope.Condition.FALSE;
import static com.exedio.cope.Condition.TRUE;
import static com.exedio.cope.Cope.and;
import static com.exedio.cope.Cope.or;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.CompositeCondition.Operator;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CompositeConditionCopeTest
{
	@Test public void testIt()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);

		assertNullPointerException(null, "conditions");
		assertNullPointerException(new Condition[]{null}, "conditions[0]");
		assertNullPointerException(new Condition[]{c1, null, c2}, "conditions[1]");

		assertSameAnd(TRUE,  new Condition[0]);
		assertSameOr (FALSE, new Condition[0]);

		// test composites with a single subcondition
		assertSameAnd(c1, c1);
		assertSameOr (c1, c1);

		assertSameAnd(c1, c1, TRUE);
		assertSameAnd(c1, TRUE, c1);
		assertEqualsAnd(newCompositeCondition(AND, c1, c2), TRUE, c1, c2);
		assertEqualsAnd(newCompositeCondition(AND, c1, c2), c1, TRUE, c2);
		assertEqualsAnd(newCompositeCondition(AND, c1, c2), c1, c2, TRUE);
		assertSameAnd(c1, TRUE, TRUE, c1);
		assertSameAnd(c1, TRUE, c1, TRUE);
		assertSameAnd(c1, c1, TRUE, TRUE);
		assertSameAnd(TRUE, TRUE, TRUE, TRUE);

		assertSameOr(c1, c1, FALSE);
		assertSameOr(c1, FALSE, c1);
		assertEqualsOr(newCompositeCondition(OR, c1, c2), FALSE, c1, c2);
		assertEqualsOr(newCompositeCondition(OR, c1, c2), c1, FALSE, c2);
		assertEqualsOr(newCompositeCondition(OR, c1, c2), c1, c2, FALSE);
		assertSameOr(c1, FALSE, FALSE, c1);
		assertSameOr(c1, FALSE, c1, FALSE);
		assertSameOr(c1, c1, FALSE, FALSE);
		assertSameOr(FALSE, FALSE, FALSE, FALSE);

		assertSameAnd(FALSE, c1, FALSE);
		assertSameAnd(FALSE, FALSE, c1);
		assertSameAnd(FALSE, FALSE, c1, c2);
		assertSameAnd(FALSE, c1, FALSE, c2);
		assertSameAnd(FALSE, c1, c2, FALSE);
		assertSameAnd(FALSE, FALSE, FALSE, c1);
		assertSameAnd(FALSE, FALSE, c1, FALSE);
		assertSameAnd(FALSE, c1, FALSE, FALSE);
		assertSameAnd(FALSE, FALSE, FALSE, FALSE);

		assertSameOr(TRUE, c1, TRUE);
		assertSameOr(TRUE, TRUE, c1);
		assertSameOr(TRUE, TRUE, c1, c2);
		assertSameOr(TRUE, c1, TRUE, c2);
		assertSameOr(TRUE, c1, c2, TRUE);
		assertSameOr(TRUE, TRUE, TRUE, c1);
		assertSameOr(TRUE, TRUE, c1, TRUE);
		assertSameOr(TRUE, c1, TRUE, TRUE);
		assertSameOr(TRUE, TRUE, TRUE, TRUE);

		assertSameAnd(FALSE, TRUE, FALSE, TRUE);
		assertSameOr (TRUE,  FALSE, TRUE, FALSE);
	}

	private static void assertSameAnd(final Condition expected,	final Condition... actual)
	{
		assertSame(expected, and(actual));
		assertSame(expected, and(Arrays.asList(actual)));
	}

	private static void assertSameOr(final Condition expected, final Condition... actual)
	{
		assertSame(expected, or(actual));
		assertSame(expected, or(Arrays.asList(actual)));
	}

	private static void assertEqualsAnd(final Condition expected,	final Condition... actual)
	{
		assertEqualsCondition(expected, and(actual));
		assertEqualsCondition(expected, and(Arrays.asList(actual)));
	}

	private static void assertEqualsOr(final Condition expected, final Condition... actual)
	{
		assertEqualsCondition(expected, or(actual));
		assertEqualsCondition(expected, or(Arrays.asList(actual)));
	}

	private static void assertEqualsCondition(final Condition expected, final Condition actual)
	{
		assertEquals(expected.toString(), actual.toString());
		assertEquals(expected, actual);
		assertNotSame(expected, actual);
	}

	private static void assertNullPointerException(final Condition[] conditions, final String expectedMessage)
	{
		final List<Condition> conditionsList = conditions!=null ? Arrays.asList(conditions) : null;
		try
		{
			and(conditions);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(expectedMessage, e.getMessage());
		}
		try
		{
			and(conditionsList);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(expectedMessage, e.getMessage());
		}
		try
		{
			or(conditions);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(expectedMessage, e.getMessage());
		}
		try
		{
			or(conditionsList);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(expectedMessage, e.getMessage());
		}
	}


	@SuppressWarnings("deprecation")
	private static CompositeCondition newCompositeCondition(
			final Operator operator,
			final Condition... conditions)
	{
		return new CompositeCondition(operator, conditions);
	}
}

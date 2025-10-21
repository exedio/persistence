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
import static com.exedio.cope.Cope.and;
import static com.exedio.cope.Cope.is;
import static com.exedio.cope.Cope.isNot;
import static com.exedio.cope.Cope.or;
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CompositeConditionCopeTest
{
	@Test void testIt()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.is(1d);
		final Condition c2 = field.is(2d);

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
		assertEqualsAnd(newAnd(c1, c2), TRUE, c1, c2);
		assertEqualsAnd(newAnd(c1, c2), c1, TRUE, c2);
		assertEqualsAnd(newAnd(c1, c2), c1, c2, TRUE);
		assertSameAnd(c1, TRUE, TRUE, c1);
		assertSameAnd(c1, TRUE, c1, TRUE);
		assertSameAnd(c1, c1, TRUE, TRUE);
		assertSameAnd(TRUE, TRUE, TRUE, TRUE);

		assertSameOr(c1, c1, FALSE);
		assertSameOr(c1, FALSE, c1);
		assertEqualsOr(newOr(c1, c2), FALSE, c1, c2);
		assertEqualsOr(newOr(c1, c2), c1, FALSE, c2);
		assertEqualsOr(newOr(c1, c2), c1, c2, FALSE);
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

	/**
	 * @see CompositeConditionUtilTest#testFlatting()
	 */
	@Test void testFlatting()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.is(1d);
		final Condition c2 = field.is(2d);
		final Condition c3 = field.is(3d);

		assertEqualsAnd(newAnd(c1, c2, c3), newAnd(c1, c2), c3);
		assertEqualsOr (newOr (c1, c2, c3), newOr (c1, c2), c3);
		assertEqualsAnd(newAnd(c1, c2, c3), c1, newAnd(c2, c3));
		assertEqualsOr (newOr (c1, c2, c3), c1, newOr (c2, c3));

		assertEqualsAnd(newAnd(c1, c2, c3), TRUE , newAnd(c1, c2), c3);
		assertEqualsOr (newOr (c1, c2, c3), FALSE, newOr (c1, c2), c3);
		assertEqualsAnd(newAnd(c1, c2, c3), TRUE , c1, newAnd(c2, c3));
		assertEqualsOr (newOr (c1, c2, c3), FALSE, c1, newOr (c2, c3));

		assertEqualsAnd(newAnd(c1, c2, c3), newAnd(c1, c2), TRUE , c3);
		assertEqualsOr (newOr (c1, c2, c3), newOr (c1, c2), FALSE, c3);
		assertEqualsAnd(newAnd(c1, c2, c3), c1, TRUE , newAnd(c2, c3));
		assertEqualsOr (newOr (c1, c2, c3), c1, FALSE, newOr (c2, c3));

		assertEqualsAnd(newAnd(c1, c2, c3), newAnd(c1, c2), c3, TRUE );
		assertEqualsOr (newOr (c1, c2, c3), newOr (c1, c2), c3, FALSE);
		assertEqualsAnd(newAnd(c1, c2, c3), c1, newAnd(c2, c3), TRUE );
		assertEqualsOr (newOr (c1, c2, c3), c1, newOr (c2, c3), FALSE);

		assertEqualsAnd(newAnd(newOr (c1, c2), c3), newOr (c1, c2), c3);
		assertEqualsOr (newOr (newAnd(c1, c2), c3), newAnd(c1, c2), c3);
		assertEqualsAnd(newAnd(c1, newOr (c2, c3)), c1, newOr (c2, c3));
		assertEqualsOr (newOr (c1, newAnd(c2, c3)), c1, newAnd(c2, c3));
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

	@Test void testFieldsCovered()
	{
		final DoubleField f1 = new DoubleField();
		final DoubleField f2 = new DoubleField();
		final DoubleField f3 = new DoubleField();
		final Condition c1 = f1.is(1d);
		final Condition c2 = f2.is(2d);
		final Condition c3 = f3.is(3d);

		assertFieldsCovered(Arrays.asList(f1, f2), newOr( c1, c2));
		assertFieldsCovered(Arrays.asList(f2, f3), newAnd(c2, c3));
		assertFieldsCovered(Arrays.asList(f2, f3, f1), newAnd(c2, c3, c1));
		assertFieldsCovered(Arrays.asList(f2, f3, f1), newAnd(c2, c3, c1).not());
		assertFieldsCovered(Arrays.asList(f1, f1, f1), newAnd(c1, c1, c1));
		assertFieldsCovered(Arrays.asList(), FALSE);
		assertFieldsCovered(Arrays.asList(), TRUE);
		assertFieldsCovered(Arrays.asList(f1), new InCondition<>(f1, false, List.of(1d, 2d)));
	}

	private static void assertNullPointerException(final Condition[] conditions, final String expectedMessage)
	{
		final List<Condition> conditionsList = conditions!=null ? Arrays.asList(conditions) : null;
		assertFails(
				() -> and(conditions),
				NullPointerException.class,
				expectedMessage);
		assertFails(
				() -> and(conditionsList),
				NullPointerException.class,
				expectedMessage);
		assertFails(
				() -> or(conditions),
				NullPointerException.class,
				expectedMessage);
		assertFails(
				() -> or(conditionsList),
				NullPointerException.class,
				expectedMessage);
	}


	@SuppressWarnings("deprecation")
	private static CompositeCondition newAnd(
			final Condition... conditions)
	{
		return new CompositeCondition(AND, conditions);
	}

	@SuppressWarnings("deprecation")
	private static CompositeCondition newOr(
			final Condition... conditions)
	{
		return new CompositeCondition(OR, conditions);
	}


	@Test void testIs()
	{
		final StringField f = new StringField();
		assertEquals(f +  "='a'",    is(f, "a").toString());
		assertEquals(f + "<>'a'", isNot(f, "a").toString());
		assertEquals(f +  "=''",    is(f, "").toString());
		assertEquals(f + "<>''", isNot(f, "").toString());
		assertEquals(f + " is " +"null",    is(f, null).toString());
		assertEquals(f + " is not null", isNot(f, null).toString());
	}


	private static final Condition TRUE = Condition.ofTrue();
	private static final Condition FALSE = Condition.ofFalse();
}

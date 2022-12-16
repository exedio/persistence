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
import static com.exedio.cope.CompositeCondition.in;
import static com.exedio.cope.Condition.FALSE;
import static com.exedio.cope.Condition.TRUE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CompositeCondition.Operator;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CompositeConditionUtilTest
{
	@Test void testIt()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);
		final Condition c3 = field.equal(3d);

		assertNotEqualsAndHash(TRUE, FALSE, c1, c2, c3);

		assertFails(
				() -> newCompositeCondition(null, (Condition[])null),
				NullPointerException.class,
				"operator");
		assertFails(
				() -> newCompositeCondition(AND),
				IllegalArgumentException.class,
				"conditions must not be empty");
		assertFails(
				() -> newCompositeCondition(AND, Collections.emptyList()),
				IllegalArgumentException.class,
				"conditions must not be empty");
		assertFails(
				() -> newCompositeCondition(OR),
				IllegalArgumentException.class,
				"conditions must not be empty");
		assertFails(
				() -> newCompositeCondition(OR, Collections.emptyList()),
				IllegalArgumentException.class,
				"conditions must not be empty");
		assertFails(
				() -> newCompositeCondition(AND, asList((Condition)null)),
				NullPointerException.class,
				"conditions[0]");
		assertFails(
				() -> newCompositeCondition(OR, (Condition)null),
				NullPointerException.class,
				"conditions[0]");
		assertFails(
				() -> newCompositeCondition(OR, asList((Condition)null)),
				NullPointerException.class,
				"conditions[0]");
		assertFails(
				() -> newCompositeCondition(AND, TRUE),
				IllegalArgumentException.class,
				"conditions[0] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(AND, asList(TRUE)),
				IllegalArgumentException.class,
				"conditions[0] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(OR, TRUE),
				IllegalArgumentException.class,
				"conditions[0] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(OR, asList(TRUE)),
				IllegalArgumentException.class,
				"conditions[0] must not be a literal, but was TRUE");

		// test flattening of CompositeCondition
		assertEquals(newCompositeCondition(AND, c1, c2, c3), c1.and(c2).and(c3));
		assertEquals(newCompositeCondition(AND, c1, c2, c3), c1.and(c2.and(c3)));
		assertEquals(newCompositeCondition(OR,  c1, c2, c3), c1.or(c2).or(c3));
		assertEquals(newCompositeCondition(OR,  c1, c2, c3), c1.or(c2.or(c3)));

		assertEquals(newCompositeCondition(AND, newCompositeCondition(OR,  c1, c2), c3), c1.or(c2).and(c3));
		assertEquals(newCompositeCondition(AND, c1, newCompositeCondition(OR,  c2, c3)), c1.and(c2.or(c3)));
		assertEquals(newCompositeCondition(OR,  newCompositeCondition(AND, c1, c2), c3), c1.and(c2).or(c3));
		assertEquals(newCompositeCondition(OR,  c1, newCompositeCondition(AND, c2, c3)), c1.or(c2.and(c3)));
	}

	@Test void testNot()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		assertFails(
				() -> new NotCondition(null),
				NullPointerException.class,
				"argument");
		assertFails(
				() -> new NotCondition(TRUE),
				IllegalArgumentException.class,
				"argument must not be a literal");
		assertSame(TRUE, FALSE.not());
		assertSame(FALSE, TRUE.not());
		assertEquals(new NotCondition(c1), c1.not());
		assertSame(c1, c1.not().not());
	}

	@Test void testNeutrumAbsolutum()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);

		// Condition.and/or
		assertSame(c1, c1.and(TRUE));
		assertSame(c1, TRUE.and(c1));
		assertSame(c1, c1.or(FALSE));
		assertSame(c1, FALSE.or(c1));

		assertSame(FALSE, c1.and(FALSE));
		assertSame(FALSE, FALSE.and(c1));
		assertSame(TRUE,  c1.or(TRUE));
		assertSame(TRUE,  TRUE.or(c1));

		assertSame(FALSE, TRUE.and(FALSE));
		assertSame(FALSE, FALSE.and(TRUE));
		assertSame(TRUE,  TRUE.or(FALSE));
		assertSame(TRUE,  FALSE.or(TRUE));

		assertFails(
				() -> FALSE.and(null),
				NullPointerException.class,
				"other");
		assertFails(
				() -> TRUE.or(null),
				NullPointerException.class,
				"other");
		assertFails(
				() -> TRUE.and(null),
				NullPointerException.class,
				"other");
		assertFails(
				() -> FALSE.or(null),
				NullPointerException.class,
				"other");

		// Function.in
		assertEquals(newCompositeCondition(OR, c1, c2), field.in(1.0, 2.0));
		assertEquals(newCompositeCondition(OR, c1, c2), field.in(asList(1.0, 2.0)));
		assertEquals(newCompositeCondition(OR, c1, c2), in(field, 1.0, 2.0));
		assertEquals(newCompositeCondition(OR, c1, c2), in(field, asList(1.0, 2.0)));
		assertEquals(c1, field.in(1.0));
		assertEquals(c1, field.in(asList(1.0)));
		assertEquals(c1, in(field, 1.0));
		assertEquals(c1, in(field, asList(1.0)));
		assertEquals(c2, field.in(2.0));
		assertEquals(c2, field.in(asList(2.0)));
		assertEquals(c2, in(field, 2.0));
		assertEquals(c2, in(field, asList(2.0)));
		assertSame(FALSE, field.in());
		assertSame(FALSE, field.in(asList()));
		assertSame(FALSE, in(field));
		assertSame(FALSE, in(field, asList()));

		// Condition.valueOf
		assertSame(TRUE,  Condition.valueOf(true));
		assertSame(FALSE, Condition.valueOf(false));
	}


	@SuppressWarnings({"deprecation", "UnusedReturnValue"})
	private static CompositeCondition newCompositeCondition(
			final Operator operator,
			final List<? extends Condition> conditions)
	{
		return new CompositeCondition(operator, conditions);
	}

	@SuppressWarnings("deprecation")
	private static CompositeCondition newCompositeCondition(
			final Operator operator,
			final Condition... conditions)
	{
		return new CompositeCondition(operator, conditions);
	}
}

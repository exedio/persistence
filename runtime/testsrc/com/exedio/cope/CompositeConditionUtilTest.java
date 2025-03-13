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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CompositeCondition.Operator;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

public class CompositeConditionUtilTest
{
	@Test void testIt()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.is(1d);
		final Condition c2 = field.is(2d);
		final Condition c3 = field.is(3d);

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
				() -> newCompositeCondition(AND, c1),
				IllegalArgumentException.class,
				"conditions must contain more than one element");
		assertFails(
				() -> newCompositeCondition(AND, List.of(c1)),
				IllegalArgumentException.class,
				"conditions must contain more than one element");
		assertFails(
				() -> newCompositeCondition(OR, c1),
				IllegalArgumentException.class,
				"conditions must contain more than one element");
		assertFails(
				() -> newCompositeCondition(OR, List.of(c1)),
				IllegalArgumentException.class,
				"conditions must contain more than one element");
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
				() -> newCompositeCondition(AND, c1, TRUE),
				IllegalArgumentException.class,
				"conditions[1] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(AND, asList(c1, TRUE)),
				IllegalArgumentException.class,
				"conditions[1] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(OR, c1, TRUE),
				IllegalArgumentException.class,
				"conditions[1] must not be a literal, but was TRUE");
		assertFails(
				() -> newCompositeCondition(OR, asList(c1, TRUE)),
				IllegalArgumentException.class,
				"conditions[1] must not be a literal, but was TRUE");
	}

	/**
	 * @see CompositeConditionCopeTest#testFlatting()
	 */
	@Test void testFlatting()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.is(1d);
		final Condition c2 = field.is(2d);
		final Condition c3 = field.is(3d);

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
		final StringField field = new StringField().optional();
		final Condition c1 = field.regexpLike("myRegexp");
		assertSame(TRUE, FALSE.not());
		assertSame(FALSE, TRUE.not());
		assertEquals("!(" + c1 + ")", c1.not().toString());
		assertSame(c1, c1.not().not());
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testNotConstructor()
	{
		final StringField field = new StringField().optional();
		final Condition c1 = field.regexpLike("myRegexp");
		assertFails(
				() -> new NotCondition(null),
				NullPointerException.class,
				"argument");
		assertFails(
				() -> new NotCondition(TRUE),
				IllegalArgumentException.class,
				"argument must not be a literal");
		assertEquals(c1.not(), new NotCondition(c1));
	}

	@Test void testNeutrumAbsolutum()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.is(1d);
		final Condition c2 = field.is(2d);

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
		assertEquals(newInCondition(field, false, 1.0, 2.0), field.in(1.0, 2.0));
		assertEquals(newInCondition(field, false, 1.0, 2.0), field.in(asList(1.0, 2.0)));
		assertEquals(newInCondition(field, false, 1.0, 2.0), in(field, 1.0, 2.0));
		assertEquals(newInCondition(field, false, 1.0, 2.0), in(field, asList(1.0, 2.0)));

		assertEquals(newInCondition(field, true, 1.0, 2.0), field.in(1.0, 2.0).not());
		assertEquals(newInCondition(field, true, 1.0, 2.0), field.in(asList(1.0, 2.0)).not());
		assertEquals(newInCondition(field, true, 1.0, 2.0), in(field, 1.0, 2.0).not());
		assertEquals(newInCondition(field, true, 1.0, 2.0), in(field, asList(1.0, 2.0)).not());

		assertEquals(newCompositeCondition(OR, field.isNull(), c1), field.in(null, 1.0));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), field.in(asList(null, 1.0)));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), in(field, null, 1.0));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), in(field, asList(null, 1.0)));

		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), field.in(null, 1.0).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), field.in(asList(null, 1.0)).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), in(field, null, 1.0).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), in(field, asList(null, 1.0)).not());

		assertEquals(newCompositeCondition(OR, field.isNull(), c1), field.in(1.0, null));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), field.in(asList(1.0, null)));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), in(field, 1.0, null));
		assertEquals(newCompositeCondition(OR, field.isNull(), c1), in(field, asList(1.0, null)));

		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), field.in(1.0, null).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), field.in(asList(1.0, null)).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), in(field, 1.0, null).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), c1.not()), in(field, asList(1.0, null)).not());

		assertEquals(newCompositeCondition(OR, field.isNull(), newInCondition(field, false, 1.0, 2.0)), field.in(null, 1.0, 2.0));
		assertEquals(newCompositeCondition(OR, field.isNull(), newInCondition(field, false, 1.0, 2.0)), field.in(asList(null, 1.0, 2.0)));
		assertEquals(newCompositeCondition(OR, field.isNull(), newInCondition(field, false, 1.0, 2.0)), in(field, null, 1.0, 2.0));
		assertEquals(newCompositeCondition(OR, field.isNull(), newInCondition(field, false, 1.0, 2.0)), in(field, asList(null, 1.0, 2.0)));

		assertEquals(newCompositeCondition(AND, field.isNotNull(), newInCondition(field, true, 1.0, 2.0)), field.in(null, 1.0, 2.0).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), newInCondition(field, true, 1.0, 2.0)), field.in(asList(null, 1.0, 2.0)).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), newInCondition(field, true, 1.0, 2.0)), in(field, null, 1.0, 2.0).not());
		assertEquals(newCompositeCondition(AND, field.isNotNull(), newInCondition(field, true, 1.0, 2.0)), in(field, asList(null, 1.0, 2.0)).not());

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
		assertSame(TRUE,  Condition.of(true));
		assertSame(FALSE, Condition.of(false));
	}

	@Test void testIn()
	{
		final StringField field = new StringField().optional();
		final Condition c1 = field.in("AAA", "BBB");
		assertEquals(field+" in ('AAA','BBB')", c1.toString());

		final Condition c2 = field.in(asList("AAA", "BBB"));
		assertEquals(field+" in ('AAA','BBB')", c2.toString());

		final Condition c1n = field.in("AAA", "BBB").not();
		assertEquals(field+" not in ('AAA','BBB')", c1n.toString());

		final Condition c2n = field.in(asList("AAA", "BBB")).not();
		assertEquals(field+" not in ('AAA','BBB')", c2n.toString());

		final Condition c3 = field.in("AAA", null);
		assertEquals("("+field + " is null or "+field+"='AAA')", c3.toString());

		final Condition c4 = field.in(asList("AAA", null));
		assertEquals("("+field + " is null or "+field+"='AAA')", c4.toString());

		final Condition c3n = field.in("AAA", null).not();
		assertEquals("("+field + " is not null and "+field+"<>'AAA')", c3n.toString());

		final Condition c4n = field.in(asList("AAA", null)).not();
		assertEquals("("+field + " is not null and "+field+"<>'AAA')", c4n.toString());

		final Condition c5 = field.in((String)null);
		assertEquals(field + " is null", c5.toString());

		final Condition c6 = field.in(asList((String)null));
		assertEquals(field + " is null", c6.toString());

		final Condition c5n = field.in((String)null).not();
		assertEquals(field + " is not null", c5n.toString());

		final Condition c6n = field.in(asList((String)null)).not();
		assertEquals(field + " is not null", c6n.toString());

		final Condition c7 = field.in("AAA");
		assertEquals(field + "='AAA'", c7.toString());

		final Condition c8 = field.in(asList("AAA"));
		assertEquals(field + "='AAA'", c8.toString());

		final Condition c7n = field.in("AAA").not();
		assertEquals(field + "<>'AAA'", c7n.toString());

		final Condition c8n = field.in(asList("AAA")).not();
		assertEquals(field + "<>'AAA'", c8n.toString());

		final Condition c9 = field.in(null, null);
		assertEquals(field + " is null", c9.toString());

		final Condition cA = field.in(asList(null, null));
		assertEquals(field + " is null", cA.toString());

		final Condition c9n = field.in(null, null).not();
		assertEquals(field + " is not null", c9n.toString());

		final Condition cAn = field.in(asList(null, null)).not();
		assertEquals(field + " is not null", cAn.toString());
	}

	@Test void testInImmutableArray()
	{
		final StringField field = new StringField().optional();
		final String[] values = {"AAA", "BBB"};
		final Condition c = field.in(values);
		assertEquals(field + " in ('AAA','BBB')", c.toString());
		values[0] = "AAx";
		assertEquals(field + " in ('AAA','BBB')", c.toString());
	}

	@Test void testInImmutableList()
	{
		final StringField field = new StringField().optional();
		final List<String> values = asList("AAA", "BBB");
		final Condition c = field.in(values);
		assertEquals(field + " in ('AAA','BBB')", c.toString());
		values.set(0, "AAx");
		assertEquals(field + " in ('AAA','BBB')", c.toString());
	}

	@Test void testInImmutableArrayNull()
	{
		final StringField field = new StringField().optional();
		final String[] values = {"AAA", "BBB", null};
		final Condition c = field.in(values);
		assertEquals("(" + field + " is null or " + field + " in ('AAA','BBB'))", c.toString());
		values[0] = "AAx";
		assertEquals("(" + field + " is null or " + field + " in ('AAA','BBB'))", c.toString());
	}

	@Test void testInImmutableListNull()
	{
		final StringField field = new StringField().optional();
		final List<String> values = asList("AAA", "BBB", null);
		final Condition c = field.in(values);
		assertEquals("(" + field + " is null or " + field + " in ('AAA','BBB'))", c.toString());
		values.set(0, "AAx");
		assertEquals("(" + field + " is null or " + field + " in ('AAA','BBB'))", c.toString());
	}

	@Test
	@Deprecated // OK: testing deprecated API
	void testDeprecatedLiteral()
	{
		assertSame(TRUE, Condition.TRUE);
		assertSame(FALSE, Condition.FALSE);
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

	@Nonnull
	private static InCondition<Double> newInCondition(final DoubleField field, final boolean not, final Double ... values)
	{
		return new InCondition<>(field, not, List.of(values));
	}

	private static final Condition TRUE = Condition.ofTrue();
	private static final Condition FALSE = Condition.ofFalse();
}

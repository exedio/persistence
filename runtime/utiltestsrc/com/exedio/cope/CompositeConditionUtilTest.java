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
import static java.util.Arrays.asList;

import com.exedio.cope.CompositeCondition.Operator;
import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class CompositeConditionUtilTest extends CopeAssert
{
	@Test public void testIt()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		final Condition c2 = field.equal(2d);
		final Condition c3 = field.equal(3d);

		assertEquals(TRUE, TRUE);
		assertEquals(FALSE, FALSE);
		assertFalse(TRUE.equals(FALSE));
		assertFalse(FALSE.equals(TRUE));

		assertFalse(TRUE.equals(c1));
		assertFalse(c1.equals(TRUE));
		assertFalse(FALSE.equals(c1));
		assertFalse(c1.equals(FALSE));

		try
		{
			newCompositeCondition(null, (Condition[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("operator", e.getMessage());
		}
		try
		{
			newCompositeCondition(AND);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			newCompositeCondition(AND, Collections.<Condition>emptyList());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR, Collections.<Condition>emptyList());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions must not be empty", e.getMessage());
		}
		try
		{
			newCompositeCondition(AND, asList((Condition)null));
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR, (Condition)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR, asList((Condition)null));
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("conditions[0]", e.getMessage());
		}
		try
		{
			newCompositeCondition(AND, Condition.TRUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal, but was TRUE", e.getMessage());
		}
		try
		{
			newCompositeCondition(AND, asList(Condition.TRUE));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal, but was TRUE", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR, Condition.TRUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal, but was TRUE", e.getMessage());
		}
		try
		{
			newCompositeCondition(OR, asList(Condition.TRUE));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("conditions[0] must not be a literal, but was TRUE", e.getMessage());
		}

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

	@Test public void testNot()
	{
		final DoubleField field = new DoubleField().optional();
		final Condition c1 = field.equal(1d);
		try
		{
			new NotCondition(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("argument", e.getMessage());
		}
		try
		{
			new NotCondition(Condition.TRUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("argument must not be a literal", e.getMessage());
		}
		assertSame(TRUE, FALSE.not());
		assertSame(FALSE, TRUE.not());
		assertEquals(new NotCondition(c1), c1.not());
		assertSame(c1, c1.not().not());
	}

	@Test public void testNeutrumAbsolutum()
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

		// Function.in
		assertEquals(newCompositeCondition(OR, c1, c2), field.in(1.0, 2.0));
		assertEquals(newCompositeCondition(OR, c1, c2), field.in(asList(1.0, 2.0)));
		assertEquals(c1, field.in(1.0));
		assertEquals(c1, field.in(asList(1.0)));
		assertEquals(c2, field.in(2.0));
		assertEquals(c2, field.in(asList(2.0)));
		assertSame(FALSE, field.in());
		assertSame(FALSE, field.in(Arrays.<Double>asList()));

		// Condition.valueOf
		assertSame(Condition.TRUE,  Condition.valueOf(true));
		assertSame(Condition.FALSE, Condition.valueOf(false));
	}


	@SuppressWarnings("deprecation")
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

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

import static com.exedio.cope.Condition.FALSE;
import static com.exedio.cope.Condition.TRUE;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CompareFunctionCondition.Operator;
import org.junit.jupiter.api.Test;

public class IntegerFieldTest
{
	@Test void testQueryCanonize()
	{
		final IntegerField any = new IntegerField().optional();
		final IntegerField mandatory = new IntegerField();
		final IntegerField min4 = new IntegerField().optional().min(4);
		final IntegerField max4 = new IntegerField().optional().max(4);
		final IntegerField min4Max8 = new IntegerField().optional().range(4, 8);

		assertEquals(in(any), any.isNull());
		assertEquals(nn(any), any.isNotNull());
		assertEquals(in(any), any.equal((Integer)null));
		assertEquals(nn(any), any.notEqual((Integer)null));
		assertEquals(cc(Operator.Equal, any, 0), any.equal(0));
		assertEquals(cc(Operator.Equal, any, MIN_VALUE), any.equal(MIN_VALUE));
		assertEquals(cc(Operator.Equal, any, MAX_VALUE), any.equal(MAX_VALUE));
		assertEquals(cc(Operator.NotEqual, any, 0), any.notEqual(0));
		assertEquals(cc(Operator.NotEqual, any, MIN_VALUE), any.notEqual(MIN_VALUE));
		assertEquals(cc(Operator.NotEqual, any, MAX_VALUE), any.notEqual(MAX_VALUE));
		assertEquals(cc(Operator.Less, any, 0), any.less(0));
		assertEquals(cc(Operator.Less, any, MIN_VALUE), any.less(MIN_VALUE));
		assertEquals(cc(Operator.Less, any, MAX_VALUE), any.less(MAX_VALUE));
		assertEquals(cc(Operator.LessEqual, any, 0), any.lessOrEqual(0));
		assertEquals(cc(Operator.LessEqual, any, MIN_VALUE), any.lessOrEqual(MIN_VALUE));
		assertEquals(cc(Operator.LessEqual, any, MAX_VALUE), any.lessOrEqual(MAX_VALUE));
		assertEquals(cc(Operator.Greater, any, 0), any.greater(0));
		assertEquals(cc(Operator.Greater, any, MIN_VALUE), any.greater(MIN_VALUE));
		assertEquals(cc(Operator.Greater, any, MAX_VALUE), any.greater(MAX_VALUE));
		assertEquals(cc(Operator.GreaterEqual, any, 0), any.greaterOrEqual(0));
		assertEquals(cc(Operator.GreaterEqual, any, MIN_VALUE), any.greaterOrEqual(MIN_VALUE));
		assertEquals(cc(Operator.GreaterEqual, any, MAX_VALUE), any.greaterOrEqual(MAX_VALUE));

		assertEquals(in(mandatory), mandatory.isNull());
		assertEquals(nn(mandatory), mandatory.isNotNull());
		assertEquals(in(mandatory), mandatory.equal((Integer)null));
		assertEquals(nn(mandatory), mandatory.notEqual((Integer)null));
		assertEquals(cc(Operator.Equal, mandatory, 0), mandatory.equal(0));
		assertEquals(cc(Operator.Equal, mandatory, MIN_VALUE), mandatory.equal(MIN_VALUE));
		assertEquals(cc(Operator.Equal, mandatory, MAX_VALUE), mandatory.equal(MAX_VALUE));

		assertEquals(in(min4), min4.equal((Integer)null));
		assertEquals(FALSE, min4.equal(0));
		assertEquals(FALSE, min4.equal(3));
		assertEquals(cc(Operator.Equal, min4, 4), min4.equal(4));
		assertEquals(FALSE, min4.equal(MIN_VALUE));
		assertEquals(cc(Operator.Equal, min4, MAX_VALUE), min4.equal(MAX_VALUE));

		assertEquals(in(max4), max4.equal((Integer)null));
		assertEquals(cc(Operator.Equal, max4, 0), max4.equal(0));
		assertEquals(cc(Operator.Equal, max4, 3), max4.equal(3));
		assertEquals(cc(Operator.Equal, max4, 4), max4.equal(4));
		assertEquals(FALSE, max4.equal(5));
		assertEquals(cc(Operator.Equal, max4, MIN_VALUE), max4.equal(MIN_VALUE));
		assertEquals(FALSE, max4.equal(MAX_VALUE));

		assertEquals(in(min4Max8), min4Max8.isNull());
		assertEquals(nn(min4Max8), min4Max8.isNotNull());
		assertEquals(in(min4Max8), min4Max8.equal((Integer)null));
		assertEquals(nn(min4Max8), min4Max8.notEqual((Integer)null));
		assertEquals(FALSE, min4Max8.equal(0));
		assertEquals(FALSE, min4Max8.equal(3));
		assertEquals(cc(Operator.Equal, min4Max8, 4), min4Max8.equal(4));
		assertEquals(cc(Operator.Equal, min4Max8, 8), min4Max8.equal(8));
		assertEquals(FALSE, min4Max8.equal(9));
		assertEquals(FALSE, min4Max8.equal(MIN_VALUE));
		assertEquals(FALSE, min4Max8.equal(MAX_VALUE));
		assertEquals(TRUE,  min4Max8.notEqual(0));
		assertEquals(TRUE,  min4Max8.notEqual(3));
		assertEquals(cc(Operator.NotEqual, min4Max8, 4), min4Max8.notEqual(4));
		assertEquals(cc(Operator.NotEqual, min4Max8, 8), min4Max8.notEqual(8));
		assertEquals(TRUE, min4Max8.notEqual(9));
		assertEquals(TRUE, min4Max8.notEqual(MIN_VALUE));
		assertEquals(TRUE, min4Max8.notEqual(MAX_VALUE));
		assertEquals(cc(Operator.Less, min4Max8, 0), min4Max8.less(0));
		assertEquals(cc(Operator.Less, min4Max8, 3), min4Max8.less(3));
		assertEquals(cc(Operator.Less, min4Max8, 4), min4Max8.less(4));
		assertEquals(cc(Operator.Less, min4Max8, 5), min4Max8.less(5));
		assertEquals(cc(Operator.Less, min4Max8, MIN_VALUE), min4Max8.less(MIN_VALUE));
		assertEquals(cc(Operator.Less, min4Max8, MAX_VALUE), min4Max8.less(MAX_VALUE));
		assertEquals(cc(Operator.LessEqual, min4Max8,  0), min4Max8.lessOrEqual( 0));
		assertEquals(cc(Operator.LessEqual, min4Max8,  3), min4Max8.lessOrEqual( 3));
		assertEquals(cc(Operator.LessEqual, min4Max8,  4), min4Max8.lessOrEqual( 4));
		assertEquals(cc(Operator.LessEqual, min4Max8,  5), min4Max8.lessOrEqual( 5));
		assertEquals(cc(Operator.LessEqual, min4Max8,  8), min4Max8.lessOrEqual( 8));
		assertEquals(cc(Operator.LessEqual, min4Max8,  9), min4Max8.lessOrEqual( 9));
		assertEquals(cc(Operator.LessEqual, min4Max8, 10), min4Max8.lessOrEqual(10));
		assertEquals(cc(Operator.LessEqual, min4Max8, MIN_VALUE), min4Max8.lessOrEqual(MIN_VALUE));
		assertEquals(cc(Operator.LessEqual, min4Max8, MAX_VALUE), min4Max8.lessOrEqual(MAX_VALUE));
		assertEquals(cc(Operator.Greater, min4Max8, 0), min4Max8.greater(0));
		assertEquals(cc(Operator.Greater, min4Max8, 2), min4Max8.greater(2));
		assertEquals(cc(Operator.Greater, min4Max8, 3), min4Max8.greater(3));
		assertEquals(cc(Operator.Greater, min4Max8, 4), min4Max8.greater(4));
		assertEquals(cc(Operator.Greater, min4Max8, 6), min4Max8.greater(6));
		assertEquals(cc(Operator.Greater, min4Max8, 7), min4Max8.greater(7));
		assertEquals(cc(Operator.Greater, min4Max8, 8), min4Max8.greater(8));
		assertEquals(cc(Operator.Greater, min4Max8, 9), min4Max8.greater(9));
		assertEquals(cc(Operator.Greater, min4Max8, MIN_VALUE), min4Max8.greater(MIN_VALUE));
		assertEquals(cc(Operator.Greater, min4Max8, MAX_VALUE), min4Max8.greater(MAX_VALUE));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 0), min4Max8.greaterOrEqual(0));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 2), min4Max8.greaterOrEqual(2));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 3), min4Max8.greaterOrEqual(3));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 4), min4Max8.greaterOrEqual(4));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 6), min4Max8.greaterOrEqual(6));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 7), min4Max8.greaterOrEqual(7));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 8), min4Max8.greaterOrEqual(8));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 9), min4Max8.greaterOrEqual(9));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, MIN_VALUE), min4Max8.greaterOrEqual(MIN_VALUE));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, MAX_VALUE), min4Max8.greaterOrEqual(MAX_VALUE));
	}

	@Test void testOptional()
	{
		final IntegerField orig = new IntegerField().optional();
		assertEquals(false, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertEquals(MIN_VALUE, orig.getMinimum());
		assertEquals(MAX_VALUE, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertEquals(MIN_VALUE, copy.getMinimum());
		assertEquals(MAX_VALUE, copy.getMaximum());
	}

	@Test void testMin()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().min(10);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNull(orig.getImplicitUniqueConstraint());
		assertEquals(10, orig.getMinimum());
		assertEquals(MAX_VALUE, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNull(copy.getImplicitUniqueConstraint());
		assertEquals(10, copy.getMinimum());
		assertEquals(MAX_VALUE, copy.getMaximum());
	}

	@Test void testUnique()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().unique().min(20);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNotNull(orig.getImplicitUniqueConstraint());
		assertEquals(20, orig.getMinimum());
		assertEquals(MAX_VALUE, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNotNull(copy.getImplicitUniqueConstraint());
		assertEquals(20, copy.getMinimum());
		assertEquals(MAX_VALUE, copy.getMaximum());
	}

	@Test void testMax()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().max(30);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNull(orig.getImplicitUniqueConstraint());
		assertEquals(MIN_VALUE, orig.getMinimum());
		assertEquals(30, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNull(copy.getImplicitUniqueConstraint());
		assertEquals(MIN_VALUE, copy.getMinimum());
		assertEquals(30, copy.getMaximum());
	}

	@Test void testRange()
	{
		final IntegerField orig = new IntegerField().range(10, 20);
		assertEquals(false, orig.isFinal());
		assertEquals(true, orig.isMandatory());
		assertEquals(10, orig.getMinimum());
		assertEquals(20, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(true, copy.isMandatory());
		assertEquals(10, copy.getMinimum());
		assertEquals(20, copy.getMaximum());
	}

	@Test void testIllegalRange()
	{
		assertIllegalRange(0,  0,  "maximum must be greater than minimum, but was 0 and 0");
		assertIllegalRange(22, 22, "maximum must be greater than minimum, but was 22 and 22");
		assertIllegalRange(22, 21, "maximum must be greater than minimum, but was 21 and 22");
		assertIllegalRange(MAX_VALUE, MIN_VALUE, "maximum must be greater than minimum, but was " + MIN_VALUE + " and " + MAX_VALUE);
		assertIllegalRange(MIN_VALUE, MIN_VALUE, "maximum must be greater than minimum, but was " + MIN_VALUE + " and " + MIN_VALUE);
		assertIllegalRange(MAX_VALUE, MAX_VALUE, "maximum must be greater than minimum, but was " + MAX_VALUE + " and " + MAX_VALUE);
	}

	private static void assertIllegalRange(final int minimum, final int maximum, final String message)
	{
		final IntegerField f = new IntegerField().optional();
		try
		{
			f.range(minimum, maximum);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	private static IsNullCondition<Integer> in(
			final IntegerField field)
	{
		return field.isNull();
	}

	private static IsNullCondition<Integer> nn(
			final IntegerField field)
	{
		return field.isNotNull();
	}

	private static CompareCondition<Integer> cc(
			final Operator operator,
			final IntegerField field,
			final Integer value)
	{
		return new CompareCondition<>(operator, field, value);
	}
}

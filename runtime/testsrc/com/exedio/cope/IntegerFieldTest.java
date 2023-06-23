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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class IntegerFieldTest
{
	@Test void testQueryCanonize()
	{
		final IntegerField any = new IntegerField().optional();
		final IntegerField mandatory = new IntegerField();
		final IntegerField min4Max8 = new IntegerField().optional().range(4, 8);

		assertEquals(in(any), any.isNull());
		assertEquals(nn(any), any.isNotNull());
		assertEquals(in(any), any.equal((Integer)null));
		assertEquals(nn(any), any.notEqual((Integer)null));
		assertEquals(cc(Operator.Equal, any, 0), any.equal(0));
		assertEquals(cc(Operator.Equal, any, MIN), any.equal(MIN));
		assertEquals(cc(Operator.Equal, any, MAX), any.equal(MAX));
		assertEquals(cc(Operator.NotEqual, any, 0), any.notEqual(0));
		assertEquals(cc(Operator.NotEqual, any, MIN), any.notEqual(MIN));
		assertEquals(cc(Operator.NotEqual, any, MAX), any.notEqual(MAX));
		assertEquals(cc(Operator.Less, any, 0), any.less(0));
		assertEquals(cc(Operator.Less, any, MIN), any.less(MIN));
		assertEquals(cc(Operator.Less, any, MAX), any.less(MAX));
		assertEquals(cc(Operator.LessEqual, any, 0), any.lessOrEqual(0));
		assertEquals(cc(Operator.LessEqual, any, MIN), any.lessOrEqual(MIN));
		assertEquals(cc(Operator.LessEqual, any, MAX), any.lessOrEqual(MAX));
		assertEquals(cc(Operator.Greater, any, 0), any.greater(0));
		assertEquals(cc(Operator.Greater, any, MIN), any.greater(MIN));
		assertEquals(cc(Operator.Greater, any, MAX), any.greater(MAX));
		assertEquals(cc(Operator.GreaterEqual, any, 0), any.greaterOrEqual(0));
		assertEquals(cc(Operator.GreaterEqual, any, MIN), any.greaterOrEqual(MIN));
		assertEquals(cc(Operator.GreaterEqual, any, MAX), any.greaterOrEqual(MAX));

		assertEquals(in(mandatory), mandatory.isNull());
		assertEquals(nn(mandatory), mandatory.isNotNull());
		assertEquals(in(mandatory), mandatory.equal((Integer)null));
		assertEquals(nn(mandatory), mandatory.notEqual((Integer)null));
		assertEquals(cc(Operator.Equal, mandatory, 0), mandatory.equal(0));
		assertEquals(cc(Operator.Equal, mandatory, MIN), mandatory.equal(MIN));
		assertEquals(cc(Operator.Equal, mandatory, MAX), mandatory.equal(MAX));

		assertEquals(in(min4Max8), min4Max8.isNull());
		assertEquals(nn(min4Max8), min4Max8.isNotNull());
		assertEquals(cc(Operator.Less, min4Max8, 0), min4Max8.less(0));
		assertEquals(cc(Operator.Less, min4Max8, 3), min4Max8.less(3));
		assertEquals(cc(Operator.Less, min4Max8, 4), min4Max8.less(4));
		assertEquals(cc(Operator.Less, min4Max8, 5), min4Max8.less(5));
		assertEquals(cc(Operator.Less, min4Max8, MIN), min4Max8.less(MIN));
		assertEquals(cc(Operator.Less, min4Max8, MAX), min4Max8.less(MAX));
		assertEquals(cc(Operator.LessEqual, min4Max8,  0), min4Max8.lessOrEqual( 0));
		assertEquals(cc(Operator.LessEqual, min4Max8,  3), min4Max8.lessOrEqual( 3));
		assertEquals(cc(Operator.LessEqual, min4Max8,  4), min4Max8.lessOrEqual( 4));
		assertEquals(cc(Operator.LessEqual, min4Max8,  5), min4Max8.lessOrEqual( 5));
		assertEquals(cc(Operator.LessEqual, min4Max8,  8), min4Max8.lessOrEqual( 8));
		assertEquals(cc(Operator.LessEqual, min4Max8,  9), min4Max8.lessOrEqual( 9));
		assertEquals(cc(Operator.LessEqual, min4Max8, 10), min4Max8.lessOrEqual(10));
		assertEquals(cc(Operator.LessEqual, min4Max8, MIN), min4Max8.lessOrEqual(MIN));
		assertEquals(cc(Operator.LessEqual, min4Max8, MAX), min4Max8.lessOrEqual(MAX));
		assertEquals(cc(Operator.Greater, min4Max8, 0), min4Max8.greater(0));
		assertEquals(cc(Operator.Greater, min4Max8, 2), min4Max8.greater(2));
		assertEquals(cc(Operator.Greater, min4Max8, 3), min4Max8.greater(3));
		assertEquals(cc(Operator.Greater, min4Max8, 4), min4Max8.greater(4));
		assertEquals(cc(Operator.Greater, min4Max8, 6), min4Max8.greater(6));
		assertEquals(cc(Operator.Greater, min4Max8, 7), min4Max8.greater(7));
		assertEquals(cc(Operator.Greater, min4Max8, 8), min4Max8.greater(8));
		assertEquals(cc(Operator.Greater, min4Max8, 9), min4Max8.greater(9));
		assertEquals(cc(Operator.Greater, min4Max8, MIN), min4Max8.greater(MIN));
		assertEquals(cc(Operator.Greater, min4Max8, MAX), min4Max8.greater(MAX));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 0), min4Max8.greaterOrEqual(0));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 2), min4Max8.greaterOrEqual(2));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 3), min4Max8.greaterOrEqual(3));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 4), min4Max8.greaterOrEqual(4));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 6), min4Max8.greaterOrEqual(6));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 7), min4Max8.greaterOrEqual(7));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 8), min4Max8.greaterOrEqual(8));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, 9), min4Max8.greaterOrEqual(9));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, MIN), min4Max8.greaterOrEqual(MIN));
		assertEquals(cc(Operator.GreaterEqual, min4Max8, MAX), min4Max8.greaterOrEqual(MAX));
	}

	@Test void testOptional()
	{
		final IntegerField orig = new IntegerField().optional();
		assertEquals(false, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertEquals(MIN, orig.getMinimum());
		assertEquals(MAX, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertEquals(MIN, copy.getMinimum());
		assertEquals(MAX, copy.getMaximum());
	}

	@Test void testMin()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().min(10);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNull(orig.getImplicitUniqueConstraint());
		assertEquals(10, orig.getMinimum());
		assertEquals(MAX, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNull(copy.getImplicitUniqueConstraint());
		assertEquals(10, copy.getMinimum());
		assertEquals(MAX, copy.getMaximum());
	}

	@Test void testUnique()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().unique().min(20);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNotNull(orig.getImplicitUniqueConstraint());
		assertEquals(20, orig.getMinimum());
		assertEquals(MAX, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNotNull(copy.getImplicitUniqueConstraint());
		assertEquals(20, copy.getMinimum());
		assertEquals(MAX, copy.getMaximum());
	}

	@Test void testMax()
	{
		final IntegerField orig = new IntegerField().toFinal().optional().max(30);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNull(orig.getImplicitUniqueConstraint());
		assertEquals(MIN, orig.getMinimum());
		assertEquals(30, orig.getMaximum());

		final IntegerField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNull(copy.getImplicitUniqueConstraint());
		assertEquals(MIN, copy.getMinimum());
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
		assertIllegalRange(0,  0,  "Redundant field with minimum==maximum (0) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22, 22, "Redundant field with minimum==maximum (22) is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(22, 21, "maximum must be at least minimum, but was 21 and 22");
		assertIllegalRange(MAX, MIN, "maximum must be at least minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "Redundant field with minimum==maximum (" + MIN + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
		assertIllegalRange(MAX, MAX, "Redundant field with minimum==maximum (" + MAX + ") is probably a mistake. You may call method rangeEvenIfRedundant if you are sure this is ok.");
	}

	private static void assertIllegalRange(final int minimum, final int maximum, final String message)
	{
		final IntegerField f = new IntegerField().optional();
		assertFails(
				() -> f.range(minimum, maximum),
				IllegalArgumentException.class,
				message);
	}

	@Test void testRangeShortcutEqual()
	{
		final IntegerField f = new IntegerField().optional().range(-3, 5);
		assertEquals(f+" is null", f.equal((Integer)null).toString());
		assertEquals("FALSE", f.equal(-4).toString());
		assertEquals(f+"='-3'", f.equal(-3).toString());
		assertEquals(f+"='5'", f.equal(5).toString());
		assertEquals("FALSE", f.equal(6).toString());
		assertEquals("FALSE", f.equal(MIN).toString());
		assertEquals("FALSE", f.equal(MAX).toString());

		final NumberFunction<Integer> b = f.bind(AnItem.TYPE.newQuery().join(AnItem.TYPE, (Condition)null));
		assertEquals("a1."+f+" is null", b.equal((Integer)null).toString());
		assertEquals("a1."+f+"='-4'", b.equal(-4).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='-3'", b.equal(-3).toString());
		assertEquals("a1."+f+"='5'", b.equal(5).toString());
		assertEquals("a1."+f+"='6'", b.equal(6).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='"+MIN+"'", b.equal(MIN).toString()); // TODO should be "FALSE"
		assertEquals("a1."+f+"='"+MAX+"'", b.equal(MAX).toString()); // TODO should be "FALSE"
	}

	@Test void testRangeShortcutNotEqual()
	{
		final IntegerField f = new IntegerField().optional().range(-3, 5);
		assertEquals(f+" is not null", f.notEqual((Integer)null).toString());
		assertEquals("TRUE",  f.notEqual(-4).toString());
		assertEquals(f+"<>'-3'", f.notEqual(-3).toString());
		assertEquals(f+"<>'5'", f.notEqual(5).toString());
		assertEquals("TRUE", f.notEqual(6).toString());
		assertEquals("TRUE", f.notEqual(MIN).toString());
		assertEquals("TRUE", f.notEqual(MAX).toString());

		final NumberFunction<Integer> b = f.bind(AnItem.TYPE.newQuery().join(AnItem.TYPE, (Condition)null));
		assertEquals("a1."+f+" is not null", b.notEqual((Integer)null).toString());
		assertEquals("a1."+f+"<>'-4'", b.notEqual(-4).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'-3'", b.notEqual(-3).toString());
		assertEquals("a1."+f+"<>'5'", b.notEqual(5).toString());
		assertEquals("a1."+f+"<>'6'", b.notEqual(6).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'"+MIN+"'", b.notEqual(MIN).toString()); // TODO should be "TRUE"
		assertEquals("a1."+f+"<>'"+MAX+"'", b.notEqual(MAX).toString()); // TODO should be "TRUE"
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

	private static final int MAX = Integer.MAX_VALUE;
	private static final int MIN = Integer.MIN_VALUE;

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused") // OK: just for initializing teh model
	private static final Model model = new Model(AnItem.TYPE);
}

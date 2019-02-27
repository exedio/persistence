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

import static com.exedio.cope.DoubleItem.TYPE;
import static com.exedio.cope.DoubleItem.any;
import static com.exedio.cope.DoubleItem.mandatory;
import static com.exedio.cope.DoubleItem.max4;
import static com.exedio.cope.DoubleItem.min4;
import static com.exedio.cope.DoubleItem.min4Max8;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Double.valueOf;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CompareFunctionCondition.Operator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DoubleTest extends TestWithEnvironment
{
	public/*for web.xml*/ static final Model MODEL = new Model(TYPE);

	public DoubleTest()
	{
		super(MODEL);
	}

	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;

	private static final Condition TRUE  = Condition.TRUE;
	private static final Condition FALSE = Condition.FALSE;

	private DoubleItem item;
	private int numberOfItems;

	@BeforeEach final void setUp()
	{
		item = new DoubleItem(2201.01);
		new DoubleItem(2202.02);
		numberOfItems = 2;
	}

	@Test void testIt()
	{
		// test model
		assertEquals(TYPE, any.getType());
		assertEquals("any", any.getName());
		assertEquals(false, any.isMandatory());
		assertEquals(null, any.getPattern());
		assertEqualBits(MIN, any.getMinimum());
		assertEqualBits(MAX, any.getMaximum());
		assertContains(any.getInitialExceptions());

		assertEquals(TYPE, mandatory.getType());
		assertEquals("mandatory", mandatory.getName());
		assertEquals(true, mandatory.isMandatory());
		assertEqualBits(MIN, mandatory.getMinimum());
		assertEqualBits(MAX, mandatory.getMaximum());
		assertContains(MandatoryViolationException.class, mandatory.getInitialExceptions());

		assertEquals(false, min4.isMandatory());
		assertEqualBits(4.0, min4.getMinimum());
		assertEqualBits(MAX, min4.getMaximum());
		assertContains(DoubleRangeViolationException.class, min4.getInitialExceptions());

		assertEquals(false, max4.isMandatory());
		assertEqualBits(MIN, max4.getMinimum());
		assertEqualBits(4.0, max4.getMaximum());
		assertContains(DoubleRangeViolationException.class, max4.getInitialExceptions());

		assertEquals(false, min4Max8.isMandatory());
		assertEqualBits(4.0, min4Max8.getMinimum());
		assertEqualBits(8.0, min4Max8.getMaximum());
		assertContains(DoubleRangeViolationException.class, min4Max8.getInitialExceptions());

		// test condition canonization
		{
			assertEqualsAndHash(in(any), any.isNull());
			assertEqualsAndHash(nn(any), any.isNotNull());
			assertEqualsAndHash(in(any), any.equal((Double)null));
			assertEqualsAndHash(nn(any), any.notEqual((Double)null));
			assertEqualsAndHash(cc(Operator.Equal, any, 0.0), any.equal(0.0));
			assertEqualsAndHash(cc(Operator.Equal, any, MIN), any.equal(MIN));
			assertEqualsAndHash(cc(Operator.Equal, any, MAX), any.equal(MAX));
			assertEqualsAndHash(cc(Operator.NotEqual, any, 0.0), any.notEqual(0.0));
			assertEqualsAndHash(cc(Operator.NotEqual, any, MIN), any.notEqual(MIN));
			assertEqualsAndHash(cc(Operator.NotEqual, any, MAX), any.notEqual(MAX));
			assertEqualsAndHash(cc(Operator.Less, any, 0.0), any.less(0.0));
			assertEqualsAndHash(cc(Operator.Less, any, MIN), any.less(MIN));
			assertEqualsAndHash(cc(Operator.Less, any, MAX), any.less(MAX));
			assertEqualsAndHash(cc(Operator.LessEqual, any, 0.0), any.lessOrEqual(0.0));
			assertEqualsAndHash(cc(Operator.LessEqual, any, MIN), any.lessOrEqual(MIN));
			assertEqualsAndHash(cc(Operator.LessEqual, any, MAX), any.lessOrEqual(MAX));
			assertEqualsAndHash(cc(Operator.Greater, any, 0.0), any.greater(0.0));
			assertEqualsAndHash(cc(Operator.Greater, any, MIN), any.greater(MIN));
			assertEqualsAndHash(cc(Operator.Greater, any, MAX), any.greater(MAX));
			assertEqualsAndHash(cc(Operator.GreaterEqual, any, 0.0), any.greaterOrEqual(0.0));
			assertEqualsAndHash(cc(Operator.GreaterEqual, any, MIN), any.greaterOrEqual(MIN));
			assertEqualsAndHash(cc(Operator.GreaterEqual, any, MAX), any.greaterOrEqual(MAX));

			assertEqualsAndHash(in(mandatory), mandatory.isNull());
			assertEqualsAndHash(nn(mandatory), mandatory.isNotNull());
			assertEqualsAndHash(in(mandatory), mandatory.equal((Double)null));
			assertEqualsAndHash(nn(mandatory), mandatory.notEqual((Double)null));
			assertEqualsAndHash(cc(Operator.Equal, mandatory, 0.0), mandatory.equal(0.0));
			assertEqualsAndHash(cc(Operator.Equal, mandatory, MIN), mandatory.equal(MIN));
			assertEqualsAndHash(cc(Operator.Equal, mandatory, MAX), mandatory.equal(MAX));

			assertEqualsAndHash(in(min4), min4.equal((Double)null));
			assertSame         (FALSE,                         min4.equal(0.0));
			assertSame         (FALSE,                         min4.equal(3.9));
			assertEqualsAndHash(cc(Operator.Equal, min4, 4.0), min4.equal(4.0));
			assertSame         (FALSE,                         min4.equal(MIN));
			assertEqualsAndHash(cc(Operator.Equal, min4, MAX), min4.equal(MAX));

			assertEqualsAndHash(in(max4), max4.equal((Double)null));
			assertEqualsAndHash(cc(Operator.Equal, max4, 0.0), max4.equal(0.0));
			assertEqualsAndHash(cc(Operator.Equal, max4, 3.9), max4.equal(3.9));
			assertEqualsAndHash(cc(Operator.Equal, max4, 4.0), max4.equal(4.0));
			assertSame         (FALSE,                         max4.equal(4.1));
			assertEqualsAndHash(cc(Operator.Equal, max4, MIN), max4.equal(MIN));
			assertSame         (FALSE,                         max4.equal(MAX));

			assertEqualsAndHash(in(min4Max8), min4Max8.isNull());
			assertEqualsAndHash(nn(min4Max8), min4Max8.isNotNull());
			assertEqualsAndHash(in(min4Max8), min4Max8.equal((Double)null));
			assertEqualsAndHash(nn(min4Max8), min4Max8.notEqual((Double)null));
			assertSame         (FALSE,                             min4Max8.equal(0.0));
			assertSame         (FALSE,                             min4Max8.equal(3.9));
			assertEqualsAndHash(cc(Operator.Equal, min4Max8, 4.0), min4Max8.equal(4.0));
			assertEqualsAndHash(cc(Operator.Equal, min4Max8, 8.0), min4Max8.equal(8.0));
			assertSame         (FALSE,                             min4Max8.equal(8.1));
			assertSame         (FALSE,                             min4Max8.equal(MIN));
			assertSame         (FALSE,                             min4Max8.equal(MAX));
			assertSame         (TRUE,                                 min4Max8.notEqual(0.0));
			assertSame         (TRUE,                                 min4Max8.notEqual(3.9));
			assertEqualsAndHash(cc(Operator.NotEqual, min4Max8, 4.0), min4Max8.notEqual(4.0));
			assertEqualsAndHash(cc(Operator.NotEqual, min4Max8, 8.0), min4Max8.notEqual(8.0));
			assertSame         (TRUE,                                 min4Max8.notEqual(8.1));
			assertSame         (TRUE,                                 min4Max8.notEqual(MIN));
			assertSame         (TRUE,                                 min4Max8.notEqual(MAX));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, 0.0), min4Max8.less(0.0));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, 3.9), min4Max8.less(3.9));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, 4.0), min4Max8.less(4.0));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, 5.0), min4Max8.less(5.0));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, MIN), min4Max8.less(MIN));
			assertEqualsAndHash(cc(Operator.Less, min4Max8, MAX), min4Max8.less(MAX));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 0.0), min4Max8.lessOrEqual(0.0));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 3.3), min4Max8.lessOrEqual(3.3));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 4.4), min4Max8.lessOrEqual(4.4));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 5.5), min4Max8.lessOrEqual(5.5));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 8.8), min4Max8.lessOrEqual(8.8));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, 9.9), min4Max8.lessOrEqual(9.9));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8,10.0), min4Max8.lessOrEqual(10.0));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, MIN), min4Max8.lessOrEqual(MIN));
			assertEqualsAndHash(cc(Operator.LessEqual, min4Max8, MAX), min4Max8.lessOrEqual(MAX));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 0.0), min4Max8.greater(0.0));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 2.2), min4Max8.greater(2.2));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 3.3), min4Max8.greater(3.3));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 4.4), min4Max8.greater(4.4));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 6.6), min4Max8.greater(6.6));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 7.7), min4Max8.greater(7.7));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 8.8), min4Max8.greater(8.8));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, 9.9), min4Max8.greater(9.9));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, MIN), min4Max8.greater(MIN));
			assertEqualsAndHash(cc(Operator.Greater, min4Max8, MAX), min4Max8.greater(MAX));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 0.0), min4Max8.greaterOrEqual(0.0));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 2.2), min4Max8.greaterOrEqual(2.2));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 3.3), min4Max8.greaterOrEqual(3.3));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 4.4), min4Max8.greaterOrEqual(4.4));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 6.6), min4Max8.greaterOrEqual(6.6));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 7.7), min4Max8.greaterOrEqual(7.7));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 8.8), min4Max8.greaterOrEqual(8.8));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, 9.9), min4Max8.greaterOrEqual(9.9));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, MIN), min4Max8.greaterOrEqual(MIN));
			assertEqualsAndHash(cc(Operator.GreaterEqual, min4Max8, MAX), min4Max8.greaterOrEqual(MAX));
		}

		{
			final DoubleField orig = new DoubleField().optional();
			assertEquals(false, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertEqualBits(MIN, orig.getMinimum());
			assertEqualBits(MAX, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertEqualBits(MIN, copy.getMinimum());
			assertEqualBits(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().min(10.1);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEqualBits(10.1, orig.getMinimum());
			assertEqualBits(MAX, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEqualBits(10.1, copy.getMinimum());
			assertEqualBits(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().unique().min(20.2);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNotNull(orig.getImplicitUniqueConstraint());
			assertEqualBits(20.2, orig.getMinimum());
			assertEqualBits(MAX, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNotNull(copy.getImplicitUniqueConstraint());
			assertEqualBits(20.2, copy.getMinimum());
			assertEqualBits(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().max(30.3);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEqualBits(MIN, orig.getMinimum());
			assertEqualBits(30.3, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEqualBits(MIN, copy.getMinimum());
			assertEqualBits(30.3, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().range(10.1, 20.2);
			assertEquals(false, orig.isFinal());
			assertEquals(true, orig.isMandatory());
			assertEqualBits(10.1, orig.getMinimum());
			assertEqualBits(20.2, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(true, copy.isMandatory());
			assertEqualBits(10.1, copy.getMinimum());
			assertEqualBits(20.2, copy.getMaximum());
		}

		assertIllegalRange(POSITIVE_INFINITY, 44.22, "minimum must not be infinite, but was Infinity");
		assertIllegalRange(44.22, POSITIVE_INFINITY, "maximum must not be infinite, but was Infinity");
		assertIllegalRange(NEGATIVE_INFINITY, 44.22, "minimum must not be infinite, but was -Infinity");
		assertIllegalRange(44.22, NEGATIVE_INFINITY, "maximum must not be infinite, but was -Infinity");
		assertIllegalRange(NaN, 44.22, "minimum must not be NaN, but was NaN");
		assertIllegalRange(44.22, NaN, "maximum must not be NaN, but was NaN");
		assertIllegalRange( 0.0,  0.0,  "maximum must be greater than minimum, but was 0.0 and 0.0");
		assertIllegalRange(22.2, 22.2, "maximum must be greater than minimum, but was 22.2 and 22.2");
		assertIllegalRange(22.2, 21.1, "maximum must be greater than minimum, but was 21.1 and 22.2");
		assertIllegalRange(MAX, MIN, "maximum must be greater than minimum, but was " + MIN + " and " + MAX);
		assertIllegalRange(MIN, MIN, "maximum must be greater than minimum, but was " + MIN + " and " + MIN);
		assertIllegalRange(MAX, MAX, "maximum must be greater than minimum, but was " + MAX + " and " + MAX);

		// test conditions
		assertEqualsAndHash(any.equal(1.1), any.equal(1.1));
		assertNotEqualsAndHash(
				any.equal(1.1),
				any.equal(2.2),
				any.equal((Double)null),
				any.greater(1.1));
		assertEqualsAndHash(any.equal(mandatory), any.equal(mandatory));
		assertNotEqualsAndHash(any.equal(mandatory), any.equal(any));

		// any
		item.setAny(1234.56);
		assertEquals(valueOf(1234.56), item.getAny());
		item.setAny(123.45);
		assertEquals(valueOf(123.45), item.getAny());

		// mandatory
		assertEqualBits(2201.01, item.getMandatory());

		item.setMandatory(52201.52);
		assertEqualBits(52201.52, item.getMandatory());

		try
		{
			mandatory.set(item, null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation on " + item + " for " + mandatory, e.getMessage());
		}
		assertEqualBits(52201.52, item.getMandatory());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new DoubleItem((Double)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new DoubleItem(new SetValue<?>[]{});
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(mandatory, e.getFeature());
			assertEquals("mandatory violation for " + mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		try
		{
			item.setMandatory(NaN);
			fail();
		}
		catch(final DoubleNaNException e)
		{
			assertEquals(item, e.getItem());
			assertSame(mandatory, e.getFeature());
			assertEquals(
					"Not a Number (NaN) on " + item + " for " + mandatory,
					e.getMessage());
		}
		try
		{
			new DoubleItem(NaN);
			fail();
		}
		catch(final DoubleNaNException e)
		{
			assertEquals(null, e.getItem());
			assertSame(mandatory, e.getFeature());
			assertEquals(
					"Not a Number (NaN) for " + mandatory,
					e.getMessage());
		}

		try
		{
			item.setMandatory(POSITIVE_INFINITY);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(mandatory, e.getFeature());
			assertEqualBits(POSITIVE_INFINITY, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"Infinity is too big for " + mandatory + ", " +
					"must be at most " + MAX + ".",
					e.getMessage());
		}

		try
		{
			item.setMandatory(NEGATIVE_INFINITY);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(mandatory, e.getFeature());
			assertEqualBits(NEGATIVE_INFINITY, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"-Infinity is too small for " + mandatory + ", " +
					"must be at least " + MIN + ".",
					e.getMessage());
		}

		// min4
		try
		{
			item.setMin4(3.9);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4, e.getFeature());
			assertSame(min4, e.getFeature());
			assertEqualBits(3.9, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3.9 is too small for " + min4 + ", " +
					"must be at least 4.0.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
		assertEquals(null, item.getMin4());

		item.setMin4(4.0);
		assertEquals(valueOf(4.0), item.getMin4());

		// max4
		item.setMax4(4.0);
		assertEquals(valueOf(4.0), item.getMax4());
		try
		{
			item.setMax4(4.1);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEqualBits(4.1, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"4.1 is too big for " + max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(valueOf(4.0), item.getMax4());
		restartTransaction();
		assertEquals(valueOf(4.0), item.getMax4());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new DoubleItem(4.1, (Date)null);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEqualBits(4.1, e.getValue());
			assertEquals(
					"range violation, " +
					"4.1 is too big for " + max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			TYPE.newItem(
					mandatory.map(12345.67),
					max4.map(4.1)
			);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEqualBits(4.1, e.getValue());
			assertEquals(
					"range violation, " +
					"4.1 is too big for " + max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8(3.9);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEqualBits(3.9, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3.9 is too small for " + min4Max8 + ", " +
					"must be at least 4.0.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8(4.0);
		assertEquals(valueOf(4.0), item.getMin4Max8());

		item.setMin4Max8(8.0);
		assertEquals(valueOf(8.0), item.getMin4Max8());

		restartTransaction();
		assertEquals(valueOf(8.0), item.getMin4Max8());

		try
		{
			item.setMin4Max8(8.1);
			fail();
		}
		catch(final DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEqualBits(8.1, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"8.1 is too big for " + min4Max8 + ", " +
					"must be at most 8.0.",
					e.getMessage());
		}
		assertEquals(valueOf(8.0), item.getMin4Max8());
		restartTransaction();
		assertEquals(valueOf(8.0), item.getMin4Max8());

		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)any, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Double.class.getName() + ", but was a " + String.class.getName() + " for " + any + '.', e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	void assertIllegalRange(final double minimum, final double maximum, final String message)
	{
		final DoubleField f = new DoubleField().optional();
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

	private static IsNullCondition<Double> in(
			final DoubleField field)
	{
		return new IsNullCondition<>(field, false);
	}

	private static IsNullCondition<Double> nn(
			final DoubleField field)
	{
		return new IsNullCondition<>(field, true);
	}

	private static CompareCondition<Double> cc(
			final Operator operator,
			final DoubleField field,
			final Double value)
	{
		return new CompareCondition<>(operator, (Selectable<Double>)field, value);
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}

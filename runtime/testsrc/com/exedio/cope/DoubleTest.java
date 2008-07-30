/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Date;

import com.exedio.cope.CompareFunctionCondition.Operator;

public class DoubleTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(DoubleItem.TYPE);

	public DoubleTest()
	{
		super(MODEL);
	}
	
	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;
	
	private static final Condition TRUE  = Condition.TRUE;
	private static final Condition FALSE = Condition.FALSE;
	
	private DoubleItem item;
	@SuppressWarnings("unused") // OK: is an item not to be found by searches
	private DoubleItem item2;
	private int numberOfItems;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DoubleItem(2201.01));
		item2 = deleteOnTearDown(new DoubleItem(2202.02));
		numberOfItems = 2;
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.any.getType());
		assertEquals("any", item.any.getName());
		assertEquals(false, item.any.isMandatory());
		assertEqualsUnmodifiable(list(), item.any.getPatterns());
		assertEquals(MIN, item.any.getMinimum());
		assertEquals(MAX, item.any.getMaximum());
		assertContains(item.any.getInitialExceptions());

		assertEquals(item.TYPE, item.mandatory.getType());
		assertEquals("mandatory", item.mandatory.getName());
		assertEquals(true, item.mandatory.isMandatory());
		assertEquals(MIN, item.mandatory.getMinimum());
		assertEquals(MAX, item.mandatory.getMaximum());
		assertContains(MandatoryViolationException.class, item.mandatory.getInitialExceptions());

		assertEquals(false, item.min4.isMandatory());
		assertEquals(4.0, item.min4.getMinimum());
		assertEquals(MAX, item.min4.getMaximum());
		assertContains(DoubleRangeViolationException.class, item.min4.getInitialExceptions());

		assertEquals(false, item.max4.isMandatory());
		assertEquals(MIN, item.max4.getMinimum());
		assertEquals(4.0, item.max4.getMaximum());
		assertContains(DoubleRangeViolationException.class, item.max4.getInitialExceptions());

		assertEquals(false, item.min4Max8.isMandatory());
		assertEquals(4.0, item.min4Max8.getMinimum());
		assertEquals(8.0, item.min4Max8.getMaximum());
		assertContains(DoubleRangeViolationException.class, item.min4Max8.getInitialExceptions());
		
		// test condition canonization
		{
			assertEquals(in(item.any), item.any.isNull());
			assertEquals(nn(item.any), item.any.isNotNull());
			assertEquals(in(item.any), item.any.equal((Double)null));
			assertEquals(nn(item.any), item.any.notEqual((Double)null));
			assertEquals(cc(Operator.Equal, item.any, 0.0), item.any.equal(0.0));
			assertEquals(cc(Operator.Equal, item.any, MIN), item.any.equal(MIN));
			assertEquals(cc(Operator.Equal, item.any, MAX), item.any.equal(MAX));
			assertEquals(cc(Operator.NotEqual, item.any, 0.0), item.any.notEqual(0.0));
			assertEquals(cc(Operator.NotEqual, item.any, MIN), item.any.notEqual(MIN));
			assertEquals(cc(Operator.NotEqual, item.any, MAX), item.any.notEqual(MAX));
			assertEquals(cc(Operator.Less, item.any, 0.0), item.any.less(0.0));
			assertEquals(cc(Operator.Less, item.any, MIN), item.any.less(MIN));
			assertEquals(cc(Operator.Less, item.any, MAX), item.any.less(MAX));
			assertEquals(cc(Operator.LessEqual, item.any, 0.0), item.any.lessOrEqual(0.0));
			assertEquals(cc(Operator.LessEqual, item.any, MIN), item.any.lessOrEqual(MIN));
			assertEquals(cc(Operator.LessEqual, item.any, MAX), item.any.lessOrEqual(MAX));
			assertEquals(cc(Operator.Greater, item.any, 0.0), item.any.greater(0.0));
			assertEquals(cc(Operator.Greater, item.any, MIN), item.any.greater(MIN));
			assertEquals(cc(Operator.Greater, item.any, MAX), item.any.greater(MAX));
			assertEquals(cc(Operator.GreaterEqual, item.any, 0.0), item.any.greaterOrEqual(0.0));
			assertEquals(cc(Operator.GreaterEqual, item.any, MIN), item.any.greaterOrEqual(MIN));
			assertEquals(cc(Operator.GreaterEqual, item.any, MAX), item.any.greaterOrEqual(MAX));
			
			assertEquals(in(item.mandatory), item.mandatory.isNull());
			assertEquals(nn(item.mandatory), item.mandatory.isNotNull());
			assertEquals(in(item.mandatory), item.mandatory.equal((Double)null));
			assertEquals(nn(item.mandatory), item.mandatory.notEqual((Double)null));
			assertEquals(cc(Operator.Equal, item.mandatory, 0.0), item.mandatory.equal(0.0));
			assertEquals(cc(Operator.Equal, item.mandatory, MIN), item.mandatory.equal(MIN));
			assertEquals(cc(Operator.Equal, item.mandatory, MAX), item.mandatory.equal(MAX));

			assertEquals(in(item.min4), item.min4.equal((Double)null));
			assertEquals(FALSE,                              item.min4.equal(0.0));
			assertEquals(FALSE,                              item.min4.equal(3.9));
			assertEquals(cc(Operator.Equal, item.min4, 4.0), item.min4.equal(4.0));
			assertEquals(FALSE,                              item.min4.equal(MIN));
			assertEquals(cc(Operator.Equal, item.min4, MAX), item.min4.equal(MAX));

			assertEquals(in(item.max4), item.max4.equal((Double)null));
			assertEquals(cc(Operator.Equal, item.max4, 0.0), item.max4.equal(0.0));
			assertEquals(cc(Operator.Equal, item.max4, 3.9), item.max4.equal(3.9));
			assertEquals(cc(Operator.Equal, item.max4, 4.0), item.max4.equal(4.0));
			assertEquals(FALSE,                              item.max4.equal(4.1));
			assertEquals(cc(Operator.Equal, item.max4, MIN), item.max4.equal(MIN));
			assertEquals(FALSE,                              item.max4.equal(MAX));

			assertEquals(in(item.min4Max8), item.min4Max8.isNull());
			assertEquals(nn(item.min4Max8), item.min4Max8.isNotNull());
			assertEquals(in(item.min4Max8), item.min4Max8.equal((Double)null));
			assertEquals(nn(item.min4Max8), item.min4Max8.notEqual((Double)null));
			assertEquals(FALSE,                                  item.min4Max8.equal(0.0));
			assertEquals(FALSE,                                  item.min4Max8.equal(3.9));
			assertEquals(cc(Operator.Equal, item.min4Max8, 4.0), item.min4Max8.equal(4.0));
			assertEquals(cc(Operator.Equal, item.min4Max8, 8.0), item.min4Max8.equal(8.0));
			assertEquals(FALSE,                                  item.min4Max8.equal(8.1));
			assertEquals(FALSE,                                  item.min4Max8.equal(MIN));
			assertEquals(FALSE,                                  item.min4Max8.equal(MAX));
			assertEquals(TRUE,                                      item.min4Max8.notEqual(0.0));
			assertEquals(TRUE,                                      item.min4Max8.notEqual(3.9));
			assertEquals(cc(Operator.NotEqual, item.min4Max8, 4.0), item.min4Max8.notEqual(4.0));
			assertEquals(cc(Operator.NotEqual, item.min4Max8, 8.0), item.min4Max8.notEqual(8.0));
			assertEquals(TRUE,                                      item.min4Max8.notEqual(8.1));
			assertEquals(TRUE,                                      item.min4Max8.notEqual(MIN));
			assertEquals(TRUE,                                      item.min4Max8.notEqual(MAX));
			assertEquals(cc(Operator.Less, item.min4Max8, 0.0), item.min4Max8.less(0.0));
			assertEquals(cc(Operator.Less, item.min4Max8, 3.9), item.min4Max8.less(3.9));
			assertEquals(cc(Operator.Less, item.min4Max8, 4.0), item.min4Max8.less(4.0));
			assertEquals(cc(Operator.Less, item.min4Max8, 5.0), item.min4Max8.less(5.0));
			assertEquals(cc(Operator.Less, item.min4Max8, MIN), item.min4Max8.less(MIN));
			assertEquals(cc(Operator.Less, item.min4Max8, MAX), item.min4Max8.less(MAX));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 0.0), item.min4Max8.lessOrEqual(0.0));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 3.3), item.min4Max8.lessOrEqual(3.3));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 4.4), item.min4Max8.lessOrEqual(4.4));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 5.5), item.min4Max8.lessOrEqual(5.5));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 8.8), item.min4Max8.lessOrEqual(8.8));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, 9.9), item.min4Max8.lessOrEqual(9.9));
			assertEquals(cc(Operator.LessEqual, item.min4Max8,10.0), item.min4Max8.lessOrEqual(10.0));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, MIN), item.min4Max8.lessOrEqual(MIN));
			assertEquals(cc(Operator.LessEqual, item.min4Max8, MAX), item.min4Max8.lessOrEqual(MAX));
			assertEquals(cc(Operator.Greater, item.min4Max8, 0.0), item.min4Max8.greater(0.0));
			assertEquals(cc(Operator.Greater, item.min4Max8, 2.2), item.min4Max8.greater(2.2));
			assertEquals(cc(Operator.Greater, item.min4Max8, 3.3), item.min4Max8.greater(3.3));
			assertEquals(cc(Operator.Greater, item.min4Max8, 4.4), item.min4Max8.greater(4.4));
			assertEquals(cc(Operator.Greater, item.min4Max8, 6.6), item.min4Max8.greater(6.6));
			assertEquals(cc(Operator.Greater, item.min4Max8, 7.7), item.min4Max8.greater(7.7));
			assertEquals(cc(Operator.Greater, item.min4Max8, 8.8), item.min4Max8.greater(8.8));
			assertEquals(cc(Operator.Greater, item.min4Max8, 9.9), item.min4Max8.greater(9.9));
			assertEquals(cc(Operator.Greater, item.min4Max8, MIN), item.min4Max8.greater(MIN));
			assertEquals(cc(Operator.Greater, item.min4Max8, MAX), item.min4Max8.greater(MAX));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 0.0), item.min4Max8.greaterOrEqual(0.0));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 2.2), item.min4Max8.greaterOrEqual(2.2));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 3.3), item.min4Max8.greaterOrEqual(3.3));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 4.4), item.min4Max8.greaterOrEqual(4.4));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 6.6), item.min4Max8.greaterOrEqual(6.6));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 7.7), item.min4Max8.greaterOrEqual(7.7));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 8.8), item.min4Max8.greaterOrEqual(8.8));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, 9.9), item.min4Max8.greaterOrEqual(9.9));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, MIN), item.min4Max8.greaterOrEqual(MIN));
			assertEquals(cc(Operator.GreaterEqual, item.min4Max8, MAX), item.min4Max8.greaterOrEqual(MAX));
		}

		{
			final DoubleField orig = new DoubleField().optional();
			assertEquals(false, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertEquals(MIN, orig.getMinimum());
			assertEquals(MAX, orig.getMaximum());

			final DoubleField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertEquals(MIN, copy.getMinimum());
			assertEquals(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().min(10.1);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEquals(10.1, orig.getMinimum());
			assertEquals(MAX, orig.getMaximum());
			
			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEquals(10.1, copy.getMinimum());
			assertEquals(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().unique().min(20.2);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNotNull(orig.getImplicitUniqueConstraint());
			assertEquals(20.2, orig.getMinimum());
			assertEquals(MAX, orig.getMaximum());
			
			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNotNull(copy.getImplicitUniqueConstraint());
			assertEquals(20.2, copy.getMinimum());
			assertEquals(MAX, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().toFinal().optional().max(30.3);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEquals(MIN, orig.getMinimum());
			assertEquals(30.3, orig.getMaximum());
			
			final DoubleField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEquals(MIN, copy.getMinimum());
			assertEquals(30.3, copy.getMaximum());
		}
		{
			final DoubleField orig = new DoubleField().range(10.1, 20.2);
			assertEquals(false, orig.isFinal());
			assertEquals(true, orig.isMandatory());
			assertEquals(10.1, orig.getMinimum());
			assertEquals(20.2, orig.getMaximum());
			
			final DoubleField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(true, copy.isMandatory());
			assertEquals(10.1, copy.getMinimum());
			assertEquals(20.2, copy.getMaximum());
		}
		
		assertIllegalRange(Double.POSITIVE_INFINITY, 44.22, "minimum must not be infinite, but was Infinity.");
		assertIllegalRange(44.22, Double.POSITIVE_INFINITY, "maximum must not be infinite, but was Infinity.");
		assertIllegalRange(Double.NEGATIVE_INFINITY, 44.22, "minimum must not be infinite, but was -Infinity.");
		assertIllegalRange(44.22, Double.NEGATIVE_INFINITY, "maximum must not be infinite, but was -Infinity.");
		assertIllegalRange(Double.NaN, 44.22, "minimum must not be NaN, but was NaN.");
		assertIllegalRange(44.22, Double.NaN, "maximum must not be NaN, but was NaN.");
		assertIllegalRange( 0.0,  0.0,  "maximum must be greater than mimimum, but was 0.0 and 0.0.");
		assertIllegalRange(22.2, 22.2, "maximum must be greater than mimimum, but was 22.2 and 22.2.");
		assertIllegalRange(22.2, 21.1, "maximum must be greater than mimimum, but was 21.1 and 22.2.");
		assertIllegalRange(MAX, MIN, "maximum must be greater than mimimum, but was " + MIN + " and " + MAX + ".");
		assertIllegalRange(MIN, MIN, "maximum must be greater than mimimum, but was " + MIN + " and " + MIN + ".");
		assertIllegalRange(MAX, MAX, "maximum must be greater than mimimum, but was " + MAX + " and " + MAX + ".");

		// test conditions
		assertEquals(item.any.equal(1.1), item.any.equal(1.1));
		assertNotEquals(item.any.equal(1.1), item.any.equal(2.2));
		assertNotEquals(item.any.equal(1.1), item.any.equal((Double)null));
		assertNotEquals(item.any.equal(1.1), item.any.greater(1.1));
		assertEquals(item.any.equal(item.mandatory), item.any.equal(item.mandatory));
		assertNotEquals(item.any.equal(item.mandatory), item.any.equal(item.any));

		// any
		item.setAny(1234.56);
		assertEquals(new Double(1234.56), item.getAny());
		item.setAny(123.45);
		assertEquals(new Double(123.45), item.getAny());
		
		// mandatory
		assertEquals(2201.01, item.getMandatory());
	
		item.setMandatory(52201.52);
		assertEquals(52201.52, item.getMandatory());
	
		try
		{
			item.mandatory.set(item, null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on " + item + " for " + item.mandatory, e.getMessage());
		}
		assertEquals(52201.52, item.getMandatory());
	
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new DoubleItem((Double)null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new DoubleItem(new SetValue[]{});
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals(item.mandatory, e.getFeature());
			assertEquals("mandatory violation on a newly created item for " + item.mandatory, e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		
		// min4
		try
		{
			item.setMin4(3.9);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4, e.getFeature());
			assertEquals(item.min4, e.getFeature());
			assertEquals(3.9, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3.9 is too small for " + item.min4 + ", " +
					"must be at least 4.0.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
		assertEquals(null, item.getMin4());

		item.setMin4(4.0);
		assertEquals(new Double(4.0), item.getMin4());

		// max4
		item.setMax4(4.0);
		assertEquals(new Double(4.0), item.getMax4());
		try
		{
			item.setMax4(4.1);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(4.1, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"4.1 is too big for " + item.max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(new Double(4.0), item.getMax4());
		restartTransaction();
		assertEquals(new Double(4.0), item.getMax4());

		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new DoubleItem(4.1, (Date)null);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(4.1, e.getValue());
			assertEquals(
					"range violation on a newly created item, " +
					"4.1 is too big for " + item.max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			DoubleItem.TYPE.newItem(
					item.mandatory.map(12345.67),
					item.max4.map(4.1)
			);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(4.1, e.getValue());
			assertEquals(
					"range violation on a newly created item, " +
					"4.1 is too big for " + item.max4 + ", " +
					"must be at most 4.0.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8(3.9);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(3.9, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3.9 is too small for " + item.min4Max8 + ", " +
					"must be at least 4.0.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8(4.0);
		assertEquals(new Double(4.0), item.getMin4Max8());

		item.setMin4Max8(8.0);
		assertEquals(new Double(8.0), item.getMin4Max8());

		restartTransaction();
		assertEquals(new Double(8.0), item.getMin4Max8());

		try
		{
			item.setMin4Max8(8.1);
			fail();
		}
		catch(DoubleRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(8.1, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"8.1 is too big for " + item.min4Max8 + ", " +
					"must be at most 8.0.",
					e.getMessage());
		}
		assertEquals(new Double(8.0), item.getMin4Max8());
		restartTransaction();
		assertEquals(new Double(8.0), item.getMin4Max8());
		
		
		model.checkUnsupportedConstraints();
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.any, "hallo");
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Double.class.getName() + ", but was a " + String.class.getName() + " for " + item.any + '.', e.getMessage());
		}
	}
	
	void assertIllegalRange(final double minimum, final double maximum, final String message)
	{
		try
		{
			new DoubleField().optional().range(minimum, maximum);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	private static final IsNullCondition<Double> in(
			final DoubleField field)
	{
		return new IsNullCondition<Double>(field, false);
	}

	private static final IsNullCondition<Double> nn(
			final DoubleField field)
	{
		return new IsNullCondition<Double>(field, true);
	}

	private static final CompareCondition<Double> cc(
			final Operator operator,
			final DoubleField field,
			final Double value)
	{
		return new CompareCondition<Double>(operator, field, value);
	}
}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public class IntegerTest extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(IntegerItem.TYPE);

	public IntegerTest()
	{
		super(MODEL);
	}
	
	private IntegerItem item, item2;
	private int numberOfItems;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new IntegerItem(2201));
		deleteOnTearDown(item2 = new IntegerItem(2202));
		numberOfItems = 2;
	}
	
	public void testIntegers()
	{
		// test model
		assertEquals(item.TYPE, item.any.getType());
		assertEquals("any", item.any.getName());
		assertEquals(false, item.any.isMandatory());
		assertEqualsUnmodifiable(list(), item.any.getPatterns());
		assertEquals(Integer.MIN_VALUE, item.any.getMinimum());
		assertEquals(Integer.MAX_VALUE, item.any.getMaximum());
		assertContains(item.any.getSetterExceptions());

		assertEquals(item.TYPE, item.mandatory.getType());
		assertEquals("mandatory", item.mandatory.getName());
		assertEquals(true, item.mandatory.isMandatory());
		assertEquals(Integer.MIN_VALUE, item.mandatory.getMinimum());
		assertEquals(Integer.MAX_VALUE, item.mandatory.getMaximum());
		assertContains(MandatoryViolationException.class, item.mandatory.getSetterExceptions());

		assertEquals(false, item.min4.isMandatory());
		assertEquals(4, item.min4.getMinimum());
		assertEquals(Integer.MAX_VALUE, item.min4.getMaximum());
		assertContains(RangeViolationException.class, item.min4.getSetterExceptions());

		assertEquals(false, item.max4.isMandatory());
		assertEquals(Integer.MIN_VALUE, item.max4.getMinimum());
		assertEquals(4, item.max4.getMaximum());
		assertContains(RangeViolationException.class, item.max4.getSetterExceptions());

		assertEquals(false, item.min4Max8.isMandatory());
		assertEquals(4, item.min4Max8.getMinimum());
		assertEquals(8, item.min4Max8.getMaximum());
		assertContains(RangeViolationException.class, item.min4Max8.getSetterExceptions());

		{
			final IntegerField orig = new IntegerField().optional();
			assertEquals(false, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertEquals(Integer.MIN_VALUE, orig.getMinimum());
			assertEquals(Integer.MAX_VALUE, orig.getMaximum());

			final IntegerField copy = orig.copy();
			assertEquals(false, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertEquals(Integer.MIN_VALUE, copy.getMinimum());
			assertEquals(Integer.MAX_VALUE, copy.getMaximum());
		}
		{
			final IntegerField orig = new IntegerField().toFinal().optional().min(10);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEquals(10, orig.getMinimum());
			assertEquals(Integer.MAX_VALUE, orig.getMaximum());
			
			final IntegerField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEquals(10, copy.getMinimum());
			assertEquals(Integer.MAX_VALUE, copy.getMaximum());
		}
		{
			final IntegerField orig = new IntegerField().toFinal().optional().unique().min(20);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNotNull(orig.getImplicitUniqueConstraint());
			assertEquals(20, orig.getMinimum());
			assertEquals(Integer.MAX_VALUE, orig.getMaximum());
			
			final IntegerField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNotNull(copy.getImplicitUniqueConstraint());
			assertEquals(20, copy.getMinimum());
			assertEquals(Integer.MAX_VALUE, copy.getMaximum());
		}
		{
			final IntegerField orig = new IntegerField().toFinal().optional().max(30);
			assertEquals(true, orig.isFinal());
			assertEquals(false, orig.isMandatory());
			assertNull(orig.getImplicitUniqueConstraint());
			assertEquals(Integer.MIN_VALUE, orig.getMinimum());
			assertEquals(30, orig.getMaximum());
			
			final IntegerField copy = orig.copy();
			assertEquals(true, copy.isFinal());
			assertEquals(false, copy.isMandatory());
			assertNull(copy.getImplicitUniqueConstraint());
			assertEquals(Integer.MIN_VALUE, copy.getMinimum());
			assertEquals(30, copy.getMaximum());
		}
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
		
		assertWrongRange(0,  0,  "maximum must be greater than mimimum, but was 0 and 0.");
		assertWrongRange(22, 22, "maximum must be greater than mimimum, but was 22 and 22.");
		assertWrongRange(22, 21, "maximum must be greater than mimimum, but was 21 and 22.");
		assertWrongRange(Integer.MAX_VALUE, Integer.MIN_VALUE, "maximum must be greater than mimimum, but was " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE + ".");
		assertWrongRange(Integer.MIN_VALUE, Integer.MIN_VALUE, "maximum must be greater than mimimum, but was " + Integer.MIN_VALUE + " and " + Integer.MIN_VALUE + ".");
		assertWrongRange(Integer.MAX_VALUE, Integer.MAX_VALUE, "maximum must be greater than mimimum, but was " + Integer.MAX_VALUE + " and " + Integer.MAX_VALUE + ".");

		// test conditions
		assertEquals(item.any.equal(1), item.any.equal(1));
		assertNotEquals(item.any.equal(1), item.any.equal(2));
		assertNotEquals(item.any.equal(1), item.any.equal((Integer)null));
		assertNotEquals(item.any.equal(1), item.any.greater(1));
		assertEquals(item.any.equal(item.mandatory), item.any.equal(item.mandatory));
		assertNotEquals(item.any.equal(item.mandatory), item.any.equal(item.any));

		// any
		item.setAny(1234);
		assertEquals(new Integer(1234), item.getAny());
		item.setAny(123);
		assertEquals(new Integer(123), item.getAny());
		
		// mandatory
		assertEquals(2201, item.getMandatory());
	
		item.setMandatory(52201);
		assertEquals(52201, item.getMandatory());
	
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
		assertEquals(52201, item.getMandatory());
	
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new IntegerItem((Integer)null);
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
			new IntegerItem(new SetValue[]{});
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
			item.setMin4(3);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4, e.getFeature());
			assertEquals(item.min4, e.getFeature());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3 is too small for " + item.min4 + ", " +
					"must be at least 4.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
		assertEquals(null, item.getMin4());

		item.setMin4(4);
		assertEquals(new Integer(4), item.getMin4());

		// max4
		item.setMax4(4);
		assertEquals(new Integer(4), item.getMax4());
		try
		{
			item.setMax4(5);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"5 is too big for " + item.max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(new Integer(4), item.getMax4());
		restartTransaction();
		assertEquals(new Integer(4), item.getMax4());

		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			new IntegerItem(5, (Date)null);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(
					"range violation on a newly created item, " +
					"5 is too big for " + item.max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());
		try
		{
			IntegerItem.TYPE.newItem(
					item.mandatory.map(1234567),
					item.max4.map(5)
			);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(item.max4, e.getFeature());
			assertEquals(item.max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(
					"range violation on a newly created item, " +
					"5 is too big for " + item.max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(numberOfItems, item.TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8(3);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3 is too small for " + item.min4Max8 + ", " +
					"must be at least 4.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8(4);
		assertEquals(new Integer(4), item.getMin4Max8());

		item.setMin4Max8(8);
		assertEquals(new Integer(8), item.getMin4Max8());

		restartTransaction();
		assertEquals(new Integer(8), item.getMin4Max8());

		try
		{
			item.setMin4Max8(9);
			fail();
		}
		catch(RangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(item.min4Max8, e.getFeature());
			assertEquals(9, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"9 is too big for " + item.min4Max8 + ", " +
					"must be at most 8.",
					e.getMessage());
		}
		assertEquals(new Integer(8), item.getMin4Max8());
		restartTransaction();
		assertEquals(new Integer(8), item.getMin4Max8());
		
		
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
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + String.class.getName() + " for " + item.any + '.', e.getMessage());
		}
	}
	
	void assertWrongRange(final int minimum, final int maximum, final String message)
	{
		try
		{
			new IntegerField().optional().range(minimum, maximum);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
}

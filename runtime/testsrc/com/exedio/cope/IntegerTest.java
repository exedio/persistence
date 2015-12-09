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

import static com.exedio.cope.IntegerItem.TYPE;
import static com.exedio.cope.IntegerItem.any;
import static com.exedio.cope.IntegerItem.mandatory;
import static com.exedio.cope.IntegerItem.max4;
import static com.exedio.cope.IntegerItem.min4;
import static com.exedio.cope.IntegerItem.min4Max8;
import static java.lang.Integer.valueOf;

import java.util.Date;

public class IntegerTest extends AbstractRuntimeModelTest
{
	public IntegerTest()
	{
		super(IntegerModelTest.MODEL);
	}

	private IntegerItem item;
	@SuppressWarnings("unused") // OK: is an item not to be found by searches
	private IntegerItem item2;
	private int numberOfItems;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = new IntegerItem(2201);
		item2 = new IntegerItem(2202);
		numberOfItems = 2;
	}

	public void testIt()
	{
		// any
		item.setAny(1234);
		assertEquals(valueOf(1234), item.getAny());
		item.setAny(123);
		assertEquals(valueOf(123), item.getAny());

		// mandatory
		assertEquals(2201, item.getMandatory());

		item.setMandatory(52201);
		assertEquals(52201, item.getMandatory());

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
		assertEquals(52201, item.getMandatory());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new IntegerItem((Integer)null);
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
			new IntegerItem(new SetValue<?>[]{});
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

		// min4
		try
		{
			item.setMin4(3);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4, e.getFeature());
			assertSame(min4, e.getFeature());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3 is too small for " + min4 + ", " +
					"must be at least 4.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4());
		restartTransaction();
		assertEquals(null, item.getMin4());

		item.setMin4(4);
		assertEquals(valueOf(4), item.getMin4());

		// max4
		item.setMax4(4);
		assertEquals(valueOf(4), item.getMax4());
		try
		{
			item.setMax4(5);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"5 is too big for " + max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(valueOf(4), item.getMax4());
		restartTransaction();
		assertEquals(valueOf(4), item.getMax4());

		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			new IntegerItem(5, (Date)null);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(
					"range violation, " +
					"5 is too big for " + max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());
		try
		{
			TYPE.newItem(
					mandatory.map(1234567),
					max4.map(5)
			);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(null, e.getItem());
			assertSame(max4, e.getFeature());
			assertSame(max4, e.getFeature());
			assertEquals(5, e.getValue());
			assertEquals(
					"range violation, " +
					"5 is too big for " + max4 + ", " +
					"must be at most 4.",
					e.getMessage());
		}
		assertEquals(numberOfItems, TYPE.search(null).size());

		// min4max8
		try
		{
			item.setMin4Max8(3);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEquals(3, e.getValue());
			assertEquals(true, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"3 is too small for " + min4Max8 + ", " +
					"must be at least 4.",
					e.getMessage());
		}
		assertEquals(null, item.getMin4Max8());
		restartTransaction();
		assertEquals(null, item.getMin4Max8());

		item.setMin4Max8(4);
		assertEquals(valueOf(4), item.getMin4Max8());

		item.setMin4Max8(8);
		assertEquals(valueOf(8), item.getMin4Max8());

		restartTransaction();
		assertEquals(valueOf(8), item.getMin4Max8());

		try
		{
			item.setMin4Max8(9);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(item, e.getItem());
			assertSame(min4Max8, e.getFeature());
			assertSame(min4Max8, e.getFeature());
			assertEquals(9, e.getValue());
			assertEquals(false, e.isTooSmall());
			assertEquals(
					"range violation on " + item + ", " +
					"9 is too big for " + min4Max8 + ", " +
					"must be at most 8.",
					e.getMessage());
		}
		assertEquals(valueOf(8), item.getMin4Max8());
		restartTransaction();
		assertEquals(valueOf(8), item.getMin4Max8());


		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)any, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + String.class.getName() + " for " + any + '.', e.getMessage());
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}

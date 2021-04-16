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

import static com.exedio.cope.LongItem.TYPE;
import static com.exedio.cope.LongItem.any;
import static com.exedio.cope.LongItem.mandatory;
import static com.exedio.cope.LongItem.max4;
import static com.exedio.cope.LongItem.min4;
import static com.exedio.cope.LongItem.min4Max8;
import static java.lang.Long.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LongTest extends TestWithEnvironment
{
	public LongTest()
	{
		super(LongModelTest.MODEL);
	}

	private LongItem item;
	private int numberOfItems;

	@BeforeEach final void setUp()
	{
		item = new LongItem(2201l);
		new LongItem(2202l);
		numberOfItems = 2;
	}

	@Test void testIt()
	{
		// any
		item.setAny(1234l);
		assertEquals(valueOf(1234), item.getAny());
		item.setAny(123l);
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
			new LongItem((Long)null);
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
			new LongItem(new SetValue<?>[]{});
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
			item.setMin4(3l);
			fail();
		}
		catch(final LongRangeViolationException e)
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

		item.setMin4(4l);
		assertEquals(valueOf(4), item.getMin4());

		// max4
		item.setMax4(4l);
		assertEquals(valueOf(4), item.getMax4());
		try
		{
			item.setMax4(5l);
			fail();
		}
		catch(final LongRangeViolationException e)
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
			new LongItem(5l, null);
			fail();
		}
		catch(final LongRangeViolationException e)
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
					mandatory.map(1234567l),
					max4.map(5l)
			);
			fail();
		}
		catch(final LongRangeViolationException e)
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
			item.setMin4Max8(3l);
			fail();
		}
		catch(final LongRangeViolationException e)
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

		item.setMin4Max8(4l);
		assertEquals(valueOf(4), item.getMin4Max8());

		item.setMin4Max8(8l);
		assertEquals(valueOf(8), item.getMin4Max8());

		restartTransaction();
		assertEquals(valueOf(8), item.getMin4Max8());

		try
		{
			item.setMin4Max8(9l);
			fail();
		}
		catch(final LongRangeViolationException e)
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

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)any, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Long.class.getName() + ", but was a " + String.class.getName() + " for " + any + '.', e.getMessage());
		}
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}

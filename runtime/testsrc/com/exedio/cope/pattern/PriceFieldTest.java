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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Price.MAX_VALUE;
import static com.exedio.cope.pattern.Price.MIN_VALUE;
import static com.exedio.cope.pattern.Price.storeOf;
import static com.exedio.cope.pattern.PriceFieldItem.bigPrice;
import static com.exedio.cope.pattern.PriceFieldItem.finalPrice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.LongRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PriceFieldTest extends TestWithEnvironment
{
	public PriceFieldTest()
	{
		super(PriceFieldModelTest.MODEL);
	}

	PriceFieldItem item;

	@BeforeEach final void setUp()
	{
		item = new PriceFieldItem(storeOf(555), storeOf(7777));
	}

	@Test void testIt()
	{
		assertEquals("finalPrice_int", SchemaInfo.getColumnName(finalPrice.getInt()));

		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setOptionalPrice(storeOf(333));
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(storeOf(333), item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setOptionalPrice(storeOf(-444));
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(storeOf(-444), item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setOptionalPrice(MIN_VALUE);
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(MIN_VALUE, item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setOptionalPrice(MAX_VALUE);
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(MAX_VALUE, item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setOptionalPrice(null);
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(7777), item.getBigPrice());

		item.setBigPrice(storeOf(5000));
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(5000), item.getBigPrice());

		try
		{
			item.setBigPrice(storeOf(4999));
			fail();
		}
		catch(final LongRangeViolationException e)
		{
			assertEquals(bigPrice.getInt(), e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(4999, e.getValue());
			assertTrue(e.isTooSmall());
		}
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(5000), item.getBigPrice());

		try
		{
			item.setBigPrice(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(bigPrice, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(5000), item.getBigPrice());
		try
		{
			new PriceFieldItem(new SetValue<?>[]{
					finalPrice.map(storeOf(567)),
					bigPrice.map(null),
			});
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(bigPrice, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			new PriceFieldItem(new SetValue<?>[]{
					finalPrice.map(storeOf(567)),
			});
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(bigPrice.getInt(), e.getFeature()); // TODO should be price itself, not the int
			assertEquals(null, e.getItem());
		}

		final PriceFieldItem item2 = new PriceFieldItem(new SetValue<?>[]{
				finalPrice.map(storeOf(567)),
				bigPrice.map(storeOf(5001)),
		});
		assertEquals(storeOf(567), item2.getFinalPrice());
		assertEquals(null, item2.getOptionalPrice());
		assertEquals(storeOf(5001), item2.getBigPrice());

		try
		{
			finalPrice.set(item2, null);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(finalPrice, e.getFeature());
			assertEquals(item2, e.getItem());
		}
	}
}

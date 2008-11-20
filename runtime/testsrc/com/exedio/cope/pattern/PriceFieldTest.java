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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Price.valueOf;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;

public class PriceFieldTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(PriceFieldItem.TYPE);
	
	public PriceFieldTest()
	{
		super(MODEL);
	}

	PriceFieldItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new PriceFieldItem(valueOf(555), valueOf(7777)));
	}
	
	public void testSerializer()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.finalPrice,
				item.finalPrice.getInt(),
				item.optionalPrice,
				item.optionalPrice.getInt(),
				item.bigPrice,
				item.bigPrice.getInt(),
			}), item.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.finalPrice,
				item.finalPrice.getInt(),
				item.optionalPrice,
				item.optionalPrice.getInt(),
				item.bigPrice,
				item.bigPrice.getInt(),
			}), item.TYPE.getDeclaredFeatures());

		assertEquals(item.TYPE, item.finalPrice.getInt().getType());
		assertEquals(item.TYPE, item.finalPrice.getType());
		assertEquals(item.TYPE, item.optionalPrice.getInt().getType());
		assertEquals(item.TYPE, item.optionalPrice.getType());
		assertEquals(item.TYPE, item.bigPrice.getInt().getType());
		assertEquals(item.TYPE, item.bigPrice.getType());
		assertEquals("finalPriceInt", item.finalPrice.getInt().getName());
		assertEquals("finalPrice",    item.finalPrice.getName());
		assertEquals("optionalPriceInt", item.optionalPrice.getInt().getName());
		assertEquals("optionalPrice",    item.optionalPrice.getName());
		assertEquals("bigPriceInt", item.bigPrice.getInt().getName());
		assertEquals("bigPrice",    item.bigPrice.getName());

		assertEquals(item.finalPrice, item.finalPrice.getInt().getPattern());
		assertEquals(item.optionalPrice, item.optionalPrice.getInt().getPattern());
		assertEquals(item.bigPrice, item.bigPrice.getInt().getPattern());
		
		assertEquals(true, item.finalPrice.isInitial());
		assertEquals(true, item.finalPrice.isFinal());
		assertEquals(Price.class, item.finalPrice.getInitialType());
		assertContains(MandatoryViolationException.class, FinalViolationException.class, item.finalPrice.getInitialExceptions());
		
		assertEquals(false, item.optionalPrice.isInitial());
		assertEquals(false, item.optionalPrice.isFinal());
		assertEquals(Price.class, item.optionalPrice.getInitialType());
		assertContains(item.optionalPrice.getInitialExceptions());
		
		assertEquals(true, item.bigPrice.isInitial());
		assertEquals(false, item.bigPrice.isFinal());
		assertEquals(Price.class, item.bigPrice.getInitialType());
		assertContains(MandatoryViolationException.class, IntegerRangeViolationException.class, item.bigPrice.getInitialExceptions());
		
		// test persistence
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(valueOf(7777), item.getBigPrice());
		
		item.setOptionalPrice(valueOf(333));
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(valueOf(333), item.getOptionalPrice());
		assertEquals(valueOf(7777), item.getBigPrice());
		
		item.setOptionalPrice(valueOf(-444));
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(valueOf(-444), item.getOptionalPrice());
		assertEquals(valueOf(7777), item.getBigPrice());
		
		item.setOptionalPrice(null);
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(valueOf(7777), item.getBigPrice());
		
		item.setBigPrice(valueOf(5000));
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(valueOf(5000), item.getBigPrice());
		
		try
		{
			item.setBigPrice(valueOf(4999));
			fail();
		}
		catch(IntegerRangeViolationException e)
		{
			assertEquals(item.bigPrice.getInt(), e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(4999, e.getValue());
			assertTrue(e.isTooSmall());
		}
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(valueOf(5000), item.getBigPrice());
		
		try
		{
			item.setBigPrice(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.bigPrice.getInt(), e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(valueOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(valueOf(5000), item.getBigPrice());
		
		final PriceFieldItem item2 = deleteOnTearDown(new PriceFieldItem(new SetValue[]{
				item.finalPrice.map(valueOf(567)),
				item.bigPrice.map(valueOf(5001)),
		}));
		assertEquals(valueOf(567), item2.getFinalPrice());
		assertEquals(null, item2.getOptionalPrice());
		assertEquals(valueOf(5001), item2.getBigPrice());
	}
}

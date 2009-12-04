/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.Price.storeOf;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.misc.Computed;

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
		item = deleteOnTearDown(new PriceFieldItem(storeOf(555), storeOf(7777)));
	}
	
	public void testIt()
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
		
		assertNotNull(item.   finalPrice.getInt().isAnnotationPresent(Computed.class));
		assertNotNull(item.optionalPrice.getInt().isAnnotationPresent(Computed.class));
		assertNotNull(item.     bigPrice.getInt().isAnnotationPresent(Computed.class));
		
		// test persistence
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
		catch(IntegerRangeViolationException e)
		{
			assertEquals(item.bigPrice.getInt(), e.getFeature());
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
		catch(MandatoryViolationException e)
		{
			assertEquals(item.bigPrice, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertEquals(storeOf(555), item.getFinalPrice());
		assertEquals(null, item.getOptionalPrice());
		assertEquals(storeOf(5000), item.getBigPrice());
		try
		{
			new PriceFieldItem(new SetValue[]{
					item.finalPrice.map(storeOf(567)),
					item.bigPrice.map(null),
			});
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.bigPrice, e.getFeature());
			assertEquals(null, e.getItem());
		}
		try
		{
			new PriceFieldItem(new SetValue[]{
					item.finalPrice.map(storeOf(567)),
			});
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.bigPrice.getInt(), e.getFeature()); // TODO should be price itself, not the int
			assertEquals(null, e.getItem());
		}
		
		final PriceFieldItem item2 = deleteOnTearDown(new PriceFieldItem(new SetValue[]{
				item.finalPrice.map(storeOf(567)),
				item.bigPrice.map(storeOf(5001)),
		}));
		assertEquals(storeOf(567), item2.getFinalPrice());
		assertEquals(null, item2.getOptionalPrice());
		assertEquals(storeOf(5001), item2.getBigPrice());
		
		try
		{
			item2.finalPrice.set(item2, null);
			fail();
		}
		catch(FinalViolationException e)
		{
			assertEquals(item2.finalPrice, e.getFeature());
			assertEquals(item2, e.getItem());
		}
	}
}

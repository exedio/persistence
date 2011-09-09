/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.pattern.PriceFieldItem.TYPE;
import static com.exedio.cope.pattern.PriceFieldItem.bigPrice;
import static com.exedio.cope.pattern.PriceFieldItem.finalPrice;
import static com.exedio.cope.pattern.PriceFieldItem.optionalPrice;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.SetValue;
import com.exedio.cope.misc.Computed;

public class PriceFieldTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PriceFieldTest.class, "MODEL");
	}

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
				TYPE.getThis(),
				finalPrice,
				finalPrice.getInt(),
				optionalPrice,
				optionalPrice.getInt(),
				bigPrice,
				bigPrice.getInt(),
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				finalPrice,
				finalPrice.getInt(),
				optionalPrice,
				optionalPrice.getInt(),
				bigPrice,
				bigPrice.getInt(),
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, finalPrice.getInt().getType());
		assertEquals(TYPE, finalPrice.getType());
		assertEquals(TYPE, optionalPrice.getInt().getType());
		assertEquals(TYPE, optionalPrice.getType());
		assertEquals(TYPE, bigPrice.getInt().getType());
		assertEquals(TYPE, bigPrice.getType());
		assertEquals("finalPrice-int",finalPrice.getInt().getName());
		assertEquals("finalPrice",    finalPrice.getName());
		assertEquals("optionalPrice-int",optionalPrice.getInt().getName());
		assertEquals("optionalPrice",    optionalPrice.getName());
		assertEquals("bigPrice-int",bigPrice.getInt().getName());
		assertEquals("bigPrice",    bigPrice.getName());

		assertEquals(finalPrice, finalPrice.getInt().getPattern());
		assertEquals(optionalPrice, optionalPrice.getInt().getPattern());
		assertEquals(bigPrice, bigPrice.getInt().getPattern());

		assertEquals(true, finalPrice.isInitial());
		assertEquals(true, finalPrice.isMandatory());
		assertEquals(true, finalPrice.isFinal());
		assertEquals(Price.class, getInitialType(finalPrice));
		assertContains(MandatoryViolationException.class, FinalViolationException.class, finalPrice.getInitialExceptions());

		assertEquals(false, optionalPrice.isInitial());
		assertEquals(false, optionalPrice.isMandatory());
		assertEquals(false, optionalPrice.isFinal());
		assertEquals(Price.class, getInitialType(optionalPrice));
		assertContains(optionalPrice.getInitialExceptions());

		assertEquals(true, bigPrice.isInitial());
		assertEquals(false, bigPrice.isFinal());
		assertEquals(Price.class, getInitialType(bigPrice));
		assertContains(MandatoryViolationException.class, IntegerRangeViolationException.class, bigPrice.getInitialExceptions());

		assertTrue(   finalPrice.getInt().isAnnotationPresent(Computed.class));
		assertTrue(optionalPrice.getInt().isAnnotationPresent(Computed.class));
		assertTrue(     bigPrice.getInt().isAnnotationPresent(Computed.class));

		assertSerializedSame(   finalPrice, 388);
		assertSerializedSame(optionalPrice, 391);
		assertSerializedSame(     bigPrice, 386);

		// test persistence
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
		catch(final IntegerRangeViolationException e)
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
			new PriceFieldItem(new SetValue[]{
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
			new PriceFieldItem(new SetValue[]{
					finalPrice.map(storeOf(567)),
			});
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(bigPrice.getInt(), e.getFeature()); // TODO should be price itself, not the int
			assertEquals(null, e.getItem());
		}

		final PriceFieldItem item2 = deleteOnTearDown(new PriceFieldItem(new SetValue[]{
				finalPrice.map(storeOf(567)),
				bigPrice.map(storeOf(5001)),
		}));
		assertEquals(storeOf(567), item2.getFinalPrice());
		assertEquals(null, item2.getOptionalPrice());
		assertEquals(storeOf(5001), item2.getBigPrice());

		try
		{
			item2.finalPrice.set(item2, null);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(item2.finalPrice, e.getFeature());
			assertEquals(item2, e.getItem());
		}
	}
}

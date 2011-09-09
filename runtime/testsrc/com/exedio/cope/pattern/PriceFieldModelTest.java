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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.AbstractRuntimeTest.getInitialType;
import static com.exedio.cope.pattern.PriceFieldItem.TYPE;
import static com.exedio.cope.pattern.PriceFieldItem.bigPrice;
import static com.exedio.cope.pattern.PriceFieldItem.finalPrice;
import static com.exedio.cope.pattern.PriceFieldItem.optionalPrice;

import java.util.Arrays;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class PriceFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PriceFieldModelTest.class, "MODEL");
	}

	public void testIt()
	{
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

		assertSerializedSame(   finalPrice, 393);
		assertSerializedSame(optionalPrice, 396);
		assertSerializedSame(     bigPrice, 391);
	}
}

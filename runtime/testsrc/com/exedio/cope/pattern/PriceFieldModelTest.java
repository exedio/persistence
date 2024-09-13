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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.PriceFieldItem.TYPE;
import static com.exedio.cope.pattern.PriceFieldItem.bigPrice;
import static com.exedio.cope.pattern.PriceFieldItem.finalPrice;
import static com.exedio.cope.pattern.PriceFieldItem.optionalPrice;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.LongRangeViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class PriceFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PriceFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
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
		assertEquals(Price.class, finalPrice.getInitialType());
		assertContains(MandatoryViolationException.class, FinalViolationException.class, finalPrice.getInitialExceptions());

		assertEquals(false, optionalPrice.isInitial());
		assertEquals(false, optionalPrice.isMandatory());
		assertEquals(false, optionalPrice.isFinal());
		assertEquals(Price.class, optionalPrice.getInitialType());
		assertContains(optionalPrice.getInitialExceptions());

		assertEquals(true, bigPrice.isInitial());
		assertEquals(false, bigPrice.isFinal());
		assertEquals(Price.class, bigPrice.getInitialType());
		assertContains(MandatoryViolationException.class, LongRangeViolationException.class, bigPrice.getInitialExceptions());

		assertTrue(   finalPrice.getInt().isAnnotationPresent(Computed.class));
		assertTrue(optionalPrice.getInt().isAnnotationPresent(Computed.class));
		assertTrue(     bigPrice.getInt().isAnnotationPresent(Computed.class));

		assertSerializedSame(   finalPrice, 393);
		assertSerializedSame(optionalPrice, 396);
		assertSerializedSame(     bigPrice, 391);

		assertEquals(null,    finalPrice.getDefaultConstant());
		assertEquals(null, optionalPrice.getDefaultConstant());
		assertEquals(null,      bigPrice.getDefaultConstant());

		assertEquals(Price.MIN_VALUE,    finalPrice.getMinimum());
		assertEquals(Price.MIN_VALUE, optionalPrice.getMinimum());
		assertEquals(Price.storeOf(5000),  bigPrice.getMinimum());

		assertEquals(Price.MAX_VALUE,    finalPrice.getMaximum());
		assertEquals(Price.MAX_VALUE, optionalPrice.getMaximum());
		assertEquals(Price.MAX_VALUE,      bigPrice.getMaximum());

		assertEquals(Long.MIN_VALUE+1,    finalPrice.getInt().getMinimum());
		assertEquals(Long.MIN_VALUE+1, optionalPrice.getInt().getMinimum());
		assertEquals(5000,                  bigPrice.getInt().getMinimum());

		assertEquals(Long.MAX_VALUE,    finalPrice.getInt().getMaximum());
		assertEquals(Long.MAX_VALUE, optionalPrice.getInt().getMaximum());
		assertEquals(Long.MAX_VALUE,      bigPrice.getInt().getMaximum());
	}

	@Test void testDefault()
	{
		final PriceField f = new PriceField();
		assertEquals(Price.MIN_VALUE, f.getMinimum());
		assertEquals(Price.MAX_VALUE, f.getMaximum());
		assertEquals(Long.MIN_VALUE+1, f.getInt().getMinimum());
		assertEquals(Long.MAX_VALUE  , f.getInt().getMaximum());
	}

	@Test void testMinZero()
	{
		final PriceField f = new PriceField().minZero();
		assertEquals(Price.ZERO,      f.getMinimum());
		assertEquals(Price.MAX_VALUE, f.getMaximum());
		assertEquals(0,              f.getInt().getMinimum());
		assertEquals(Long.MAX_VALUE, f.getInt().getMaximum());
	}

	@Test void testMin()
	{
		final PriceField f = new PriceField().min(Price.storeOf(2000));
		assertEquals(Price.storeOf(2000), f.getMinimum());
		assertEquals(Price.MAX_VALUE,     f.getMaximum());
		assertEquals(2000,           f.getInt().getMinimum());
		assertEquals(Long.MAX_VALUE, f.getInt().getMaximum());

		assertFails(
				() -> f.min(null),
				NullPointerException.class,
				"Cannot invoke \"com.exedio.cope.pattern.Price.store()\" because \"minimum\" is null");
	}

	@Test void testMax()
	{
		final PriceField f = new PriceField().max(Price.storeOf(4000));
		assertEquals(Price.MIN_VALUE,     f.getMinimum());
		assertEquals(Price.storeOf(4000), f.getMaximum());
		assertEquals(Long.MIN_VALUE+1, f.getInt().getMinimum());
		assertEquals(4000,             f.getInt().getMaximum());

		assertFails(
				() -> f.max(null),
				NullPointerException.class,
				"Cannot invoke \"com.exedio.cope.pattern.Price.store()\" because \"maximum\" is null");
	}

	@Test void testRange()
	{
		final PriceField f = new PriceField().range(Price.storeOf(2000), Price.storeOf(4000));
		assertEquals(Price.storeOf(2000), f.getMinimum());
		assertEquals(Price.storeOf(4000), f.getMaximum());
		assertEquals(2000, f.getInt().getMinimum());
		assertEquals(4000, f.getInt().getMaximum());

		assertFails(
				() -> f.range(null, Price.ZERO),
				NullPointerException.class,
				"Cannot invoke \"com.exedio.cope.pattern.Price.store()\" because \"minimum\" is null");
		assertFails(
				() -> f.range(Price.ZERO, null),
				NullPointerException.class,
				"Cannot invoke \"com.exedio.cope.pattern.Price.store()\" because \"maximum\" is null");
		final PriceField redundant = f.rangeEvenIfRedundant(Price.ZERO, Price.ZERO);
		assertEquals(Price.ZERO, redundant.getMinimum());
		assertEquals(Price.ZERO, redundant.getMinimum());
		assertFails(
				() -> f.range(Price.ZERO, Price.ZERO),
				IllegalArgumentException.class,
				"Redundant field with minimum==maximum (0) is probably a mistake. " +
				"You may call method rangeEvenIfRedundant if you are sure this is ok.");
	}

	@Test void testDefaultConstant()
	{
		final PriceField f = new PriceField();
		assertEquals(null, f.getDefaultConstant());
		assertEquals(true, f.isInitial());

		final PriceField df = f.defaultTo(Price.storeOf(2000));
		assertEquals(Price.storeOf(2000), df.getDefaultConstant());
		assertEquals(false, df.isInitial());

		assertFails(
				() -> f.defaultTo(null),
				NullPointerException.class,
				"Cannot invoke \"com.exedio.cope.pattern.Price.store()\" because \"defaultConstant\" is null");
	}
}

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

import static com.exedio.cope.pattern.Money.array;
import static com.exedio.cope.pattern.Money.nullToZero;
import static com.exedio.cope.pattern.Money.storeOf;
import static com.exedio.cope.pattern.Money.zero;
import static com.exedio.cope.pattern.MoneyTest.Cy.eur;
import static com.exedio.cope.pattern.MoneyTest.Cy.usd;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

public class MoneyTest
{
	enum Cy implements Money.Currency
	{
		eur, usd
	}


	@Test void testStoreOfLong()
	{
		assertEquals( 5, storeOf(Long.valueOf( 5), eur).amountStore(eur));
		assertEquals(-5, storeOf(Long.valueOf(-5), eur).amountStore(eur));
		assertEquals( 0, storeOf(Long.valueOf( 0), eur).amountStore(eur));
		assertEquals(null, storeOf((Long)null, eur));
	}

	@Test void testStoreOfInteger()
	{
		assertEquals( 5, storeOf(Integer.valueOf( 5), eur).amountStore(eur));
		assertEquals(-5, storeOf(Integer.valueOf(-5), eur).amountStore(eur));
		assertEquals( 0, storeOf(Integer.valueOf( 0), eur).amountStore(eur));
		assertEquals(null, storeOf((Integer)null, eur));
	}

	@Test void testNullToZero()
	{
		final Money<Cy> x = storeOf(1, eur);
		final Money<Cy> z = zero(eur);
		assertSame  (x, nullToZero(x,    eur));
		assertEquals(z, nullToZero(null, eur));
		assertEquals(z, nullToZero(z,    eur));
	}

	@Test void testNullToZeroMismatch()
	{
		final Money<Cy> z = zero(eur);
		try
		{
			nullToZero(z, usd);
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("currency mismatch 0.00eur/usd", e.getMessage());
		}
	}

	@Test void testNegateReuse()
	{
		final Money<Cy> z = zero(eur);
		final Money<Cy> a = storeOf(1, eur);
		assertSame(z, z.negate());
		assertEquals(storeOf(-1, eur), a.negate());
	}

	@Test void testAddReuse()
	{
		final Money<Cy> z = zero(eur);
		final Money<Cy> a = storeOf(1, eur);
		assertSame(a, a.add(z));
		assertSame(a, z.add(a));
		assertSame(z, z.add(z));
		assertEquals(storeOf(2, eur), a.add(a));
	}

	@Test void testCurrencyMismatch()
	{
		final Money<Cy> a = storeOf(222, eur);
		final Money<Cy> b = storeOf(333, usd);
		assertCurrencyMismatch(() -> a.amountStore(usd));
		assertCurrencyMismatch(() -> a.amountStoreIntExact(usd));
		assertCurrencyMismatch(() -> a.compareTo(b));
		assertCurrencyMismatch(() -> a.lessThan(b));
		assertCurrencyMismatch(() -> a.greaterThan(b));
		assertCurrencyMismatch(() -> a.lessThanOrEqual(b));
		assertCurrencyMismatch(() -> a.greaterThanOrEqual(b));
		assertCurrencyMismatch(() -> a.add(b));
		assertCurrencyMismatch(() -> a.subtract(b));
		assertCurrencyMismatch(() -> a.min(b));
		assertCurrencyMismatch(() -> a.max(b));
		assertCurrencyMismatch(() -> a.doubleAmount(usd));
		assertCurrencyMismatch(() -> a.bigAmount(usd));
		assertCurrencyMismatch(() -> a.getAmount(usd));
		assertCurrencyMismatch(() -> a.computeDouble(b, (x,y) -> { throw new AssertionFailedError(); }));
		assertCurrencyMismatch(() -> a.computeDouble(b, (x,y) -> { throw new AssertionFailedError(); }, RoundingMode.UNNECESSARY));
		assertCurrencyMismatch(() -> a.computeBig   (b, (x,y) -> { throw new AssertionFailedError(); }));
	}
	private static void assertCurrencyMismatch(final Executable executable)
	{
		assertFails(
				executable,
				IllegalArgumentException.class,
				"currency mismatch 2.22eur/usd");
	}

	@Test void testArray()
	{
		final Money<Cy>[] a = array(2);
		assertEquals(2, a.length);
		assertEquals(null, a[0]);
		assertEquals(null, a[1]);
	}

	@Test void testArrayEmpty()
	{
		final Money<Cy>[] a = array(0);
		assertEquals(0, a.length);
	}

	@Test void testArrayNegative()
	{
		assertFails(
				() -> array(-1),
				NegativeArraySizeException.class,
				"-1");
	}

	@Test void testComputeDouble()
	{
		final Money<Cy> a = storeOf(222, eur);
		final Money<Cy> b = storeOf(333, eur);
		final Money<Cy> c = storeOf(888, eur);
		assertEquals(c, a.computeDouble(x ->
		{
			assertEquals(2.22, x);
			return 8.88;
		}));
		assertEquals(c, a.computeDouble(x ->
		{
			assertEquals(2.22, x);
			return 8.88;
		}, RoundingMode.UNNECESSARY));
		assertEquals(c, a.computeDouble(b, (x,y) ->
		{
			assertEquals(2.22, x);
			assertEquals(3.33, y);
			return 8.88;
		}));
		assertEquals(c, a.computeDouble(b, (x,y) ->
		{
			assertEquals(2.22, x);
			assertEquals(3.33, y);
			return 8.88;
		}, RoundingMode.UNNECESSARY));
	}

	@Test void testComputeBigDecimal()
	{
		final Money<Cy> a = storeOf(222, eur);
		final Money<Cy> b = storeOf(333, eur);
		final Money<Cy> c = storeOf(888, eur);
		assertEquals(c, a.computeBig(x ->
		{
			assertEquals(BigDecimal.valueOf(222, 2), x);
			return BigDecimal.valueOf(888, 2);
		}));
		assertEquals(c, a.computeBig(b, (x,y) ->
		{
			assertEquals(BigDecimal.valueOf(222, 2), x);
			assertEquals(BigDecimal.valueOf(333, 2), y);
			return BigDecimal.valueOf(888, 2);
		}));
		assertFails(
				() -> a.computeBig(x -> null),
				NullPointerException.class, "Cannot invoke \"java.math.BigDecimal.compareTo(java.math.BigDecimal)\" because \"value\" is null");
		assertFails(
				() -> a.computeBig(b, (x,y) -> null),
				NullPointerException.class, "Cannot invoke \"java.math.BigDecimal.compareTo(java.math.BigDecimal)\" because \"value\" is null");
	}
}

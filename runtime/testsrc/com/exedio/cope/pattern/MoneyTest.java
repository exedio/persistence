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

import static com.exedio.cope.JavaVersion.assertThrowsNegativeArraySizeException;
import static com.exedio.cope.pattern.Money.array;
import static com.exedio.cope.pattern.Money.nullToZero;
import static com.exedio.cope.pattern.Money.storeOf;
import static com.exedio.cope.pattern.Money.zero;
import static com.exedio.cope.pattern.MoneyTest.Cy.eur;
import static com.exedio.cope.pattern.MoneyTest.Cy.usd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
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
		assertThrowsNegativeArraySizeException(
				() -> array(-1),
				-1);
	}
}

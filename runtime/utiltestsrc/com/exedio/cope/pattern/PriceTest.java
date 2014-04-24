/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.pattern.Price.ZERO;
import static com.exedio.cope.pattern.Price.nullToZero;
import static com.exedio.cope.pattern.Price.storeOf;
import static com.exedio.cope.pattern.Price.valueOf;

import com.exedio.cope.junit.CopeAssert;
import java.math.BigDecimal;

public final class PriceTest extends CopeAssert
{
	private static final Price MAX_VALUE_1 = Price.storeOf(Price.MAX_VALUE.store() - 1);
	private static final Price MIN_VALUE_1 = Price.storeOf(Price.MIN_VALUE.store() + 1);

	public static void testIt()
	{
		assertEquals(5, new Price(5).store());
		assertEquals(0, new Price(0).store());
	}

	public static void testZero()
	{
		assertEquals(0, ZERO.store());
		assertEquals(0.0, ZERO.doubleValue());
	}

	public static void testStoreOfInt()
	{
		assertEquals(storeOf(1), storeOf(1));
		assertNotSame(storeOf(1), storeOf(1));
		assertEquals(storeOf(-1), storeOf(-1));
		assertSame(ZERO, storeOf(0));
		assertSame(MIN_VALUE, storeOf(Integer.MIN_VALUE));
		assertSame(MAX_VALUE, storeOf(Integer.MAX_VALUE));
	}

	public static void testStoreOfInteger()
	{
		assertEquals( 5, storeOf(Integer.valueOf( 5)).store());
		assertEquals(-5, storeOf(Integer.valueOf(-5)).store());
		assertSame(ZERO, storeOf(Integer.valueOf( 0)));
		assertEquals(null, storeOf((Integer)null));
	}

	public static void testNullToZero()
	{
		final Price x = storeOf(1);
		assertSame(x,    nullToZero(x));
		assertSame(ZERO, nullToZero(null));
		assertSame(ZERO, nullToZero(ZERO));
	}

	public static void testValueOfDouble()
	{
		assertEquals( 222, valueOf( 2.22).store());
		assertEquals(-222, valueOf(-2.22).store());
		assertEquals( 220, valueOf( 2.2 ).store());
		assertEquals(-220, valueOf(-2.2 ).store());
		assertEquals( 200, valueOf( 2.0 ).store());
		assertEquals(-200, valueOf(-2.0 ).store());
		assertEquals( 202, valueOf( 2.02).store());
		assertEquals(-202, valueOf(-2.02).store());
		assertEquals( 002, valueOf( 0.02).store());
		assertEquals(-002, valueOf(-0.02).store());

		// from wikipedia
		assertEquals( 301, valueOf( 3.013  ).store()); // because the next digit (3) is 4 or less)
		assertEquals( 301, valueOf( 3.014  ).store()); // because the next digit (3) is 4 or less)
		assertEquals( 301, valueOf( 3.01499).store()); // because the next digit (3) is 4 or less)
		assertEquals( 302, valueOf( 3.015  ).store()); // because the next digit is 5, and the hundredths digit (1) is odd)
		assertEquals( 302, valueOf( 3.01501).store()); // because the next digit is 5, and the hundredths digit (1) is odd)
		assertEquals( 302, valueOf( 3.016  ).store()); // because the next digit (6) is 6 or more)
		assertEquals( 304, valueOf( 3.044  ).store()); // because the next digit (4) is less than 5
		assertEquals( 304, valueOf( 3.045  ).store()); // because the next digit is 5, and the hundredths digit (4) is even)
		assertEquals( 305, valueOf( 3.04501).store()); // because the next digit is 5, but it is followed by non-zero digits)
		assertEquals( 305, valueOf( 3.046  ).store()); // because the next digit (6) is 6 or more)
		assertEquals(-301, valueOf(-3.013  ).store());
		assertEquals(-301, valueOf(-3.014  ).store());
		assertEquals(-301, valueOf(-3.01499).store());
		assertEquals(-302, valueOf(-3.015  ).store());
		assertEquals(-302, valueOf(-3.01501).store());
		assertEquals(-302, valueOf(-3.016  ).store());
		assertEquals(-304, valueOf(-3.044  ).store());
		assertEquals(-304, valueOf(-3.045  ).store());
		assertEquals(-305, valueOf(-3.04501).store());
		assertEquals(-305, valueOf(-3.046  ).store());

		assertEquals( 10997, valueOf( 109.974  ).store());
		assertEquals( 10997, valueOf( 109.97499).store());
		assertEquals( 10998, valueOf( 109.975  ).store());
		assertEquals( 10998, valueOf( 109.97501).store());
		assertEquals( 10998, valueOf( 109.976  ).store());
		assertEquals( 10998, valueOf( 109.984  ).store());
		assertEquals( 10998, valueOf( 109.98499).store());
		assertEquals( 10998, valueOf( 109.985  ).store());
		assertEquals( 10999, valueOf( 109.98501).store());
		assertEquals( 10999, valueOf( 109.986  ).store());
		assertEquals(-10997, valueOf(-109.974  ).store());
		assertEquals(-10997, valueOf(-109.97499).store());
		assertEquals(-10998, valueOf(-109.975  ).store());
		assertEquals(-10998, valueOf(-109.97501).store());
		assertEquals(-10998, valueOf(-109.976  ).store());
		assertEquals(-10998, valueOf(-109.984  ).store());
		assertEquals(-10998, valueOf(-109.98499).store());
		assertEquals(-10998, valueOf(-109.985  ).store());
		assertEquals(-10999, valueOf(-109.98501).store());
		assertEquals(-10999, valueOf(-109.986  ).store());

		assertEquals(Integer.MAX_VALUE, valueOf(Integer.MAX_VALUE/100d).store());
		assertEquals(Integer.MIN_VALUE, valueOf(Integer.MIN_VALUE/100d).store());
		assertSame(ZERO, valueOf( 0.0));
		assertSame(ZERO, valueOf(-0.0));
		try
		{
			valueOf((Integer.MAX_VALUE/100d) + 0.01);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.147483648E7", e.getMessage());
		}
		try
		{
			valueOf((Integer.MIN_VALUE/100d) - 0.01);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too small: -2.1474836490000002E7", e.getMessage());
		}
		try
		{
			valueOf(Double.NaN);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("NaN not allowed", e.getMessage());
		}
		try
		{
			valueOf(Double.NEGATIVE_INFINITY);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Infinity not allowed", e.getMessage());
		}
		try
		{
			valueOf(Double.POSITIVE_INFINITY);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Infinity not allowed", e.getMessage());
		}
	}

	public static void testValueOfBigDecimal()
	{
		assertEquals( 222, valueOf(bd( 222, 2)).store());
		assertEquals(-222, valueOf(bd(-222, 2)).store());
		assertEquals( 220, valueOf(bd( 22,  1)).store());
		assertEquals(-220, valueOf(bd(-22,  1)).store());
		assertEquals( 200, valueOf(bd( 2,   0)).store());
		assertEquals(-200, valueOf(bd(-2,   0)).store());
		assertEquals( 202, valueOf(bd( 202, 2)).store());
		assertEquals(-202, valueOf(bd(-202, 2)).store());
		assertEquals( 002, valueOf(bd(   2, 2)).store());
		assertEquals(-002, valueOf(bd(-  2, 2)).store());
		// from wikipedia
		assertEquals( 302, valueOf(bd( 3016,   3)).store()); // because the next digit (6) is 6 or more)
		assertEquals( 301, valueOf(bd( 3013,   3)).store()); // because the next digit (3) is 4 or less)
		assertEquals( 302, valueOf(bd( 3015,   3)).store()); // because the next digit is 5, and the hundredths digit (1) is odd)
		assertEquals( 304, valueOf(bd( 3045,   3)).store()); // because the next digit is 5, and the hundredths digit (4) is even)
		assertEquals( 305, valueOf(bd( 304501, 5)).store()); // because the next digit is 5, but it is followed by non-zero digits)
		assertEquals(-302, valueOf(bd(-3016,   3)).store());
		assertEquals(-301, valueOf(bd(-3013,   3)).store());
		assertEquals(-302, valueOf(bd(-3015,   3)).store());
		assertEquals(-304, valueOf(bd(-3045,   3)).store());
		assertEquals(-305, valueOf(bd(-304501, 5)).store());

		assertEquals(Integer.MAX_VALUE, valueOf(bd(Integer.MAX_VALUE, 2)).store());
		assertEquals(Integer.MIN_VALUE, valueOf(bd(Integer.MIN_VALUE, 2)).store());
		assertSame(ZERO, valueOf(bd(0, 0)));
		try
		{
			valueOf(bd(Integer.MAX_VALUE, 2).add(bd(1, 2)));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 21474836.48", e.getMessage());
		}
		try
		{
			valueOf(bd(Integer.MIN_VALUE, 2).subtract(bd(1, 2)));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too small: -21474836.49", e.getMessage());
		}
		try
		{
			valueOf((BigDecimal)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public static void testDoubleValue()
	{
		assertEquals( 2.22, storeOf( 222).doubleValue());
		assertEquals(-2.22, storeOf(-222).doubleValue());
		assertEquals( 2.2,  storeOf( 220).doubleValue());
		assertEquals(-2.2,  storeOf(-220).doubleValue());
		assertEquals( 0.0,  storeOf(   0).doubleValue());
	}

	public static void testBigValue()
	{
		assertEquals(bd( 222, 2), storeOf( 222).bigValue());
		assertEquals(bd(-222, 2), storeOf(-222).bigValue());
		assertEquals(bd( 22,  1), storeOf( 220).bigValue());
		assertEquals(bd(-22,  1), storeOf(-220).bigValue());
		assertEquals(bd( 2,   0), storeOf( 200).bigValue());
		assertEquals(bd(-2,   0), storeOf(-200).bigValue());
		assertEquals(bd( 0,   0), storeOf(   0).bigValue());
	}

	public static void testAdd()
	{
		assertEquals( 555, storeOf( 333).add(storeOf( 222)).store());
		assertEquals(-111, storeOf(-333).add(storeOf( 222)).store());
		assertEquals( 111, storeOf( 333).add(storeOf(-222)).store());
		assertEquals(-555, storeOf(-333).add(storeOf(-222)).store());
	}

	public static void testAddOverflow()
	{
		assertEquals( 2147483646, MAX_VALUE.add(storeOf(-1)).store());
		assertEquals( 2147483647, MAX_VALUE.add(storeOf( 0)).store());
		assertAddOverflows(       MAX_VALUE,    storeOf( 1));
		assertAddOverflows(       MAX_VALUE,    storeOf( 2));
		assertEquals(-2147483647, MIN_VALUE.add(storeOf( 1)).store());
		assertEquals(-2147483648, MIN_VALUE.add(storeOf( 0)).store());
		assertAddOverflows(       MIN_VALUE,    storeOf(-1));
		assertAddOverflows(       MIN_VALUE,    storeOf(-2));

		assertEquals( 2147483645, MAX_VALUE_1.add(storeOf(-1)).store());
		assertEquals( 2147483646, MAX_VALUE_1.add(storeOf( 0)).store());
		assertEquals( 2147483647, MAX_VALUE_1.add(storeOf( 1)).store());
		assertAddOverflows(       MAX_VALUE_1,    storeOf( 2));
		assertEquals(-2147483646, MIN_VALUE_1.add(storeOf( 1)).store());
		assertEquals(-2147483647, MIN_VALUE_1.add(storeOf( 0)).store());
		assertEquals(-2147483648, MIN_VALUE_1.add(storeOf(-1)).store());
		assertAddOverflows(       MIN_VALUE_1,    storeOf(-2));

		assertEquals( 2147483646, storeOf(-1).add(MAX_VALUE).store());
		assertEquals( 2147483647, storeOf( 0).add(MAX_VALUE).store());
		assertAddOverflows(       storeOf( 1),    MAX_VALUE);
		assertAddOverflows(       storeOf( 2),    MAX_VALUE);
		assertEquals(-2147483647, storeOf( 1).add(MIN_VALUE).store());
		assertEquals(-2147483648, storeOf( 0).add(MIN_VALUE).store());
		assertAddOverflows(       storeOf(-1),    MIN_VALUE);
		assertAddOverflows(       storeOf(-2),    MIN_VALUE);

		assertEquals( 2147483645, storeOf(-1).add(MAX_VALUE_1).store());
		assertEquals( 2147483646, storeOf( 0).add(MAX_VALUE_1).store());
		assertEquals( 2147483647, storeOf( 1).add(MAX_VALUE_1).store());
		assertAddOverflows(       storeOf( 2),    MAX_VALUE_1);
		assertEquals(-2147483646, storeOf( 1).add(MIN_VALUE_1).store());
		assertEquals(-2147483647, storeOf( 0).add(MIN_VALUE_1).store());
		assertEquals(-2147483648, storeOf(-1).add(MIN_VALUE_1).store());
		assertAddOverflows(       storeOf(-2),    MIN_VALUE_1);

		assertAddOverflows(MAX_VALUE,   MAX_VALUE  );
		assertAddOverflows(MAX_VALUE  , MAX_VALUE_1);
		assertAddOverflows(MAX_VALUE_1, MAX_VALUE  );
		assertAddOverflows(MAX_VALUE_1, MAX_VALUE_1);
		assertAddOverflows(MIN_VALUE,   MIN_VALUE  );
		assertAddOverflows(MIN_VALUE  , MIN_VALUE_1);
		assertAddOverflows(MIN_VALUE_1, MIN_VALUE  );
		assertAddOverflows(MIN_VALUE_1, MIN_VALUE_1);
	}

	private static void assertAddOverflows(final Price left, final Price right)
	{
		try
		{
			left.add(right);
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("overflow " + left  + " plus " + right, e.getMessage());
		}
	}

	public static void testSubtract()
	{
		assertEquals( 333, storeOf( 555).subtract(storeOf( 222)).store());
		assertEquals(-333, storeOf(-111).subtract(storeOf( 222)).store());
		assertEquals( 333, storeOf( 111).subtract(storeOf(-222)).store());
		assertEquals(-333, storeOf(-555).subtract(storeOf(-222)).store());
	}

	public static void testSubtractOverflow()
	{
		assertEquals( 2147483646, MAX_VALUE.subtract(storeOf( 1)).store());
		assertEquals( 2147483647, MAX_VALUE.subtract(storeOf( 0)).store());
		assertEquals(-2147483648, MAX_VALUE.subtract(storeOf(-1)).store()); // TODO fail
		assertEquals(-2147483647, MAX_VALUE.subtract(storeOf(-2)).store()); // TODO fail
		assertEquals(-2147483647, MIN_VALUE.subtract(storeOf(-1)).store());
		assertEquals(-2147483648, MIN_VALUE.subtract(storeOf( 0)).store());
		assertEquals( 2147483647, MIN_VALUE.subtract(storeOf( 1)).store()); // TODO fail
		assertEquals( 2147483646, MIN_VALUE.subtract(storeOf( 2)).store()); // TODO fail
	}

	public static void testNegative()
	{
		assertEquals(storeOf(-555), storeOf( 555).negative());
		assertEquals(storeOf( 555), storeOf(-555).negative());
		assertSame(Price.ZERO, storeOf(0).negative());
		assertEquals(storeOf(Integer.MIN_VALUE+1), MAX_VALUE.negative());
		assertEquals(storeOf(Integer.MAX_VALUE), storeOf(Integer.MIN_VALUE+1).negative());
		try
		{
			MIN_VALUE.negative();
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("no negative for -21474836.48", e.getMessage());
		}
	}

	public static void testMultiplyInt()
	{
		assertEquals( 999, storeOf( 333).multiply( 3).store());
		assertEquals(-999, storeOf(-333).multiply( 3).store());
		assertEquals(-999, storeOf( 333).multiply(-3).store());
		assertEquals( 999, storeOf(-333).multiply(-3).store());

		// TODO overflow
		assertEquals( 1073741823, storeOf(1073741823).multiply(1).store());
		assertEquals( 1073741824, storeOf(1073741824).multiply(1).store());
		assertEquals( 1073741825, storeOf(1073741825).multiply(1).store());
		assertEquals( 2147483646, storeOf(1073741823).multiply(2).store());
		assertEquals(-2147483648, storeOf(1073741824).multiply(2).store()); // wrong
		assertEquals(-2147483646, storeOf(1073741825).multiply(2).store()); // wrong
	}

	public static void testMultiplyDouble()
	{
		assertEquals( 999, storeOf( 333).multiply( 3d).store());
		assertEquals(-999, storeOf(-333).multiply( 3d).store());
		assertEquals(-999, storeOf( 333).multiply(-3d).store());
		assertEquals( 999, storeOf(-333).multiply(-3d).store());

		assertEquals( 1073741823, storeOf(1073741823).multiply(1d).store());
		assertEquals( 1073741824, storeOf(1073741824).multiply(1d).store());
		assertEquals( 1073741825, storeOf(1073741825).multiply(1d).store());
		assertEquals( 2147483646, storeOf(1073741823).multiply(2d).store());
		try
		{
			storeOf(1073741824).multiply(2d);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.147483648E7", e.getMessage());
		}
		try
		{
			storeOf(1073741825).multiply(2d);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.14748365E7", e.getMessage());
		}
	}

	public static void testDivideDouble()
	{
		assertEquals( 333, storeOf( 999).divide( 3d).store());
		assertEquals(-333, storeOf(-999).divide( 3d).store());
		assertEquals(-333, storeOf( 999).divide(-3d).store());
		assertEquals( 333, storeOf(-999).divide(-3d).store());

		assertEquals( 1073741823, storeOf(1073741823).divide(1d ).store());
		assertEquals( 1073741824, storeOf(1073741824).divide(1d ).store());
		assertEquals( 1073741825, storeOf(1073741825).divide(1d ).store());
		assertEquals( 2147483646, storeOf(1073741823).divide(0.5).store());
		try
		{
			storeOf(1073741824).divide(0.5);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.147483648E7", e.getMessage());
		}
		try
		{
			storeOf(1073741825).divide(0.5);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.14748365E7", e.getMessage());
		}
	}

	public static void testEquals()
	{
		assertEquals(storeOf( 123), storeOf( 123));
		assertEquals(storeOf(-123), storeOf(-123));
		assertFalse(storeOf(123).equals(storeOf( 124)));
		assertFalse(storeOf(123).equals(storeOf(-123)));
		assertNotSame(storeOf(123), storeOf(123));
		assertFalse(storeOf(123).equals(Integer.valueOf(123)));
		assertFalse(storeOf(123).equals(Double.valueOf(1.23)));
		assertFalse(storeOf(123).equals(null));
	}

	public static void testHashCode()
	{
		assertEquals(storeOf( 123).hashCode(), storeOf( 123).hashCode());
		assertEquals(storeOf(-123).hashCode(), storeOf(-123).hashCode());
		assertFalse(storeOf(123).hashCode()==storeOf( 124).hashCode());
		assertFalse(storeOf(123).hashCode()==storeOf(-123).hashCode());
	}

	public static void testCompareTo()
	{
		assertEquals(-1, storeOf(122).compareTo(storeOf(123)));
		assertEquals( 0, storeOf(123).compareTo(storeOf(123)));
		assertEquals( 1, storeOf(124).compareTo(storeOf(123)));
		assertEquals( 1, storeOf(-122).compareTo(storeOf(-123)));
		assertEquals( 0, storeOf(-123).compareTo(storeOf(-123)));
		assertEquals(-1, storeOf(-124).compareTo(storeOf(-123)));
	}

	public static void testToString()
	{
		assertToString( "1.23", storeOf( 123));
		assertToString("-1.23", storeOf(-123));
		assertToString( "1.03", storeOf( 103));
		assertToString("-1.03", storeOf(-103));
		assertToString( "0.23", storeOf(  23));
		assertToString("-0.23", storeOf( -23));
		assertToString( "0.03", storeOf(   3));
		assertToString("-0.03", storeOf(  -3));
		assertToString( "0.00", storeOf(   0),  "0"  );
		assertToString( "1.20", storeOf( 120),  "1.2");
		assertToString("-1.20", storeOf(-120), "-1.2");
		assertToString( "1.00", storeOf( 100),  "1"  );
		assertToString("-1.00", storeOf(-100), "-1"  );
		assertToString("-21474836.48", MIN_VALUE);
		assertToString( "21474836.47", MAX_VALUE);
	}

	private static void assertToString(final String expected, final Price actual)
	{
		assertToString(expected, actual, expected);
	}

	private static void assertToString(final String expected, final Price actual, final String expectedShort)
	{
		assertEquals("toString", expected, actual.toString());
		assertEquals("toStringShort", expectedShort, actual.toStringShort());
	}

	public static void testSerialization()
	{
		assertEquals(storeOf( 3456), reserialize(storeOf( 3456), 62));
		assertEquals(storeOf(-3456), reserialize(storeOf(-3456), 62));
	}

	private static final BigDecimal bd(final long unscaledVal, final int scale)
	{
		return BigDecimal.valueOf(unscaledVal, scale);
	}

	public static void testLessThan()
	{
		assertEquals(true,  storeOf( 122).lessThan(storeOf( 123)));
		assertEquals(false, storeOf( 123).lessThan(storeOf( 123)));
		assertEquals(false, storeOf( 124).lessThan(storeOf( 123)));
		assertEquals(false, storeOf(-122).lessThan(storeOf(-123)));
		assertEquals(false, storeOf(-123).lessThan(storeOf(-123)));
		assertEquals(true,  storeOf(-124).lessThan(storeOf(-123)));
	}

	public static void testGreaterThan()
	{
		assertEquals(false, storeOf( 122).greaterThan(storeOf( 123)));
		assertEquals(false, storeOf( 123).greaterThan(storeOf( 123)));
		assertEquals(true,  storeOf( 124).greaterThan(storeOf( 123)));
		assertEquals(true,  storeOf(-122).greaterThan(storeOf(-123)));
		assertEquals(false, storeOf(-123).greaterThan(storeOf(-123)));
		assertEquals(false, storeOf(-124).greaterThan(storeOf(-123)));
	}

	public static void testLessThanOrEqual()
	{
		assertEquals(true,  storeOf( 122).lessThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 123).lessThanOrEqual(storeOf( 123)));
		assertEquals(false, storeOf( 124).lessThanOrEqual(storeOf( 123)));
		assertEquals(false, storeOf(-122).lessThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-123).lessThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-124).lessThanOrEqual(storeOf(-123)));
	}

	public static void testGreaterThanOrEqual()
	{
		assertEquals(false, storeOf( 122).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 123).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 124).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf(-122).greaterThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-123).greaterThanOrEqual(storeOf(-123)));
		assertEquals(false, storeOf(-124).greaterThanOrEqual(storeOf(-123)));
	}

	public static void testGetLower()
	{
		assertEquals(storeOf( 122), storeOf( 122).getLower(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 123).getLower(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 124).getLower(storeOf( 123)));
		assertEquals(storeOf(-123), storeOf(-122).getLower(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-123).getLower(storeOf(-123)));
		assertEquals(storeOf(-124), storeOf(-124).getLower(storeOf(-123)));
	}

	public static void testGetGreater()
	{
		assertEquals(storeOf( 123), storeOf( 122).getGreater(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 123).getGreater(storeOf( 123)));
		assertEquals(storeOf( 124), storeOf( 124).getGreater(storeOf( 123)));
		assertEquals(storeOf(-122), storeOf(-122).getGreater(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-123).getGreater(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-124).getGreater(storeOf(-123)));
	}

	public static void testGrossToNetPercent()
	{
		assertEquals(storeOf( 100), storeOf( 126).grossToNetPercent(26));
		assertEquals(storeOf(-100), storeOf(-126).grossToNetPercent(26));
		assertEquals(storeOf(   0), storeOf(   0).grossToNetPercent(26));
		assertEquals(storeOf(   0), storeOf(   0).grossToNetPercent(0));
		assertEquals(storeOf( 126), storeOf( 126).grossToNetPercent(0));
		assertEquals(storeOf(-126), storeOf(-126).grossToNetPercent(0));

		try
		{
			ZERO.grossToNetPercent(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("rate must not be negative, but was -1", e.getMessage());
		}
		{
			final Price p = storeOf( 126);
			assertSame(p, p.grossToNetPercent(0));
		}
	}

	public static void testGrossToTaxPercent()
	{
		assertEquals(storeOf( 26), storeOf( 126).grossToTaxPercent(26));
		assertEquals(storeOf(-26), storeOf(-126).grossToTaxPercent(26));
		assertEquals(storeOf(  0), storeOf(   0).grossToTaxPercent(26));
		assertEquals(storeOf(  0), storeOf(   0).grossToTaxPercent(0));
		assertEquals(storeOf(  0), storeOf( 126).grossToTaxPercent(0));
		assertEquals(storeOf(  0), storeOf(-126).grossToTaxPercent(0));

		try
		{
			ZERO.grossToTaxPercent(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("rate must not be negative, but was -1", e.getMessage());
		}
	}
}

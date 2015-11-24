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

import static com.exedio.cope.pattern.Price.ZERO;
import static com.exedio.cope.pattern.Price.nullToZero;
import static com.exedio.cope.pattern.Price.parse;
import static com.exedio.cope.pattern.Price.storeOf;
import static com.exedio.cope.pattern.Price.valueOf;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_DOWN;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UP;

import com.exedio.cope.junit.CopeAssert;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class PriceTest extends CopeAssert
{
	private static final int MIN_STORE = Integer.MIN_VALUE + 1;
	private static final int MAX_STORE = Integer.MAX_VALUE;

	private static final Price p49 = storeOf(1073741823);
	private static final Price p50 = storeOf(1073741824);
	private static final Price p51 = storeOf(1073741825);
	private static final Price p97 = storeOf(2147483645);
	private static final Price p98 = storeOf(2147483646);
	private static final Price p99 = storeOf(2147483647);

	private static final Price mp49 = p49.negative();
	private static final Price mp50 = p50.negative();
	private static final Price mp51 = p51.negative();
	private static final Price mp97 = p97.negative();
	private static final Price mp98 = p98.negative();
	private static final Price mp99 = p99.negative();

	public static void testIt()
	{
		assertEquals(5, storeOf(5).store());
		assertEquals(0, storeOf(0).store());
	}

	public static void testZero()
	{
		assertEquals(0, ZERO.store());
		assertEquals(0.0, ZERO.doubleValue());
	}

	public static void testStoreOfInt()
	{
		assertEquals(storeOf(1), storeOf(1));
		assertSame  (storeOf(1), storeOf(1));
		assertEquals(storeOf(-1), storeOf(-1));
		assertNotSame(storeOf(-1), storeOf(-1));
		assertEquals(storeOf(1000), storeOf(1000)); // yet in cache
		assertSame  (storeOf(1000), storeOf(1000));
		assertEquals(storeOf(1001), storeOf(1001)); // outside cache
		assertNotSame(storeOf(1001), storeOf(1001));
		assertSame(ZERO, storeOf(0));
		assertSame(mp99, storeOf(MIN_STORE));
		assertSame( p99, storeOf(MAX_STORE));
	}

	public static void testStoreOfIntOutOfRange()
	{
		try
		{
			storeOf(Integer.MIN_VALUE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Integer.MIN_VALUE not allowed", e.getMessage());
		}
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

		assertEquals(MAX_STORE, valueOf(MAX_STORE/100d).store());
		assertEquals(MIN_STORE, valueOf(MIN_STORE/100d).store());
		assertSame(ZERO, valueOf( 0.0));
		assertSame(ZERO, valueOf(-0.0));
		try
		{
			valueOf((MAX_STORE/100d) + 0.01);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 2.147483648E7", e.getMessage());
		}
		try
		{
			valueOf((MIN_STORE/100d) - 0.01);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too small: -2.147483648E7", e.getMessage());
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

	public static void testValueOfDoubleRoundSpecialProblem()
	{
		final double problem = 0.575;
		assertEquals( "0.575", Double.toString( problem));
		assertEquals("-0.575", Double.toString(-problem));
		assertEquals(    58,   valueOf( problem).store());
		assertEquals(   -58,   valueOf(-problem).store());
		assertValueOfDouble(problem, 58, HALF_EVEN, HALF_UP, UP);
		assertValueOfDouble(problem, 57, DOWN, HALF_DOWN);
	}

	public static void testValueOfDoubleRoundSpecialProblem2()
	{
		final double problem = 1.435;
		assertEquals( "1.435", Double.toString( problem));
		assertEquals("-1.435", Double.toString(-problem));
		assertEquals(    144,  valueOf( problem).store());
		assertEquals(   -144,  valueOf(-problem).store());
		assertValueOfDouble(problem, 144, HALF_EVEN, HALF_UP, UP);
		assertValueOfDouble(problem, 143, DOWN, HALF_DOWN);
	}

	private static void assertValueOfDouble(final double origin, final int expected, final RoundingMode... roundingModes)
	{
		for(final RoundingMode rm : roundingModes)
		{
			assertEquals( expected, valueOf( origin, rm).store());
			assertEquals(-expected, valueOf(-origin, rm).store());
		}
	}

	public static void testRoundSpecialProblemOnDivide()
	{
		final Price problem = Price.storeOf(115);
		assertEquals( "2.0", Double.toString( 2d));
		assertEquals("-2.0", Double.toString(-2d));
		// result 0.575
		assertEquals(    58, problem.divide( 2d).store());
		assertEquals(   -58, problem.divide(-2d).store());
		assertValueOfDoubleOnDivide(problem, 2d, 57, DOWN, HALF_DOWN);
		assertValueOfDoubleOnDivide(problem, 2d, 58, HALF_EVEN, HALF_UP, UP);
	}

	private static void assertValueOfDoubleOnDivide(final Price origin, final double divisor, final int expected, final RoundingMode... roundingModes)
	{
		for (final RoundingMode rm : roundingModes)
		{
			assertEquals( expected, origin.divide( divisor, rm).store());
			assertEquals(-expected, origin.divide(-divisor, rm).store());
		}
	}

	public static void testRoundSpecialProblemOnMultiply()
	{
		final Price problem = Price.storeOf(41);
		final double multiplier = 3.5;
		assertEquals( "3.5", Double.toString( multiplier));
		assertEquals("-3.5", Double.toString(-multiplier));
		// result 1.435
		assertEquals(   144, problem.multiply( multiplier).store());
		assertEquals(  -144, problem.multiply(-multiplier).store());
		assertValueOfDoubleOnMultiply(problem, multiplier, 144, HALF_EVEN, HALF_UP, UP);
		assertValueOfDoubleOnMultiply(problem, multiplier, 143, DOWN, HALF_DOWN);
	}

	private static void assertValueOfDoubleOnMultiply(final Price origin, final double multiplier, final int expected, final RoundingMode... roundingModes)
	{
		for(final RoundingMode rm : roundingModes)
		{
			assertEquals( expected, origin.multiply( multiplier, rm).store());
			assertEquals(-expected, origin.multiply(-multiplier, rm).store());
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

		assertEquals(MAX_STORE, valueOf(bd(MAX_STORE, 2)).store());
		assertEquals(MIN_STORE, valueOf(bd(MIN_STORE, 2)).store());
		assertSame(ZERO, valueOf(bd(0, 0)));
		assertValueOfIllegal(bd(MAX_STORE, 2).add(     bd(1, 2)), "too big: 21474836.48");
		assertValueOfIllegal(bd(MIN_STORE, 2).subtract(bd(1, 2)), "too small: -21474836.48");
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

	private static final BigDecimal bd(final long unscaledVal, final int scale)
	{
		return BigDecimal.valueOf(unscaledVal, scale);
	}

	private static void assertValueOfIllegal(final BigDecimal value, final String message)
	{
		try
		{
			valueOf(value);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
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
		assertSame(BigDecimal.ZERO, storeOf(0).bigValue()); // relies on BigDecimal implementation
	}

	public static void testAdd()
	{
		assertEquals( 555, storeOf( 333).add(storeOf( 222)).store());
		assertEquals(-111, storeOf(-333).add(storeOf( 222)).store());
		assertEquals( 111, storeOf( 333).add(storeOf(-222)).store());
		assertEquals(-555, storeOf(-333).add(storeOf(-222)).store());
	}

	public static void testAddNeutral()
	{
		final Price p = storeOf(555);
		assertSame(p, p.add(ZERO));
		assertSame(p, ZERO.add(p));
		assertSame(ZERO, ZERO.add(ZERO));
	}

	public static void testAddOverflow()
	{
		assertEquals( p98,  p99.add(storeOf(-1)));
		assertEquals( p99,  p99.add(storeOf( 0)));
		assertAddOverflows( p99,    storeOf( 1));
		assertAddOverflows( p99,    storeOf( 2));
		assertEquals(mp98, mp99.add(storeOf( 1)));
		assertEquals(mp99, mp99.add(storeOf( 0)));
		assertAddOverflows(mp99,    storeOf(-1));
		assertAddOverflows(mp99,    storeOf(-2));

		assertEquals( p97,  p98.add(storeOf(-1)));
		assertEquals( p98,  p98.add(storeOf( 0)));
		assertEquals( p99,  p98.add(storeOf( 1)));
		assertAddOverflows( p98,    storeOf( 2));
		assertEquals(mp97, mp98.add(storeOf( 1)));
		assertEquals(mp98, mp98.add(storeOf( 0)));
		assertEquals(mp99, mp98.add(storeOf(-1)));
		assertAddOverflows(mp98,    storeOf(-2));

		assertEquals( p98, storeOf(-1).add( p99));
		assertEquals( p99, storeOf( 0).add( p99));
		assertAddOverflows(storeOf( 1),     p99);
		assertAddOverflows(storeOf( 2),     p99);
		assertEquals(mp98, storeOf( 1).add(mp99));
		assertEquals(mp99, storeOf( 0).add(mp99));
		assertAddOverflows(storeOf(-1),    mp99);
		assertAddOverflows(storeOf(-2),    mp99);

		assertEquals( p97, storeOf(-1).add( p98));
		assertEquals( p98, storeOf( 0).add( p98));
		assertEquals( p99, storeOf( 1).add( p98));
		assertAddOverflows(storeOf( 2),     p98);
		assertEquals(mp97, storeOf( 1).add(mp98));
		assertEquals(mp98, storeOf( 0).add(mp98));
		assertEquals(mp99, storeOf(-1).add(mp98));
		assertAddOverflows(storeOf(-2),    mp98);

		assertAddOverflows( p99,  p99);
		assertAddOverflows( p99,  p98);
		assertAddOverflows( p98,  p99);
		assertAddOverflows( p98,  p98);
		assertAddOverflows(mp99, mp99);
		assertAddOverflows(mp99, mp98);
		assertAddOverflows(mp98, mp99);
		assertAddOverflows(mp98, mp98);
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

	public static void testSubtractNeutral()
	{
		final Price p = storeOf(555);
		assertSame(p, p.subtract(ZERO));
		assertEquals(storeOf(-555), ZERO.subtract(p));
		assertSame(ZERO, ZERO.subtract(ZERO));
	}

	public static void testSubtractOverflow()
	{
		assertEquals( p98,       p99.subtract(storeOf( 1)));
		assertEquals( p99,       p99.subtract(storeOf( 0)));
		assertSubtractOverflows( p99, storeOf(-1));
		assertSubtractOverflows( p99, storeOf(-2));
		assertEquals(mp98,      mp99.subtract(storeOf(-1)));
		assertEquals(mp99,      mp99.subtract(storeOf( 0)));
		assertSubtractOverflows(mp99, storeOf( 1));
		assertSubtractOverflows(mp99, storeOf( 2));
	}

	private static void assertSubtractOverflows(final Price left, final Price right)
	{
		try
		{
			left.subtract(right);
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("overflow " + left  + " minus " + right, e.getMessage());
		}
	}

	public static void testNegative()
	{
		assertEquals(storeOf(-555), storeOf( 555).negative());
		assertEquals(storeOf( 555), storeOf(-555).negative());
		assertSame(Price.ZERO, storeOf(0).negative());
		assertEquals(storeOf(MIN_STORE),  p99.negative());
		assertEquals(storeOf(MAX_STORE), mp99.negative());
	}

	public static void testMultiplyInt()
	{
		assertEquals( 999, storeOf( 333).multiply( 3).store());
		assertEquals(-999, storeOf(-333).multiply( 3).store());
		assertEquals(-999, storeOf( 333).multiply(-3).store());
		assertEquals( 999, storeOf(-333).multiply(-3).store());
	}

	public static void testMultiplyIntNeutral()
	{
		final Price p = storeOf(555);
		assertSame(ZERO, p.multiply(0));
		assertSame(p, p.multiply(1));
		assertEquals(storeOf(-555), p.multiply(-1));
	}

	public static void testMultiplyIntOverflow()
	{
		assertEquals( p49,  p49.multiply(1));
		assertEquals( p50,  p50.multiply(1));
		assertEquals( p51,  p51.multiply(1));
		assertEquals( p98,  p49.multiply(2));
		assertMultiplyOver( p50, 2);
		assertMultiplyOver( p51, 2);

		assertEquals(mp49,  p49.multiply(-1));
		assertEquals(mp50,  p50.multiply(-1));
		assertEquals(mp51,  p51.multiply(-1));
		assertEquals(mp98,  p49.multiply(-2));
		assertMultiplyOver( p50, -2);
		assertMultiplyOver( p51, -2);

		assertEquals(mp49, mp49.multiply(1));
		assertEquals(mp50, mp50.multiply(1));
		assertEquals(mp51, mp51.multiply(1));
		assertEquals(mp98, mp49.multiply(2));
		assertMultiplyOver(mp50,  2);
		assertMultiplyOver(mp51,  2);

		assertEquals( p49, mp49.multiply(-1));
		assertEquals( p50, mp50.multiply(-1));
		assertEquals( p51, mp51.multiply(-1));
		assertEquals( p98, mp49.multiply(-2));
		assertMultiplyOver(mp50, -2);
		assertMultiplyOver(mp51, -2);
	}

	private static void assertMultiplyOver(final Price left, final int right)
	{
		try
		{
			left.multiply(right);
			fail();
		}
		catch(final ArithmeticException e)
		{
			assertEquals("overflow " + left  + " multiply " + right, e.getMessage());
		}
	}

	public static void testMultiplyDouble()
	{
		assertEquals( 999, storeOf( 333).multiply( 3d).store());
		assertEquals(-999, storeOf(-333).multiply( 3d).store());
		assertEquals(-999, storeOf( 333).multiply(-3d).store());
		assertEquals( 999, storeOf(-333).multiply(-3d).store());
	}

	public static void testMultiplyDoubleNeutral()
	{
		final Price p = storeOf(555);
		assertSame(ZERO, p.multiply(0.0));
		assertSame(p, p.multiply(1.0));
		assertEquals(storeOf(-555), p.multiply(-1.0));
	}

	public static void testMultiplyDoubleOverflow()
	{
		assertEquals(p49,  p49.multiply(1d));
		assertEquals(p50,  p50.multiply(1d));
		assertEquals(p51,  p51.multiply(1d));
		assertEquals(p98,  p49.multiply(2d));
		assertMultiplyOver(p50,         2d, "too big: 21474836.480");
		assertMultiplyOver(p51,         2d, "too big: 21474836.500");
	}

	private static void assertMultiplyOver(final Price left, final double right, final String message)
	{
		try
		{
			left.multiply(right);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	public static void testDivideDouble()
	{
		assertEquals( 333, storeOf( 999).divide( 3d).store());
		assertEquals(-333, storeOf(-999).divide( 3d).store());
		assertEquals(-333, storeOf( 999).divide(-3d).store());
		assertEquals( 333, storeOf(-999).divide(-3d).store());
	}

	public static void testDivideDoubleNeutral()
	{
		final Price p = storeOf(555);
		try
		{
			p.divide(0.0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("Infinity not allowed", e.getMessage());
		}
		assertSame(p, p.divide(1.0));
		assertEquals(storeOf(-555), p.divide(-1.0));
	}

	public static void testDivideDoubleOverflow()
	{
		assertEquals(p49, p49.divide(1d ));
		assertEquals(p50, p50.divide(1d ));
		assertEquals(p51, p51.divide(1d ));
		assertEquals(p98, p49.divide(0.5));
		assertDivideOver( p50,       0.5, "too big: 21474836.48");
		assertDivideOver( p51,       0.5, "too big: 21474836.50");
	}

	private static void assertDivideOver(final Price left, final double right, final String message)
	{
		try
		{
			left.divide(right);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	public static void testEqualsSame()
	{
		assertEquals(storeOf( 123), storeOf( 123));
		assertEquals(storeOf(-123), storeOf(-123));
		assertFalse(storeOf(123).equals(storeOf( 124)));
		assertFalse(storeOf(123).equals(storeOf(-123)));
		assertSame(storeOf(123), storeOf(123));
	}

	public static void testEqualsOther()
	{
		assertFalse(storeOf(123).equals(Integer.valueOf(123)));
		assertFalse(storeOf(123).equals(Double.valueOf(1.23)));
		assertFalse(storeOf(123).equals(null));
	}

	public static void testEqualsNotSame()
	{
		assertEquals(storeOf( 4123), storeOf( 4123));
		assertEquals(storeOf(-4123), storeOf(-4123));
		assertFalse(storeOf(4123).equals(storeOf( 124)));
		assertFalse(storeOf(4123).equals(storeOf(-4123)));
		assertNotSame(storeOf(4123), storeOf(4123));
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
		assertEquals(-1, storeOf( 122).compareTo(storeOf( 123)));
		assertEquals( 0, storeOf( 123).compareTo(storeOf( 123)));
		assertEquals( 1, storeOf( 124).compareTo(storeOf( 123)));
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
		assertToString("-21474836.47", mp99);
		assertToString( "21474836.47",  p99);
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

	public static void testFormatReal() throws ParseException
	{
		final DecimalFormat en = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		final DecimalFormat de = (DecimalFormat)NumberFormat.getInstance(Locale.GERMAN);
		en.setParseBigDecimal(true);
		de.setParseBigDecimal(true);
		assertParse(en, "0", "0.1", "-0.1", "1.1", "-1.1", "1,234.5", "-1,234.5", "21,474,836.47", "-21,474,836.47");
		assertParse(de, "0", "0,1", "-0,1", "1,1", "-1,1", "1.234,5", "-1.234,5", "21.474.836,47", "-21.474.836,47");
		en.setGroupingUsed(false);
		de.setGroupingUsed(false);
		assertParse(en, "0", "0.1", "-0.1", "1.1", "-1.1",  "1234.5",  "-1234.5",   "21474836.47",   "-21474836.47");
		assertParse(de, "0", "0,1", "-0,1", "1,1", "-1,1",  "1234,5",  "-1234,5",   "21474836,47",   "-21474836,47");
	}

	public static void testFormatSynthetic() throws ParseException
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		assertEquals('.', df.getDecimalFormatSymbols().getDecimalSeparator());
		assertEquals('.', df.getDecimalFormatSymbols().getMonetaryDecimalSeparator());
		assertEquals('-', df.getDecimalFormatSymbols().getMinusSign());
		assertEquals(',', df.getDecimalFormatSymbols().getGroupingSeparator());

		final DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('d');
		dfs.setGroupingSeparator('g');
		dfs.setMinusSign('m');
		df.setDecimalFormatSymbols(dfs);
		assertEquals('d', df.getDecimalFormatSymbols().getDecimalSeparator());
		assertEquals('.', df.getDecimalFormatSymbols().getMonetaryDecimalSeparator());
		assertEquals('m', df.getDecimalFormatSymbols().getMinusSign());
		assertEquals('g', df.getDecimalFormatSymbols().getGroupingSeparator());

		assertParse(df, "0", "0d1", "m0d1", "1d1", "m1d1", "1g234d5", "m1g234d5", "21g474g836d47", "m21g474g836d47");
		df.setGroupingUsed(false);
		assertParse(df, "0", "0d1", "m0d1", "1d1", "m1d1",  "1234d5",  "m1234d5",   "21474836d47",   "m21474836d47");
	}

	private static void assertParse(
			final DecimalFormat format,
			final String zero,
			final String fractPos,
			final String fractNeg,
			final String smallPos,
			final String smallNeg,
			final String groupPos,
			final String groupNeg,
			final String s99,
			final String sm99) throws ParseException
	{
		// compare to double
		assertEquals("zero"    , zero    , format.format(    0.0));
		assertEquals("fractPos", fractPos, format.format(    0.1));
		assertEquals("fractNeg", fractNeg, format.format(   -0.1));
		assertEquals("smallPos", smallPos, format.format(    1.1));
		assertEquals("smallNeg", smallNeg, format.format(   -1.1));
		assertEquals("groupPos", groupPos, format.format( 1234.5));
		assertEquals("groupNeg", groupNeg, format.format(-1234.5));

		assertEquals("zero"    , zero    , storeOf(      0).format(format));
		assertEquals("fractPos", fractPos, storeOf(     10).format(format));
		assertEquals("fractNeg", fractNeg, storeOf(    -10).format(format));
		assertEquals("smallPos", smallPos, storeOf(    110).format(format));
		assertEquals("smallNeg", smallNeg, storeOf(   -110).format(format));
		assertEquals("groupPos", groupPos, storeOf( 123450).format(format));
		assertEquals("groupNeg", groupNeg, storeOf(-123450).format(format));
		assertEquals("p99"     ,  s99,                  p99.format(format));
		assertEquals("mp99"    , sm99,                 mp99.format(format));

		assertEquals("zero"    , storeOf(      0), parse(zero    , format));
		assertEquals("fractPos", storeOf(     10), parse(fractPos, format));
		assertEquals("fractNeg", storeOf(    -10), parse(fractNeg, format));
		assertEquals("smallPos", storeOf(    110), parse(smallPos, format));
		assertEquals("smallNeg", storeOf(   -110), parse(smallNeg, format));
		assertEquals("groupPos", storeOf( 123450), parse(groupPos, format));
		assertEquals("groupNeg", storeOf(-123450), parse(groupNeg, format));
		assertEquals( "p99",                  p99, parse( s99    , format));
		assertEquals("mp99",                 mp99, parse(sm99    , format));
	}

	public static void testParseTooBig() throws ParseException
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		try
		{
			parse("21474836.48", df);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too big: 21474836.48", e.getMessage());
		}
	}

	public static void testParseTooSmall() throws ParseException
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		try
		{
			parse("-21474836.48", df);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("too small: -21474836.48", e.getMessage());
		}
	}

	public static void testParseTooPrecise()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		try
		{
			parse("1.101", df);
			fail();
		}
		catch(final ParseException e)
		{
			assertEquals("Rounding necessary:1.101", e.getMessage());
		}
	}

	public static void testParseBigDecimalNotSupported() throws ParseException
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		try
		{
			parse("1.00", df);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("format does not support BigDecimal", e.getMessage());
		}
	}

	public static void testParseNullSource() throws ParseException
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		try
		{
			parse(null, df);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public static void testParseNullFormat() throws ParseException
	{
		try
		{
			parse("1.00", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	public static void testSerialization()
	{
		assertEquals(storeOf( 3456), reserialize(storeOf( 3456), 62));
		assertEquals(storeOf(-3456), reserialize(storeOf(-3456), 62));
	}

	public static void testSerializationCache()
	{
		assertEquals(storeOf(  -1), reserialize(storeOf(   -1), 62));
		assertEquals(storeOf(   0), reserialize(storeOf(    0), 62));
		assertEquals(storeOf(   1), reserialize(storeOf(    1), 62));
		assertEquals(storeOf(1000), reserialize(storeOf( 1000), 62));
		assertEquals(storeOf(1001), reserialize(storeOf( 1001), 62));

		assertNotSame(storeOf(  -1), reserialize(storeOf(   -1), 62));
		assertSame   (storeOf(   0), reserialize(storeOf(    0), 62));
		assertSame   (storeOf(   1), reserialize(storeOf(    1), 62));
		assertSame   (storeOf(1000), reserialize(storeOf( 1000), 62));
		assertNotSame(storeOf(1001), reserialize(storeOf( 1001), 62));
	}

	public static void testEqualsZero()
	{
		assertEquals(false, storeOf(-1).equalsZero());
		assertEquals(true,  storeOf( 0).equalsZero());
		assertEquals(false, storeOf( 1).equalsZero());
	}

	public static void testLessThanZero()
	{
		assertEquals(true,  storeOf(-1).lessThanZero());
		assertEquals(false, storeOf( 0).lessThanZero());
		assertEquals(false, storeOf( 1).lessThanZero());
	}

	public static void testGreaterThanZero()
	{
		assertEquals(false, storeOf(-1).greaterThanZero());
		assertEquals(false, storeOf( 0).greaterThanZero());
		assertEquals(true,  storeOf( 1).greaterThanZero());
	}

	public static void testLessThanOrEqualZero()
	{
		assertEquals(true,  storeOf(-1).lessThanOrEqualZero());
		assertEquals(true,  storeOf( 0).lessThanOrEqualZero());
		assertEquals(false, storeOf( 1).lessThanOrEqualZero());
	}

	public static void testGreaterThanOrEqualZero()
	{
		assertEquals(false, storeOf(-1).greaterThanOrEqualZero());
		assertEquals(true,  storeOf( 0).greaterThanOrEqualZero());
		assertEquals(true,  storeOf( 1).greaterThanOrEqualZero());
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
		assertEquals(storeOf(  58), storeOf(  69).grossToNetPercent(20));
		assertEquals(storeOf( -58), storeOf( -69).grossToNetPercent(20));
		assertEquals(storeOf(  57), storeOf(  69).grossToNetPercent(20, HALF_DOWN));
		assertEquals(storeOf( -57), storeOf( -69).grossToNetPercent(20, HALF_DOWN));
		assertEquals(storeOf(  58), storeOf(  69).grossToNetPercent(20, HALF_UP));
		assertEquals(storeOf( -58), storeOf( -69).grossToNetPercent(20, HALF_UP));

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
		assertEquals(storeOf( 58), storeOf( 120).grossToTaxPercent(92));
		assertEquals(storeOf(-58), storeOf(-120).grossToTaxPercent(92));
		assertEquals(storeOf( 57), storeOf( 120).grossToTaxPercent(92, HALF_DOWN));
		assertEquals(storeOf(-57), storeOf(-120).grossToTaxPercent(92, HALF_DOWN));

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

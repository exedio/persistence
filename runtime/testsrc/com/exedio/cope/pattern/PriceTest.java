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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_DOWN;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;
import static java.math.RoundingMode.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.Assert;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * This test is equivalent to {@link MoneyAmountTest}.
 */
@SuppressWarnings("Convert2MethodRef") // OK: easier to read
public class PriceTest
{
	private static final long MIN_STORE = Long.MIN_VALUE + 1;
	private static final long MAX_STORE = Long.MAX_VALUE;

	private static final Price p49 = storeOf(4611686018427387903l);
	private static final Price p50 = storeOf(4611686018427387904l);
	private static final Price p51 = storeOf(4611686018427387905l);
	private static final Price p97 = storeOf(9223372036854775805l);
	private static final Price p98 = storeOf(9223372036854775806l);
	private static final Price p99 = storeOf(9223372036854775807l);

	private static final Price mp49 = p49.negate();
	private static final Price mp50 = p50.negate();
	private static final Price mp51 = p51.negate();
	private static final Price mp97 = p97.negate();
	private static final Price mp98 = p98.negate();
	private static final Price mp99 = p99.negate();

	@Test void testIt()
	{
		assertEquals(5, storeOf(5).store());
		assertEquals(0, storeOf(0).store());
	}

	@Test void testStoreIntExact()
	{
		assertEquals(5, storeOf(5).storeIntExact());
		assertEquals(0, storeOf(0).storeIntExact());
		assertEquals(Integer.MAX_VALUE, storeOf(Integer.MAX_VALUE).storeIntExact());
		assertEquals(Integer.MIN_VALUE, storeOf(Integer.MIN_VALUE).storeIntExact());
	}

	@Test void testStoreIntExactOverflow()
	{
		final Price p = storeOf(Integer.MAX_VALUE + 1l);
		assertFails(() ->
			p.storeIntExact(),
			ArithmeticException.class,
			"not an integer: 2147483648");
	}

	@Test void testStoreIntExactUnderflow()
	{
		final Price p = storeOf(Integer.MIN_VALUE - 1l);
		assertFails(() ->
			p.storeIntExact(),
			ArithmeticException.class,
			"not an integer: -2147483649");
	}

	@Test void testZero()
	{
		assertEquals(0, ZERO.store());
		assertEqualBits(0.0, ZERO.doubleValue());
	}

	@Test void testStoreOfInt()
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

	@Test void testStoreOfIntOutOfRange()
	{
		assertFails(() ->
			storeOf(Long.MIN_VALUE),
			IllegalArgumentException.class,
			"Long.MIN_VALUE not allowed");
	}

	@Test void testStoreOfLong()
	{
		assertEquals( 5, storeOf(Long.valueOf( 5)).store());
		assertEquals(-5, storeOf(Long.valueOf(-5)).store());
		assertSame(ZERO, storeOf(Long.valueOf( 0)));
		assertEquals(null, storeOf((Long)null));
	}

	@Test void testStoreOfInteger()
	{
		assertEquals( 5, storeOf(Integer.valueOf( 5)).store());
		assertEquals(-5, storeOf(Integer.valueOf(-5)).store());
		assertSame(ZERO, storeOf(Integer.valueOf( 0)));
		assertEquals(null, storeOf((Integer)null));
	}

	@Test void testNullToZero()
	{
		final Price x = storeOf(1);
		assertSame(x,    nullToZero(x));
		assertSame(ZERO, nullToZero(null));
		assertSame(ZERO, nullToZero(ZERO));
	}

	@Test void testValueOfDouble()
	{
		assertEquals( 222, valueOf( 2.22).store());
		assertEquals(-222, valueOf(-2.22).store());
		assertEquals( 220, valueOf( 2.2 ).store());
		assertEquals(-220, valueOf(-2.2 ).store());
		assertEquals( 200, valueOf( 2.0 ).store());
		assertEquals(-200, valueOf(-2.0 ).store());
		assertEquals( 202, valueOf( 2.02).store());
		assertEquals(-202, valueOf(-2.02).store());
		assertEquals(   2, valueOf( 0.02).store());
		assertEquals(-  2, valueOf(-0.02).store());

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

		assertEquals(MAX_STORE-1407, valueOf((MAX_STORE/100d) - 8.0).store());
		assertEquals(MIN_STORE+1407, valueOf((MIN_STORE/100d) + 8.0).store());
		assertSame(ZERO, valueOf( 0.0));
		assertSame(ZERO, valueOf(-0.0));
		assertFails(() ->
			valueOf((MAX_STORE/100d) + 0.01),
			IllegalArgumentException.class,
			"too big: 9.223372036854776E+16");
		assertFails(() ->
			valueOf((MIN_STORE/100d) - 0.01),
			IllegalArgumentException.class,
			"too small: -9.223372036854776E+16");
		assertFails(() ->
			valueOf(Double.NaN),
			IllegalArgumentException.class,
			"NaN not allowed");
		assertFails(() ->
			valueOf(Double.NEGATIVE_INFINITY),
			IllegalArgumentException.class,
			"Infinity not allowed");
		assertFails(() ->
			valueOf(Double.POSITIVE_INFINITY),
			IllegalArgumentException.class,
			"Infinity not allowed");
	}

	@Test void testValueOfDoubleRoundSpecialProblem()
	{
		final double problem = 0.575;
		assertEquals( "0.575", Double.toString( problem));
		assertEquals("-0.575", Double.toString(-problem));
		assertEquals(    58,   valueOf( problem).store());
		assertEquals(   -58,   valueOf(-problem).store());
		assertValueOfDouble(problem, 58, HALF_EVEN, HALF_UP, UP);
		assertValueOfDouble(problem, 57, DOWN, HALF_DOWN);
	}

	@Test void testValueOfDoubleRoundSpecialProblem2()
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

	@Test void testRoundSpecialProblemOnDivide()
	{
		final Price problem = storeOf(115);
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

	@Test void testRoundSpecialProblemOnMultiply()
	{
		final Price problem = storeOf(41);
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

	@Test void testValueOfBigDecimal()
	{
		assertEquals( 222, valueOf(bd( 222, 2)).store());
		assertEquals(-222, valueOf(bd(-222, 2)).store());
		assertEquals( 220, valueOf(bd( 22,  1)).store());
		assertEquals(-220, valueOf(bd(-22,  1)).store());
		assertEquals( 200, valueOf(bd( 2,   0)).store());
		assertEquals(-200, valueOf(bd(-2,   0)).store());
		assertEquals( 202, valueOf(bd( 202, 2)).store());
		assertEquals(-202, valueOf(bd(-202, 2)).store());
		assertEquals(   2, valueOf(bd(   2, 2)).store());
		assertEquals(-  2, valueOf(bd(-  2, 2)).store());
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
		assertValueOfIllegal(bd(MAX_STORE, 2).add(     bd(1, 2)), "too big: 92233720368547758.08");
		assertValueOfIllegal(bd(MIN_STORE, 2).subtract(bd(1, 2)), "too small: -92233720368547758.08");
		assertFails(() ->
			valueOf(null),
			NullPointerException.class, null);
	}

	private static BigDecimal bd(final long unscaledVal, final int scale)
	{
		return BigDecimal.valueOf(unscaledVal, scale);
	}

	private static void assertValueOfIllegal(final BigDecimal value, final String message)
	{
		assertFails(() ->
			valueOf(value),
			IllegalArgumentException.class,
			message);
	}

	@Test void testDoubleValue()
	{
		assertEqualBits( 2.22, storeOf( 222).doubleValue());
		assertEqualBits(-2.22, storeOf(-222).doubleValue());
		assertEqualBits( 2.2,  storeOf( 220).doubleValue());
		assertEqualBits(-2.2,  storeOf(-220).doubleValue());
		assertEqualBits( 0.0,  storeOf(   0).doubleValue());
	}

	@Test void testBigValue()
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

	@Test void testBigValueAndValueOf()
	{
		assertEquals( p97, valueOf( p97.bigValue()));
		assertEquals( p98, valueOf( p98.bigValue()));
		assertEquals( p99, valueOf( p99.bigValue()));
		assertEquals(mp97, valueOf(mp97.bigValue()));
		assertEquals(mp98, valueOf(mp98.bigValue()));
		assertEquals(mp99, valueOf(mp99.bigValue()));
		assertValueOfIllegal( p99.bigValue().subtract( p98.bigValue()).add( p99.bigValue()), "too big: 92233720368547758.08");
		assertValueOfIllegal(mp99.bigValue().subtract(mp98.bigValue()).add(mp99.bigValue()), "too small: -92233720368547758.08");
	}

	@Test void testAdd()
	{
		assertEquals( 555, storeOf( 333).add(storeOf( 222)).store());
		assertEquals(-111, storeOf(-333).add(storeOf( 222)).store());
		assertEquals( 111, storeOf( 333).add(storeOf(-222)).store());
		assertEquals(-555, storeOf(-333).add(storeOf(-222)).store());
	}

	@Test void testAddNeutral()
	{
		final Price p = storeOf(555);
		assertSame(p, p.add(ZERO));
		assertSame(p, ZERO.add(p));
		assertSame(ZERO, ZERO.add(ZERO));
	}

	@Test void testAddOverflow()
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
		assertFails(() ->
			left.add(right),
			ArithmeticException.class,
			"overflow " + left  + " plus " + right);
	}

	@Test void testSubtract()
	{
		assertEquals( 333, storeOf( 555).subtract(storeOf( 222)).store());
		assertEquals(-333, storeOf(-111).subtract(storeOf( 222)).store());
		assertEquals( 333, storeOf( 111).subtract(storeOf(-222)).store());
		assertEquals(-333, storeOf(-555).subtract(storeOf(-222)).store());
	}

	@Test void testSubtractNeutral()
	{
		final Price p = storeOf(555);
		assertSame(p, p.subtract(ZERO));
		assertEquals(storeOf(-555), ZERO.subtract(p));
		assertSame(ZERO, ZERO.subtract(ZERO));
	}

	@Test void testSubtractOverflow()
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
		assertFails(() ->
			left.subtract(right),
			ArithmeticException.class,
			"overflow " + left  + " minus " + right);
	}

	@Test void testNegate()
	{
		assertEquals(storeOf(-555), storeOf( 555).negate());
		assertEquals(storeOf( 555), storeOf(-555).negate());
		assertSame(ZERO, storeOf(0).negate());
		assertEquals(storeOf(MIN_STORE),  p99.negate());
		assertEquals(storeOf(MAX_STORE), mp99.negate());
	}

	@Test void testMultiplyInt()
	{
		assertEquals( 999, storeOf( 333).multiply( 3).store());
		assertEquals(-999, storeOf(-333).multiply( 3).store());
		assertEquals(-999, storeOf( 333).multiply(-3).store());
		assertEquals( 999, storeOf(-333).multiply(-3).store());
	}

	@Test void testMultiplyIntNeutral()
	{
		final Price p = storeOf(555);
		assertSame(ZERO, p.multiply(0));
		assertSame(p, p.multiply(1));
		assertEquals(storeOf(-555), p.multiply(-1));
	}

	@Test void testMultiplyIntOverflow()
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
		assertFails(() ->
			left.multiply(right),
			ArithmeticException.class,
			"overflow " + left  + " multiply " + right);
	}

	@Test void testMultiplyDouble()
	{
		assertEquals( 999, storeOf( 333).multiply( 3d).store());
		assertEquals(-999, storeOf(-333).multiply( 3d).store());
		assertEquals(-999, storeOf( 333).multiply(-3d).store());
		assertEquals( 999, storeOf(-333).multiply(-3d).store());
	}

	@Test void testMultiplyDoubleNeutral()
	{
		final Price p = storeOf(555);
		assertSame(ZERO, p.multiply(0.0));
		assertSame(p, p.multiply(1.0));
		assertEquals(storeOf(-555), p.multiply(-1.0));
	}

	@Test void testMultiplyDoubleOverflow()
	{
		assertEquals(p49,  p49.multiply(1d));
		assertEquals(p50,  p50.multiply(1d));
		assertEquals(p51,  p51.multiply(1d));
		assertEquals(p98,  p49.multiply(2d));
		assertMultiplyOver(p50,         2d, "too big: 92233720368547758.080");
		assertMultiplyOver(p51,         2d, "too big: 92233720368547758.100");
	}

	private static void assertMultiplyOver(final Price left, final double right, final String message)
	{
		assertFails(() ->
			left.multiply(right),
			IllegalArgumentException.class,
			message);
	}

	@Test void testDivideDouble()
	{
		assertEquals( 333, storeOf( 999).divide( 3d).store());
		assertEquals(-333, storeOf(-999).divide( 3d).store());
		assertEquals(-333, storeOf( 999).divide(-3d).store());
		assertEquals( 333, storeOf(-999).divide(-3d).store());
	}

	@Test void testDivideDoubleNeutral()
	{
		final Price p = storeOf(555);
		assertFails(() ->
			p.divide(0.0),
			IllegalArgumentException.class,
			"Infinity not allowed");
		assertSame(p, p.divide(1.0));
		assertEquals(storeOf(-555), p.divide(-1.0));
	}

	@Test void testDivideDoubleOverflow()
	{
		assertEquals(p49, p49.divide(1d ));
		assertEquals(p50, p50.divide(1d ));
		assertEquals(p51, p51.divide(1d ));
		assertEquals(p98, p49.divide(0.5));
		assertDivideOver( p50,       0.5, "too big: 92233720368547758.08");
		assertDivideOver( p51,       0.5, "too big: 92233720368547758.10");
	}

	private static void assertDivideOver(final Price left, final double right, final String message)
	{
		assertFails(() ->
			left.divide(right),
			IllegalArgumentException.class,
			message);
	}

	@Test void testEqualsOrSame()
	{
		assertSame(         storeOf(  123), storeOf(  123)); // from cache
		assertEqualsAndHash(storeOf(- 123), storeOf(- 123));
		assertEqualsAndHash(storeOf( 4123), storeOf( 4123));
		assertEqualsAndHash(storeOf(-4123), storeOf(-4123));
		assertNotEqualsAndHash(
				storeOf(  123),
				storeOf(  124),
				storeOf(- 123),
				storeOf( 4123),
				storeOf(-4123),
				Integer.valueOf(123),
				Double.valueOf(1.23));
		assertNotSame(storeOf(4123), storeOf(4123));
	}

	@Test void testCompareTo()
	{
		assertEquals(-1, storeOf( 122).compareTo(storeOf( 123)));
		assertEquals( 0, storeOf( 123).compareTo(storeOf( 123)));
		assertEquals( 1, storeOf( 124).compareTo(storeOf( 123)));
		assertEquals( 1, storeOf(-122).compareTo(storeOf(-123)));
		assertEquals( 0, storeOf(-123).compareTo(storeOf(-123)));
		assertEquals(-1, storeOf(-124).compareTo(storeOf(-123)));
	}

	@Test void testToString()
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
		assertToString("-92233720368547758.07", mp99);
		assertToString( "92233720368547758.07",  p99);
	}

	private static void assertToString(final String expected, final Price actual)
	{
		assertToString(expected, actual, expected);
	}

	private static void assertToString(final String expected, final Price actual, final String expectedShort)
	{
		assertEquals(expected, actual.toString(), "toString");
		assertEquals(expectedShort, actual.toStringShort(), "toStringShort");
	}

	@Test void testFormatReal() throws ParseException
	{
		final DecimalFormat en = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		final DecimalFormat de = (DecimalFormat)NumberFormat.getInstance(Locale.GERMAN);
		en.setParseBigDecimal(true);
		de.setParseBigDecimal(true);
		assertParse(en, "0", "0.1", "-0.1", "1.1", "-1.1", "1,234.5", "-1,234.5", "92,233,720,368,547,758.07", "-92,233,720,368,547,758.07");
		assertParse(de, "0", "0,1", "-0,1", "1,1", "-1,1", "1.234,5", "-1.234,5", "92.233.720.368.547.758,07", "-92.233.720.368.547.758,07");
		en.setGroupingUsed(false);
		de.setGroupingUsed(false);
		assertParse(en, "0", "0.1", "-0.1", "1.1", "-1.1",  "1234.5",  "-1234.5",      "92233720368547758.07",      "-92233720368547758.07");
		assertParse(de, "0", "0,1", "-0,1", "1,1", "-1,1",  "1234,5",  "-1234,5",      "92233720368547758,07",      "-92233720368547758,07");
	}

	@Test void testFormatSynthetic() throws ParseException
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

		assertParse(df, "0", "0d1", "m0d1", "1d1", "m1d1", "1g234d5", "m1g234d5", "92g233g720g368g547g758d07", "m92g233g720g368g547g758d07");
		df.setGroupingUsed(false);
		assertParse(df, "0", "0d1", "m0d1", "1d1", "m1d1",  "1234d5",  "m1234d5",      "92233720368547758d07",      "m92233720368547758d07");
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
		assertEquals(zero,     format.format(    0.0), "zero");
		assertEquals(fractPos, format.format(    0.1), "fractPos");
		assertEquals(fractNeg, format.format(   -0.1), "fractNeg");
		assertEquals(smallPos, format.format(    1.1), "smallPos");
		assertEquals(smallNeg, format.format(   -1.1), "smallNeg");
		assertEquals(groupPos, format.format( 1234.5), "groupPos");
		assertEquals(groupNeg, format.format(-1234.5), "groupNeg");

		assertEquals(zero,     storeOf(      0).format(format), "zero");
		assertEquals(fractPos, storeOf(     10).format(format), "fractPos");
		assertEquals(fractNeg, storeOf(    -10).format(format), "fractNeg");
		assertEquals(smallPos, storeOf(    110).format(format), "smallPos");
		assertEquals(smallNeg, storeOf(   -110).format(format), "smallNeg");
		assertEquals(groupPos, storeOf( 123450).format(format), "groupPos");
		assertEquals(groupNeg, storeOf(-123450).format(format), "groupNeg");
		assertEquals( s99,                  p99.format(format),  "p99");
		assertEquals(sm99,                 mp99.format(format), "mp99");

		assertEquals(storeOf(      0), parse(zero    , format), "zero");
		assertEquals(storeOf(     10), parse(fractPos, format), "fractPos");
		assertEquals(storeOf(    -10), parse(fractNeg, format), "fractNeg");
		assertEquals(storeOf(    110), parse(smallPos, format), "smallPos");
		assertEquals(storeOf(   -110), parse(smallNeg, format), "smallNeg");
		assertEquals(storeOf( 123450), parse(groupPos, format), "groupPos");
		assertEquals(storeOf(-123450), parse(groupNeg, format), "groupNeg");
		assertEquals(             p99, parse( s99    , format), "p99");
		assertEquals(            mp99, parse(sm99    , format), "mp99");
	}

	@Test void testParseTooBig()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		assertFails(() ->
			parse("92233720368547758.08", df),
			ParseException.class,
			"too big: 92233720368547758.08");
	}

	@Test void testParseTooSmall()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		assertFails(() ->
			parse("-92233720368547758.08", df),
			ParseException.class,
			"too small: -92233720368547758.08");
	}

	@Test void testParseTooPrecise()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		assertFails(() ->
			parse("1.101", df),
			ParseException.class,
			"Rounding necessary:1.101");
	}

	@Test void testParseBigDecimalNotSupported()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		assertFails(() ->
			parse("1.00", df),
			IllegalArgumentException.class,
			"format does not support BigDecimal");
	}

	@Test void testParseNullSource()
	{
		final DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.ENGLISH);
		df.setParseBigDecimal(true);
		assertFails(() ->
			parse(null, df),
			NullPointerException.class, null);
	}

	@Test void testParseNullFormat()
	{
		assertFails(() ->
			parse("1.00", null),
			NullPointerException.class, null);
	}

	@Test void testSerialization()
	{
		assertEquals(storeOf( 3456), reserialize(storeOf( 3456)));
		assertEquals(storeOf(-3456), reserialize(storeOf(-3456)));
	}

	@Test void testSerializationCache()
	{
		assertEquals(storeOf(  -1), reserialize(storeOf(   -1)));
		assertEquals(storeOf(   0), reserialize(storeOf(    0)));
		assertEquals(storeOf(   1), reserialize(storeOf(    1)));
		assertEquals(storeOf(1000), reserialize(storeOf( 1000)));
		assertEquals(storeOf(1001), reserialize(storeOf( 1001)));

		assertNotSame(storeOf(  -1), reserialize(storeOf(   -1)));
		assertSame   (storeOf(   0), reserialize(storeOf(    0)));
		assertSame   (storeOf(   1), reserialize(storeOf(    1)));
		assertSame   (storeOf(1000), reserialize(storeOf( 1000)));
		assertNotSame(storeOf(1001), reserialize(storeOf( 1001)));
	}

	private static Price reserialize(final Price value)
	{
		return Assert.reserialize(value, 66);
	}

	@Test void testEqualsZero()
	{
		assertEquals(false, storeOf(-1).equalsZero());
		assertEquals(true,  storeOf( 0).equalsZero());
		assertEquals(false, storeOf( 1).equalsZero());
	}

	@Test void testLessThanZero()
	{
		assertEquals(true,  storeOf(-1).lessThanZero());
		assertEquals(false, storeOf( 0).lessThanZero());
		assertEquals(false, storeOf( 1).lessThanZero());
	}

	@Test void testGreaterThanZero()
	{
		assertEquals(false, storeOf(-1).greaterThanZero());
		assertEquals(false, storeOf( 0).greaterThanZero());
		assertEquals(true,  storeOf( 1).greaterThanZero());
	}

	@Test void testLessThanOrEqualZero()
	{
		assertEquals(true,  storeOf(-1).lessThanOrEqualZero());
		assertEquals(true,  storeOf( 0).lessThanOrEqualZero());
		assertEquals(false, storeOf( 1).lessThanOrEqualZero());
	}

	@Test void testGreaterThanOrEqualZero()
	{
		assertEquals(false, storeOf(-1).greaterThanOrEqualZero());
		assertEquals(true,  storeOf( 0).greaterThanOrEqualZero());
		assertEquals(true,  storeOf( 1).greaterThanOrEqualZero());
	}

	@Test void testLessThan()
	{
		assertEquals(true,  storeOf( 122).lessThan(storeOf( 123)));
		assertEquals(false, storeOf( 123).lessThan(storeOf( 123)));
		assertEquals(false, storeOf( 124).lessThan(storeOf( 123)));
		assertEquals(false, storeOf(-122).lessThan(storeOf(-123)));
		assertEquals(false, storeOf(-123).lessThan(storeOf(-123)));
		assertEquals(true,  storeOf(-124).lessThan(storeOf(-123)));
	}

	@Test void testGreaterThan()
	{
		assertEquals(false, storeOf( 122).greaterThan(storeOf( 123)));
		assertEquals(false, storeOf( 123).greaterThan(storeOf( 123)));
		assertEquals(true,  storeOf( 124).greaterThan(storeOf( 123)));
		assertEquals(true,  storeOf(-122).greaterThan(storeOf(-123)));
		assertEquals(false, storeOf(-123).greaterThan(storeOf(-123)));
		assertEquals(false, storeOf(-124).greaterThan(storeOf(-123)));
	}

	@Test void testLessThanOrEqual()
	{
		assertEquals(true,  storeOf( 122).lessThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 123).lessThanOrEqual(storeOf( 123)));
		assertEquals(false, storeOf( 124).lessThanOrEqual(storeOf( 123)));
		assertEquals(false, storeOf(-122).lessThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-123).lessThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-124).lessThanOrEqual(storeOf(-123)));
	}

	@Test void testGreaterThanOrEqual()
	{
		assertEquals(false, storeOf( 122).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 123).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf( 124).greaterThanOrEqual(storeOf( 123)));
		assertEquals(true,  storeOf(-122).greaterThanOrEqual(storeOf(-123)));
		assertEquals(true,  storeOf(-123).greaterThanOrEqual(storeOf(-123)));
		assertEquals(false, storeOf(-124).greaterThanOrEqual(storeOf(-123)));
	}

	@Test void testMin()
	{
		assertEquals(storeOf( 122), storeOf( 122).min(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 123).min(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 124).min(storeOf( 123)));
		assertEquals(storeOf(-123), storeOf(-122).min(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-123).min(storeOf(-123)));
		assertEquals(storeOf(-124), storeOf(-124).min(storeOf(-123)));
	}

	@Test void testMax()
	{
		assertEquals(storeOf( 123), storeOf( 122).max(storeOf( 123)));
		assertEquals(storeOf( 123), storeOf( 123).max(storeOf( 123)));
		assertEquals(storeOf( 124), storeOf( 124).max(storeOf( 123)));
		assertEquals(storeOf(-122), storeOf(-122).max(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-123).max(storeOf(-123)));
		assertEquals(storeOf(-123), storeOf(-124).max(storeOf(-123)));
	}

	@Test void testGrossToNetPercent()
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

		assertFails(() ->
			ZERO.grossToNetPercent(-1),
			IllegalArgumentException.class,
			"rate must not be negative, but was -1");
		{
			final Price p = storeOf( 126);
			assertSame(p, p.grossToNetPercent(0));
		}
	}

	@Test void testGrossToTaxPercent()
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

		assertFails(() ->
			ZERO.grossToTaxPercent(-1),
			IllegalArgumentException.class,
			"rate must not be negative, but was -1");
	}

	@Test void testGrossToTaxPercentDouble()
	{
		assertEquals(storeOf( 26), storeOf( 126).grossToTaxPercent(26d));
		assertEquals(storeOf(-26), storeOf(-126).grossToTaxPercent(26d));
		assertEquals(storeOf(  0), storeOf(   0).grossToTaxPercent(26d));
		assertEquals(storeOf(  0), storeOf(   0).grossToTaxPercent(0d));
		assertEquals(storeOf(  0), storeOf( 126).grossToTaxPercent(0d));
		assertEquals(storeOf(  0), storeOf(-126).grossToTaxPercent(0d));
		assertEquals(storeOf( 58), storeOf( 120).grossToTaxPercent(92d));
		assertEquals(storeOf(-58), storeOf(-120).grossToTaxPercent(92d));
		assertEquals(storeOf( 57), storeOf( 120).grossToTaxPercent(92d, HALF_DOWN));
		assertEquals(storeOf(-57), storeOf(-120).grossToTaxPercent(92d, HALF_DOWN));

		assertFails(() ->
			ZERO.grossToTaxPercent(-0.001d),
			IllegalArgumentException.class,
			"rate must not be negative, but was -0.001");
	}
}

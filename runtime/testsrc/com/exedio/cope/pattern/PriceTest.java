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

import static com.exedio.cope.pattern.Price.ZERO;
import static com.exedio.cope.pattern.Price.storeOf;
import static com.exedio.cope.pattern.Price.valueOf;

import java.math.BigDecimal;

import com.exedio.cope.junit.CopeAssert;

public final class PriceTest extends CopeAssert
{
	public void testIt()
	{
		assertEquals(5, new Price(5).store());
		assertEquals(0, new Price(0).store());
	}
	
	public void testZero()
	{
		assertEquals(0, ZERO.store());
		assertEquals(0.0, ZERO.doubleValue());
	}
	
	public void testStoreOfInt()
	{
		assertEquals(storeOf(1), storeOf(1));
		assertNotSame(storeOf(1), storeOf(1));
		assertEquals(storeOf(-1), storeOf(-1));
		assertSame(ZERO, storeOf(0));
	}
	
	public void testStoreOfInteger()
	{
		assertEquals( 5, storeOf(new Integer( 5)).store());
		assertEquals(-5, storeOf(new Integer(-5)).store());
		assertSame(ZERO, storeOf(new Integer(0)));
		assertEquals(null, storeOf((Integer)null));
	}
	
	public void testValueOfDouble()
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
		assertEquals( 302, valueOf( 3.016  ).store()); // because the next digit (6) is 6 or more)
		assertEquals( 301, valueOf( 3.013  ).store()); // because the next digit (3) is 4 or less)
		assertEquals( 302, valueOf( 3.015  ).store()); // because the next digit is 5, and the hundredths digit (1) is odd)
		assertEquals( 304, valueOf( 3.045  ).store()); // because the next digit is 5, and the hundredths digit (4) is even)
		assertEquals( 305, valueOf( 3.04501).store()); // because the next digit is 5, but it is followed by non-zero digits)
		assertEquals(-302, valueOf(-3.016  ).store());
		assertEquals(-301, valueOf(-3.013  ).store());
		assertEquals(-302, valueOf(-3.015  ).store());
		assertEquals(-304, valueOf(-3.045  ).store());
		assertEquals(-305, valueOf(-3.04501).store());
		
		assertEquals(Integer.MAX_VALUE, valueOf(Integer.MAX_VALUE/100d).store());
		assertEquals(Integer.MIN_VALUE, valueOf(Integer.MIN_VALUE/100d).store());
		assertSame(ZERO, valueOf( 0.0));
		assertSame(ZERO, valueOf(-0.0));
		try
		{
			valueOf((Integer.MAX_VALUE/100d) + 0.01);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("too big: 2.147483648E7", e.getMessage());
		}
		try
		{
			valueOf((Integer.MIN_VALUE/100d) - 0.01);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("too small: -2.1474836490000002E7", e.getMessage());
		}
		try
		{
			valueOf(Double.NaN);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("NaN not allowed", e.getMessage());
		}
		try
		{
			valueOf(Double.NEGATIVE_INFINITY);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Infinity not allowed", e.getMessage());
		}
		try
		{
			valueOf(Double.POSITIVE_INFINITY);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Infinity not allowed", e.getMessage());
		}
	}
	
	public void testValueOfBigDecimal()
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
		catch(IllegalArgumentException e)
		{
			assertEquals("too big: 21474836.48", e.getMessage());
		}
		try
		{
			valueOf(bd(Integer.MIN_VALUE, 2).subtract(bd(1, 2)));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("too small: -21474836.49", e.getMessage());
		}
		try
		{
			valueOf((BigDecimal)null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	public void testDoubleValue()
	{
		assertEquals( 2.22, storeOf( 222).doubleValue());
		assertEquals(-2.22, storeOf(-222).doubleValue());
		assertEquals( 2.2,  storeOf( 220).doubleValue());
		assertEquals(-2.2,  storeOf(-220).doubleValue());
		assertEquals( 0.0,  storeOf(   0).doubleValue());
	}
	
	public void testBigValue()
	{
		assertEquals(bd( 222, 2), storeOf( 222).bigValue());
		assertEquals(bd(-222, 2), storeOf(-222).bigValue());
		assertEquals(bd( 22,  1), storeOf( 220).bigValue());
		assertEquals(bd(-22,  1), storeOf(-220).bigValue());
		assertEquals(bd( 2,   0), storeOf( 200).bigValue());
		assertEquals(bd(-2,   0), storeOf(-200).bigValue());
		assertEquals(bd( 0,   0), storeOf(   0).bigValue());
	}
	
	public void testAdd()
	{
		assertEquals( 555, storeOf( 333).add(storeOf( 222)).store());
		assertEquals(-111, storeOf(-333).add(storeOf( 222)).store());
		assertEquals( 111, storeOf( 333).add(storeOf(-222)).store());
		assertEquals(-555, storeOf(-333).add(storeOf(-222)).store());
	}
	
	public void testMultiplyInt()
	{
		assertEquals( 999, storeOf( 333).multiply( 3).store());
		assertEquals(-999, storeOf(-333).multiply( 3).store());
		assertEquals(-999, storeOf( 333).multiply(-3).store());
		assertEquals( 999, storeOf(-333).multiply(-3).store());
	}
	
	public void testEquals()
	{
		assertEquals(storeOf( 123), storeOf( 123));
		assertEquals(storeOf(-123), storeOf(-123));
		assertFalse(storeOf(123).equals(storeOf( 124)));
		assertFalse(storeOf(123).equals(storeOf(-123)));
		assertNotSame(storeOf(123), storeOf(123));
		assertFalse(storeOf(123).equals(new Integer(123)));
		assertFalse(storeOf(123).equals(new Double(1.23)));
		assertFalse(storeOf(123).equals(null));
	}
	
	public void testHashCode()
	{
		assertEquals(storeOf( 123).hashCode(), storeOf( 123).hashCode());
		assertEquals(storeOf(-123).hashCode(), storeOf(-123).hashCode());
		assertFalse(storeOf(123).hashCode()==storeOf( 124).hashCode());
		assertFalse(storeOf(123).hashCode()==storeOf(-123).hashCode());
	}
	
	public void testCompareTo()
	{
		assertEquals(-1, storeOf(122).compareTo(storeOf(123)));
		assertEquals( 0, storeOf(123).compareTo(storeOf(123)));
		assertEquals( 1, storeOf(124).compareTo(storeOf(123)));
		assertEquals( 1, storeOf(-122).compareTo(storeOf(-123)));
		assertEquals( 0, storeOf(-123).compareTo(storeOf(-123)));
		assertEquals(-1, storeOf(-124).compareTo(storeOf(-123)));
	}
	
	public void testToString()
	{
		assertEquals( "1.23", storeOf( 123).toString());
		assertEquals("-1.23", storeOf(-123).toString());
		assertEquals( "1.03", storeOf( 103).toString());
		assertEquals("-1.03", storeOf(-103).toString());
		assertEquals( "0.23", storeOf(  23).toString());
		assertEquals("-0.23", storeOf( -23).toString());
		assertEquals( "0.03", storeOf(   3).toString());
		assertEquals("-0.03", storeOf(  -3).toString());
		assertEquals( "0.00", storeOf(   0).toString());
		
		// serialization
		assertEquals(storeOf( 3456), reserialize(storeOf( 3456), 62));
		assertEquals(storeOf(-3456), reserialize(storeOf(-3456), 62));
	}
	
	private static final BigDecimal bd(final long unscaledVal, final int scale)
	{
		return BigDecimal.valueOf(unscaledVal, scale);
	}
}

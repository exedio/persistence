/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.Price.valueOf;
import static com.exedio.cope.pattern.Price.ZERO;

import com.exedio.cope.junit.CopeAssert;

public final class PriceTest extends CopeAssert
{
	public void testIt()
	{
		assertEquals(5, new Price(5).value());
		assertEquals(0, new Price(0).value());
		
		// ZERO
		assertEquals(0, ZERO.value());
		assertEquals(0.0, ZERO.doubleValue());
		
		// valueOf(int)
		assertEquals(valueOf(1), valueOf(1));
		assertNotSame(valueOf(1), valueOf(1));
		assertSame(ZERO, valueOf(0));
		
		// valueOf(Integer)
		assertEquals(5, valueOf(new Integer(5)).value());
		assertSame(ZERO, valueOf(new Integer(0)));
		assertEquals(null, valueOf((Integer)null));
		
		// valueof(double)
		assertEquals(222, valueOf(2.22).value());
		assertEquals(220, valueOf(2.2).value());
		assertSame(ZERO, valueOf(0.0));
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
		// TODO test and implement proper rounding
		
		// doubleValue
		assertEquals(2.22, valueOf(222).doubleValue());
		assertEquals(2.2, valueOf(220).doubleValue());
		assertEquals(0.0, valueOf(0).doubleValue());
		
		// add
		assertEquals(555, valueOf(333).add(valueOf(222)).value());
		
		// multiply
		assertEquals(999, valueOf(333).multiply(3).value());
		
		// equals
		assertEquals(valueOf(123), valueOf(123));
		assertFalse(valueOf(123).equals(valueOf(124)));
		assertNotSame(valueOf(123), valueOf(123));
		assertFalse(valueOf(123).equals(new Integer(123)));
		assertFalse(valueOf(123).equals(new Double(1.23)));
		assertFalse(valueOf(123).equals(null));
		
		// hashCode
		assertEquals(valueOf(123).hashCode(), valueOf(123).hashCode());
		assertFalse(valueOf(123).hashCode()==valueOf(124).hashCode());
		
		// toString()
		assertEquals("1.23", valueOf(123).toString());
		assertEquals("1.03", valueOf(103).toString());
		assertEquals("0.23", valueOf( 23).toString());
		assertEquals("0.03", valueOf(  3).toString());
		assertEquals("0.00", valueOf(  0).toString());
	}
}

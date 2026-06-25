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

package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NumberFieldTest
{
	@Test void testAllowsZeroValueMin()
	{
		assertEquals(true, intF.range(  -1, 10).allowsZeroValue());
		assertEquals(true, lngF.range(  -1, 10).allowsZeroValue());
		assertEquals(true, dblF.range(-0.1, 10).allowsZeroValue());

		assertEquals(true, intF.range(0, 10).allowsZeroValue());
		assertEquals(true, lngF.range(0, 10).allowsZeroValue());
		assertEquals(true, dblF.range(0, 10).allowsZeroValue());

		assertEquals(false, intF.range(  1, 10).allowsZeroValue());
		assertEquals(false, lngF.range(  1, 10).allowsZeroValue());
		assertEquals(false, dblF.range(0.1, 10).allowsZeroValue());
	}

	@Test void testAllowsZeroValueMax()
	{
		assertEquals(true, intF.range(-10,   1).allowsZeroValue());
		assertEquals(true, lngF.range(-10,   1).allowsZeroValue());
		assertEquals(true, dblF.range(-10, 0.1).allowsZeroValue());

		assertEquals(true, intF.range(-10, 0).allowsZeroValue());
		assertEquals(true, lngF.range(-10, 0).allowsZeroValue());
		assertEquals(true, dblF.range(-10, 0).allowsZeroValue());

		assertEquals(false, intF.range(-10,   -1).allowsZeroValue());
		assertEquals(false, lngF.range(-10,   -1).allowsZeroValue());
		assertEquals(false, dblF.range(-10, -0.1).allowsZeroValue());
	}

	private static final IntegerField intF = new IntegerField();
	private static final    LongField lngF = new    LongField();
	private static final  DoubleField dblF = new  DoubleField();
}

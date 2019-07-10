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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.PrimitiveUtil.toPrimitive;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class PrimitiveUtilTest
{
	@Test void testToPrimitiveWrapper()
	{
		assertSame(boolean.class, toPrimitive(Boolean  .class));
		assertSame(char   .class, toPrimitive(Character.class));
		assertSame(byte   .class, toPrimitive(Byte     .class));
		assertSame(short  .class, toPrimitive(Short    .class));
		assertSame(int    .class, toPrimitive(Integer  .class));
		assertSame(long   .class, toPrimitive(Long     .class));
		assertSame(float  .class, toPrimitive(Float    .class));
		assertSame(double .class, toPrimitive(Double   .class));
	}
	@Test void testToPrimitivePrimitive()
	{
		assertSame(null, toPrimitive(boolean.class));
		assertSame(null, toPrimitive(char   .class));
		assertSame(null, toPrimitive(byte   .class));
		assertSame(null, toPrimitive(short  .class));
		assertSame(null, toPrimitive(int    .class));
		assertSame(null, toPrimitive(long   .class));
		assertSame(null, toPrimitive(float  .class));
		assertSame(null, toPrimitive(double .class));
	}
	@Test void testToPrimitiveOther()
	{
		assertSame(null, toPrimitive(String.class));
		assertSame(null, toPrimitive(Number.class));
		assertSame(null, toPrimitive(Map   .class));
	}
	@Test void testToPrimitiveNull()
	{
		assertSame(null, toPrimitive(null));
	}
}

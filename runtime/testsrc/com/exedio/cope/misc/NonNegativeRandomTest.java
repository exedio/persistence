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

import static com.exedio.cope.misc.NonNegativeRandom.nextLong;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

public class NonNegativeRandomTest
{
	@Test void test()
	{
		assertEquals( 0, nextLong(new MyRandom(  0)));
		assertEquals(33, nextLong(new MyRandom( 33)));
		assertEquals(33, nextLong(new MyRandom(-33)));
		assertEquals(MAX_VALUE, nextLong(new MyRandom(MAX_VALUE)));
		assertEquals(MAX_VALUE-1, nextLong(new MyRandom(MAX_VALUE-1)));
		assertEquals(MAX_VALUE-2, nextLong(new MyRandom(MAX_VALUE-2)));
		assertEquals(MAX_VALUE,   nextLong(new MyRandom(MIN_VALUE)));
		assertEquals(MAX_VALUE,   nextLong(new MyRandom(MIN_VALUE+1)));
		assertEquals(MAX_VALUE-1, nextLong(new MyRandom(MIN_VALUE+2)));
	}

	static final class MyRandom extends Random
	{
		private static final long serialVersionUID = - 1143497430954927734L;
		final long result;

		MyRandom(final long result)
		{
			this.result = result;
		}

		@Override public long nextLong()
		{
			return result;
		}
	}
}

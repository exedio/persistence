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

import java.util.Random;
import org.junit.jupiter.api.Test;

public class DefaultToReuseTest
{
	@Test void testRandom()
	{
		final LongField f = new LongField().defaultToRandom(new Random());
		assertEquals(true, f.isMandatory());
		assertEquals(true, f.hasDefault());
		assertEquals(Long.MIN_VALUE, f.getMinimum());

		{
			final LongField f2 = f.optional();
			assertEquals(false, f2.isMandatory());
			assertEquals(true,  f2.hasDefault());
			assertEquals(Long.MIN_VALUE, f2.getMinimum());
		}
		{
			final LongField f2 = f.min(0);
			assertEquals(true, f2.isMandatory());
			assertEquals(true, f2.hasDefault());
			assertEquals(0, f2.getMinimum());
		}
	}

	@Test void testNext()
	{
		final IntegerField f = new IntegerField().defaultToNext(55);
		assertEquals(true, f.isMandatory());
		assertEquals(true, f.hasDefault());
		assertEquals(true, f.isDefaultNext());
		assertEquals(55, f.getDefaultNextStartX());
		assertEquals(Integer.MIN_VALUE, f.getMinimum());

		{
			final IntegerField f2 = f.optional();
			assertEquals(false, f2.isMandatory());
			assertEquals(true,  f2.hasDefault());
			assertEquals(true,  f2.isDefaultNext());
			assertEquals(55, f2.getDefaultNextStartX());
			assertEquals(Integer.MIN_VALUE, f2.getMinimum());
		}
		{
			final IntegerField f2 = f.min(0);
			assertEquals(true, f2.isMandatory());
			assertEquals(true, f2.hasDefault());
			assertEquals(true, f2.isDefaultNext());
			assertEquals(55, f2.getDefaultNextStartX());
			assertEquals(0, f2.getMinimum());
		}
	}
}

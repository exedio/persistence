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

package com.exedio.cope.tojunit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


public final class EqualsAssert
{
	public static void assertEqualsAndHash(final Object left, final Object right)
	{
		assertNotSame(left, right);
		assertEquals(left, right);
		assertEquals(right, left);
		assertEquals(left.hashCode(), right.hashCode());
		assertEqualsSpecial(left);
		assertEqualsSpecial(right);
	}

	public static void assertNotEqualsAndHash(final Object... objects)
	{
		for(int i = 0; i<objects.length; i++)
		{
			final Object left = objects[i];

			for(int j = 0; j<objects.length; j++)
			{
				final Object right = objects[j];

				if(i==j)
				{
					assertSame(left, right);
					assertEqualsSpecial(left);
				}
				else
				{
					assertFalse(left.equals(right),                ""+i+'/'+j);
					assertFalse(right.equals(left),                ""+i+'/'+j);
					assertFalse(left.hashCode()==right.hashCode(), ""+i+'/'+j);
				}
			}
			assertEqualsSpecial(left);
		}
	}

	@SuppressWarnings({"EqualsWithItself", "ObjectEqualsNull"})
	private static void assertEqualsSpecial(final Object object)
	{
		assertTrue (object.equals(object));
		assertFalse(object.equals(null));
		assertFalse(object.equals(SOME_OBJECT));
		assertFalse(object.equals("hello7777"));
		assertFalse(object.equals(Integer.valueOf(-7777)));
	}

	private static final Object SOME_OBJECT = new Object();


	public static void assertEqualBits(final double a, final double b)
	{
		assertEquals(Double.valueOf(a), Double.valueOf(b));
	}


	private EqualsAssert()
	{
		// prevent instantiation
	}
}

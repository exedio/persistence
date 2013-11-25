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

package com.exedio.cope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class EqualsAssert
{
	public static void assertEqualsAndHash(final Object expected, final Object actual)
	{
		assertEquals(expected, actual);
		assertEquals(actual, expected);
		assertEquals(expected.hashCode(), actual.hashCode());
		assertEqualsSpecial(expected);
		assertEqualsSpecial(actual);
	}

	public static void assertNotEqualsAndHash(final Object... objects)
	{
		for(int i = 0; i<objects.length; i++)
		{
			final Object expected = objects[i];

			lj: for(int j = 0; j<objects.length; j++)
			{
				final Object actual = objects[j];

				if(i==j)
					continue lj;

				assertTrue(""+i+'/'+j, !expected.equals(actual));
				assertTrue(""+i+'/'+j, !actual.equals(expected));
				assertTrue(""+i+'/'+j, expected.hashCode()!=actual.hashCode());
			}
			assertEqualsSpecial(expected);
		}
	}

	@SuppressFBWarnings("EC_NULL_ARG")
	private static void assertEqualsSpecial(final Object object)
	{
		assertTrue(object.equals(object));
		assertTrue(!object.equals(null));
		assertTrue(!object.equals(SOME_OBJECT));
		assertFalse(object.equals("hello7777"));
		assertFalse(object.equals(Integer.valueOf(-7777)));
	}

	private static final Object SOME_OBJECT = new Object();

	private EqualsAssert()
	{
		// prevent instantiation
	}
}

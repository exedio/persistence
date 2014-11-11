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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class EqualsAssertUtil
{
	public static void assertEqualsAndHash(final Object left, final Object right)
	{
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
					assertFalse(""+i+'/'+j, left.equals(right));
					assertFalse(""+i+'/'+j, right.equals(left));
					assertFalse(""+i+'/'+j, left.hashCode()==right.hashCode());
				}
			}
			assertEqualsSpecial(left);
		}
	}

	@SuppressFBWarnings({"EC_NULL_ARG","SA_LOCAL_SELF_COMPARISON"})
	private static void assertEqualsSpecial(final Object object)
	{
		assertTrue (object.equals(object));
		assertFalse(object.equals(null));
		assertFalse(object.equals(SOME_OBJECT));
		assertFalse(object.equals("hello7777"));
		assertFalse(object.equals(Integer.valueOf(-7777)));
	}

	private static final Object SOME_OBJECT = new Object();

	private EqualsAssertUtil()
	{
		// prevent instantiation
	}
}

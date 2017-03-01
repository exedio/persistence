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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.CopeName;
import org.junit.Test;

public class LocalizationKeysTest
{
	@Test public void testTopClass()
	{
		assertIt(LocalizationKeysClass    .class, PACK + "LocalizationKeysClass",       "LocalizationKeysClass");
		assertIt(LocalizationKeysClassPure.class, PACK + "LocalizationKeysClassActual", "LocalizationKeysClassActual");
	}

	@Test public void testTopEnum()
	{
		assertIt(LocalizationKeysEnum.x,     PACK + "LocalizationKeysEnum.x",       "LocalizationKeysEnum.x");
		assertIt(LocalizationKeysEnumPure.x, PACK + "LocalizationKeysEnumActual.x", "LocalizationKeysEnumActual.x");
	}

	private final String PACK = LocalizationKeysTest.class.getPackage().getName() + '.';

	@Test public void testDefaultPackage() throws ClassNotFoundException
	{
		assertIt(Class.forName(
				"LocalizationKeysClassInDefaultPackage"),
				"LocalizationKeysClassInDefaultPackage");
	}


	class InnerLoc
	{
		class Innermost
		{
			// empty
		}
		@CopeName("InnermostActual")
		class InnermostPure
		{
			// empty
		}
	}
	@CopeName("InnerActual")
	class InnerPure
	{
		// empty
	}

	@Test public void testInnerClass()
	{
		assertIt(InnerLoc              .class, PREFIX1 + "InnerLoc",                 PREFIX2 + "InnerLoc");
		assertIt(InnerLoc.Innermost    .class, PREFIX1 + "InnerLoc.Innermost",       PREFIX2 + "InnerLoc.Innermost");
		assertIt(InnerLoc.InnermostPure.class, PREFIX1 + "InnerLoc.InnermostActual", PREFIX2 + "InnerLoc.InnermostActual");
		assertIt(InnerPure             .class, PREFIX1 + "InnerActual",              PREFIX2 + "InnerActual");
	}

	private static void assertIt(final Class<?> clazz, final String... expected)
	{
		assertEqualsUnmodifiable(asList(
				expected),
				LocalizationKeys.get(clazz));
	}


	enum InnerEnum
	{
		normal,
		body
		{
			@Override void method()
			{
				// empty
			}
		},
		@CopeName("actual") pure;

		void method()
		{
			// empty
		}
	}

	@CopeName("InnerEnumActual")
	enum InnerEnumPure
	{
		x
	}

	@Test public void testInnerEnum()
	{
		assertIt(InnerEnum.normal, PREFIX1 + "InnerEnum.normal",  PREFIX2 + "InnerEnum.normal");
		assertIt(InnerEnum.body,   PREFIX1 + "InnerEnum.body",    PREFIX2 + "InnerEnum.body");
		assertIt(InnerEnum.pure,   PREFIX1 + "InnerEnum.actual",  PREFIX2 + "InnerEnum.actual");
		assertIt(InnerEnumPure.x,  PREFIX1 + "InnerEnumActual.x", PREFIX2 + "InnerEnumActual.x");
	}

	private static void assertIt(final Enum<?> value, final String... expected)
	{
		assertEqualsUnmodifiable(asList(
				expected),
				LocalizationKeys.get(value));
	}


	private static final String PREFIX1 = LocalizationKeysTest.class.getName()       + '.';
	private static final String PREFIX2 = LocalizationKeysTest.class.getSimpleName() + '.';


	@Test public void testNullClass()
	{
		try
		{
			LocalizationKeys.get((Class<?>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	@Test public void testNullEnum()
	{
		try
		{
			LocalizationKeys.get((Enum<?>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}

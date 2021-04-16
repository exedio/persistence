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

import static com.exedio.cope.DefaultChangeHook.factory;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class DefaultChangeHookTest
{
	@Test void testFactoryToString()
	{
		assertEquals("com.exedio.cope.DefaultChangeHook", factory.toString());
	}

	@Test void testFactorySingleton()
	{
		assertSame(factory, factory());
	}

	@Test void testFactoryModelNull()
	{
		try
		{
			factory.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
	}


	@Test void beforeNewNotStatic()
	{
		try
		{
			factory.create(modelBeforeNewNotStatic);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"method beforeNewCopeItem(SetValue[]) " +
					"in class " + BeforeNewNotStatic.class.getName() +
					" must be static",
					e.getMessage());
		}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class BeforeNewNotStatic extends Item
	{
		@SuppressWarnings("MethodMayBeStatic")
		private SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
		{
			throw new AssertionError();
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BeforeNewNotStatic> TYPE = com.exedio.cope.TypesBound.newType(BeforeNewNotStatic.class);

		@com.exedio.cope.instrument.Generated
		protected BeforeNewNotStatic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	private static final Model modelBeforeNewNotStatic = Model.builder().
			add(BeforeNewNotStatic.TYPE).
			changeHooks().
			build();


	@Test void beforeNewWrongReturn()
	{
		try
		{
			factory.create(modelBeforeNewWrongReturn);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"method beforeNewCopeItem(SetValue[]) " +
					"in class " + BeforeNewWrongReturn.class.getName() +
					" must return SetValue[], " +
					"but returns java.lang.String", e.getMessage());
		}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class BeforeNewWrongReturn extends Item
	{
		private static String beforeNewCopeItem(final SetValue<?>[] setValues)
		{
			throw new AssertionError();
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BeforeNewWrongReturn> TYPE = com.exedio.cope.TypesBound.newType(BeforeNewWrongReturn.class);

		@com.exedio.cope.instrument.Generated
		protected BeforeNewWrongReturn(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	private static final Model modelBeforeNewWrongReturn = Model.builder().
			add(BeforeNewWrongReturn.TYPE).
			changeHooks().
			build();


	private static final ChangeHook.Factory factory = factory();
}

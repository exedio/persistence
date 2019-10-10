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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.ChangeHooks.EMPTY;
import static com.exedio.cope.misc.ChangeHooks.cascade;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.ChangeHook.Factory;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorChangeHook;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class ChangeHooksTest
{
	@Test void testEmptyToString()
	{
		assertEquals("empty", EMPTY.toString());
		assertEquals("com.exedio.cope.misc.ChangeHooks$EmptyFactory", EMPTY.getClass().getName());
	}

	@Test void testEmpty()
	{
		final ChangeHook h = EMPTY.create(MODEL);
		final SetValue<?>[] sv = new SetValue<?>[]{};
		assertSame(sv, h.beforeNew(null, sv));
		h.afterNew(null);
		assertSame(sv, h.beforeSet(null, sv));
		h.beforeDelete(null);
		assertEquals("empty", h.toString());
		assertEquals("com.exedio.cope.misc.ChangeHooks$Empty", h.getClass().getName());
	}

	@Test void testEmptyModelNull()
	{
		try
		{
			EMPTY.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
	}

	@Test void testEmptySingleton()
	{
		assertSame(EMPTY.create(MODEL), EMPTY.create(MODEL));
	}


	@Test void testCascadeEmpty()
	{
		assertSame(EMPTY, cascade());
	}

	@Test void testCascadeSingle()
	{
		final Factory h1 = model -> new AssertionErrorChangeHook();
		assertSame(h1, cascade(h1));
	}

	@Test void testCascade()
	{
		final Factory f1 = factoryToString("hook1");
		final Factory f2 = factoryToString("hook2");
		final Factory fc = cascade(f1, f2);
		assertEquals("toStringFactory(hook1) / toStringFactory(hook2)", fc.toString());
		assertEquals("com.exedio.cope.misc.ChangeHooks$CascadeFactory", fc.getClass().getName());

		final ChangeHook hc = fc.create(MODEL);
		assertEquals("toStringHook(hook1) / toStringHook(hook2)", hc.toString());
		assertSame(CascadeChangeHook.class, hc.getClass());
	}

	@Test void testCascadeNullHook()
	{
		try
		{
			cascade((Factory[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hooks", e.getMessage());
		}
	}

	@Test void testCascadeNullHooks()
	{
		try
		{
			cascade((Factory)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("hooks[0]", e.getMessage());
		}
	}

	@Test void testCascadeModelNull()
	{
		final Factory fc = cascade(
				factoryToString("hook1"),
				factoryToString("hook2"));
		try
		{
			fc.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
	}

	@Test void testCascadeApplyReturnsNull()
	{
		final Factory fc = cascade(
				factoryToString("hook1"),
				new Factory()
				{
					@SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION") // OK: testing bad behaviour
					@Override public ChangeHook create(final Model model)
					{
						return null;
					}
					@Override public String toString()
					{
						return "toStringFactoryReturnsNull";
					}
				},
				factoryToString("hook2"));

		assertEquals("toStringFactory(hook1) / toStringFactoryReturnsNull / toStringFactory(hook2)", fc.toString());
		try
		{
			fc.create(MODEL);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ChangeHook.Factory returned null: toStringFactoryReturnsNull", e.getMessage());
		}
	}


	public static Factory factoryToString(final String name)
	{
		return new Factory()
		{
			@Override
			public ChangeHook create(final Model model)
			{
				assertNotNull(model);
				return new ToStringChangeHook(name);
			}

			@Override
			public String toString()
			{
				return "toStringFactory(" + name + ")";
			}
		};
	}

	private static final class ToStringChangeHook extends AssertionErrorChangeHook
	{
		private final String name;

		private ToStringChangeHook(final String name)
		{
			this.name = name;
			assertNotNull(name);
		}

		@Override
		public String toString()
		{
			return "toStringHook(" + name + ")";
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

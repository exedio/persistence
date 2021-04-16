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
import static com.exedio.cope.misc.ChangeHooks.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorChangeHook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CascadeChangeHookSetValuesNullTest
{
	private ChangeHook hook;

	@BeforeEach void before()
	{
		hook = cascade(
				model -> new MyHook(0, false),
				model -> new MyHook(1, true),
				model -> new MyHook(2, false)).
				create(MODEL);
	}

	@Test void testBeforeNewNull()
	{
		try
		{
			hook.beforeNew(null, new SetValue<?>[]{});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("result of ChangeHook#beforeNew: MyHook(1)", e.getMessage());
		}
	}

	@Test void testBeforeSetNull()
	{
		try
		{
			hook.beforeSet(null, new SetValue<?>[]{});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("result of ChangeHook#beforeSet: MyHook(1)", e.getMessage());
		}
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static final class MyHook extends AssertionErrorChangeHook
	{
		private final int id;
		private final boolean returnNull;

		MyHook(final int id, final boolean returnNull)
		{
			this.id = id;
			this.returnNull = returnNull;
		}

		@Override public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] sv)
		{
			return returnNull ? null : sv;
		}

		@Override public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] sv)
		{
			return returnNull ? null : sv;
		}

		@Override
		public String toString()
		{
			return "MyHook(" + id + ")";
		}
	}
}

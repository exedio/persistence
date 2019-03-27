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

import static com.exedio.cope.RuntimeAssert.activate;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.ChangeHooks.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.ChangeHook;
import com.exedio.cope.ChangeHook.Factory;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorChangeHook;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CascadeChangeHookTest
{
	private ChangeHook hook;
	private MyItem item;

	@BeforeEach void before()
	{
		final Factory fc = cascade(
				model -> new MyHook(model, 1),
				model -> new MyHook(model, 2));
		assertEquals("com.exedio.cope.misc.ChangeHooks$CascadeFactory", fc.getClass().getName());
		hook = fc.create(MODEL);
		item = activate(MyItem.TYPE, 55);
	}

	@Test void testToString()
	{
		assertEquals("MyHook(1) / MyHook(2)", hook.toString());
		assertSame(CascadeChangeHook.class, hook.getClass());
	}

	@Test void testBeforeNew()
	{
		hook.beforeNew(MyItem.TYPE, new SetValue<?>[]{MyItem.field.map("origin")});
		assertEvents("beforeNew1(origin)", "beforeNew2(origin / beforeNew1)");
	}

	@Test void testAfterNew()
	{
		hook.afterNew(item);
		assertEvents("afterNew1(MyItem-55)", "afterNew2(MyItem-55)");
	}

	@Test void testBeforeSet()
	{
		hook.beforeSet(item, new SetValue<?>[]{MyItem.field.map("origin")});
		assertEvents("beforeSet1(MyItem-55,origin)", "beforeSet2(MyItem-55,origin / beforeSet1)");
	}

	@Test void testBeforeDelete()
	{
		hook.beforeDelete(item);
		assertEvents("beforeDelete1(MyItem-55)", "beforeDelete2(MyItem-55)");
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore
		static final StringField field = new StringField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static final class MyHook extends AssertionErrorChangeHook
	{
		private final int id;

		MyHook(final Model model, final int id)
		{
			assertSame(MODEL, model);
			this.id = id;
		}

		@Override public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] sv)
		{
			assertSame(MyItem.TYPE, type);
			final String value = value(sv);
			addEvent("beforeNew" + id + "(" + value + ")");
			sv[0] = MyItem.field.map(value + " / beforeNew" + id);
			return sv;
		}

		@Override public void afterNew(final Item item)
		{
			addEvent("afterNew" + id + "(" + item + ")");
		}

		@Override public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] sv)
		{
			final String value = value(sv);
			addEvent("beforeSet" + id + "(" + item + "," + value + ")");
			sv[0] = MyItem.field.map(value + " / beforeSet" + id);
			return sv;
		}

		private static String value(final SetValue<?>[] sv)
		{
			assertEquals(1, sv.length);
			assertSame(MyItem.field, sv[0].settable);
			return (String)sv[0].value;
		}

		@Override public void beforeDelete(final Item item)
		{
			addEvent("beforeDelete" + id + "(" + item + ")");
		}

		@Override
		public String toString()
		{
			return "MyHook(" + id + ")";
		}
	}

	private static final ArrayList<String> events = new ArrayList<>();

	@BeforeEach final void cleanEvents()
	{
		events.clear();
	}

	protected static void assertEvents(final String... logs)
	{
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(Arrays.asList(logs), events);
		events.clear();
	}

	static final void addEvent(final String event)
	{
		assertNotNull(event);
		events.add(event);
	}
}

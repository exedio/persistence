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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.junit.AssertionErrorChangeHook;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;

@WrapInterim
public abstract class ChangeHookAbstractTest extends TestWithEnvironment
{
	protected ChangeHookAbstractTest(final Model model)
	{
		super(model);
	}

	static final class MyHook extends AssertionErrorChangeHook
	{
		private final Model model;
		private final Type<?> type;
		private final StringField field;

		MyHook(final Model model)
		{
			assertNotNull(model);
			this.model = model;

			type = model.getType("MyItem");
			assertNotNull(type);
			field = (StringField)type.getFeature("field");
			assertNotNull(field);
		}

		@Override public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] sv)
		{
			assertSame(this.type, type);
			final String value = value(sv);
			addEvent("Hook#beforeNew(" + value + ")");
			sv[0] = SetValue.map(field, value + " / Hook#beforeNew");
			return sv;
		}

		@Override public void afterNew(final Item item)
		{
			addEvent("Hook#afterNew(" + item + ")");
		}

		@Override public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] sv)
		{
			final String value = value(sv);
			addEvent("Hook#beforeSet(" + item + "," + value + ")");
			sv[0] = SetValue.map(field, value + " / Hook#beforeSet");
			return sv;
		}

		private String value(final SetValue<?>[] sv)
		{
			assertEquals(1, sv.length);
			assertSame(field, sv[0].settable);
			return (String)sv[0].value;
		}

		@Override public void beforeDelete(final Item item)
		{
			addEvent("Hook#beforeDelete(" + item + ")");
		}

		@Override
		public String toString()
		{
			return "MyHook(" + model + ")";
		}
	}

	protected static final String value(final StringField field, final SetValue<?>[] sv)
	{
		assertEquals(1, sv.length);
		assertSame(field, sv[0].settable);
		return (String)sv[0].value;
	}

	private static final ArrayList<String> events = new ArrayList<>();

	@BeforeEach final void cleanEvents()
	{
		events.clear();
	}

	protected static void assertEvents(final String... logs)
	{
		//noinspection MisorderedAssertEqualsArguments OK: false positive
		assertEquals(Arrays.asList(logs), events);
		events.clear();
	}

	static final void addEvent(final String event)
	{
		assertNotNull(event);
		events.add(event);
	}
}

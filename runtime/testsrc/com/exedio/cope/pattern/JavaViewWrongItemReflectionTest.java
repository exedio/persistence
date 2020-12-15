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

package com.exedio.cope.pattern;

import static com.exedio.cope.AbstractRuntimeTest.activate;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class JavaViewWrongItemReflectionTest
{
	@Test void testOk()
	{
		final MyItem item = activate(MyItem.TYPE, 55);
		assertEquals("getView result", item.getView());
		assertEquals("getView result", MyItem.view.get(item));
	}

	@Test void testNull()
	{
		assertFails(
				() -> MyItem.view.get(null),
				NullPointerException.class,
				null);
	}

	@Test void testOther()
	{
		final OtherItem item = activate(OtherItem.TYPE, 66);
		assertFails(
				() -> MyItem.view.get(item),
				IllegalArgumentException.class,
				"object is not an instance of declaring class");
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final JavaView view = new JavaView();

		String getView()
		{
			return "getView result";
		}


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class OtherItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<OtherItem> TYPE = com.exedio.cope.TypesBound.newType(OtherItem.class);

		@com.exedio.cope.instrument.Generated
		private OtherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE, OtherItem.TYPE);

	static
	{
		MODEL.enableSerialization(JavaViewWrongItemReflectionTest.class, "MODEL");
	}
}

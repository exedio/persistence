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
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class JavaViewFunctionWrongValueTypeTest
{
	@Test void testNonNull()
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


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final Function<MyItem,String> TARGET_STRING = MyItem::getView;
		@SuppressWarnings({"RawTypeCanBeGeneric", "rawtypes"}) // OK: test bad API usage
		static final Function TARGET_RAW = TARGET_STRING;
		@SuppressWarnings("unchecked") // OK: test bad API usage
		static final Function<MyItem,Integer> TARGET_INT = TARGET_RAW;

		static final JavaView view = JavaView.createInt(TARGET_INT);

		String getView()
		{
			return "getView result";
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(JavaViewFunctionWrongValueTypeTest.class, "MODEL");
	}
}

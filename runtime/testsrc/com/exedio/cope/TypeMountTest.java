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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypeMountTest
{
	@Test void testIt()
	{
		final Type<Item1> type1 = Item1.TYPE;
		final Type<Item2> type2 = Item2.TYPE;

		try
		{
			type1.getModel();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type Item1 (" + Item1.class.getName() + ") does not belong to any model",
					e.getMessage());
		}
		try
		{
			type2.getModel();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type Item2 (" + Item2.class.getName() + ") does not belong to any model",
					e.getMessage());
		}
		assertSame(Item1.class, Item2.f.getValueClass());
		try
		{
			Item2.f.getValueType();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"item field Item2.f (" + Item1.class.getName() + ") does not belong to any model",
					e.getMessage());
		}

		final Model model = new Model(type1, type2);
		assertSame(model, type1.getModel());
		assertSame(model, type2.getModel());
		assertSame(Item1.class, Item2.f.getValueClass());
		assertSame(type1, Item2.f.getValueType());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Item1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class);

		@com.exedio.cope.instrument.Generated
		protected Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Item2 extends Item
	{
		@WrapperIgnore
		static final ItemField<Item1> f = ItemField.create(Item1.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class);

		@com.exedio.cope.instrument.Generated
		protected Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

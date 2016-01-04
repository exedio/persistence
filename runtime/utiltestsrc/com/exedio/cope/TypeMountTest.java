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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TypeMountTest
{
	@Test public void testIt()
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
					"value type of Item2.f (" + Item1.class.getName() + ") does not belong to any model",
					e.getMessage());
		}

		final Model model = new Model(type1, type2);
		assertSame(model, type1.getModel());
		assertSame(model, type2.getModel());
		assertSame(Item1.class, Item2.f.getValueClass());
		assertSame(type1, Item2.f.getValueType());
	}

	static class Item1 extends Item
	{
		Item1(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;
		static final Type<Item1> TYPE = TypesBound.newType(Item1.class);
	}

	static class Item2 extends Item
	{
		static final ItemField<Item1> f = ItemField.create(Item1.class);

		Item2(final ActivationParameters ap)
		{
			super(ap);
		}

		private static final long serialVersionUID = 1l;
		static final Type<Item2> TYPE = TypesBound.newType(Item2.class);
	}
}

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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

@WrapperIgnore
public class TypeSetTest
{
	@Test void testIt()
	{
		final Model m = new Model(Item1.TYPE, Item2.TYPE);

		try
		{
			m.containsTypeSet((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("typeSet", e.getMessage());
		}
		try
		{
			m.containsTypeSet(new Type<?>[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("typeSet is empty", e.getMessage());
		}
		try
		{
			m.containsTypeSet(new Type<?>[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("typeSet[0]", e.getMessage());
		}

		assertEquals(true,  m.containsTypeSet(Item1.TYPE, Item2.TYPE));
		assertEquals(true,  m.containsTypeSet(Item1.TYPE, Item2.TYPE, Item2.TYPE)); // TODO fail here
		assertEquals(false, m.containsTypeSet(ItemX.TYPE));
		try
		{
			m.containsTypeSet(ItemX.TYPE, Item1.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent type set: ItemX, [Item1]", e.getMessage());
		}
		try
		{
			m.containsTypeSet(ItemX.TYPE, Item1.TYPE, Item2.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent type set: ItemX, [Item1], [Item2]", e.getMessage());
		}
		try
		{
			m.containsTypeSet(ItemX.TYPE, Item2.TYPE, Item1.TYPE);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent type set: ItemX, [Item2], [Item1]", e.getMessage());
		}
	}

	static class Item1 extends Item
	{
		private static final long serialVersionUID = 1l;

		Item1(final ActivationParameters ap)
		{
			super(ap);
		}

		static final Type<Item1> TYPE = TypesBound.newType(Item1.class);
	}

	static class Item2 extends Item
	{
		private static final long serialVersionUID = 1l;

		Item2(final ActivationParameters ap)
		{
			super(ap);
		}

		static final Type<Item2> TYPE = TypesBound.newType(Item2.class);
	}

	static class ItemX extends Item
	{
		private static final long serialVersionUID = 1l;

		ItemX(final ActivationParameters ap)
		{
			super(ap);
		}

		static final Type<ItemX> TYPE = TypesBound.newType(ItemX.class);
	}
}

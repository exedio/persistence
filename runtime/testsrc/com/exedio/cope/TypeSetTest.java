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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypeSetTest
{
	@Test void testIt()
	{
		final Model m = new Model(Item1.TYPE, Item2.TYPE);

		try
		{
			m.containsTypeSet((Type<?>[])null);
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

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Item1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class,Item1::new);

		@com.exedio.cope.instrument.Generated
		protected Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Item2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class,Item2::new);

		@com.exedio.cope.instrument.Generated
		protected Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class ItemX extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemX> TYPE = com.exedio.cope.TypesBound.newType(ItemX.class,ItemX::new);

		@com.exedio.cope.instrument.Generated
		protected ItemX(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

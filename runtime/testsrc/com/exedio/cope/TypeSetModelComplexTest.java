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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypeSetModelComplexTest
{
	@Test void testIt()
	{
		final Type<ItemA1> typeA1 = ItemA1.TYPE;
		final Type<ItemA2> typeA2 = ItemA2.TYPE;
		final TypeSet typeSetA = new TypeSet(typeA1, typeA2);
		assertEqualsUnmodifiable(list(typeA1, typeA2), typeSetA.getExplicitTypes());

		final Type<ItemB1> typeB1 = ItemB1.TYPE;
		final TypeSet typeSetB = new TypeSet(typeB1);
		assertEqualsUnmodifiable(list(typeB1), typeSetB.getExplicitTypes());

		final Type<Item1> type1 = Item1.TYPE;
		final Type<Item2> type2 = Item2.TYPE;

		final Model model = Model.builder().add(typeSetA, typeSetB).add(type1, type2).build();
		assertEqualsUnmodifiable(list(typeA1, typeA2, typeB1, type1, type2), model.getTypes());
		assertEqualsUnmodifiable(list(typeA1, typeA2, typeB1, type1, type2), model.getTypesSortedByHierarchy());

		final Type<ItemX> typeX = ItemX.TYPE;
		assertEquals(true, model.containsTypeSet(typeA1, typeA2));
		assertEquals(false, model.containsTypeSet(typeX));
		try
		{
			model.containsTypeSet(typeA1, typeA2, typeX);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent type set: [ItemA1], [ItemA2], ItemX", e.getMessage());
		}

		assertEquals(true, model.contains(typeSetA));
		assertEquals(false, model.contains(new TypeSet(typeX)));
		final TypeSet typeAX = new TypeSet(typeA1, typeA2, typeX);
		try
		{
			model.contains(typeAX);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("inconsistent type set: [ItemA1], [ItemA2], ItemX", e.getMessage());
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemA1> TYPE = com.exedio.cope.TypesBound.newType(ItemA1.class);

		@com.exedio.cope.instrument.Generated
		private ItemA1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemA2> TYPE = com.exedio.cope.TypesBound.newType(ItemA2.class);

		@com.exedio.cope.instrument.Generated
		private ItemA2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemB1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemB1> TYPE = com.exedio.cope.TypesBound.newType(ItemB1.class);

		@com.exedio.cope.instrument.Generated
		private ItemB1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class);

		@com.exedio.cope.instrument.Generated
		private Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class);

		@com.exedio.cope.instrument.Generated
		private Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemX extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemX> TYPE = com.exedio.cope.TypesBound.newType(ItemX.class);

		@com.exedio.cope.instrument.Generated
		private ItemX(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.junit.CopeAssert;

public class TypeSetModelComplexTest extends CopeAssert
{
	public void testIt()
	{
		final Type<ItemA1> typeA1 = TypesBound.newType(ItemA1.class);
		final Type<ItemA2> typeA2 = TypesBound.newType(ItemA2.class);
		final TypeSet typeSetA = new TypeSet(typeA1, typeA2);
		assertEqualsUnmodifiable(list(typeA1, typeA2), typeSetA.getExplicitTypes());

		final Type<ItemB1> typeB1 = TypesBound.newType(ItemB1.class);
		final TypeSet typeSetB = new TypeSet(typeB1);
		assertEqualsUnmodifiable(list(typeB1), typeSetB.getExplicitTypes());

		final Type<Item1> type1 = TypesBound.newType(Item1.class);
		final Type<Item2> type2 = TypesBound.newType(Item2.class);

		final Model model = new Model((Revisions.Factory)null, new TypeSet[]{typeSetA, typeSetB}, type1, type2);
		assertEqualsUnmodifiable(list(typeA1, typeA2, typeB1, type1, type2), model.getTypes());
		assertEqualsUnmodifiable(list(typeA1, typeA2, typeB1, type1, type2), model.getTypesSortedByHierarchy());

		final Type<ItemX> typeX = TypesBound.newType(ItemX.class);
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

	static class ItemA1 extends Item
	{
		private static final long serialVersionUID = 1l;
		private ItemA1(final ActivationParameters ap) { super(ap); }
	}

	static class ItemA2 extends Item
	{
		private static final long serialVersionUID = 1l;
		private ItemA2(final ActivationParameters ap) { super(ap); }
	}

	static class ItemB1 extends Item
	{
		private static final long serialVersionUID = 1l;
		private ItemB1(final ActivationParameters ap) { super(ap); }
	}

	static class Item1 extends Item
	{
		private static final long serialVersionUID = 1l;
		private Item1(final ActivationParameters ap) { super(ap); }
	}

	static class Item2 extends Item
	{
		private static final long serialVersionUID = 1l;
		private Item2(final ActivationParameters ap) { super(ap); }
	}

	static class ItemX extends Item
	{
		private static final long serialVersionUID = 1l;
		private ItemX(final ActivationParameters ap) { super(ap); }
	}
}

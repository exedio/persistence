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

import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class TypeCompareTest
{
	@SuppressWarnings("EqualsWithItself")
	@Test void testType()
	{
		final Type<AnItem> type1 = newType(AnItem.class, AnItem::new);
		final Type<AnotherItem> type2 = newType(AnotherItem.class, AnotherItem::new);
		try
		{
			type1.compareTo(type2);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type AnItem (com.exedio.cope.TypeCompareTest$AnItem) " +
					"does not belong to any model",
					e.getMessage());
		}
		try
		{
			type2.compareTo(type1);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type AnotherItem (com.exedio.cope.TypeCompareTest$AnotherItem) " +
					"does not belong to any model",
					e.getMessage());
		}

		final Model model = new Model(type1, type2);
		assertEquals(0, type1.compareTo(type1));
		assertEquals(0, type2.compareTo(type2));
		assertEquals(-1, type1.compareTo(type2));
		assertEquals( 1, type2.compareTo(type1));

		final Type<AnotherModelItem> typeOtherModel = newType(AnotherModelItem.class, AnotherModelItem::new);
		try
		{
			type1.compareTo(typeOtherModel);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type AnotherModelItem (com.exedio.cope.TypeCompareTest$AnotherModelItem) " +
					"does not belong to any model",
					e.getMessage());
		}
		try
		{
			typeOtherModel.compareTo(type1);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"type AnotherModelItem (com.exedio.cope.TypeCompareTest$AnotherModelItem) " +
					"does not belong to any model",
					e.getMessage());
		}

		final Model otherModel = new Model(typeOtherModel);
		try
		{
			type1.compareTo(typeOtherModel);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"types are not comparable, because they do not belong to the same model: " +
					"AnItem (" + model + ") and AnotherModelItem (" + otherModel + ").",
					e.getMessage());
		}
		try
		{
			typeOtherModel.compareTo(type1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"types are not comparable, because they do not belong to the same model: " +
					"AnotherModelItem (" + otherModel + ") and AnItem (" + model + ").",
					e.getMessage());
		}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnotherItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnotherItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnotherModelItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnotherModelItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

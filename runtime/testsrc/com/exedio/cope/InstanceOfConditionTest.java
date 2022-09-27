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

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class InstanceOfConditionTest
{
	@Test void test()
	{
		final This<ItemS> thisS = ItemS.TYPE.getThis();
		assertEquals(
				"ItemS.this instanceOf Item1",
				thisS.instanceOf(Item1.TYPE).toString());
		assertEquals(
				"ItemS.this instanceOf [Item1, Item2]",
				thisS.instanceOf(Item1.TYPE, Item2.TYPE).toString());
		assertEquals(
				"ItemS.this instanceOf [Item1, Item2, Item3]",
				thisS.instanceOf(Item1.TYPE, Item2.TYPE, Item3.TYPE).toString());
		assertEquals(
				"ItemS.this instanceOf [Item1, Item2, Item3, Item4]",
				thisS.instanceOf(Item1.TYPE, Item2.TYPE, Item3.TYPE, Item4.TYPE).toString());
		assertEquals(
				"ItemS.this instanceOf [Item1, Item2, Item3, Item4]",
				thisS.instanceOf(new Type<?>[]{Item1.TYPE, Item2.TYPE, Item3.TYPE, Item4.TYPE}).toString());
		assertEquals(
				"ItemS.this not instanceOf Item1",
				thisS.notInstanceOf(Item1.TYPE).toString());
		assertEquals(
				"ItemS.this not instanceOf [Item1, Item2]",
				thisS.notInstanceOf(Item1.TYPE, Item2.TYPE).toString());
		assertEquals(
				"ItemS.this not instanceOf [Item1, Item2, Item3]",
				thisS.notInstanceOf(Item1.TYPE, Item2.TYPE, Item3.TYPE).toString());
		assertEquals(
				"ItemS.this not instanceOf [Item1, Item2, Item3, Item4]",
				thisS.notInstanceOf(Item1.TYPE, Item2.TYPE, Item3.TYPE, Item4.TYPE).toString());
		assertEquals(
				"ItemS.this not instanceOf [Item1, Item2, Item3, Item4]",
				thisS.notInstanceOf(new Type<?>[]{Item1.TYPE, Item2.TYPE, Item3.TYPE, Item4.TYPE}).toString());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class ItemS extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemS> TYPE = com.exedio.cope.TypesBound.newType(ItemS.class,ItemS::new);

		@com.exedio.cope.instrument.Generated
		protected ItemS(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item1 extends ItemS
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class,Item1::new);

		@com.exedio.cope.instrument.Generated
		private Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item2 extends ItemS
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class,Item2::new);

		@com.exedio.cope.instrument.Generated
		private Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item3 extends ItemS
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item3> TYPE = com.exedio.cope.TypesBound.newType(Item3.class,Item3::new);

		@com.exedio.cope.instrument.Generated
		private Item3(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Item4 extends ItemS
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Item4> TYPE = com.exedio.cope.TypesBound.newType(Item4.class,Item4::new);

		@com.exedio.cope.instrument.Generated
		private Item4(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

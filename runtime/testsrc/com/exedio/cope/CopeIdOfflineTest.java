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
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeIdOfflineTest
{
	@Test void testIt() throws NoSuchIDException
	{
		assertFails("MyItem", "no separator '-' in id");
		assertFails("MyItemNotExists-x", "type <MyItemNotExists> does not exist");
		assertFails("MyItem--1",         "type <MyItem-> does not exist");
		assertFails("MyItem-"+MIN_VALUE, "type <MyItem-> does not exist");
		assertFails("MyAbstractItem-6", "type is abstract");
		assertFails("MyItem-+1", "has plus sign");
		assertFails("MyItem-00",  "has leading zeros");
		assertFails("MyItem-01",  "has leading zeros");
		assertFails("MyItem-000", "has leading zeros");
		assertFails("MyItem-001", "has leading zeros");
		assertFails("MyItem-x",  "wrong number format <x>");
		assertFails("MyItem-",   "wrong number format <>");

		assertOk("MyItem-0", MyItem.TYPE);
		assertOk("MyItem-1", MyItem.TYPE);
		assertOk("MyItem-"+(MAX_VALUE-1), MyItem.TYPE);
		assertOk("MyItem-"+ MAX_VALUE,    MyItem.TYPE);
		assertFails("MyItem-"+MAX_VALUE_PLUS_ONE, "wrong number format <"+MAX_VALUE_PLUS_ONE+">");

		assertOk("My66Item-0",  My66Item.TYPE);
		assertOk("My66Item-1",  My66Item.TYPE);
		assertOk("My66Item-65", My66Item.TYPE);
		assertOk("My66Item-66", My66Item.TYPE);
		assertFails("My66Item-67", "must be less or equal 66");
		assertFails("My66Item-68", "must be less or equal 66");
		assertFails("My66Item-"+(MAX_VALUE-1), "must be less or equal 66");
		assertFails("My66Item-"+ MAX_VALUE,    "must be less or equal 66");
		assertFails("My66Item-"+ MAX_VALUE_PLUS_ONE, "wrong number format <"+MAX_VALUE_PLUS_ONE+">");
	}


	private static final String MAX_VALUE_PLUS_ONE = "9223372036854775808";

	/**
	 * @see RuntimeTester#assertIDFails(String, String, boolean)
	 */
	private static void assertFails(final String id, final String detail)
	{
		final NoSuchIDException e = com.exedio.cope.tojunit.Assert.assertFails(
				() -> MODEL.getItem(id),
				NoSuchIDException.class,
				"no such id <" + id + ">, " + detail);
		assertEquals(true, e.notAnID());
		final NoSuchIDException e2 = com.exedio.cope.tojunit.Assert.assertFails(
				() -> MODEL.getTypeByItemID(id),
				NoSuchIDException.class,
				"no such id <" + id + ">, " + detail);
		assertEquals(true, e2.notAnID());
	}

	private static void assertOk(final String id, final Type<?> type) throws NoSuchIDException
	{
		com.exedio.cope.tojunit.Assert.assertFails(
				() -> MODEL.getItem(id),
				IllegalStateException.class,
				"there is no cope transaction bound to this thread for model " + MODEL + ", " +
				"see Model#startTransaction");
		assertSame(type, MODEL.getTypeByItemID(id));
	}

	@CopeCreateLimit(MAX_VALUE)
	@WrapperType(constructor = NONE, genericConstructor = NONE, indent = 2, comments = false)
	private abstract static class MyAbstractItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 2l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyAbstractItem> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(MyAbstractItem.class);

		@com.exedio.cope.instrument.Generated
		protected MyAbstractItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends MyAbstractItem
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeCreateLimit(66)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class My66Item extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<My66Item> TYPE = com.exedio.cope.TypesBound.newType(My66Item.class,My66Item::new);

		@com.exedio.cope.instrument.Generated
		private My66Item(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE, MyAbstractItem.TYPE, My66Item.TYPE);


	@Test void testIdNull()
	{
		com.exedio.cope.tojunit.Assert.assertFails(
				() -> MODEL.getItem(null),
				NullPointerException.class,
				"Cannot invoke \"String.lastIndexOf(int)\" because \"id\" is null");
	}
	@Test void testIdNullType()
	{
		com.exedio.cope.tojunit.Assert.assertFails(
				() -> MODEL.getTypeByItemID(null),
				NullPointerException.class,
				"Cannot invoke \"String.lastIndexOf(int)\" because \"id\" is null");
	}
}

/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.SerializationCheck.check;

import java.lang.reflect.Field;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;

public class SerializationCheckTest extends CopeAssert
{
	public void testIt() throws NoSuchFieldException
	{
		try
		{
			check(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}

		assertEqualsUnmodifiable(list(), check(MODEL_OK));

		final Field field1 = Item1.class.getDeclaredField("serializedField1");
		final Field field2 = Item2.class.getDeclaredField("serializedField2");
		assertEqualsUnmodifiable(list(field1, field2), check(MODEL));
	}


	static class ItemOk extends Item
	{
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;

		private ItemOk(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type TYPE = TypesBound.newType(ItemOk.class);
	}

	private static Model MODEL_OK = new Model(ItemOk.TYPE);


	static class Item1 extends Item
	{
		static final StringField f1 = new StringField();

		static int staticField1;
		transient int transientField1;
		static transient int staticTransientField1;
		int serializedField1;

		private Item1(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type TYPE = TypesBound.newType(Item1.class);
	}

	static class Item2 extends Item
	{
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;
		int serializedField2;

		private Item2(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type TYPE = TypesBound.newType(Item2.class);
	}

	private static Model MODEL = new Model(Item1.TYPE, Item2.TYPE);
}

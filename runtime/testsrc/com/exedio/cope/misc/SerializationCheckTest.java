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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.SerializationCheck.check;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import java.lang.reflect.Field;
import org.junit.Test;

public class SerializationCheckTest
{
	@Test public void testNull()
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
	}

	@Test public void testOk()
	{
		assertEqualsUnmodifiable(list(), check(MODEL_OK));
	}

	@Test public void testWrong() throws NoSuchFieldException
	{
		final Field field1 = Item1.class.getDeclaredField("serializedField1");
		final Field field2 = Item2.class.getDeclaredField("serializedField2");
		assertEqualsUnmodifiable(list(field1, field2), check(MODEL));
	}


	@WrapperIgnore
	static class ItemOk extends Item
	{
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;

		private ItemOk(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type<ItemOk> TYPE = TypesBound.newType(ItemOk.class);
	}

	private static final Model MODEL_OK = new Model(ItemOk.TYPE);


	@WrapperIgnore
	static class Item1 extends Item
	{
		static final StringField f1 = new StringField();

		static int staticField1;
		transient int transientField1;
		static transient int staticTransientField1;
		int serializedField1;

		private Item1(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type<Item1> TYPE = TypesBound.newType(Item1.class);
	}

	@WrapperIgnore
	static class Item2 extends Item
	{
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;
		int serializedField2;

		private Item2(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
		static final Type<Item2> TYPE = TypesBound.newType(Item2.class);
	}

	private static final Model MODEL = new Model(Item1.TYPE, Item2.TYPE);
}

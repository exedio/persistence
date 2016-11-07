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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.SerializationCheck.check;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
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


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ItemOk extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ItemOk> TYPE = com.exedio.cope.TypesBound.newType(ItemOk.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ItemOk(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL_OK = new Model(ItemOk.TYPE);


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Item1 extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f1 = new StringField();

		static int staticField1;
		transient int transientField1;
		static transient int staticTransientField1;
		int serializedField1;


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Item1> TYPE = com.exedio.cope.TypesBound.newType(Item1.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Item1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Item2 extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField f2 = new StringField();

		static int staticField2;
		transient int transientField2;
		static transient int staticTransientField2;
		int serializedField2;


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Item2> TYPE = com.exedio.cope.TypesBound.newType(Item2.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Item2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Item1.TYPE, Item2.TYPE);
}

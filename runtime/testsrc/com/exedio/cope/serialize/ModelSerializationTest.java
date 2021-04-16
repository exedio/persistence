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

package com.exedio.cope.serialize;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.lang.Integer.toHexString;
import static java.lang.System.identityHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.ListField;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.jupiter.api.Test;

public class ModelSerializationTest
{
	private static final Model model = new Model(MyItem.TYPE);

	@SuppressWarnings("unused")
	private final Model modelNonStatic = null;
	@SuppressWarnings({"unused", "FieldMayBeFinal"})
	private static Model modelNonFinal = null;
	@SuppressWarnings("unused")
	private static final Model modelNull = null;
	@SuppressWarnings("unused")
	private static final String modelWrong = "wrong";

	private static final Class<?> itemClass = MyItem.class;
	private static final Class<?> testClass = ModelSerializationTest.class;
	private static final String itemClassName = itemClass.getName();
	private static final String testClassName = testClass.getName();

	@Test void test() throws IOException
	{
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(null, null),
				NullPointerException.class,
				"type");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(itemClass, null),
				NullPointerException.class,
				"name");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(itemClass, "model"),
				IllegalArgumentException.class,
				itemClassName + "#model does not exist.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelx"),
				IllegalArgumentException.class,
				testClassName + "#modelx does not exist.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelNonStatic"),
				IllegalArgumentException.class,
				testClassName + "#modelNonStatic is not static final.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelNonFinal"),
				IllegalArgumentException.class,
				testClassName + "#modelNonFinal is not static final.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelNull"),
				IllegalArgumentException.class,
				testClassName + "#modelNull is null.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelWrong"),
				IllegalArgumentException.class,
				testClassName + "#modelWrong is not a model, but java.lang.String.");
		assertNotSerializable();

		assertFails(
				() -> model.enableSerialization(CacheIsolationTest.class, "MODEL"),
				IllegalArgumentException.class,
				"enableSerialization does not resolve to itself com.exedio.cope.CacheIsolationTest#MODEL");
		assertNotSerializable();

		model.enableSerialization(testClass, "model");
		assertSerializable();

		assertFails(
				() -> model.enableSerialization(testClass, "modelx"),
				IllegalStateException.class,
				"enableSerialization already been called for " + testClassName + "#model");
		assertSerializable();
	}

	private static void assertNotSerializable() throws IOException
	{
		assertEquals(false, model.isSerializationEnabled());
		assertNotSerializable(model, Model.class);
		assertNotSerializable(MyItem.TYPE, Model.class);
		assertNotSerializable(NotItem.TYPE, Type.class);
		assertNotSerializable(MyItem.TYPE.getThis(), Model.class);
		assertNotSerializable(MyItem.name, Model.class);
		assertNotSerializable(MyItem.list, Model.class);
		assertNotSerializable(NotItem.field, Type.class);
		assertNotSerializable(NotItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.create(new StringField()), ListField.class);
		assertEquals("com.exedio.cope.Model@" + toHexString(identityHashCode(model)), model.toString());
	}

	private static void assertSerializable() throws IOException
	{
		assertEquals(true, model.isSerializationEnabled());
		assertSerializedSame(model, 181);
		assertSerializedSame(MyItem.TYPE, 282);
		assertNotSerializable(NotItem.TYPE, Type.class);
		assertSerializedSame(MyItem.TYPE.getThis(), 384);
		assertSerializedSame(MyItem.name, 384);
		assertSerializedSame(MyItem.list, 384);
		assertNotSerializable(NotItem.field, Type.class);
		assertNotSerializable(NotItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.create(new StringField()), ListField.class);
		assertEquals(testClassName + "#model", model.toString());
	}

	private static void assertNotSerializable(final Serializable value, final Class<?> exceptionMessage) throws IOException
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new ObjectOutputStream(bos))
		{
			oos.writeObject(value);
			fail();
		}
		catch(final NotSerializableException e)
		{
			assertEquals(
					exceptionMessage==Model.class
					? exceptionMessage.getName() + " (can be fixed by calling method enableSerialization(Class,String))"
					: exceptionMessage.getName(),
				e.getMessage());
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField name = new StringField().optional();
		@Wrapper(wrap="*", visibility=NONE)
		static final ListField<String> list = ListField.create(new StringField());


		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class NotItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final StringField field = new StringField();
		@Wrapper(wrap="*", visibility=NONE)
		static final ListField<String> pattern = ListField.create(new StringField());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<NotItem> TYPE = com.exedio.cope.TypesBound.newType(NotItem.class);

		@com.exedio.cope.instrument.Generated
		protected NotItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

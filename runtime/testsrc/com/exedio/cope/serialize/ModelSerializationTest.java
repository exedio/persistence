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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.ListField;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.junit.Test;

public class ModelSerializationTest extends CopeAssert
{
	private static final Model model = new Model(ModelSerializationItem.TYPE);

	@SuppressWarnings("unused")
	private final Model modelNonStatic = null;
	@SuppressWarnings("unused")
	private static Model modelNonFinal = null;
	@SuppressWarnings("unused")
	private static final Model modelNull = null;
	@SuppressWarnings("unused")
	private static final String modelWrong = "wrong";

	private static final Class<?> itemClass = ModelSerializationItem.class;
	private static final Class<?> testClass = ModelSerializationTest.class;

	@Test public void test() throws IOException
	{
		assertEquals(false, model.isSerializationEnabled());
		assertNotSerializable(model, Model.class);
		assertNotSerializable(ModelSerializationItem.TYPE, Model.class);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertNotSerializable(ModelSerializationItem.TYPE.getThis(), Model.class);
		assertNotSerializable(ModelSerializationItem.name, Model.class);
		assertNotSerializable(ModelSerializationItem.list, Model.class);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.create(new StringField()), ListField.class);
		assertNotNull(model.toString());

		try
		{
			model.enableSerialization(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(itemClass, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(itemClass, "model");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(itemClass.getName() + "#model does not exist.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(testClass, "modelx");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(testClass.getName() + "#modelx does not exist.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(testClass, "modelNonStatic");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(testClass.getName() + "#modelNonStatic is not static final.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(testClass, "modelNonFinal");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(testClass.getName() + "#modelNonFinal is not static final.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(testClass, "modelNull");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(testClass.getName() + "#modelNull is null.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(testClass, "modelWrong");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(testClass.getName() + "#modelWrong is not a model, but java.lang.String.", e.getMessage());
		}
		assertEquals(false, model.isSerializationEnabled());
		try
		{
			model.enableSerialization(CacheIsolationTest.class, "MODEL");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("enableSerialization does not resolve to itself com.exedio.cope.CacheIsolationTest#MODEL", e.getMessage());
		}

		assertEquals(false, model.isSerializationEnabled());
		assertNotSerializable(model, Model.class);
		assertNotSerializable(ModelSerializationItem.TYPE, Model.class);
		assertNotSerializable(AnItem.TYPE, Type.class);

		model.enableSerialization(testClass, "model");
		assertEquals(true, model.isSerializationEnabled());
		assertSerializedSame(model, 181);
		assertSerializedSame(ModelSerializationItem.TYPE, 298);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertSerializedSame(ModelSerializationItem.TYPE.getThis(), 400);
		assertSerializedSame(ModelSerializationItem.name, 400);
		assertSerializedSame(ModelSerializationItem.list, 400);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.create(new StringField()), ListField.class);
		assertEquals(testClass.getName() + "#model", model.toString());

		try
		{
			model.enableSerialization(itemClass, "modelx");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("enableSerialization already been called for " + testClass.getName() + "#model", e.getMessage());
		}
		assertEquals(true, model.isSerializationEnabled());
		assertSerializedSame(model, 181);
		assertSerializedSame(ModelSerializationItem.TYPE, 298);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertSerializedSame(ModelSerializationItem.TYPE.getThis(), 400);
		assertSerializedSame(ModelSerializationItem.name, 400);
		assertSerializedSame(ModelSerializationItem.list, 400);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.create(new StringField()), ListField.class);
	}

	private static final void assertNotSerializable(final Serializable value, final Class<?> exceptionMessage) throws IOException
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

	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}

		static final StringField field = new StringField();
		static final ListField<String> pattern = ListField.create(new StringField());

		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
	}
}

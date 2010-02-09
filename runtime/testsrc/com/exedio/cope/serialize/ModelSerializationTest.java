/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.pattern.ListField;

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
	
	public void test() throws IOException
	{
		assertNotSerializable(model, Model.class);
		assertNotSerializable(ModelSerializationItem.TYPE, Model.class);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertNotSerializable(ModelSerializationItem.TYPE.getThis(), Model.class);
		assertNotSerializable(ModelSerializationItem.name, Model.class);
		assertNotSerializable(ModelSerializationItem.list, Model.class);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.newList(new StringField()), ListField.class);
		
		try
		{
			model.enableSerialization(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationItem.class, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationItem.class, "model");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationItem.class.getName() + "#model does not exist.", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationTest.class, "modelx");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationTest.class.getName() + "#modelx does not exist.", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationTest.class, "modelNonStatic");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationTest.class.getName() + "#modelNonStatic is not static final.", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationTest.class, "modelNonFinal");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationTest.class.getName() + "#modelNonFinal is not static final.", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationTest.class, "modelNull");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationTest.class.getName() + "#modelNull is null.", e.getMessage());
		}
		try
		{
			model.enableSerialization(ModelSerializationTest.class, "modelWrong");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ModelSerializationTest.class.getName() + "#modelWrong is not a model, but java.lang.String.", e.getMessage());
		}
		try
		{
			model.enableSerialization(CacheIsolationTest.class, "MODEL");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("enableSerialization does not resolve to itself com.exedio.cope.CacheIsolationTest#MODEL", e.getMessage());
		}
		
		assertNotSerializable(model, Model.class);
		assertNotSerializable(ModelSerializationItem.TYPE, Model.class);
		assertNotSerializable(AnItem.TYPE, Type.class);
		
		model.enableSerialization(ModelSerializationTest.class, "model");
		assertSerializedSame(model, 181);
		assertSerializedSame(ModelSerializationItem.TYPE, 298);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertSerializedSame(ModelSerializationItem.TYPE.getThis(), 400);
		assertSerializedSame(ModelSerializationItem.name, 400);
		assertSerializedSame(ModelSerializationItem.list, 400);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.newList(new StringField()), ListField.class);
		
		try
		{
			model.enableSerialization(ModelSerializationItem.class, "modelx");
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("enableSerialization already been called for " + ModelSerializationTest.class.getName() + "#model", e.getMessage());
		}
		assertSerializedSame(model, 181);
		assertSerializedSame(ModelSerializationItem.TYPE, 298);
		assertNotSerializable(AnItem.TYPE, Type.class);
		assertSerializedSame(ModelSerializationItem.TYPE.getThis(), 400);
		assertSerializedSame(ModelSerializationItem.name, 400);
		assertSerializedSame(ModelSerializationItem.list, 400);
		assertNotSerializable(AnItem.field, Type.class);
		assertNotSerializable(AnItem.pattern, Type.class);
		assertNotSerializable(new StringField(), StringField.class);
		assertNotSerializable(ListField.newList(new StringField()), ListField.class);
	}
	
	private static final void assertNotSerializable(final Serializable value, final Class exceptionMessage) throws IOException
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(bos);
		try
		{
			oos.writeObject(value);
			fail();
		}
		catch(NotSerializableException e)
		{
			assertEquals(
					exceptionMessage==Model.class
					? exceptionMessage.getName() + " (can be fixed by calling method enableSerialization(Class,String))"
					: exceptionMessage.getName(),
				e.getMessage());
		}
		oos.close();
	}
	
	static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		
		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
		
		static final StringField field = new StringField();
		static final ListField<String> pattern = ListField.newList(new StringField());
		
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
	}
}

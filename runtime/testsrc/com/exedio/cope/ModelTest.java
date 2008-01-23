/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * Tests the model itself, without creating/using any persistent data.
 * @author Ralf Wiebicke
 */
public class ModelTest extends TestmodelTest
{
	
	public void testSupportsReadCommitted()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		assertEquals(dialect.supportsReadCommitted, model.supportsReadCommitted());
	}
	
	public static final void assertEquals(final String expected, final String actual)
	{
		assertEquals("-----"+expected+"-----"+actual+"-----", expected, actual);
	}
	
	public void testConnect()
	{
		final ConnectProperties defaultProps = getProperties();
		// test duplicate call of connect
		final Date connectDate = model.getConnectDate();
		assertNotNull(connectDate);
		try
		{
			model.connect(defaultProps);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model already been connected", e.getMessage());
		}
		assertSame(connectDate, model.getConnectDate());
	}
	
	@Deprecated // OK: test deprecated api
	public void testAttributeOption()
	{
		final AttributeItem item = null;
		
		assertEquals(false, item.MANDATORY.isFinal);
		assertEquals(false, item.MANDATORY.unique);
		assertEquals(false, item.MANDATORY.optional);
		assertEquals(false, item.OPTIONAL.isFinal);
		assertEquals(false, item.OPTIONAL.unique);
		assertEquals(true,  item.OPTIONAL.optional);
		assertEquals(false, item.UNIQUE.isFinal);
		assertEquals(true,  item.UNIQUE.unique);
		assertEquals(false, item.UNIQUE.optional);
		assertEquals(false, item.UNIQUE_OPTIONAL.isFinal);
		assertEquals(true,  item.UNIQUE_OPTIONAL.unique);
		assertEquals(true,  item.UNIQUE_OPTIONAL.optional);
		assertEquals(true,  item.FINAL.isFinal);
		assertEquals(false, item.FINAL.unique);
		assertEquals(false, item.FINAL.optional);
		assertEquals(true,  item.FINAL_OPTIONAL.isFinal);
		assertEquals(false, item.FINAL_OPTIONAL.unique);
		assertEquals(true,  item.FINAL_OPTIONAL.optional);
		assertEquals(true,  item.FINAL_UNIQUE.isFinal);
		assertEquals(true,  item.FINAL_UNIQUE.unique);
		assertEquals(false, item.FINAL_UNIQUE.optional);
		assertEquals(true,  item.FINAL_UNIQUE_OPTIONAL.isFinal);
		assertEquals(true,  item.FINAL_UNIQUE_OPTIONAL.unique);
		assertEquals(true,  item.FINAL_UNIQUE_OPTIONAL.optional);
	}

	public void testType() throws IOException
	{
		final AttributeItem item = null;
		
		assertEquals(AttributeItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());
		assertEquals(item.TYPE, Type.forClass(AttributeItem.class));
		try
		{
			Type.forClass(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("there is no type for class com.exedio.cope.Item", e.getMessage());
		}
		assertEquals(item.TYPE, model.getType(item.TYPE.getID()));

		assertSame(item.TYPE, item.TYPE.getThis().getType());
		assertEquals("AttributeItem.this", item.TYPE.getThis().getID());
		assertEquals("AttributeItem.this", item.TYPE.getThis().toString());
		assertEquals("this", item.TYPE.getThis().getName());
		
		final Field[] attributes = {
			item.someString,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.day,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData.getBody(),
			item.someData.getContentType(),
			item.someData.getLastModified(),
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getFields());
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getDeclaredFields());
		assertEqualsUnmodifiable(list(), item.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), item.TYPE.getDeclaredUniqueConstraints());

		final Feature[] features = {
			item.TYPE.getThis(),
			item.someString,
			item.someStringUpperCase,
			item.someStringLength,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.day,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData,
			item.someData.getBody(),
			item.someData.getContentType(),
			item.someData.getLastModified(),
		};
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getDeclaredFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getDeclaredFeature("someStringUpperCase"));
		
		try
		{
			new Type<Item>(null);
			fail();
		}
		catch(NullPointerException e)
		{/*OK*/}
		try
		{
			new Type<Item>(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Cannot make a type for " + Item.class + " itself, but only for subclasses.", e.getMessage());
		}
		try
		{
			new Type<Item>(castItemClass(NoItem.class));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoItem.class.toString() + " is not a subclass of Item", e.getMessage());
		}
		try
		{
			new Type<NoCreationConstructor>(NoCreationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor NoCreationConstructor(" + SetValue.class.getName() + "[])", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		try
		{
			new Type<NoReactivationConstructor>(NoReactivationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor NoReactivationConstructor(" + ReactivationConstructorDummy.class.getName() + ",int)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		try
		{
			new Model((Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("types must not be null", e.getMessage());
		}
		try
		{
			new Model(new Type[]{});
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}

		// TODO put this into an extra test called an extra ant target
		{
			final String prefix = System.getProperty("com.exedio.cope.testprotocol.prefix");
			if(prefix!=null)
			{
				final java.util.Properties databaseInfo = model.getDatabaseInfo();
				final java.util.Properties prefixed = new java.util.Properties();
				final File file = new File(System.getProperty("com.exedio.cope.testprotocol.file"));
				for(Iterator i = databaseInfo.keySet().iterator(); i.hasNext(); )
				{
					final String name = (String)i.next();
					prefixed.setProperty(prefix+'.'+name, databaseInfo.getProperty(name));
				}
				final ConnectProperties p = model.getProperties();
				for(final ConnectProperties.Field field : p.getFields())
				{
					if(field.getDefaultValue()!=null
						&& field!=p.mediaRooturl
						&& !field.hasHiddenValue()
						&& field.isSpecified()
						&& field.getValue()!=null)
						prefixed.setProperty(prefix+".cope."+field.getKey(), field.getValue().toString());
				}
				final PrintStream out = new PrintStream(new FileOutputStream(file, true));
				prefixed.store(out, null);
				out.close();
			}
		}
	}
	
	static class NoItem
	{
		NoItem()
		{
			// just a dummy constructor
		}
	}
	
	static class NoCreationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoCreationConstructor()
		{
			super(new SetValue[]{});
		}
	}

	static class NoReactivationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoReactivationConstructor(final SetValue[] initialAttributes)
		{
			super(initialAttributes);
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	private static final Class<Item> castItemClass(Class c)
	{
		return c;
	}
	
	public void testDisconnect()
	{
		assertWithin(Main.beforeModel, Main.afterModel, model.getInitializeDate());
		
		model.commit();
		final ConnectProperties p = model.getProperties();
		assertNotNull(p);
		
		model.disconnect();
		try
		{
			model.getProperties();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not yet connected, use connect(Properties)", e.getMessage());
		}
		assertEquals(null, model.getConnectDate());

		try
		{
			model.disconnect();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not yet connected, use connect(Properties)", e.getMessage());
		}
		assertEquals(null, model.getConnectDate());

		final Date before = new Date();
		model.connect(p);
		final Date after = new Date();
		assertSame(p, model.getProperties());
		assertWithin(before, after, model.getConnectDate());
		model.startTransaction("ModelTest.testDisconnect");
	}
}

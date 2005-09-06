/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.Arrays;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * Tests the model itself, without creating/using any persistent data.
 * @author Ralf Wiebicke
 */
public class ModelTest extends AbstractLibTest
{
	public ModelTest()
	{
		super(Main.model);
	}
	
	public void testSupportsReadCommitted()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		if ( model.getDatabase().getClass().getName().equals("com.exedio.cope.HsqldbDatabase") )
		{
			assertEquals( false, model.supportsReadCommitted() );
		}
		else if ( model.getDatabase().getClass().getName().equals("com.exedio.cope.OracleDatabase") )
		{
			assertEquals( true, model.supportsReadCommitted() );
		}
		else if ( model.getDatabase().getClass().getName().equals("com.exedio.cope.MysqlDatabase") )
		{
			assertEquals( true, model.supportsReadCommitted() );
		}
		else
		{
			fail( model.getDatabase().getClass().getName() );
		}
	}
	
	public void testSetPropertiesInitially()
	{
		final Properties defaultProps = new Properties();
		model.setPropertiesInitially(defaultProps);

		final File file = new File(defaultProps.getSource());
		final java.util.Properties newProps = Properties.loadProperties(file);
		
		{
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty(Properties.DATABASE_URL, "zack");
			final String source = file.getAbsolutePath()+'/'+Properties.DATABASE_URL+"=zack";
			try
			{
				model.setPropertiesInitially(new Properties(props, source));
			}
			catch(RuntimeException e)
			{
				assertEquals(
						"inconsistent initialization for " + Properties.DATABASE_URL +
						", expected " + model.getProperties().getDatabaseUrl() +
						" but got zack.", e.getMessage());
			}
		}
		{
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty(Properties.DATABASE_USER, "zick");
			final String source = file.getAbsolutePath()+'/'+Properties.DATABASE_USER+"=zick";
			try
			{
				model.setPropertiesInitially(new Properties(props, source));
			}
			catch(RuntimeException e)
			{
				assertEquals(
						"inconsistent initialization for " + Properties.DATABASE_USER +
						" between " + model.getProperties().getSource() + " and " + source +
						", expected "+model.getProperties().getDatabaseUser()+" but got zick.", e.getMessage());
			}
		}
		{
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty(Properties.DATABASE_PASSWORD, "zock");
			final String source = file.getAbsolutePath()+'/'+Properties.DATABASE_PASSWORD+"=zock";
			try
			{
				model.setPropertiesInitially(new Properties(props, source));
			}
			catch(RuntimeException e)
			{
				// dont put password into exception message
				assertEquals(
						"inconsistent initialization for " +
						Properties.DATABASE_PASSWORD + ".", e.getMessage());
			}
		}
	}
	
	public void testType()
	{
		final AttributeItem item = null;
		
		assertEquals(false, item.MANDATORY.readOnly);
		assertEquals(false, item.MANDATORY.unique);
		assertEquals(true,  item.MANDATORY.mandatory);
		assertEquals(false, item.OPTIONAL.readOnly);
		assertEquals(false, item.OPTIONAL.unique);
		assertEquals(false, item.OPTIONAL.mandatory);
		assertEquals(false, item.UNIQUE.readOnly);
		assertEquals(true,  item.UNIQUE.unique);
		assertEquals(true,  item.UNIQUE.mandatory);
		assertEquals(false, item.UNIQUE_OPTIONAL.readOnly);
		assertEquals(true,  item.UNIQUE_OPTIONAL.unique);
		assertEquals(false, item.UNIQUE_OPTIONAL.mandatory);
		assertEquals(true,  item.READ_ONLY.readOnly);
		assertEquals(false, item.READ_ONLY.unique);
		assertEquals(true,  item.READ_ONLY.mandatory);
		assertEquals(true,  item.READ_ONLY_OPTIONAL.readOnly);
		assertEquals(false, item.READ_ONLY_OPTIONAL.unique);
		assertEquals(false, item.READ_ONLY_OPTIONAL.mandatory);
		assertEquals(true,  item.READ_ONLY_UNIQUE.readOnly);
		assertEquals(true,  item.READ_ONLY_UNIQUE.unique);
		assertEquals(true,  item.READ_ONLY_UNIQUE.mandatory);
		assertEquals(true,  item.READ_ONLY_UNIQUE_OPTIONAL.readOnly);
		assertEquals(true,  item.READ_ONLY_UNIQUE_OPTIONAL.unique);
		assertEquals(false, item.READ_ONLY_UNIQUE_OPTIONAL.mandatory);

		assertEquals(AttributeItem.class, item.TYPE.getJavaClass());
		assertEquals(item.TYPE, Type.findByJavaClass(AttributeItem.class));
		try
		{
			Type.findByJavaClass(ModelTest.class);
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals("there is no type for class com.exedio.cope.ModelTest", e.getMessage());
		}
		assertEquals(item.TYPE, model.findTypeByID(item.TYPE.getID()));
		
		final Attribute[] attributes = new Attribute[]{
			item.someString,
			item.someNotNullString,
			item.someInteger,
			item.someNotNullInteger,
			item.someLong,
			item.someNotNullLong,
			item.someDouble,
			item.someNotNullDouble,
			item.someDate,
			item.someLongDate,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData.getData(),
			item.someData.getMimeMajor(),
			item.someData.getMimeMinor(),
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getAttributes());
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getDeclaredAttributes());

		final Feature[] features = new Feature[]{
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
			item.someLongDate,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData,
			item.someData.getData(),
			item.someData.getMimeMajor(),
			item.someData.getMimeMinor(),
			item.emptyItem,
		};
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getFeature("someStringUpperCase"));
		
		try
		{
			new Type(NoCreationConstructor.class);
			fail();
		}
		catch(NestingRuntimeException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor:" + NoCreationConstructor.class.getName() +
					".<init>([L" + AttributeValue.class.getName() + ";)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getNestedCause().getClass());
		}

		try
		{
			new Type(NoReactivationConstructor.class);
			fail();
		}
		catch(NestingRuntimeException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor:" + NoReactivationConstructor.class.getName() +
					".<init>(" + ReactivationConstructorDummy.class.getName() + ", int)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getNestedCause().getClass());
		}
	}
	
	static class NoCreationConstructor extends Item
	{
		NoCreationConstructor()
		{
			super(null);
		}
	}

	static class NoReactivationConstructor extends Item
	{
		NoReactivationConstructor(final AttributeValue[] initialAttributes)
		{
			super(null);
		}
	}

}

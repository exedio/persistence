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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;

import com.exedio.cope.testmodel.AttributeItem;

/**
 * Tests the model itself, without creating/using any persistent data.
 * @author Ralf Wiebicke
 */
public class ModelTest extends TestmodelTest
{
	
	public void testSupportsReadCommitted()
	{
		assertEquals( true, model.hasCurrentTransaction() );
		Database realDatabase = model.getDatabase();
		if ( realDatabase instanceof WrappingDatabase )
		{
			realDatabase = ((WrappingDatabase)realDatabase).getWrappedDatabase();
		}
		if ( realDatabase.getClass().getName().equals("com.exedio.cope.HsqldbDatabase") )
		{
			assertEquals( false, model.supportsReadCommitted() );
		}
		else if ( realDatabase.getClass().getName().equals("com.exedio.cope.OracleDatabase") )
		{
			assertEquals( true, model.supportsReadCommitted() );
		}
		else if ( realDatabase.getClass().getName().equals("com.exedio.cope.MysqlDatabase") )
		{
			assertEquals( true, model.supportsReadCommitted() );
		}
		else if ( realDatabase.getClass().getName().equals("com.exedio.cope.PostgresqlDatabase") )
		{
			assertEquals( true, model.supportsReadCommitted() );
		}
		else
		{
			fail( realDatabase.getClass().getName() );
		}
	}
	
	public static final void assertEquals(final String expected, final String actual)
	{
		assertEquals("-----"+expected+"-----"+actual+"-----", expected, actual);
	}
	
	public void testSetPropertiesInitially()
	{
		final Properties mp = model.getProperties();
		final Properties defaultProps = getProperties();
		model.setPropertiesInitially(defaultProps);

		// TODO: use an extra model for the model test, then the following code can be removed
		final String defaultSource = defaultProps.getSource();
		final int defaultSourceSpace = defaultSource.indexOf(' ');
		final File file = new File(defaultSource.substring(0, defaultSourceSpace));
		final java.util.Properties newProps = Properties.loadProperties(file);
		newProps.setProperty("database.forcename.StringItem", "STRINGITEMS");
		newProps.setProperty("database.forcename.STRINGITEMS.this", "STRINGITEM_ID");
		newProps.setProperty("database.forcename.STRINGITEMS.any", "ANY");
		newProps.setProperty("database.forcename.STRINGITEMS.mandatory", "MANDATORY");
		newProps.setProperty("database.forcename.STRINGITEMS.min4", "MIN_4");
		newProps.setProperty("database.forcename.STRINGITEMS.max4", "MAX_4");
		newProps.setProperty("database.forcename.STRINGITEMS.min4Max8", "MIN4_MAX8");
		newProps.setProperty("database.forcename.STRINGITEMS.exact6", "EXACT_6");
		newProps.setProperty("database.forcename.ItemWithSingleUnique", "UNIQUE_ITEMS");
		newProps.setProperty("database.forcename.UNIQUE_ITEMS.this", "UNIQUE_ITEM_ID");
		newProps.setProperty("database.forcename.UNIQUE_ITEMS.uniqueString", "UNIQUE_STRING");
		newProps.setProperty("database.forcename.UNIQUE_ITEMS.otherString", "OTHER_STRING");
		newProps.setProperty("database.forcename.ItemWithSingleUnique_uniqueString_Unq", "IX_ITEMWSU_US");
		
		if(model.getProperties().hasDatadirPath())
		{
			final char SEP = File.separatorChar;
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty(Properties.DATADIR_PATH, props.getProperty(Properties.DATADIR_PATH)+SEP+"AttributeItem");
			final String source = file.getAbsolutePath()+'/'+Properties.DATADIR_PATH+"=/AttributeItem";
			try
			{
				model.setPropertiesInitially(new Properties(props, source));
			}
			catch(RuntimeException e)
			{
				assertEquals(
						"inconsistent initialization for " + Properties.DATADIR_PATH +
						" between " + mp.getSource() + " and " + source +
						", expected " + mp.getDatadirPath() + " but got " + mp.getDatadirPath() + SEP + "AttributeItem.", e.getMessage());
			}
		}
		{
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty(mp.mediaRooturl.key, "zosch");
			final String source = file.getAbsolutePath()+'/'+mp.mediaRooturl.key+"=zosch";
			try
			{
				model.setPropertiesInitially(new Properties(props, source));
			}
			catch(RuntimeException e)
			{
				assertEquals(
						"inconsistent initialization for " + mp.mediaRooturl.key +
						" between " + mp.getSource() + " and " + source +
						", expected " + mp.getMediaRootUrl() +
						" but got zosch.", e.getMessage());
			}

			props.remove(mp.mediaRooturl.key);
			assertEquals(mp.mediaRooturl.getDefaultValue(), new Properties(props, "source").getMediaRootUrl());

			props.setProperty(mp.mediaRooturl.key, "klack");
			assertEquals("klack", new Properties(props, "source").getMediaRootUrl());
		}
		{
			final java.util.Properties props = (java.util.Properties)newProps.clone();
			props.setProperty("zack", "zosch");
			try
			{
				new Properties(props, "wrongKey");
			}
			catch(RuntimeException e)
			{
				assertEquals("property zack in wrongKey is not allowed.", e.getMessage());
			}
		}
	}
	
	public void testType() throws IOException
	{
		final AttributeItem item = null;
		
		assertEquals(false, item.MANDATORY.isFinal);
		assertEquals(false, item.MANDATORY.unique);
		assertEquals(true,  item.MANDATORY.mandatory);
		assertEquals(false, item.OPTIONAL.isFinal);
		assertEquals(false, item.OPTIONAL.unique);
		assertEquals(false, item.OPTIONAL.mandatory);
		assertEquals(false, item.UNIQUE.isFinal);
		assertEquals(true,  item.UNIQUE.unique);
		assertEquals(true,  item.UNIQUE.mandatory);
		assertEquals(false, item.UNIQUE_OPTIONAL.isFinal);
		assertEquals(true,  item.UNIQUE_OPTIONAL.unique);
		assertEquals(false, item.UNIQUE_OPTIONAL.mandatory);
		assertEquals(true,  item.FINAL.isFinal);
		assertEquals(false, item.FINAL.unique);
		assertEquals(true,  item.FINAL.mandatory);
		assertEquals(true,  item.FINAL_OPTIONAL.isFinal);
		assertEquals(false, item.FINAL_OPTIONAL.unique);
		assertEquals(false, item.FINAL_OPTIONAL.mandatory);
		assertEquals(true,  item.FINAL_UNIQUE.isFinal);
		assertEquals(true,  item.FINAL_UNIQUE.unique);
		assertEquals(true,  item.FINAL_UNIQUE.mandatory);
		assertEquals(true,  item.FINAL_UNIQUE_OPTIONAL.isFinal);
		assertEquals(true,  item.FINAL_UNIQUE_OPTIONAL.unique);
		assertEquals(false, item.FINAL_UNIQUE_OPTIONAL.mandatory);

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
			item.day,
			item.someBoolean,
			item.someNotNullBoolean,
			item.someItem,
			item.someNotNullItem,
			item.someEnum,
			item.someNotNullEnum,
			item.someData.getData(),
			item.someData.getMimeMajor(),
			item.someData.getMimeMinor(),
			item.someData.getLastModified(),
		};
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getAttributes());
		assertEqualsUnmodifiable(Arrays.asList(attributes), item.TYPE.getDeclaredAttributes());
		assertEqualsUnmodifiable(list(), item.TYPE.getUniqueConstraints());
		assertEqualsUnmodifiable(list(), item.TYPE.getDeclaredUniqueConstraints());

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
			item.day,
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
			item.someData.getLastModified(),
			item.emptyItem,
		};
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(Arrays.asList(features), item.TYPE.getDeclaredFeatures());
		
		assertEquals(item.someString, item.TYPE.getDeclaredFeature("someString"));
		assertEquals(item.someStringUpperCase, item.TYPE.getDeclaredFeature("someStringUpperCase"));
		
		try
		{
			new Type(NoCreationConstructor.class);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}

		try
		{
			new Type(NoReactivationConstructor.class);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}

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
				final Properties p = model.getProperties();
				prefixed.setProperty(prefix+".cope."+p.databaseDontSupportEmptyStrings, String.valueOf(p.getDatabaseDontSupportEmptyStrings()));
				prefixed.setProperty(prefix+".cope."+p.pksourceButterfly, String.valueOf(p.getPkSourceButterfly()));
				final PrintStream out = new PrintStream(new FileOutputStream(file, true));
				prefixed.store(out, null);
				out.close();
			}
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

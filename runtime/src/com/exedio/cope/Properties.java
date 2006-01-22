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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public final class Properties extends com.exedio.cope.util.Properties
{

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	private final StringField databaseCode = new StringField("database");
	final StringField databaseUrl =  new StringField("database.url");
	final StringField databaseUser =  new StringField("database.user");
	final StringField databasePassword =  new StringField("database.password", true);
	final BooleanField databaseLog = new BooleanField("database.log", false);
	
	final BooleanField databaseDontSupportPreparedStatements = new BooleanField("database.dontSupport.preparedStatements", false);
	final BooleanField databaseDontSupportEmptyStrings = new BooleanField("database.dontSupport.emptyStrings", false);
	final BooleanField databaseDontSupportNativeDate = new BooleanField("database.dontSupport.nativeDate", false);
	final BooleanField databaseDontSupportLimit = new BooleanField("database.dontSupport.limit", false);
	
	static final String DATABASE_FORCE_NAME = "database.forcename";
	static final String DATABASE_TABLE_OPTION = "database.tableOption";
	
	static final String PKSOURCE_BUTTERFLY = "pksource.butterfly";
	final BooleanField pksourceButterfly = new BooleanField(PKSOURCE_BUTTERFLY, false);
	final BooleanField fulltextIndex = new BooleanField("fulltextIndex", false);
	final IntField connectionPoolMaxIdle = new IntField("connectionPool.maxIdle", 10, 0);
	
	final IntField cacheLimit = new IntField("cache.limit", 10000, 0);
	final IntField cacheQueryLimit = new IntField("cache.queryLimit", 10000, 0);
	final BooleanField cacheQueryLogging = new BooleanField("cache.queryLogging", false);
	
	public static final String DATADIR_PATH = "datadir.path";
	final StringField mediaRooturl =  new StringField("media.rooturl", "media/");
	final IntField mediaOffsetExpires = new IntField("media.offsetExpires", 1000 * 5, 0);
	
	private final String source;

	// NOTE:
	// If you another attributes here,
	// you probably have to add another
	// test to ensureEquality as well.
	private final Constructor database;
	private final java.util.Properties databaseForcedNames;
	private final java.util.Properties databaseTableOptions;
	private final java.util.Properties databaseCustomProperties;
	
	private final File datadirPath;

	public Properties()
	{
		this(getDefaultPropertyFile());
	}
	
	public static final File getDefaultPropertyFile()
	{
		String filename = System.getProperty(FILE_NAME_PROPERTY);
		if(filename==null)
			filename = DEFAULT_FILE_NAME;

		return new File(filename);
	}

	public Properties(final String propertyFileName)
	{
		this(new File(propertyFileName));
	}
	
	public static final java.util.Properties loadProperties(final File propertyFile)
	{
		final java.util.Properties properties = new java.util.Properties();
		FileInputStream stream = null;
		try
		{
			stream = new FileInputStream(propertyFile);
			properties.load(stream);
			return properties;
		}
		catch(IOException e)
		{
			throw new RuntimeException("property file "+propertyFile.getAbsolutePath()+" not found.", e);
		}
		finally
		{
			if(stream!=null)
			{
				try
				{
					stream.close();
				}
				catch(IOException e) {}
			}
		}
	}

	public Properties(final File propertyFile)
	{
		this(loadProperties(propertyFile), propertyFile.getAbsolutePath());
	}

	public Properties(final java.util.Properties properties, final String source)
	{
		super(properties, source);
		this.source = source;

		final String databaseCustomPropertiesPrefix;
		{
			final String databaseCode = this.databaseCode.value;
			if(databaseCode.length()<=2)
				throw new RuntimeException("database from "+source+" must have at least two characters, but was "+databaseCode);

			databaseCustomPropertiesPrefix = "database." + databaseCode;

			database = getDatabaseConstructor( databaseCode, source );
		}

		databaseForcedNames = getPropertyMap(properties, DATABASE_FORCE_NAME);
		databaseCustomProperties = getPropertyMap(properties, databaseCustomPropertiesPrefix);
		databaseTableOptions = getPropertyMap(properties, DATABASE_TABLE_OPTION);
		
		final String datadirPathString  = properties.getProperty(DATADIR_PATH);
		if(datadirPathString!=null)
		{
			final File datadirPathTest = new File(datadirPathString);

			if(!datadirPathTest.exists())
				throw new RuntimeException(DATADIR_PATH + ' ' + datadirPathTest.getAbsolutePath() + " does not exist.");
			if(!datadirPathTest.isDirectory())
				throw new RuntimeException(DATADIR_PATH + ' ' + datadirPathTest.getAbsolutePath() + " is not a directory.");
			if(!datadirPathTest.canRead())
				throw new RuntimeException(DATADIR_PATH + ' ' + datadirPathTest.getAbsolutePath() + " is not readable.");
			if(!datadirPathTest.canWrite())
				throw new RuntimeException(DATADIR_PATH + ' ' + datadirPathTest.getAbsolutePath() + " is not writable.");
			try
			{
				datadirPath = datadirPathTest.getCanonicalFile();
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			datadirPath = null;
		}

		{
			final HashSet allowedValues = new HashSet(Arrays.asList(new String[]{
					databaseCode.key,
					databaseUrl.key,
					databaseUser.key,
					databasePassword.key,
					databaseLog.key,
					databaseDontSupportPreparedStatements.key,
					databaseDontSupportEmptyStrings.key,
					databaseDontSupportNativeDate.key,
					databaseDontSupportLimit.key,
					pksourceButterfly.key,
					fulltextIndex.key,
					connectionPoolMaxIdle.key,
					cacheLimit.key,
					cacheQueryLimit.key,
					cacheQueryLogging.key,
					DATADIR_PATH,
					mediaRooturl.key,
					mediaOffsetExpires.key,
				}));
			for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
			{
				final String key = (String)i.next();
				if(!allowedValues.contains(key)
					&&	!key.startsWith(databaseCustomPropertiesPrefix+'.')
					&&	!key.startsWith(DATABASE_FORCE_NAME+'.')
					&&	!key.startsWith(DATABASE_TABLE_OPTION+'.')
					&&	!key.startsWith("x-build."))
					throw new RuntimeException("property "+key+" in "+source+" is not allowed.");
			}
		}

	}
	
	private static Constructor getDatabaseConstructor( String databaseCode, String source )
	{
		final String databaseName =
			"com.exedio.cope." +
			Character.toUpperCase(databaseCode.charAt(0)) +
			databaseCode.substring(1) +
			"Database";

		final Class databaseClass;
		try
		{
			databaseClass = Class.forName(databaseName);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" not found.");
		}

		if(!Database.class.isAssignableFrom(databaseClass))
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" not a subclass of "+Database.class.getName()+".");
		}
		try
		{
			return databaseClass.getDeclaredConstructor(new Class[]{Properties.class});
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" has no constructor with a single Properties argument.");
		}
	}
	
	private final RuntimeException newNotSetException(final String key)
	{
		return new RuntimeException("property "+key+" in "+source+" not set.");
	}
	
	private java.util.Properties getPropertyMap(final java.util.Properties properties, String prefix)
	{
		final java.util.Properties result = new java.util.Properties();
		prefix = prefix + '.';
		final int length = prefix.length();

		for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
		{
			final String key = (String)i.next();
			if(key.startsWith(prefix))
				result.put(key.substring(length), properties.getProperty(key));
		}
		return result;
	}

	Database createDatabase()
	{
		return createDatabase( database );
	}
	
	Database createDatabase( String databaseCode )
	{
		return createDatabase( getDatabaseConstructor(databaseCode, source) );
	}
	
	private Database createDatabase( Constructor constructor )
	{
		try
		{
			return (Database)constructor.newInstance(new Object[]{this});
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getDatabase()
	{
		return database.getDeclaringClass().getName();
	}

	public String getDatabaseUrl()
	{
		return databaseUrl.value;
	}

	public String getDatabaseUser()
	{
		return databaseUser.value;
	}

	public String getDatabasePassword()
	{
		return databasePassword.value;
	}
	
	public boolean getDatabaseLog()
	{
		return databaseLog.value;
	}
	
	public boolean getDatabaseDontSupportPreparedStatements()
	{
		return databaseDontSupportPreparedStatements.value;
	}
	
	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return databaseDontSupportEmptyStrings.value;
	}
	
	public boolean getDatabaseDontSupportLimit()
	{
		return databaseDontSupportLimit.value;
	}
	
	public boolean getDatabaseDontSupportNativeDate()
	{
		return databaseDontSupportNativeDate.value;
	}
	
	java.util.Properties getDatabaseForcedNames()
	{
		return databaseForcedNames;
	}
	
	java.util.Properties getDatabaseTableOptions()
	{
		return databaseTableOptions;
	}
	
	String getDatabaseCustomProperty(final String key)
	{
		return databaseCustomProperties.getProperty(key);
	}
	
	public boolean getPkSourceButterfly()
	{
		return pksourceButterfly.value;
	}
	
	public boolean getFulltextIndex()
	{
		return fulltextIndex.value;
	}
	
	public int getConnectionPoolMaxIdle()
	{
		return connectionPoolMaxIdle.value;
	}
	
	public int getCacheLimit()
	{
		return cacheLimit.value;
	}
	
	public int getCacheQueryLimit()
	{
		return cacheQueryLimit.value;
	}
	
	public boolean getCacheQueryLogging()
	{
		return cacheQueryLogging.value;
	}
	
	public boolean hasDatadirPath()
	{
		return datadirPath!=null;
	}
	
	public File getDatadirPath()
	{
		if(datadirPath==null)
			throw newNotSetException(DATADIR_PATH);

		return datadirPath;
	}
	
	public String getMediaRootUrl()
	{
		return mediaRooturl.value;
	}
	
	/**
	 * Returns the offset, the Expires http header of media
	 * is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 * If zero, no Expires header is sent.
	 * 
	 * TODO: make this configurable per media as well.
	 */
	public int getMediaOffsetExpires()
	{
		return mediaOffsetExpires.value;
	}
	
	final void ensureEquality(final Properties other)
	{
		super.ensureEquality(other);
		
		ensureEquality(other, databaseCode.key, this.getDatabase(), other.getDatabase());
		ensureEquality(other, DATABASE_FORCE_NAME, this.databaseForcedNames, other.databaseForcedNames);
		ensureEquality(other, "database.DATABASE.*", this.databaseCustomProperties, other.databaseCustomProperties);
		ensureEquality(other, DATADIR_PATH, this.datadirPath, other.datadirPath);
	}
	
	private final void ensureEquality(
			final Properties other, final String name,
			final Object thisValue, final Object otherValue)
	{
		if((thisValue!=null && !thisValue.equals(otherValue)) ||
			(thisValue==null && otherValue!=null))
			throw new RuntimeException(
					"inconsistent initialization for " + name +
					" between " + source + " and " + other.source +
					"," + " expected " + thisValue + " but got " + otherValue + '.');
	}
}

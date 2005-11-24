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

public final class Properties
{

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	public static final String DATABASE = "database";
	public static final String DATABASE_URL = "database.url";
	public static final String DATABASE_USER = "database.user";
	public static final String DATABASE_PASSWORD = "database.password";
	public static final String DATABASE_LOG = "database.log";
	public static final String DATABASE_DONT_SUPPORT_PREPARED_STATEMENTS = "database.dontSupport.preparedStatements";
	public static final String DATABASE_DONT_SUPPORT_EMPTY_STRINGS = "database.dontSupport.emptyStrings";
	public static final String DATABASE_DONT_SUPPORT_NATIVE_DATE = "database.dontSupport.nativeDate";
	public static final String DATABASE_DONT_SUPPORT_LIMIT = "database.dontSupport.limit";
	static final String DATABASE_FORCE_NAME = "database.forcename";
	static final String DATABASE_TABLE_OPTION = "database.tableOption";
	public static final String PKSOURCE_BUTTERFLY = "pksource.butterfly";
	public static final String FULLTEXT_INDEX = "fulltextIndex";
	public static final String CONNECTION_POOL_MAX_IDLE = "connectionPool.maxIdle";
	public static final String CACHE_LIMIT = "cache.limit";
	public static final String CACHE_QUERY_LIMIT = "cache.queryLimit";
	public static final String CACHE_QUERY_LOGGING = "cache.queryLogging";
	public static final String DATADIR_PATH = "datadir.path";
	public static final String MEDIA_ROOT_URL = "media.rooturl";
	public static final String MEDIA_ROOT_URL_DEFAULT = "media/";
	public static final String MEDIA_OFFSET_EXPIRES = "media.offsetExpires";

	private final String source;

	// NOTE:
	// If you another attributes here,
	// you probably have to add another
	// test to ensureEquality as well.
	private final Constructor database;
	private final String databaseUrl;
	private final String databaseUser;
	private final String databasePassword;
	private final boolean databaseLog;
	private final boolean databaseDontSupportPreparedStatements;
	private final boolean databaseDontSupportEmptyStrings;
	private final boolean databaseDontSupportNativeDate;
	private final boolean databaseDontSupportLimit;
	private final java.util.Properties databaseForcedNames;
	private final java.util.Properties databaseTableOptions;
	private final java.util.Properties databaseCustomProperties;
	
	private final boolean pkSourceButterfly;
	private final boolean fulltextIndex;
	private final int connectionPoolMaxIdle;
	private final int cacheLimit;
	private final int cacheQueryLimit;
	private final boolean cacheQueryLogging;

	private final File datadirPath;
	private final String mediaRootUrl;
	private final int mediaOffsetExpires;

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
			throw new NestingRuntimeException(e, "property file "+propertyFile.getAbsolutePath()+" not found.");
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
		this.source = source;

		final String databaseCustomPropertiesPrefix;
		{
			final String databaseCode = getPropertyNotNull(properties, DATABASE);
			if(databaseCode.length()<=2)
				throw new RuntimeException("database from "+source+" must have at least two characters, but was "+databaseCode);

			databaseCustomPropertiesPrefix = "database." + databaseCode;

			database = getDatabaseConstructor( databaseCode, source );
		}

		databaseForcedNames = getPropertyMap(properties, DATABASE_FORCE_NAME);
		databaseCustomProperties = getPropertyMap(properties, databaseCustomPropertiesPrefix);
		databaseTableOptions = getPropertyMap(properties, DATABASE_TABLE_OPTION);
		databaseUrl = getPropertyNotNull(properties, DATABASE_URL);
		databaseUser = getPropertyNotNull(properties, DATABASE_USER);
		databasePassword = getPropertyNotNull(properties, DATABASE_PASSWORD);
		
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
				throw new NestingRuntimeException(e);
			}
		}
		else
		{
			datadirPath = null;
		}

		final String explicitMediaRootUrl = properties.getProperty(MEDIA_ROOT_URL);
		mediaRootUrl = explicitMediaRootUrl!=null ? explicitMediaRootUrl : MEDIA_ROOT_URL_DEFAULT;
		mediaOffsetExpires = getPropertyInt(properties, MEDIA_OFFSET_EXPIRES, 1000 * 5, 0);
		
		this.databaseDontSupportPreparedStatements = getPropertyBoolean(properties, DATABASE_DONT_SUPPORT_PREPARED_STATEMENTS, false);
		this.databaseDontSupportEmptyStrings = getPropertyBoolean(properties, DATABASE_DONT_SUPPORT_EMPTY_STRINGS, false);
		this.databaseDontSupportNativeDate = getPropertyBoolean(properties, DATABASE_DONT_SUPPORT_NATIVE_DATE, false);
		this.databaseDontSupportLimit = getPropertyBoolean(properties, DATABASE_DONT_SUPPORT_LIMIT, false);
		this.pkSourceButterfly = getPropertyBoolean(properties, PKSOURCE_BUTTERFLY, false);
		this.fulltextIndex = getPropertyBoolean(properties, FULLTEXT_INDEX, false);
		this.connectionPoolMaxIdle = getPropertyInt(properties, CONNECTION_POOL_MAX_IDLE, 10, 0);
		this.cacheLimit = getPropertyInt(properties, CACHE_LIMIT, 10000, 0);
		this.cacheQueryLimit = getPropertyInt(properties, CACHE_QUERY_LIMIT, 10000, 0);
		this.cacheQueryLogging = getPropertyBoolean(properties, CACHE_QUERY_LOGGING, false);
		this.databaseLog = getPropertyBoolean(properties, DATABASE_LOG, false);
		
		{
			final HashSet allowedValues = new HashSet(Arrays.asList(new String[]{
					DATABASE,
					DATABASE_URL,
					DATABASE_USER,
					DATABASE_PASSWORD,
					DATABASE_LOG,
					DATABASE_DONT_SUPPORT_PREPARED_STATEMENTS,
					DATABASE_DONT_SUPPORT_EMPTY_STRINGS,
					DATABASE_DONT_SUPPORT_NATIVE_DATE,
					DATABASE_DONT_SUPPORT_LIMIT,
					PKSOURCE_BUTTERFLY,
					FULLTEXT_INDEX,
					CONNECTION_POOL_MAX_IDLE,
					CACHE_LIMIT,
					CACHE_QUERY_LIMIT,
					CACHE_QUERY_LOGGING,
					DATADIR_PATH,
					MEDIA_ROOT_URL,
					MEDIA_OFFSET_EXPIRES,
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
	
	private String getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(result==null)
			throw newNotSetException(key);

		return result;
	}

	private boolean getPropertyBoolean(final java.util.Properties properties, final String key, final boolean defaultValue)
	{
		final String s = properties.getProperty(key);
		if(s==null)
			return defaultValue;
		else
		{
			if(s.equals("true"))
				return true;
			else if(s.equals("false"))
				return false;
			else
				throw new RuntimeException("property "+key+" in "+source+" has invalid value, expected >true< or >false<, but got >"+s+"<.");
		}
	}

	private int getPropertyInt(final java.util.Properties properties, final String key, final int defaultValue, final int minimumValue)
	{
		if(defaultValue<minimumValue)
			throw new RuntimeException(key+defaultValue+','+minimumValue);
		
		final String s = properties.getProperty(key);
		if(s==null)
			return defaultValue;
		else
		{
			final int result;
			
			try
			{
				result = Integer.parseInt(s);
			}
			catch(NumberFormatException e)
			{
				throw new RuntimeException(
						"property " + key + " in " + source + " has invalid value, " +
						"expected an integer greater " + minimumValue + ", but got >" + s + "<.", e);
			}

			if(result<minimumValue)
				throw new RuntimeException(
						"property " + key + " in " + source + " has invalid value, " +
						"expected an integer greater " + minimumValue + ", but got " +result + '.');

			return result;
		}
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
		try
		{
			return (Database)database.newInstance(new Object[]{this});
		}
		catch(InstantiationException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	public String getSource()
	{
		return source;
	}

	public String getDatabase()
	{
		return database.getDeclaringClass().getName();
	}

	public String getDatabaseUrl()
	{
		return databaseUrl;
	}

	public String getDatabaseUser()
	{
		return databaseUser;
	}

	public String getDatabasePassword()
	{
		return databasePassword;
	}
	
	public boolean getDatabaseLog()
	{
		return databaseLog;
	}
	
	public boolean getDatabaseDontSupportPreparedStatements()
	{
		return databaseDontSupportPreparedStatements;
	}
	
	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return databaseDontSupportEmptyStrings;
	}
	
	public boolean getDatabaseDontSupportLimit()
	{
		return databaseDontSupportLimit;
	}
	
	public boolean getDatabaseDontSupportNativeDate()
	{
		return databaseDontSupportNativeDate;
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
		return pkSourceButterfly;
	}
	
	public boolean getFulltextIndex()
	{
		return fulltextIndex;
	}
	
	public int getConnectionPoolMaxIdle()
	{
		return connectionPoolMaxIdle;
	}
	
	public int getCacheLimit()
	{
		return cacheLimit;
	}
	
	public int getCacheQueryLimit()
	{
		return cacheQueryLimit;
	}
	
	public boolean getCacheQueryLogging()
	{
		return cacheQueryLogging;
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
		return mediaRootUrl;
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
		return mediaOffsetExpires;
	}
	
	final void ensureEquality(final Properties other)
	{
		ensureEquality(other, DATABASE, this.getDatabase(), other.getDatabase());
		ensureEquality(other, DATABASE_URL, this.databaseUrl, other.databaseUrl);
		ensureEquality(other, DATABASE_USER, this.databaseUser, other.databaseUser);
		ensureEquality(other, DATABASE_PASSWORD, this.databasePassword, other.databasePassword, true);
		ensureEquality(other, DATABASE_LOG, this.databaseLog, other.databaseLog);
		ensureEquality(other, DATABASE_DONT_SUPPORT_PREPARED_STATEMENTS, this.databaseDontSupportPreparedStatements, other.databaseDontSupportPreparedStatements);
		ensureEquality(other, DATABASE_DONT_SUPPORT_EMPTY_STRINGS, this.databaseDontSupportEmptyStrings, other.databaseDontSupportEmptyStrings);
		ensureEquality(other, DATABASE_DONT_SUPPORT_NATIVE_DATE, this.databaseDontSupportNativeDate, other.databaseDontSupportNativeDate);
		ensureEquality(other, DATABASE_DONT_SUPPORT_LIMIT, this.databaseDontSupportLimit, other.databaseDontSupportLimit);
		ensureEquality(other, DATABASE_FORCE_NAME, this.databaseForcedNames, other.databaseForcedNames);
		ensureEquality(other, "database.DATABASE.*", this.databaseCustomProperties, other.databaseCustomProperties);
		
		ensureEquality(other, PKSOURCE_BUTTERFLY, this.pkSourceButterfly, other.pkSourceButterfly);
		ensureEquality(other, FULLTEXT_INDEX, this.fulltextIndex, other.fulltextIndex);
		ensureEquality(other, CONNECTION_POOL_MAX_IDLE, this.connectionPoolMaxIdle, other.connectionPoolMaxIdle);
		ensureEquality(other, CACHE_LIMIT, this.cacheLimit, other.cacheLimit);
		ensureEquality(other, CACHE_QUERY_LIMIT, this.cacheQueryLimit, other.cacheQueryLimit);
		ensureEquality(other, CACHE_QUERY_LOGGING, this.cacheQueryLogging, other.cacheQueryLogging);
		ensureEquality(other, DATADIR_PATH, this.datadirPath, other.datadirPath);
		ensureEquality(other, MEDIA_ROOT_URL, this.mediaRootUrl, other.mediaRootUrl);
		ensureEquality(other, MEDIA_OFFSET_EXPIRES, this.mediaOffsetExpires, other.mediaOffsetExpires);
	}
	
	private final void ensureEquality(
			final Properties other, final String name,
			final boolean thisValue, final boolean otherValue)
	{
		ensureEquality(other, name, Boolean.valueOf(thisValue), Boolean.valueOf(otherValue), false);
	}
	
	private final void ensureEquality(
			final Properties other, final String name,
			final int thisValue, final int otherValue)
	{
		ensureEquality(other, name, new Integer(thisValue), new Integer(otherValue), false);
	}
	
	private final void ensureEquality(
			final Properties other, final String name,
			final Object thisValue, final Object otherValue)
	{
		ensureEquality(other, name, thisValue, otherValue, false);
	}
	
	private final void ensureEquality(
			final Properties other, final String name,
			final Object thisValue, final Object otherValue,
			final boolean hideValues)
	{
		if((thisValue!=null && !thisValue.equals(otherValue)) ||
			(thisValue==null && otherValue!=null))
			throw new RuntimeException(
					"inconsistent initialization for " + name +
					" between " + source + " and " + other.source +
					(hideValues ? "." : "," + " expected " + thisValue + " but got " + otherValue + '.'));
	}
}

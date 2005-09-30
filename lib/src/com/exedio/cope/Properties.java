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
import java.util.Iterator;

public final class Properties
{

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	public static final String DATABASE = "database";
	public static final String DATABASE_URL = "database.url";
	public static final String DATABASE_USER = "database.user";
	public static final String DATABASE_PASSWORD = "database.password";
	public static final String DATABASE_DONT_SUPPORT_EMPTY_STRINGS = "database.dont.support.empty.strings";
	static final String DATABASE_FORCE_NAME = "database.forcename";
	public static final String PKSOURCE_BUTTERFLY = "pksource.butterfly";
	public static final String CONNECTION_POOL_MAX_IDLE = "connectionPool.maxIdle";
	public static final String DATADIR_PATH = "datadir.path";
	public static final String MEDIA_ROOT_URL = "media.rooturl";
	public static final String MEDIA_ROOT_URL_DEFAULT = "media/";

	private final String source;

	// NOTE:
	// If you another attributes here,
	// you probably have to add another
	// test to ensureEquality as well.
	private final Constructor database;
	private final String databaseUrl;
	private final String databaseUser;
	private final String databasePassword;
	private final boolean databaseDontSupportEmptyStrings;
	private final java.util.Properties databaseForcedNames;
	private final java.util.Properties databaseCustomProperties;
	
	private final boolean pkSourceButterfly;
	private final int connectionPoolMaxIdle;

	private final File datadirPath;
	private final String mediaRootUrl;

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

		{
			final String databaseCode = getPropertyNotNull(properties, DATABASE);
			if(databaseCode.length()<=2)
				throw new RuntimeException("database from "+source+" must have at least two characters, but was "+databaseCode);

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
				database = databaseClass.getDeclaredConstructor(new Class[]{Properties.class});
			}
			catch(NoSuchMethodException e)
			{
				throw new RuntimeException("class "+databaseName+" from "+source+" has no constructor with a single Properties argument.");
			}

			{
				// TODO use some kind of prefix property view
				databaseForcedNames = new java.util.Properties();
				final String databaseForceNamePrefix = DATABASE_FORCE_NAME + '.';
				final int databaseForceNamePrefixLength = databaseForceNamePrefix.length();

				databaseCustomProperties = new java.util.Properties();
				final String databaseCustomPropertiesPrefix = "database." + databaseCode + '.';
				final int databaseCustomPropertiesPrefixLength = databaseCustomPropertiesPrefix.length();

				for(Iterator i = properties.keySet().iterator(); i.hasNext(); )
				{
					final String key = (String)i.next();
					if(key.startsWith(databaseForceNamePrefix))
						databaseForcedNames.put(key.substring(databaseForceNamePrefixLength), properties.getProperty(key));
					if(key.startsWith(databaseCustomPropertiesPrefix))
						databaseCustomProperties.put(key.substring(databaseCustomPropertiesPrefixLength), properties.getProperty(key));
				}
			}
		}

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
		
		this.databaseDontSupportEmptyStrings = getPropertyBoolean(properties, DATABASE_DONT_SUPPORT_EMPTY_STRINGS, false);
		this.pkSourceButterfly = getPropertyBoolean(properties, PKSOURCE_BUTTERFLY, false);
		this.connectionPoolMaxIdle = getPropertyInt(properties, CONNECTION_POOL_MAX_IDLE, 10, 5);
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
				throw new RuntimeException("property "+key+" in "+source+" has invalid value, expected >true< or >false< bot got >"+s+"<.");
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
	
	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return databaseDontSupportEmptyStrings;
	}
	
	java.util.Properties getDatabaseForcedNames()
	{
		return databaseForcedNames;
	}
	
	String getDatabaseCustomProperty(final String key)
	{
		return databaseCustomProperties.getProperty(key);
	}
	
	public boolean getPkSourceButterfly()
	{
		return pkSourceButterfly;
	}
	
	public int getConnectionPoolMaxIdle()
	{
		return connectionPoolMaxIdle;
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
	
	final void ensureEquality(final Properties other)
	{
		ensureEquality(other, DATABASE, this.getDatabase(), other.getDatabase());
		ensureEquality(other, DATABASE_URL, this.databaseUrl, other.databaseUrl);
		ensureEquality(other, DATABASE_USER, this.databaseUser, other.databaseUser);
		ensureEquality(other, DATABASE_PASSWORD, this.databasePassword, other.databasePassword, true);
		ensureEquality(other, DATABASE_DONT_SUPPORT_EMPTY_STRINGS, this.databaseDontSupportEmptyStrings, other.databaseDontSupportEmptyStrings);
		ensureEquality(other, DATABASE_FORCE_NAME, this.databaseForcedNames, other.databaseForcedNames);
		
		ensureEquality(other, PKSOURCE_BUTTERFLY, this.pkSourceButterfly, other.pkSourceButterfly);
		ensureEquality(other, CONNECTION_POOL_MAX_IDLE, this.connectionPoolMaxIdle, other.connectionPoolMaxIdle);
		ensureEquality(other, DATADIR_PATH, this.datadirPath, other.datadirPath);
		ensureEquality(other, MEDIA_ROOT_URL, this.mediaRootUrl, other.mediaRootUrl);
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

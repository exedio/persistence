
package com.exedio.cope.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class Properties
{

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	private static final String DATABASE = "database";
	private static final String DATABASE_URL = "database.url";
	private static final String DATABASE_USER = "database.user";
	private static final String DATABASE_PASSWORD = "database.password";
	private static final String MEDIA_DIRECTORY = "media.directory";
	private static final String MEDIA_URL = "media.url";

	private final String source;

	private final Constructor database;
	private final String databaseUrl;
	private final String databaseUser;
	private final String databasePassword;

	private final File mediaDirectory;
	private final String mediaUrl;

	public Properties()
	{
		this(getDefaultPropertyFile());
	}
	
	private static final File getDefaultPropertyFile()
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
				"com.exedio.cope.lib." +
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
		}

		databaseUrl = getPropertyNotNull(properties, DATABASE_URL);
		databaseUser = getPropertyNotNull(properties, DATABASE_USER);
		databasePassword = getPropertyNotNull(properties, DATABASE_PASSWORD);

		final String mediaDirectoryString  = properties.getProperty(MEDIA_DIRECTORY);
		if(mediaDirectoryString!=null)
		{
			final File mediaDirectoryTest = new File(mediaDirectoryString);

			if(!mediaDirectoryTest.exists())
				throw new RuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" does not exist.");
			if(!mediaDirectoryTest.isDirectory())
				throw new RuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not a directory.");
			if(!mediaDirectoryTest.canRead())
				throw new RuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not readable.");
			if(!mediaDirectoryTest.canWrite())
				throw new RuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not writable.");
			try
			{
				mediaDirectory = mediaDirectoryTest.getCanonicalFile();
			}
			catch(IOException e)
			{
				throw new NestingRuntimeException(e);
			}
			mediaUrl  = getPropertyNotNull(properties, MEDIA_URL);
		}
		else
		{
			mediaDirectory = null;
			mediaUrl  = null;
		}
	}
	
	private String getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(result==null)
			throw new RuntimeException("property "+key+" in "+source+" not set.");

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
	
	public File getMediaDirectory()
	{
		if(mediaDirectory==null)
			throw new RuntimeException("property media.directory in "+source+" not set.");

		return mediaDirectory;
	}

	public String getMediaUrl()
	{
		if(mediaDirectory==null)
			throw new RuntimeException("property media.directory in "+source+" not set.");

		return mediaUrl;
	}
	
	public java.util.Properties toProperties()
	{
		final java.util.Properties properties = new java.util.Properties();
		properties.setProperty("source", source);
		properties.setProperty(DATABASE, database.getClass().getName());
		properties.setProperty(DATABASE_URL, databaseUrl);
		properties.setProperty(DATABASE_USER, databaseUser);
		properties.setProperty(DATABASE_PASSWORD, databasePassword);
		properties.setProperty(MEDIA_DIRECTORY, mediaDirectory.getAbsolutePath());
		properties.setProperty(MEDIA_URL, mediaUrl);
		return properties;
	}
	
}

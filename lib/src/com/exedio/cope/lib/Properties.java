
package com.exedio.cope.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class Properties
{

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	private final String source;

	private final String database;
	private final String databaseDriver;
	private final String databaseUrl;
	private final String databaseUser;
	private final String databasePassword;

	private final File mediaDirectory;
	private final String mediaUrl;

	public Properties()
	{
		final java.util.Properties properties = new java.util.Properties();
		FileInputStream stream = null;
		File file = null;
		try
		{
			String filename = System.getProperty(FILE_NAME_PROPERTY);
			if(filename==null)
				filename = DEFAULT_FILE_NAME;
			
			file = new File(filename);
			source = file.getAbsolutePath();
			stream = new FileInputStream(file);
			properties.load(stream);
		}
		catch(IOException e)
		{
			throw new InitializerRuntimeException(e, "property file "+file.getAbsolutePath()+" not found.");
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
		database = getPropertyNotNull(properties, "database");
		databaseDriver = getPropertyNotNull(properties, "database.driver");
		databaseUrl = getPropertyNotNull(properties, "database.url");
		databaseUser = getPropertyNotNull(properties, "database.user");
		databasePassword = getPropertyNotNull(properties, "database.password");

		final String mediaDirectoryString  = properties.getProperty("media.directory");
		if(mediaDirectoryString!=null)
		{
			final File mediaDirectoryTest = new File(mediaDirectoryString);

			if(!mediaDirectoryTest.exists())
				throw new InitializerRuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" does not exist.");
			if(!mediaDirectoryTest.isDirectory())
				throw new InitializerRuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not a directory.");
			if(!mediaDirectoryTest.canRead())
				throw new InitializerRuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not readable.");
			if(!mediaDirectoryTest.canWrite())
				throw new InitializerRuntimeException("media directory "+mediaDirectoryTest.getAbsolutePath()+" is not writable.");
			try
			{
				mediaDirectory = mediaDirectoryTest.getCanonicalFile();
			}
			catch(IOException e)
			{
				throw new InitializerRuntimeException(e);
			}
			mediaUrl  = getPropertyNotNull(properties, "media.url");
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
			throw new InitializerRuntimeException("property "+key+" in "+source+" not set.");

		return result;
	}

	public String getDatabase()
	{
		return database;
	}

	public String getDatabaseDriver()
	{
		return databaseDriver;
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
			throw new InitializerRuntimeException("property media.directory in "+source+" not set.");

		return mediaDirectory;
	}

	public String getMediaUrl()
	{
		if(mediaDirectory==null)
			throw new InitializerRuntimeException("property media.directory in "+source+" not set.");

		return mediaUrl;
	}

}

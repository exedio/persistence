
package com.exedio.cope.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

final class Properties
{
	private static Properties theInstance = null;
	
	static Properties getInstance()
	{
		if(theInstance!=null)
			return theInstance;
		
		theInstance = new Properties();
		return theInstance;
	}

	private static final String FILE_NAME_PROPERTY = "com.exedio.cope.properties";
	private static final String DEFAULT_FILE_NAME = "cope.properties";
	
	private File file = null;
	private final String database;
	private final String driver;
	private final String url;
	private final String user;
	private final String password;
	private final File mediaDirectory;

	private Properties()
	{
		final java.util.Properties properties = new java.util.Properties();
		FileInputStream stream = null;
		try
		{
			String filename = System.getProperty(FILE_NAME_PROPERTY);
			if(filename==null)
				filename = DEFAULT_FILE_NAME;
			
			file = new File(filename);
			stream = new FileInputStream(file);
			properties.load(stream);
		}
		catch(IOException e)
		{
			throw new InitializerRuntimeException(e, "ERROR: property file "+file.getAbsolutePath()+" not found.");
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
		driver = getPropertyNotNull(properties, "database.driver");
		url = getPropertyNotNull(properties, "database.url");
		user = getPropertyNotNull(properties, "database.user");
		password = getPropertyNotNull(properties, "database.password");
		final String mediaDirectoryString  = getPropertyNotNull(properties, "media.directory");
		final File mediaDirectoryTest = new File(mediaDirectoryString);

		if(!mediaDirectoryTest.exists())
			throw new InitializerRuntimeException("ERROR: media directory "+mediaDirectoryTest.getAbsolutePath()+" does not exist.");
		if(!mediaDirectoryTest.isDirectory())
			throw new InitializerRuntimeException("ERROR: media directory "+mediaDirectoryTest.getAbsolutePath()+" is not a directory.");
		if(!mediaDirectoryTest.canRead())
			throw new InitializerRuntimeException("ERROR: media directory "+mediaDirectoryTest.getAbsolutePath()+" is not readable.");
		if(!mediaDirectoryTest.canWrite())
			throw new InitializerRuntimeException("ERROR: media directory "+mediaDirectoryTest.getAbsolutePath()+" is not writable.");
		try
		{
			mediaDirectory = mediaDirectoryTest.getCanonicalFile();
		}
		catch(IOException e)
		{
			throw new InitializerRuntimeException(e);
		}
	}
	
	private String getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(result==null)
			throw new InitializerRuntimeException("ERROR: property "+key+" in "+file.getAbsolutePath()+" not set.");

		return result;
	}

	public String getDatabase()
	{
		return database;
	}

	public String getDatabaseDriver()
	{
		return driver;
	}

	public String getDatabaseUrl()
	{
		return url;
	}

	public String getDatabaseUser()
	{
		return user;
	}

	public String getDatabasePassword()
	{
		return password;
	}
	
	public File getMediaDirectory()
	{
		return mediaDirectory;
	}

}

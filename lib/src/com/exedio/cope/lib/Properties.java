
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
			final String m = "ERROR: property file "+file.getAbsolutePath()+" not found.";
			System.out.println(m);
			throw new SystemException(e, m);
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
	}
	
	private String getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(result==null)
		{
			final String m = "ERROR: property "+key+" in "+file.getAbsolutePath()+" not set.";
			System.out.println(m);
			throw new RuntimeException(m);
		}
		return result;
	}

	public String getDatabase()
	{
		return database;
	}

	public String getDriver()
	{
		return driver;
	}

	public String getUrl()
	{
		return url;
	}

	public String getUser()
	{
		return user;
	}

	public String getPassword()
	{
		return password;
	}

}

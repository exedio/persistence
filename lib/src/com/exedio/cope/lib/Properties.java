
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

	private static final String FILE_NAME = "cope.properties";
	
	private final String database;
	private final String driver;
	private final String url;
	private final String user;
	private final String password;

	private Properties()
	{
		final java.util.Properties properties = new java.util.Properties();
		File file = null;
		FileInputStream stream = null;
		try
		{
			file = new File(FILE_NAME);
			stream = new FileInputStream(file);
			properties.load(stream);
		}
		catch(IOException e)
		{
			throw new SystemException(e, file.getAbsolutePath().toString());
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
	
	private static String getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(result==null)
		{
			final String m = "ERROR: property "+key+" in "+FILE_NAME+" not set.";
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

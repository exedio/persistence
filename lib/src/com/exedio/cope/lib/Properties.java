
package com.exedio.cope.lib;

import java.io.FileInputStream;
import java.io.IOException;

class Properties
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
			stream = new FileInputStream(FILE_NAME);
			properties.load(stream);
		}
		catch(IOException e)
		{
			throw new SystemException(e);
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
		driver = properties.getProperty("database.driver");
		url = properties.getProperty("database.url");
		user = properties.getProperty("database.user");
		password = properties.getProperty("database.password");
	}
	
	private static void getPropertyNotNull(final java.util.Properties properties, final String key)
	{
		final String result = properties.getProperty(key);
		if(key==null)
			throw new RuntimeException("property "+key+" in "+FILE_NAME+" not set.");
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

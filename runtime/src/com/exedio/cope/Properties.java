/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class Properties extends com.exedio.cope.util.Properties
{
	
	private final StringField databaseCode = new StringField("database");
	private final StringField databaseUrl =  new StringField("database.url");
	private final StringField databaseUser =  new StringField("database.user");
	private final StringField databasePassword =  new StringField("database.password", true);
	private final BooleanField databaseLog = new BooleanField("database.log", false);
	private final BooleanField databaseLogStatementInfo = new BooleanField("database.logStatementInfo", false);
	
	private final BooleanField databaseDontSupportPreparedStatements = new BooleanField("database.dontSupport.preparedStatements", false);
	private final BooleanField databaseDontSupportEmptyStrings = new BooleanField("database.dontSupport.emptyStrings", false);
	private final BooleanField databaseDontSupportNativeDate = new BooleanField("database.dontSupport.nativeDate", false);
	private final BooleanField databaseDontSupportLimit = new BooleanField("database.dontSupport.limit", false);
	
	private final MapField databaseForcedNames = new MapField("database.forcename");
	private final MapField databaseTableOptions = new MapField("database.tableOption");
	private final MapField databaseCustomProperties;
	
	static final String PKSOURCE_BUTTERFLY = "pksource.butterfly";
	private final BooleanField pksourceButterfly = new BooleanField(PKSOURCE_BUTTERFLY, false);
	private final BooleanField fulltextIndex = new BooleanField("fulltextIndex", false);

	private final IntField connectionPoolIdleInitial = new IntField("connectionPool.idleInitial", 0, 0);
	private final IntField connectionPoolIdleLimit = new IntField("connectionPool.idleLimit", 10, 0);
	
	private final BooleanField transactionLog = new BooleanField("transaction.log", false);

	private final IntField cacheLimit = new IntField("cache.limit", 10000, 0);
	private final IntField cacheQueryLimit = new IntField("cache.queryLimit", 10000, 0);
	public static final String CACHE_QUERY_HISTOGRAM = "cache.queryHistogram";
	private final BooleanField cacheQueryHistogram = new BooleanField(CACHE_QUERY_HISTOGRAM, false);

	final IntField dataFieldBufferSizeDefault = new IntField("dataField.bufferSizeDefault", 20*1024, 1);
	final IntField dataFieldBufferSizeLimit = new IntField("dataField.bufferSizeLimit", 1024*1024, 1);
	
	private final FileField datadirPath = new FileField("datadir.path");
	private final StringField mediaRooturl =  new StringField("media.rooturl", "media/");
	private final IntField mediaOffsetExpires = new IntField("media.offsetExpires", 1000 * 5, 0);
	
	private final Constructor<? extends Database> database;

	public Properties()
	{
		this(getDefaultPropertyFile());
	}
	
	public static final File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	public Properties(final File file)
	{
		this(loadProperties(file), file.getAbsolutePath());
	}

	public Properties(final java.util.Properties properties, final String source)
	{
		super(properties, source);

		final String databaseCode = this.databaseCode.getStringValue();
		database = getDatabaseConstructor( databaseCode, source );

		databaseCustomProperties = new MapField("database." + databaseCode);
		
		if(connectionPoolIdleInitial.getIntValue()>connectionPoolIdleLimit.getIntValue())
			throw new RuntimeException("value for " + connectionPoolIdleInitial.getKey() + " must not be greater than " + connectionPoolIdleLimit.getKey());
		
		if(datadirPath.getFileValue()!=null)
		{
			final File value = datadirPath.getFileValue();

			if(!value.exists())
				throw new RuntimeException(datadirPath.getKey() + ' ' + value.getAbsolutePath() + " does not exist.");
			if(!value.isDirectory())
				throw new RuntimeException(datadirPath.getKey() + ' ' + value.getAbsolutePath() + " is not a directory.");
			if(!value.canRead())
				throw new RuntimeException(datadirPath.getKey() + ' ' + value.getAbsolutePath() + " is not readable.");
			if(!value.canWrite())
				throw new RuntimeException(datadirPath.getKey() + ' ' + value.getAbsolutePath() + " is not writable.");
		}
		
		ensureValidity(new String[]{"x-build"});
	}
	
	private static final Constructor<? extends Database> getDatabaseConstructor(final String databaseCode, final String source)
	{
		if(databaseCode.length()<=2)
			throw new RuntimeException("database from " + source + " must have at least two characters, but was " + databaseCode);
		
		final String databaseName =
			"com.exedio.cope." +
			Character.toUpperCase(databaseCode.charAt(0)) +
			databaseCode.substring(1) +
			"Database";

		final Class<?> databaseClassRaw;
		try
		{
			databaseClassRaw = Class.forName(databaseName);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" not found.");
		}

		if(!Database.class.isAssignableFrom(databaseClassRaw))
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" not a subclass of "+Database.class.getName()+".");
		}
		final Class<? extends Database> databaseClass = databaseClassRaw.asSubclass(Database.class);
		try
		{
			return databaseClass.getDeclaredConstructor(new Class[]{DialectParameters.class});
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("class "+databaseName+" from "+source+" does not have the required constructor.");
		}
	}
	
	private final RuntimeException newNotSetException(final String key)
	{
		return new RuntimeException("property " + key + " in " + getSource() + " not set.");
	}
	
	Database createDatabase(final DialectParameters parameters)
	{
		try
		{
			return database.newInstance(parameters);
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
		return databaseUrl.getStringValue();
	}

	public String getDatabaseUser()
	{
		return databaseUser.getStringValue();
	}

	public String getDatabasePassword()
	{
		return databasePassword.getStringValue();
	}
	
	public boolean getDatabaseLog()
	{
		return databaseLog.getBooleanValue();
	}
	
	public boolean getDatabaseLogStatementInfo()
	{
		return databaseLogStatementInfo.getBooleanValue();
	}
	
	public boolean getDatabaseDontSupportPreparedStatements()
	{
		return databaseDontSupportPreparedStatements.getBooleanValue();
	}
	
	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return databaseDontSupportEmptyStrings.getBooleanValue();
	}
	
	public boolean getDatabaseDontSupportLimit()
	{
		return databaseDontSupportLimit.getBooleanValue();
	}
	
	public boolean getDatabaseDontSupportNativeDate()
	{
		return databaseDontSupportNativeDate.getBooleanValue();
	}
	
	java.util.Properties getDatabaseForcedNames()
	{
		return databaseForcedNames.getMapValue();
	}
	
	java.util.Properties getDatabaseTableOptions()
	{
		return databaseTableOptions.getMapValue();
	}
	
	String getDatabaseCustomProperty(final String key)
	{
		return databaseCustomProperties.getValue(key);
	}
	
	public boolean getPkSourceButterfly()
	{
		return pksourceButterfly.getBooleanValue();
	}
	
	public boolean getFulltextIndex()
	{
		return fulltextIndex.getBooleanValue();
	}
	
	public int getConnectionPoolIdleInitial()
	{
		return connectionPoolIdleInitial.getIntValue();
	}
	
	public int getConnectionPoolIdleLimit()
	{
		return connectionPoolIdleLimit.getIntValue();
	}
	
	public boolean getTransactionLog()
	{
		return transactionLog.getBooleanValue();
	}
	
	public int getCacheLimit()
	{
		return cacheLimit.getIntValue();
	}
	
	public int getCacheQueryLimit()
	{
		return cacheQueryLimit.getIntValue();
	}
	
	public boolean getCacheQueryHistogram()
	{
		return cacheQueryHistogram.getBooleanValue();
	}
	
	public boolean hasDatadirPath()
	{
		return datadirPath.getFileValue()!=null;
	}
	
	public File getDatadirPath()
	{
		final File result = datadirPath.getFileValue();
		
		if(result==null)
			throw newNotSetException(datadirPath.getKey());

		return result;
	}
	
	public String getMediaRootUrl()
	{
		return mediaRooturl.getStringValue();
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
		return mediaOffsetExpires.getIntValue();
	}
}

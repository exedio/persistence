/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.exedio.dsmf.SQLRuntimeException;

public final class ConnectProperties extends com.exedio.cope.util.Properties
{
	private static final String DIALECT_FROM_URL = "from url";
	private final StringField dialectCode = new StringField("dialect", DIALECT_FROM_URL);
	private final StringField databaseUrl =  new StringField("database.url");
	private final StringField databaseUser =  new StringField("database.user");
	private final StringField databasePassword =  new StringField("database.password", true);
	private final BooleanField databaseLog = new BooleanField("database.log", false);
	private final IntField databaseLogThreshold = new IntField("database.log.threshold", 0, 0);
	private final BooleanField databaseLogQueryInfo = new BooleanField("database.logStatementInfo", false); // TODO rename property
	
	private final BooleanField databaseDontSupportPreparedStatements = new BooleanField("database.dontSupport.preparedStatements", false);
	private final BooleanField databaseDontSupportEmptyStrings = new BooleanField("database.dontSupport.emptyStrings", false);
	private final BooleanField databaseDontSupportNativeDate = new BooleanField("database.dontSupport.nativeDate", false);
	private final BooleanField databaseDontSupportLimit = new BooleanField("database.dontSupport.limit", false);
	
	private final BooleanField mysqlLowerCaseTableNames = new BooleanField("mysql.lower_case_table_names", false);
	
	private final MapField databaseTableOptions = new MapField("database.tableOption");
	private final MapField databaseCustomProperties;
	
	private final BooleanField fulltextIndex = new BooleanField("fulltextIndex", false);

	private final IntField connectionPoolIdleInitial = new IntField("connectionPool.idleInitial", 0, 0);
	private final IntField connectionPoolIdleLimit = new IntField("connectionPool.idleLimit", 10, 0);
	
	private final BooleanField transactionLog = new BooleanField("transaction.log", false);

	private final IntField itemCacheLimit = new IntField("cache.limit", 10000, 0);
	private final IntField queryCacheLimit = new IntField("cache.queryLimit", 10000, 0);

	final IntField dataFieldBufferSizeDefault = new IntField("dataField.bufferSizeDefault", 20*1024, 1);
	final IntField dataFieldBufferSizeLimit = new IntField("dataField.bufferSizeLimit", 1024*1024, 1);
	
	final StringField mediaRooturl =  new StringField("media.rooturl", "media/");
	private final IntField mediaOffsetExpires = new IntField("media.offsetExpires", 1000 * 5, 0);
	
	private final Constructor<? extends Dialect> dialect;

	public ConnectProperties()
	{
		this(getDefaultPropertyFile(), null);
	}
	
	public ConnectProperties(final Source context)
	{
		this(getDefaultPropertyFile(), context);
	}
	
	public static final File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	public ConnectProperties(final File file)
	{
		this(file, null);
	}

	public ConnectProperties(final File file, final Source context)
	{
		this(loadProperties(file), file.getAbsolutePath(), context);
	}

	public ConnectProperties(final java.util.Properties properties, final String sourceDescription, final Source context)
	{
		this(getSource(properties, sourceDescription), context);
	}
	
	public ConnectProperties(final Source source, final Source context)
	{
		super(source, context);

		final String dialectCodeRaw = this.dialectCode.getStringValue();
		
		final String dialectCode;
		if(DIALECT_FROM_URL.equals(dialectCodeRaw))
		{
			final String url = databaseUrl.getStringValue();
			final String prefix = "jdbc:";
			if(!url.startsWith(prefix))
				throw new RuntimeException("cannot parse " + databaseUrl.getKey() + '=' + url + ", missing prefix '" + prefix + '\'');
			final int pos = url.indexOf(':', prefix.length());
			if(pos<0)
				throw new RuntimeException("cannot parse " + databaseUrl.getKey() + '=' + url + ", missing second colon");
			dialectCode = url.substring(prefix.length(), pos);
		}
		else
			dialectCode = dialectCodeRaw;
			
		dialect = getDialectConstructor(dialectCode, source.getDescription());

		databaseCustomProperties = new MapField("database." + dialectCode);
		
		if(connectionPoolIdleInitial.getIntValue()>connectionPoolIdleLimit.getIntValue())
			throw new RuntimeException("value for " + connectionPoolIdleInitial.getKey() + " must not be greater than " + connectionPoolIdleLimit.getKey());
		
		ensureValidity("x-build");
	}
	
	private static final Constructor<? extends Dialect> getDialectConstructor(final String dialectCode, final String sourceDescription)
	{
		if(dialectCode.length()<=2)
			throw new RuntimeException("dialect from " + sourceDescription + " must have at least two characters, but was " + dialectCode);
		
		final String dialectName =
			"com.exedio.cope." +
			Character.toUpperCase(dialectCode.charAt(0)) +
			dialectCode.substring(1) +
			"Dialect";

		final Class<?> dialectClassRaw;
		try
		{
			dialectClassRaw = Class.forName(dialectName);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("class " + dialectName + " from " + sourceDescription + " not found.");
		}

		if(!Dialect.class.isAssignableFrom(dialectClassRaw))
		{
			throw new RuntimeException(dialectClassRaw.toString() + " from " + sourceDescription + " not a subclass of " + Dialect.class.getName() + '.');
		}
		final Class<? extends Dialect> dialectClass = dialectClassRaw.asSubclass(Dialect.class);
		try
		{
			return dialectClass.getDeclaredConstructor(new Class[]{DialectParameters.class});
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("class " + dialectName + " from " + sourceDescription + " does not have the required constructor.");
		}
	}
	
	Database createDatabase(final boolean revisionEnabled)
	{
		final DialectParameters parameters;
		Connection probeConnection = null;
		try
		{
			probeConnection = DriverManager.getConnection(getDatabaseUrl(), getDatabaseUser(), getDatabasePassword());
			parameters = new DialectParameters(this, probeConnection);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "create");
		}
		finally
		{
			if(probeConnection!=null)
			{
				try
				{
					probeConnection.close();
					probeConnection = null;
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
		}

		final Dialect dialect;
		try
		{
			dialect = this.dialect.newInstance(parameters);
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
		
		return new Database(dialect.driver, parameters, dialect, revisionEnabled);
	}
	
	public String getDialect()
	{
		return dialect.getDeclaringClass().getName();
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
	
	public int getDatabaseLogThreshold()
	{
		return databaseLogThreshold.getIntValue();
	}

	public boolean getDatabaseLogQueryInfo()
	{
		return databaseLogQueryInfo.getBooleanValue();
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
	
	public boolean getMysqlLowerCaseTableNames()
	{
		return mysqlLowerCaseTableNames.getBooleanValue();
	}
	
	java.util.Properties getDatabaseTableOptions()
	{
		return databaseTableOptions.getMapValue();
	}
	
	String getDatabaseCustomProperty(final String key)
	{
		return databaseCustomProperties.getValue(key);
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
	
	public int getItemCacheLimit()
	{
		return itemCacheLimit.getIntValue();
	}
	
	public int getQueryCacheLimit()
	{
		return queryCacheLimit.getIntValue();
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
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Has been renamed to {@link #getDialect()}.
	 */
	@Deprecated
	public String getDatabase()
	{
		return getDialect();
	}
	
	/**
	 * @deprecated Use {@link #getDatabaseLogQueryInfo()} instead
	 */
	@Deprecated
	public boolean getDatabaseLogStatementInfo()
	{
		return getDatabaseLogQueryInfo();
	}
	
	/**
	 * @deprecated renamed to {@link #getItemCacheLimit()}.
	 */
	@Deprecated
	public int getCacheLimit()
	{
		return getItemCacheLimit();
	}
	
	/**
	 * @deprecated renamed to {@link #getQueryCacheLimit()}.
	 */
	@Deprecated
	public int getCacheQueryLimit()
	{
		return getQueryCacheLimit();
	}
}

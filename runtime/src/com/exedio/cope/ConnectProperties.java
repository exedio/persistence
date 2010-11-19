/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.util.Locale;

public final class ConnectProperties extends com.exedio.cope.util.Properties
{
	private final BooleanField log = new BooleanField("log", true);

	private static final String DIALECT_FROM_URL = "from url";
	private final StringField dialectCode = new StringField("dialect", DIALECT_FROM_URL);
	private final StringField databaseUrl =  new StringField("database.url");
	private final StringField databaseUser =  new StringField("database.user");
	private final StringField databasePassword =  new StringField("database.password", true);

	private final BooleanField databaseDontSupportPreparedStatements = new BooleanField("database.dontSupport.preparedStatements", false);
	private final BooleanField databaseDontSupportEmptyStrings = new BooleanField("database.dontSupport.emptyStrings", false);
	private final BooleanField databaseDontSupportNativeDate = new BooleanField("database.dontSupport.nativeDate", false);

	private final BooleanField mysqlLowerCaseTableNames = new BooleanField("mysql.lower_case_table_names", false);
	final BooleanField longSyntheticNames = new BooleanField("database.longSyntheticNames", false);

	/**
	 * The table name for the revision information.
	 * The value "while" prevents name collisions
	 * with other tables,
	 * since "while" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final StringField revisionTableName = new StringField("schema.revision.table", "while");

	/**
	 * The name of the unique constraint
	 * on the table for the revision information.
	 * The value "protected" prevents name collisions
	 * with other tables,
	 * since "protected" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final StringField revisionUniqueName = new StringField("schema.revision.unique", "protected");

	private final MapField databaseTableOptions = new MapField("database.tableOption");

	private final BooleanField fulltextIndex = new BooleanField("fulltextIndex", false);

	private final IntField connectionPoolIdleInitial = new IntField("connectionPool.idleInitial", 0, 0);
	private final IntField connectionPoolIdleLimit = new IntField("connectionPool.idleLimit", 50, 0);

	private final IntField querySearchSizeLimit = new IntField("query.searchSizeLimit", 100000, 1);

	private final IntField itemCacheLimit  = new IntField("cache.item.limit", 100000, 0);
	private final IntField queryCacheLimit = new IntField("cache.query.limit", 10000, 0);
	final BooleanField itemCacheConcurrentModificationDetection = new BooleanField("cache.item.concurrentModificationDetection", true);

	final IntField dataFieldBufferSizeDefault = new IntField("dataField.bufferSizeDefault", 20*1024, 1);
	final IntField dataFieldBufferSizeLimit = new IntField("dataField.bufferSizeLimit", 1024*1024, 1);

	final BooleanField cluster = new BooleanField("cluster", false);

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
		this(getSource(file), context);
	}

	public ConnectProperties(final java.util.Properties properties, final String sourceDescription, final Source context)
	{
		this(getSource(properties, sourceDescription), context);
	}

	public ConnectProperties(final Source source, final Source context)
	{
		super(source, context);

		final String dialectCodeRaw = this.dialectCode.stringValue();

		final String dialectCode;
		if(DIALECT_FROM_URL.equals(dialectCodeRaw))
		{
			final String url = databaseUrl.stringValue();
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

		if(connectionPoolIdleInitial.intValue()>connectionPoolIdleLimit.intValue())
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
		catch(final ClassNotFoundException e)
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
		catch(final NoSuchMethodException e)
		{
			throw new RuntimeException("class " + dialectName + " from " + sourceDescription + " does not have the required constructor.");
		}
	}

	public boolean isLoggingEnabled()
	{
		return log.booleanValue();
	}

	Dialect createDialect(final DialectParameters parameters)
	{
		try
		{
			return this.dialect.newInstance(parameters);
		}
		catch(final InstantiationException e)
		{
			throw new RuntimeException(dialect.toGenericString(), e);
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(dialect.toGenericString(), e);
		}
		catch(final InvocationTargetException e)
		{
			throw new RuntimeException(dialect.toGenericString(), e);
		}
	}

	public String getDialect()
	{
		return dialect.getDeclaringClass().getName();
	}

	public String getDatabaseUrl()
	{
		return databaseUrl.stringValue();
	}

	public String getDatabaseUser()
	{
		return databaseUser.stringValue();
	}

	public String getDatabasePassword()
	{
		return databasePassword.stringValue();
	}

	public boolean getDatabaseDontSupportPreparedStatements()
	{
		return databaseDontSupportPreparedStatements.booleanValue();
	}

	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return databaseDontSupportEmptyStrings.booleanValue();
	}

	/**
	 * @deprecated Always returns false.
	 */
	@Deprecated
	public boolean getDatabaseDontSupportLimit()
	{
		return false;
	}

	public boolean getDatabaseDontSupportNativeDate()
	{
		return databaseDontSupportNativeDate.booleanValue();
	}

	String filterTableName(final String tableName)
	{
		return
			mysqlLowerCaseTableNames.booleanValue()
			? tableName.toLowerCase(Locale.ENGLISH)
			: tableName;
	}

	String getTableOption(final Table table)
	{
		return databaseTableOptions.getValue(table.id);
	}

	public boolean getFulltextIndex()
	{
		return fulltextIndex.booleanValue();
	}

	public int getConnectionPoolIdleInitial()
	{
		return connectionPoolIdleInitial.intValue();
	}

	public int getConnectionPoolIdleLimit()
	{
		return connectionPoolIdleLimit.intValue();
	}

	/**
	 * @see Query#getSearchSizeLimit()
	 * @see Query#setSearchSizeLimit(int)
	 */
	public int getQuerySearchSizeLimit()
	{
		return querySearchSizeLimit.intValue();
	}

	public int getItemCacheLimit()
	{
		return itemCacheLimit.intValue();
	}

	public int getQueryCacheLimit()
	{
		return queryCacheLimit.intValue();
	}

	public String getMediaRootUrl()
	{
		return mediaRooturl.stringValue();
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
		return mediaOffsetExpires.intValue();
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

	/**
	 * @deprecated
	 * Not supported anymore.
	 * This method always returns false.
	 */
	@Deprecated
	public boolean getTransactionLog()
	{
		return false;
	}

	/**
	 * @deprecated
	 * Not supported anymore.
	 * This method always returns true.
	 */
	@Deprecated
	public boolean getOracleVarchar()
	{
		return true;
	}
}

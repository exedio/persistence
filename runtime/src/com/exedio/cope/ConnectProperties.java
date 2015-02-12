/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.lang.Thread.MAX_PRIORITY;
import static java.lang.Thread.MIN_PRIORITY;

import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;

@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public final class ConnectProperties extends com.exedio.cope.util.Properties
{
	private static final String DIALECT_FROM_URL = "from url";
	private final String dialectCode = value("dialect", DIALECT_FROM_URL);

	private final String connectionUrl      = value      ("connection.url",      (String)null);
	private final String connectionUsername = value      ("connection.username", (String)null);
	private final String connectionPassword = valueHidden("connection.password", (String)null);
	final String connectionPostgresqlSearchPath = value  ("connection.postgresql.search_path", "\"$user\"");


	private final boolean disableEmptyStrings       = value("disableSupport.emptyStrings", false);
	private final boolean disablePreparedStatements = value("disableSupport.preparedStatements", false);
	private final boolean disableNativeDate         = value("disableSupport.nativeDate", false);
	private final boolean disableNotNull            = value("disableSupport.notNull", false);
	private final boolean disableUniqueViolation    = value("disableSupport.uniqueViolation", false);
	private final boolean disableSemicolon          = value("disableSupport.semicolon", true); // TODO

	public boolean isSupportDisabledForEmptyStrings()
	{
		return disableEmptyStrings;
	}

	public boolean isSupportDisabledForPreparedStatements()
	{
		return disablePreparedStatements;
	}

	public boolean isSupportDisabledForNativeDate()
	{
		return disableNativeDate;
	}

	public boolean isSupportDisabledForNotNull()
	{
		return disableNotNull;
	}

	boolean isSupportDisabledForUniqueViolation()
	{
		return disableUniqueViolation;
	}

	public boolean isSupportDisabledForSemicolon()
	{
		return disableSemicolon;
	}


	private final boolean fulltextIndex = value("fulltextIndex", false);

	public boolean getFulltextIndex()
	{
		return fulltextIndex;
	}


	/**
	 * By default, {@link Model#deleteSchemaForTest()} does some optimizations
	 * for executing faster that {@link Model#deleteSchema()}.
	 * This feature is experimental.
	 * If it causes any problems, you may disable this property.
	 * Then {@link Model#deleteSchemaForTest() deleteSchemaForTest}
	 * behaves exactly as {@link Model#deleteSchema() deleteSchema}.
	 */
	final boolean deleteSchemaForTest = value("deleteSchemaForTest", true);

	// schema

	final PrimaryKeyGenerator primaryKeyGenerator = valEn("schema.primaryKeyGenerator", PrimaryKeyGenerator.memory);
	final boolean longSyntheticNames = value("schema.tableInNames", false);

	/**
	 * The table name for the revision information.
	 * The value "while" prevents name collisions
	 * with other tables,
	 * since "while" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final String revisionTableName = value("schema.revision.table", "while");

	/**
	 * The name of the primary key constraint
	 * on the table for the revision information.
	 * The value "protected" prevents name collisions
	 * with other tables,
	 * since "protected" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final String revisionPrimaryKeyName = value("schema.revision.unique", "protected"); // TODO rename key

	private final boolean mysqlLowerCaseTableNames = value("schema.mysql.lower_case_table_names", false);
	final MysqlRowFormat  mysqlRowFormat           = valEn("schema.mysql.rowFormat", MysqlRowFormat.NONE);
	final boolean         mysqlAvoidTruncate       = value("schema.mysql.avoidTruncate", false);

	/**
	 * If true, {@link Model#reviseIfSupportedAndAutoEnabled} will trigger execution
	 * of revisions if necessary;
	 * if false, it will throw an exception if revisions are pending.
	 * Default is true.
	 */
	final boolean autoReviseEnabled = value("revise.auto.enabled", true);
	static final String reviseSavepointKey = "revise.savepoint";
	final boolean reviseSavepoint = value(reviseSavepointKey, false);


	private final int connectionPoolIdleInitial = value("connectionPool.idleInitial", 0, 0);
	private final int connectionPoolIdleLimit   = value("connectionPool.idleLimit",  50, 0);

	public int getConnectionPoolIdleInitial()
	{
		return connectionPoolIdleInitial;
	}

	public int getConnectionPoolIdleLimit()
	{
		return connectionPoolIdleLimit;
	}


	private final int querySearchSizeLimit = value("query.searchSizeLimit", 100000, 1);

	private final int itemCacheLimit      = value("cache.item.limit",     100000, 0);
	private final int queryCacheLimit     = value("cache.query.limit",     10000, 0);
	private final int queryCacheSizeLimit = value("cache.query.sizeLimit", 10000, 0);
	        final boolean itemCacheStamps = value("cache.stamps", true);

	/**
	 * @see Query#getSearchSizeLimit()
	 * @see Query#setSearchSizeLimit(int)
	 */
	public int getQuerySearchSizeLimit()
	{
		return querySearchSizeLimit;
	}

	public int getItemCacheLimit()
	{
		return itemCacheLimit;
	}

	public int getQueryCacheLimit()
	{
		return queryCacheLimit;
	}

	public int getQueryCacheSizeLimit()
	{
		return queryCacheSizeLimit;
	}


	final int dataFieldBufferSizeDefault = value("dataField.bufferSizeDefault", 20*1024, 1);
	final int dataFieldBufferSizeLimit   = value("dataField.bufferSizeLimit", 1024*1024, 1);

	final     int changeListenersQueueCapacity = value("changeListeners.queueCapacity", 1000, 1);
	final     int changeListenersThreads       = value("changeListeners.threads",        1, 1);
	final     int changeListenersThreadsMax    = value("changeListeners.threadsMax",    10, 1);
	final boolean changeListenersPrioritySet   = value("changeListeners.prioritySet",   false);
	final     int changeListenersPriority      = value("changeListeners.priority",      MAX_PRIORITY, MIN_PRIORITY);

	final ClusterProperties clusterPropertiesWithoutContext = noContext() ? value("cluster", false, ClusterProperties.factory()) : null;


	private final String mediaRooturl    = value("media.rooturl", "media/");
	private final int mediaOffsetExpires = value("media.offsetExpires", 1000 * 5, 0);
	private final String mediaUrlSecret = noContext()
			? checkMediaUrlSecret       (valueHidden(     "media.url.secret", ""))
			: checkMediaUrlSecretContext(getContext().get("media.url.secret"));

	private static final String checkMediaUrlSecret(final String s)
	{
		final int length = s.length();
		if(length==0)
			return null;
		if(length<10)
			throw new IllegalArgumentException("media.url.secret must be at least 10 characters, but just has " + length);
		return s;
	}

	private static final String checkMediaUrlSecretContext(final String s)
	{
		return ( (s==null) || (s.length()<10) ) ? null : s;
	}

	public String getMediaRootUrl()
	{
		return mediaRooturl;
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
		return mediaOffsetExpires;
	}

	public String getMediaUrlSecret()
	{
		return mediaUrlSecret;
	}


	public static Factory<ConnectProperties> factory()
	{
		return new Factory<ConnectProperties>()
		{
			@Override
			public ConnectProperties create(final Source source)
			{
				return new ConnectProperties(source, null);
			}
		};
	}

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
		this(Sources.load(file), context);
	}

	public ConnectProperties(final java.util.Properties properties, final String sourceDescription, final Source context)
	{
		this(Sources.view(properties, sourceDescription), context);
	}

	@SuppressWarnings("deprecation")
	public ConnectProperties(final Source source, final Source context)
	{
		super(source, context);

		final String dialectCodeRaw = this.dialectCode;

		final String dialectCode;
		if(DIALECT_FROM_URL.equals(dialectCodeRaw))
		{
			final String url = connectionUrl;
			final String prefix = "jdbc:";
			if(!url.startsWith(prefix))
				throw new IllegalArgumentException("cannot parse connection.url=" + url + ", missing prefix '" + prefix + '\'');
			final int pos = url.indexOf(':', prefix.length());
			if(pos<0)
				throw new IllegalArgumentException("cannot parse connection.url=" + url + ", missing second colon");
			dialectCode = url.substring(prefix.length(), pos);
		}
		else
			dialectCode = dialectCodeRaw;

		dialect = getDialectConstructor(dialectCode, source.getDescription());

		{
			final int position = connectionPostgresqlSearchPath.indexOf(',');
			if(position>=0)
				throw new IllegalArgumentException(
					"value for connection.postgresql.search_path '" + connectionPostgresqlSearchPath + "'" +
					" contains forbidden comma on position " + position + '.');
		}

		if(connectionPoolIdleInitial>connectionPoolIdleLimit)
			throw new IllegalArgumentException("value for connectionPool.idleInitial must not be greater than connectionPool.idleLimit");
	}

	private static final Constructor<? extends Dialect> getDialectConstructor(final String dialectCode, final String sourceDescription)
	{
		if(dialectCode.length()<=2)
			throw new IllegalArgumentException("dialect from " + sourceDescription + " must have at least two characters, but was " + dialectCode);

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
			throw new IllegalArgumentException("class " + dialectName + " from " + sourceDescription + " not found.", e);
		}

		if(!Dialect.class.isAssignableFrom(dialectClassRaw))
		{
			throw new IllegalArgumentException(dialectClassRaw.toString() + " from " + sourceDescription + " not a subclass of " + Dialect.class.getName() + '.');
		}
		final Class<? extends Dialect> dialectClass = dialectClassRaw.asSubclass(Dialect.class);
		try
		{
			return dialectClass.getDeclaredConstructor(DialectParameters.class);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException("class " + dialectName + " from " + sourceDescription + " does not have the required constructor.", e);
		}
	}

	Dialect createDialect(final DialectParameters parameters)
	{
		try
		{
			return this.dialect.newInstance(parameters);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(dialect.toGenericString(), e);
		}
	}

	public String getDialect()
	{
		return dialect.getDeclaringClass().getName();
	}

	public String getConnectionUrl()
	{
		return connectionUrl;
	}

	public String getConnectionUsername()
	{
		return connectionUsername;
	}

	public String getConnectionPassword()
	{
		return connectionPassword;
	}

	java.util.Properties newConnectionInfo()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("user",     connectionUsername);
		result.setProperty("password", connectionPassword);
		return result;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		e.put("connection.url",  connectionUrl);
		e.put("connection.user", connectionUsername);
	}

	String filterTableName(final String tableName)
	{
		return
			mysqlLowerCaseTableNames
			? tableName.toLowerCase(Locale.ENGLISH)
			: tableName;
	}

	boolean noContext()
	{
		try
		{
			getContext();
			return false;
		}
		catch(final IllegalStateException e)
		{
			return true;
		}
	}

	// TODO move into framework
	private <E extends Enum<E>> E valEn(
			final String key,
			final E defaultValue)
	{
		return Enum.valueOf(defaultValue.getDeclaringClass(), value(key, defaultValue.name()));
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getConnectionUsername()} instead
	 */
	@Deprecated
	public String getConnectionUser()
	{
		return getConnectionUsername();
	}

	/**
	 * @deprecated Replaced by org.apache.log4j. Always returns true.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public boolean isLoggingEnabled()
	{
		return true;
	}

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
	@SuppressWarnings("static-method")
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
	@SuppressWarnings("static-method")
	public boolean getOracleVarchar()
	{
		return true;
	}

	/**
	 * @deprecated
	 * Not supported anymore.
	 * This method always returns false.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public boolean getDatabaseDontSupportLimit()
	{
		return false;
	}

	/**
	 * @deprecated Use {@link #getConnectionUrl()} instead
	 */
	@Deprecated
	public String getDatabaseUrl()
	{
		return getConnectionUrl();
	}

	/**
	 * @deprecated Use {@link #getConnectionUsername()} instead
	 */
	@Deprecated
	public String getDatabaseUser()
	{
		return getConnectionUsername();
	}

	/**
	 * @deprecated Use {@link #getConnectionPassword()} instead
	 */
	@Deprecated
	public String getDatabasePassword()
	{
		return getConnectionPassword();
	}

	/**
	 * @deprecated Use {@link #isSupportDisabledForPreparedStatements()} instead
	 */
	@Deprecated
	public boolean getDatabaseDontSupportPreparedStatements()
	{
		return isSupportDisabledForPreparedStatements();
	}

	/**
	 * @deprecated Use {@link #isSupportDisabledForEmptyStrings()} instead
	 */
	@Deprecated
	public boolean getDatabaseDontSupportEmptyStrings()
	{
		return isSupportDisabledForEmptyStrings();
	}

	/**
	 * @deprecated Use {@link #isSupportDisabledForNativeDate()} instead
	 */
	@Deprecated
	public boolean getDatabaseDontSupportNativeDate()
	{
		return isSupportDisabledForNativeDate();
	}
}

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

import com.exedio.cope.pattern.MediaFingerprintOffset;
import com.exedio.cope.util.PoolProperties;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Callable;

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

	private static final PrimaryKeyGenerator primaryKeyGeneratorDEFAULT = PrimaryKeyGenerator.memory;
	final PrimaryKeyGenerator primaryKeyGenerator;
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
	final boolean         mysqlUtf8mb4             = value("schema.mysql.utf8mb4", true);
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


	final PoolProperties connectionPool = value("connectionPool", PoolProperties.factory(50));

	public int getConnectionPoolIdleInitial()
	{
		return connectionPool.getIdleInitial();
	}

	public int getConnectionPoolIdleLimit()
	{
		return connectionPool.getIdleLimit();
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


	private static final String mediaRooturlDEFAULT = "media/";
	private final String mediaRooturl;
	private final int mediaOffsetExpires = value("media.offsetExpires", 1000 * 5, 0);
	private final int mediaFingerOffset  = value("media.fingerprintOffset", 0, 0);
	private final String mediaUrlSecret = noContext()
			? checkMediaUrlSecret       (valueHidden(     "media.url.secret", ""))
			: checkMediaUrlSecretContext(getContext().get("media.url.secret"));

	private final String checkMediaUrlSecret(final String s)
	{
		final int length = s.length();
		if(length==0)
			return null;
		if(length<10)
			throw newException(
					"media.url.secret",
					"must have at least 10 characters, " +
					"but was '" + s + "' with just " + length + " characters");
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
	 * @see #getMediaMaxAge()
	 */
	public int getMediaOffsetExpires()
	{
		return mediaOffsetExpires;
	}

	private final int mediaMaxAge = mediaOffsetExpires/1000;

	/**
	 * @see #getMediaOffsetExpires()
	 */
	public int getMediaMaxAge()
	{
		return mediaMaxAge;
	}

	private final MediaFingerprintOffset mediaFingerprintOffsetState = new MediaFingerprintOffset(mediaFingerOffset);

	public MediaFingerprintOffset mediaFingerprintOffset()
	{
		return mediaFingerprintOffsetState;
	}

	public String getMediaUrlSecret()
	{
		return mediaUrlSecret;
	}


	public static Factory factory()
	{
		return new Factory(
				primaryKeyGeneratorDEFAULT,
				mediaRooturlDEFAULT);
	}

	public static class Factory implements Properties.Factory<ConnectProperties>
	{
		private final PrimaryKeyGenerator primaryKeyGenerator;
		private final String mediaRootUrl;

		Factory(
				final PrimaryKeyGenerator primaryKeyGenerator,
				final String mediaRootUrl)
		{
			this.primaryKeyGenerator = primaryKeyGenerator;
			this.mediaRootUrl = mediaRootUrl;
		}

		public Factory primaryKeyGeneratorSequence()
		{
			return new Factory(PrimaryKeyGenerator.sequence, mediaRootUrl);
		}

		public Factory mediaRootUrl(final String mediaRootUrl)
		{
			return new Factory(primaryKeyGenerator, mediaRootUrl);
		}

		@Override
		public ConnectProperties create(final Source source)
		{
			return new ConnectProperties(source, null,
					primaryKeyGenerator,
					mediaRootUrl);
		}
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

	public ConnectProperties(final Source source, final Source context)
	{
		this(source, context,
				primaryKeyGeneratorDEFAULT,
				mediaRooturlDEFAULT);
	}

	@SuppressWarnings("deprecation")
	ConnectProperties(
			final Source source, final Source context,
			final PrimaryKeyGenerator primaryKeyGenerator,
			final String mediaRootUrl)
	{
		super(source, context);

		this.primaryKeyGenerator = valEn("schema.primaryKeyGenerator", primaryKeyGenerator);
		this.mediaRooturl = value("media.rooturl", mediaRootUrl);

		final String dialectCodeRaw = this.dialectCode;

		final String dialectCode;
		if(DIALECT_FROM_URL.equals(dialectCodeRaw))
		{
			final String url = connectionUrl;
			final String prefix = "jdbc:";
			if(!url.startsWith(prefix))
				throw newException("connection.url", "must start with '" + prefix + "', but was '" + url + '\'');
			final int pos = url.indexOf(':', prefix.length());
			if(pos<0)
				throw newException("connection.url", "must contain two colons, but was '" + url + '\'');
			dialectCode = url.substring(prefix.length(), pos);
		}
		else
			dialectCode = dialectCodeRaw;

		dialect = getDialectConstructor(dialectCode);

		{
			final int position = connectionPostgresqlSearchPath.indexOf(',');
			if(position>=0)
				throw newException(
					"connection.postgresql.search_path",
					"must not contain commas, " +
					"but did at position " + position + " and was '" + connectionPostgresqlSearchPath + '\'');
		}
	}

	private final Constructor<? extends Dialect> getDialectConstructor(final String dialectCode)
	{
		if(dialectCode.length()<=2)
			throw newException("dialect", "must have at least two characters, but was '" + dialectCode + '\'');

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
			throw newException("dialect", "must name a class, but was '" + dialectName + '\'', e);
		}

		if(!Dialect.class.isAssignableFrom(dialectClassRaw))
		{
			throw newException(
					"dialect",
					"must name a subclass of " + Dialect.class.getName() + ", " +
					"but was " + dialectClassRaw.getName());
		}
		final Class<? extends Dialect> dialectClass = dialectClassRaw.asSubclass(Dialect.class);
		try
		{
			return dialectClass.getDeclaredConstructor(Probe.class);
		}
		catch(final NoSuchMethodException e)
		{
			throw newException(
					"dialect",
					"must name a class with a constructor with parameter " + Probe.class.getName() + ", " +
					"but was " + dialectClassRaw.getName(), e);
		}
	}

	Dialect createDialect(final Probe probe)
	{
		try
		{
			return this.dialect.newInstance(probe);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(dialect.toGenericString(), e);
		}
	}

	Probe probe()
	{
		final Driver driver;

		try
		{
			driver = DriverManager.getDriver(connectionUrl);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, connectionUrl);
		}
		if(driver==null)
			throw new RuntimeException(connectionUrl);

		try(Connection connection = driver.connect(connectionUrl, newConnectionInfo()))
		{
			return new Probe(this, driver, connection);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, connectionUrl);
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

	public Callable<?> getProbeTest()
	{
		return new Callable<String>()
		{
			@Override
			public String call()
			{
				final EnvironmentInfo info = probe().environmentInfo;
				return
						info.getDatabaseProductName() + ' ' +
						info.getDatabaseVersionDescription() + ' ' +
						info.getDriverName() + ' ' +
						info.getDriverVersionDescription();
			}
			@Override
			public String toString()
			{
				return "probe";
			}
		};
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
	 * @deprecated Replaced by org.slf4j. Always returns true.
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

	/**
	 * @deprecated not supported anymore.
	 * This methods always returns {@code false}.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public boolean isSupportDisabledForNotNull()
	{
		return false;
	}
}

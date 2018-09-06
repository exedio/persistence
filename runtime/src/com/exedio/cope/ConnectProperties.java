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

import com.exedio.cope.pattern.MediaFingerprintOffset;
import com.exedio.cope.util.PoolProperties;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceFactory;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;

@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public final class ConnectProperties extends com.exedio.cope.util.Properties
{
	final ConnectionProperties connection = valnp("connection", ConnectionProperties::new);

	final ServiceFactory<Dialect,CopeProbe> dialect = valueService("dialect", fromUrl(connection.url),
			Dialect.class, CopeProbe.class);

	private final boolean disableEmptyStrings       = value("disableSupport.emptyStrings", false);
	private final boolean disablePreparedStatements = value("disableSupport.preparedStatements", false);
	private final boolean disableNativeDate;
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

	/**
	 * DO NOT USE the value of itemCacheLimitOLD, just for default of itemCacheLimit.
	 */
	private final int itemCacheLimitOLD   = value("cache.item.limit",     100000, 0);
	private final int itemCacheLimit      = value("cache.item.globalLimit", itemCacheLimitOLD, 0);
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
	final VaultProperties dataFieldVault = value("dataField.vault", false, VaultProperties.factory());

	public VaultProperties getVaultProperties()
	{
		return dataFieldVault;
	}

	@Nonnull
	public VaultProperties getVaultPropertiesStrict()
	{
		if(dataFieldVault==null)
			throw new IllegalArgumentException(
					"vaults are disabled (dataField.vault=false) in " + getSource());
		return dataFieldVault;
	}

	public String getVaultAlgorithm()
	{
		return dataFieldVault!=null ? dataFieldVault.getAlgorithm() : null;
	}

	final boolean comparableCheck = value("comparableCheck", true); // TODO remove, is just a panic button

	final     int changeListenersQueueCapacity = value("changeListeners.queueCapacity", 1000, 1);
	final ThreadSwarmProperties chaListThreads = valnp("changeListeners.threads", ThreadSwarmProperties::new);

	final ClusterProperties cluster = value("cluster", false, ClusterProperties.factory());


	private final String mediaRooturl;
	private final int mediaOffsetExpires = value("media.offsetExpires", 1000 * 5, 0);
	private final int mediaFingerOffset  = value("media.fingerprintOffset", 0, 0);
	private final String mediaUrlSecret = valueMediaUrlSecret("media.url.secret");

	private String valueMediaUrlSecret(final String key)
	{
		final String result = valueHidden(key, "");
		final int length = result.length();
		if(length==0)
			return null;
		if(length<10)
			throw newException(key,
					"must have at least 10 characters, " +
					"but was '" + result + "' with just " + length + " characters");
		return result;
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
				false, // disableNativeDate
				PrimaryKeyGenerator.memory,
				"media/");
	}

	public static final class Factory implements Properties.Factory<ConnectProperties>
	{
		private final boolean disableNativeDate;
		private final PrimaryKeyGenerator primaryKeyGenerator;
		private final String mediaRootUrl;

		Factory(
				final boolean disableNativeDate,
				final PrimaryKeyGenerator primaryKeyGenerator,
				final String mediaRootUrl)
		{
			this.disableNativeDate = disableNativeDate;
			this.primaryKeyGenerator = primaryKeyGenerator;
			this.mediaRootUrl = mediaRootUrl;
		}

		public Factory disableNativeDate()
		{
			return new Factory(true, primaryKeyGenerator, mediaRootUrl);
		}

		public Factory primaryKeyGeneratorSequence()
		{
			return new Factory(disableNativeDate, PrimaryKeyGenerator.sequence, mediaRootUrl);
		}

		public Factory mediaRootUrl(final String mediaRootUrl)
		{
			return new Factory(disableNativeDate, primaryKeyGenerator, mediaRootUrl);
		}

		@Override
		public ConnectProperties create(final Source source)
		{
			return new ConnectProperties(source, this);
		}
	}

	public static File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	@SuppressWarnings("deprecation") // needed for idea
	public ConnectProperties(final File file)
	{
		this(file, null);
	}

	@SuppressWarnings("deprecation") // needed for idea
	public static ConnectProperties create(final Source source)
	{
		return new ConnectProperties(source, (Source)null);
	}

	ConnectProperties(final Source source, final Factory factory)
	{
		super(source);

		this.disableNativeDate = value("disableSupport.nativeDate", factory.disableNativeDate);
		this.primaryKeyGenerator = value("schema.primaryKeyGenerator", factory.primaryKeyGenerator);
		this.mediaRooturl = value("media.rooturl", factory.mediaRootUrl);

		if(cluster!=null && !primaryKeyGenerator.persistent)
			throw newException("cluster",
					"not supported together with schema.primaryKeyGenerator=" + primaryKeyGenerator);
	}

	private static String fromUrl(final String url)
	{
		final String prefix = "jdbc:";
		if(!url.startsWith(prefix))
			return null;
		final int pos = url.indexOf(':', prefix.length());
		if(pos<0)
			return null;

		final String code = url.substring(prefix.length(), pos);
		if(code.length()<=2)
			return null;

		return
			"com.exedio.cope." +
			Character.toUpperCase(code.charAt(0)) +
			code.substring(1) +
			"Dialect";
	}

	public String getDialect()
	{
		return dialect.getServiceClass().getName();
	}

	public String getConnectionUrl()
	{
		return connection.url;
	}

	public String getConnectionUsername()
	{
		return connection.username;
	}

	public String getConnectionPassword()
	{
		return connection.password;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		connection.putRevisionEnvironment("connection", e);
	}

	String filterTableName(final String tableName)
	{
		return
			mysqlLowerCaseTableNames
			? tableName.toLowerCase(Locale.ENGLISH)
			: tableName;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated
	 * Calling this method within methods overriding {@link Properties#getTests()}
	 * is no longer needed, as class {@code ConnectProperties}
	 * provides {@link com.exedio.cope.util.Properties.Probe probes} anyway.
	 */
	@Deprecated
	public Callable<?> getProbeTest()
	{
		return new Callable<String>()
		{
			@Override
			public String call()
			{
				return probe();
			}
			@Override
			public String toString()
			{
				return "probe";
			}
		};
	}

	/**
	 * @deprecated
	 * Calling this method within {@link com.exedio.cope.util.Properties.Probe probes}
	 * is no longer needed, as class {@code ConnectProperties}
	 * provides {@link com.exedio.cope.util.Properties.Probe probes} anyway.
	 */
	@Deprecated
	public String probe()
	{
		return
				connection.probe() +
				(dataFieldVault!=null ? ' ' + dataFieldVault.probe() : "");
	}

	/**
	 * @deprecated Use {@link #ConnectProperties(File)} instead as {@code context} is no longer supported.
	 */
	@Deprecated
	public ConnectProperties(final File file, final Source context)
	{
		this(Sources.load(file), context);
	}

	/**
	 * @deprecated Use {@link #create(Properties.Source)} instead as {@code context} is no longer supported.
	 */
	@Deprecated
	public ConnectProperties(final java.util.Properties properties, final String sourceDescription, final Source context)
	{
		this(Sources.view(properties, sourceDescription), context);
	}

	/**
	 * @deprecated Use {@link #create(Properties.Source)} instead as {@code context} is no longer supported.
	 */
	@Deprecated
	public ConnectProperties(final Source source, final Source context)
	{
		this(noContext(source, context), factory());
	}

	@Deprecated
	private static Source noContext(final Source source, final Source context)
	{
		if(context!=null)
			throw new NoSuchMethodError("context not allowed, but was " + context);

		return source;
	}

	/**
	 * @deprecated Use
	 * {@link #ConnectProperties(File) ConnectProperties}({@link #getDefaultPropertyFile()})
	 * instead.
	 */
	@Deprecated
	public ConnectProperties()
	{
		this(getDefaultPropertyFile(), null);
	}

	/**
	 * @deprecated Use
	 * {@link #ConnectProperties(File,Properties.Source) ConnectProperties}({@link #getDefaultPropertyFile()}, context)
	 * instead.
	 */
	@Deprecated
	public ConnectProperties(final Source context)
	{
		this(getDefaultPropertyFile(), context);
	}

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

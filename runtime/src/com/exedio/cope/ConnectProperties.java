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

import static java.lang.Math.toIntExact;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import com.exedio.cope.misc.FactoryProperties;
import com.exedio.cope.pattern.MediaFingerprintOffset;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.PoolProperties;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceFactory;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultProperties;
import io.micrometer.core.instrument.Tags;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;

public final class ConnectProperties extends FactoryProperties<ConnectProperties.Factory>
{
	// connection

	final ConnectionProperties connection = valnp("connection", ConnectionProperties::new);

	public String getConnectionUrl()
	{
		return connection.url;
	}

	public String getConnectionUsername()
	{
		return connection.username;
	}

	/**
	 * @deprecated Do not use this method anymore
	 */
	@Deprecated
	public String getConnectionPassword()
	{
		return connection.password;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		connection.putRevisionEnvironment("connection", e);
	}

	java.util.Properties newInfo()
	{
		final java.util.Properties result = new java.util.Properties();
		connection.setInfo(result);

		final DialectProbeInfo dialectProbeInfo =
				dialect.getServiceClass().getAnnotation(DialectProbeInfo.class);
		if(dialectProbeInfo!=null)
		{
			final String[] value = dialectProbeInfo.value();
			for(int i = 0; i<value.length; )
				result.setProperty(value[i++], value[i++]);
		}

		return result;
	}

	@Probe
	EnvironmentInfo probeConnect()
	{
		final EnvironmentInfo result = probeEnvironmentInfo();
		final CopeProbe probe = new CopeProbe(this, result);
		dialect.newInstance(probe);
		return result;
	}

	EnvironmentInfo probeEnvironmentInfo()
	{
		return connection.probe(newInfo());
	}


	// dialect

	final ServiceFactory<Dialect,CopeProbe> dialect = valueService("dialect", fromUrl(connection.url),
			Dialect.class, CopeProbe.class);

	private static Class<? extends Dialect> fromUrl(final String url)
	{
		final HashSet<Class<? extends Dialect>> result = new HashSet<>();
		for(final DialectUrlMapper mapper : dialectUrlMappers())
		{
			final Class<? extends Dialect> clazz = mapper.map(url);
			if(clazz!=null)
				result.add(clazz);
		}

		return
				(result.size()==1)
				? result.iterator().next()
				: null;
	}

	private static Iterable<DialectUrlMapper> dialectUrlMappers()
	{
		return ServiceLoader.load(DialectUrlMapper.class, DialectUrlMapper.class.getClassLoader());
	}

	{
		final DialectProbeInfo probeInfo =
				dialect.getServiceClass().getAnnotation(DialectProbeInfo.class);
		if(probeInfo!=null)
		{
			final String[] value = probeInfo.value();
			if(value.length==0)
				throw newExceptionDialectProbeInfo("being empty");
			if(value.length%2!=0)
				throw newExceptionDialectProbeInfo("containing an odd (" + value.length + ") number of elements");
			for(int i = 0; i<value.length; i++)
				if(value[i].isEmpty())
					throw newExceptionDialectProbeInfo("containing an empty element at position " + i);
		}
	}

	private IllegalPropertiesException newExceptionDialectProbeInfo(final String detail)
	{
		return newException("dialect",
				"specifies " + dialect.getServiceClass().getName() + " with @DialectProbeInfo " +
				detail);
	}

	public static Iterable<?> getDialectUrlMappers()
	{
		return dialectUrlMappers();
	}

	public String getDialect()
	{
		return dialect.getServiceClass().getName();
	}

	Tags tags()
	{
		return
				connection.tags("connection").and(
				"dialect", dialect.getServiceClass().getName());
	}


	// disableSupport

	private final boolean disablePreparedStatements = value("disableSupport.preparedStatements", false);
	        final boolean disableCheckConstraint    = value("disableSupport.checkConstraint", false);
	private final boolean disableNativeDate         = value("disableSupport.nativeDate", factory.disableNativeDate);
	private final boolean disableUniqueViolation    = value("disableSupport.uniqueViolation", false);
	private final boolean disableSemicolon          = value("disableSupport.semicolon", false);

	/**
	 * @deprecated
	 * Not supported any longer.
	 * Always returns false.
	 */
	@Deprecated
	public boolean isSupportDisabledForEmptyStrings()
	{
		return false;
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

	/**
	 * Returns true if multiple sql statements are not joined using a semicolon.
	 * This may slow down certain operations such as schema creation / destruction.
	 * But it may help to investigate problems of one of the joined sql statements.
	 */
	public boolean isSupportDisabledForSemicolon()
	{
		return disableSemicolon;
	}


	// fulltextIndex

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

	/**
	 * MySQL maximum length is 64:
	 * <a href="https://dev.mysql.com/doc/refman/8.0/en/identifier-length.html">...</a>
	 * MySQL does support check constraints only since version 8.
	 * On MySQL a primary key constraint does not have a name.
	 * <p>
	 * PostgreSQL maximum length is 63:
	 * <a href="https://www.postgresql.org/docs/9.6/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS">...</a>
	 */
	final Trimmer trimmerStandard = valueTrimmer("", 60);
	final Trimmer trimmerLegacy   = valueTrimmer("Legacy", factory.legacyNameLength ? 25 : trimmerStandard.maxLength);

	Trimmer valueTrimmer(
			@Nonnull final String keyPostfix,
			final int defaultValue)
	{
		return new Trimmer(value("schema.nameLength" + keyPostfix, defaultValue, 1));
	}

	Trimmer trimmer(@Nonnull final TrimClass trimClass)
	{
		return switch(trimClass)
		{
			case standard -> trimmerStandard;
			case legacy   -> trimmerLegacy;
		};
	}

	enum TrimClass
	{
		standard, legacy
	}


	final PrimaryKeyGenerator primaryKeyGenerator = value("schema.primaryKeyGenerator", factory.primaryKeyGenerator);
	final boolean longSyntheticNames = value("schema.tableInNames", false);

	/**
	 * The table name for the revision information.
	 * The value "while" prevents name collisions
	 * with other tables,
	 * since "while" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final String revisionTableName = value("schema.revision.table", factory.revisionTableName);

	/**
	 * The name of the primary key constraint
	 * on the table for the revision information.
	 * The value "protected" prevents name collisions
	 * with other tables,
	 * since "protected" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	final String revisionPrimaryKeyName = value("schema.revision.unique", factory.revisionPrimaryKeyName); // TODO rename key


	// revise

	/**
	 * If true, {@link Model#reviseIfSupportedAndAutoEnabled} will trigger execution
	 * of revisions if necessary;
	 * if false, it will throw an exception if revisions are pending.
	 * Default is true.
	 */
	final boolean autoReviseEnabled = value("revise.auto.enabled", true);
	static final String reviseSavepointKey = "revise.savepoint";
	final boolean reviseSavepoint = value(reviseSavepointKey, false);


	// connectionPool

	final PoolProperties connectionPool = value("connectionPool", PoolProperties.factory(50));

	public int getConnectionPoolIdleInitial()
	{
		return connectionPool.getIdleInitial();
	}

	public int getConnectionPoolIdleLimit()
	{
		return connectionPool.getIdleLimit();
	}


	// querySearchSizeLimit

	private final int querySearchSizeLimit = value("query.searchSizeLimit", 100000, 1);

	/**
	 * @see Query#getSearchSizeLimit()
	 * @see Query#setSearchSizeLimit(int)
	 */
	public int getQuerySearchSizeLimit()
	{
		return querySearchSizeLimit;
	}


	// cache

	private final int itemCacheLimit      = value("cache.item.limit",     100000, 0);
	private final int queryCacheLimit     = value("cache.query.limit",     10000, 0);
	private final int queryCacheSizeLimit = value("cache.query.sizeLimit", 10000, 0);
	        final boolean cacheStamps     = value("cache.stamps", true);

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


	// vault

	final VaultProperties vault = value("vault", false, VaultProperties.factory());

	public VaultProperties getVaultProperties()
	{
		return vault;
	}

	@Nonnull
	public VaultProperties getVaultPropertiesStrict()
	{
		if(vault==null)
			throw new IllegalArgumentException(
					"vaults are disabled (vault=false) in " + getSource());
		return vault;
	}


	// changeListeners

	final     int changeListenersQueueCapacity = value("changeListeners.queueCapacity", 1000, 1);
	final ThreadSwarmProperties chaListThreads = valnp("changeListeners.threads", ThreadSwarmProperties::new);


	final ClusterProperties cluster = value("cluster", false, ClusterProperties.factory());

	public boolean isClusterEnabled()
	{
		return cluster!=null;
	}


	// media

	private final String mediaRootUrl    = value("media.rooturl", factory.mediaRootUrl);
	private final Duration mediaOffsetExpires = valueIntMillis("media.offsetExpires", ofSeconds(5), Duration.ZERO);
	private final int mediaFingerOffset  = value("media.fingerprintOffset", 0, 0);
	private final String mediaUrlSecret = valueMediaUrlSecret("media.url.secret");

	private Duration valueIntMillis(
			final String key,
			final Duration defaultValue,
			final Duration minimum)
	{
		return value(key, defaultValue, minimum, ofMillis(Integer.MAX_VALUE));
	}

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
		return mediaRootUrl;
	}

	/**
	 * Returns the offset, the Expires http header of media
	 * is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 * If zero, no Expires header is sent.
	 *
	 * @deprecated Use {@link #getMediaMaxAge()} instead
	 */
	@Deprecated
	public int getMediaOffsetExpires()
	{
		return toIntExact(mediaOffsetExpires.toMillis()); // toIntExact cannot fail because of valueIntMillis
	}

	private final Duration mediaMaxAge = (mediaOffsetExpires.compareTo(ofSeconds(1))>=0) ? mediaOffsetExpires.withNanos(0) : null;

	public int getMediaMaxAge()
	{
		return mediaMaxAge!=null ? toIntExact(mediaMaxAge.getSeconds()) : 0; // toIntExact cannot fail because of valueIntMillis;
	}

	/**
	 * Returns a duration suitable for
	 * {@link com.exedio.cope.pattern.MediaServlet#getMaximumAge(com.exedio.cope.pattern.MediaPath.Locator)}.
	 */
	@SuppressWarnings("JavadocReference") // OK: protected member of public class is part of public API
	public Duration getMediaServletMaximumAge()
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


	// construction

	public static Factory factory()
	{
		return new Factory(
				false, // disableNativeDate
				false, // legacyNameLength
				PrimaryKeyGenerator.memory,
				"while", "protected",
				"media/");
	}

	public static final class Factory implements Properties.Factory<ConnectProperties>
	{
		private final boolean disableNativeDate;
		private final boolean legacyNameLength;
		private final PrimaryKeyGenerator primaryKeyGenerator;
		private final String revisionTableName;
		private final String revisionPrimaryKeyName;
		private final String mediaRootUrl;

		Factory(
				final boolean disableNativeDate,
				final boolean legacyNameLength,
				final PrimaryKeyGenerator primaryKeyGenerator,
				final String revisionTableName,
				final String revisionPrimaryKeyName,
				final String mediaRootUrl)
		{
			this.disableNativeDate = disableNativeDate;
			this.legacyNameLength = legacyNameLength;
			this.primaryKeyGenerator = primaryKeyGenerator;
			this.revisionTableName = revisionTableName;
			this.revisionPrimaryKeyName = revisionPrimaryKeyName;
			this.mediaRootUrl = mediaRootUrl;
		}

		public Factory disableNativeDate()
		{
			return new Factory(
					true, legacyNameLength, primaryKeyGenerator,
					revisionTableName, revisionPrimaryKeyName,
					mediaRootUrl);
		}

		/**
		 * Changes default of {@code schema.nameLengthLegacy} to {@code 25}.
		 */
		public Factory legacyNameLength()
		{
			return new Factory(
					disableNativeDate, true, primaryKeyGenerator,
					revisionTableName, revisionPrimaryKeyName,
					mediaRootUrl);
		}

		public Factory primaryKeyGeneratorSequence()
		{
			return new Factory(
					disableNativeDate, legacyNameLength, PrimaryKeyGenerator.sequence,
					revisionTableName, revisionPrimaryKeyName,
					mediaRootUrl);
		}

		public Factory revisionTable(final String name, final String primaryKeyName)
		{
			return new Factory(
					disableNativeDate, legacyNameLength, primaryKeyGenerator,
					name, primaryKeyName,
					mediaRootUrl);
		}

		public Factory mediaRootUrl(final String mediaRootUrl)
		{
			return new Factory(
					disableNativeDate, legacyNameLength, primaryKeyGenerator,
					revisionTableName, revisionPrimaryKeyName,
					mediaRootUrl);
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

	public ConnectProperties(final File file)
	{
		this(file, null);
	}

	public static ConnectProperties create(final Source source)
	{
		return new ConnectProperties(source, (Source)null);
	}

	ConnectProperties(final Source source, final Factory factory)
	{
		super(source, factory);

		if(cluster!=null && !primaryKeyGenerator.persistent)
			throw newException("cluster",
					"not supported together with schema.primaryKeyGenerator=" + primaryKeyGenerator);
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
				probeConnect() +
				(vault!=null ? ' ' + vault.probe() : "");
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
}

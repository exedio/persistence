/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;

public final class ConnectProperties extends com.exedio.cope.util.Properties
{
	private static final String DIALECT_FROM_URL = "from url";
	private final StringField dialectCode = new StringField("dialect", DIALECT_FROM_URL);

	private final StringField connectionUrl      = new StringField("connection.url");
	private final StringField connectionUsername = new StringField("connection.username");
	private final StringField connectionPassword = new StringField("connection.password", true);
	final BooleanField connectionTransactionIsolationReadCommitted = new BooleanField("connection.transactionIsolation.readCommitted", false);
	final BooleanField connectionTransactionIsolationRepeatableRead = new BooleanField("connection.transactionIsolation.repeatableRead", true);

	private final BooleanField disablePreparedStatements = new BooleanField("disableSupport.preparedStatements", false);
	private final BooleanField disableUniqueViolation    = new BooleanField("disableSupport.uniqueViolation", false);
	private final BooleanField disableEmptyStrings       = new BooleanField("disableSupport.emptyStrings", false);
	private final BooleanField disableNativeDate         = new BooleanField("disableSupport.nativeDate", false);
	private final BooleanField disableNotNull            = new BooleanField("disableSupport.notNull", false);
	private final BooleanField disableSemicolon          = new BooleanField("disableSupport.semicolon", true); // TODO
	private final BooleanField fulltextIndex = new BooleanField("fulltextIndex", false);

	final BooleanField hsqldbNullsAreSortedLow = new BooleanField("hsqldb.nullsAreSortedLow", false);

	// schema

	final PrimaryKeyGenerator primaryKeyGenerator = myEnumValue("schema.primaryKeyGenerator", PrimaryKeyGenerator.class, PrimaryKeyGenerator.memory);
	final BooleanField updateCounter = new BooleanField("schema.updateCounter", true);
	final BooleanField longSyntheticNames = new BooleanField("schema.tableInNames", false);

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

	private final BooleanField mysqlLowerCaseTableNames = new BooleanField("schema.mysql.lower_case_table_names", false);

	/**
	 * If true, {@link Model#reviseIfSupportedAndAutoEnabled} will trigger execution
	 * of revisions if necessary;
	 * if false, it will throw an exception if revisions are pending.
	 * Default is true.
	 */
	final BooleanField autoReviseEnabled = new BooleanField("revise.auto.enabled", true);


	private final IntField connectionPoolIdleInitial = new IntField("connectionPool.idleInitial", 0, 0);
	private final IntField connectionPoolIdleLimit   = new IntField("connectionPool.idleLimit",  50, 0);

	private final IntField querySearchSizeLimit = new IntField("query.searchSizeLimit", 100000, 1);

	private final IntField itemCacheLimit  = new IntField("cache.item.limit", 100000, 0);
	private final IntField queryCacheLimit = new IntField("cache.query.limit", 10000, 0);
	private final IntField queryCacheSizeLimit = new IntField("cache.query.sizeLimit", 10000, 0);
	final BooleanField itemCacheInvalidateLast       = new BooleanField("cache.item.invalidateLast", true);
	final     IntField itemCacheInvalidateLastMargin = new     IntField("cache.item.invalidateLast.margin", 0, 0);

	final IntField dataFieldBufferSizeDefault = new IntField("dataField.bufferSizeDefault", 20*1024, 1);
	final IntField dataFieldBufferSizeLimit   = new IntField("dataField.bufferSizeLimit", 1024*1024, 1);

	final     IntField changeListenersQueueCapacity = new     IntField("changeListeners.queueCapacity", 1000, 1);
	final     IntField changeListenersThreads       = new     IntField("changeListeners.threads",        1, 1);
	final     IntField changeListenersThreadsMax    = new     IntField("changeListeners.threadsMax",    10, 1);
	final BooleanField changeListenersPrioritySet   = new BooleanField("changeListeners.prioritySet",   false);
	final     IntField changeListenersPriority      = new     IntField("changeListeners.priority",      MAX_PRIORITY, MIN_PRIORITY);

	final StringField mediaRooturl =  new StringField("media.rooturl", "media/");
	private final IntField mediaOffsetExpires = new IntField("media.offsetExpires", 1000 * 5, 0);
	private final String mediaUrlSecret = noContext()
			? checkMediaUrlSecret(new StringField("media.url.secret", "").stringValue())
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
			final String url = connectionUrl.stringValue();
			final String prefix = "jdbc:";
			if(!url.startsWith(prefix))
				throw new RuntimeException("cannot parse " + connectionUrl.getKey() + '=' + url + ", missing prefix '" + prefix + '\'');
			final int pos = url.indexOf(':', prefix.length());
			if(pos<0)
				throw new RuntimeException("cannot parse " + connectionUrl.getKey() + '=' + url + ", missing second colon");
			dialectCode = url.substring(prefix.length(), pos);
		}
		else
			dialectCode = dialectCodeRaw;

		dialect = getDialectConstructor(dialectCode, source.getDescription());

		if(connectionTransactionIsolationReadCommitted.booleanValue() &&
			connectionTransactionIsolationRepeatableRead.booleanValue())
			throw new RuntimeException(connectionTransactionIsolationReadCommitted.getKey() + " and " + connectionTransactionIsolationRepeatableRead.getKey() + " cannot be enabled both");
		if(connectionPoolIdleInitial.intValue()>connectionPoolIdleLimit.intValue())
			throw new RuntimeException("value for " + connectionPoolIdleInitial.getKey() + " must not be greater than " + connectionPoolIdleLimit.getKey());
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
			return dialectClass.getDeclaredConstructor(new Class<?>[]{DialectParameters.class});
		}
		catch(final NoSuchMethodException e)
		{
			throw new RuntimeException("class " + dialectName + " from " + sourceDescription + " does not have the required constructor.");
		}
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

	public String getConnectionUrl()
	{
		return connectionUrl.stringValue();
	}

	public String getConnectionUsername()
	{
		return connectionUsername.stringValue();
	}

	public String getConnectionPassword()
	{
		return connectionPassword.stringValue();
	}

	java.util.Properties newConnectionInfo()
	{
		final java.util.Properties result = new java.util.Properties();
		result.setProperty("user",     connectionUsername.stringValue());
		result.setProperty("password", connectionPassword.stringValue());
		return result;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		e.put("connection.url",  connectionUrl. stringValue());
		e.put("connection.user", connectionUsername.stringValue());
	}

	public boolean isSupportDisabledForPreparedStatements()
	{
		return disablePreparedStatements.booleanValue();
	}

	boolean isSupportDisabledForUniqueViolation()
	{
		return disableUniqueViolation.booleanValue();
	}

	public boolean isSupportDisabledForEmptyStrings()
	{
		return disableEmptyStrings.booleanValue();
	}

	public boolean isSupportDisabledForNativeDate()
	{
		return disableNativeDate.booleanValue();
	}

	public boolean isSupportDisabledForNotNull()
	{
		return disableNotNull.booleanValue();
	}

	public boolean isSupportDisabledForSemicolon()
	{
		return disableSemicolon.booleanValue();
	}

	String filterTableName(final String tableName)
	{
		return
			mysqlLowerCaseTableNames.booleanValue()
			? tableName.toLowerCase(Locale.ENGLISH)
			: tableName;
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

	int getQueryCacheSizeLimit()
	{
		return queryCacheSizeLimit.intValue();
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
	private <E extends Enum<E>> E myEnumValue(
			final String key,
			final Class<E> enumClass,
			final E defaultValue)
	{
		return Enum.valueOf(enumClass, new StringField(key, defaultValue.name()).stringValue());
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

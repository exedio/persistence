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

import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PoolCounter;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vault.VaultService;
import gnu.trove.TLongHashSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

final class Connect
{
	final Instant date = Instant.now();
	private final RevisionsConnect revisions;
	final ConnectProperties properties;
	final Dialect dialect;

	final boolean supportsEmptyStrings;
	final boolean supportsUTF8mb4;
	final SortedSet<String> supportedDataHashAlgorithms;
	final boolean supportsRandom;
	final boolean supportsCheckConstraints;
	final boolean supportsNativeDate;
	final boolean supportsUniqueViolation;

	final ConnectionFactory connectionFactory;
	final ConnectionPool connectionPool;
	final Marshallers marshallers;
	final Executor executor;
	final Database database;
	final VaultService vault;
	final ItemCache itemCache;
	final QueryCache queryCache;
	final Cluster cluster;
	final ChangeListenerDispatcher changeListenerDispatcher;

	boolean revised = false;

	Connect(
			final String modelName,
			final Types types,
			final Revisions.Factory revisionsFactory,
			final ConnectProperties properties,
			final Transactions transactions,
			final ChangeListeners changeListeners)
	{
		this.properties = properties;

		final CopeProbe probe = properties.probeInternal();

		this.revisions = RevisionsConnect.wrap(probe.environmentInfo, revisionsFactory);
		this.dialect = properties.dialect.newInstance(probe);

		supportsEmptyStrings = !properties.isSupportDisabledForEmptyStrings() && dialect.supportsEmptyStrings();
		supportsUTF8mb4 = dialect.supportsUTF8mb4();
		supportedDataHashAlgorithms = toUnmodifiableSortedSet(dialect.getBlobHashAlgorithms());
		supportsRandom = dialect.supportsRandom();
		// SchemaInfo
		supportsCheckConstraints = dialect.dsmfDialect.supportsCheckConstraints();
		supportsNativeDate = !properties.isSupportDisabledForNativeDate() && (dialect.getDateTimestampType()!=null);
		supportsUniqueViolation = !properties.isSupportDisabledForUniqueViolation() && dialect.supportsUniqueViolation();

		this.connectionFactory = new ConnectionFactory(properties.connection, probe.driver, dialect);
		final Pool<Connection> pool = new Pool<>(
				connectionFactory,
				properties.connectionPool,
				new PoolCounter());
		this.connectionPool = new ConnectionPool(pool);
		this.marshallers = new Marshallers(supportsNativeDate);
		this.executor = new Executor(dialect, supportsUniqueViolation, properties, marshallers);
		this.database = new Database(
				dialect.dsmfDialect,
				probe,
				dialect,
				connectionPool,
				executor,
				transactions,
				revisions);
		{
			final VaultProperties props = properties.dataFieldVault;
			this.vault = props!=null ? props.newService() : null;
		}

		this.itemCache = new ItemCache(types.typeListSorted, properties);
		this.queryCache = new QueryCache(properties.getQueryCacheLimit());

		{
			final ClusterProperties props = properties.cluster;
			//noinspection ThisEscapedInObjectConstruction
			this.cluster = props!=null ? new Cluster(modelName, types, props, this) : null;
		}

		this.changeListenerDispatcher =
			new ChangeListenerDispatcher(
					types, modelName, changeListeners, properties);
	}

	private static SortedSet<String> toUnmodifiableSortedSet(final String[] array)
	{
		if(array==null || array.length==0)
			return Collections.emptySortedSet();

		return Collections.unmodifiableSortedSet(
				new TreeSet<>(Arrays.asList(array)));
	}

	void close()
	{
		changeListenerDispatcher.startClose();
		if(cluster!=null)
			cluster.startClose();

		connectionPool.flush();

		if(vault!=null)
			vault.close();

		// let threads have some time to terminate,
		// doing other thing in the mean time
		changeListenerDispatcher.joinClose();
		if(cluster!=null)
			cluster.joinClose();
	}

	void invalidate(
			final TLongHashSet[] invalidations,
			final boolean propagateToCluster,
			final TransactionInfo transactionInfo)
	{
		itemCache.invalidate(invalidations);
		queryCache.invalidate(invalidations);
		if(propagateToCluster && cluster!=null)
			cluster.sendInvalidate(invalidations);
		changeListenerDispatcher.invalidate(invalidations, transactionInfo);
	}

	void createSchema()
	{
		database.createSchema();
		clearCache();
	}

	void deleteSchema(final boolean forTest)
	{
		database.deleteSchema(forTest && properties.deleteSchemaForTest);
		clearCache();
		database.flushSequences();
	}

	void dropSchema()
	{
		database.dropSchema();
		clearCache();
	}

	void tearDownSchema()
	{
		database.tearDownSchema();
		clearCache();
	}

	void clearCache()
	{
		itemCache.clear();
		queryCache.clear();
	}

	void purgeSchema(final JobContext ctx)
	{
		dialect.purgeSchema(ctx, database, connectionPool);
	}

	Revisions getRevisions()
	{
		return revisions!=null ? revisions.get() : null;
	}

	void revise(final boolean explicitRequest)
	{
		if(revised) // synchronization is done by Model#revise
			return;

		revisions.get().revise(
				properties,
				connectionFactory, connectionPool,
				executor, database.probe, dialect,
				explicitRequest);

		revised = true;
	}

	Map<Integer, byte[]> getRevisionLogs(final boolean withMutex)
	{
		return Revisions.getLogs(withMutex, properties, connectionPool, executor);
	}

	List<ThreadController> getThreadControllers()
	{
		final ArrayList<ThreadController> result = new ArrayList<>(2);
		changeListenerDispatcher.addThreadControllers(result);
		if(cluster!=null)
			cluster.addThreadControllers(result);
		return Collections.unmodifiableList(result);
	}

	String getSchemaSavepoint() throws SQLException
	{
		return dialect.getSchemaSavepoint(connectionPool);
	}
}

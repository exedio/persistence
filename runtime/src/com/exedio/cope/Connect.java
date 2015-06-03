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
import gnu.trove.TIntHashSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class Connect
{
	final long date = System.currentTimeMillis();
	private final RevisionsConnect revisions;
	final ConnectProperties properties;
	final Dialect dialect;
	final ConnectionFactory connectionFactory;
	final ConnectionPool connectionPool;
	final Marshallers marshallers;
	final Executor executor;
	final Database database;
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

		final DialectParameters dialectParameters = properties.probe();

		this.revisions = RevisionsConnect.wrap(dialectParameters.environmentInfo, revisionsFactory);
		this.dialect = properties.createDialect(dialectParameters);
		this.connectionFactory = new ConnectionFactory(properties, dialectParameters.driver, dialect);
		@SuppressWarnings("deprecation") // TODO when property context is not supported anymore
		final Pool<Connection> pool = new Pool<>(
				connectionFactory,
				properties.getConnectionPoolIdleLimit(),
				properties.getConnectionPoolIdleInitial(),
				new PoolCounter());
		this.connectionPool = new ConnectionPool(pool);
		this.marshallers = new Marshallers(supportsNativeDate());
		this.executor = new Executor(dialect, properties, marshallers);
		this.database = new Database(
				dialect.dsmfDialect,
				dialectParameters,
				dialect,
				connectionPool,
				executor,
				transactions,
				this.revisions);

		this.itemCache = new ItemCache(types.typeListSorted, properties);
		this.queryCache = new QueryCache(properties.getQueryCacheLimit());

		{
			final ClusterProperties clusterProperties =
				ClusterProperties.get(properties);

			if(clusterProperties!=null)
				this.cluster = new Cluster(modelName, types, clusterProperties, this);
			else
				this.cluster = null;
		}

		this.changeListenerDispatcher =
			new ChangeListenerDispatcher(
					types, modelName, changeListeners, properties);
	}

	void close()
	{
		changeListenerDispatcher.startClose();
		if(cluster!=null)
			cluster.startClose();

		connectionPool.flush();

		// let threads have some time to terminate,
		// doing other thing in the mean time
		changeListenerDispatcher.joinClose();
		if(cluster!=null)
			cluster.joinClose();
	}

	boolean supportsEmptyStrings()
	{
		return !properties.isSupportDisabledForEmptyStrings() && dialect.supportsEmptyStrings();
	}

	boolean supportsNativeDate()
	{
		return !properties.isSupportDisabledForNativeDate() && (dialect.getDateTimestampType()!=null);
	}

	void invalidate(final TIntHashSet[] invalidations, final boolean propagateToCluster)
	{
		itemCache.invalidate(invalidations);
		queryCache.invalidate(invalidations);
		if(propagateToCluster && cluster!=null)
			cluster.sendInvalidate(invalidations);
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
				executor, database.dialectParameters, dialect,
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

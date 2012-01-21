/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntHashSet;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PoolCounter;
import com.exedio.cope.util.PrefixSource;
import com.exedio.dsmf.SQLRuntimeException;

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

	final boolean supportsTransactionIsolationReadCommitted;

	boolean revised = false;

	Connect(
			final String name,
			final Types types,
			final Revisions.Factory revisionsFuture,
			final ConnectProperties properties,
			final ChangeListeners changeListeners)
	{
		this.properties = properties;

		final String url = properties.getConnectionUrl();
		final Driver driver;

		try
		{
			driver = DriverManager.getDriver(url);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, url);
		}
		if(driver==null)
			throw new RuntimeException(url);

		final DialectParameters dialectParameters;
		Connection probeConnection = null;
		try
		{
			probeConnection = driver.connect(url, properties.newConnectionInfo());
			dialectParameters = new DialectParameters(properties, probeConnection);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, url);
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
				catch(final SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
		}
		this.revisions = RevisionsConnect.wrap(dialectParameters.environmentInfo, revisionsFuture);
		this.dialect = properties.createDialect(dialectParameters);
		this.connectionFactory = new ConnectionFactory(properties, driver, dialect);
		this.connectionPool = new ConnectionPool(new Pool<Connection>(
				connectionFactory,
				properties.getConnectionPoolIdleLimit(),
				properties.getConnectionPoolIdleInitial(),
				new PoolCounter()));
		this.marshallers = new Marshallers(supportsNativeDate());
		this.executor = new Executor(dialect, properties, marshallers);
		this.database = new Database(
				dialect.dsmfDialect,
				dialectParameters,
				dialect,
				connectionPool,
				executor,
				this.revisions);

		this.itemCache = new ItemCache(types.typeListSorted, properties);
		this.queryCache = new QueryCache(properties.getQueryCacheLimit());

		if(properties.cluster.booleanValue())
		{
			final ClusterProperties clusterProperties =
				ClusterProperties.get(new PrefixSource(properties.getContext(), "cluster."));

			if(clusterProperties!=null)
				this.cluster = new Cluster(name, types, clusterProperties, this);
			else
				this.cluster = null;
		}
		else
		{
			this.cluster = null;
		}

		this.changeListenerDispatcher =
			new ChangeListenerDispatcher(
					types, name, changeListeners, properties);

		this.supportsTransactionIsolationReadCommitted =
			!dialect.fakesSupportTransactionIsolationReadCommitted() &&
			dialectParameters.supportsTransactionIsolationReadCommitted;
	}

	void close()
	{
		changeListenerDispatcher.startClose();

		if(cluster!=null)
			cluster.close();

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

	void deleteSchema()
	{
		{
			//final long start = System.currentTimeMillis();
			dialect.dsmfDialect.deleteSchema(database.makeSchema(false));
			//System.out.println("experimental deleteSchema " + (System.currentTimeMillis()-start) + "ms");
		}
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

	Revisions getRevisions()
	{
		return revisions!=null ? revisions.get() : null;
	}

	void revise()
	{
		if(revised) // synchronization is done by Model#revise
			return;

		revisions.get().revise(properties, connectionFactory, connectionPool, executor, database.dialectParameters);

		revised = true;
	}

	Map<Integer, byte[]> getRevisionLogs(final boolean withMutex)
	{
		return revisions.get().getLogs(withMutex, properties, connectionPool, executor);
	}

	List<ThreadController> getThreadControllers()
	{
		final ArrayList<ThreadController> result = new ArrayList<ThreadController>(2);
		changeListenerDispatcher.addThreadControllers(result);
		if(cluster!=null)
			cluster.addThreadControllers(result);
		return Collections.unmodifiableList(result);
	}
}

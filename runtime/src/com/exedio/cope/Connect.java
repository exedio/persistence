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

import gnu.trove.TIntHashSet;

import java.sql.Connection;
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
	final ConnectProperties properties;
	final boolean log;
	final Dialect dialect;
	final ConnectionFactory connectionFactory;
	final ConnectionPool connectionPool;
	final Executor executor;
	final Database database;
	final ItemCache itemCache;
	final QueryCache queryCache;
	final ClusterSenderMulticast clusterSender;
	final ClusterListenerMulticast clusterListener;

	final boolean supportsReadCommitted;

	boolean revised = false;

	Connect(
			final String name,
			final Types types,
			final Revisions revisions,
			final ConnectProperties properties,
			final ChangeListeners changeListeners)
	{
		this.properties = properties;
		this.log = properties.isLoggingEnabled();

		final DialectParameters dialectParameters;
		Connection probeConnection = null;
		try
		{
			probeConnection = DriverManager.getConnection(
					properties.getDatabaseUrl(),
					properties.getDatabaseUser(),
					properties.getDatabasePassword());
			dialectParameters = new DialectParameters(properties, probeConnection);
		}
		catch(final SQLException e)
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
				catch(final SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
		}

		this.dialect = properties.createDialect(dialectParameters);
		this.connectionFactory = new ConnectionFactory(properties, dialect);
		this.connectionPool = new ConnectionPool(new Pool<Connection>(
				connectionFactory,
				properties.getConnectionPoolIdleLimit(),
				properties.getConnectionPoolIdleInitial(),
				new PoolCounter()));
		this.executor = new Executor(dialect, properties);
		this.database = new Database(
				dialect.dsmfDialect,
				dialectParameters,
				dialect,
				connectionPool,
				executor,
				revisions);

		this.itemCache = new ItemCache(types.typeListSorted, properties.getItemCacheLimit());
		this.queryCache = new QueryCache(properties.getQueryCacheLimit());

		if(properties.cluster.booleanValue())
		{
			final ClusterProperties clusterProperties = ClusterProperties.get(new PrefixSource(properties.getContext(), "cluster."));
			if(clusterProperties!=null)
			{
				this.clusterSender   = new ClusterSenderMulticast(clusterProperties);
				this.clusterListener = new ClusterListenerMulticast(clusterProperties, name, clusterSender, types.concreteTypeCount, this, changeListeners);
			}
			else
			{
				this.clusterSender   = null;
				this.clusterListener = null;
			}
		}
		else
		{
			this.clusterSender   = null;
			this.clusterListener = null;
		}

		this.supportsReadCommitted =
			!dialect.fakesSupportReadCommitted() &&
			dialectParameters.supportsTransactionIsolationLevel;
	}

	void close()
	{
		if(clusterSender!=null)
			clusterSender.close();
		if(clusterListener!=null)
			clusterListener.close();

		connectionPool.flush();
	}

	boolean supportsEmptyStrings()
	{
		return !properties.getDatabaseDontSupportEmptyStrings() && dialect.supportsEmptyStrings();
	}

	boolean supportsNativeDate()
	{
		return !properties.getDatabaseDontSupportNativeDate() && (dialect.getDateTimestampType()!=null);
	}

	void invalidate(final TIntHashSet[] invalidations, final boolean propagateToCluster)
	{
		itemCache.invalidate(invalidations);
		queryCache.invalidate(invalidations);
		if(propagateToCluster && clusterSender!=null)
			clusterSender.invalidate(invalidations);
	}

	void deleteSchema()
	{
		itemCache.clear();
		queryCache.clear();
		{
			//final long start = System.currentTimeMillis();
			dialect.dsmfDialect.deleteSchema(database.makeSchema(false));
			//System.out.println("experimental deleteSchema " + (System.currentTimeMillis()-start) + "ms");
		}
		database.flushSequences();
	}

	void revise(final Revisions revisions)
	{
		if(revised) // synchronization is done by Model#revise
			return;

		revisions.revise(properties, connectionPool, executor, database.dialectParameters.getRevisionEnvironment(), log);

		revised = true;
	}

	Map<Integer, byte[]> getRevisionLogs(final Revisions revisions)
	{
		return revisions.getLogs(properties, connectionPool, executor);
	}

	List<ThreadController> getThreadControllers()
	{
		final ArrayList<ThreadController> result = new ArrayList<ThreadController>();
		if(clusterListener!=null)
			clusterListener.addThreadControllers(result);
		return Collections.unmodifiableList(result);
	}
}

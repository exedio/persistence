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
import java.util.Map;

import com.exedio.cope.util.Pool;
import com.exedio.cope.util.PoolCounter;
import com.exedio.dsmf.SQLRuntimeException;

final class Connect
{
	final long date = System.currentTimeMillis();
	final ConnectProperties properties;
	final Dialect dialect;
	final ConnectionPool connectionPool;
	final Executor executor;
	final Database database;
	final ItemCache itemCache;
	final QueryCache queryCache;
	final ClusterSender clusterSender;
	final ClusterListener clusterListener;
	
	Connect(
			final Types types,
			final Revisions revisions,
			final ConnectProperties properties)
	{
		this.properties = properties;
		
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
		catch(SQLException e)
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
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "close");
				}
			}
		}
		
		this.dialect = properties.createDialect(dialectParameters);
		this.connectionPool = new ConnectionPool(new Pool<Connection>(
				new ConnectionFactory(properties, dialect),
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
		
		this.itemCache = new ItemCache(types.concreteTypeList, properties.getItemCacheLimit());
		this.queryCache = new QueryCache(properties.getQueryCacheLimit());
		
		if(database.cluster)
		{
			final ClusterConfig config = ClusterConfig.get(properties);
			if(config!=null)
			{
				this.clusterSender   = new ClusterSender  (config, properties);
				this.clusterListener = new ClusterListener(config, properties, clusterSender, types.concreteTypeCount, itemCache, queryCache);
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
	}
	
	void close()
	{
		if(clusterSender!=null)
			clusterSender.close();
		if(clusterListener!=null)
			clusterListener.close();
		
		database.close();
	}
	
	void invalidate(final TIntHashSet[] invalidations)
	{
		itemCache.invalidate(invalidations);
		queryCache.invalidate(invalidations);
		if(clusterSender!=null)
			clusterSender.invalidate(invalidations);
	}

	void deleteSchema()
	{
		itemCache.clear();
		queryCache.clear();
		{
			//final long start = System.currentTimeMillis();
			dialect.dsmfDialect.deleteSchema(database.makeSchema());
			//System.out.println("experimental deleteSchema " + (System.currentTimeMillis()-start) + "ms");
		}
		database.flushSequences();
	}

	void revise(final Revisions revisions)
	{
		revisions.revise(connectionPool, executor, database.dialectParameters.getRevisionEnvironment());
	}

	Map<Integer, byte[]> getRevisionLogs(final Revisions revisions)
	{
		return revisions.getRevisionLogs(connectionPool, executor);
	}
}

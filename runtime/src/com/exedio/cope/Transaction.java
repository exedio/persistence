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

import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TLongHashSet;
import gnu.trove.TLongObjectHashMap;
import io.micrometer.core.instrument.Timer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Transaction
{
	private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

	final Connect connect;
	final long id;
	final String name;
	final long startDate;
	final Timer.Sample startSample;

	/**
	 * index in array is {@link Type#cacheIdTransiently};
	 * value in array is a map, where the keys are {@link Item#pk item pks}
	 * and the values are {@link Entity}s
	 */
	private final TLongObjectHashMap<Entity>[] entityMaps;
	private TLongHashSet[] invalidations = null;
	@SuppressFBWarnings("VO_VOLATILE_INCREMENT") // OK: is never incremented concurrently, as this works on current transaction only
	private volatile int invalidationSize = 0;
	private Thread boundThread = null;
	ArrayList<QueryInfo> queryInfos = null;
	private Connection connection = null;
	private ConnectionPool connectionPool = null;
	private long cacheStamp = Long.MAX_VALUE;
	private boolean closed = false;

	Transaction(
			final Connect connect,
			final int concreteTypeCount,
			final long id,
			final String name,
			final long startDate)
	{
		this.connect = connect;
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.startSample = Timer.start();
		this.entityMaps = cast(new TLongObjectHashMap<?>[concreteTypeCount]);

		if(logger.isDebugEnabled())
			logger.debug(MessageFormat.format("{0} start: {1}", id, name));
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic array creation
	private static <X> TLongObjectHashMap<X>[] cast(final TLongObjectHashMap[] o)
	{
		return o;
	}

	/**
	 * calling this method directly breaks Model.boundTransactions
	 */
	void bindToCurrentThread()
	{
		if ( closed )
		{
			throw new IllegalStateException("cannot join closed transaction " + id);
		}
		if ( boundThread!=null && !boundThread.equals(Thread.currentThread()) )
		{
			throw new IllegalStateException("transaction already bound to other thread: " + boundThread.getId());
		}
		boundThread = Thread.currentThread();
	}

	boolean assertBoundToCurrentThread()
	{
		return Thread.currentThread().equals(boundThread);
	}

	/**
	 * calling this method directly breaks Model.boundTransactions
	 */
	void unbindThread()
	{
		if ( boundThread==null )
		{
			throw new RuntimeException( "transaction not bound to any thread" );
		}
		boundThread = null;
	}

	public boolean isClosed()
	{
		return closed;
	}

	Entity getEntity(final Item item, final boolean present)
	{
		assert !closed : name;

		final Type<?> type = item.type;
		final long pk = item.pk;

		TLongObjectHashMap<Entity> entityMap = entityMaps[type.cacheIdTransiently];
		if(entityMap==null)
		{
			entityMap = new TLongObjectHashMap<>();
			entityMaps[type.cacheIdTransiently] = entityMap;
		}

		Entity result = entityMap.get(pk);
		if(result==null)
		{
			final State state;
			if ( present )
			{
				if ( isInvalidated(item) )
				{
					state = connect.database.load(getConnection(), item);
				}
				else
				{
					state = connect.itemCache.getState(this, item);
				}
			}
			else
			{
				state = new CreatedState( this, item );
			}
			result = new Entity(this, state);
			entityMap.put(pk, result);
		}
		else
		{
			if ( !present )
			{
				throw new RuntimeException("tried to create entity that is already in cache: " + item.getCopeID() + '/' + result);
			}
		}
		return result;
	}

	void removeEntity(final Item item)
	{
		final TLongObjectHashMap<Entity> entityMap = entityMaps[item.type.cacheIdTransiently];
		if(entityMap!=null)
		{
			entityMap.remove( item.pk );
		}
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	WrittenState getGlobalState(final Item item)
	{
		return connect.itemCache.getStateIfPresent(item);
	}

	ArrayList<Object> search(final Query<?> query, final boolean totalOnly)
	{
		final QueryCache queryCache = connect.queryCache;
		if(!queryCache.isEnabled() || isInvalidated(query) || isExternal(query))
		{
			return query.searchUncached(this, totalOnly);
		}
		else
		{
			return queryCache.search(this, query, totalOnly);
		}
	}

	private static boolean isExternal(final Query<?> query)
	{
		if ( query.type.external )
		{
			return true;
		}
		if ( query.joins==null )
		{
			return false;
		}
		for(final Join nextJoin : query.joins)
		{
			if ( nextJoin.type.external )
			{
				return true;
			}
		}
		return false;
	}

	private boolean isInvalidated(final Query<?> query)
	{
		if ( isInvalidated(query.type) )
		{
			return true;
		}
		if ( query.joins==null )
		{
			return false;
		}
		for(final Join nextJoin : query.joins)
		{
			if ( isInvalidated(nextJoin.type) )
			{
				return true;
			}
		}
		return false;
	}

	private boolean isInvalidated(final Type<?> type)
	{
		if(type==null)
			throw new NullPointerException();

		if(invalidations==null)
			return false;

		for(final Type<?> instanceType : type.getTypesOfInstances())
		{
			final TLongHashSet invalidationsForType = invalidations[instanceType.cacheIdTransiently];
			if(invalidationsForType!=null && !invalidationsForType.isEmpty())
				return true;
		}

		return false;
	}

	private boolean isInvalidated( final Item item )
	{
		if(invalidations==null)
			return false;

		final TLongHashSet invalidationsForType = invalidations[item.type.cacheIdTransiently];
		return invalidationsForType!=null && invalidationsForType.contains(item.pk);
	}

	void addInvalidation(final Item item)
	{
		if(invalidations==null)
			invalidations = new TLongHashSet[entityMaps.length];

		final int typeTransiently = item.type.cacheIdTransiently;
		TLongHashSet invalidationsForType = invalidations[typeTransiently];
		if ( invalidationsForType==null )
		{
			invalidationsForType = new TLongHashSet();
			invalidations[typeTransiently] = invalidationsForType;
		}
		invalidationsForType.add(item.pk);
		//noinspection NonAtomicOperationOnVolatileField OK: is never incremented concurrently, as this works on current transaction only
		invalidationSize++;
	}

	public int getInvalidationSize()
	{
		return invalidationSize;
	}

	Entity getEntityIfActive(final Type<?> type, final long pk)
	{
		assert !closed : name;

		final TLongObjectHashMap<Entity> entityMap = entityMaps[type.cacheIdTransiently];
		if(entityMap==null)
			return null;
		return entityMap.get(pk);
	}

	Connection getConnection()
	{
		assert !closed : name;

		if(connection!=null)
			return connection;

		if(connectionPool!=null)
			throw new RuntimeException();

		connectionPool = connect.connectionPool;
		this.cacheStamp = connect.cacheStamp.next();
		final Connection connection = connectionPool.get(false);
		this.connection = connection;

		return connection;
	}

	long getCacheStamp()
	{
		assert !closed : name;
		if(connection==null)
			throw new RuntimeException(name);

		return cacheStamp;
	}

	long getCacheStampOrMax()
	{
		return cacheStamp;
	}

	/**
	 * calling this method directly breaks model.openTransactions
	 */
	void commitOrRollback(
			final boolean commit,
			final Model model,
			final TransactionCounter transactionCounter)
	{
		assert !closed : name;

		if(commit && invalidations!=null)
		{
			assert entityMaps.length==invalidations.length;
			model.types.unsetKnownToBeEmptyForTest(invalidations);
		}

		// notify database
		boolean hadConnection = false;
		try
		{
			if(connection!=null)
			{
				hadConnection = true;
				try
				{
					if(commit)
						connection.commit();
					else
						connection.rollback();
				}
				catch(final SQLException e)
				{
					logger.warn( "commit or rollback failed", e );
					throw new SQLRuntimeException(e, commit ? "commit" : "rollback");
				}
				catch(final RuntimeException | Error e)
				{
					logger.warn( "commit or rollback failed", e );
					throw e;
				}
			}
		}
		finally
		{
			if(connection!=null)
			{
				connectionPool.put(connection);
				connection = null;
				connectionPool = null;
			}

			closed = true;
			unbindThread();
		}

		if(invalidations!=null)
		{
			if(commit) // notify global cache
				connect.invalidate(invalidations, true, new TransactionInfoLocal(this));
		}

		transactionCounter.count(startSample, commit, hadConnection);

		if(logger.isDebugEnabled())
			logger.debug(MessageFormat.format("{0} {2}: {1}", id, name, (commit ? "commit" : "rollback")));

		// cleanup
		// do this at the end, because there is no hurry with cleanup
		for(int type = 0; type<entityMaps.length; type++)
		{
			final TLongObjectHashMap<Entity> map = entityMaps[type];
			if(map!=null)
			{
				map.clear();
				entityMaps[type] = null;
			}
		}
		if(invalidations!=null)
		{
			assert entityMaps.length==invalidations.length;
			for(int type = 0; type<invalidations.length; type++)
			{
				final TLongHashSet set = invalidations[type];
				if(set!=null)
				{
					set.clear();
					invalidations[type] = null;
				}
			}
			invalidations = null;
		}
	}


	// commitHooks

	final CommitHooks preCommitHooks = new CommitHooks();

	/**
	 * @see Model#addPreCommitHookIfAbsent(Runnable)
	 * @see #getPreCommitHookDuplicates()
	 */
	public int getPreCommitHookCount()
	{
		return preCommitHooks.getCount();
	}

	/**
	 * @see Model#addPreCommitHookIfAbsent(Runnable)
	 * @see #getPreCommitHookCount()
	 */
	public int getPreCommitHookDuplicates()
	{
		return preCommitHooks.getDuplicates();
	}

	final CommitHooks postCommitHooks = new CommitHooks();

	/**
	 * @see Model#addPostCommitHookIfAbsent(Runnable)
	 * @see #getPostCommitHookDuplicates()
	 */
	public int getPostCommitHookCount()
	{
		return postCommitHooks.getCount();
	}

	/**
	 * @see Model#addPostCommitHookIfAbsent(Runnable)
	 * @see #getPostCommitHookCount()
	 */
	public int getPostCommitHookDuplicates()
	{
		return postCommitHooks.getDuplicates();
	}


	public long getID()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public Date getStartDate()
	{
		return new Date(startDate);
	}

	public Thread getBoundThread()
	{
		return boundThread;
	}

	public void setQueryInfoEnabled(final boolean enabled)
	{
		this.queryInfos = enabled ? new ArrayList<>() : null;
	}

	public List<QueryInfo> getQueryInfos()
	{
		final ArrayList<QueryInfo> queryInfos = this.queryInfos;
		return queryInfos!=null ? Collections.unmodifiableList(queryInfos) : null;
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();

		bf.append(id);
		if(closed)
			bf.append("(closed)");
		bf.append(':');
		bf.append(name!=null ? name : ANONYMOUS);

		return bf.toString();
	}

	static final String ANONYMOUS = "ANONYMOUS_TRANSACTION";


	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getPostCommitHookCount()} instead
	 */
	@Deprecated
	public int getCommitHookCount()
	{
		return getPostCommitHookCount();
	}
}

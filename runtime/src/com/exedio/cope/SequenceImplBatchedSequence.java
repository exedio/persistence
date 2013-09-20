package com.exedio.cope;

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class SequenceImplBatchedSequence implements SequenceImpl
{
	private final int start;
	private final Executor executor;
	private final ConnectionPool connectionPool;
	private final String name;
	private final String quotedName;

	private final Object lock = new Object();

	SequenceImplBatchedSequence(
			final IntegerColumn column,
			final int start,
			final ConnectionPool connectionPool,
			final Database database)
	{
		this.start = start;
		this.executor = database.executor;
		this.connectionPool = connectionPool;
		this.name = database.properties.filterTableName(column.makeGlobalID("Seq"));
		this.quotedName = database.dsmfDialect.quoteName(this.name);
	}

	SequenceImplBatchedSequence(
			final String name,
			final int start,
			final ConnectProperties properties,
			final ConnectionPool connectionPool,
			final Executor executor,
			final com.exedio.dsmf.Dialect dsmfDialect)
	{
		this.start = start;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.name = properties.filterTableName(name);
		this.quotedName = dsmfDialect.quoteName(this.name);
	}

	public void makeSchema(final Schema schema)
	{
		new Sequence(schema, name, start);
	}

	private int batchStart = Integer.MIN_VALUE;
	private int batchIndex = 0;
	private static final int BATCH_SIZE = 1024;

	private Set<Integer> doubleCheck = Collections.synchronizedSet( new HashSet<Integer>() );

	public int next()
	{
		final int result = nextInternal();
		if ( !doubleCheck.add(result) )
		{
			throw new RuntimeException( "duplicate: "+result );
		}
		return result;
	}

	private int nextInternal()
	{
		synchronized ( lock )
		{
			if ( batchIndex<0 || batchIndex>=BATCH_SIZE )
			{
				throw new RuntimeException( "expected 0 < "+batchIndex+" < "+BATCH_SIZE );
			}
			if ( batchStart==Integer.MIN_VALUE || batchIndex+1==BATCH_SIZE )
			{
				final Connection connection = connectionPool.get(true);
				try
				{
					batchStart = executor.dialect.nextSequence(executor, connection, quotedName).intValue();
				}
				finally
				{
					connectionPool.put(connection);
				}
				if ( batchStart>(Integer.MAX_VALUE/BATCH_SIZE)-1 )
				{
					throw new RuntimeException( "overflow" );
				}
				if ( batchStart==Integer.MIN_VALUE )
				{
					// double check, because we use MIN_VALUE as magic value for "not initialized"
					throw new RuntimeException();
				}
				batchIndex = 0;
			}
			else
			{
				batchIndex++;
			}
			return batchStart*BATCH_SIZE + batchIndex;
		}
	}

	public int getNext()
	{
		synchronized ( lock )
		{
			if ( batchStart==Integer.MIN_VALUE || batchIndex==BATCH_SIZE )
			{
				final Connection connection = connectionPool.get(true);
				try
				{
					return executor.dialect.getNextSequence(executor, connection, name)*BATCH_SIZE;
				}
				finally
				{
					connectionPool.put(connection);
				}
			}
			else
			{
				return batchStart*BATCH_SIZE + batchIndex + 1;
			}
		}
	}

	public void delete(final StringBuilder bf, final Dialect dialect)
	{
		dialect.deleteSequence(bf, quotedName, start);
	}

	public void flush()
	{
		// empty
	}

	public String getSchemaName()
	{
		return name;
	}
}

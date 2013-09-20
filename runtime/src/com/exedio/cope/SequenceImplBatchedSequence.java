package com.exedio.cope;

import com.exedio.dsmf.Schema;

final class SequenceImplBatchedSequence implements SequenceImpl
{
	private static final int BATCH_SIZE = 1024;
	
	private final SequenceImplSequence sequence;

	private final Object lock = new Object();
	private int batchStart = Integer.MIN_VALUE;
	private int batchIndex = 0;

	SequenceImplBatchedSequence(
			final IntegerColumn column,
			final int start,
			final ConnectionPool connectionPool,
			final Database database)
	{
		this.sequence = new SequenceImplSequence( column, start, connectionPool, database );
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		sequence.makeSchema( schema );
	}

	@Override
	public int next()
	{
		synchronized ( lock )
		{
			if ( batchIndex<0 || batchIndex>=BATCH_SIZE )
			{
				throw new RuntimeException( "expected 0 < "+batchIndex+" < "+BATCH_SIZE );
			}
			if ( batchStart==Integer.MIN_VALUE || batchIndex+1==BATCH_SIZE )
			{
				// get from database:
				batchStart = sequence.next();
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

	@Override
	public int getNext()
	{
		synchronized ( lock )
		{
			if ( batchStart==Integer.MIN_VALUE || batchIndex==BATCH_SIZE )
			{
				return sequence.getNext() * BATCH_SIZE;
			}
			else
			{
				return batchStart*BATCH_SIZE + batchIndex + 1;
			}
		}
	}

	@Override
	public void delete(final StringBuilder bf, final Dialect dialect)
	{
		sequence.delete( bf, dialect );
	}

	@Override
	public void flush()
	{
		batchStart = Integer.MIN_VALUE;
		batchIndex = 0;
	}

	@Override
	public String getSchemaName()
	{
		return sequence.getSchemaName();
	}
}

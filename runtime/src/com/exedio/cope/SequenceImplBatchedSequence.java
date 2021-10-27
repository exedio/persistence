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

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import io.micrometer.core.instrument.Timer;

final class SequenceImplBatchedSequence implements SequenceImpl
{
	private static final long BATCH_NOT_INITIALIZED = Long.MIN_VALUE;
	private static final int BATCH_POWER = 6;
	static final int BATCH_SIZE = 1 << BATCH_POWER;

	private final Sequence.Type type;
	private final SequenceImplSequence sequence;

	private final Object lock = new Object();
	private long batchStart = BATCH_NOT_INITIALIZED;
	private int indexInBatch = 0;

	SequenceImplBatchedSequence(
			final Timer.Builder timer,
			final IntegerColumn column,
			final Sequence.Type type,
			final long start,
			final ConnectionPool connectionPool,
			final Database database)
	{
		this.type = type;
		this.sequence = new SequenceImplSequence( timer, column, type, start, connectionPool, database, String.valueOf(BATCH_POWER) );
	}

	@Override
	public void makeSchema(final Schema schema)
	{
		sequence.makeSchema( schema );
	}

	@Override
	public long next()
	{
		synchronized ( lock )
		{
			if ( indexInBatch<0 || indexInBatch>=BATCH_SIZE )
			{
				throw new RuntimeException( "expected 0 < "+indexInBatch+" < "+BATCH_SIZE );
			}
			if ( batchStart==BATCH_NOT_INITIALIZED || indexInBatch+1==BATCH_SIZE )
			{
				// get from database:
				batchStart = sequence.next();
				if ( batchStart>(type.MAX_VALUE/BATCH_SIZE)-1 )
				{
					throw new RuntimeException( "overflow: " + batchStart );
				}
				if ( batchStart==BATCH_NOT_INITIALIZED )
				{
					// double check
					throw new RuntimeException();
				}
				indexInBatch = 0;
			}
			else
			{
				indexInBatch++;
			}
			return batchStart*BATCH_SIZE + indexInBatch;
		}
	}

	@Override
	public long getNext()
	{
		synchronized ( lock )
		{
			if ( batchStart==BATCH_NOT_INITIALIZED || indexInBatch==BATCH_SIZE )
			{
				return sequence.getNext() * BATCH_SIZE;
			}
			else
			{
				return batchStart*BATCH_SIZE + indexInBatch + 1;
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
		synchronized ( lock )
		{
			batchStart = BATCH_NOT_INITIALIZED;
			indexInBatch = 0;
		}
	}

	@Override
	public String getSchemaName()
	{
		return sequence.getSchemaName();
	}
}

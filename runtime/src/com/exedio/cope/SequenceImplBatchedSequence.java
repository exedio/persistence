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

import com.exedio.dsmf.Schema;

final class SequenceImplBatchedSequence implements SequenceImpl
{
	private static final int BATCH_POWER = 6;
	private static final int BATCH_SIZE = 1 << BATCH_POWER;

	private final SequenceImplSequence sequence;

	private final Object lock = new Object();
	private int batchStart = Integer.MIN_VALUE;
	private int indexInBatch = 0;

	SequenceImplBatchedSequence(
			final IntegerColumn column,
			final int start,
			final ConnectionPool connectionPool,
			final Database database)
	{
		this.sequence = new SequenceImplSequence( column, start, connectionPool, database, String.valueOf(BATCH_POWER) );
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
			if ( indexInBatch<0 || indexInBatch>=BATCH_SIZE )
			{
				throw new RuntimeException( "expected 0 < "+indexInBatch+" < "+BATCH_SIZE );
			}
			if ( batchStart==Integer.MIN_VALUE || indexInBatch+1==BATCH_SIZE )
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
	public int getNext()
	{
		synchronized ( lock )
		{
			if ( batchStart==Integer.MIN_VALUE || indexInBatch==BATCH_SIZE )
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
		batchStart = Integer.MIN_VALUE;
		indexInBatch = 0;
	}

	@Override
	public String getSchemaName()
	{
		return sequence.getSchemaName();
	}
}

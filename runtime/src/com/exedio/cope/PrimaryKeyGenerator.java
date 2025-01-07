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

import com.exedio.dsmf.Sequence;

enum PrimaryKeyGenerator
{
	memory(false)
	{
		@Override
		SequenceImpl newSequenceImpl(
				final ModelMetrics metrics,
				final IntegerColumn column,
				final Sequence.Type type,
				final long start,
				final ConnectionPool connectionPool,
				final Database database)
		{
			return new SequenceImplMax(column, start, connectionPool);
		}
	},
	sequence(true)
	{
		@Override
		SequenceImpl newSequenceImpl(
				final ModelMetrics metrics,
				final IntegerColumn column,
				final Sequence.Type type,
				final long start,
				final ConnectionPool connectionPool,
				final Database database)
		{
			return new SequenceImplSequence(metrics, column, type, start, connectionPool, database, NAME_SUFFIX);
		}
	},
	batchedSequence(true)
	{
		@Override
		SequenceImpl newSequenceImpl(
				final ModelMetrics metrics,
				final IntegerColumn column,
				final Sequence.Type type,
				final long start,
				final ConnectionPool connectionPool,
				final Database database)
		{
			return column.kind.primaryKey()
				? new SequenceImplBatchedSequence(metrics, column, type, start, connectionPool, database)
				: new SequenceImplSequence       (metrics, column, type, start, connectionPool, database, NAME_SUFFIX)
			;
		}
	};

	private static final String NAME_SUFFIX = "Seq";

	final boolean persistent;

	PrimaryKeyGenerator(final boolean persistent)
	{
		this.persistent = persistent;
	}

	abstract SequenceImpl newSequenceImpl(
			ModelMetrics metrics,
			IntegerColumn column,
			Sequence.Type type,
			long start,
			ConnectionPool connectionPool,
			Database database);
}

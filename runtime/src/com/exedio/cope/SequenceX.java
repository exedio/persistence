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
import java.sql.Connection;

final class SequenceX
{
	private final Feature feature;
	private final Sequence.Type type;
	private final long start;
	private final SequenceCounter counter;

	private SequenceImpl impl;
	private volatile boolean knownToBeEmptyForTest = false;

	SequenceX(
			final Feature feature,
			final Sequence.Type type, final long start,
			final long minimum, final long maximum)
	{
		this.feature = feature;
		this.type = type;
		this.start = start;
		this.counter = new SequenceCounter(feature, start, minimum, maximum);
	}

	void onModelNameSet(final ModelMetrics metrics)
	{
		counter.onModelNameSet(metrics);
	}

	private ModelMetrics metrics(final ModelMetrics metrics)
	{
		return metrics.name(com.exedio.cope.Sequence.class).tag(feature);
	}

	void connectPrimaryKey(final Database database, final IntegerColumn column, final ModelMetrics metrics)
	{
		knownToBeEmptyForTest = false;

		if(impl!=null)
			throw new IllegalStateException("already connected " + feature);
		impl = database.newSequenceImpl(metrics(metrics), type, start, column);
	}

	void connectSequence(final Database database, final String name, final ModelMetrics metrics)
	{
		knownToBeEmptyForTest = false;

		if(impl!=null)
			throw new IllegalStateException("already connected " + feature);
		impl = database.newSequenceImplCluster(metrics(metrics), type, start, name);
	}

	void disconnect()
	{
		knownToBeEmptyForTest = false;

		if(impl==null)
			throw new IllegalStateException("not yet connected " + feature);
		impl = null;
	}

	private SequenceImpl impl()
	{
		final SequenceImpl impl = this.impl;
		if(impl==null)
			throw new Model.NotConnectedException(feature.getType().getModel());
		return impl;
	}

	void makeSchema(final Schema schema)
	{
		impl().makeSchema(schema);
	}

	long next()
	{
		knownToBeEmptyForTest = false;

		final long result = impl().next();
		counter.next(result);
		return result;
	}

	void delete(final StringBuilder bf, final Dialect dialect)
	{
		impl().delete(bf, dialect);
	}

	void flush()
	{
		impl().flush();
		counter.flush();
	}

	SequenceInfo getInfo()
	{
		return counter.getInfo();
	}

	SequenceBehindInfo check(final Model model, final IntegerColumn column)
	{
		model.transactions.assertNoCurrentTransaction();
		final ConnectionPool connectionPool = model.connect().connectionPool;
		final Long featureMaximum;
		final Connection connection = connectionPool.get(true);
		try
		{
			featureMaximum = column.max(connection, column.table.database.executor);
		}
		finally
		{
			connectionPool.put(connection);
		}

		return new SequenceBehindInfo(feature, featureMaximum, impl().getNext());
	}

	String getSchemaName()
	{
		return impl().getSchemaName();
	}

	boolean isKnownToBeEmptyForTest()
	{
		return knownToBeEmptyForTest;
	}

	void setKnownToBeEmptyForTest()
	{
		knownToBeEmptyForTest = true;
	}

	@Override
	public String toString()
	{
		return feature.toString();
	}
}

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
import java.sql.Connection;

final class SequenceX
{
	private final Feature feature;
	private final int start;
	private final SequenceCounter counter;

	private SequenceImpl impl;
	private IntegerColumn column = null;
	private volatile boolean knownToBeEmptyForTest = false;

	SequenceX(final Feature feature, final int start, final int minimum, final int maximum)
	{
		this.feature = feature;
		this.start = start;
		this.counter = new SequenceCounter(feature, start, minimum, maximum);
	}

	void connect(final Database database, final IntegerColumn column)
	{
		this.knownToBeEmptyForTest = false;

		if(impl!=null)
			throw new IllegalStateException("already connected " + feature);
		impl = database.newSequenceImpl(start, column);
		this.column = column;
	}

	void connectCluster(final Database database, final IntegerColumn column, final String name)
	{
		this.knownToBeEmptyForTest = false;

		if(impl!=null)
			throw new IllegalStateException("already connected " + feature);
		impl = database.newSequenceImplCluster(start, name);
		this.column = column;
	}

	void disconnect()
	{
		this.knownToBeEmptyForTest = false;

		if(impl==null)
			throw new IllegalStateException("not yet connected " + feature);
		impl = null;
		column = null;
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

	int next()
	{
		this.knownToBeEmptyForTest = false;

		final int result = impl().next();
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

	int check(final Model model)
	{
		model.transactions.assertNoCurrentTransaction();
		final ConnectionPool connectionPool = model.connect().connectionPool;
		final Integer maxO;
		final Connection connection = connectionPool.get(true);
		try
		{
			maxO = column.max(connection, column.table.database.executor);
			if(maxO==null)
				return 0;
		}
		finally
		{
			connectionPool.put(connection);
		}

		final int max = maxO.intValue();
		final int current = impl().getNext();
		//System.out.println("---" + impl().getClass().getSimpleName() + "----"+feature.getID()+": " + max + " / " + current);
		return (max<current) ? 0 : (max-current+1);
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
	public final String toString()
	{
		return feature.toString();
	}
}

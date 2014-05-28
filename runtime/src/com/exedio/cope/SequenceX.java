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
	private final int minimum;
	private final int maximum;

	private SequenceImpl impl;
	private IntegerColumn column = null;
	private volatile int count = 0;
	private volatile int first = Integer.MAX_VALUE;
	private volatile int last = Integer.MIN_VALUE;
	volatile boolean knownToBeEmptyForTest = false;

	SequenceX(final Feature feature, final int start, final int minimum, final int maximum)
	{
		if(feature==null)
			throw new NullPointerException();
		if(start<minimum || start>maximum)
			throw new IllegalArgumentException(String.valueOf(start) + '/' + String.valueOf(minimum) + '/' + String.valueOf(maximum));

		this.feature = feature;
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;
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
			throw new IllegalStateException("not yet connected " + feature);
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

		if(result<minimum || result>maximum)
			throw new IllegalStateException("sequence overflow to " + result + " in " + feature + " limited to " + minimum + ',' + maximum);
		if((count++)==0)
			first = result;
		last = result;

		return result;
	}

	void delete(final StringBuilder bf, final Dialect dialect)
	{
		if(impl==null)
			throw new Model.NotConnectedException(feature.getType().getModel());

		impl.delete(bf, dialect);
	}

	void flush()
	{
		impl().flush();
		count = 0;
		first = Integer.MAX_VALUE;
		last = Integer.MIN_VALUE;
	}

	SequenceInfo getInfo()
	{
		final int count = this.count;
		final int first = this.first;
		final int last  = this.last;
		return
			count!=0 && first!=Integer.MAX_VALUE && last!=Integer.MIN_VALUE
			? new SequenceInfo(feature, start, minimum, maximum, count, first, last)
			: new SequenceInfo(feature, start, minimum, maximum);
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
		if(impl==null)
			throw new Model.NotConnectedException(feature.getType().getModel());

		return impl.getSchemaName();
	}

	@Override
	public final String toString()
	{
		return feature.toString();
	}
}

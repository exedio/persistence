/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;

import com.exedio.cope.util.SequenceInfo;
import com.exedio.dsmf.Schema;

final class Sequence
{
	private final Feature feature;
	private final int start;
	private final int minimum;
	private final int maximum;
	
	private SequenceImpl impl;
	private volatile int count = 0;
	private volatile int first = Integer.MAX_VALUE;
	private volatile int last = Integer.MIN_VALUE;
	
	Sequence(final Feature feature, final int start, final int minimum, final int maximum)
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
		if(impl!=null)
			throw new IllegalStateException("already connected " + feature);
		impl =
			database.cluster
			? new SequenceImplSequence(column, start, database)
			: new SequenceImplMax(column, start);
	}
	
	void disconnect()
	{
		if(impl==null)
			throw new IllegalStateException("not yet connected " + feature);
		impl = null;
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

	int next(final Connection connection)
	{
		final int result = impl().next(connection);
		
		if(result<minimum || result>maximum)
			throw new RuntimeException("sequence overflow to " + result + " in " + feature);
		if((count++)==0)
			first = result;
		last = result;
		
		return result;
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
}

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

import com.exedio.cope.util.PrimaryKeyInfo;
import com.exedio.dsmf.Schema;

final class PkSource
{
	private final Type type;
	private final int start;
	private final int minimum;
	private final int maximum;
	private PkSourceImpl impl;
	private volatile int count = 0;
	private volatile int first = Integer.MAX_VALUE;
	private volatile int last = Integer.MIN_VALUE;
	
	PkSource(final Type type)
	{
		assert type!=null;
		this.type = type;
		this.start = PK.MIN_VALUE;
		this.minimum = PK.MIN_VALUE;
		this.maximum = PK.MAX_VALUE;
	}
	
	void connect(final Database database, final IntegerColumn column)
	{
		if(impl!=null)
			throw new IllegalStateException("already connected " + type);
		impl =
			database.cluster
			? new DefaultToNextSequenceImpl(start, database, column)
			: new DefaultToNextMaxImpl(column, start);
	}
	
	void disconnect()
	{
		if(impl==null)
			throw new IllegalStateException("not yet connected " + type);
		impl = null;
	}
	
	private PkSourceImpl impl()
	{
		final PkSourceImpl impl = this.impl;
		if(impl==null)
			throw new IllegalStateException("not yet connected " + type);
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
			throw new RuntimeException("sequence overflow to " + result + " in type " + type.id);
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

	PrimaryKeyInfo getInfo()
	{
		final int count = this.count;
		final int first = this.first;
		final int last  = this.last;
		return
			count!=0 && first!=Integer.MAX_VALUE && last!=Integer.MIN_VALUE
			? new PrimaryKeyInfo(type, minimum, maximum, count, first, last)
			: new PrimaryKeyInfo(type, minimum, maximum);
	}
}

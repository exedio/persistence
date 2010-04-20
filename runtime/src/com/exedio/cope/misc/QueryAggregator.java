/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Query;
import com.exedio.cope.Query.Result;

public final class QueryAggregator<R>
{
	private final static int UNLIMITED = -77;
	
	private final List<Query<R>> queries;
	private int offset = 0;
	private int limit = -1;
	
	public QueryAggregator(final List<Query<R>> queries)
	{
		this.queries = queries;
	}
	
	@SuppressWarnings("unchecked")
	public static <R> QueryAggregator<R> get(
			final Query<R> query1,
			final Query<R> query2)
	{
		return new QueryAggregator<R>(java.util.Arrays.asList(query1, query2));
	}
	
	@SuppressWarnings("unchecked")
	public static <R> QueryAggregator<R> get(
			final Query<R> query1,
			final Query<R> query2,
			final Query<R> query3)
	{
		return new QueryAggregator<R>(java.util.Arrays.asList(query1, query2, query3));
	}
	
	public List<Query<R>> getQueries()
	{
		return queries;
	}
	
	/**
	 * @see Query#getOffset()
	 */
	public int getOffset()
	{
		return offset;
	}
	
	/**
	 * @see Query#getLimit()
	 */
	public int getLimit()
	{
		return limit!=UNLIMITED ? limit : -1;
	}
	
	/**
	 * @see Query#setLimit(int,int)
	 */
	public void setLimit(final int offset, final int limit)
	{
		if(offset<0)
			throw new IllegalArgumentException("offset must not be negative, but was " + offset);
		if(limit<0)
			throw new IllegalArgumentException("limit must not be negative, but was " + limit);

		this.offset = offset;
		this.limit = limit;
	}
	
	/**
	 * @see Query#setLimit(int)
	 */
	public void setLimit(final int offset)
	{
		if(offset<0)
			throw new IllegalArgumentException("offset must not be negative, but was " + offset);

		this.offset = offset;
		this.limit = UNLIMITED;
	}

	/**
	 * @see Query#searchAndTotal()
	 */
	public Result<R> searchAndTotal()
	{
		List<R> data = null;
		int total = 0;
		
		final Iterator<Query<R>> i = queries.iterator();
		{
			Query<R> first = null;
			int totalBeforeFirst = 0;
			while(i.hasNext())
			{
				final Query<R> query = i.next();
				totalBeforeFirst = total;
				total += query.total();
				if(total>offset)
				{
					first = query;
					break;
				}
			}
			if(first==null)
				return result(Collections.<R>emptyList(), total);
			
			data = new ArrayList<R>(search(first, offset-totalBeforeFirst));
		}
		{
			final int totalBreak = (limit!=UNLIMITED) ? (offset+limit) : Integer.MAX_VALUE;
			Query<R> last = null;
			int totalBeforeLast = 0;
			while(i.hasNext())
			{
				final Query<R> query = i.next();
				totalBeforeLast = total;
				total += query.total();
				if(total>totalBreak)
				{
					last = query;
					break;
				}
				data.addAll(search(query, 0));
			}
			if(last==null)
				return result(unmodifiableList(data), total);
			
			assert limit!=UNLIMITED;
			
			final int nowLimit = limit+offset-totalBeforeLast;
			if(nowLimit>0)
			{
				last.setLimit(0, nowLimit);
				data.addAll(last.search());
			}
		}
		
		while(i.hasNext())
			total += i.next().total();
		
		return result(unmodifiableList(data), total);
	}
	
	private List<R> search(final Query<R> query, final int offset)
	{
		if(limit!=UNLIMITED)
			query.setLimit(offset, limit);
		else
			query.setLimit(offset);
		
		return query.search();
	}
	
	private Query.Result<R> result(final List<R> data, final int total)
	{
		return
			(limit!=UNLIMITED)
			? new Query.Result<R>(data, total, offset, getLimit())
			: new Query.Result<R>(data, total, offset);
	}
}

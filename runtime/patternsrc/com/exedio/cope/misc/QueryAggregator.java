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

package com.exedio.cope.misc;

import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Collections.unmodifiableList;

import com.exedio.cope.Query;
import com.exedio.cope.Query.Result;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class QueryAggregator<R>
{
	private static final int UNLIMITED = -77;

	private final List<Query<? extends R>> queries;
	private int pageOffset = 0;
	private int pageLimit = -1;

	private QueryAggregator(
			final List<Query<? extends R>> queries,
			@SuppressWarnings("unused") final double dummy)
	{
		this.queries = queries;
	}

	public QueryAggregator(final List<Query<? extends R>> queries)
	{
		this(new ArrayList<>(queries), 0.0);
	}

	public static <R> QueryAggregator<R> get(
			final Query<? extends R> query1,
			final Query<? extends R> query2)
	{
		return new QueryAggregator<>(java.util.Arrays.asList(query1, query2), 0.0);
	}

	public static <R> QueryAggregator<R> get(
			final Query<? extends R> query1,
			final Query<? extends R> query2,
			final Query<? extends R> query3)
	{
		return new QueryAggregator<>(java.util.Arrays.asList(query1, query2, query3), 0.0);
	}

	public List<Query<? extends R>> getQueries()
	{
		return unmodifiableList(queries);
	}

	/**
	 * @see Query#getPageOffset()
	 */
	public int getPageOffset()
	{
		return pageOffset;
	}

	/**
	 * @see Query#getPageLimitOrMinusOne()
	 */
	public int getPageLimitOrMinusOne()
	{
		return pageLimit!=UNLIMITED ? pageLimit : -1;
	}

	/**
	 * @see Query#setPage(int,int)
	 */
	public void setPage(final int offset, final int limit)
	{
		requireNonNegative(offset, "offset");
		requireNonNegative(limit, "limit");

		this.pageOffset = offset;
		this.pageLimit = limit;
	}

	/**
	 * @see Query#setPageUnlimited(int)
	 */
	public void setPageUnlimited(final int offset)
	{
		requireNonNegative(offset, "offset");

		this.pageOffset = offset;
		this.pageLimit = UNLIMITED;
	}

	/**
	 * @see Query#searchAndTotal()
	 */
	public Result<R> searchAndTotal()
	{
		for(final Query<?> q : queries)
			if(q.getPageOffset()!=0 || q.getPageLimitOrMinusOne()!=-1)
				throw new IllegalArgumentException("queries must not be limited, but was: " + q);

		final List<R> data;
		int total = 0;

		final Iterator<Query<? extends R>> i = queries.iterator();
		{
			Query<? extends R> first = null;
			int totalBeforeFirst = 0;
			while(i.hasNext())
			{
				final Query<? extends R> query = i.next();
				totalBeforeFirst = total;
				total += query.total();
				if(total>pageOffset)
				{
					first = query;
					break;
				}
			}
			if(first==null)
				return result(Collections.emptyList(), total);

			data = new ArrayList<>(search(first, pageOffset-totalBeforeFirst));
		}
		{
			final int totalBreak = (pageLimit!=UNLIMITED) ? (pageOffset+pageLimit) : Integer.MAX_VALUE;
			Query<? extends R> last = null;
			int totalBeforeLast = 0;
			while(i.hasNext())
			{
				final Query<? extends R> query = i.next();
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

			assert pageLimit!=UNLIMITED;

			final int nowLimit = pageLimit+pageOffset-totalBeforeLast;
			if(nowLimit>0)
			{
				last.setPage(0, nowLimit);
				data.addAll(last.search());
				last.setPageUnlimited(0);
			}
		}

		while(i.hasNext())
			total += i.next().total();

		return result(unmodifiableList(data), total);
	}

	private List<? extends R> search(final Query<? extends R> query, final int offset)
	{
		if(pageLimit!=UNLIMITED)
			query.setPage(offset, pageLimit);
		else
			query.setPageUnlimited(offset);

		final List<? extends R> result = query.search();
		query.setPageUnlimited(0);
		return result;
	}

	private Query.Result<R> result(final List<R> data, final int total)
	{
		return
			(pageLimit!=UNLIMITED)
			? new Query.Result<>(data, total, pageOffset, getPageLimitOrMinusOne())
			: new Query.Result<>(data, total, pageOffset);
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getPageOffset()} instead.
	 */
	@Deprecated
	public int getOffset()
	{
		return getPageOffset();
	}

	/**
	 * @deprecated Use {@link #getPageLimitOrMinusOne()} instead.
	 */
	@Deprecated
	public int getLimit()
	{
		return getPageLimitOrMinusOne();
	}

	/**
	 * @deprecated Use {@link #setPage(int, int)} instead.
	 */
	@Deprecated
	public void setLimit(final int offset, final int limit)
	{
		setPage(offset, limit);
	}

	/**
	 * @deprecated Use {@link #setPageUnlimited(int)} instead.
	 */
	@Deprecated
	public void setLimit(final int offset)
	{
		setPageUnlimited(offset);
	}
}

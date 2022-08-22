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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

import gnu.trove.TIntHashSet;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public final class Query<R> implements Serializable
{
	private static final long serialVersionUID = 1l;

	static final int UNLIMITED = -66;

	final Model model;
	private Selectable<? extends R> selectSingle;
	private Selectable<?>[] selectsMulti;
	private boolean distinct = false;
	final Type<?> type;
	private int joinIndex = 0;
	ArrayList<Join> joins = null;
	private Condition condition;

	// groupBy-arrays must never be modified, because they are reused by copy constructor
	private Function<?>[] groupBy;

	private Condition having;

	// orderBy-arrays must never be modified, because they are reused by copy constructor
	private Selectable<?>[] orderBy = null;
	private boolean[] orderAscending;

	private int pageOffset = 0;
	private int pageLimit = UNLIMITED;

	private static final int SEARCH_SIZE_LIMIT_DEFAULT = Integer.MIN_VALUE;
	private int searchSizeLimit = SEARCH_SIZE_LIMIT_DEFAULT;

	private static final int SEARCH_SIZE_CACHE_LIMIT_DEFAULT = Integer.MIN_VALUE;
	private int searchSizeCacheLimit = SEARCH_SIZE_CACHE_LIMIT_DEFAULT;

	public Query(final Selectable<? extends R> select)
	{
		this(select, (Condition)null);
	}

	public Query(final Selectable<? extends R> select, final Condition condition)
	{
		this.selectSingle = select;
		this.type = select.getType();
		this.model = this.type.getModel();
		this.condition = replaceTrue(condition);
	}

	/**
	 * Copy Constructor
	 */
	public Query(final Query<R> query)
	{
		this.model = query.model;
		this.selectSingle = query.selectSingle;
		this.selectsMulti = query.selectsMulti;
		this.distinct = query.distinct;
		this.type = query.type;
		this.joinIndex = query.joinIndex;
		this.joins = query.joins!=null ? new ArrayList<>(query.joins) : null;
		this.condition = query.condition;
		this.groupBy = query.groupBy;
		this.having = query.having;
		this.orderBy = query.orderBy;
		this.orderAscending = query.orderAscending;
		this.pageOffset = query.pageOffset;
		this.pageLimit = query.pageLimit;
		this.searchSizeLimit = query.searchSizeLimit;
		this.searchSizeCacheLimit = query.searchSizeCacheLimit;
	}

	/**
	 * Copy Constructor
	 */
	public Query(final Selectable<? extends R> select, final Query<?> query)
	{
		this.model = query.model;
		this.selectSingle = select;
		this.distinct = query.distinct;
		this.type = query.type;
		this.joinIndex = query.joinIndex;
		this.joins = query.joins!=null ? new ArrayList<>(query.joins) : null;
		this.condition = query.condition;
		this.groupBy = query.groupBy;
		this.having = query.having;
		this.orderBy = query.orderBy;
		this.orderAscending = query.orderAscending;
		this.pageOffset = query.pageOffset;
		this.pageLimit = query.pageLimit;
		this.searchSizeLimit = query.searchSizeLimit;
		this.searchSizeCacheLimit = query.searchSizeCacheLimit;
	}

	public Query(final Selectable<R> select, final Type<?> type, final Condition condition)
	{
		requireNonNull(select, "select");
		this.model = type.getModel();
		this.selectSingle = select;
		this.type = type;
		this.condition = replaceTrue(condition);
	}

	private Query(final Selectable<?>[] selects, final Type<?> type, final Condition condition)
	{
		this.model = type.getModel();
		this.selectsMulti = checkAndCopy(selects);
		this.type = type;
		this.condition = replaceTrue(condition);
	}

	public static Query<List<Object>> newQuery(final Selectable<?>[] selects, final Type<?> type, final Condition condition)
	{
		return new Query<>(selects, type, condition);
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	Selectable<?>[] selects()
	{
		if(selectSingle!=null)
			return new Selectable<?>[]{selectSingle};
		else
			return selectsMulti;
	}

	public Selectable<? extends R> getSelectSingle()
	{
		final Selectable<? extends R> result = selectSingle;
		if(result==null)
			throw new IllegalStateException("use getSelects instead");
		return result;
	}

	public List<Selectable<?>> getSelects()
	{
		if(selectSingle!=null)
			return Collections.singletonList(selectSingle);
		else
			return Collections.unmodifiableList(Arrays.asList(selectsMulti));
	}

	public void setSelect(final Selectable<? extends R> select)
	{
		if(selectSingle==null)
			throw new IllegalStateException("use setSelects instead");
		assert selectsMulti==null;
		this.selectSingle = select;
	}

	public void setSelects(final Selectable<?>... selects)
	{
		final Selectable<?>[] selectsCopy = checkAndCopy(selects);
		if(selectsMulti==null)
			throw new IllegalStateException("use setSelect instead");
		assert selectSingle==null;
		this.selectsMulti = selectsCopy;
	}

	private static Selectable<?>[] checkAndCopy(final Selectable<?>[] selects)
	{
		if(selects.length<2)
			throw new IllegalArgumentException("must have at least 2 selects, but was " + Arrays.asList(selects));
		for(int i = 0; i<selects.length; i++)
			if(selects[i]==null)
				throw new NullPointerException("selects" + '[' + i + ']');
		return com.exedio.cope.misc.Arrays.copyOf(selects);
	}

	public boolean isDistinct()
	{
		return distinct;
	}

	public void setDistinct(final boolean distinct)
	{
		this.distinct = distinct;
	}

	public Type<?> getType()
	{
		return type;
	}

	public void setCondition(final Condition condition)
	{
		this.condition = replaceTrue(condition);
	}

	public Condition getCondition()
	{
		return condition;
	}

	/**
	 * If there is already a condition set for this query,
	 * this is equivalent to
	 * {@code {@link #setCondition(Condition) setCondition}({@link #getCondition()}.{@link Condition#and(Condition) and}(narrowingCondition))}.
	 */
	public void narrow(final Condition narrowingCondition)
	{
		condition =
			condition!=null
			? condition.and(narrowingCondition)
			: replaceTrue(narrowingCondition);
	}

	private Join join(final Join join)
	{
		if(joins==null)
			joins = new ArrayList<>();

		joins.add(join);

		return join;
	}

	/**
	 * Does an inner join with the given type without any join condition.
	 */
	public Join join(final Type<?> type)
	{
		return join(new Join(joinIndex++, Join.Kind.INNER, type, null));
	}

	/**
	 * Does an inner join with the given type on the given join condition.
	 */
	public Join join(final Type<?> type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.INNER, type, condition));
	}

	public Join joinOuterLeft(final Type<?> type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.OUTER_LEFT, type, condition));
	}

	public Join joinOuterRight(final Type<?> type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.OUTER_RIGHT, type, condition));
	}

	public List<Join> getJoins()
	{
		return joins==null ? Collections.emptyList() : Collections.unmodifiableList(joins);
	}


	// groupBy

	/**
	 * @deprecated Use {@link #getGroupBys()} instead.
	 */
	@Deprecated
	public List<Selectable<?>> getGroupBy()
	{
		return
			groupBy==null
			? Collections.emptyList()
			: Collections.unmodifiableList(Arrays.asList(groupBy));
	}

	public List<? extends Function<?>> getGroupBys()
	{
		return
			groupBy==null
			? Collections.emptyList()
			: Collections.unmodifiableList(Arrays.asList(groupBy));
	}

	/**
	 * @deprecated
	 * Use {@link #setGroupBy(Function[])})} instead,
	 * any non-Function will cause this method to fail.
	 */
	@Deprecated
	public void setGroupBy(final Selectable<?>... groupBy)
	{
		setGroupBy(function(groupBy, "groupBy"));
	}

	public void setGroupBy(final Function<?>... groupBy)
	{
		requireNonNull(groupBy, "groupBy");
		if(groupBy.length==0)
		{
			this.groupBy = null;
			return;
		}
		for(int i = 0; i<groupBy.length; i++)
			if(groupBy[i]==null)
				throw new NullPointerException("groupBy[" + i + ']');

		this.groupBy = com.exedio.cope.misc.Arrays.copyOf( groupBy );
	}


	// having

	public void setHaving(final Condition having)
	{
		this.having = replaceTrue(having);
	}

	public Condition getHaving()
	{
		return having;
	}


	// orderBy

	public List<Selectable<?>> getOrderByFunctions()
	{
		return
			orderBy==null
			? Collections.emptyList()
			: Collections.unmodifiableList(Arrays.asList(orderBy));
	}

	public List<Boolean> getOrderByAscending()
	{
		if(orderAscending==null)
			return Collections.emptyList();

		final ArrayList<Boolean> result = new ArrayList<>(orderAscending.length);
		for(final boolean b : orderAscending)
			result.add(b);
		return Collections.unmodifiableList(result);
	}

	public void setOrderByThis(final boolean ascending)
	{
		this.orderBy = new Selectable<?>[]{type.thisFunction};
		this.orderAscending = new boolean[]{ascending};
	}

	public void setOrderBy(final Selectable<?> orderBy, final boolean ascending)
	{
		requireNonNull(orderBy, "orderBy");

		this.orderBy = new Selectable<?>[]{orderBy};
		this.orderAscending = new boolean[]{ascending};
	}

	public void setOrderByAndThis(final Selectable<?> orderBy, final boolean ascending)
	{
		requireNonNull(orderBy, "orderBy");

		this.orderBy = new Selectable<?>[]{orderBy, type.thisFunction};
		this.orderAscending = new boolean[]{ascending, true};
	}

	/**
	 * @throws IllegalArgumentException if {@code orderBy.length!=ascending.length}
	 */
	public void setOrderBy(final Selectable<?>[] orderBy, final boolean[] ascending)
	{
		requireNonNull(orderBy, "orderBy");
		requireNonNull(ascending, "ascending");

		if(orderBy.length!=ascending.length)
			throw new IllegalArgumentException(
					"orderBy and ascending must have same length, " +
					"but was " + orderBy.length +
					" and " + ascending.length);
		if(orderBy.length==0)
		{
			this.orderBy = null;
			this.orderAscending = null;
			return;
		}
		for(int i = 0; i<orderBy.length; i++)
			if(orderBy[i]==null)
				throw new NullPointerException("orderBy" + '[' + i + ']');

		this.orderBy = com.exedio.cope.misc.Arrays.copyOf(orderBy);
		this.orderAscending = com.exedio.cope.misc.Arrays.copyOf(ascending);
	}

	public void addOrderBy(final Selectable<?> orderBy)
	{
		addOrderBy(orderBy, true);
	}

	public void addOrderByDescending(final Selectable<?> orderBy)
	{
		addOrderBy(orderBy, false);
	}

	public void addOrderBy(final Selectable<?> orderBy, final boolean ascending)
	{
		requireNonNull(orderBy, "orderBy");

		if(this.orderBy==null)
			this.orderBy = new Selectable<?>[]{ orderBy };
		else
		{
			final int l = this.orderBy.length;
			final Selectable<?>[] result = new Selectable<?>[l+1];
			System.arraycopy(this.orderBy, 0, result, 0, l);
			result[l] = orderBy;
			this.orderBy = result;
		}

		if(this.orderAscending==null)
			this.orderAscending = new boolean[]{ ascending };
		else
		{
			final int l = this.orderAscending.length;
			final boolean[] result = new boolean[l+1];
			System.arraycopy(this.orderAscending, 0, result, 0, l);
			result[l] = ascending;
			this.orderAscending = result;
		}
	}

	public void resetOrderBy()
	{
		orderBy = null;
		orderAscending = null;
	}


	@Deprecated
	private static <E> Function<E> function(final Selectable<E> selectable, final String message)
	{
		if(selectable==null)
			return null;
		if(!(selectable instanceof Function))
			throw new IllegalArgumentException(message + " is no Function but " + selectable);

		return (Function<E>)selectable;
	}

	@Deprecated
	private static Function<?>[] function(final Selectable<?>[] selectable, final String message)
	{
		if(selectable==null)
			return null;

		final Function<?>[] result = new Function<?>[selectable.length];
		for(int i = 0; i<selectable.length; i++)
			result[i] = function(selectable[i], message + '[' + i + ']');
		return result;
	}

	// offset / limit

	public int getPageOffset()
	{
		return pageOffset;
	}

	public int getPageLimitOrMinusOne()
	{
		return pageLimit!=UNLIMITED ? pageLimit : -1;
	}

	/**
	 * @see #setPageUnlimited(int)
	 * @param limit the maximum number of items to be found.
	 *        For specifying offset but no limit use {@link #setPageUnlimited(int)} instead.
	 * @throws IllegalArgumentException if offset is a negative value
	 * @throws IllegalArgumentException if limit is a negative value
	 */
	public void setPage(final int offset, final int limit)
	{
		requireNonNegative(offset, "offset");
		requireNonNegative(limit, "limit");

		this.pageOffset = offset;
		this.pageLimit = limit;
	}

	/**
	 * @see #setPage(int, int)
	 * @throws IllegalArgumentException if offset is a negative value
	 */
	public void setPageUnlimited(final int offset)
	{
		requireNonNegative(offset, "offset");

		this.pageOffset = offset;
		this.pageLimit = UNLIMITED;
	}


	// searchSizeLimit

	/**
	 * @see #setSearchSizeLimit(int)
	 */
	public int getSearchSizeLimit()
	{
		return
			(searchSizeLimit==SEARCH_SIZE_LIMIT_DEFAULT)
			? model.getConnectProperties().getQuerySearchSizeLimit()
			: searchSizeLimit;
	}

	/**
	 * Sets the search size limit for this query.
	 * <p>
	 * Method {@link #search()} will fail with an {@link SearchSizeLimitExceededException}
	 * as soon as the size of the result set exceeds the search size limit.
	 * Methods {@link #total()} and {@link #exists()} are not affected by this limit.
	 * <p>
	 * Setting the search size limit does not guarantee,
	 * that {@link #search()} actually fails when exceeding the limit.
	 * But it is guaranteed, that it does not fail when not exceeding the limit.
	 * In particular, it may not fail, if the result is fetched from the query cache.
	 * <p>
	 * If search size limit is not set, it defaults to
	 * {@link ConnectProperties#getQuerySearchSizeLimit()}.
	 *
	 * @see #getSearchSizeLimit()
	 */
	public void setSearchSizeLimit(final int searchSizeLimit)
	{
		this.searchSizeLimit = requireGreaterZero(searchSizeLimit, "searchSizeLimit");
	}

	/**
	 * @see #setSearchSizeLimit(int)
	 */
	public static final class SearchSizeLimitExceededException extends IllegalStateException
	{
		private static final long serialVersionUID = 1l;

		SearchSizeLimitExceededException(final String message)
		{
			super(message);
		}
	}


	// searchSizeCacheLimit

	/**
	 * @see #setSearchSizeCacheLimit(int)
	 */
	public int getSearchSizeCacheLimit()
	{
		return
			(searchSizeCacheLimit==SEARCH_SIZE_CACHE_LIMIT_DEFAULT)
			? model.getConnectProperties().getQueryCacheSizeLimit()
			: searchSizeCacheLimit;
	}

	/**
	 * Sets the search size cache limit for this query.
	 * <p>
	 * Results of method {@link #search()} will not be considered for inclusion
	 * into query cache as soon as the size of the result set exceeds the
	 * search size cache limit.
	 * Methods {@link #total()} and {@link #exists()} are not affected by this limit.
	 * <p>
	 * Setting the search size cache limit does not guarantee,
	 * that {@link #search()} is not satisfied from the cache.
	 * <p>
	 * If search size cache limit is not set, it defaults to
	 * {@link ConnectProperties#getQueryCacheSizeLimit()}.
	 *
	 * @see #getSearchSizeCacheLimit()
	 * @see Transaction#setQueryCacheDisabled(boolean) 
	 */
	public void setSearchSizeCacheLimit(final int searchSizeCacheLimit)
	{
		this.searchSizeCacheLimit = requireGreaterZero(searchSizeCacheLimit, "searchSizeCacheLimit");
	}


	/**
	 * Searches for items matching this query.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an {@code UnsupportedOperationException}.
	 */
	public List<R> search()
	{
		final Transaction transaction = model.currentTransaction();

		if(pageLimit==0 || condition==Condition.FALSE)
		{
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("skipped search because " + (pageLimit==0 ? "limit==0" : "condition==false")));
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(castQL(transaction.search(this, Mode.SEARCH)));
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // TODO: Database#search does not support generics
	private List<R> castQL(final List o)
	{
		return o;
	}

	/**
	 * Counts the items matching this query.
	 * <p>
	 * Returns the
	 * {@link Collection#size() size} of what
	 * {@link #search()} would have returned for this query with
	 * {@link #setPageUnlimited(int)} reset set to {@code (0)}.
	 */
	public int total()
	{
		final Transaction transaction = model.currentTransaction();

		if(condition==Condition.FALSE)
		{
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("skipped search because condition==false"));
			return 0;
		}

		final ArrayList<Object> result =
			transaction.search(this, Mode.TOTAL);
		assert result.size()==1;
		return (Integer)result.iterator().next();
	}

	/**
	 * Checks the existence of items matching this query.
	 * <p>
	 * Returns whether or not any result matches what
	 * {@link #search()} would have returned for this query with
	 * {@link #setPageUnlimited(int)} reset set to {@code (0)}.
	 */
	public boolean exists()
	{
		final Transaction transaction = model.currentTransaction();

		if(condition==Condition.FALSE)
		{
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("skipped search because condition==false"));
			return false;
		}

		final ArrayList<Object> result =
				transaction.search(this, Mode.EXISTS);
		assert result.size()==1;
		return Integer.valueOf(1).equals(result.iterator().next());
	}

	TC check()
	{
		final TC tc = new TC(this);

		for(final Selectable<?> select : selects())
			Cope.check(select, tc, null);

		if(condition!=null)
			condition.check(tc);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.check(tc);
		}

		if(orderBy!=null)
			for(final Selectable<?> ob : orderBy)
				Cope.check(ob, tc, null);

		return tc;
	}

	HashSet<Table> getTables()
	{
		return check().getTables();
	}

	/**
	 * Searches for items matching this query.
	 * <p>
	 * Returns a {@link Result} containing the
	 * {@link Result#getData() data} and the
	 * {@link Result#getTotal() total}.
	 * The {@link Result#getData() data} is equal to
	 * what {@link #search()} would have returned for this query.
	 * The {@link Result#getTotal() total} is equal to what
	 * {@link #total()} would have returned for this query.
	 * <p>
	 * This method does it's best to avoid issuing two queries
	 * for searching and totaling.
	 */
	public Result<R> searchAndTotal()
	{
		return new Result<>(this);
	}

	enum Mode
	{
		SEARCH, TOTAL, EXISTS;

		boolean isSearch()
		{
			return this==SEARCH;
		}

		private boolean isTotal()
		{
			return this==TOTAL;
		}

		private boolean isExists()
		{
			return this==EXISTS;
		}
	}

	public static final class Result<R>
	{
		final List<R> data;
		final int total;
		final int offset;
		final int limit;

		Result(final Query<R> query)
		{
			this.data = query.search();
			final int dataSize = data.size();
			this.offset = query.getPageOffset();
			this.limit = query.getPageLimitOrMinusOne();

			this.total =
					(((dataSize>0) || (offset==0))  &&  ((dataSize<limit) || (limit==-1)))
					? (offset+dataSize)
					: query.total();
		}

		public Result(
				final List<R> data,
				final int total,
				final int offset,
				final int limit)
		{
			this.data   = requireNonNull(data, "data");
			this.total  = requireNonNegative(total,  "total");
			this.offset = requireNonNegative(offset, "offset");
			this.limit  = requireNonNegative(limit,  "limit");
		}

		public Result(
				final List<R> data,
				final int total,
				final int offset)
		{
			this.data   = requireNonNull(data, "data");
			this.total  = requireNonNegative(total,  "total");
			this.offset = requireNonNegative(offset, "offset");
			this.limit  = -1;
		}

		/**
		 * @return
		 * the result of {@link Query#search()}
		 * evaluated within the execution of {@link Query#searchAndTotal()}
		 * that created this {@code Result}.
		 */
		@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // data is unmodifiable
		public List<R> getData()
		{
			return data;
		}

		/**
		 * @return
		 * the result of {@link Query#total()}
		 * evaluated within the execution of {@link Query#searchAndTotal()}
		 * that created this {@code Result}.
		 */
		public int getTotal()
		{
			return total;
		}

		/**
		 * @return
		 * the result of {@link Query#getPageOffset()}
		 * evaluated within the execution of {@link Query#searchAndTotal()}
		 * that created this {@code Result}.
		 */
		public int getPageOffset()
		{
			return offset;
		}

		/**
		 * @return
		 * the result of {@link Query#getPageLimitOrMinusOne()}
		 * evaluated within the execution of {@link Query#searchAndTotal()}
		 * that created this {@code Result}.
		 */
		public int getPageLimitOrMinusOne()
		{
			return limit;
		}

		@Override
		public boolean equals(final Object other)
		{
			if(!(other instanceof Result<?>))
				return false;

			final Result<?> o = (Result<?>)other;

			return total==o.total && offset==o.offset && limit==o.limit && data.equals(o.data);
		}

		@Override
		public int hashCode()
		{
			return total ^ (offset<<8) ^ (limit<<16) ^ data.hashCode();
		}

		@Override
		public String toString()
		{
			return data.toString() + '(' + total + ')';
		}

		@SuppressWarnings("unchecked") // OK: for singleton property
		public static <R> Result<R> empty()
		{
			return EMPTY;
		}

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: for singleton property
		private static final Result EMPTY = new Result(Collections.emptyList(), 0, 0);

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
	}

	/**
	 * Searches equivalently to {@link #search()},
	 * but assumes that the search result has at most one element.
	 * <p>
	 * Returns null, if the search result is {@link Collection#isEmpty() empty},
	 * returns the only element of the search result,
	 * if the result {@link Collection#size() size} is exactly one.
	 * @throws IllegalArgumentException if the search result size is greater than one.
	 * @see #searchSingletonStrict()
	 * @see Type#searchSingleton(Condition)
	 */
	public R searchSingleton()
	{
		// this is the most efficient implementation for
		// array-backed lists returned by #search()
		final List<R> resultList = search();
		switch(resultList.size())
		{
			case 0:
				return null;
			case 1:
				return resultList.get(0);
			default:
				throw new IllegalArgumentException(
						"expected result of size one or less, " +
						"but was " + resultList +
						" for query: " + this);
		}
	}

	/**
	 * Searches equivalently to {@link #search()},
	 * but assumes that the search result has exactly one element.
	 * <p>
	 * Returns the only element of the search result,
	 * if the result {@link Collection#size() size} is exactly one.
	 * @throws IllegalArgumentException if the search result size is not exactly one.
	 * @see #searchSingleton()
	 * @see Type#searchSingletonStrict(Condition)
	 */
	public R searchSingletonStrict()
	{
		// this is the most efficient implementation for
		// array-backed lists returned by #search()
		final List<R> resultList = search();
		switch(resultList.size())
		{
			case 0:
				throw new IllegalArgumentException(
						"expected result of size one, " +
						"but was empty for query: " + this);
			case 1:
				return resultList.get(0);
			default:
				throw new IllegalArgumentException(
						"expected result of size one, " +
						"but was " + resultList +
						" for query: " + this);
		}
	}

	@Override
	public String toString()
	{
		return toString(false, Mode.SEARCH);
	}

	/**
	 * BEWARE:
	 * The results of this method also determinates,
	 * whether to queries are equal for a hit in the query cache.
	 * Do not forget anything !!!
	 */
	String toString(final boolean key, final Mode mode)
	{
		final Type<?> type = this.type;
		final StringBuilder bf = new StringBuilder();

		bf.append("select ");

		if(mode.isExists())
			bf.append("exists( select ");

		if(distinct && !mode.isExists())
			bf.append("distinct ");

		if(!mode.isSearch())
		{
			bf.append("count(*)");
		}
		else
		{
			final Selectable<?>[] selects = selects();
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');

				selects[i].toString(bf, type);
			}
		}

		bf.append(" from ").
			append(type);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.toString(bf, key, type);
		}

		if(condition!=null)
		{
			bf.append(" where ");
			condition.toString(bf, key, type);
		}

		if(groupBy!=null)
		{
			bf.append(" group by ");
			for(int i = 0; i<groupBy.length; i++)
			{
				if(i>0)
					bf.append(',');

				groupBy[i].toString(bf, type);
			}
		}

		if(having!=null)
		{
			bf.append(" having ");
			having.toString(bf, key, type);
		}

		if(mode.isSearch())
		{
			if(orderBy!=null)
			{
				bf.append(" order by ");
				for(int i = 0; i<orderBy.length; i++)
				{
					if(i>0)
						bf.append(", ");

					orderBy[i].toString(bf, type);
					if(!orderAscending[i])
						bf.append(" desc");
				}
			}

			if(pageOffset>0)
				bf.append(" offset '").
					append(pageOffset).
					append('\'');

			if(pageLimit!=UNLIMITED)
				bf.append(" limit '").
					append(pageLimit).
					append('\'');
		}

		if(mode.isExists())
			bf.append(" )");

		return bf.toString();
	}

	@SuppressWarnings("RedundantCast")
	ArrayList<Object> searchUncached(final Transaction transaction, final Mode mode)
	{
		return search(
				transaction.getConnection(),
				transaction.connect.executor,
				mode,
				(StringBuilder)null,
				transaction.queryInfos);
	}

	private static Condition replaceTrue(final Condition c)
	{
		return c==Condition.TRUE ? null : c;
	}

	ArrayList<Object> search(
			final Connection connection,
			final Executor executor,
			final Mode mode,
			final StringBuilder sqlOnlyBuffer,
			final ArrayList<QueryInfo> queryInfos)
	{
		executor.testListener().search(connection, this, mode);

		final Dialect dialect = executor.dialect;
		final boolean distinct = this.distinct;

		final ArrayList<Join> joins = this.joins;
		final Statement bf = executor.newStatement(this, sqlOnlyBuffer!=null);

		if(mode.isExists())
		{
			bf.append(dialect.getExistsPrefix());
		}

		final boolean countSubSelect = mode.isTotal() && (distinct || groupBy!=null);
		if (countSubSelect)
		{
			bf.append("SELECT COUNT(*) FROM ( ");
		}

		bf.append("SELECT ");

		final Selectable<?>[] selects = selects();
		final Marshaller<?>[] selectMarshallers;

		final boolean anyValuePossibleInOrderBy =
				mode.isSearch() &&
				orderBy!=null &&
				distinct &&
				selects.length==1 &&
				selects[0] instanceof This && // TODO could be broader
				selects[0].getType()==type;

		if(!countSubSelect && mode.isTotal())
		{
			bf.append("COUNT(*)");
			selectMarshallers = null;
		}
		else
		{
			if(distinct && !mode.isExists())
				bf.append("DISTINCT ");

			selectMarshallers = new Marshaller<?>[selects.length];
			final Marshallers marshallers = model.connect().marshallers;
			int copeTotalDistinctCount = 0;
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');

				bf.appendSelect(selects[i], null);
				if(mode.isTotal() && distinct && (selects.length>1) && dialect.subqueryRequiresAliasInSelect())
					bf.append(" as cope_total_distinct" + (copeTotalDistinctCount++));
				selectMarshallers[i] = marshallers.get(selects[i]);
			}
			// The following content of the SELECT clause is just for satisfying databases:
			// for SELECT DISTINCT, ORDER BY expressions must appear in select list
			if(anyValuePossibleInOrderBy && !dialect.supportsAnyValue())
			{
				for(final Selectable<?> selectable : orderBy)
				{
					if(isOrderByDependent(selectable))
					{
						bf.append(',');
						bf.appendSelect(selectable, null);
					}
				}
			}
		}

		bf.append(" FROM ").
			appendTypeDefinition(null, type, joins!=null);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.search(bf);
		}

		if(condition!=null)
		{
			bf.append(" WHERE ");
			condition.append(bf);
		}

		if (groupBy!=null)
		{
			for ( int i=0; i<groupBy.length; i++ )
			{
				if(i==0)
					bf.append(" GROUP BY ");
				else
					bf.append(',');

				bf.appendSelect(groupBy[i], null);
			}
		}

		if(having!=null)
		{
			bf.append(" HAVING ");
			having.append(bf);
		}

		if(mode.isSearch())
		{
			final Selectable<?>[] orderBy = this.orderBy;

			if(orderBy!=null)
			{
				final boolean anyValuePossible =
						anyValuePossibleInOrderBy &&
						dialect.supportsAnyValue();

				final boolean[] orderAscending = this.orderAscending;
				for(int i = 0; i<orderBy.length; i++)
				{
					if(i==0)
						bf.append(" ORDER BY ");
					else
						bf.append(',');

					final boolean anyValue =
							anyValuePossible &&
							isOrderByDependent(orderBy[i]);
					if(anyValue)
						bf.append("ANY_VALUE(");

					bf.append(orderBy[i]);

					if(anyValue)
						bf.append(')');

					if(!orderAscending[i])
						bf.append(" DESC");
					dialect.appendOrderByPostfix(bf, orderAscending[i]);

					// TODO break here, if already ordered by some unique function
				}
			}

			final int pageOffset = this.pageOffset;
			final int pageLimit  = this.pageLimit;
			if( pageOffset>0 || pageLimit!=UNLIMITED )
				dialect.appendPageClauseAfter(bf, pageOffset, pageLimit);
		}

		if(countSubSelect)
		{
			bf.append(" )");
			if (dialect.subqueryRequiresAlias())
			{
				bf.append(" AS cope_total_distinct");
			}
		}

		if(mode.isExists())
		{
			bf.append(dialect.getExistsPostfix());
			if(dialect.subqueryRequiresAlias())
			{
				bf.append(" AS cope_exists");
			}
		}

		QueryInfo queryInfo = null;
		if(queryInfos!=null)
			queryInfos.add(queryInfo = new QueryInfo(toString()));

		if(sqlOnlyBuffer!=null)
		{
			assert bf.getParameters()==null;
			sqlOnlyBuffer.append(bf.getText());
			return null;
		}

		//System.out.println(bf.toString());

		final ArrayList<Object> result = new ArrayList<>();
		final int sizeLimit = getSearchSizeLimit();
		executor.query(connection, bf, queryInfo, false, resultSet ->
			{
				if(!mode.isSearch())
				{
					resultSet.next();
					result.add(resultSet.getInt(1));
					if(resultSet.next())
						throw new RuntimeException("Only one total result set expected: " + bf);
					return null;
				}

				int sizeLimitCountDown = sizeLimit;
				while(resultSet.next())
				{
					if((--sizeLimitCountDown)<0)
						throw new SearchSizeLimitExceededException("exceeded hard limit of " + sizeLimit + ": " + this);

					int columnIndex = 1;
					final Object[] resultRow = (selects.length > 1) ? new Object[selects.length] : null;

					for(int selectIndex = 0; selectIndex<selects.length; selectIndex++)
					{
						//noinspection ConstantConditions OK: selectMarshallers cannot be null when mode.isSearch()==true
						final Object resultCell =
							selectMarshallers[selectIndex].unmarshal(resultSet, columnIndex);
						columnIndex += selectMarshallers[selectIndex].columns;
						if(resultRow!=null)
							resultRow[selectIndex] = resultCell;
						else
							result.add(resultCell);
					}
					if(resultRow!=null)
						result.add(Collections.unmodifiableList(Arrays.asList(resultRow)));
				}

				return null;
			}
		);

		return result;
	}

	private boolean isOrderByDependent(final Selectable<?> selectable)
	{
		return
				selectable instanceof FunctionField && // TODO could be broader, same join as select
				selectable.getType()==type;
	}


	int[] getTypeCacheIds()
	{
		final TIntHashSet queryTypeSet = new TIntHashSet();
		putType(queryTypeSet, type);
		if(joins!=null)
			for(final Join join : joins)
				putType(queryTypeSet, join.type);
		return queryTypeSet.toArray();
	}

	private static void putType(final TIntHashSet types, final Type<?> type)
	{
		for(Type<?> t = type; t!=null; t = t.supertype)
			types.add(t.cacheIdTransiently);
	}


	// ------------------- binary compatibility -------------------

	public void setOrderBy(final Function<?> orderBy, final boolean ascending)
	{
		setOrderBy((Selectable<?>)orderBy, ascending);
	}

	public void setOrderByAndThis(final Function<?> orderBy, final boolean ascending)
	{
		setOrderByAndThis((Selectable<?>)orderBy, ascending);
	}

	public void setOrderBy(final Function<?>[] orderBy, final boolean[] ascending)
	{
		setOrderBy((Selectable<?>[])orderBy, ascending);
	}

	public void addOrderBy(final Function<?> orderBy)
	{
		addOrderBy((Selectable<?>)orderBy);
	}

	public void addOrderByDescending(final Function<?> orderBy)
	{
		addOrderByDescending((Selectable<?>)orderBy);
	}

	public void addOrderBy(final Function<?> orderBy, final boolean ascending)
	{
		addOrderBy((Selectable<?>)orderBy, ascending);
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

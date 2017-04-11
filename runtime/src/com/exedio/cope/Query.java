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

import static com.exedio.cope.misc.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

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
	private Selectable<?>[] groupBy;

	private Condition having;

	// orderBy-arrays must never be modified, because they are reused by copy constructor
	private Selectable<?>[] orderBy = null;
	private boolean[] orderAscending;

	private int offset = 0;
	private int limit = UNLIMITED;

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
		this.offset = query.offset;
		this.limit = query.limit;
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
		this.offset = query.offset;
		this.limit = query.limit;
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

	/**
	 * @deprecated Use {@link #newQuery(Selectable[], Type, Condition)} instead
	 */
	@Deprecated
	public Query(final Selectable<?>[] selects, final Type<?> type, final Condition condition)
	{
		this.model = type.getModel();
		this.selectsMulti = checkAndCopy(selects);
		this.type = type;
		this.condition = replaceTrue(condition);
	}

	@SuppressWarnings("deprecation") // OK: is a constructor wrapper
	public static Query<List<Object>> newQuery(final Selectable<?>[] selects, final Type<?> type, final Condition condition)
	{
		return new Query<>(selects, type, condition);
	}

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
			throw new IllegalStateException("use getSelectMulti instead"); // TODO implement getSelectMulti
		return result;
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
		return this.condition;
	}

	/**
	 * If there is already a condition set for this query,
	 * this is equivalent to
	 * <tt>{@link #setCondition(Condition) setCondition}({@link #getCondition()}.{@link Condition#and(Condition) and}(narrowingCondition))</tt>.
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
		return joins==null ? Collections.<Join>emptyList() : Collections.unmodifiableList(joins);
	}


	// groupBy

	public List<Selectable<?>> getGroupBy()
	{
		return
			groupBy==null
			? Collections.<Selectable<?>>emptyList()
			: Collections.unmodifiableList(Arrays.asList(groupBy));
	}

	public void setGroupBy(final Selectable<?>... groupBy)
	{
		this.groupBy = com.exedio.cope.misc.Arrays.copyOf( groupBy );
	}


	// having

	public void setHaving(final Condition having)
	{
		this.having = replaceTrue(having);
	}

	public Condition getHaving()
	{
		return this.having;
	}


	// orderBy

	public List<Selectable<?>> getOrderByFunctions()
	{
		return
			orderBy==null
			? Collections.<Selectable<?>>emptyList()
			: Collections.unmodifiableList(Arrays.asList(orderBy));
	}

	public List<Boolean> getOrderByAscending()
	{
		if(orderAscending==null)
			return Collections.<Boolean>emptyList();

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
	 * @throws IllegalArgumentException if <tt>orderBy.length!=ascending.length</tt>
	 */
	public void setOrderBy(final Selectable<?>[] orderBy, final boolean[] ascending)
	{
		if(orderBy.length!=ascending.length)
			throw new IllegalArgumentException(
					"orderBy and ascending must have same length, " +
					"but was " + orderBy.length +
					" and " + ascending.length);
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


	// offset / limit

	public int getOffset()
	{
		return offset;
	}

	public int getLimit()
	{
		return limit!=UNLIMITED ? limit : -1;
	}

	/**
	 * @see #setLimit(int)
	 * @param limit the maximum number of items to be found.
	 *        For specifying no limit use {@link #setLimit(int)} instead.
	 * @throws IllegalArgumentException if offset is a negative value
	 * @throws IllegalArgumentException if limit is a negative value
	 */
	public void setLimit(final int offset, final int limit)
	{
		requireNonNegative(offset, "offset");
		requireNonNegative(limit, "limit");

		this.offset = offset;
		this.limit = limit;
	}

	/**
	 * @see #setLimit(int, int)
	 * @throws IllegalArgumentException if offset is a negative value
	 */
	public void setLimit(final int offset)
	{
		requireNonNegative(offset, "offset");

		this.offset = offset;
		this.limit = UNLIMITED;
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
	 * Method {@link #search()} will fail with an {@link IllegalStateException}
	 * as soon as the size of the result set exceeds the search size limit.
	 * Method {@link #total()} is not affected by this limit.
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
		if(searchSizeLimit<1)
			throw new IllegalArgumentException(
					"searchSizeLimit must be greater zero, but was " + searchSizeLimit);

		this.searchSizeLimit = searchSizeLimit;
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
	 * Method {@link #total()} is not affected by this limit.
	 * <p>
	 * Setting the search size cache limit does not guarantee,
	 * that {@link #search()} is not satisfied from the cache.
	 * <p>
	 * If search size cache limit is not set, it defaults to
	 * {@link ConnectProperties#getQueryCacheSizeLimit()}.
	 *
	 * @see #getSearchSizeCacheLimit()
	 */
	public void setSearchSizeCacheLimit(final int searchSizeCacheLimit)
	{
		if(searchSizeCacheLimit<1)
			throw new IllegalArgumentException(
					"searchSizeCacheLimit must be greater zero, but was " + searchSizeCacheLimit);

		this.searchSizeCacheLimit = searchSizeCacheLimit;
	}


	/**
	 * Searches for items matching this query.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <tt>UnsupportedOperationException</tt>.
	 */
	public List<R> search()
	{
		final Transaction transaction = model.currentTransaction();

		if(limit==0 || condition==Condition.FALSE)
		{
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("skipped search because " + (limit==0 ? "limit==0" : "condition==false")));
			return Collections.<R>emptyList();
		}

		return Collections.unmodifiableList(castQL(transaction.search(this, false)));
	}

	@SuppressWarnings({"unchecked", "rawtypes", "static-method"}) // TODO: Database#search does not support generics
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
	 * {@link #setLimit(int)} reset set to <tt>(0)</tt>.
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
			transaction.search(this, true);
		assert result.size()==1;
		return ((Integer)result.iterator().next()).intValue();
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
			this.offset = query.getOffset();
			this.limit = query.getLimit();

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

		public List<R> getData()
		{
			return data;
		}

		public int getTotal()
		{
			return total;
		}

		public int getOffset()
		{
			return offset;
		}

		public int getLimit()
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
		 * @deprecated Use {@link #getTotal()} instead
		 */
		@Deprecated
		public int getCountWithoutLimit()
		{
			return getTotal();
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
						" for query: " + toString());
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
						"but was empty for query: " + toString());
			case 1:
				return resultList.get(0);
			default:
				throw new IllegalArgumentException(
						"expected result of size one, " +
						"but was " + resultList +
						" for query: " + toString());
		}
	}

	@Override
	public String toString()
	{
		return toString(false, false);
	}

	/**
	 * BEWARE:
	 * The results of this method also determinates,
	 * whether to queries are equal for a hit in the query cache.
	 * Do not forget anything !!!
	 */
	String toString(final boolean key, final boolean totalOnly)
	{
		final Type<?> type = this.type;
		final StringBuilder bf = new StringBuilder();

		bf.append("select ");

		if(distinct)
			bf.append("distinct ");

		if(totalOnly)
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

		if(!totalOnly)
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

			if(offset>0)
				bf.append(" offset '").
					append(offset).
					append('\'');

			if(limit!=UNLIMITED)
				bf.append(" limit '").
					append(limit).
					append('\'');
		}

		return bf.toString();
	}

	ArrayList<Object> searchUncached(final Transaction transaction, final boolean totalOnly)
	{
		return search(
				transaction.getConnection(),
				transaction.connect.executor,
				totalOnly,
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
			final boolean totalOnly,
			final StringBuilder sqlOnlyBuffer,
			final ArrayList<QueryInfo> queryInfos)
	{
		executor.testListener().search(connection, this, totalOnly);

		final Dialect dialect = executor.dialect;
		final Dialect.LimitSupport limitSupport = executor.limitSupport;
		final int offset = this.offset;
		final int limit = this.limit;
		final boolean limitActive = offset>0 || limit!=Query.UNLIMITED;
		final boolean distinct = this.distinct;
		if(offset<0)
			throw new RuntimeException();

		final ArrayList<Join> joins = this.joins;
		final Statement bf = executor.newStatement(this, sqlOnlyBuffer!=null);

		final boolean countSubSelect = totalOnly && (distinct || groupBy!=null);
		if (countSubSelect)
		{
			bf.append("SELECT COUNT(*) FROM ( ");
		}

		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause(bf, offset, limit);

		bf.append("SELECT ");

		final Selectable<?>[] selects = this.selects();
		final Marshaller<?>[] selectMarshallers;

		if(!countSubSelect&&totalOnly)
		{
			bf.append("COUNT(*)");
			selectMarshallers = null;
		}
		else
		{
			if(distinct)
				bf.append("DISTINCT ");

			selectMarshallers = new Marshaller<?>[selects.length];
			final Marshallers marshallers = model.connect().marshallers;
			int copeTotalDistinctCount = 0;
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');

				bf.appendSelect(selects[i], null);
				if(totalOnly && distinct && (selects.length>1) && dialect.subqueryRequiresAliasInSelect())
					bf.append(" as cope_total_distinct" + (copeTotalDistinctCount++));
				selectMarshallers[i] = marshallers.get(selects[i]);
			}
		}

		bf.append(" FROM ").
			appendTypeDefinition((Join)null, this.type, joins!=null);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.search(bf);
		}

		if(this.condition!=null)
		{
			bf.append(" WHERE ");
			this.condition.append(bf);
		}

		if (this.groupBy!=null)
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

		if(!totalOnly)
		{
			final Selectable<?>[] orderBy = this.orderBy;

			if(orderBy!=null)
			{
				final boolean anyValuePossible =
						distinct &&
						selects.length==1 &&
						selects[0] instanceof This && // TODO could be broader
						selects[0].getType()==type &&
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
							orderBy[i] instanceof FunctionField && // TODO could be broader, same join as select
							orderBy[i].getType()==type;
					if(anyValue)
						bf.append("ANY_VALUE(");

					bf.append(orderBy[i], (Join)null);

					if(anyValue)
						bf.append(')');

					if(!orderAscending[i])
						bf.append(" DESC");
					dialect.appendOrderByPostfix(bf, orderAscending[i]);

					// TODO break here, if already ordered by some unique function
				}
			}

			if(limitActive)
			{
				switch(limitSupport)
				{
					case CLAUSE_AFTER_WHERE: dialect.appendLimitClause (bf, offset, limit); break;
					case CLAUSES_AROUND:     dialect.appendLimitClause2(bf, offset, limit); break;
					default:
						throw new RuntimeException(limitSupport.name());
				}
			}
		}

		final ArrayList<Object> result = new ArrayList<>();

		if(countSubSelect)
		{
			bf.append(" )");
			if (dialect.subqueryRequiresAlias())
			{
				bf.append(" AS cope_total_distinct");
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

		final int sizeLimit = this.getSearchSizeLimit();
		executor.query(connection, bf, queryInfo, false, resultSet ->
			{
				if(totalOnly)
				{
					resultSet.next();
					result.add(resultSet.getInt(1));
					if(resultSet.next())
						throw new RuntimeException("Only one total result set expected: " + bf.toString());
					return null;
				}

				int sizeLimitCountDown = sizeLimit;
				while(resultSet.next())
				{
					if((--sizeLimitCountDown)<0)
						throw new IllegalStateException("exceeded hard limit of " + sizeLimit + ": " + Query.this.toString());

					int columnIndex = 1;
					final Object[] resultRow = (selects.length > 1) ? new Object[selects.length] : null;

					for(int selectIndex = 0; selectIndex<selects.length; selectIndex++)
					{
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
	 * @deprecated Use {@link #searchAndTotal()} instead
	 */
	@Deprecated
	public Result<R> searchAndCountWithoutLimit()
	{
		return searchAndTotal();
	}

	/**
	 * @deprecated Use {@link #total()} instead
	 */
	@Deprecated
	public int countWithoutLimit()
	{
		return total();
	}

	/**
	 * @deprecated renamed to {@link #searchSingleton()}.
	 */
	@Deprecated
	public R searchUnique()
	{
		return searchSingleton();
	}

	/**
	 * @deprecated Use {@link Result#empty()} instead
	 */
	@Deprecated
	public static <R> Result<R> emptyResult()
	{
		return Result.empty();
	}
}

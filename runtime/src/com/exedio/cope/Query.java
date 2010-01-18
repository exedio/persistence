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

package com.exedio.cope;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.Executor.ResultSetHandler;

public final class Query<R>
{
	final static int UNLIMITED = -66;
	
	final Model model;
	private Selectable[] selects;
	private boolean distinct = false;
	final Type<?> type;
	private int joinIndex = 0;
	ArrayList<Join> joins = null;
	private Condition condition;

	private Function[] orderBy = null;
	private boolean[] orderAscending;
	
	private int offset = 0;
	private int limit = UNLIMITED;
	
	public Query(final Selectable<? extends R> select)
	{
		this(select, (Condition)null);
	}
	
	public Query(final Selectable<? extends R> select, final Condition condition)
	{
		this.selects = new Selectable[]{select};
		this.type = select.getType();
		this.model = this.type.getModel();
		this.condition = replaceTrue(condition);
	}
	
	public Query(final Selectable<R> select, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = new Selectable[]{select};
		this.type = type;
		this.condition = replaceTrue(condition);
	}
	
	public Query(final Selectable[] selects, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = selects;
		this.type = type;
		this.condition = replaceTrue(condition);
	}
	
	public void setSelects(final Selectable... selects)
	{
		check(selects);
		this.selects = selects;
	}
	
	private static final void check(final Selectable[] selects)
	{
		if(selects.length==0)
			throw new IllegalArgumentException("must not be empty");
		for(int i = 0; i<selects.length; i++)
			if(selects[i]==null)
				throw new NullPointerException("selects" + '[' + i + ']');
	}
	
	public boolean isDistinct()
	{
		return distinct;
	}
	
	public void setDistinct(final boolean distinct)
	{
		this.distinct = distinct;
	}
	
	public Type getType()
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
			: narrowingCondition;
	}
	
	private Join join(final Join join)
	{
		if(joins==null)
			joins = new ArrayList<Join>();
		
		joins.add(join);

		return join;
	}
	
	/**
	 * Does an inner join with the given type without any join condition.
	 */
	public Join join(final Type type)
	{
		return join(new Join(joinIndex++, Join.Kind.INNER, type, null));
	}
	
	/**
	 * Does an inner join with the given type on the given join condition.
	 */
	public Join join(final Type type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.INNER, type, condition));
	}
	
	public Join joinOuterLeft(final Type type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.OUTER_LEFT, type, condition));
	}
	
	public Join joinOuterRight(final Type type, final Condition condition)
	{
		return join(new Join(joinIndex++, Join.Kind.OUTER_RIGHT, type, condition));
	}
	
	public List<Join> getJoins()
	{
		return joins==null ? Collections.<Join>emptyList() : Collections.unmodifiableList(joins);
	}
	
	public List<Function> getOrderByFunctions()
	{
		return
			orderBy==null
			? Collections.<Function>emptyList()
			: Collections.unmodifiableList(Arrays.asList(orderBy));
	}
	
	public List<Boolean> getOrderByAscending()
	{
		if(orderAscending==null)
			return Collections.<Boolean>emptyList();
		
		final ArrayList<Boolean> result = new ArrayList<Boolean>(orderAscending.length);
		for(int i = 0; i<orderAscending.length; i++)
			result.add(orderAscending[i]);
		return Collections.unmodifiableList(result);
	}
	
	public void setOrderByThis(final boolean ascending)
	{
		this.orderBy = new Function[]{type.thisFunction};
		this.orderAscending = new boolean[]{ascending};
	}
	
	public void setOrderBy(final Function orderBy, final boolean ascending)
	{
		if(orderBy==null)
			throw new NullPointerException("orderBy");
		
		this.orderBy = new Function[]{orderBy};
		this.orderAscending = new boolean[]{ascending};
	}
	
	public void setOrderByAndThis(final Function orderBy, final boolean ascending)
	{
		if(orderBy==null)
			throw new NullPointerException("orderBy");
		
		this.orderBy = new Function[]{orderBy, type.thisFunction};
		this.orderAscending = new boolean[]{ascending, true};
	}
	
	/**
	 * @throws IllegalArgumentException if <tt>orderBy.length!=ascending.length</tt>
	 */
	public void setOrderBy(final Function[] orderBy, final boolean[] ascending)
	{
		if(orderBy.length!=ascending.length)
			throw new IllegalArgumentException(
					"orderBy and ascending must have same length, " +
					"but was " + orderBy.length +
					" and " + ascending.length);
		for(int i = 0; i<orderBy.length; i++)
			if(orderBy[i]==null)
				throw new NullPointerException("orderBy" + '[' + i + ']');
		
		this.orderBy = orderBy;
		this.orderAscending = ascending;
	}

	public void addOrderBy(final Function orderBy)
	{
		addOrderBy(orderBy, true);
	}
	
	public void addOrderByDescending(final Function orderBy)
	{
		addOrderBy(orderBy, false);
	}
	
	public void addOrderBy(final Function orderBy, final boolean ascending)
	{
		if(this.orderBy==null)
			this.orderBy = new Function[]{ orderBy };
		else
		{
			final int l = this.orderBy.length;
			final Function[] result = new Function[l+1];
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
	 * @throws IllegalArgumentException if offset is a negative value
	 * @throws IllegalArgumentException if limit is a negative value
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
	 * @see #setLimit(int, int)
	 * @throws IllegalArgumentException if offset is a negative value
	 */
	public void setLimit(final int offset)
	{
		if(offset<0)
			throw new IllegalArgumentException("offset must not be negative, but was " + offset);

		this.offset = offset;
		this.limit = UNLIMITED;
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
		final Transaction transaction = model.getCurrentTransaction();
		
		if(limit==0 || condition==Condition.FALSE)
		{
			final List<QueryInfo> queryInfos = transaction.queryInfos;
			if(queryInfos!=null)
				queryInfos.add(new QueryInfo("skipped search because " + (limit==0 ? "limit==0" : "condition==false")));
			return Collections.<R>emptyList();
		}
		
		return Collections.unmodifiableList(castQL(transaction.search(this, false)));
	}
	
	@SuppressWarnings("unchecked") // TODO: Database#search does not support generics
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
		final Transaction transaction = model.getCurrentTransaction();
		
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
		
		for(final Selectable select : selects)
			Cope.check(select, tc, null);
		
		if(condition!=null)
			condition.check(tc);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.check(tc);
		}
		
		if(orderBy!=null)
			for(Function ob : orderBy)
				Cope.check(ob, tc, null);
		
		return tc;
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
		return new Result<R>(this);
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
		
		/**
		 * Creates an empty Result.
		 */
		Result()
		{
			this.data = Collections.emptyList();
			this.total = 0;
			this.offset = 0;
			this.limit = -1;
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
			if(!(other instanceof Result))
				return false;
			
			final Result o = (Result)other;

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
	
	public static <R> Result<R> emptyResult()
	{
		return new Result<R>();
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
	
	String toString(final boolean key, final boolean totalOnly)
	{
		final Type type = this.type;
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
				model.connect().executor,
				totalOnly,
				transaction.queryInfos);
	}
	
	private static final Condition replaceTrue(final Condition c)
	{
		return c==Condition.TRUE ? null : c;
	}
	
	ArrayList<Object> search(
			final Connection connection,
			final Executor executor,
			final boolean totalOnly,
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
		final Statement bf = executor.newStatement(this);
		
		if (totalOnly && distinct)
		{
			bf.append("select count(*) from ( ");
		}
		
		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause(bf, offset, limit);
		
		bf.append("select");
		
		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSE_AFTER_SELECT)
			dialect.appendLimitClause(bf, offset, limit);
		
		bf.append(' ');
		
		final Selectable[] selects = this.selects;
		final Column[] selectColumns = new Column[selects.length];
		final Type[] selectTypes = new Type[selects.length];

		if(!distinct&&totalOnly)
		{
			bf.append("count(*)");
		}
		else
		{
			if(distinct)
				bf.append("distinct ");
			
			final Holder<Column> selectColumn = new Holder<Column>();
			final Holder<Type  > selectType   = new Holder<Type  >();
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');
				
				selectColumn.value = null;
				selectType  .value = null;
				bf.appendSelect(selects[i], null, selectColumn, selectType);
				selectColumns[i] = selectColumn.value;
				selectTypes  [i] = selectType  .value;
			}
		}

		bf.append(" from ").
			appendTypeDefinition((Join)null, this.type);

		if(joins!=null)
		{
			for(final Join join : joins)
				join.search(bf);
		}

		if(this.condition!=null)
		{
			bf.append(" where ");
			this.condition.append(bf);
		}
		
		if(!totalOnly)
		{
			final Function[] orderBy = this.orderBy;
			
			if(orderBy!=null)
			{
				final boolean[] orderAscending = this.orderAscending;
				for(int i = 0; i<orderBy.length; i++)
				{
					if(i==0)
						bf.append(" order by ");
					else
						bf.append(',');
					
					bf.append(orderBy[i], (Join)null);
					
					if(!orderAscending[i])
						bf.append(" desc");

					// TODO break here, if already ordered by some unique function
				}
			}
			
			if(limitActive)
			{
				switch(limitSupport)
				{
					case CLAUSE_AFTER_WHERE: dialect.appendLimitClause (bf, offset, limit); break;
					case CLAUSES_AROUND:     dialect.appendLimitClause2(bf, offset, limit); break;
					case CLAUSE_AFTER_SELECT:
					case NONE:
						break;
				}
			}
		}
		
		final Model model = this.model;
		final ArrayList<Object> result = new ArrayList<Object>();
		
		if(totalOnly && distinct)
		{
			bf.append(" )");
			if (dialect.subqueryRequiresAlias())
			{
				bf.append(" as cope_total_distinct");
			}
		}
		
		//System.out.println(bf.toString());

		executor.query(connection, bf, queryInfos, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				if(totalOnly)
				{
					resultSet.next();
					result.add(Integer.valueOf(resultSet.getInt(1)));
					if(resultSet.next())
						throw new RuntimeException();
					return null;
				}
				
				if(offset>0 && limitSupport==Dialect.LimitSupport.NONE)
				{
					// TODO: ResultSet.relative
					// Would like to use
					//    resultSet.relative(limitStart+1);
					// but this throws a java.sql.SQLException:
					// Invalid operation for forward only resultset : relative
					for(int i = offset; i>0; i--)
						resultSet.next();
				}
					
				int i = ((limit==Query.UNLIMITED||(limitSupport!=Dialect.LimitSupport.NONE)) ? Integer.MAX_VALUE : limit );
				if(i<=0)
					throw new RuntimeException(String.valueOf(limit));
				
				while(resultSet.next() && (--i)>=0)
				{
					int columnIndex = 1;
					final Object[] resultRow = (selects.length > 1) ? new Object[selects.length] : null;
					final Row dummyRow = new Row();
						
					for(int selectIndex = 0; selectIndex<selects.length; selectIndex++)
					{
						final Selectable select;
						{
							Selectable select0 = selects[selectIndex];
							if(select0 instanceof BindFunction)
								select0 = ((BindFunction)select0).function;
							if(select0 instanceof Aggregate)
								select0 = ((Aggregate)select0).getSource();
							select = select0;
						}
						
						final Object resultCell;
						if(select instanceof FunctionField)
						{
							selectColumns[selectIndex].load(resultSet, columnIndex++, dummyRow);
							final FunctionField selectField = (FunctionField)select;
							if(select instanceof ItemField)
							{
								final StringColumn typeColumn = ((ItemField)selectField).getTypeColumn();
								if(typeColumn!=null)
									typeColumn.load(resultSet, columnIndex++, dummyRow);
							}
							resultCell = selectField.get(dummyRow);
						}
						else if(select instanceof View)
						{
							final View selectFunction = (View)select;
							resultCell = selectFunction.load(resultSet, columnIndex++);
						}
						else
						{
							final Number pk = (Number)resultSet.getObject(columnIndex++);
							//System.out.println("pk:"+pk);
							if(pk==null)
							{
								// can happen when using right outer joins
								resultCell = null;
							}
							else
							{
								final Type type = selectTypes[selectIndex];
								final Type currentType;
								if(type==null)
								{
									final String typeID = resultSet.getString(columnIndex++);
									currentType = model.getType(typeID);
									if(currentType==null)
										throw new RuntimeException("no type with type id "+typeID);
								}
								else
									currentType = type;

								final int pkPrimitive = pk.intValue();
								if(!PK.isValid(pkPrimitive))
									throw new RuntimeException("invalid primary key " + pkPrimitive + " for type " + type.id);
								resultCell = currentType.getItemObject(pkPrimitive);
							}
						}
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
		});

		return result;
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
}

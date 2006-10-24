/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public final class Query<R>
{
	final static int UNLIMITED_COUNT = -66;
	
	final Model model;
	final Selectable[] selects;
	boolean distinct = false;
	final Type type;
	ArrayList<Join> joins = null;
	Condition condition;

	Function[] orderBy = null;
	boolean[] orderAscending;
	
	int limitStart = 0;
	int limitCount = UNLIMITED_COUNT;
	
	boolean makeStatementInfo = false;
	private StatementInfo statementInfo = null;
	
	public Query(final Selectable<? extends R> select)
	{
		this(select, (Condition)null);
	}
	
	public Query(final Selectable<? extends R> select, final Condition condition)
	{
		this.selects = new Selectable[]{select};
		this.type = select.getType();
		this.model = this.type.getModel();
		this.condition = condition;
	}
	
	public Query(final Selectable<R> select, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = new Selectable[]{select};
		this.type = type;
		this.condition = condition;
	}
	
	public Query(final Selectable[] selects, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = selects;
		this.type = type;
		this.condition = condition;
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
		this.condition = condition;
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
		return join(new Join(Join.Kind.INNER, type, null));
	}
	
	/**
	 * Does an inner join with the given type on the given join condition.
	 */
	public Join join(final Type type, final Condition condition)
	{
		return join(new Join(Join.Kind.INNER, type, condition));
	}
	
	public Join joinOuterLeft(final Type type, final Condition condition)
	{
		return join(new Join(Join.Kind.OUTER_LEFT, type, condition));
	}
	
	public Join joinOuterRight(final Type type, final Condition condition)
	{
		return join(new Join(Join.Kind.OUTER_RIGHT, type, condition));
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
			throw new NullPointerException("orderBy is null");
		
		this.orderBy = new Function[]{orderBy};
		this.orderAscending = new boolean[]{ascending};
	}
	
	public void setOrderByAndThis(final Function orderBy, final boolean ascending)
	{
		if(orderBy==null)
			throw new NullPointerException("orderBy is null");
		
		this.orderBy = new Function[]{orderBy, type.thisFunction};
		this.orderAscending = new boolean[]{ascending, true};
	}
	
	/**
	 * @throws RuntimeException if <tt>orderBy.length!=ascending.length</tt>
	 */
	public void setOrderBy(final Function[] orderBy, final boolean[] ascending)
	{
		if(orderBy.length!=ascending.length)
			throw new RuntimeException("orderBy and ascending must have same length, but was "+orderBy.length+" and "+ascending.length);
		for(int i = 0; i<orderBy.length; i++)
			if(orderBy[i]==null)
				throw new NullPointerException("orderBy contains null at index "+i);
		
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
	
	/**
	 * @see #setLimit(int)
	 * @param count the maximum number of items to be found.
	 * @throws RuntimeException if start is a negative value
	 * @throws RuntimeException if count is a negative value
	 */	
	public void setLimit(final int start, final int count)
	{
		if(start<0)
			throw new RuntimeException("start must not be negative, but was " + start);
		if(count<0)
			throw new RuntimeException("count must not be negative, but was " + count);

		this.limitStart = start;
		this.limitCount = count;
	}
	
	/**
	 * @see #setLimit(int, int)
	 * @throws RuntimeException if start is a negative value
	 */	
	public void setLimit(final int start)
	{
		if(start<0)
			throw new RuntimeException("start must not be negative, but was " + start);

		this.limitStart = start;
		this.limitCount = UNLIMITED_COUNT;
	}
	
	public void enableMakeStatementInfo()
	{
		makeStatementInfo = true;
	}
	
	public StatementInfo getStatementInfo()
	{
		return statementInfo;
	}
	
	public void clearStatementInfo()
	{
		statementInfo = null;;
	}
	
	void addStatementInfo(final StatementInfo newInfo)
	{
		if(makeStatementInfo != (newInfo!=null))
			throw new RuntimeException(String.valueOf(makeStatementInfo));
		
		if(newInfo!=null)
		{
			final String ROOT_FOR_MULTIPLE = "--- multiple statements ---";
			final StatementInfo previousInfo = this.statementInfo;
			if(previousInfo!=null)
			{
				if(ROOT_FOR_MULTIPLE.equals(previousInfo.text))
				{
					previousInfo.addChild(newInfo);
				}
				else
				{
					final StatementInfo rootForMultiple = new StatementInfo(ROOT_FOR_MULTIPLE);
					rootForMultiple.addChild(previousInfo);
					rootForMultiple.addChild(newInfo);
					this.statementInfo = rootForMultiple;
				}
			}
			else
				this.statementInfo = newInfo;
		}
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
		check();
		
		if(limitCount==0)
		{
			if(makeStatementInfo)
				addStatementInfo(new StatementInfo("skipped search because limitCount==0"));
			return Collections.<R>emptyList();
		}
		
		return model.getCurrentTransaction().search(
			this
		);
	}
	
	List<R> searchUncached()
	{
		return castQL(Collections.unmodifiableList(model.getDatabase().search(model.getCurrentTransaction().getConnection(), this, false)));
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
	public int countWithoutLimit()
	{
		check();
		final Collection result = model.getDatabase().search(model.getCurrentTransaction().getConnection(), this, true);
		return ((Integer)result.iterator().next()).intValue();
	}

	private void check()
	{
		//System.out.println("select " + type.getJavaClass().getName() + " where " + condition);
		if(condition!=null)
			condition.check(this);

		if(joins!=null && !model.supportsRightOuterJoins())
		{
			for(Iterator i = joins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				if(join.getKind()==Join.Kind.OUTER_RIGHT)
					throw new RuntimeException("right outer joins not supported, see Model#supportsRightOuterJoins");
			}
		}
	}
	
	/**
	 * Searches for items matching this query.
	 * <p>
	 * Returns a {@link Result} containing the
	 * {@link Result#getData() data} and the
	 * {@link Result#getCountWithoutLimit() countWithoutLimit}.
	 * The {@link Result#getData() data} is equal to
	 * what {@link #search()} would have returned for this query.
	 * The {@link Result#getCountWithoutLimit() countWithoutLimit} is equal to what
	 * {@link #countWithoutLimit()} would have returned for this query.
	 * <p>
	 * This method does it's best to avoid issuing two queries
	 * for searching and counting.
	 */
	public Result searchAndCountWithoutLimit()
	{
		final Collection data = search();
		final int dataSize = data.size();

		return new Result(data,
				(((dataSize>0) || (limitStart==0))  &&  ((dataSize<limitCount) || (limitCount==UNLIMITED_COUNT)))
				? (limitStart+dataSize)
				: countWithoutLimit());
	}
	
	public static final class Result
	{
		final Collection data;
		final int countWithoutLimit;
		
		private Result(final Collection data, final int countWithoutLimit)
		{
			if(data==null)
				throw new RuntimeException();
			if(countWithoutLimit<0)
				throw new RuntimeException(String.valueOf(countWithoutLimit));
			
			this.data = data;
			this.countWithoutLimit = countWithoutLimit;
		}
		
		public Collection getData()
		{
			return data;
		}
		
		public int getCountWithoutLimit()
		{
			return countWithoutLimit;
		}
		
		@Override
		public boolean equals(final Object o)
		{
			final Result or = (Result)o;

			return countWithoutLimit==or.countWithoutLimit && data.equals(or.data);
		}
		
		@Override
		public String toString()
		{
			return data.toString() + '(' + countWithoutLimit + ')';
		}
	}
	
	/**
	 * Searches equivalently to {@link #search()},
	 * but assumes that the search result has at most one element.
	 * <p>
	 * Returns null, if the search result is {@link Collection#isEmpty() empty},
	 * returns the only element of the search result, if the result {@link Collection#size() size} is exactly one.
	 * @throws RuntimeException if the search result size is greater than one.
	 * @see Type#searchSingleton(Condition)
	 */
	public R searchSingleton()
	{
		final List<R> searchResultCollection = search();
		final Iterator<R> searchResult = searchResultCollection.iterator();
		if(searchResult.hasNext())
		{
			final R result = searchResult.next();
			if(searchResult.hasNext())
				throw new RuntimeException("expected result of size one or less, but was " + searchResultCollection + " for query: " + toString());
			else
				return result;
		}
		else
			return null;
	}
	
	/**
	 * @deprecated renamed to {@link #searchSingleton()}.
	 */
	@Deprecated
	public R searchUnique()
	{
		return searchSingleton();
	}
	
	@Override
	public String toString()
	{
		final StringBuffer bf = new StringBuffer();
		
		// BEWARE
		// this method duplicates Query.Key#toString(),
		// so if you change something here,
		// you will probably want to change it there as well.
		
		bf.append("select ");
		
		if(distinct)
			bf.append("distinct ");
		
		for(int i = 0; i<selects.length; i++)
		{
			if(i>0)
				bf.append(',');

			bf.append(selects[i]);
		}

		bf.append(" from ").
			append(type);

		if(joins!=null)
		{
			for(final Join join : joins)
			{
				bf.append(' ').
					append(join.kind.sql).
					append(join.type);

				final Condition joinCondition = join.condition;
				if(joinCondition!=null)
				{
					bf.append(" on ").
						append(joinCondition);
				}
			}
		}

		if(condition!=null)
		{
			bf.append(" where ").
				append(condition);
		}

		if(orderBy!=null)
		{
			bf.append(" order by ");
			for(int i = 0; i<orderBy.length; i++)
			{
				if(i>0)
					bf.append(", ");
				
				bf.append(orderBy[i]);
				if(!orderAscending[i])
					bf.append(" desc");
			}
		}
		
		if(limitStart>0 || limitCount!=UNLIMITED_COUNT)
		{
			bf.append(" limit ").
				append(limitStart).
				append(' ').
				append(limitCount);
		}
		
		return bf.toString();
	}
	
	static final class Key
	{
		// model left out, because it can be computed from type
		final Selectable[] selects;
		final boolean distinct;
		final Type type;
		final Join[] joins;
		final Condition condition;
		final Function[] orderBy;
		final boolean[] orderAscending;

		final int limitStart;
		final int limitCount;

		// makeStatementInfo and statementInfo are deliberatly left out
		
		private final int hashCode;
		
		int hits = 0;
		
		Key(final Query<? extends Object> query)
		{
			selects = query.selects;
			distinct = query.distinct;
			type = query.type;
			joins = query.joins==null ? null : query.joins.toArray(new Join[query.joins.size()]);
			condition = query.condition;
			orderBy = query.orderBy;
			orderAscending = query.orderAscending;
			limitStart = query.limitStart;
			limitCount = query.limitCount;
			
			hashCode =
				Arrays.hashCode(selects)
				^ hashCode(distinct)
				^ hashCode(type)
				^ Arrays.hashCode(joins)
				^ hashCode(condition)
				^ Arrays.hashCode(orderBy)
				^ Arrays.hashCode(orderAscending)
				^ limitStart
				^ limitCount;
		}
		
		@Override
		public boolean equals(final Object obj)
		{
			final Key other = (Key)obj;
			return
				Arrays.equals(selects, other.selects)
				&& distinct == other.distinct
				&& type==type
				&& Arrays.equals(joins, other.joins)
				&& equals(condition, other.condition)
				&& Arrays.equals(orderBy, other.orderBy)
				&& Arrays.equals(orderAscending, other.orderAscending)
				&& limitStart == other.limitStart
				&& limitCount == other.limitCount;
		}
		
		@Override
		public int hashCode()
		{
			return hashCode;
		}
		
		private static boolean equals(final Object a, final Object b)
		{
			assert a==null || !a.getClass().isArray();
			assert b==null || !b.getClass().isArray();
			return a==null ? b==null : ( b!=null && a.equals(b) );
		}
		
		private static int hashCode(final Object obj)
		{
			assert obj==null || !obj.getClass().isArray();
			return obj==null ? 0 : obj.hashCode();
		}
		
		private static int hashCode(final boolean b)
		{
			return b ? 1 : 0;
		}

		/**
		 * BEWARE
		 * this method nearly duplicates {@link Query#toString()},
		 * so if you change something here,
		 * you will probably want to change it there as well.
		 */
		@Override
		public String toString()
		{
			final StringBuffer bf = new StringBuffer();
			
			bf.append("select ");
			
			if(distinct)
				bf.append("distinct ");
			
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');

				bf.append(selects[i]);
			}

			bf.append(" from ").
				append(type);

			if(joins!=null)
			{
				for(final Join join : joins)
				{
					bf.append(' ').
						append(join.kind.sql).
						append(join.type);

					final Condition joinCondition = join.condition;
					if(joinCondition!=null)
					{
						bf.append(" on ").
							append(joinCondition.toStringForQueryKey());
					}
				}
			}

			if(condition!=null)
			{
				bf.append(" where ").
					append(condition.toStringForQueryKey());
			}

			if(orderBy!=null)
			{
				bf.append(" order by ");
				for(int i = 0; i<orderBy.length; i++)
				{
					if(i>0)
						bf.append(", ");
					
					bf.append(orderBy[i]);
					if(!orderAscending[i])
						bf.append(" desc");
				}
			}
			
			if(limitStart>0 || limitCount!=UNLIMITED_COUNT)
			{
				bf.append(" limit ").
					append(limitStart).
					append(' ').
					append(limitCount);
			}
			
			return bf.toString();
		}
	}
}

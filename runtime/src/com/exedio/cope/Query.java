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
	final Function[] selects;
	final Type type;
	ArrayList<Join> joins = null;
	Condition condition;

	Function[] orderBy = null;
	boolean[] orderAscending;
	
	int limitStart = 0;
	int limitCount = UNLIMITED_COUNT;
	
	boolean makeStatementInfo = false;
	private StatementInfo statementInfo = null;
	
	public Query(final Function<? extends R> select, final Condition condition)
	{
		this.selects = new Function[]{select};
		this.type = select.getType();
		this.model = this.type.getModel();
		this.condition = condition;
	}
	
	public Query(final Function<R> select, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = new Function[]{select};
		this.type = type;
		this.condition = condition;
	}
	
	public Query(final Function[] selects, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selects = selects;
		this.type = type;
		this.condition = condition;
	}
	
	public Type getType()
	{
		return type;
	}
	
	public void setCondition(final Condition condition)
	{
		this.condition = condition;
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
		return join(new Join(Join.KIND_INNER, type, null));
	}
	
	/**
	 * Does an inner join with the given type on the given join condition.
	 */
	public Join join(final Type type, final Condition condition)
	{
		return join(new Join(Join.KIND_INNER, type, condition));
	}
	
	public Join joinOuterLeft(final Type type, final Condition condition)
	{
		return join(new Join(Join.KIND_OUTER_LEFT, type, condition));
	}
	
	public Join joinOuterRight(final Type type, final Condition condition)
	{
		return join(new Join(Join.KIND_OUTER_RIGHT, type, condition));
	}
	
	public List<Join> getJoins()
	{
		return joins==null ? Collections.<Join>emptyList() : joins;
	}
	
	public void setOrderByThis(final boolean ascending)
	{
		this.orderBy = new Function[]{type.getThis()};
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
		
		this.orderBy = new Function[]{orderBy, type.getThis()};
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
	public final int countWithoutLimit()
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
				if(join.getKind()==Join.KIND_OUTER_RIGHT)
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
		
		public boolean equals(final Object o)
		{
			final Result or = (Result)o;

			return countWithoutLimit==or.countWithoutLimit && data.equals(or.data);
		}
		
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
	 * @see Type#searchUnique(Condition)
	 */
	public R searchUnique()
	{
		final Iterator<R> searchResult = search().iterator();
		if(searchResult.hasNext())
		{
			final R result = searchResult.next();
			if(searchResult.hasNext())
				throw new RuntimeException(toString());
			else
				return result;
		}
		else
			return null;
	}
	
	public String toString()
	{
		final StringBuffer bf = new StringBuffer();
		
		bf.append("select ");
		
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
			for(Iterator i = joins.iterator(); i.hasNext(); )
			{
				final Join join = (Join)i.next();
				
				bf.append(' ').
					append(join.getKindString()).
					append(" join ").
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
		final Model model;
		final Function[] selects;
		final Type type;
		final ArrayList<Join> joins;
		final Condition condition;

		final Function[] orderBy;
		final boolean[] orderAscending;

		final int limitStart;
		final int limitCount;

		final boolean makeStatementInfo;
		
		String name = null;
		int hits = 0;
		
		Key(final Query<? extends Object> query)
		{
			model = query.model;
			selects = query.selects;
			type = query.type;
			joins = query.joins==null ? null : new ArrayList<Join>(query.joins);
			condition = query.condition;
			orderBy = query.orderBy;
			orderAscending = query.orderAscending;
			limitStart = query.limitStart;
			limitCount = query.limitCount;
			makeStatementInfo = query.makeStatementInfo;
		}
		
		public boolean equals( Object obj )
		{
			if ( obj==null )
			{
				throw new NullPointerException( "must not compare Query.Key to null" );
			}
			Key other = (Key)obj;
			return equals( model, other.model )
				&& Arrays.equals( selects, other.selects )
				&& equals( type, other.type )
				&& equals( joins, other.joins )
				&& equals( condition, other.condition )
				&& equals( orderBy, other.orderBy )
				&& orderAscending==other.orderAscending
				&& limitStart == other.limitStart
				&& limitCount == other.limitCount
				&& makeStatementInfo == other.makeStatementInfo;
		}
		
		private static boolean equals( Object a, Object b )
		{
			return a==null ? b==null : ( b!=null && a.equals(b) );
		}
		
		private static int hashCode( Object obj )
		{
			return obj==null ? 0 : obj.hashCode();
		}
		
		private static int hashCode( boolean b )
		{
			return b ? 1 : 0;
		}
		
		private static int hashCode( Object[] selects )
		{
			if ( selects==null )
			{
				return 0;
			}
			else
			{
				int hash = 0;
				for ( int i=0; i<selects.length; i++ )
				{
					hash ^= hashCode( selects[i] );
				}
				return hash;
			}
		}
		
		private static int hashCode( boolean[] array )
		{
			// use Arrays.hashCode() in JDK 1.5
			if ( array==null )
			{
				return 0;
			}
			else
			{
				int hash = 0;
				for ( int i=0; i<array.length; i++ )
				{
					hash = 3*hash + hashCode( array[i] );
				}
				return hash;
			}
		}
		
		public int hashCode()
		{
			return hashCode(model) 
					^ hashCode(selects)
					^ hashCode(type)
					^ hashCode(joins)
					^ hashCode(condition)
					^ hashCode(orderBy)
					^ hashCode(orderAscending)
					^ limitStart
					^ limitCount
					^ hashCode(makeStatementInfo);
		}		
	}
}

/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.search.Condition;

public final class Query
{
	final static int UNLIMITED_COUNT = -66;
	
	final Model model;
	final Selectable[] selectables;
	final Type type;
	ArrayList joins = null;
	Condition condition;

	Function orderBy = null;
	
	boolean orderAscending;
	boolean deterministicOrder = false;

	int start = 0;
	int count = UNLIMITED_COUNT;
	
	boolean makeStatementInfo = false;
	private StatementInfo statementInfo = null;
	
	public Query(final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selectables = new Type[]{type};
		this.type = type;
		this.condition = condition;
	}
	
	public Query(final Selectable selectable, final Condition condition)
	{
		this.selectables = new Selectable[]{selectable};
		if(selectable instanceof Function)
			this.type = ((Function)selectable).getType();
		else
			this.type = (Type)selectable;

		this.model = this.type.getModel();
		this.condition = condition;
	}
	
	public Query(final Selectable selectable, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selectables = new Selectable[]{selectable};
		this.type = type;
		this.condition = condition;
	}
	
	public Query(final Selectable[] selectables, final Type type, final Condition condition)
	{
		this.model = type.getModel();
		this.selectables = selectables;
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
	
	private final Join join(final Join join)
	{
		if(joins==null)
			joins = new ArrayList();
		
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
	
	public List getJoins()
	{
		return joins==null ? Collections.EMPTY_LIST : joins;
	}
	
	public void setOrderBy(final Function orderBy, final boolean ascending)
	{
		this.orderBy = orderBy;
		this.orderAscending = ascending;
	}

	public void setDeterministicOrder(final boolean deterministicOrder)
	{
		this.deterministicOrder = deterministicOrder;
	}

	/**
	 * @see #setRange(int)
	 * @param count the maximum number of items to be found.
	 * @throws RuntimeException if start is a negative value
	 * @throws RuntimeException if count is a negative value
	 */	
	public void setRange(final int start, final int count)
	{
		if(start<0)
			throw new RuntimeException("start must not be negative, but was " + start);
		if(count<0)
			throw new RuntimeException("count must not be negative, but was " + count);

		this.start = start;
		this.count = count;
	}
	
	/**
	 * @see #setRange(int, int)
	 * @throws RuntimeException if start is a negative value
	 */	
	public void setRange(final int start)
	{
		if(start<0)
			throw new RuntimeException("start must not be negative, but was " + start);

		this.start = start;
		this.count = UNLIMITED_COUNT;
	}
	
	public void enableMakeStatementInfo()
	{
		makeStatementInfo = true;
	}
	
	public StatementInfo getStatementInfo()
	{
		return statementInfo;
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
	 * result in an <code>UnsupportedOperationException</code>.
	 */
	public final Collection search()
	{
		check();
		
		if(count==0)
			return Collections.EMPTY_LIST;

		return Collections.unmodifiableList(model.getDatabase().search(model.getCurrentTransaction().getConnection(), this, false));
	}
	
	/**
	 * Counts the items matching this query.
	 * <p>
	 * Returns the
	 * {@link Collection#size() size} of what
	 * {@link #search()} would have returned for this query with
	 * {@link #setRange(int)} reset set to <code>(0)</code>.
	 */
	public final int countWithoutRange()
	{
		check();
		final Collection result = model.getDatabase().search(model.getCurrentTransaction().getConnection(), this, true);
		return ((Integer)result.iterator().next()).intValue();
	}

	private final void check()
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
	 * {@link Result#getCountWithoutRange() countWithoutRange}.
	 * The {@link Result#getData() data} is equal to
	 * what {@link #search()} would have returned for this query.
	 * The {@link Result#getCountWithoutRange() countWithoutRange} is equal to what
	 * {@link #countWithoutRange()} would have returned for this query.
	 * <p>
	 * This method does it's best to avoid issuing two queries
	 * for searching and counting.
	 */
	public final Result searchAndCountWithoutRange()
	{
		final Collection data = search();
		final int dataSize = data.size();

		return new Result(data,
				(dataSize<count || count==UNLIMITED_COUNT)
				? (start+dataSize)
				: countWithoutRange());
	}
	
	public static final class Result
	{
		final Collection data;
		final int countWithoutRange;
		
		private Result(final Collection data, final int countWithoutRange)
		{
			if(data==null)
				throw new RuntimeException();
			if(countWithoutRange<0)
				throw new RuntimeException(String.valueOf(countWithoutRange));
			
			this.data = data;
			this.countWithoutRange = countWithoutRange;
		}
		
		public Collection getData()
		{
			return data;
		}
		
		public int getCountWithoutRange()
		{
			return countWithoutRange;
		}
		
		public boolean equals(final Object o)
		{
			final Result or = (Result)o;

			return countWithoutRange==or.countWithoutRange && data.equals(or.data);
		}
		
		public String toString()
		{
			return data.toString() + '(' + countWithoutRange + ')';
		}
	}
}

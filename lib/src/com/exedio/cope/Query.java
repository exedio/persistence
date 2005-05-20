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
import java.util.List;

import com.exedio.cope.search.Condition;

public final class Query
{
	final Model model;
	final Selectable[] selectables;
	final Type type;
	ArrayList joins = null;
	final Condition condition;

	Function orderBy = null;
	
	boolean orderAscending;
	boolean deterministicOrder = false;

	int start = 0;
	int count = -1;
	
	boolean makeStatementInfo = false;
	StatementInfo statementInfo = null;
	
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
	
	final void join(final Join join)
	{
		if(joins==null)
			joins = new ArrayList();
		
		joins.add(join);
	}
	
	/**
	 * Does an inner join with the given type on the given join condition.
	 */
	public void join(final Type type, final Condition condition)
	{
		join(new Join(Join.KIND_INNER, type, condition));
	}
	
	public void joinOuterLeft(final Type type, final Condition condition)
	{
		join(new Join(Join.KIND_OUTER_LEFT, type, condition));
	}
	
	public void joinOuterRight(final Type type, final Condition condition)
	{
		join(new Join(Join.KIND_OUTER_RIGHT, type, condition));
	}
	
	public List getJoins()
	{
		return joins;
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
	 * @throws RuntimeException if start < 0
	 */	
	public void setRange(final int start, final int count)
	{
		this.start = start;
		this.count = count;
		if(start<0)
			throw new RuntimeException();
	}
	
	public void enableMakeStatementInfo()
	{
		makeStatementInfo = true;
	}
	
	public StatementInfo getStatementInfo()
	{
		return statementInfo;
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
		//System.out.println("select " + type.getJavaClass().getName() + " where " + condition);
		if(condition!=null)
			condition.check(this);
		return Collections.unmodifiableList(model.getDatabase().search(this));
	}
	
}

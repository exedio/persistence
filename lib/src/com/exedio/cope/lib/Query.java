package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.lib.search.Condition;

public class Query
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
	
	public void join(final Type type, final Condition condition)
	{
		if(joins==null)
			joins = new ArrayList();
		
		joins.add(new Join(type, condition));
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
	
	void check()
	{
		if(condition!=null)
			condition.check(this);
	}

}

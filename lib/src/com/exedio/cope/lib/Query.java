package com.exedio.cope.lib;

import java.util.TreeSet;

import com.exedio.cope.lib.search.Condition;

public class Query
{
	final Model model;
	final Selectable[] selectables;
	final TreeSet fromTypes = new TreeSet(Type.COMPARATOR);
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
		this.fromTypes.add(type);
		this.condition = condition;
	}
	
	public Query(final Type selectType, final Type fromType2, final Condition condition)
	{
		this.model = selectType.getModel();
		this.selectables = new Type[]{selectType};
		this.fromTypes.add(selectType);
		this.fromTypes.add(fromType2);
		this.condition = condition;
	}
	
	public Query(final Selectable selectable, final Type[] fromTypes, final Condition condition)
	{
		this.model = fromTypes[0].getModel();
		this.selectables = new Selectable[]{selectable};
		for(int i = 0; i<fromTypes.length; i++)
			this.fromTypes.add(fromTypes[i]);
		this.condition = condition;
	}
	
	public Query(final Selectable[] selectables, final Type[] fromTypes, final Condition condition)
	{
		this.model = fromTypes[0].getModel();
		this.selectables = selectables;
		for(int i = 0; i<fromTypes.length; i++)
			this.fromTypes.add(fromTypes[i]);
		this.condition = condition;
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
			condition.check(fromTypes);
	}

}

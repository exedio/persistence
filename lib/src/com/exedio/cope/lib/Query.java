package com.exedio.cope.lib;

import com.exedio.cope.lib.search.Condition;

public class Query
{
	final Type type;
	final Condition condition;
	
	public Query(final Type type, final Condition condition)
	{
		this.type = type;
		this.condition = condition;
	}

}

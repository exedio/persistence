
package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.Statement;

public abstract class CompositeCondition extends Condition
{
	private final String operator;
	public final Condition[] conditions;

	CompositeCondition(final String operator, final Condition[] conditions)
	{
		this.operator = operator;
		this.conditions = conditions;
	}

	public final void appendStatement(final Statement bf)
	{
		bf.append('(');
		conditions[0].appendStatement(bf);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(operator);
			conditions[i].appendStatement(bf);
		}
		bf.append(')');
	}

	public final void check(final TreeSet fromTypes)
	{
		for(int i = 0; i<conditions.length; i++)
			conditions[i].check(fromTypes);
	}

	public final String toString()
	{
		final StringBuffer buf = new StringBuffer();
		
		buf.append('(');
		buf.append(conditions[0].toString());
		for(int i = 1; i<conditions.length; i++)
		{
			buf.append(operator);
			buf.append(conditions[i].toString());
		}
		buf.append(')');
		
		return buf.toString();
	}
}

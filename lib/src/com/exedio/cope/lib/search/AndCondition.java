
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.Type;

public final class AndCondition extends Condition
{
	public final Condition[] conditions;

	public AndCondition(final Condition[] conditions)
	{
		this.conditions = conditions;
	}

	public final void appendStatement(final Statement bf)
	{
		bf.append('(');
		conditions[0].appendStatement(bf);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(" and ");
			conditions[i].appendStatement(bf);
		}
		bf.append(')');
	}

	public final void check(final Type type)
	{
		for(int i = 0; i<conditions.length; i++)
			conditions[i].check(type);
	}

	public final String toString()
	{
		final StringBuffer buf = new StringBuffer();
		
		buf.append('(');
		buf.append(conditions[0].toString());
		for(int i = 1; i<conditions.length; i++)
		{
			buf.append(" and ");
			buf.append(conditions[i].toString());
		}
		buf.append(')');
		
		return buf.toString();
	}
}

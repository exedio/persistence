
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Database;

public final class AndCondition extends Condition
{
	public final Condition[] conditions;

	public AndCondition(final Condition[] conditions)
	{
		this.conditions = conditions;
	}

	public final void appendStatement(final Database.Statement bf)
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


package persistence.search;

import persistence.Attribute;
import persistence.StringAttribute;

public final class AndCondition extends Condition
{
	public final Condition[] conditions;

	public AndCondition(final Condition[] conditions)
	{
		this.conditions = conditions;
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


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
	
}

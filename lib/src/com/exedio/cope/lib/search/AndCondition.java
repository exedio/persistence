
package com.exedio.cope.lib.search;

public final class AndCondition extends CompositeCondition
{
	public AndCondition(final Condition[] conditions)
	{
		super(" and ", conditions);
	}
}

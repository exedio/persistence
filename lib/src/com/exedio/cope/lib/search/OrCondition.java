
package com.exedio.cope.lib.search;

public final class OrCondition extends CompositeCondition
{
	public OrCondition(final Condition[] conditions)
	{
		super(" or ", conditions);
	}
}

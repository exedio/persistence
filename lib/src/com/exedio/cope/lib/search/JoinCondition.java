package com.exedio.cope.lib.search;

import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Statement;

public final class JoinCondition extends Condition
{
	public final ItemAttribute attribute;

	public JoinCondition(final ItemAttribute attribute)
	{
		this.attribute = attribute;
	}

	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute).
			append('=').
			appendPK(attribute.getTargetType());
	}

	public final void check(final Query query)
	{
		check(attribute, query);
		check(attribute.getTargetType(), query);
	}

	public final String toString()
	{
		return attribute.getName() + "=" + attribute.getTargetType().getJavaClass().getName() + ".PK";
	}

}

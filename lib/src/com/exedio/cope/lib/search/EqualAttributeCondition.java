package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringAttribute;

public final class EqualAttributeCondition extends Condition
{
	public final Attribute attribute1;
	public final Attribute attribute2;

	public EqualAttributeCondition(
				final StringAttribute attribute1,
				final StringAttribute attribute2)
	{
		this.attribute1 = attribute1;
		this.attribute2 = attribute2;
	}

	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute1).
			append('=').
			append(attribute2);
	}

	public final void check(final TreeSet fromTypes)
	{
		check(attribute1, fromTypes);
		check(attribute2, fromTypes);
	}

	public final String toString()
	{
		return attribute1.getName() + "=" + attribute2.getName();
	}

}

package com.exedio.cope.lib.search;

import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Query;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringAttribute;

public final class EqualAttributeCondition extends Condition
{
	public final ObjectAttribute attribute1;
	public final ObjectAttribute attribute2;

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

	public final void check(final Query query)
	{
		check(attribute1, query);
		check(attribute2, query);
	}

	public final String toString()
	{
		return attribute1.getName() + "=" + attribute2.getName();
	}

}


package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Statement;

public abstract class Condition
{
	public abstract void appendStatement(Statement statment);
	
	public abstract void check(final TreeSet fromTypes);

	public final void check(final Attribute attribute, final TreeSet fromTypes)
	{
		if (!fromTypes.contains(attribute.getType()))
			throw new RuntimeException(
				"attribute "
					+ attribute
					+ " belongs to type "
					+ attribute.getType()
					+ ", which is not a from-type of the query: "
					+ fromTypes);
	}
}

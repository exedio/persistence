
package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringAttribute;

public final class EqualCondition extends Condition
{
	public final Attribute attribute;
	public final Object value;

	public EqualCondition(final StringAttribute attribute, final String value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public EqualCondition(final IntegerAttribute attribute, final Integer value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public EqualCondition(final ItemAttribute attribute, final Item value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute);
		if(value!=null)
			bf.append('=').
				appendValue(attribute, value);
		else
			bf.append(" is null");
	}

	public final void check(final TreeSet fromTypes)
	{
		check(attribute, fromTypes);
	}

	public final String toString()
	{
		return attribute.getName() + "='" + value + '\'';
	}
	
}


package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.Type;

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
		bf.append(attribute).
			append('=').
			appendValue(attribute, value);
	}

	public final void check(final Type type)
	{
		if(attribute.getType()!=type)
			throw new RuntimeException("attribute "+attribute+" belongs to type "+attribute.getType()+", which is the type of the query: "+type);
	}

	public final String toString()
	{
		return attribute.getName() + "='" + value + '\'';
	}
	
}

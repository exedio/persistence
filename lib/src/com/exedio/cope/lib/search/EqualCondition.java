
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
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
	
	public EqualCondition(final ItemAttribute attribute, final Item value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public final String toString()
	{
		return attribute.getName() + "='" + value + '\'';
	}
	
}

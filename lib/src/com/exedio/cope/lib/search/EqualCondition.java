
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.database.Database;

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
	
	public final void appendSQL(final Database database, final StringBuffer bf)
	{
		bf.append(attribute.getPersistentQualifier()).
			append('=');
		if(attribute instanceof StringAttribute)
			bf.append('\'');
		bf.append(value);
		if(attribute instanceof StringAttribute)
			bf.append('\'');
	}

	public final String toString()
	{
		return attribute.getName() + "='" + value + '\'';
	}
	
}

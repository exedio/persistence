
package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.Function;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringFunction;

public final class EqualCondition extends Condition
{
	public final Function attribute; // TODO rename attribute
	public final Object value;

	public EqualCondition(final ObjectAttribute attribute)
	{
		this.attribute = attribute;
		this.value = null;
	}
	
	public EqualCondition(final StringFunction attribute, final String value) // TODO rename argument
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public EqualCondition(final IntegerAttribute attribute, final Integer value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public EqualCondition(final LongAttribute attribute, final Long value)
	{
		this.attribute = attribute;
		this.value = value;
	}
	
	public EqualCondition(final DoubleAttribute attribute, final Double value)
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

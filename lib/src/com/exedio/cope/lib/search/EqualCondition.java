
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
	public final Function function;
	public final Object value;

	public EqualCondition(final ObjectAttribute function)
	{
		this.function = function;
		this.value = null;
	}
	
	public EqualCondition(final StringFunction function, final String value)
	{
		this.function = function;
		this.value = value;
	}
	
	public EqualCondition(final IntegerAttribute attribute, final Integer value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public EqualCondition(final LongAttribute attribute, final Long value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public EqualCondition(final DoubleAttribute attribute, final Double value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public EqualCondition(final ItemAttribute attribute, final Item value)
	{
		this.function = attribute;
		this.value = value;
	}
	
	public final void appendStatement(final Statement bf)
	{
		bf.append(function);
		if(value!=null)
			bf.append('=').
				appendValue(function, value);
		else
			bf.append(" is null");
	}

	public final void check(final TreeSet fromTypes)
	{
		check(function, fromTypes);
	}

	public final String toString()
	{
		return function.getName() + "='" + value + '\'';
	}
	
}

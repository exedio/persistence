
package com.exedio.cope.lib.search;

import java.util.TreeSet;

import com.exedio.cope.lib.DoubleAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.LongAttribute;
import com.exedio.cope.lib.ObjectAttribute;
import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.StringAttribute;

public class LiteralCondition extends Condition
{
	private final String operator; 
	public final ObjectAttribute attribute;
	public final Object value;

	private LiteralCondition(final String operator, final ObjectAttribute attribute, final Object value)
	{
		this.operator = operator;
		this.attribute = attribute;
		this.value = value;

		if(operator==null)
			throw new NullPointerException();
		if(attribute==null)
			throw new NullPointerException();
		if(value==null)
			throw new NullPointerException();
	}
	
	LiteralCondition(final String operator, final StringAttribute attribute, final String value)
	{
		this(operator, (ObjectAttribute)attribute, (Object)value);
	}
	
	LiteralCondition(final String operator, final IntegerAttribute attribute, final Integer value)
	{
		this(operator, (ObjectAttribute)attribute, (Object)value);
	}
	
	LiteralCondition(final String operator, final LongAttribute attribute, final Long value)
	{
		this(operator, (ObjectAttribute)attribute, (Object)value);
	}
	
	LiteralCondition(final String operator, final DoubleAttribute attribute, final Double value)
	{
		this(operator, (ObjectAttribute)attribute, (Object)value);
	}
	
	LiteralCondition(final String operator, final EnumerationAttribute attribute, final EnumerationValue value)
	{
		this(operator, (ObjectAttribute)attribute, (Object)value);
	}
	
	public final void appendStatement(final Statement bf)
	{
		bf.append(attribute);
		if(value!=null)
			bf.append(operator).
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
		return attribute.getName() + operator + '\'' + value + '\'';
	}

}

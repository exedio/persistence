/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.exedio.cope;

import com.exedio.cope.function.LengthFunction;
import com.exedio.cope.function.SumFunction;
import com.exedio.cope.function.UppercaseFunction;
import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.Condition;
import com.exedio.cope.search.EqualTargetCondition;
import com.exedio.cope.search.NotEqualCondition;
import com.exedio.cope.search.OrCondition;

/**
 * Utility class for creating conditions.
 * May be subclassed to access methods without class qualifier.
 * 
 * @author Ralf Wiebicke
 */
public abstract class Cope
{
	Cope()
	{}

	public static final NotEqualCondition isNotNull(final ObjectAttribute attribute)
	{
		return new NotEqualCondition(attribute, null);
	}
	
	public static final EqualTargetCondition equalTarget(final ItemAttribute attribute)
	{
		return new EqualTargetCondition(attribute, null);
	}
	
	public static final EqualTargetCondition equalTarget(final ItemAttribute attribute, final Join targetJoin)
	{
		return new EqualTargetCondition(attribute, targetJoin);
	}
	
	public static final AndCondition and(final Condition condition1, final Condition condition2)
	{
		return new AndCondition(new Condition[]{condition1, condition2});
	}
	
	public static final OrCondition or(final Condition condition1, final Condition condition2)
	{
		return new OrCondition(new Condition[]{condition1, condition2});
	}
	
	public static final AttributeValue attributeValue(final StringAttribute attribute, final String value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final BooleanAttribute attribute, final Boolean value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final BooleanAttribute attribute, final boolean value)
	{
		return new AttributeValue(attribute, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public static final AttributeValue attributeValue(final IntegerAttribute attribute, final Integer value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final IntegerAttribute attribute, final int value)
	{
		return new AttributeValue(attribute, new Integer(value));
	}
	
	public static final AttributeValue attributeValue(final LongAttribute attribute, final Long value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final LongAttribute attribute, final long value)
	{
		return new AttributeValue(attribute, new Long(value));
	}
	
	public static final AttributeValue attributeValue(final DoubleAttribute attribute, final Double value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final DoubleAttribute attribute, final double value)
	{
		return new AttributeValue(attribute, new Double(value));
	}
	
	public static final AttributeValue attributeValue(final ItemAttribute attribute, final Item value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final AttributeValue attributeValue(final EnumAttribute attribute, final EnumValue value)
	{
		return new AttributeValue(attribute, value);
	}
	
	public static final LengthFunction length(final StringFunction source)
	{
		return new LengthFunction(source);
	}
	
	public static final SumFunction sum(final IntegerFunction addend1, final IntegerFunction addend2)
	{
		return new SumFunction(new IntegerFunction[]{addend1, addend2});
	}

	public static final SumFunction sum(final IntegerFunction addend1, final IntegerFunction addend2, final IntegerFunction addend3)
	{
		return new SumFunction(new IntegerFunction[]{addend1, addend2, addend3});
	}

	public static final UppercaseFunction uppercase(final StringFunction source)
	{
		return new UppercaseFunction(source);
	}
	
}

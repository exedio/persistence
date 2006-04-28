/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class DoubleAttribute extends FunctionAttribute<Double>
{

	private DoubleAttribute(final boolean isfinal, final boolean optional, final boolean unique, final Double defaultValue)
	{
		super(isfinal, optional, unique, defaultValue);
		checkDefaultValue();
	}
	
	public DoubleAttribute(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}

	public FunctionAttribute<Double> copyFunctionAttribute()
	{
		return new DoubleAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultValue);
	}
	
	public DoubleAttribute defaultTo(final Double defaultValue)
	{
		return new DoubleAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultValue);
	}
	
	@Override
	Class initialize(final java.lang.reflect.Type genericType)
	{
		return Double.class;
	}
	
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DoubleColumn(table, name, optional, 30);
	}

	Double get(final Row row)
	{
		return (Double)row.get(getColumn());
	}
	
	void set(final Row row, final Double surface)
	{
		row.put(getColumn(), surface);
	}
	
	/**
	 * @throws RuntimeException if this attribute is not {@link #isMandatory() mandatory}.
	 */
	public final double getMandatory(final Item item)
	{
		if(optional)
			throw new RuntimeException("attribute " + toString() + " is not mandatory");
		
		return get(item).doubleValue();
	}
	
	public final void set(final Item item, final double value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Double(value));
		}
		catch(MandatoryViolationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public final SetValue map(final double value)
	{
		return new SetValue(this, new Double(value));
	}
	
	public final EqualCondition equal(final double value)
	{
		return new EqualCondition<Double>(this, new Double(value));
	}
	
	public final NotEqualCondition notEqual(final double value)
	{
		return new NotEqualCondition<Double>(this, new Double(value));
	}
	
	public final LessCondition less(final double value)
	{
		return new LessCondition<Double>(this, new Double(value));
	}
	
	public final LessEqualCondition lessOrEqual(final double value)
	{
		return new LessEqualCondition<Double>(this, new Double(value));
	}
	
	public final GreaterCondition greater(final double value)
	{
		return new GreaterCondition<Double>(this, new Double(value));
	}
	
	public final GreaterEqualCondition greaterOrEqual(final double value)
	{
		return new GreaterEqualCondition<Double>(this, new Double(value));
	}
	
}

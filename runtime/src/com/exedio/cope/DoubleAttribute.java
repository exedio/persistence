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

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class DoubleAttribute extends FunctionAttribute
{

	private DoubleAttribute(final boolean readOnly, final boolean mandatory, final boolean unique)
	{
		super(readOnly, mandatory, unique, Double.class);
	}
	
	public DoubleAttribute(final Option option)
	{
		this(option.isFinal, option.mandatory, option.unique);
	}

	public FunctionAttribute copyFunctionAttribute()
	{
		return new DoubleAttribute(isfinal, mandatory, implicitUniqueConstraint!=null);
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new DoubleColumn(table, name, notNull, 30);
	}

	Object get(final Row row)
	{
		return (Double)row.get(getColumn());
	}
	
	void set(final Row row, final Object surface)
	{
		row.put(getColumn(), (Double)surface);
	}
	
	public final Double get(final Item item)
	{
		return (Double)getObject(item);
	}
	
	/**
	 * @throws RuntimeException if this attribute is not {@link #isMandatory() mandatory}.
	 */
	public final double getMandatory(final Item item)
	{
		if(!mandatory)
			throw new RuntimeException("attribute " + toString() + " is not mandatory");
		
		return get(item).doubleValue();
	}
	
	public final void set(final Item item, final Double value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			FinalViolationException
	{
		try
		{
			item.set(this, value);
		}
		catch(LengthViolationException e)
		{
			throw new RuntimeException(e);
		}
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
	
	public final AttributeValue map(final Double value)
	{
		return new AttributeValue(this, value);
	}
	
	public final AttributeValue map(final double value)
	{
		return new AttributeValue(this, new Double(value));
	}
	
	public final EqualCondition equal(final Double value)
	{
		return new EqualCondition(this, value);
	}
	
	public final EqualCondition equal(final double value)
	{
		return new EqualCondition(this, new Double(value));
	}
	
	public final NotEqualCondition notEqual(final Double value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final double value)
	{
		return new NotEqualCondition(this, new Double(value));
	}
	
	public final LessCondition less(final double value)
	{
		return new LessCondition(this, new Double(value));
	}
	
	public final LessEqualCondition lessOrEqual(final double value)
	{
		return new LessEqualCondition(this, new Double(value));
	}
	
	public final GreaterCondition greater(final double value)
	{
		return new GreaterCondition(this, new Double(value));
	}
	
	public final GreaterEqualCondition greaterOrEqual(final double value)
	{
		return new GreaterEqualCondition(this, new Double(value));
	}
	
}

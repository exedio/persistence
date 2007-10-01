/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.search.SumAggregate;

public final class DoubleField extends FunctionField<Double>
{

	private DoubleField(final boolean isfinal, final boolean optional, final boolean unique, final Double defaultConstant)
	{
		super(isfinal, optional, unique, Double.class, defaultConstant);
		checkDefaultValue();
	}
	
	public DoubleField()
	{
		this(false, false, false, null);
	}

	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public DoubleField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}

	@Override
	public DoubleField copy()
	{
		return new DoubleField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public DoubleField toFinal()
	{
		return new DoubleField(true, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public DoubleField optional()
	{
		return new DoubleField(isfinal, true, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public DoubleField unique()
	{
		return new DoubleField(isfinal, optional, true, defaultConstant);
	}
	
	public DoubleField defaultTo(final Double defaultConstant)
	{
		return new DoubleField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public Class getWrapperSetterType()
	{
		return optional ? Double.class : double.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DoubleColumn(table, name, optional);
	}

	@Override
	Double get(final Row row)
	{
		return (Double)row.get(getColumn());
	}
	
	@Override
	void set(final Row row, final Double surface)
	{
		row.put(getColumn(), surface);
	}
	
	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	public final double getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
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
	
	// convenience methods for conditions and views ---------------------------------

	public final SumAggregate<Double> sum()
	{
		return new SumAggregate<Double>(this);
	}
}

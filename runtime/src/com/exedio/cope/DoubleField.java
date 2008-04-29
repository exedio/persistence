/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Set;

import com.exedio.cope.search.SumAggregate;

public final class DoubleField extends FunctionField<Double>
{
	private static final double MIN = -Double.MAX_VALUE;
	private static final double MAX = Double.MAX_VALUE;
	
	private final double minimum;
	private final double maximum;

	private DoubleField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Double defaultConstant,
			final double minimum, final double maximum)
	{
		super(isfinal, optional, unique, Double.class, defaultConstant);
		this.minimum = minimum;
		this.maximum = maximum;
		
		if(Double.isInfinite(minimum))
			throw new IllegalArgumentException("minimum must not be infinite, but was " + minimum + '.');
		if(Double.isInfinite(maximum))
			throw new IllegalArgumentException("maximum must not be infinite, but was " + maximum + '.');
		if(Double.isNaN(minimum))
			throw new IllegalArgumentException("minimum must not be NaN, but was " + minimum + '.');
		if(Double.isNaN(maximum))
			throw new IllegalArgumentException("maximum must not be NaN, but was " + maximum + '.');
		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');
		
		checkDefaultValue();
	}
	
	public DoubleField()
	{
		this(false, false, false, null, MIN, MAX);
	}

	@Override
	public DoubleField copy()
	{
		return new DoubleField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}
	
	@Override
	public DoubleField toFinal()
	{
		return new DoubleField(true, optional, unique, defaultConstant, minimum, maximum);
	}
	
	@Override
	public DoubleField optional()
	{
		return new DoubleField(isfinal, true, unique, defaultConstant, minimum, maximum);
	}
	
	@Override
	public DoubleField unique()
	{
		return new DoubleField(isfinal, optional, true, defaultConstant, minimum, maximum);
	}
	
	public DoubleField defaultTo(final Double defaultConstant)
	{
		return new DoubleField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}
	
	public DoubleField range(final double minimum, final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, defaultConstant, minimum, maximum);
	}
	
	public DoubleField min(final double minimum)
	{
		return new DoubleField(isfinal, optional, unique, defaultConstant, minimum, MAX);
	}
	
	public DoubleField max(final double maximum)
	{
		return new DoubleField(isfinal, optional, unique, defaultConstant, MIN, maximum);
	}
	
	public double getMinimum()
	{
		return minimum;
	}
	
	public double getMaximum()
	{
		return maximum;
	}
	
	@Override
	public Set<Class<? extends Throwable>> getSetterExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getSetterExceptions();
		if(minimum!=MIN || maximum!=MAX)
			result.add(DoubleRangeViolationException.class);
		return result;
	}
	
	@Override
	public Class getInitialType()
	{
		return optional ? Double.class : double.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DoubleColumn(table, this, name, optional, minimum, maximum);
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
	
	@Override
	void checkNotNullValue(final Double value, final Item exceptionItem) throws RangeViolationException
	{
		final double valuePrimitive = value.doubleValue();
		
		// TODO better exceptions
		if(Double.isInfinite(valuePrimitive))
			throw new RuntimeException(getID() + '#' + valuePrimitive);
		if(Double.isNaN(valuePrimitive))
			throw new RuntimeException(getID() + '#' + valuePrimitive);
		
		if(valuePrimitive<minimum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new DoubleRangeViolationException(this, exceptionItem, value, false, maximum);
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
	
	@Override
	public Condition equal(final Double value)
	{
		if(value!=null)
		{
			final double valuePrimitive = value.doubleValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.FALSE;
			else
				return super.equal(value);
		}
		else
			return super.equal(value);
	}
	
	@Override
	public Condition notEqual(final Double value)
	{
		if(value!=null)
		{
			final double valuePrimitive = value.doubleValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.TRUE;
			else
				return super.notEqual(value);
		}
		else
			return super.notEqual(value);
	}
	// TODO the same for less, lessEqual, greater, greaterEqual
	
	// convenience methods for conditions and views ---------------------------------

	public final SumAggregate<Double> sum()
	{
		return new SumAggregate<Double>(this);
	}
	
	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public DoubleField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, MIN, MAX);
	}
}

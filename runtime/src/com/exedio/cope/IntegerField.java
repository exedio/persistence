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

import java.util.Set;

import com.exedio.cope.function.PlusView;
import com.exedio.cope.search.SumAggregate;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a integer.
 *
 * @author Ralf Wiebicke
 */
public final class IntegerField extends FunctionField<Integer> implements IntegerFunction
{
	final int minimum;
	final int maximum;

	private IntegerField(final boolean isfinal, final boolean optional, final boolean unique, final Integer defaultConstant, final int minimum, final int maximum)
	{
		super(isfinal, optional, unique, Integer.class, defaultConstant);
		this.minimum = minimum;
		this.maximum = maximum;

		if(minimum>=maximum)
			throw new IllegalArgumentException("maximum must be greater than mimimum, but was " + maximum + " and " + minimum + '.');

		checkDefaultValue();
	}
	
	/**
	 * Creates a new mandatory <tt>IntegerField</tt>.
	 */
	public IntegerField()
	{
		this(false, false, false, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public IntegerField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	@Override
	public IntegerField copy()
	{
		return new IntegerField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimum, maximum);
	}
	
	@Override
	public IntegerField toFinal()
	{
		return new IntegerField(true, optional, implicitUniqueConstraint!=null, defaultConstant, minimum, maximum);
	}
	
	@Override
	public IntegerField optional()
	{
		return new IntegerField(isfinal, true, implicitUniqueConstraint!=null, defaultConstant, minimum, maximum);
	}

	@Override
	public IntegerField unique()
	{
		return new IntegerField(isfinal, optional, true, defaultConstant, minimum, maximum);
	}
	
	public IntegerField defaultTo(final Integer defaultConstant)
	{
		return new IntegerField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimum, maximum);
	}
	
	public IntegerField range(final int minimum, final int maximum)
	{
		return new IntegerField(this.isfinal, this.optional, this.implicitUniqueConstraint!=null, this.defaultConstant, minimum, maximum);
	}
	
	public IntegerField min(final int minimum)
	{
		return new IntegerField(this.isfinal, this.optional, this.implicitUniqueConstraint!=null, this.defaultConstant, minimum, Integer.MAX_VALUE);
	}
	
	public IntegerField max(final int maximum)
	{
		return new IntegerField(this.isfinal, this.optional, this.implicitUniqueConstraint!=null, this.defaultConstant, Integer.MIN_VALUE, maximum);
	}
	
	public final int getMinimum()
	{
		return minimum;
	}
	
	public final int getMaximum()
	{
		return maximum;
	}
	
	@Override
	public Set<Class> getSetterExceptions()
	{
		final Set<Class> result = super.getSetterExceptions();
		if(minimum!=Integer.MIN_VALUE || maximum!=Integer.MAX_VALUE)
			result.add(RangeViolationException.class);
		return result;
	}
	
	@Override
	Class getWrapperValueClass()
	{
		return optional ? Integer.class : int.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, minimum, maximum, false);
	}
	
	@Override
	Integer get(final Row row)
	{
		return (Integer)row.get(getColumn());
	}
	
	@Override
	void set(final Row row, final Integer surface)
	{
		row.put(getColumn(), surface);
	}
	
	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	public final int getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
		return get(item).intValue();
	}
	
	public final void set(final Item item, final int value)
		throws
			UniqueViolationException,
			FinalViolationException,
			RangeViolationException
	{
		set(item, Integer.valueOf(value));
	}

	@Override
	void checkNotNullValue(final Integer value, final Item exceptionItem) throws RangeViolationException
	{
		final int valuePrimitive = value.intValue();
		if(valuePrimitive<minimum)
			throw new RangeViolationException(this, exceptionItem, value, true, minimum);
		if(valuePrimitive>maximum)
			throw new RangeViolationException(this, exceptionItem, value, false, maximum);
	}
	
	@Override
	public Condition equal(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
			if(valuePrimitive<minimum || valuePrimitive>maximum)
				return Condition.FALSE;
			else
				return super.equal(value);
		}
		else
			return super.equal(value);
	}
	
	@Override
	public Condition notEqual(final Integer value)
	{
		if(value!=null)
		{
			final int valuePrimitive = value.intValue();
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

	@Override
	public BindIntegerFunction bind(final Join join)
	{
		return new BindIntegerFunction(this, join);
	}
	
	public final PlusView plus(final IntegerFunction other)
	{
		return new PlusView(new IntegerFunction[]{this, other});
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction)}.
	 */
	@Deprecated
	public final PlusView sum(final IntegerFunction other)
	{
		return plus(other);
	}
	
	public final SumAggregate<Integer> sum()
	{
		return new SumAggregate<Integer>(this);
	}
}

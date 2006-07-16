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

import com.exedio.cope.function.PlusView;
import com.exedio.cope.search.SumAggregate;

public final class IntegerAttribute extends FunctionAttribute<Integer> implements IntegerFunction
{

	private IntegerAttribute(final boolean isfinal, final boolean optional, final boolean unique, final Integer defaultConstant)
	{
		super(isfinal, optional, unique, Integer.class, defaultConstant);
		checkDefaultValue();
	}
	
	public IntegerAttribute()
	{
		this(Item.MANDATORY);
	}
	
	public IntegerAttribute(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
	
	@Override
	public IntegerAttribute copyFunctionAttribute()
	{
		return new IntegerAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}

	public IntegerAttribute defaultTo(final Integer defaultConstant)
	{
		return new IntegerAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
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
	 * @throws RuntimeException if this attribute is not {@link #isMandatory() mandatory}.
	 */
	public final int getMandatory(final Item item)
	{
		if(optional)
			throw new RuntimeException("attribute " + toString() + " is not mandatory");
		
		return get(item).intValue();
	}
	
	public final void set(final Item item, final int value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, Integer.valueOf(value));
	}

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

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

import com.exedio.cope.search.SumAggregate;

public final class LongField extends FunctionField<Long>
{

	private LongField(final boolean isfinal, final boolean optional, final boolean unique, final Long defaultConstant)
	{
		super(isfinal, optional, unique, Long.class, defaultConstant);
		checkDefaultValue();
	}
	
	public LongField()
	{
		this(Item.MANDATORY);
	}
	
	public LongField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
	
	@Override
	public LongField copyFunctionField()
	{
		return new LongField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	public LongField defaultTo(final Long defaultConstant)
	{
		return new LongField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, Long.MIN_VALUE, Long.MAX_VALUE, true);
	}
	
	@Override
	Long get(final Row row)
	{
		return (Long)row.get(getColumn());
	}
		
	@Override
	void set(final Row row, final Long surface)
	{
		row.put(getColumn(), surface);
	}
	
	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	public final long getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
		return get(item).longValue();
	}
	
	public final void set(final Item item, final long value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, Long.valueOf(value));
	}
	
	// convenience methods for conditions and views ---------------------------------

	public final SumAggregate<Long> sum()
	{
		return new SumAggregate<Long>(this);
	}
}

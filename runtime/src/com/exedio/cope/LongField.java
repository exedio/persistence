/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.LinkedList;
import java.util.List;

import com.exedio.cope.instrument.Wrapped;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperSuppressor;

public final class LongField extends NumberField<Long>
{
	private static final long serialVersionUID = 1l;

	private LongField(final boolean isfinal, final boolean optional, final boolean unique, final Long defaultConstant)
	{
		super(isfinal, optional, unique, Long.class, defaultConstant);
		checkDefaultConstant();
	}

	public LongField()
	{
		this(false, false, false, null);
	}

	@Override
	public LongField copy()
	{
		return new LongField(isfinal, optional, unique, defaultConstant);
	}

	@Override
	public LongField toFinal()
	{
		return new LongField(true, optional, unique, defaultConstant);
	}

	@Override
	public LongField optional()
	{
		return new LongField(isfinal, true, unique, defaultConstant);
	}

	@Override
	public LongField unique()
	{
		return new LongField(isfinal, optional, true, defaultConstant);
	}

	@Override
	public LongField nonUnique()
	{
		return new LongField(isfinal, optional, false, defaultConstant);
	}

	@Override
	public LongField noDefault()
	{
		return new LongField(isfinal, optional, unique, null);
	}

	@Override
	public LongField defaultTo(final Long defaultConstant)
	{
		return new LongField(isfinal, optional, unique, defaultConstant);
	}

	@Override
	public Class getInitialType()
	{
		return optional ? Long.class : long.class;
	}

	public SelectType<Long> getValueType()
	{
		return SimpleSelectType.LONG;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final LinkedList<Wrapper> result = new LinkedList<Wrapper>(
				Wrapper.makeByReflection(LongField.class, this, super.getWrappers()));
		for(final Wrapper wrapper : result)
		{
			if(wrapper!=null && "getMandatory".equals(wrapper.getName()))
			{
				result.remove(wrapper);
				result.add(0, wrapper);
				break;
			}
		}
		return result;
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, this, name, false, optional, Long.MIN_VALUE, Long.MAX_VALUE, true);
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
	@Wrapped(pos=10, comment="Returns the value of {0}.", name="get{0}", suppressor=WrapperSuppressorOptional.class)
	public final long getMandatory(final Item item)
	{
		return getMandatoryObject(item).longValue();
	}

	private final class WrapperSuppressorOptional implements WrapperSuppressor
	{
		@Override public boolean isSuppressed()
		{
			return !LongField.this.isMandatory();
		}
	}

	public final void set(final Item item, final long value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, Long.valueOf(value));
	}
}

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


public final class BooleanField extends FunctionField<Boolean>
{
	static final int[] ALLOWED_VALUES = {0, 1};

	private BooleanField(final boolean isfinal, final boolean optional, final boolean unique, final Boolean defaultConstant)
	{
		super(isfinal, optional, unique, Boolean.class, defaultConstant);
		checkDefaultValue();
	}
	
	public BooleanField()
	{
		this(false, false, false, null);
	}
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public BooleanField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
	
	@Override
	public BooleanField copy()
	{
		return new BooleanField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public BooleanField toFinal()
	{
		return new BooleanField(true, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public BooleanField optional()
	{
		return new BooleanField(isfinal, true, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public BooleanField unique()
	{
		return new BooleanField(isfinal, optional, true, defaultConstant);
	}
	
	public BooleanField defaultTo(final Boolean defaultConstant)
	{
		return new BooleanField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public Class getWrapperSetterType()
	{
		return optional ? Boolean.class : boolean.class;
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, ALLOWED_VALUES);
	}
	
	@Override
	Boolean get(final Row row)
	{
		final Object cell = row.get(getColumn());
		if(cell==null)
			return null;
		else
		{
			switch(((Integer)cell).intValue())
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cacheToSurface:"+cell);
			}
		}
	}
	
	static final Integer FALSE = Integer.valueOf(0);
	static final Integer TRUE = Integer.valueOf(1);
		
	@Override
	void set(final Row row, final Boolean surface)
	{
		row.put(getColumn(), surface==null ? null : surface.booleanValue() ? TRUE : FALSE);
	}
	
	/**
	 * @throws IllegalArgumentException if this field is not {@link #isMandatory() mandatory}.
	 */
	public final boolean getMandatory(final Item item)
	{
		if(optional)
			throw new IllegalArgumentException("field " + toString() + " is not mandatory");
		
		return get(item).booleanValue();
	}
	
	public final void set(final Item item, final boolean value)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		set(item, new Boolean(value));
	}
}

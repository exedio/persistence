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

import com.exedio.cope.util.Day;

public final class DayField extends FunctionField<Day>
{
	
	private DayField(final boolean isfinal, final boolean optional, final boolean unique, final Day defaultConstant)
	{
		super(isfinal, optional, unique, Day.class, defaultConstant);
		checkDefaultValue();
	}
	
	public DayField()
	{
		this(Item.MANDATORY);
	}
	
	public DayField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
	
	@Override
	public DayField copyFunctionField()
	{
		return new DayField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public DayField toFinal()
	{
		return new DayField(true, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	public DayField unique()
	{
		return new DayField(isfinal, optional, true, defaultConstant);
	}
	
	public DayField defaultTo(final Day defaultConstant)
	{
		return new DayField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DayColumn(table, name, optional);
	}
	
	@Override
	Day get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : DayColumn.getDay(((Integer)cell).intValue());
	}
		
	@Override
	void set(final Row row, final Day surface)
	{
		row.put(getColumn(), surface==null ? null : Integer.valueOf(DayColumn.getTransientNumber(surface)));
	}
	
}

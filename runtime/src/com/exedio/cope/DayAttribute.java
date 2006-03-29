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

import com.exedio.cope.util.Day;

public final class DayAttribute extends FunctionAttribute<Day>
{
	
	private DayAttribute(final boolean isfinal, final boolean mandatory, final boolean unique)
	{
		super(isfinal, mandatory, unique);
	}
	
	public DayAttribute(final Option option)
	{
		this(option.isFinal, option.mandatory, option.unique);
	}
	
	public FunctionAttribute copyFunctionAttribute()
	{
		return new DayAttribute(isfinal, mandatory, implicitUniqueConstraint!=null);
	}
	
	@Override
	Class initialize(final java.lang.reflect.Type genericType)
	{
		return Day.class;
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new DayColumn(table, name, notNull);
	}
	
	Day get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : DayColumn.getDay(((Integer)cell).intValue());
	}
		
	void set(final Row row, final Day surface)
	{
		row.put(getColumn(), surface==null ? null : Integer.valueOf(DayColumn.getTransientNumber(surface)));
	}
	
}

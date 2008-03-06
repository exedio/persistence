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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.instrument.Wrapper;
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
		this(false, false, false, null);
	}
	
	@Override
	public DayField copy()
	{
		return new DayField(isfinal, optional, unique, defaultConstant);
	}
	
	@Override
	public DayField toFinal()
	{
		return new DayField(true, optional, unique, defaultConstant);
	}
	
	@Override
	public DayField optional()
	{
		return new DayField(isfinal, true, unique, defaultConstant);
	}
	
	@Override
	public DayField unique()
	{
		return new DayField(isfinal, optional, true, defaultConstant);
	}
	
	public DayField defaultTo(final Day defaultConstant)
	{
		return new DayField(isfinal, optional, unique, defaultConstant);
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		if(!isfinal)
		{
			final Set<Class<? extends Throwable>> exceptions = getSetterExceptions();
			exceptions.remove(MandatoryViolationException.class); // cannot set null
			
			result.add(
				new Wrapper("touch").
				addComment("Sets today for the date field {0}.").
				addThrows(exceptions));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new DayColumn(table, this, name, optional);
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
	
	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
	 */
	public void touch(final Item item)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Day()); // TODO: make a more efficient implementation
		}
		catch(MandatoryViolationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public DayField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
}

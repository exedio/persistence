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

import java.util.Date;

public final class DateAttribute extends FunctionAttribute<Date>
{

	private DateAttribute(final boolean isfinal, final boolean optional, final boolean unique, final Date defaultValue)
	{
		super(isfinal, optional, unique, Date.class, defaultValue);
		checkDefaultValue();
	}
	
	public DateAttribute(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null);
	}
	
	public FunctionAttribute<Date> copyFunctionAttribute()
	{
		return new DateAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultValue);
	}
	
	public DateAttribute defaultTo(final Date defaultValue)
	{
		return new DateAttribute(isfinal, optional, implicitUniqueConstraint!=null, defaultValue);
	}
	
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final boolean useLong =
			getType().getModel().getProperties().getDatabaseDontSupportNativeDate() ||
			!(getType().getModel().getDatabase().getDateTimestampType()!=null);
		
		return
				useLong
				? (Column)new IntegerColumn(table, name, optional, 20, true, null)
				: (Column)new TimestampColumn(table, name, optional);
	}
	
	Date get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}
		
	void set(final Row row, final Date surface)
	{
		row.put(getColumn(), surface==null ? null : Long.valueOf(surface.getTime()));
	}
	
	/**
	 * @throws FinalViolationException
	 *         if this attribute is {@link #isFinal() final}.
	 */
	public final void touch(final Item item)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Date()); // TODO: make a more efficient implementation
		}
		catch(MandatoryViolationException e)
		{
			throw new RuntimeException(e);
		}
	}

}

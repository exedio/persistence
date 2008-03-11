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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.exedio.cope.util.Day;

final class DayColumn extends Column
{
	static final int JDBC_TYPE = Types.DATE;
	
	DayColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional)
	{
		super(table, field, id, false, optional, JDBC_TYPE);
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getDayType();
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
	{
		return null;
	}
	
	static int getTransientNumber(final Day day)
	{
		return getTransientNumber(day.getYear(), day.getMonth(), day.getDay());
	}
	
	private static int getTransientNumber(final java.sql.Date day)
	{
		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(day);
		return getTransientNumber(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
	}
	
	private static int getTransientNumber(final int year, final int month, final int day)
	{
		if(year<1000 || year>9999 || month<1 || month>12 || day<1 || day>31)
			throw new RuntimeException("wrong day: " + year + '/' + month +  '/' + day );

		return (((year*12) + (month-1))*31) + (day-1);
	}
	
	static Day getDay(final int transientNumber)
	{
		final int m = transientNumber / 31;
		return new Day(m/12, (m%12)+1, (transientNumber%31)+1);
	}
	
	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final java.sql.Date loadedDate = resultSet.getDate(columnIndex);
		//System.out.println("DayColumn.load "+columnIndex+" "+loadedDate);
		row.put(this, (loadedDate!=null) ? Integer.valueOf(getTransientNumber(loadedDate)) : null);
	}
	
	@Override
	final String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			final Day day = getDay(((Integer)cache).intValue());
			return "{d '"+day.getYear()+'-'+day.getMonth()+'-'+day.getDay()+"'}";
		}
	}
	
	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		return (cache==null) ? null : new Date(getDay(((Integer)cache).intValue()).getTimeInMillis());
	}

	@Override
	Object getCheckValue()
	{
		return Integer.valueOf(DayColumn.getTransientNumber(new Day(2005, 9, 26)));
	}
}

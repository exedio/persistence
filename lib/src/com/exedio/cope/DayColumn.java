/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.exedio.cope.util.Day;

final class DayColumn extends Column
{
	static final int JDBC_TYPE = Types.DATE;
	
	DayColumn(final Table table, final String id, final boolean notNull)
	{
		super(table, id, false, notNull, JDBC_TYPE);
	}
	
	final String getDatabaseType()
	{
		return table.database.getDayType(); 
	}

	final String getCheckConstraintIfNotNull()
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
	
	final void load(final ResultSet resultSet, final int columnIndex, final PersistentState state)
			throws SQLException
	{
		final java.sql.Date loadedDate = resultSet.getDate(columnIndex);
		//System.out.println("DayColumn.load "+columnIndex+" "+loadedDate);
		if(loadedDate!=null)
			state.load(this, getTransientNumber(loadedDate));
	}
	
	final Object load(final ResultSet resultSet, final int columnIndex)
			throws SQLException
	{
		final java.sql.Date loadedDate = resultSet.getDate(columnIndex);
		//System.out.println("DayColumn.load "+columnIndex+" "+loadedDate);
		return (loadedDate!=null) ? new Integer(getTransientNumber(loadedDate)) : null;
	}

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
	
}

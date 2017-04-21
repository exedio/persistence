/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

final class DayColumn extends Column
{
	DayColumn(
			final Table table,
			final String id,
			final boolean optional)
	{
		super(table, id, false, false, optional);
	}

	@Override
	String getDatabaseType()
	{
		return table.database.dialect.getDayType();
	}

	static int getTransientNumber(final Day day)
	{
		return (((day.getYear()*12) + (day.getMonth()-1))*31) + (day.getDay()-1);
	}

	static Day getDay(final int transientNumber)
	{
		final int m = transientNumber / 31;
		return new Day(m/12, (m%12)+1, (transientNumber%31)+1);
	}

	@Override
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final java.sql.Date loadedDate = resultSet.getDate(columnIndex);
		//System.out.println("DayColumn.load "+columnIndex+" "+loadedDate);
		row.put(this, (loadedDate!=null) ? getTransientNumber(DayField.unmarshal(loadedDate)) : null);
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			final Day day = getDay((Integer)cache);
			final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			nf.setMinimumIntegerDigits(2);
			return "{d '"+day.getYear()+'-'+nf.format(day.getMonth())+'-'+nf.format(day.getDay())+"'}";
		}
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		return (cache==null) ? null : DayField.marshal(getDay((Integer)cache));
	}
}

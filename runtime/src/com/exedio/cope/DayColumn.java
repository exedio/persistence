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

import static com.exedio.cope.Marshallers.unmarshalDay;

import com.exedio.cope.util.Day;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DayColumn extends Column
{
	private final Day minimum;
	private final Day maximum;

	DayColumn(
			final Table table,
			final String id,
			final boolean optional,
			final Day minimum,
			final Day maximum)
	{
		super(table, id, false, Kind.nonPrimaryKey(optional));
		this.minimum = minimum;
		this.maximum = maximum;
	}

	@Override
	String getDatabaseType()
	{
		return table.database.dialect.getDayType();
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		final Dialect dialect = table.database.dialect;
		newCheck(dsmf, "MN", quotedID + ">=" + dialect.toLiteral(minimum));
		newCheck(dsmf, "MX", quotedID + "<=" + dialect.toLiteral(maximum));
	}

	static int getTransientNumber(final Day day)
	{
		return (((day.getYear()*12) + (day.getMonthValue()-1))*31) + (day.getDayOfMonth()-1);
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
		final String loadedDate = resultSet.getString(columnIndex);
		//System.out.println("DayColumn.load "+columnIndex+" "+loadedDate);
		row.put(this, (loadedDate!=null) ? getTransientNumber(unmarshalDay(loadedDate)) : null);
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return "{d '" + getDay((Integer)cache).toLocalDate() + "'}";
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		return (cache==null) ? null : table.database.dialect.marshalDay(getDay((Integer)cache));
	}
}

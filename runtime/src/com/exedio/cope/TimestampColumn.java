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

import com.exedio.cope.util.TimeZoneStrict;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

final class TimestampColumn extends Column
{
	TimestampColumn(
			final Table table,
			final String id,
			final boolean optional)
	{
		super(table, id, false, false, optional);
		assert table.database.dialect.getDateTimestampType()!=null;
	}

	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getDateTimestampType();
	}

	@Override
	final String getCheckConstraintIfNotNull()
	{
		return null;
	}

	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Timestamp ts = resultSet.getTimestamp(columnIndex, newGMTCalendar());

		final Long rowValue;
		if(ts==null)
		{
			rowValue = null;
		}
		else
		{
			assert noNanos(ts) : ts;
			rowValue = Long.valueOf(ts.getTime());
		}

		row.put(this, rowValue);
	}

	@Override
	final String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return newLiteralFormat().format(new Date(((Long)cache).longValue()));
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		if(cache==null)
			return null;

		final Timestamp ts = new Timestamp(((Long)cache).longValue());
		assert noNanos(ts) : ts;
		return ts;
	}

	private static boolean noNanos(final Timestamp ts)
	{
		return (ts.getNanos()%1000000)==0;
	}

	/**
	 * Don't use a static instance,
	 * since then access must be synchronized
	 */
	static final SimpleDateFormat newLiteralFormat()
	{
		final SimpleDateFormat result =
				new SimpleDateFormat("{'ts' ''yyyy-MM-dd HH:mm:ss.SSS''}");
		result.setTimeZone(GMT);
		result.setLenient(false);
		return result;
	}

	static final Calendar newGMTCalendar()
	{
		return new GregorianCalendar(GMT, Locale.ENGLISH);
	}

	private static final TimeZone GMT = TimeZoneStrict.getTimeZone("GMT");
}

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

import static com.exedio.cope.DateField.Precision.MINUTE;
import static com.exedio.cope.DateField.Precision.SECOND;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.util.TimeZoneStrict;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

final class TimestampColumn extends Column
{
	private final long minimum;
	private final long maximum;
	private final Precision precision;

	TimestampColumn(
			final Table table,
			final String id,
			final boolean optional,
			final long minimum,
			final long maximum,
			final Precision precision)
	{
		super(table, id, false, false, optional);
		this.minimum = minimum;
		this.maximum = maximum;
		this.precision = precision;
	}

	@Override
	String getDatabaseType()
	{
		return table.database.dialect.getDateTimestampType();
	}

	@Override
	@SuppressWarnings({"fallthrough", "RedundantSuppression"}) // RedundantSuppression needed because fallthrough is also obeyed by javac
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		final Dialect dialect = table.database.dialect;
		newCheck(dsmf, "MN", quotedID + ">=" + dialect.toLiteral(new Date(minimum)));
		newCheck(dsmf, "MX", quotedID + "<=" + dialect.toLiteral(new Date(maximum)));

		switch(precision)
		{
			case HOUR:
				newCheck(dsmf, "PM",
						dialect.getDateExtract(quotedID, MINUTE) + "=0");
				// fall through

			case MINUTE:
			case SECOND:
				newCheck(dsmf, "PS",
						dialect.getDateTimestampPrecisionMinuteSecond(precision==SECOND, quotedID));
				// fall through

			case MILLI:
				break; // nothing

			default:
				throw new RuntimeException("" + precision);
		}
	}

	@Override
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
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
			rowValue = ts.getTime();
		}

		row.put(this, rowValue);
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return table.database.dialect.toLiteral(new Date((Long)cache));
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		if(cache==null)
			return null;

		final Timestamp ts = new Timestamp((Long)cache);
		assert noNanos(ts) : ts;
		return ts;
	}

	private static boolean noNanos(final Timestamp ts)
	{
		return (ts.getNanos()%1000000)==0;
	}

	static Calendar newGMTCalendar()
	{
		return new GregorianCalendar(GMT, Locale.ENGLISH);
	}

	private static final TimeZone GMT = TimeZoneStrict.getTimeZone("GMT");
}

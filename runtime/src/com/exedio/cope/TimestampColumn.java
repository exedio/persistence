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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

final class TimestampColumn extends Column
{
	TimestampColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional)
	{
		super(table, field, id, false, optional);
		assert table.database.dialect.getDateTimestampType()!=null;
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getDateTimestampType();
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
	{
		return null;
	}
	
	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Timestamp ts = resultSet.getTimestamp(columnIndex);
		//System.out.println("TimestampColumn.load "+columnIndex+" "+ts);
		row.put(this, (ts!=null) ? Long.valueOf(ts.getTime()) : null);
	}
	
	@Override
	final String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			// Don't use a static instance,
			// since then access must be synchronized
			final SimpleDateFormat df = new SimpleDateFormat("{'ts' ''yyyy-MM-dd HH:mm:ss.SSS''}");

			return df.format(new Date(((Long)cache).longValue()));
		}
	}
	
	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		return (cache==null) ? null : new Timestamp(((Long)cache).longValue());
	}

	@Override
	Object getCheckValue()
	{
		return Long.valueOf(System.currentTimeMillis());
	}
}

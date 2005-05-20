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
import java.text.SimpleDateFormat;
import java.util.Date;

final class TimestampColumn extends Column
{
	static final int JDBC_TYPE = Types.TIMESTAMP;
	
	TimestampColumn(final Table table, final String id, final boolean notNull)
	{
		super(table, id, false, notNull, JDBC_TYPE);
	}
	
	final String getDatabaseType()
	{
		return ((DatabaseTimestampCapable)table.database).getDateTimestampType(); 
	}

	final String getCheckConstraintIfNotNull()
	{
		return null;
	}
	
	private static final boolean skipWorkAround =
		!System.getProperty("java.runtime.version").startsWith("1.3");

	/**
	 * Returns what should be returned by
	 * {@link Date#getTime()}.
	 * For both oracle and hsqldb the dates returned by ResultSets
	 * return zero milliseconds with getTime().
	 * 
	 * This method provides a workaround.
	 * 
	 * Probably has to be adjusted for different jdbc drivers
	 * and jdk versions. 
	 */
	private static final long getTime(final Date date)
	{
		//System.out.println("TimeColumn.getTime");
		
		long result = date.getTime();
		
		if(skipWorkAround)
			return result;
	
		//System.out.println("          result "+result+" before");
		if((result%1000)!=0)
			throw new RuntimeException(String.valueOf(result));
		
		//final Calendar cal = Calendar.getInstance();
		//cal.setTime(date);
		//System.out.println("          cal "+cal);
		
		final String dateString = date.toString();
		//System.out.println("          toString "+dateString);
		final int dotPos = dateString.lastIndexOf('.');
		final String millisString = dateString.substring(dotPos+1);
		final int millis = Integer.parseInt(millisString);
		result += millis;
		
		//System.out.println("          result "+result);
		return result;
	}
	
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedTimestamp = resultSet.getObject(columnIndex);
		//System.out.println("TimestampColumn.load "+columnIndex+" "+loadedTimestamp);
		if(loadedTimestamp!=null)
		{
			row.load(this, getTime(((Date)loadedTimestamp)));
		}
	}
	
	final Object load(final ResultSet resultSet, final int columnIndex)
			throws SQLException
	{
		final Object loadedTimestamp = resultSet.getObject(columnIndex);
		//System.out.println("TimestampColumn.load "+columnIndex+" "+loadedTimestamp);
		return (loadedTimestamp!=null) ? new Long(getTime(((Date)loadedTimestamp))) : null;
	}

	final Object cacheToDatabase(final Object cache)
	{
		// Don't use a static instance,
		// since then access must be synchronized
		final SimpleDateFormat df = new SimpleDateFormat("{'ts' ''yyyy-MM-dd HH:mm:ss.S''}");

		if(cache==null)
			return "NULL";
		else
		{
			return df.format(new Date(((Long)cache).longValue()));
		}
	}
	
}

package com.exedio.cope.lib;

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

package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DoubleColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.DOUBLE);

	DoubleColumn(final Table table, final String id, 
					  final boolean notNull, final int precision)
	{
		super(table, id, false, notNull, Database.theInstance.getDoubleType(precision), JDBC_TYPE);
	}
	
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedDouble = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		if(loadedDouble!=null)
		{
			row.load(this, ((Double)loadedDouble).doubleValue());
		}
	}

	final Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Double)cache).toString();
	}
	
}

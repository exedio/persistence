package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class DoubleColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.DOUBLE);
	
	final int precision;

	DoubleColumn(final Table table, final String id, 
					  final boolean notNull, final int precision)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		this.precision = precision;
	}
	
	final String getDatabaseType()
	{
		return table.database.getDoubleType(precision);
	}

	final String getCheckConstraintIfNotNull()
	{
		return null;
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

	final Object load(final ResultSet resultSet, final int columnIndex)
			throws SQLException
	{
		final Object loadedDouble = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		return (loadedDouble!=null) ? (Double)loadedDouble : null;
	}

	final Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Double)cache).toString();
	}
	
}

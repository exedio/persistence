package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

class IntegerColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.INTEGER);

	IntegerColumn(final Type type, final String trimmedName,
					  final boolean notNull, final int precision)
	{
		super(type, trimmedName, notNull, "number(" + precision + ",0)"/* TODO: this is database specific */, JDBC_TYPE);
	}
	
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		if(loadedInteger!=null)
		{
			row.load(this, convertSQLResult(loadedInteger));
		}
	}

	final Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}
	
	static final int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE for Oracle
		// Whether the returned object is an Integer or a BigDecimal,
		// depends on whether OracleStatement.defineColumnType is used or not,
		// so we support both here.
		return (sqlInteger instanceof Integer) ? 
			((Integer)sqlInteger).intValue() :
			((BigDecimal)sqlInteger).intValue();
	}

}

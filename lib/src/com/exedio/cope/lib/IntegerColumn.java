package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

class IntegerColumn extends Column
{
	static final Integer JDBC_TYPE_INT = new Integer(Types.INTEGER);
	static final Integer JDBC_TYPE_LONG = new Integer(Types.BIGINT);
	
	final boolean longInsteadOfInt;

	IntegerColumn(final Type type, final String id,
					  final boolean notNull, final int precision, final boolean longInsteadOfInt)
	{
		super(type, id, notNull, Database.theInstance.getIntegerType(precision), longInsteadOfInt ? JDBC_TYPE_LONG : JDBC_TYPE_INT);
		this.longInsteadOfInt = longInsteadOfInt;
	}
	
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
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
		{
			if(longInsteadOfInt)
				return ((Long)cache).toString();
			else
				return ((Integer)cache).toString();
		}
	}
	
	final long convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE for Oracle
		// Whether the returned object is an Integer or a BigDecimal,
		// depends on whether OracleStatement.defineColumnType is used or not,
		// so we support both here.
		if(sqlInteger instanceof BigDecimal)
			return ((BigDecimal)sqlInteger).intValue();
		else
		{
			if (longInsteadOfInt)
				return ((Long)sqlInteger).longValue();
			else
				return ((Integer)sqlInteger).longValue();
		}
	}

}

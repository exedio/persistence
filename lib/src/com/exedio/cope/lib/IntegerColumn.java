package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public final class IntegerColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.INTEGER);

	// TODO: make a sub class for foreign key columns	 
	final Class targetTypeClass;
	final String integrityConstraintName;

	IntegerColumn(final Type type, final String trimmedName,
					  final boolean notNull, final int precision,
					  final Class targetTypeClass, final String integrityConstraintName)
	{
		super(type, trimmedName, notNull, "number(" + precision + ",0)"/* TODO: this is database specific */, JDBC_TYPE);
		if((targetTypeClass==null)!=(integrityConstraintName==null))
			throw new RuntimeException();
		this.targetTypeClass = targetTypeClass;
		this.integrityConstraintName = integrityConstraintName;
	}
	
	String getForeignTableNameProtected()
	{
		if(targetTypeClass!=null)
			return Type.getType(targetTypeClass.getName()).protectedName;
		else
			return null; 
	}
	
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		if(loadedInteger!=null)
		{
			row.load(this, convertSQLResult(loadedInteger));
		}
	}

	Object cacheToDatabase(final Object cache)
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

package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

class IntegerColumn extends Column
{
	static final int JDBC_TYPE_INT = Types.INTEGER;
	static final int JDBC_TYPE_LONG = Types.BIGINT;
	
	final int precision;
	final boolean longInsteadOfInt;
	final int[] allowedValues;

	IntegerColumn(final Table table, final String id, 
					  final boolean notNull, final int precision,
					  final boolean longInsteadOfInt, final int[] allowedValues)
	{
		super(table, id, false, notNull, longInsteadOfInt ? JDBC_TYPE_LONG : JDBC_TYPE_INT);
		this.precision = precision;
		this.longInsteadOfInt = longInsteadOfInt;
		this.allowedValues = allowedValues;

		if(allowedValues!=null && allowedValues.length<2)
			throw new RuntimeException(id);
	}

	/**
	 * Creates a primary key column.
	 */	
	IntegerColumn(final Table table)
	{
		// IMPLEMENTATION NOTE
		//
		// The following line specifies the column name for the generated
		// primary key of all cope tables to be "this". This prevents
		// name collisions with columns for cope attributes, since "this"
		// is a reserved java keyword, which cannot be used for java attributes.
		//
		// It's a string literal, since the string is not used anywhere else
		// in the framework. In theory, one could specify different column names
		// for the primary key of different tables here, and the framework
		// should work as well. I tried this once (uncomment the line below),
		// and it did pass all tests.
		//
		//super(table, "this"+table.id, true, true, JDBC_TYPE_INT);

		super(table, "this", true, true, JDBC_TYPE_INT);

		this.precision = ItemColumn.SYNTETIC_PRIMARY_KEY_PRECISION;
		this.longInsteadOfInt = false;
		this.allowedValues = null;
	}
	
	final String getDatabaseType()
	{
		return table.database.getIntegerType(precision); 
	}

	final String getCheckConstraintIfNotNull()
	{
		if(allowedValues!=null)
		{
			final StringBuffer bf = new StringBuffer();
			bf.append(protectedID + " IN (");

			for(int j = 0; j<allowedValues.length; j++)
			{
				if(j>0)
					bf.append(',');
				bf.append(allowedValues[j]);
			}
			bf.append(')');
			return bf.toString();
		}
		else
			return null;
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

	final Object load(final ResultSet resultSet, final int columnIndex)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		if(loadedInteger!=null)
		{
			if(loadedInteger instanceof BigDecimal)
			{
				if (longInsteadOfInt)
					return new Long(((BigDecimal)loadedInteger).longValue());
				else
					return new Integer(((BigDecimal)loadedInteger).intValue());
			}
			else
			{
				if (longInsteadOfInt)
					return (Long)loadedInteger;
				else
					return (Integer)loadedInteger;
			}
		}
		else
			return null;
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
	
	private final long convertSQLResult(final Object sqlInteger)
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

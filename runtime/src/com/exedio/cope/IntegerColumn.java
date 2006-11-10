/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

class IntegerColumn extends Column
{
	static final int JDBC_TYPE_INT = Types.INTEGER;
	static final int JDBC_TYPE_LONG = Types.BIGINT;
	
	final long minimum;
	final long maximum;
	final boolean longInsteadOfInt;
	final int[] allowedValues;

	IntegerColumn(final Table table, final String id,
					  final boolean optional,
					  final long minimum, final long maximum,
					  final boolean longInsteadOfInt)
	{
		super(table, id, false, optional, longInsteadOfInt ? JDBC_TYPE_LONG : JDBC_TYPE_INT);
		this.minimum = minimum;
		this.maximum = maximum;
		this.longInsteadOfInt = longInsteadOfInt;
		this.allowedValues = null;

		assert assertMembers();
	}

	IntegerColumn(final Table table, final String id,
					  final boolean optional,
					  final int[] allowedValues)
	{
		super(table, id, false, optional, JDBC_TYPE_INT);
		this.minimum = 0;
		this.maximum = max(allowedValues);
		this.longInsteadOfInt = false;
		this.allowedValues = allowedValues;
		
		if(allowedValues.length<2)
			throw new RuntimeException(id);
		
		// ensure, that allowedValues are unique and ordered
		int current = Integer.MIN_VALUE;
		for(int i = 0; i<allowedValues.length; i++)
		{
			if(current>=allowedValues[i])
				throw new RuntimeException();
			current = allowedValues[i];
		}
		assert assertMembers();
	}

	private static final int max(final int[] ints)
	{
		int result = 0;
		
		for(int i = 0; i<ints.length; i++)
		{
			final int inti = ints[i];
			if(result<inti)
				result = inti;
		}
		
		return result;
	}
	
	/**
	 * Creates a primary key column.
	 */
	IntegerColumn(final Table table)
	{
		// IMPLEMENTATION NOTE
		//
		// In theory, one could specify different column names
		// for the primary key of different tables here, and the framework
		// should work as well. I tried this once (uncomment the line below),
		// and it did pass all tests.
		//
		//super(table, Table.PK_COLUMN_NAME+table.id, true, true, JDBC_TYPE_INT);

		super(table, Table.PK_COLUMN_NAME, true, true, JDBC_TYPE_INT);

		this.minimum = Type.MIN_PK;
		this.maximum = Type.MAX_PK;
		this.longInsteadOfInt = false;
		this.allowedValues = null;
		
		assert assertMembers();
	}

	private boolean assertMembers()
	{
		assert minimum<=maximum;
		assert longInsteadOfInt || minimum>=Integer.MIN_VALUE;
		assert longInsteadOfInt || maximum<=Integer.MAX_VALUE;
		return true;
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.getIntegerType(minimum, maximum);
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
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
		{
			return '(' + protectedID + ">=" + minimum + ") AND (" + protectedID + "<=" + maximum + ')';
		}
	}
	
	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		// TODO: should have small numbers (or even bits) in cache instead of full integers if allowedValues!=null
		row.put(this, (loadedInteger!=null) ? convertSQLResult(loadedInteger) : null);
	}

	@Override
	final String cacheToDatabase(final Object cache)
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
	
	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		return longInsteadOfInt ? (Long)cache : (Object)(Integer)cache;
	}
	
	@Override
	final Object getCheckValue()
	{
		return longInsteadOfInt ? (Object)Long.valueOf(1) : Integer.valueOf(1);
	}
	
	private final Number convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE for Oracle
		// Whether the returned object is an Integer or a BigDecimal,
		// depends on whether OracleStatement.defineColumnType is used or not,
		// so we support both here.
		//
		// IMPLEMENTATION NOTE for MySQL
		// A SumAggregate across an IntegerFunction may return Longs or Doubles,
		// so we support all here.
		if (longInsteadOfInt)
		{
			if(sqlInteger instanceof Long)
				return (Long)sqlInteger;
			else
				return Long.valueOf(((Number)sqlInteger).longValue());
		}
		else
		{
			if(sqlInteger instanceof Integer)
				return (Integer)sqlInteger;
			else
				return Integer.valueOf(((Number)sqlInteger).intValue());
		}
	}

}

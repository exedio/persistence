/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Executor.NO_SUCH_ROW;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.Executor.ResultSetHandler;

class IntegerColumn extends Column
{
	final long minimum;
	final long maximum;
	final boolean longInsteadOfInt;
	final int[] allowedValues;

	IntegerColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final long minimum,
			final long maximum,
			final boolean longInsteadOfInt)
	{
		super(table, field, id, false, optional);
		this.minimum = minimum;
		this.maximum = maximum;
		this.longInsteadOfInt = longInsteadOfInt;
		this.allowedValues = null;

		assert assertMembers();
	}

	IntegerColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final int[] allowedValues)
	{
		super(table, field, id, false, optional);
		this.minimum = 0;
		this.maximum = max(allowedValues);
		this.longInsteadOfInt = false;
		this.allowedValues = allowedValues;
		
		assert allowedValues.length>(optional?0:1) : id;
		
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
		//super(table, Table.PK_COLUMN_NAME+table.id, true, true);

		super(table, null, Table.PK_COLUMN_NAME, true, true);

		this.minimum = PK.MIN_VALUE;
		this.maximum = PK.MAX_VALUE;
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
		return table.database.dialect.getIntegerType(minimum, maximum);
	}

	@Override
	final String getCheckConstraintIfNotNull()
	{
		if(allowedValues!=null)
		{
			final StringBuilder bf = new StringBuilder();
			bf.append(quotedID + " IN (");

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
			return '(' + quotedID + ">=" + minimum + ") AND (" + quotedID + "<=" + maximum + ')';
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

	Integer max(final Connection connection, final Executor executor)
	{
		final Statement bf = executor.newStatement();
		bf.append("select max(").
			append(quotedID).
			append(") from ").
			append(table.quotedID);
			
		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				final Object o = resultSet.getObject(1);
				if(o!=null)
				{
					final int result = Executor.convertSQLResult(o);
					if(result<minimum || result>maximum)
						throw new RuntimeException("invalid maximum " + result + " in column " + id);
					return result;
				}
				else
				{
					return null;
				}
			}
		});
	}
}

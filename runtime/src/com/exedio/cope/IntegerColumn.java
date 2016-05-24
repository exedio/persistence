/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.CheckConstraint;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

class IntegerColumn extends Column
{
	final long minimum;
	final long maximum;
	final boolean longInsteadOfInt;
	final int[] allowedValues;
	private final Precision precision;

	IntegerColumn(
			final Table table,
			final String id,
			final boolean synthetic,
			final boolean optional,
			final long minimum,
			final long maximum,
			final boolean longInsteadOfInt)
	{
		this(table, id, synthetic, optional, minimum, maximum, longInsteadOfInt, Precision.Millis);
	}

	IntegerColumn(
			final Table table,
			final String id,
			final boolean synthetic,
			final boolean optional,
			final long minimum,
			final long maximum,
			final boolean longInsteadOfInt,
			final Precision precision)
	{
		super(table, id, synthetic, false, optional);
		this.minimum = minimum;
		this.maximum = maximum;
		this.longInsteadOfInt = longInsteadOfInt;
		this.allowedValues = null;
		this.precision = precision;

		assert assertMembers();
	}

	IntegerColumn(
			final Table table,
			final String id,
			final boolean optional,
			final int[] allowedValues)
	{
		super(table, id, false, false, optional);
		this.minimum = 0;
		this.maximum = max(allowedValues);
		this.longInsteadOfInt = false;
		this.allowedValues = allowedValues;
		this.precision = Precision.Millis;

		assert allowedValues.length>(optional?0:1) : id;

		// ensure, that allowedValues are unique and ordered
		int current = Integer.MIN_VALUE;
		for(final int allowedValue : allowedValues)
		{
			if(current>=allowedValue)
				throw new RuntimeException();
			current = allowedValue;
		}
		assert assertMembers();
	}

	private static final int max(final int[] ints)
	{
		int result = 0;

		for(final int inti : ints)
		{
			if(result<inti)
				result = inti;
		}

		return result;
	}

	/**
	 * Creates a primary key column.
	 */
	IntegerColumn(final Table table, final long maximum)
	{
		super(table, Table.PK_COLUMN_NAME, true, true, true);
		this.minimum = PK.MIN_VALUE;
		this.maximum = maximum;
		this.longInsteadOfInt = true;
		this.allowedValues = null;
		this.precision = Precision.Millis;

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
	void makeSchema(final com.exedio.dsmf.Table dt)
	{
		super.makeSchema(dt);

		if(allowedValues!=null)
		{
			final boolean parenthesis = table.database.dialect.inRequiresParenthesis();

			final StringBuilder bf = new StringBuilder();
			if(parenthesis)
				bf.append('(');
			bf.append(quotedID);
			if(parenthesis)
				bf.append(')');
			bf.append(" IN (");

			for(int j = 0; j<allowedValues.length; j++)
			{
				if(j>0)
					bf.append(',');
				if(parenthesis)
					bf.append('(');
				bf.append(allowedValues[j]);
				if(parenthesis)
					bf.append(')');
			}
			bf.append(')');

			new CheckConstraint(dt, makeGlobalID("EN"), bf.toString());
		}
		else
		{
			new CheckConstraint(dt, makeGlobalID("MN"), quotedID + ">=" + minimum);
			new CheckConstraint(dt, makeGlobalID("MX"), quotedID + "<=" + maximum);

			if(precision!=Precision.Millis)
				new CheckConstraint(dt, makeGlobalID("PR"),
					table.database.dialect.getDateIntegerPrecision(quotedID, precision));
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

	Long max(final Connection connection, final Executor executor)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT MAX(").
			append(quotedID).
			append(") FROM ").
			append(table.quotedID);

		return executor.query(connection, bf, null, false, new ResultSetHandler<Long>()
		{
			public Long handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);

				final Object o = resultSet.getObject(1);
				if(o!=null)
				{
					final long result = Executor.convertSQLResult(o);
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

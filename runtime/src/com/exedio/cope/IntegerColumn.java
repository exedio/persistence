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
		this(table, id, synthetic, optional, minimum, maximum, longInsteadOfInt, Precision.MILLI);
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
		this.longInsteadOfInt = false;
		this.allowedValues = strictlyMonotonicallyIncreasing(allowedValues);
		this.minimum = allowedValues[0];
		this.maximum = allowedValues[allowedValues.length-1];
		this.precision = Precision.MILLI;

		assert allowedValues.length>(optional?0:1) : id;
		assert assertMembers();
	}

	static final int[] strictlyMonotonicallyIncreasing(final int[] allowedValues)
	{
		// ensure, that allowedValues are unique and ordered
		if(allowedValues.length==0)
			throw new IllegalArgumentException("empty");
		int current = allowedValues[0];
		for(int i = 1; i<allowedValues.length; i++)
		{
			final int allowedValue = allowedValues[i];
			if(current>=allowedValue)
				throw new IllegalArgumentException("" + current + ">=" + allowedValue + " at " + i);
			current = allowedValue;
		}
		return allowedValues;
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
		this.precision = Precision.MILLI;

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
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		if(allowedValues!=null)
		{
			final boolean parenthesis = table.database.dialect.inRequiresParenthesis();
			final String comma = table.database.dialect.getInComma();

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
					bf.append(comma);
				if(parenthesis)
					bf.append('(');
				bf.append(allowedValues[j]);
				if(parenthesis)
					bf.append(')');
			}
			bf.append(')');

			newCheckConstraint(dsmf, "EN", bf.toString());
		}
		else
		{
			newCheckConstraint(dsmf, "MN", quotedID + ">=" + minimum);
			newCheckConstraint(dsmf, "MX", quotedID + "<=" + maximum);

			if(precision.constrains())
				newCheckConstraint(dsmf, "PR",
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
			assert longInsteadOfInt ? (cache instanceof Long) : (cache instanceof Integer);
			return cache.toString();
		}
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		assert cache==null || (longInsteadOfInt ? (cache instanceof Long) : (cache instanceof Integer));
		return cache;
	}

	private Number convertSQLResult(final Object sqlInteger)
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

		return executor.query(connection, bf, null, false, resultSet ->
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
		);
	}
}

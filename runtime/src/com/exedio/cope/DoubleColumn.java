/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DoubleColumn extends Column
{
	final double minimum;
	final double maximum;
	
	DoubleColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final double minimum,
			final double maximum)
	{
		super(table, field, id, false, optional);
		this.minimum = minimum;
		this.maximum = maximum;
		
		assert !Double.isInfinite(minimum) : minimum;
		assert !Double.isInfinite(maximum) : maximum;
		assert !Double.isNaN(minimum) : minimum;
		assert !Double.isNaN(maximum) : maximum;
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getDoubleType();
	}

	@Override
	final String getCheckConstraintIfNotNull()
	{
		if(table.database.oracle)
			return null;
		
		return '(' + protectedID + ">=" + minimum + ") AND (" + protectedID + "<=" + maximum + ')';
	}

	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedDouble = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		row.put(this, (loadedDouble!=null) ? convertSQLResult(loadedDouble) : null);
	}

	private final static Double convertSQLResult(final Object sqlDouble)
	{
		if(sqlDouble instanceof BigDecimal)
		{
			return Double.valueOf(((BigDecimal)sqlDouble).doubleValue()); // for SumAggregate on Oracle
		}
		else
		{
			return (Double)sqlDouble;
		}
	}
	
	@Override
	final String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Double)cache).toString();
	}
	
	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		assert cache==null || cache instanceof Double;
		return cache;
	}
	
	@Override
	Object getCheckValue()
	{
		return new Double(2.2);
	}
	
}

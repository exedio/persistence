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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class DoubleColumn extends Column
{
	static final int JDBC_TYPE = Types.DOUBLE;
	
	DoubleColumn(final Table table, final String id,
					  final boolean optional)
	{
		super(table, id, false, optional, JDBC_TYPE);
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getDoubleType();
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
	{
		return null;
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
		return (Double)cache;
	}
	
	@Override
	Object getCheckValue()
	{
		return new Double(2.2);
	}
	
}

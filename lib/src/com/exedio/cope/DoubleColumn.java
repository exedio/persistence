/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

final class DoubleColumn extends Column
{
	static final int JDBC_TYPE = Types.DOUBLE;
	
	final int precision;

	DoubleColumn(final Table table, final String id, 
					  final boolean notNull, final int precision)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		this.precision = precision;
	}
	
	final String getDatabaseType()
	{
		return table.database.getDoubleType(precision);
	}

	final String getCheckConstraintIfNotNull()
	{
		return null;
	}

	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedDouble = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		row.put(this, (loadedDouble!=null) ? (Double)loadedDouble : null);
	}

	final String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Double)cache).toString();
	}
	
	Object cacheToDatabasePrepared(final Object cache)
	{
		return (Double)cache;
	}
	
	Object getCheckValue()
	{
		return new Double(2.2);
	}
	
}

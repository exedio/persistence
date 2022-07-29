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

import java.sql.ResultSet;
import java.sql.SQLException;

final class DoubleColumn extends Column
{
	final double minimum;
	final double maximum;

	DoubleColumn(
			final Table table,
			final String id,
			final boolean optional,
			final double minimum,
			final double maximum)
	{
		super(table, id, false, Kind.nonPrimaryKey(optional));
		this.minimum = minimum;
		this.maximum = maximum;

		assert !Double.isInfinite(minimum) : minimum;
		assert !Double.isInfinite(maximum) : maximum;
		assert !Double.isNaN(minimum) : minimum;
		assert !Double.isNaN(maximum) : maximum;
	}

	@Override
	String getDatabaseType()
	{
		return table.database.dialect.getDoubleType();
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		final Dialect dialect = table.database.dialect;
		newCheck(dsmf, "MN", quotedID + ">=" + dialect.format(minimum));
		newCheck(dsmf, "MX", quotedID + "<=" + dialect.format(maximum));
	}

	@Override
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final double loadedDouble = resultSet.getDouble(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		row.put(this, !resultSet.wasNull() ? loadedDouble : null);
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			assert cache instanceof Double;
			return cache.toString();
		}
	}

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		assert cache==null || cache instanceof Double;
		return cache;
	}
}

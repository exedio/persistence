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

abstract class Column
{
	final Table table;
	final String id;
	final String protectedID;
	final boolean primaryKey;
	final boolean notNull;
	final int jdbcType;
	
	Column(
			final Table table, final String id,
			final boolean primaryKey, final boolean notNull,
			final int jdbcType)
	{
		this.table = table;
		this.id = id.intern();
		this.protectedID = table.database.protectName(id).intern();
		this.primaryKey = primaryKey;
		this.notNull = notNull;
		this.jdbcType = jdbcType;
		table.addColumn(this);
	}
	
	abstract String getDatabaseType();
	
	final StringColumn getTypeColumn()
	{
		if(!primaryKey)
			throw new RuntimeException(id);
		
		return table.getTypeColumn();
	}

	final String getPrimaryKeyConstraintID()
	{
		if(!primaryKey)
			throw new RuntimeException(id);

		return table.database.trimName(table.id + "_" + "Pk");
	}
	
	final String getCheckConstraintID()
	{
		if(!table.database.supportsCheckConstraints())
			return null;

		return table.database.trimName(table.id + "_" + id + "_Ck");
	}
	
	final String getCheckConstraint()
	{
		if(!table.database.supportsCheckConstraints())
			return null;

		final String ccinn = getCheckConstraintIfNotNull();
		
		if(notNull)
		{
			if(ccinn!=null)
				return "(" + protectedID + " IS NOT NULL) AND (" + ccinn + ')';
			else
				return protectedID + " IS NOT NULL";
		}
		else
		{
			if(ccinn!=null)
				return "(" + ccinn + ") OR (" + protectedID + " IS NULL)";
			else
				return null;
		}
	}
	
	abstract String getCheckConstraintIfNotNull();
	
	public final String toString()
	{
		return id;
	}

	/**
	 * Loads the value of the column from a result set,
	 * that loads the item into memory, and put the results into
	 * a row.
	 */
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;

	/**
	 * Loads the value of the column from a result set,
	 * that selects that column in a search, and returns the results.
	 */
	abstract Object load(ResultSet resultSet, int columnIndex) throws SQLException;

	abstract Object cacheToDatabase(Object cache);

	void report(final ReportTable reportTable)
	{
		final ReportColumn result = new ReportColumn(reportTable, id, getDatabaseType(), true);

		if(primaryKey)
			new ReportPrimaryKeyConstraint(reportTable, getPrimaryKeyConstraintID(), true);
		else
		{
			final String checkConstraint = getCheckConstraint();
			if(checkConstraint!=null)
				new ReportConstraint(reportTable, getCheckConstraintID(), ReportConstraint.TYPE_CHECK, true, checkConstraint);
		}
	}
		
}

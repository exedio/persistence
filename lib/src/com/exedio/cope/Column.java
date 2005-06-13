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

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.ReportColumn;
import com.exedio.dsmf.PrimaryKeyConstraint;

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
		this.protectedID = table.database.driver.protectName(id).intern();
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

	void report(final com.exedio.dsmf.Table reportTable)
	{
		final ReportColumn result = new ReportColumn(reportTable, id, getDatabaseType());

		if(primaryKey)
			new PrimaryKeyConstraint(reportTable, table.database.trimName(table.id + "_" + "Pk"), id);
		else
		{
			if(table.database.supportsCheckConstraints())
			{
				final String ccinn = getCheckConstraintIfNotNull();
				final String checkConstraint;
				
				if(notNull)
				{
					if(ccinn!=null)
						checkConstraint = "(" + protectedID + " IS NOT NULL) AND (" + ccinn + ')';
					else
						checkConstraint = protectedID + " IS NOT NULL";
				}
				else
				{
					if(ccinn!=null)
						checkConstraint = "(" + ccinn + ") OR (" + protectedID + " IS NULL)";
					else
						checkConstraint = null;
				}
	
				if(checkConstraint!=null)
					new CheckConstraint(reportTable, table.database.trimName(table.id + "_" + id + "_Ck"), checkConstraint);
			}
		}
	}
		
}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.dsmf.PrimaryKeyConstraint;

abstract class Column
{
	final Table table;
	final String id;
	final String protectedID;
	final boolean primaryKey;
	final boolean optional;
	final int typeForDefiningColumn;
	
	Column(
			final Table table, final String id,
			final boolean primaryKey, final boolean optional,
			final int typeForDefiningColumn)
	{
		final Database database = table.database;
		this.table = table;
		this.id = database.intern(database.makeName(id));
		this.protectedID = database.intern(database.getDriver().protectName(this.id));
		this.primaryKey = primaryKey;
		this.optional = optional;
		this.typeForDefiningColumn = typeForDefiningColumn;
		table.addColumn(this);
	}
	
	abstract String getDatabaseType();
	
	final StringColumn getTypeColumn()
	{
		if(!primaryKey)
			throw new RuntimeException(id);
		
		return table.typeColumn;
	}

	abstract String getCheckConstraintIgnoringMandatory();
	
	@Override
	public final String toString()
	{
		return table.id + '#' + id;
	}

	/**
	 * Loads the value of the column from a result set,
	 * that loads the item into memory, and put the results into
	 * a row.
	 */
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;
	abstract String cacheToDatabase(Object cache);
	abstract Object cacheToDatabasePrepared(Object cache);
	abstract Object getCheckValue();

	void makeSchema(final com.exedio.dsmf.Table dsmfTable)
	{
		new com.exedio.dsmf.Column(dsmfTable, id, getDatabaseType());

		final String ccim = getCheckConstraintIgnoringMandatory();
		if(primaryKey)
		{
			new PrimaryKeyConstraint(dsmfTable, table.database.makeName(table.id + "_" + "Pk"), id);
			if(ccim!=null)
				new CheckConstraint(dsmfTable, table.database.makeName(table.id + "_" + id + "_CkPk"), ccim);
		}
		else
		{
			final String checkConstraint;
			
			if(optional)
			{
				if(ccim!=null)
					checkConstraint = "(" + ccim + ") OR (" + protectedID + " IS NULL)";
				else
					checkConstraint = null;
			}
			else
			{
				if(ccim!=null)
					checkConstraint = "(" + protectedID + " IS NOT NULL) AND (" + ccim + ')';
				else
					checkConstraint = protectedID + " IS NOT NULL";
			}

			if(checkConstraint!=null)
				new CheckConstraint(dsmfTable, table.database.makeName(table.id + "_" + id + "_Ck"), checkConstraint);
		}
	}
		
}

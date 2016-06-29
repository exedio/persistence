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

import static com.exedio.cope.Intern.intern;
import static com.exedio.dsmf.Dialect.NOT_NULL;

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Column
{
	final Table table;
	final String id;
	final String quotedID;
	final String idForGlobal;
	final boolean synthetic;
	final boolean primaryKey;
	final boolean optional;

	Column(
			final Table table,
			final String id,
			final boolean synthetic,
			final boolean primaryKey,
			final boolean optional)
	{
		final Database database = table.database;
		this.table = table;
		this.id = intern(database.makeName(TrimClass.Other,
				(synthetic&&table.database.properties.longSyntheticNames) ? (id+table.id) : id));
		this.quotedID = intern(database.dsmfDialect.quoteName(this.id));
		this.idForGlobal = id;
		this.synthetic = synthetic;
		this.primaryKey = primaryKey;
		this.optional = optional;
		table.addColumn(this);

		assert !primaryKey || synthetic : table.id+':'+id;
	}

	abstract String getDatabaseType();

	final String makeGlobalID(final TrimClass trimClass, final String suffix)
	{
		return table.makeGlobalID(trimClass, idForGlobal + '_' + suffix);
	}

	final void newCheckConstraint(
			final com.exedio.dsmf.Table dsmfTable,
			final String suffix, final String condition)
	{
		new CheckConstraint(dsmfTable, makeGlobalID(TrimClass.CheckConstraint, suffix), condition);
	}

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

	void makeSchema(final com.exedio.dsmf.Table dsmfTable)
	{
		final String databaseType = getDatabaseType();
		final String databaseTypeClause =
			!optional
			? databaseType + NOT_NULL
			: databaseType;

		new com.exedio.dsmf.Column(dsmfTable, id, databaseTypeClause);

		if(primaryKey)
			new PrimaryKeyConstraint(dsmfTable, table.makeGlobalID(TrimClass.Other, "Pk"), id);
	}
}

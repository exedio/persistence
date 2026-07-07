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

import com.exedio.cope.ConnectProperties.TrimClass;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Column
{
	final Table table;
	final String name;
	final String nameQuoted;
	final String nameForGlobal;
	final boolean synthetic;
	final Kind kind;

	Column(
			final Table table,
			final String name,
			final boolean synthetic,
			final Kind kind)
	{
		final Database database = table.database;
		this.table = table;
		this.name = intern(database.properties.trimmerLegacy.trimString(
				(synthetic&&table.database.properties.longSyntheticNames) ? (name+table.name) : name));
		this.nameQuoted = intern(database.dsmfDialect.quoteName(this.name));
		this.nameForGlobal = name;
		this.synthetic = synthetic;
		this.kind = kind;
		//noinspection ThisEscapedInObjectConstruction
		table.addColumn(this);

		assert !kind.primaryKey() || synthetic : table.name+':'+name;
	}

	protected enum Kind
	{
		primaryKey, notNull, nullable;

		static Kind nonPrimaryKey(final boolean optional)
		{
			return  optional ? nullable : notNull;
		}

		boolean primaryKey()
		{
			return this==primaryKey;
		}

		boolean forbidsNull()
		{
			return this!=nullable;
		}
	}

	abstract String getDatabaseType();

	final String makeGlobalName(final TrimClass trimClass, final String suffix)
	{
		return table.makeGlobalName(trimClass, nameForGlobal + '_' + suffix);
	}

	final void newCheck(
			final com.exedio.dsmf.Column dsmf,
			final String suffix, final String condition)
	{
		dsmf.newCheck(makeGlobalName(TrimClass.standard, suffix), condition);
	}

	@Override
	public final String toString()
	{
		return table.name + '#' + name;
	}

	/**
	 * Loads the value of the column from a result set,
	 * that loads the item into memory, and put the results into
	 * a row.
	 */
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;
	abstract String cacheToDatabase(Object cache);
	abstract Object cacheToDatabasePrepared(Object cache);

	final void makeSchema(final com.exedio.dsmf.Table dsmf)
	{
		final String databaseType = getDatabaseType();
		final String databaseTypeClause =
			kind.forbidsNull()
			? databaseType + NOT_NULL
			: databaseType;

		final com.exedio.dsmf.Column dsmfColumn =
				dsmf.newColumn(name, databaseTypeClause);

		if(kind.primaryKey())
			dsmfColumn.newPrimaryKey(table.makeGlobalName(TrimClass.standard, "PK"));

		makeSchema(dsmfColumn);
	}

	/**
	 * @param dsmf used in subclasses
	 */
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		// empty default implementation
	}

	@Override
	public final boolean equals(final Object other)
	{
		throw new RuntimeException(toString()); // should not be used, maintained in IdentityHashMap
	}

	@Override
	public final int hashCode()
	{
		throw new RuntimeException(toString()); // should not be used, maintained in IdentityHashMap
	}
}

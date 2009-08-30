/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.dsmf.Schema;

final class Table
{
	final Database database;
	final String id;
	final String idLower;
	final String protectedID;
	final IntegerColumn primaryKey;
	final StringColumn typeColumn;

	Table(
			final Database database,
			final String id,
			final Type<? extends Item> supertype,
			final String[] typesOfInstancesColumnValues)
	{
		this.database = database;
		this.id = database.intern(database.makeName(id));
		this.idLower = database.mysqlLowerCaseTableNames ? this.id.toLowerCase() : this.id;
		this.protectedID = database.intern(database.dsmfDialect.protectName(this.idLower));
		this.primaryKey =
			(supertype!=null)
			? new ItemColumn(this, supertype)
			: new IntegerColumn(this);
		this.typeColumn =
			(typesOfInstancesColumnValues!=null)
			? new StringColumn(this, null, TYPE_COLUMN_NAME, false, typesOfInstancesColumnValues)
			: null;
		database.addTable(this);
	}
	
	private List<Column> columns = null;
	private List<Column> allColumnsModifiable = new ArrayList<Column>();
	private List<Column> allColumns = null;

	private List<UniqueConstraint> uniqueConstraints = null;
	
	/**
	 * The column name for the primary key.
	 * The value "this" prevents name collisions
	 * with columns for cope fields,
	 * since "this" is a reserved java keyword,
	 * which cannot be used for java fields.
	 */
	static final String PK_COLUMN_NAME = "this";

	/**
	 * The column name for the type information.
	 * The value "class" prevents name collisions
	 * with columns for cope fields,
	 * since "class" is a reserved java keyword,
	 * which cannot be used for java fields.
	 */
	private static final String TYPE_COLUMN_NAME = "class";

	/**
	 * The table name for the revision information.
	 * The value "while" prevents name collisions
	 * with other tables,
	 * since "while" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	static final String REVISION_TABLE_NAME = "while";

	/**
	 * The name of the unique contraint
	 * on the table for the revision information.
	 * The value "protected" prevents name collisions
	 * with other tables,
	 * since "protected" is a reserved java keyword,
	 * which cannot be used for java classes.
	 */
	static final String REVISION_UNIQUE_CONSTRAINT_NAME = "protected";

	/**
	 * A name for aliases is sql statements.
	 * The value prevents name collisions
	 * with columns for cope fields / types,
	 * since is is a reserved java keyword,
	 * which cannot be used for java fields / classes.
	 */
	static final String SQL_ALIAS_1 = "return", SQL_ALIAS_2 = "break";

	void addColumn(final Column column)
	{
		allColumnsModifiable.add(column);
	}
	
	/**
	 * Returns &quot;payload&quot; columns of this type only,
	 * excluding primary key column
	 * and the optional type column for the primary key.
	 * @see #getAllColumns()
	 * @see #primaryKey
	 */
	List<Column> getColumns()
	{
		if(columns==null)
			throw new RuntimeException();
		
		return columns;
	}
	
	/**
	 * Returns all columns of this type,
	 * including primary key column.
	 * @see #getColumns()
	 * @see #primaryKey
	 */
	List<Column> getAllColumns()
	{
		if(allColumns==null)
			throw new RuntimeException();
		
		return allColumns;
	}
	
	void setUniqueConstraints(final List<UniqueConstraint> uniqueConstraints)
	{
		if(uniqueConstraints==null)
			throw new IllegalArgumentException();
		if(allColumns!=null)
			throw new RuntimeException();
		if(this.uniqueConstraints!=null)
			throw new RuntimeException();

		this.uniqueConstraints = uniqueConstraints;
	}
	
	List<UniqueConstraint> getUniqueConstraints()
	{
		if(uniqueConstraints==null)
			throw new RuntimeException();
		
		return uniqueConstraints;
	}
	
	final void finish()
	{
		final ArrayList<Column> columns = new ArrayList<Column>();
		for(final Column column : allColumnsModifiable)
		{
			// TODO dont use TYPE_COLUMN_NAME
			if(!column.primaryKey && !TYPE_COLUMN_NAME.equals(column.id))
				columns.add(column);
		}
		
		this.columns = Collections.unmodifiableList(columns);
		allColumns = Collections.unmodifiableList(allColumnsModifiable);
		allColumnsModifiable = null;
	}
	
	@Override
	public final String toString()
	{
		return id;
	}
	
	void makeSchema(final Schema schema)
	{
		final com.exedio.dsmf.Table result = new com.exedio.dsmf.Table(schema, idLower, database.getTableOptions().getProperty(id));
		
		for(final Column c : getAllColumns())
			c.makeSchema(result);

		for(final UniqueConstraint uc : getUniqueConstraints())
			uc.makeSchema(result);
	}
}

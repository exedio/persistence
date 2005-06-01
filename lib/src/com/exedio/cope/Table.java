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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


final class Table
{
	final Database database;
	final String id;
	final String protectedID;

	Table(final Database database, final String id)
	{
		this.database = database;
		this.id = database.trimName(id).intern();
		this.protectedID = database.protectName(this.id).intern();
		database.addTable(this);
	}
	
	private boolean buildStage = true;

	private final ArrayList columnsModifiable = new ArrayList();
	private final List columns = Collections.unmodifiableList(columnsModifiable);
	
	private Column primaryKey;
	private StringColumn typeColumn = null;
	
	private final List allColumnsModifiable = new ArrayList();
	private final List allColumns = Collections.unmodifiableList(allColumnsModifiable);

	private List uniqueConstraints = null;
	
	/**
	 * The column name for the type information.
	 * The value "class" prevents name collisions
	 * with columns for cope attributes,
	 * since "class" is a reserved java keyword,
	 * which cannot be used for java attributes.
	 */
	private static final String TYPE_COLUMN_NAME = "class";

	void addColumn(final Column column)
	{
		if(!buildStage)
			throw new RuntimeException();

		if(column.primaryKey)
		{
			if(primaryKey!=null)
				throw new RuntimeException(column.id);

			primaryKey = column;
		}
		else if(TYPE_COLUMN_NAME.equals(column.id))
		{
			// do not add it to columnsModifiable
		}
		else
		{
			columnsModifiable.add(column);
		}
		allColumnsModifiable.add(column);
	}
	
	void addTypeColumn(final ArrayList typeIDs)
	{
		if(!buildStage)
			throw new RuntimeException();
		if(typeColumn!=null)
			throw new RuntimeException();
		
		typeColumn = new StringColumn(this, TYPE_COLUMN_NAME, true, (String[])typeIDs.toArray(new String[typeIDs.size()]));
	}
	
	/**
	 * Returns &quot;payload&quot; columns of this type only,
	 * excluding primary key column.
	 * @see #getAllColumns()
	 * @see #getPrimaryKey()
	 */
	List getColumns()
	{
		buildStage = false;
		return columns;
	}
	
	Column getPrimaryKey()
	{
		buildStage = false;
		return primaryKey;
	}
	
	StringColumn getTypeColumn()
	{
		buildStage = false;
		return typeColumn;
	}
	
	/**
	 * Returns all columns of this type,
	 * including primary key column.
	 * @see #getColumns()
	 * @see #getPrimaryKey()
	 */
	List getAllColumns()
	{
		buildStage = false;
		return allColumns;
	}
	
	void setUniqueConstraints(final List uniqueConstraints)
	{
		if(uniqueConstraints==null)
			throw new IllegalArgumentException();
		if(!buildStage)
			throw new RuntimeException();
		if(this.uniqueConstraints!=null)
			throw new RuntimeException();

		this.uniqueConstraints = uniqueConstraints;
	}
	
	List getUniqueConstraints()
	{
		buildStage = false;
		return uniqueConstraints;
	}
	
	public final String toString()
	{
		return id;
	}

}

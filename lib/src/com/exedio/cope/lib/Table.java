package com.exedio.cope.lib;

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
		this.id = database.trimName(id);
		this.protectedID = database.protectName(this.id);
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
		else if("class".equals(column.id))
		{
			// do not add it to columnsModifiable
		}
		else
		{
			columnsModifiable.add(column);
		}
		allColumnsModifiable.add(column);
	}
	
	void addTypeColumn()
	{
		if(!buildStage)
			throw new RuntimeException();
		if(typeColumn!=null)
			throw new RuntimeException();
		
		// IMPLEMENTATION NOTE
		//
		// The following line specifies the column name for the type information
		// to be "class". This prevents name collisions with columns for cope 
		// attributes, since "class" is a reserved java keyword, which cannot be
		// used for java attributes.
		//
		// It's a string literal, since the string is not used anywhere else
		// in the framework.
		typeColumn = new StringColumn(this, "class", true, 1, 30); // TODO: allow values for each subtype only
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

package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


final class Table
{
	private static final List tablesModifiable = new ArrayList();
	private static final List tables = Collections.unmodifiableList(tablesModifiable);
	
	static final List getTables()
	{
		return tables;
	}

	final String id;
	final String protectedID;

	Table(final String id)
	{
		this.id = id;
		this.protectedID = Database.theInstance.protectName(this.id);
		tablesModifiable.add(this);
	}
	
	private boolean buildStage = true;

	private final ArrayList columnsModifiable = new ArrayList();
	private final List columns = Collections.unmodifiableList(columnsModifiable);
	
	private Column primaryKey;
	
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
			allColumnsModifiable.add(column);
		}
		else
		{
			columnsModifiable.add(column);
			allColumnsModifiable.add(column);
		}
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

}

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
	
	private final ArrayList columnsModifiable = new ArrayList();
	final List columns = Collections.unmodifiableList(columnsModifiable);
	
	Column primaryKey; // TODO: make private
	
	private final List allColumnsModifiable = new ArrayList();
	final List allColumns = Collections.unmodifiableList(allColumnsModifiable);

	void addColumn(final Column column)
	{
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
	 */
	List getColumns() // TODO: remove method
	{
		return columns;
	}
	
	/**
	 * Returns all columns of this type,
	 * including primary key column.
	 * @see #getColumns()
	 */
	List getAllColumns() // TODO: remove method
	{
		return allColumns;
	}
	
	private List uniqueConstraints = null;

	void setUniqueConstraints(final List uniqueConstraints) // TODO: remove method
	{
		this.uniqueConstraints = uniqueConstraints;
	}
	
	List getUniqueConstraints() // TODO: remove method
	{
		return uniqueConstraints;
	}

}

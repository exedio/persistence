package com.exedio.cope.lib;

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
		return table.database.trimName(table.id + "_" + id + "_Ck");
	}
	
	final String getCheckConstraint()
	{
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

}



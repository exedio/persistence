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
	final String databaseType;
	final Integer jdbcType;
	
	Column(
			final Table table, final String id,
			final boolean primaryKey, final boolean notNull,
			final String databaseType, final Integer jdbcType)
	{
		this.table = table;
		this.id = id;
		this.protectedID = Database.theInstance.protectName(id);
		this.primaryKey = primaryKey;
		this.notNull = notNull;
		this.databaseType = databaseType;
		this.jdbcType = jdbcType;
		table.addColumn(this);
	}
	
	final String getPrimaryKeyConstraintID()
	{
		if(!primaryKey)
			throw new RuntimeException(id);

		return Database.theInstance.trimName(table.id + "_" + "Pk");
	}
	
	public final String toString()
	{
		return id;
	}
	
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;
	abstract Object cacheToDatabase(Object cache);

}



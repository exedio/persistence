package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Column
{
	final Type type;
	final String id;
	final String protectedName;
	final boolean notNull;
	final String databaseType;
	final Integer jdbcType;
	
	Column(final Type type, final String id, final boolean notNull, final String databaseType, final Integer jdbcType)
	{
		this.type = type;
		this.id = id;
		this.protectedName = Database.theInstance.protectName(id);
		this.notNull = notNull;
		this.databaseType = databaseType;
		this.jdbcType = jdbcType;
	}
	
	public final String toString()
	{
		return id;
	}
	
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;
	abstract Object cacheToDatabase(Object cache);

}



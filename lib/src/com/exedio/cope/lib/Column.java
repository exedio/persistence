package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;

abstract class Column
{
	final Type type;
	final String trimmedName;
	final String protectedName;
	final boolean notNull;
	final String databaseType;
	final Integer jdbcType;
	
	Column(final Type type, final String trimmedName, final boolean notNull, final String databaseType, final Integer jdbcType)
	{
		this.type = type;
		this.trimmedName = trimmedName;
		this.protectedName = Database.theInstance.protectName(trimmedName);
		this.notNull = notNull;
		this.databaseType = databaseType;
		this.jdbcType = jdbcType;
	}
	
	public final String toString()
	{
		return trimmedName;
	}
	
	abstract void load(ResultSet resultSet, int columnIndex, Row row) throws SQLException;
	abstract Object cacheToDatabase(Object cache);

}



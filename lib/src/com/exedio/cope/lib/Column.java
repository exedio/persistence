package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

abstract class Column
{
	final Type type;
	final String trimmedName;
	final String protectedName;
	final boolean notNull;
	final String databaseType;
	
	Column(final Type type, final String trimmedName, final boolean notNull, final String databaseType)
	{
		this.type = type;
		this.trimmedName = trimmedName;
		this.protectedName = Database.theInstance.protectName(trimmedName);
		this.notNull = notNull;
		this.databaseType = databaseType;
	}
	
	public final String toString()
	{
		return trimmedName;
	}
	
	abstract void load(ResultSet resultSet, int columnIndex, HashMap itemCache) throws SQLException;
	abstract Object cacheToDatabase(Object cache);

}



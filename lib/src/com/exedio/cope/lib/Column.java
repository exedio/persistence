package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

abstract class Column
{
	final Type type;
	final String name;
	final boolean notNull;
	final String databaseType;
	
	Column(final Type type, final String name, final boolean notNull, final String databaseType)
	{
		this.type = type;
		this.name = Database.theInstance.makePersistentQualifier(name);
		this.notNull = notNull;
		this.databaseType = databaseType;
	}
	
	public final String toString()
	{
		return name;
	}
	
	abstract void load(ResultSet resultSet, int columnIndex, HashMap itemCache) throws SQLException;
	abstract Object cacheToDatabase(Object cache);

}



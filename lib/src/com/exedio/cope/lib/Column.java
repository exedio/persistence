package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;

abstract class Column
{
	final Type type;
	final String name;
	final String databaseType;
	
	Column(final Type type, final String name, final String databaseType)
	{
		this.type = type;
		this.name = Database.theInstance.makePersistentQualifier(name);
		this.databaseType = databaseType;
	}
	
	public final String toString()
	{
		return name;
	}
	
	abstract Object databaseToCache(Object cell);
	abstract Object cacheToDatabase(Object cache);

}



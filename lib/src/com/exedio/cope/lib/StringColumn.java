package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;

class StringColumn extends Column
{
	StringColumn(final Type type, final String name)
	{
		super(type, name, "varchar2(4000)"/* TODO: this is database specific */);
	}
	
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else if(cell instanceof String)
			return cell;
		else
			throw new RuntimeException("cellToCache:"+cell);
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return "'" + ((String)cache) + '\'';
	}

}



package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.math.BigDecimal;

class IntegerColumn extends Column
{
	IntegerColumn(final Type type, final String name, final int precision)
	{
		super(type, name, "number(" + precision + ",0)"/* TODO: this is database specific */);
	}
	
	Object databaseToCache(final Object cell)
	{
		if(cell==null)
			return null;
		else
			return new Integer(((BigDecimal)cell).intValue()); // TODO: use ResultSet.getInt() somehow
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}

}



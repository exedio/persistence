package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

final class IntegerColumn extends Column
{
	IntegerColumn(final Type type, final String name, final boolean notNull, final int precision)
	{
		super(type, name, notNull, "number(" + precision + ",0)"/* TODO: this is database specific */);
	}
	
	void load(final ResultSet resultSet, final int columnIndex, final HashMap itemCache)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		if(loadedInteger!=null)
		{
			// TODO: somehow do without that BigDecimal
			itemCache.put(this, new Integer(((BigDecimal)loadedInteger).intValue()));
		}
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return ((Integer)cache).toString();
	}

}



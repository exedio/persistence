package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

final class IntegerColumn extends Column
{
	final String foreignTable;

	IntegerColumn(final Type type, final String trimmedName,
					  final boolean notNull, final int precision,
					  final String foreignTable)
	{
		super(type, trimmedName, notNull, "number(" + precision + ",0)"/* TODO: this is database specific */);
		this.foreignTable = foreignTable;
	}
	
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		if(loadedInteger!=null)
		{
			// TODO: somehow do without that BigDecimal
			row.load(this, ((BigDecimal)loadedInteger).intValue());
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



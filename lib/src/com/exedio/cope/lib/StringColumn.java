package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class StringColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.VARCHAR);

	StringColumn(final Type type, final String trimmedName, final boolean notNull, final int maxLength)
	{
		super(type, trimmedName, notNull, Database.theInstance.getStringType(maxLength), JDBC_TYPE);
	}
	
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final String loadedString = resultSet.getString(columnIndex);
		//System.out.println("StringColumn.load:"+loadedString);
		if(loadedString!=null)
			row.load(this, loadedString);
	}

	Object cacheToDatabase(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
			return "'" + ((String)cache) + '\'';
	}

}



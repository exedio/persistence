
package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ComputedStringFunction
	extends ComputedFunction
	implements StringFunction
{
	public ComputedStringFunction(
			final Function[] sources,
			final String[] sqlFragments,
			final String functionName)
	{
		super(sources, sqlFragments, functionName, StringColumn.JDBC_TYPE);
	}

	final Object load(final ResultSet resultSet, final int columnIndex)
	throws SQLException
	{
		return resultSet.getString(columnIndex);
	}

	final Object surface2Database(final Object value)
	{
		return StringColumn.cacheToDatabaseStatic(value);
	}
	
}

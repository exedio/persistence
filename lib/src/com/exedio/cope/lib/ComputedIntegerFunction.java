
package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ComputedIntegerFunction
	extends ComputedFunction
	implements IntegerFunction
{
	public ComputedIntegerFunction(
			final Function[] sources,
			final String[] sqlFragments,
			final String functionName)
	{
		super(sources, sqlFragments, functionName, IntegerColumn.JDBC_TYPE_INT);
	}

	final Object load(final ResultSet resultSet, final int columnIndex)
	throws SQLException
	{
		final Object loadedInteger = resultSet.getObject(columnIndex);
		//System.out.println("IntegerColumn.load "+trimmedName+" "+loadedInteger);
		if(loadedInteger!=null)
		{
			if(loadedInteger instanceof BigDecimal)
				return new Integer(((BigDecimal)loadedInteger).intValue());
			else
				return (Integer)loadedInteger;
		}
		else
			return null;
	}

	final Object surface2Database(final Object value)
	{
		if(value==null)
			return "NULL";
		else
			return ((Integer)value).toString();
	}
	
}

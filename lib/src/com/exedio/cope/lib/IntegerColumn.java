package com.exedio.cope.lib;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

final class IntegerColumn extends Column
{
	final String foreignTable;
	final String integrityConstraintName;

	IntegerColumn(final Type type, final String trimmedName,
					  final boolean notNull, final int precision,
					  final String foreignTable, final String integrityConstraintName)
	{
		super(type, trimmedName, notNull, "number(" + precision + ",0)"/* TODO: this is database specific */);
		if((foreignTable==null)!=(integrityConstraintName==null))
			throw new RuntimeException();
		this.foreignTable = foreignTable;
		this.integrityConstraintName = integrityConstraintName;
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



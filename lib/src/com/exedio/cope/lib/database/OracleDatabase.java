
package com.exedio.cope.lib.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import oracle.jdbc.OracleStatement;

import com.exedio.cope.lib.Database;

public class OracleDatabase extends Database
{
	public OracleDatabase()
	{
		super(true);
	}

	public String protectName(final String name)
	{
		return '"' + name + '"';
	}

	protected void defineColumnTypes(final List columnTypes, final Statement statement)
			throws SQLException
	{
		final OracleStatement s = (OracleStatement)statement;
		int columnIndex = 1;
		for(Iterator i = columnTypes.iterator(); i.hasNext(); columnIndex++)
		{
			final Integer columnType = (Integer)i.next();
			s.defineColumnType(columnIndex, columnType.intValue());
		}
	}

}

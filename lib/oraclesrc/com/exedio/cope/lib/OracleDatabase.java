package com.exedio.cope.lib;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import oracle.jdbc.OracleStatement;

final class OracleDatabase
		extends Database
		implements DatabaseColumnTypesDefinable
{

	String getIntegerType(final int precision)
	{
		return "number(" + precision + ",0)";
	}

	String getStringType(final int maxLength)
	{
		return "varchar2("+maxLength+")";
	}
	
	private String extractConstraintName(final SQLException e, final String start, final String end)
	{
		final String m = e.getMessage();
		if(m.startsWith(start) && m.endsWith(end))
		{
			final int pos = m.indexOf('.', start.length());
			return m.substring(pos+1, m.length()-end.length());
		}
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "ORA-00001: unique constraint (", ") violated\n");
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "ORA-02292: integrity constraint (", ") violated - child record found\n");
	}

	public void defineColumnTypes(final List columnTypes, final Statement statement)
			throws SQLException
	{
		//System.out.println("defineColumnTypes: "+columnTypes);
		final OracleStatement s = (OracleStatement)statement;
		int columnIndex = 1;
		for(Iterator i = columnTypes.iterator(); i.hasNext(); columnIndex++)
		{
			final Integer columnType = (Integer)i.next();
			s.defineColumnType(columnIndex, columnType.intValue());
		}
	}

}

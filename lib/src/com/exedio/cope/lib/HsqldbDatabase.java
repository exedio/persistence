
package com.exedio.cope.lib;

import java.sql.SQLException;

final class HsqldbDatabase
		extends Database
		implements
			DatabaseTimestampCapable
{

	protected HsqldbDatabase(final Properties properties)
	{
		super(properties);
	}

	String getIntegerType(final int precision)
	{
		// TODO: use precision to select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (precision <= 10) ? "integer" : "bigint";
	}

	String getDoubleType(final int precision)
	{
		return "double";
	}

	String getStringType(final int maxLength)
	{
		return "varchar("+maxLength+")";
	}
	
	public String getDateTimestampType()
	{
		return "timestamp";
	}

	private final String extractConstraintName(final SQLException e, final String start, final char end)
	{
		final String m = e.getMessage();
		if(m.startsWith(start))
		{
			final int pos = m.indexOf(end, start.length());
			if(pos<0)
				return null;
			return m.substring(start.length(), pos);
		}
		else
			return null;
	}
	
	private final String extractConstraintName(final SQLException e, final String start)
	{
		final String m = e.getMessage();
		if(m.startsWith(start))
			return m.substring(start.length());
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "Unique constraint violation: ");
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "Integrity constraint violation ", ' ');
	}

	Statement getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" alter column ").
			append(oldColumnName).
			append(" rename to ").
			append(newColumnName);
		return bf;
	}

	Statement getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf;
	}

	Statement getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		throw new RuntimeException("not implemented");
	}

}

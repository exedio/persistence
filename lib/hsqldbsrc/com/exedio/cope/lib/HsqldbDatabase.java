
package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.hsqldb.jdbcDriver;

final class HsqldbDatabase
		extends Database
		implements
			DatabaseTimestampCapable
{
	static
	{
		try
		{
			Class.forName(jdbcDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

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

	Statement getRenameColumnStatement(final String tableName,
			final String oldColumnName, final String newColumnName, final String columnType)
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

	void fillReport(final Report report)
	{
		super.fillReport(report);

		// TODO: use SYSTEM_TABLE_CONSTRAINTS
		// select stc.CONSTRAINT_NAME, stc.CONSTRAINT_TYPE, stc.TABLE_NAME, scc.CHECK_CLAUSE
		// from SYSTEM_TABLE_CONSTRAINTS stc
		// left outer join SYSTEM_CHECK_CONSTRAINTS scc on stc.CONSTRAINT_NAME = scc.CONSTRAINT_NAME
		final Statement bf = createStatement();
		bf.append("select " +
				"CONSTRAINT_NAME," +
				"CHECK_CLAUSE " +
				"from SYSTEM_CHECK_CONSTRAINTS").
			defineColumnString().
			defineColumnString();
		try
		{
			executeSQLQuery(bf, new ReportConstraintHandler(report));
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	private static class ReportConstraintHandler implements ResultSetHandler
	{
		private final Report report;

		ReportConstraintHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String constraintName = resultSet.getString(1);
				final String checkClause = resultSet.getString(2);
				final int end = checkClause.indexOf("\".");
				if(end<=0)
					throw new RuntimeException(checkClause);
				final int start = checkClause.lastIndexOf("\"", end-1);
				if(start<0)
					throw new RuntimeException(checkClause);

				final String tableName = checkClause.substring(start+1, end);
				final String tablePrefix = checkClause.substring(start, end+2);
				final ReportTable table = report.notifyExistentTable(tableName);
				
				String checkClauseClean = checkClause;
				for(int pos = checkClauseClean.indexOf(tablePrefix); pos>=0; pos = checkClauseClean.indexOf(tablePrefix))
					checkClauseClean = checkClauseClean.substring(0, pos) + checkClauseClean.substring(pos+tablePrefix.length());
				
				//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" checkClause:>"+checkClause+"<");
				final ReportConstraint constraint = table.notifyExistentCheckConstraint(constraintName, checkClauseClean);
			}
		}
	}
}


package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;

class HsqldbDatabase
		extends Database
		implements
			DatabaseReportable,
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

	public Report reportDatabase()
	{
		final Report report = new Report(this, getTables());

		{
			final com.exedio.cope.lib.Statement bf = createStatement();
			bf.append(GET_TABLES);
			try
			{
				executeSQL(bf, new MetaDataTableHandler(report));
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}
		
		report.finish();

		return report;
	}

	private static class MetaDataTableHandler implements ResultSetHandler
	{
		private final Report report;

		MetaDataTableHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString("TABLE_NAME");
				final ReportTable table = report.notifyExistentTable(tableName);
				//System.out.println("EXISTS:"+tableName);
			}
		}
	}
}

package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import oracle.jdbc.OracleStatement;

final class OracleDatabase
		extends Database
		implements
			DatabaseColumnTypesDefinable,
			DatabaseTimestampCapable,
			DatabaseReportable
{

	String getIntegerType(final int precision)
	{
		return "number(" + precision + ",0)";
	}

	String getDoubleType(final int precision)
	{
		return "number(" + precision + ",8)";
	}

	String getStringType(final int maxLength)
	{
		return "varchar2("+(maxLength!=Integer.MAX_VALUE ? maxLength : 2000)+")";
	}
	
	public String getDateTimestampType()
	{
		return "timestamp(3)";
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


	public Report reportDatabase()
	{
		final Report report = new Report(getTables());

		{
			final com.exedio.cope.lib.Statement bf = createStatement();
			bf.append("select TABLE_NAME, LAST_ANALYZED from user_tables").
				defineColumnString().
				defineColumnTimestamp();
			try
			{
				executeSQL(bf, new ReportTableHandler(report));
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}
		{
			final com.exedio.cope.lib.Statement bf = createStatement();
			bf.append("select TABLE_NAME, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, DATA_PRECISION, DATA_SCALE from user_tab_columns").
				defineColumnString().
				defineColumnString().
				defineColumnString().
				defineColumnInteger().
				defineColumnInteger().
				defineColumnInteger();
			try
			{
				executeSQL(bf, new ReportColumnHandler(report));
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}
		{
			final com.exedio.cope.lib.Statement bf = createStatement();
			bf.append("select TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE  from user_constraints order by table_name").
				defineColumnString().
				defineColumnString().
				defineColumnString();
			try
			{
				executeSQL(bf, new ReportConstraintHandler(report));
			}
			catch(ConstraintViolationException e)
			{
				throw new SystemException(e);
			}
		}
		
		report.finish();

		return report;
	}

	private static class ReportTableHandler implements ResultSetHandler
	{
		private final Report report;

		ReportTableHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final Date lastAnalyzed = (Date)resultSet.getObject(2);
				final ReportTable table = report.notifyExistentTable(tableName);
				table.setLastAnalyzed(lastAnalyzed);
				//System.out.println("EXISTS:"+tableName);
			}
		}
	}

	private static class ReportColumnHandler implements ResultSetHandler
	{
		private final Report report;

		ReportColumnHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final String columnName = resultSet.getString(2);
				final String dataType = resultSet.getString(3);

				final String columnType;
				if(dataType.equals("NUMBER"))
				{
					final int dataPrecision = resultSet.getInt(5);
					final int dataScale = resultSet.getInt(6);
					columnType = "number("+dataPrecision+','+dataScale+')';
				}
				else if(dataType.equals("VARCHAR2"))
				{
					final int dataLength = resultSet.getInt(4);
					columnType = "varchar2("+dataLength+')';
				}
				else
					columnType = dataType.toLowerCase(); // TODO: create upper case types
					
				final ReportTable table = report.notifyExistentTable(tableName);
				final ReportColumn column = table.notifyExistentColumn(columnName, columnType);
				//System.out.println("EXISTS:"+tableName);
			}
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
				final String tableName = resultSet.getString(1);
				final String constraintName = resultSet.getString(2);
				final String constraintType = resultSet.getString(3);
				final ReportTable table = report.notifyExistentTable(tableName);
				final ReportConstraint constraint = table.notifyExistentConstraint(constraintName);
				//System.out.println("EXISTS:"+tableName);
			}
		}
	}

}


package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class HsqldbDatabase
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
		{
			final com.exedio.cope.lib.Statement bf = createStatement();
			bf.append(GET_COLUMNS);
			try
			{
				executeSQL(bf, new MetaDataColumnHandler(report));
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

	private class MetaDataColumnHandler implements ResultSetHandler
	{
		private final Report report;

		MetaDataColumnHandler(final Report report)
		{
			this.report = report;
		}

		public void run(ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString("TABLE_NAME");
				final String columnName = resultSet.getString("COLUMN_NAME");
				final int dataType = resultSet.getInt("DATA_TYPE");
				
				final ReportTable table = report.getTable(tableName);
				if(table!=null)
				{
					String columnType = getColumnType(dataType, resultSet);
					if(columnType==null)
						columnType = String.valueOf(dataType);

					table.notifyExistentColumn(columnName, columnType);
				}
				//System.out.println("EXISTS:"+tableName);
			}
		}
	}
	
	final String getColumnType(final int dataType, final ResultSet resultSet)
			throws SQLException
	{
		switch(dataType)
		{
			case Types.INTEGER:
				return "integer";
			case Types.BIGINT:
				return "bigint";
			case Types.DOUBLE:
				return "double";
			case Types.TIMESTAMP:
				return "timestamp";
			case Types.VARCHAR:
				final int dataLength = resultSet.getInt("COLUMN_SIZE");
				return "varchar("+dataLength+')';
			default:
				return null;
		}
	}

	// TODO: make getRenameColumnStatement function for override
	final void renameColumn(final String tableName, final String oldColumnName, final String newColumnName)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" alter column ").
			append(protectName(oldColumnName)).
			append(" rename to ").
			append(protectName(newColumnName));

		try
		{
			//System.err.println("renameColumn:"+bf);
			executeSQL(bf, EMPTY_RESULT_SET_HANDLER);
		}
		catch(ConstraintViolationException e)
		{
			throw new SystemException(e);
		}
	}

}

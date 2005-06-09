/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hsqldb.jdbcDriver;

import com.exedio.dsmf.HsqldbDriver;

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
		super(new HsqldbDriver(), properties, null);
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

	protected String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
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
				final int columnSize = resultSet.getInt("COLUMN_SIZE");
				return "varchar("+columnSize+')';
			default:
				return null;
		}
	}

	private final String extractConstraintName(final SQLException e, final int vendorCode, final String start, final char end)
	{
		//System.out.println("-u-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());

		if(e.getErrorCode()!=vendorCode)
			return null;

		final String m = e.getMessage();
		if(m.startsWith(start))
		{
			if(end=='\0')
				return m.substring(start.length());
			else
			{
				final int pos = m.indexOf(end, start.length());
				if(pos<0)
					return null;
				return m.substring(start.length(), pos);
			}
		}
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, -104, "Unique constraint violation: ", '\0');
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extractConstraintName(e, -8, "Integrity constraint violation ", ' ');
	}

	void fillReport(final ReportSchema report)
	{
		super.fillReport(report);

		final Statement bf = createStatement();
		bf.append(
			"select stc.CONSTRAINT_NAME, stc.CONSTRAINT_TYPE, stc.TABLE_NAME, scc.CHECK_CLAUSE " +
			"from SYSTEM_TABLE_CONSTRAINTS stc " +
			"left outer join SYSTEM_CHECK_CONSTRAINTS scc on stc.CONSTRAINT_NAME = scc.CONSTRAINT_NAME");

		executeSQLQuery(bf, new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						final String constraintName = resultSet.getString(1);
						final String constraintType = resultSet.getString(2);
						final String tableName = resultSet.getString(3);
						
						final ReportTable table = report.notifyExistentTable(tableName);
						
						if("CHECK".equals(constraintType))
						{
							final String tablePrefix = protectName(tableName)+'.';
							String checkClause = resultSet.getString(4);
							for(int pos = checkClause.indexOf(tablePrefix); pos>=0; pos = checkClause.indexOf(tablePrefix))
								checkClause = checkClause.substring(0, pos) + checkClause.substring(pos+tablePrefix.length());

							table.notifyExistentCheckConstraint(constraintName, checkClause);
						}
						else if("PRIMARY KEY".equals(constraintType))
							table.notifyExistentPrimaryKeyConstraint(constraintName);
						else if("FOREIGN KEY".equals(constraintType))
							table.notifyExistentForeignKeyConstraint(constraintName);
						else if("UNIQUE".equals(constraintType))
						{
							//printRow(resultSet);
							final StringBuffer clause = new StringBuffer();
							final Statement bf = createStatement();
							bf.append("select COLUMN_NAME from SYSTEM_INDEXINFO where INDEX_NAME like 'SYS_IDX_").
								append(constraintName).
								append("_%' and NON_UNIQUE=false order by ORDINAL_POSITION");
							executeSQLQuery(bf, new ResultSetHandler()
								{
									public void run(final ResultSet resultSet) throws SQLException
									{
										//printMeta(resultSet);
										boolean first = true;
										clause.append('(');
										while(resultSet.next())
										{
											//printRow(resultSet);
											if(first)
												first = false;
											else
												clause.append(',');
											final String columnName = resultSet.getString(1);
											clause.append(protectName(columnName));
										}
										clause.append(')');
									}
								}, false);

							table.notifyExistentUniqueConstraint(constraintName, clause.toString());
						}
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

					}
				}
			}, false);
	}

}

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

package com.exedio.dsmf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.exedio.dsmf.ReportNode.ResultSetHandler;


public final class OracleDriver extends Driver
{
	public OracleDriver(final String schema)
	{
		super(schema);
	}

	String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
		switch(dataType)
		{
			case Types.DECIMAL:
				final int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
				if(decimalDigits>0)
					return "NUMBER("+columnSize+','+decimalDigits+')';
				else
					return "NUMBER("+columnSize+')';
			case Types.OTHER:
				return "TIMESTAMP(3)";
			case Types.VARCHAR:
				return "VARCHAR2("+columnSize+')';
			case Types.TIMESTAMP:
				return "DATE";
			case Types.LONGVARCHAR:
				return "LONG";
			default:
				return null;
		}
	}

	private ReportConstraint makeUniqueConstraint(final ReportTable table, final String constraintName, final ArrayList columns)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append('(');
		boolean first = true;
		for(Iterator i = columns.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');
			
			bf.append(protectName((String)i.next()));
		}
		bf.append(')');
		return table.notifyExistentUniqueConstraint(constraintName, bf.toString());
	}
	
	void fillReport(final ReportSchema report)
	{
		super.fillReport(report);

		report.querySQL("select TABLE_NAME, LAST_ANALYZED from user_tables", new ReportNode.ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
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
			});
		
		report.querySQL(
				"select " +
				"uc.TABLE_NAME," +
				"uc.CONSTRAINT_NAME," +
				"uc.CONSTRAINT_TYPE," +
				"uc.SEARCH_CONDITION," +
				"ucc.COLUMN_NAME " +
				"from user_constraints uc " +
				"left outer join user_cons_columns ucc " +
					"on uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
					"and uc.TABLE_NAME=ucc.TABLE_NAME " +
				"order by uc.TABLE_NAME, ucc.POSITION",
			new ResultSetHandler()
			{
				String uniqueConstraintName = null;
				ReportTable uniqueConstraintTable = null;
				final ArrayList uniqueColumns = new ArrayList();
				public void run(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(1);
						final String constraintName = resultSet.getString(2);
						final String constraintType = resultSet.getString(3);
						final ReportTable table = report.notifyExistentTable(tableName);
						//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
						if("C".equals(constraintType))
						{
							final String searchCondition = resultSet.getString(4);
							//System.out.println("searchCondition:>"+searchCondition+"<");
							table.notifyExistentCheckConstraint(constraintName, searchCondition);
						}
						else if("P".equals(constraintType))
							table.notifyExistentPrimaryKeyConstraint(constraintName);
						else if("R".equals(constraintType))
							table.notifyExistentForeignKeyConstraint(constraintName);
						else if("U".equals(constraintType))
						{
							final String columnName = resultSet.getString(5);
							if(uniqueConstraintName==null)
							{
								uniqueConstraintName = constraintName;
								uniqueConstraintTable = table;
								uniqueColumns.add(columnName);
							}
							else if(uniqueConstraintName.equals(constraintName) && uniqueConstraintTable==table)
								uniqueColumns.add(columnName);
							else
							{
								makeUniqueConstraint(uniqueConstraintTable, uniqueConstraintName, uniqueColumns);
								uniqueConstraintName = constraintName;
								uniqueConstraintTable = table;
								uniqueColumns.clear();
								uniqueColumns.add(columnName);
							}
						}
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

						//System.out.println("EXISTS:"+tableName);
					}
					if(uniqueConstraintName!=null)
						makeUniqueConstraint(uniqueConstraintTable, uniqueConstraintName, uniqueColumns);
				}
			});
	}

	String getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" rename column ").
			append(oldColumnName).
			append(" to ").
			append(newColumnName);
		return bf.toString();
	}

	String getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" add (").
			append(columnName).
			append(' ').
			append(columnType).
			append(')');
		return bf.toString();
	}

	String getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" modify ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}
	
}

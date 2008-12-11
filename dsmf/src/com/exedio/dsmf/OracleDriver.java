/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.HashMap;
import java.util.Iterator;

import com.exedio.dsmf.Node.ResultSetHandler;

public final class OracleDriver extends Driver
{
	private static final String SYSTEM_TABLE_PREFIX = "BIN$"; // for Oracle 10
	
	public OracleDriver(final String schema)
	{
		super(schema, SYSTEM_TABLE_PREFIX);
	}

	@Override
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
			{
				final String typeName = resultSet.getString("TYPE_NAME");
				if("NVARCHAR2".equals(typeName))
				{
					if((columnSize%2)!=0)
						return "error: NVARCHAR2 with an odd COLUMN_SIZE: "+columnSize;

					return "NVARCHAR2("+(columnSize/2)+')';
				}
				else if("TIMESTAMP(3)".equals(typeName))
					return "TIMESTAMP(3)";
				else if("NCLOB".equals(typeName))
					return "NCLOB";
				
				return "error: unknown TYPE_NAME for Types.OTHER: "+typeName;
			}
			case Types.VARCHAR:
				return "VARCHAR2("+columnSize+" BYTE)";
			case Types.TIMESTAMP:
			case Types.DATE:
				return "DATE";
			case Types.LONGVARCHAR:
				return "LONG";
			case Types.BLOB:
				return "BLOB";
			case Types.CLOB:
				return "CLOB";
			default:
				return null;
		}
	}

	Constraint makeUniqueConstraint(final Table table, final String constraintName, final ArrayList columns)
	{
		final StringBuilder bf = new StringBuilder();
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
	
	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL("select TABLE_NAME from user_tables", new Node.ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						final String tableName = resultSet.getString(1);
						schema.notifyExistentTable(tableName);
						//System.out.println("EXISTS:"+tableName);
					}
				}
			});
		
		schema.querySQL(
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
				"order by uc.TABLE_NAME, uc.CONSTRAINT_NAME, ucc.POSITION",
			new ResultSetHandler()
			{
				String uniqueConstraintName = null;
				Table uniqueConstraintTable = null;
				final ArrayList<String> uniqueColumns = new ArrayList<String>();
				public void run(final ResultSet resultSet) throws SQLException
				{
					final HashMap<String, String> duplicateCheckConstraints = new HashMap<String, String>();
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(1);
						if(tableName.startsWith(SYSTEM_TABLE_PREFIX))
							continue;
						
						final String constraintName = resultSet.getString(2);
						final String constraintType = resultSet.getString(3);
						final Table table = schema.notifyExistentTable(tableName);
						//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
						if("C".equals(constraintType))
						{
							final String searchCondition = resultSet.getString(4);
							//System.out.println("searchCondition:>"+constraintName+"< >"+searchCondition+"<");
							final String duplicateCondition =
								duplicateCheckConstraints.put(constraintName, searchCondition);
							if(duplicateCondition!=null)
							{
								System.out.println(
										"mysterious duplicate check constraint >" + constraintName +
										"< with " +(searchCondition.equals(duplicateCondition)
												? ("equal condition >" + searchCondition + '<')
												: ("different conditions >" + searchCondition + "< and >" + duplicateCondition + '<')));
								continue;
							}
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

	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" rename column ").
			append(oldColumnName).
			append(" to ").
			append(newColumnName);
		return bf.toString();
	}

	@Override
	public String createColumn(final String tableName, final String columnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" add (").
			append(columnName).
			append(' ').
			append(columnType).
			append(')');
		return bf.toString();
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" modify ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}

	@Override
	public String createSequence(final String sequenceName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("create sequence ").
			append(sequenceName).
			append(
					" increment by 1" +
					" start with 0" +
					" maxvalue " + Integer.MAX_VALUE +
					" minvalue 0" +
					" nocycle" +
					" noorder");
		return bf.toString();
	}
}

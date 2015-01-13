/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.Node.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class OracleDialect extends Dialect
{
	public OracleDialect(final String schema)
	{
		super(schema);
	}

	@Override
	public boolean supportsSemicolon()
	{
		return false;
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
			case Types.VARCHAR:     return "VARCHAR2("+columnSize+" BYTE)";
			case Types.TIMESTAMP:
			case Types.DATE:        return "DATE";
			case Types.LONGVARCHAR: return "LONG";
			case Types.BLOB:        return "BLOB";
			case Types.CLOB:        return "CLOB";
			default:
				return null;
		}
	}

	Constraint makeUniqueConstraint(final Table table, final String constraintName, final ArrayList<?> columns)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append('(');
		boolean first = true;
		for(final Iterator<?> i = columns.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(quoteName((String)i.next()));
		}
		bf.append(')');
		return table.notifyExistentUniqueConstraint(constraintName, bf.toString());
	}

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL("SELECT TABLE_NAME FROM user_tables", new ResultSetHandler()
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
				"SELECT " +
				"uc.TABLE_NAME," +
				"uc.CONSTRAINT_NAME," +
				"uc.CONSTRAINT_TYPE," +
				"uc.SEARCH_CONDITION," +
				"ucc.COLUMN_NAME " +
				"FROM user_constraints uc " +
				"LEFT OUTER JOIN user_cons_columns ucc " +
					"ON uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
					"AND uc.TABLE_NAME=ucc.TABLE_NAME " +
				"WHERE uc.CONSTRAINT_TYPE in ('C','P','U')" +
				"ORDER BY uc.TABLE_NAME, uc.CONSTRAINT_NAME, ucc.POSITION",
			new ResultSetHandler()
			{
				String uniqueConstraintName = null;
				Table uniqueConstraintTable = null;
				final ArrayList<String> uniqueColumns = new ArrayList<>();
				public void run(final ResultSet resultSet) throws SQLException
				{
					final HashMap<String, String> duplicateCheckConstraints = new HashMap<>();
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(1);
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

		verifyForeignKeyConstraints(
				"SELECT uc.CONSTRAINT_NAME,uc.TABLE_NAME,ucc.COLUMN_NAME,uic.TABLE_NAME,uic.COLUMN_NAME " +
				"FROM USER_CONSTRAINTS uc " +
				"JOIN USER_cons_columns ucc ON uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
				"JOIN USER_IND_COLUMNS uic ON uc.R_CONSTRAINT_NAME=uic.INDEX_NAME " +
				"WHERE uc.CONSTRAINT_TYPE='R'",
				schema);

		schema.querySQL(
				"SELECT SEQUENCE_NAME " +
				"FROM USER_SEQUENCES",
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String name = resultSet.getString(1);
						schema.notifyExistentSequence(name);
					}
				}
			});
	}

	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" RENAME COLUMN ").
			append(oldColumnName).
			append(" TO ").
			append(newColumnName);
		return bf.toString();
	}

	@Override
	public String createColumn(final String tableName, final String columnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" ADD (").
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
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" MODIFY ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}

	@Override
	void appendForeignKeyCreateStatement(final StringBuilder bf)
	{
		bf.append(" DEFERRABLE");
	}

	@Override
	void createSequence(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		createSequenceStatic(bf, sequenceName, startWith);
	}

	public static void createSequenceStatic(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		bf.append("CREATE SEQUENCE ").
			append(sequenceName).
			append(
					" INCREMENT BY 1" +
					" START WITH ").append(startWith).append(
					" MAXVALUE " + Integer.MAX_VALUE +
					" MINVALUE ").append(startWith).append(
					" NOCYCLE" +
					" NOORDER");
	}
}

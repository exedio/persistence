/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class HsqldbDriver extends Driver
{
	public HsqldbDriver()
	{
		super(null, null);
	}

	@Override
	String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
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
			case Types.DATE:
				return "date";
			case Types.BINARY:
				return "binary";
			case Types.VARCHAR:
				final int columnSize = resultSet.getInt("COLUMN_SIZE");
				return "varchar("+columnSize+')';
			default:
				return null;
		}
	}
	
	private static final String SYSTEM_TABLE_CONSTRAINTS = "INFORMATION_SCHEMA.SYSTEM_TABLE_CONSTRAINTS";
	private static final String SYSTEM_CHECK_CONSTRAINTS = "INFORMATION_SCHEMA.SYSTEM_CHECK_CONSTRAINTS";
	private static final String SYSTEM_INDEXINFO = "INFORMATION_SCHEMA.SYSTEM_INDEXINFO";

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL(
				"select stc.CONSTRAINT_NAME, stc.CONSTRAINT_TYPE, stc.TABLE_NAME, scc.CHECK_CLAUSE " +
				"from " + SYSTEM_TABLE_CONSTRAINTS + " stc " +
				"left outer join " + SYSTEM_CHECK_CONSTRAINTS + " scc on stc.CONSTRAINT_NAME = scc.CONSTRAINT_NAME",
			new Node.ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						final String constraintName = resultSet.getString(1);
						final String constraintType = resultSet.getString(2);
						final String tableName = resultSet.getString(3);
						
						final Table table = schema.notifyExistentTable(tableName);
						
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
							final StringBuilder clause = new StringBuilder();
							final StringBuilder bf = new StringBuilder();
							bf.append("select COLUMN_NAME from " + SYSTEM_INDEXINFO + " where INDEX_NAME like 'SYS_IDX_").
								append(constraintName).
								append("_%' and NON_UNIQUE=false order by ORDINAL_POSITION");
							
							schema.querySQL(bf.toString(), new Node.ResultSetHandler()
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
								});

							table.notifyExistentUniqueConstraint(constraintName, clause.toString());
						}
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

					}
				}
			});
		schema.querySQL(
				"select SEQUENCE_NAME " +
				"from INFORMATION_SCHEMA.SYSTEM_SEQUENCES",
			new Node.ResultSetHandler()
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
		bf.append("alter table ").
			append(tableName).
			append(" alter column ").
			append(oldColumnName).
			append(" rename to ").
			append(newColumnName);
		return bf.toString();
	}

	@Override
	public String createColumn(final String tableName, final String columnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		throw new RuntimeException("not implemented");
	}

	@Override
	public String createSequence(final String sequenceName, final int startWith)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("create sequence ").
			append(sequenceName).
			append(
					" as integer" +
					" start with " + startWith +
					" increment by 1");
		return bf.toString();
	}
}

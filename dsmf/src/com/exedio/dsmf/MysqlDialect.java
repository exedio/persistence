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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.StringTokenizer;

import com.exedio.dsmf.Node.ResultSetHandler;

public final class MysqlDialect extends Dialect
{
	final String primaryKeyColumnName;
	
	public MysqlDialect(final String primaryKeyColumnName)
	{
		super(null, null);
		this.primaryKeyColumnName = primaryKeyColumnName;
	}
	
	private static final char QUOTE_CHARACTER = '`';

	/**
	 * Use backticks to quote names for mysql.
	 */
	@Override
	public String quoteName(final String name)
	{
		if(name.indexOf(QUOTE_CHARACTER)>=0)
			throw new IllegalArgumentException("database name contains forbidden characters: "+name);

		return QUOTE_CHARACTER + name + QUOTE_CHARACTER;
	}

	@Override
	public boolean supportsCheckConstraints()
	{
		return false;
	}

	@Override
	String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
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
				return "DATE";
			case Types.VARCHAR:
				return "varchar("+columnSize+") character set utf8 binary";
			case Types.LONGVARCHAR:
				switch(columnSize)
				{
					case 65535:      return "text character set utf8 binary";
					case 16277215:   return "mediumtext character set utf8 binary";
					case 2147483647: return "longtext character set utf8 binary";
					default:         return "LONGVARCHAR("+columnSize+')';
				}
			case Types.BINARY:
				switch(columnSize)
				{
					case 255:        return "TINYBLOB";
					default:         return "BINARY("+columnSize+')';
				}
			case Types.LONGVARBINARY:
				switch(columnSize)
				{
					case 65535:      return "BLOB";
					case 16277215:   return "MEDIUMBLOB";
					case 2147483647: return "LONGBLOB";
					default:         return "LONGVARBINARY("+columnSize+')';
				}
			default:
				return null;
		}
	}
	
	final String unQuoteName(final String quotedName)
	{
		final int length = quotedName.length();
		if(length<3)
			throw new RuntimeException(quotedName);
		if(quotedName.charAt(0)!=QUOTE_CHARACTER)
			throw new RuntimeException(quotedName);
		if(quotedName.charAt(length-1)!=QUOTE_CHARACTER)
			throw new RuntimeException(quotedName);

		return quotedName.substring(1, length-1);
	}

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);
		{
			for(final Table table : schema.getTables())
			{
				if(!table.exists())
					continue;
				
				{
					final StringBuilder bf = new StringBuilder();
					bf.append("show columns from ").
						append(quoteName(table.name));
					
					schema.querySQL(bf.toString(), new Node.ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								//printMeta(resultSet);
								while(resultSet.next())
								{
									//printRow(resultSet);
									final String key = resultSet.getString("Key");
									if("PRI".equals(key))
									{
										final String field = resultSet.getString("Field");
										if(primaryKeyColumnName.equals(field) && table.required())
										{
											for(final Constraint c : table.getConstraints())
											{
												if(c instanceof PrimaryKeyConstraint)
												{
													table.notifyExistentPrimaryKeyConstraint(c.name);
													break;
												}
											}
										}
										else
											table.notifyExistentPrimaryKeyConstraint(field+"_Pk");
									}
								}
							}
						});
				}
				{
					final StringBuilder bf = new StringBuilder();
					bf.append("show create table ").
						append(quoteName(table.name));
					
					schema.querySQL(bf.toString(), new ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								while(resultSet.next())
								{
									final String tableName = resultSet.getString("Table");
									final String createTable = resultSet.getString("Create Table");
									final Table table = schema.notifyExistentTable(tableName);
									//System.out.println("----------"+tableName+"----"+createTable);
									final StringTokenizer t = new StringTokenizer(createTable);
									for(String s = t.nextToken(); t.hasMoreTokens(); s = t.nextToken())
									{
										//System.out.println("----------"+tableName+"---------------"+s);
										if("CONSTRAINT".equals(s))
										{
											if(!t.hasMoreTokens())
												continue;
											final String quotedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------quotedName:"+quotedName);
											final String name = unQuoteName(quotedName);
											//System.out.println("----------"+tableName+"--------------------name:"+name);
											if(!t.hasMoreTokens() || !"FOREIGN".equals(t.nextToken()) ||
												!t.hasMoreTokens() || !"KEY".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											//final String source =
											t.nextToken();
											//System.out.println("----------"+tableName+"--------------------source:"+source);
											if(!t.hasMoreTokens() || !"REFERENCES".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											//final String targetTable =
											t.nextToken();
											//System.out.println("----------"+tableName+"--------------------targetTable:"+targetTable);
											if(!t.hasMoreTokens())
												continue;
											//final String targetAttribute =
											t.nextToken();
											//System.out.println("----------"+tableName+"--------------------targetAttribute:"+targetAttribute);
											
											table.notifyExistentForeignKeyConstraint(name);
										}
										//UNIQUE KEY `AttriEmptyItem_parKey_Unq` (`parent`,`key`)
										if("UNIQUE".equals(s))
										{
											if(!t.hasMoreTokens() || !"KEY".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											final String quotedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------quotedName:"+quotedName);
											final String name = unQuoteName(quotedName);
											//System.out.println("----------"+tableName+"--------------------name:"+name);
											if(!t.hasMoreTokens())
												continue;
											final String clause = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------clause:"+clause);

											final int clauseLengthM1 = clause.length()-1;
											table.notifyExistentUniqueConstraint(name, clause.charAt(clauseLengthM1)==',' ? clause.substring(0, clauseLengthM1) : clause);
										}
									}
								}
							}
						});
				}
			}
		}
	}

	@Override
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(" engine=innodb");
	}
	
	@Override
	boolean needsTargetColumnName()
	{
		return true;
	}
	
	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" change ").
			append(oldColumnName).
			append(' ').
			append(newColumnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	// TODO is same as hsqldb
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
	public String dropPrimaryKeyConstraint(final String tableName, final String constraintName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" drop primary key");
		return bf.toString();
	}
	
	@Override
	public String dropForeignKeyConstraint(final String tableName, final String constraintName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" drop foreign key ").
			append(constraintName);
		return bf.toString();
	}
	
	@Override
	public String dropUniqueConstraint(final String tableName, final String constraintName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" drop index ").
			append(constraintName);
		return bf.toString();
	}
	
	public static final String SEQUENCE_COLUMN = "x";
	
	@Override
	public String createSequence(final String sequenceName, final int startWith)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("create table ").
			append(sequenceName).
			append(" (" + SEQUENCE_COLUMN + " integer AUTO_INCREMENT PRIMARY KEY) engine=InnoDB AUTO_INCREMENT=" + (startWith+1));
		return bf.toString();
	}
	
	@Override
	public String dropSequence(final String sequenceName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("drop table ").
			append(sequenceName);
		return bf.toString();
	}
	
	@Deprecated // experimental api
	@Override
	public int deleteSchema(final Schema schema)
	{
		Connection connection = null;
		java.sql.Statement sqlStatement = null;
		final StringBuilder bf = new StringBuilder();
		try
		{
			connection = schema.connectionProvider.getConnection();
			sqlStatement = connection.createStatement();
			int rows = 0; 
			
			bf.setLength(0);
			bf.append("set FOREIGN_KEY_CHECKS=0");
			rows += sqlStatement.executeUpdate(bf.toString());
			
			for(final Table table : schema.getTables())
			{
				bf.setLength(0);
				bf.append("delete from ").
					append(quoteName(table.name));
				rows += sqlStatement.executeUpdate(bf.toString());
			}
			
			bf.setLength(0);
			bf.append("set FOREIGN_KEY_CHECKS=1");
			rows += sqlStatement.executeUpdate(bf.toString());
			
			for(final Sequence sequence : schema.getSequences())
			{
				bf.setLength(0);
				bf.append("truncate ").
					append(quoteName(sequence.name));
				rows += sqlStatement.executeUpdate(bf.toString());
				
				final int startWith = sequence.startWith;
				if(startWith!=0)
				{
					bf.setLength(0);
					bf.append("insert into ").
						append(sequence.name).
						append(" values(").
						append(startWith).
						append(')');
					sqlStatement.executeUpdate(bf.toString());
				}
			}
			
			return rows;
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, bf.toString());
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(connection!=null)
			{
				try
				{
					// do not put it into connection pool again
					// because foreign key constraints are disabled
					connection.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
}

/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.StringTokenizer;

import com.exedio.dsmf.Node.ResultSetHandler;

public final class MysqlDriver extends Driver
{
	final String primaryKeyColumnName;
	private final boolean toLowerCase;
	
	public MysqlDriver(final String primaryKeyColumnName, final boolean toLowerCase)
	{
		super(null, null);
		this.primaryKeyColumnName = primaryKeyColumnName;
		this.toLowerCase = toLowerCase;
		//System.out.println("toLowerCase:"+toLowerCase);
	}
	
	private static final char PROTECTOR = '`';

	/**
	 * Use backticks to protect name for mysql.
	 */
	@Override
	public String protectName(final String name)
	{
		if(name.indexOf(PROTECTOR)>=0)
			throw new RuntimeException("database name contains forbidden characters: "+name);

		return PROTECTOR + name + PROTECTOR;
	}

	@Override
	public boolean supportsCheckConstraints()
	{
		return false;
	}

	@Override
	public String canonizeTableName(final String tableName)
	{
		return toLowerCase ? tableName.toLowerCase() : tableName;
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
					default:         return "LONGVARCHAR("+columnSize+")";
				}
			case Types.BINARY:
				switch(columnSize)
				{
					case 255:        return "TINYBLOB";
					default:         return "BINARY("+columnSize+")";
				}
			case Types.LONGVARBINARY:
				switch(columnSize)
				{
					case 65535:      return "BLOB";
					case 16277215:   return "MEDIUMBLOB";
					case 2147483647: return "LONGBLOB";
					default:         return "LONGVARBINARY("+columnSize+")";
				}
			default:
				return null;
		}
	}
	
	private final String unprotectName(final String protectedName)
	{
		final int length = protectedName.length();
		if(length<3)
			throw new RuntimeException(protectedName);
		if(protectedName.charAt(0)!=MysqlDriver.PROTECTOR)
			throw new RuntimeException(protectedName);
		if(protectedName.charAt(length-1)!=MysqlDriver.PROTECTOR)
			throw new RuntimeException(protectedName);

		return protectedName.substring(1, protectedName.length()-1);
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
					final StringBuffer bf = new StringBuffer();
					bf.append("show columns from ").
						append(protectName(table.name));
					
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
					final StringBuffer bf = new StringBuffer();
					bf.append("show create table ").
						append(protectName(table.name));
					
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
											final String protectedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------protectedName:"+protectedName);
											final String name = unprotectName(protectedName);
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
											final String protectedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------protectedName:"+protectedName);
											final String name = unprotectName(protectedName);
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
	void appendTableCreateStatement(final StringBuffer bf)
	{
		bf.append(" engine=innodb");
	}
	
	@Override
	boolean needsTargetColumnName()
	{
		return true;
	}
	
	@Override
	String getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
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
	String getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	@Override
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

	@Override
	String getDropPrimaryKeyConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" drop primary key");
		return bf.toString();
	}
	
	@Override
	String getDropForeignKeyConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" drop foreign key ").
			append(protectName(constraintName));
		return bf.toString();
	}
	
	@Override
	String getDropUniqueConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" drop index ").
			append(protectName(constraintName));
		return bf.toString();
	}
}

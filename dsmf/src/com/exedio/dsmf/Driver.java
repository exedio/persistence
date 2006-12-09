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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class Driver
{
	final String schema;
	final String systemTableNamePrefix;

	Driver(final String schema, final String systemTableNamePrefix)
	{
		this.schema = schema;
		this.systemTableNamePrefix = systemTableNamePrefix;
	}
	
	/**
	 * Protects a database name from being interpreted as a SQL keyword.
	 * This is usually done by enclosing the name with some (database specific) delimiters.
	 * The default implementation uses double quotes as delimiter.
	 */
	public String protectName(final String name)
	{
		if(name.indexOf('"')>=0)
			throw new IllegalArgumentException("database name contains forbidden characters: "+name);
		
		return '"' + name + '"';
	}
	
	public boolean supportsCheckConstraints()
	{
		return true;
	}
	
	public String canonizeTableName(final String tableName)
	{
		return tableName;
	}

	abstract String getColumnType(int dataType, ResultSet resultSet) throws SQLException;

	void verify(final Schema schema)
	{
		schema.querySQL(Node.GET_TABLES, new Node.ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					while(resultSet.next())
					{
						final String tableName = resultSet.getString("TABLE_NAME");
						if(systemTableNamePrefix==null || !tableName.startsWith(systemTableNamePrefix))
						{
							//printRow(resultSet);
							schema.notifyExistentTable(tableName);
							//System.out.println("EXISTS:"+tableName);
						}
					}
				}
			});
		
		schema.querySQL(Node.GET_COLUMNS, new Node.ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					while(resultSet.next())
					{
						final String tableName = resultSet.getString("TABLE_NAME");
						final String columnName = resultSet.getString("COLUMN_NAME");
						final int dataType = resultSet.getInt("DATA_TYPE");
						
						final Table table = schema.getTable(tableName);
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
			});
	}
	
	void appendTableCreateStatement(final StringBuffer bf)
	{
		// empty default implementation
	}
	
	boolean needsTargetColumnName()
	{
		return false;
	}
	
	public abstract String getRenameColumnStatement(String tableName, String oldColumnName, String newColumnName, String columnType);
	public abstract String getCreateColumnStatement(String tableName, String columnName, String columnType);
	public abstract String getModifyColumnStatement(String tableName, String columnName, String newColumnType);

	private final String getDropConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(protectName(tableName)).
			append(" drop constraint ").
			append(protectName(constraintName));
		return bf.toString();
	}
	
	public String getDropPrimaryKeyConstraintStatement(final String tableName, final String constraintName)
	{
		return getDropConstraintStatement(tableName, constraintName);
	}
	
	public String getDropForeignKeyConstraintStatement(final String tableName, final String constraintName)
	{
		return getDropConstraintStatement(tableName, constraintName);
	}
	
	public String getDropUniqueConstraintStatement(final String tableName, final String constraintName)
	{
		return getDropConstraintStatement(tableName, constraintName);
	}

	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	protected static final void printMeta(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("------"+i+":"+metaData.getColumnName(i)+":"+metaData.getColumnType(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	protected static final void printRow(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("----------"+i+":"+resultSet.getObject(i));
	}
}

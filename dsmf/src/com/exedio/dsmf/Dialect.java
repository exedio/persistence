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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract class Dialect
{
	final String schema;

	Dialect(final String schema)
	{
		this.schema = schema;
	}

	/**
	 * Quotes a database name. This prevents the name from being interpreted as a SQL keyword.
	 * This is usually done by enclosing the name with some (database specific) quotation characters.
	 * The default implementation uses double quotes as specified by ANSI SQL.
	 */
	public String quoteName(final String name)
	{
		if(name.indexOf('"')>=0)
			throw new IllegalArgumentException("database name contains forbidden characters: "+name);

		return '"' + name + '"';
	}

	public boolean supportsCheckConstraints()
	{
		return true;
	}

	public boolean supportsSemicolon()
	{
		return true;
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
						if("BLOCKS".equals(tableName) || "LOBS".equals(tableName) || "LOB_IDS".equals(tableName))
							continue;
						//printRow(resultSet);

						schema.notifyExistentTable(tableName);
						//System.out.println("EXISTS:"+tableName);
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

	protected static final void verifyForeignKeyConstraints(final String sql, final Schema schema)
	{
		schema.querySQL(
			sql,
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(2);
						final Table table = schema.getTable(tableName);
						if(table!=null)
							table.notifyExistentForeignKeyConstraint(
									resultSet.getString(1), // constraintName
									resultSet.getString(3), // foreignKeyColumn
									resultSet.getString(4), // targetTable
									resultSet.getString(5)  // targetColumn
							);
					}
				}
			});
	}

	/**
	 * @param bf used in subclasses
	 */
	void appendTableCreateStatement(final StringBuilder bf)
	{
		// empty default implementation
	}

	boolean needsTargetColumnName()
	{
		return false;
	}

	/**
	 * @param bf used in subclasses
	 */
	void appendForeignKeyCreateStatement(final StringBuilder bf)
	{
		// empty default implementation
	}

	// derby needs a different syntax
	public String renameTable(final String tableName, final String newTableName)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" RENAME TO ").
			append(newTableName);
		return bf.toString();
	}

	public abstract String renameColumn(String tableName, String oldColumnName, String newColumnName, String columnType);
	public abstract String createColumn(String tableName, String columnName, String columnType);
	public abstract String modifyColumn(String tableName, String columnName, String newColumnType);

	private static final void dropConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP CONSTRAINT ").
			append(constraintName);
	}

	void dropPrimaryKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		dropConstraint(bf, tableName, constraintName);
	}

	void dropForeignKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		dropConstraint(bf, tableName, constraintName);
	}

	void dropUniqueConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		dropConstraint(bf, tableName, constraintName);
	}

	abstract void createSequence(StringBuilder bf, String sequenceName, int startWith);

	void dropSequence(final StringBuilder bf, final String sequenceName)
	{
		bf.append("DROP SEQUENCE ").
			append(sequenceName);
	}

	/**
	 * The default implementation just drops and re-creates the schema.
	 * Subclasses are encouraged to provide a more efficient implementation.
	 * @deprecated Use {@link com.exedio.cope.Model#deleteSchema()} instead.
	 */
	@Deprecated
	@SuppressWarnings("static-method")
	public final void deleteSchema(final Schema schema)
	{
		schema.drop();
		schema.create();
	}

	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	protected static final void printMeta(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("------"+i+":"+metaData.getColumnName(i)+":"+metaData.getColumnType(i));
	}

	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	protected static final void printRow(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("----------"+i+":"+resultSet.getObject(i));
	}
}

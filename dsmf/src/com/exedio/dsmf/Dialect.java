/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

public abstract class Dialect
{
	public static final String NOT_NULL = " not null";

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

	String normalizeCheckConstraintCondition(final String s)
	{
		return s;
	}

	abstract String getColumnType(int dataType, ResultSet resultSet) throws SQLException;

	abstract void verify(Schema schema);

	static final void verifyTablesByMetaData(final Schema schema)
	{
		schema.querySQL(Node.GET_TABLES, resultSet ->
		{
			final int TABLE_NAME = resultSet.findColumn("TABLE_NAME");
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(TABLE_NAME);
				schema.notifyExistentTable(tableName);
			}
		});
	}

	/**
	 * @param tableSchema null means any schema is allowed
	 */
	final void verifyColumnsByMetaData(final Schema schema, final String tableSchema)
	{
		schema.querySQL(Node.GET_COLUMNS, resultSet ->
		{
			final int TABLE_SCHEMA= resultSet.findColumn("TABLE_SCHEM"); // sic
			final int TABLE_NAME  = resultSet.findColumn("TABLE_NAME" );
			final int COLUMN_NAME = resultSet.findColumn("COLUMN_NAME");
			final int DATA_TYPE   = resultSet.findColumn("DATA_TYPE"  );
			while(resultSet.next())
			{
				if(tableSchema!=null &&
					!tableSchema.equals(resultSet.getString(TABLE_SCHEMA)))
					continue;

				final String columnName = resultSet.getString(COLUMN_NAME);
				final int    dataType   = resultSet.getInt   (DATA_TYPE  );

				final Table table = schema.getTableStrict(resultSet, TABLE_NAME);
				String columnType = getColumnType(dataType, resultSet);
				if(columnType==null)
					columnType = "DATA_TYPE(" + dataType + ')';

				table.notifyExistentColumn(columnName, columnType);
			}
		});
	}

	static final void verifyForeignKeyConstraints(final String sql, final Schema schema)
	{
		schema.querySQL(sql, resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = schema.getTableStrict(resultSet, 2);
				table.notifyExistentForeignKeyConstraint(
						resultSet.getString(1), // constraintName
						resultSet.getString(3), // foreignKeyColumn
						resultSet.getString(4), // targetTable
						resultSet.getString(5));// targetColumn
			}
		});
	}

	static final void verifyUniqueConstraints(final String sql, final Schema schema)
	{
		schema.querySQL(sql, resultSet ->
		{
			final UniqueConstraintCollector collector =
					new UniqueConstraintCollector(schema);
			while(resultSet.next())
			{
				final Table table = schema.getTableStrict(resultSet, 1);
				final String constraintName = resultSet.getString(2);
				final String columnName = resultSet.getString(3);
				collector.onColumn(table, constraintName, columnName);
			}
			collector.finish();
		});
	}

	static final void verifySequences(final String sql, final Schema schema)
	{
		schema.querySQL(sql, resultSet ->
		{
			while(resultSet.next())
			{
				final String name = resultSet.getString(1);
				final long maxValue = resultSet.getLong(2);
				schema.notifyExistentSequence(name, Sequence.Type.fromMaxValueExact(maxValue));
			}
		});
	}

	boolean getBooleanStrict(
			final ResultSet resultSet,
			final int columnIndex,
			final String trueValue,
			final String falseValue)
	throws SQLException
	{
		final String value = resultSet.getString(columnIndex);
		if(falseValue.equals(value))
			return false;
		else if(trueValue.equals(value))
			return true;

		final StringBuilder bf = new StringBuilder();
		bf.append("inconsistent boolean value, \"").
			append(trueValue).
			append("\"/\"").
			append(falseValue).
			append("\" required");

		append(bf, resultSet, columnIndex);

		throw new IllegalStateException(bf.toString());
	}

	static void append(
			final StringBuilder bf, final ResultSet resultSet,
			final int columnIndexMarked)
	throws SQLException
	{
		bf.append(", result set was");

		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();

		for(int i = 1; i<=columnCount; i++)
		{
			final Object o = resultSet.getObject(i);
			if(o==null)
				continue;

			bf.append(' ').
				append(metaData.getColumnName(i));
			if(i==columnIndexMarked)
				bf.append('*');
			bf.append('=').
				append(o);
		}
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

	abstract void createSequence(
			StringBuilder bf, String sequenceName,
			Sequence.Type type, long start);

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
	static final void print(final ResultSet resultSet) throws SQLException
	{
		final StringBuilder bf = new StringBuilder();

		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();

		boolean first = true;
		for(int i = 1; i<=columnCount; i++)
		{
			if(first)
				first = false;
			else
				bf.append(' ');

			bf.append(metaData.getColumnName(i)).
				append(':').
				append(metaData.getColumnType(i)).
				append('=').
				append(resultSet.getObject(i));
		}

		System.out.println(bf.toString());
	}
}

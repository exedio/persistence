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

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class Dialect
{
	public static final String NOT_NULL = " not null";

	private final String schema;

	Dialect(final String schema)
	{
		this.schema = schema;
	}

	final String getSchema()
	{
		return schema;
	}

	/**
	 * Quotes a database name. This prevents the name from being interpreted as a SQL keyword.
	 * This is usually done by enclosing the name with some (database specific) quotation characters.
	 * The default implementation uses double quotes as specified by ANSI SQL.
	 */
	public String quoteName(final String name)
	{
		// protection against SQL injection https://en.wikipedia.org/wiki/SQL_injection
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

	String adjustExistingCheckConstraintCondition(final String s)
	{
		return s;
	}

	abstract String getColumnType(int dataType, ResultSet resultSet) throws SQLException;

	abstract void verify(Schema schema);

	static final void verifyTablesByMetaData(final Schema schema)
	{
		schema.querySQL(GET_TABLES, resultSet ->
		{
			final int TABLE_NAME = resultSet.findColumn("TABLE_NAME");
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(TABLE_NAME);
				notifyExistentTable(schema, tableName);
			}
		});
	}

	/**
	 * @param tableSchema null means any schema is allowed
	 */
	final void verifyColumnsByMetaData(final Schema schema, final String tableSchema)
	{
		schema.querySQL(GET_COLUMNS, resultSet ->
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

				final Table table = getTableStrict(schema, resultSet, TABLE_NAME);
				String columnType = getColumnType(dataType, resultSet);
				if(columnType==null)
					columnType = "DATA_TYPE(" + dataType + ')';

				notifyExistentColumn(table, columnName, columnType);
			}
		});
	}

	static final void verifyForeignKeyConstraints(final String sql, final Schema schema)
	{
		schema.querySQL(sql, resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = getTableStrict(schema, resultSet, 2);
				notifyExistentForeignKey(table,
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
				final Table table = getTableStrict(schema, resultSet, 1);
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

	static final void notifyExistentTable(final Schema schema, final String tableName)
	{
		final Table result = schema.getTable(tableName);
		if(result==null)
			//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
			new Table(schema, tableName, false);
		else
			result.notifyExists();
	}

	static final Column notifyExistentColumn(
			final Table table,
			final String columnName,
			final String existingType)
	{
		Column result = table.getColumn(columnName);
		if(result==null)
			result = new Column(table, columnName, existingType, false);
		else
			result.notifyExists(existingType);

		return result;
	}

	static final void notifyExistentCheck(
			final Table table,
			final String constraintName,
			final String condition)
	{
		final Constraint result = table.getConstraint(constraintName);

		if(result==null)
			//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
			new CheckConstraint(table, null, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);
	}

	static final void notifyExistentPrimaryKey(
			final Table table,
			final String constraintName)
	{
		final Constraint result = table.getConstraint(constraintName);

		if(result==null)
			//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
			new PrimaryKeyConstraint(table, null, constraintName, false, null);
		else
			result.notifyExists();
	}

	static final void notifyExistentForeignKey(
			final Table table,
			final String constraintName,
			final String foreignKeyColumn,
			final String targetTable,
			final String targetColumn)
	{
		final ForeignKeyConstraint result = (ForeignKeyConstraint)table.getConstraint(constraintName);

		if(result==null)
			//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
			new ForeignKeyConstraint(
					table, table.getColumn(foreignKeyColumn), constraintName, false,
					foreignKeyColumn, targetTable, targetColumn);
		else
			result.notifyExists(foreignKeyColumn, targetTable, targetColumn);
	}

	static final void notifyExistentUnique(
			final Table table,
			final String constraintName,
			final String condition)
	{
		final Constraint result = table.getConstraint(constraintName);

		if(result==null)
			//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
			new UniqueConstraint(table, null, constraintName, false, condition);
		else
			result.notifyExistsCondition(condition);
	}

	static final class UniqueConstraintCollector
	{
		private final Schema schema;

		UniqueConstraintCollector(final Schema schema)
		{
			this.schema = requireNonNull(schema);
		}

		private Table table = null;
		private String name = null;
		private final ArrayList<String> columns = new ArrayList<>();

		void onColumn(
				final Table table,
				final String name,
				final String column)
		{
			requireNonNull(table);
			requireNonNull(name);
			requireNonNull(column);

			if(this.table==null)
			{
				this.table = table;
				this.name = name;
				this.columns.add(column);
			}
			else if(this.table==table && this.name.equals(name))
			{
				this.columns.add(column);
			}
			else
			{
				flush();
				this.table = table;
				this.name = name;
				this.columns.add(column);
			}
		}

		void finish()
		{
			if(table!=null)
				flush();
		}

		private void flush()
		{
			final StringBuilder bf = new StringBuilder();
			bf.append('(');
			boolean first = true;
			for(final String column: columns)
			{
				if(first)
					first = false;
				else
					bf.append(',');

				bf.append(schema.quoteName(column));
			}
			bf.append(')');
			notifyExistentUnique(table, name, bf.toString());

			this.table = null;
			this.name = null;
			this.columns.clear();
		}
	}

	static final class SequenceTypeMapper
	{
		private final String bit31;
		private final String bit63;

		SequenceTypeMapper(final String bit31, final String bit63)
		{
			this.bit31 = requireNonNull(bit31);
			this.bit63 = requireNonNull(bit63);
		}

		String map(final Sequence.Type type)
		{
			switch(type)
			{
				case bit31: return bit31;
				case bit63: return bit63;
				default:
					throw new RuntimeException("" + type);
			}
		}

		Sequence.Type unmap(final String string, final String message)
		{
			if(string.equals(bit31))
				return Sequence.Type.bit31;
			else if(string.equals(bit63))
				return Sequence.Type.bit63;
			else
				throw new IllegalArgumentException(string + '/' + message);
		}
	}

	static final boolean getBooleanStrict(
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

	static final Table getTableStrict(
			final Schema schema,
			final ResultSet resultSet,
			final int columnIndex) throws SQLException
	{
		final String name = resultSet.getString(columnIndex);
		final Table result = schema.getTable(name);
		if(result!=null)
			return result;

		final StringBuilder bf = new StringBuilder();
		bf.append("table \"").
				append(name).
				append("\" required");

		append(bf, resultSet, columnIndex);

		throw new IllegalStateException(bf.toString());
	}

	private static void append(
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

	private static final String GET_TABLES = "getTables";
	private static final String GET_COLUMNS = "getColumns";

	@FunctionalInterface
	interface ResultSetHandler
	{
		void run(ResultSet resultSet) throws SQLException;
	}

	@SuppressWarnings("StringEquality")
	@SuppressFBWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE"}) // Comparison of String parameter using == or !=
	final void querySQL(
			final ConnectionProvider connectionProvider,
			final String statement,
			final ResultSetHandler resultSetHandler)
	{
		Connection connection = null;
		try
		{
			//noinspection resource OK: have to use putConnection
			connection = connectionProvider.getConnection();
			//System.err.println(statement);

			if(GET_TABLES==statement)
			{
				try(ResultSet resultSet = connection.getMetaData().
						getTables(null, schema, null, new String[]{"TABLE"}))
				{
					resultSetHandler.run(resultSet);
				}
			}
			else if(GET_COLUMNS==statement)
			{
				try(ResultSet resultSet = connection.getMetaData().
						getColumns(null, schema, null, null))
				{
					resultSetHandler.run(resultSet);
				}
			}
			else
			{
				try(
					java.sql.Statement sqlStatement = connection.createStatement();
					ResultSet resultSet = sqlStatement.executeQuery(statement))
				{
					resultSetHandler.run(resultSet);
				}
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, statement);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					connectionProvider.putConnection(connection);
				}
				catch(final SQLException ignored)
				{
					// exception is already thrown
				}
			}
		}
	}

	static final String getCatalog(final Schema schema)
	{
		final ConnectionProvider connectionProvider = schema.connectionProvider;
		try
		{
			final Connection connection = connectionProvider.getConnection();
			try
			{
				return connection.getCatalog();
			}
			finally
			{
				connectionProvider.putConnection(connection);
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "getCatalog");
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
		return
				"ALTER TABLE " + tableName +
				" RENAME TO " + newTableName;
	}

	public abstract String renameColumn(String tableName, String oldColumnName, String newColumnName, String columnType);
	public abstract String createColumn(String tableName, String columnName, String columnType);
	public abstract String modifyColumn(String tableName, String columnName, String newColumnType);

	private static void dropConstraint(final StringBuilder bf, final String tableName, final String constraintName)
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
	@SuppressWarnings({"static-method", "MethodMayBeStatic"})
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

		System.out.println(bf);
	}
}

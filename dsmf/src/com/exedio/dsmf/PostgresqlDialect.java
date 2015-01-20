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

public final class PostgresqlDialect extends Dialect
{
	public PostgresqlDialect()
	{
		super(null);
	}

	@Override
	String normalizeCheckConstraintCondition(final String x)
	{
		final String s = x.
				replace("::bigint", "").
				replace("::text[]", "").
				replace("::\"text\"[]", "").
				replace("::text", "").
				replace("::\"text\"", "").
				replace("::character varying", "").
				replace("::double precision", "").
				replace("::numeric", "").
				replaceAll( " = ANY \\(*ARRAY\\[(.*?)]\\)*", " IN ($1)").
				replaceAll(" <> ALL \\(*ARRAY\\[(.*?)]\\)*", " NOT IN ($1)");

		final StringBuilder bf = new StringBuilder();
		final int l = s.length();
		for(int i = 0; i<l; i++)
		{
			final char c = s.charAt(i);
			switch(c)
			{
				case ' ':
				case '(':
				case ')':
					// omit character
					// TODO do omit outside string literals only
					break;
				default:
					bf.append(c);
			}
		}
		final String result = bf.toString();
		//System.out.println("---" + x + "---" + result + "---");
		return result;
	}

	public static final String SMALLINT  = "smallint";
	public static final String INTEGER   = "integer";
	public static final String BIGINT    = "bigint";
	public static final String DOUBLE    = "double precision";
	public static final int VARCHAR_LIMIT = 10485760;
	public static final String DATE      = "date";
	public static final String TIMESTAMP = "timestamp (3) without time zone"; // "3" are fractional digits retained in the seconds field, TODO fetch precision and time zone
	public static final String BINARY    = "bytea";

	@Override
	String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final String withoutNullable = getColumnTypeWithoutNullable(dataType, resultSet);
		if(withoutNullable==null)
			return null;

		final boolean nullable = resultSet.getBoolean("NULLABLE");
		if(nullable || "this".equals(resultSet.getString("COLUMN_NAME"))) // TODO does not work with primary key of table 'while'
			return withoutNullable;

		return withoutNullable + NOT_NULL;
	}

	private static String getColumnTypeWithoutNullable(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
		switch(dataType)
		{
			case Types.SMALLINT:  return SMALLINT;
			case Types.INTEGER:   return INTEGER;
			case Types.BIGINT:    return BIGINT;
			case Types.DOUBLE:    return DOUBLE;
			case Types.VARCHAR:
				if( columnSize<0 || columnSize>VARCHAR_LIMIT )
					return "text";
				else
					return "varchar("+columnSize+')';
			case Types.DATE:      return DATE;
			case Types.TIMESTAMP: return TIMESTAMP;
			case Types.BINARY:    return BINARY;
			default:
				return null;
		}
	}

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL(
				"SELECT " +
				"ut.relname," +
				"uc.conname," +
				"uc.contype," +
				"uc.consrc " +
				"FROM pg_constraint uc " +
				"INNER JOIN pg_class ut on uc.conrelid=ut.oid " +
				"WHERE ut.relname NOT LIKE 'pg_%' AND ut.relname NOT LIKE 'pga_%' AND uc.contype IN ('c','p')",
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(1);
						final String constraintName = resultSet.getString(2);
						final String constraintType = resultSet.getString(3);
						final Table table = schema.notifyExistentTable(tableName);
						//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
						if("c".equals(constraintType))
						{
							String searchCondition = resultSet.getString(4);
							//System.out.println("searchCondition:>"+searchCondition+"<");
							if(searchCondition.startsWith("(")&& searchCondition.endsWith(")"))
								searchCondition = searchCondition.substring(1, searchCondition.length()-1);
							table.notifyExistentCheckConstraint(constraintName, searchCondition);
						}
						else if("p".equals(constraintType))
							table.notifyExistentPrimaryKeyConstraint(constraintName);
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

						//System.out.println("EXISTS:"+tableName);
					}
				}
			});

		final String catalog = schema.getCatalog();

		schema.querySQL(
				"SELECT tc.table_name, tc.constraint_name, cu.column_name " +
				"FROM information_schema.table_constraints tc " +
				"JOIN information_schema.key_column_usage cu " +
				"ON tc.constraint_name=cu.constraint_name AND tc.table_name=cu.table_name " +
				"WHERE tc.constraint_type='UNIQUE' " +
				"AND tc.table_catalog='" + catalog + "' " +
				"AND cu.table_catalog='" + catalog + "' " +
				"ORDER BY tc.table_name, tc.constraint_name, cu.ordinal_position",
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					final ArrayList<String> condition = new ArrayList<>();
					Table lastTable = null;
					String lastConstraintName = null;
					while(resultSet.next())
					{
						//printRow(resultSet);
						final Table table = schema.getTable(resultSet.getString(1));
						if(table!=null)
						{
							final String constraintName = resultSet.getString(2);
							final String columnName = resultSet.getString(3);

							if(lastTable!=null && (table!=lastTable || !constraintName.equals(lastConstraintName)))
							{
								lastTable.notifyExistentUniqueConstraint(lastConstraintName, condition);
								condition.clear();
							}

							condition.add(columnName);
							lastTable = table;
							lastConstraintName = constraintName;
						}
					}
					if(lastTable!=null)
						lastTable.notifyExistentUniqueConstraint(lastConstraintName, condition);
				}
			});

		verifyForeignKeyConstraints(
				"SELECT rc.constraint_name, src.table_name, src.column_name, tgt.table_name, tgt.column_name " +
				"FROM information_schema.referential_constraints rc " +
				"JOIN information_schema.key_column_usage src ON rc.constraint_name=src.constraint_name " +
				"JOIN information_schema.key_column_usage tgt ON rc.unique_constraint_name=tgt.constraint_name " +
				"WHERE rc.constraint_catalog='" + catalog + '\'',
				schema);

		schema.querySQL(
				"SELECT sequence_name " +
				"FROM information_schema.sequences " +
				"WHERE sequence_catalog='" + catalog + '\'',
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
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(" WITHOUT OIDS");
	}

	// same as oracle
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

	// same as hsqldb
	@Override
	public String createColumn(final String tableName, final String columnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" ADD COLUMN ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	// same as oracle
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
	void createSequence(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		bf.append("CREATE SEQUENCE ").
			append(sequenceName).
			append(
					" INCREMENT BY 1" +
					" START WITH " + startWith +
					" MAXVALUE " + Integer.MAX_VALUE +
					" MINVALUE " + startWith +
					" NO CYCLE");
	}
}

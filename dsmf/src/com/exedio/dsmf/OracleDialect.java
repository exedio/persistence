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
import java.sql.SQLException;
import java.sql.Types;

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
		final String withoutNullable = getColumnTypeWithoutNullable(dataType, resultSet);
		if(withoutNullable==null)
			return null;

		final String nullable = resultSet.getString("IS_NULLABLE");
		if("YES".equals(nullable))
			return withoutNullable;
		else if("NO".equals(nullable))
		{
			if("this".equals(resultSet.getString("COLUMN_NAME"))) // TODO does not work with primary key of table 'while'
				return withoutNullable;

			return withoutNullable + NOT_NULL;
		}
		else
			throw new RuntimeException(
					resultSet.getString("TABLE_NAME") + '/' +
					resultSet.getString("COLUMN_NAME") + '/' +
					resultSet.getString("TYPE_NAME") + '/' +
					dataType + '/' +
					nullable + '/' +
					resultSet.getString("NULLABLE"));
	}

	private static String getColumnTypeWithoutNullable(final int dataType, final ResultSet resultSet) throws SQLException
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

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL(
				"SELECT TABLE_NAME FROM user_tables",
		resultSet ->
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				schema.notifyExistentTable(tableName);
			}
		});

		schema.querySQL(
				"SELECT " +
				"TABLE_NAME," + // 1
				"CONSTRAINT_NAME," + // 2
				"CONSTRAINT_TYPE," + // 3
				"SEARCH_CONDITION " + // 4
				"FROM user_constraints " +
				"WHERE CONSTRAINT_TYPE in ('C','P') " +
				"ORDER BY TABLE_NAME, CONSTRAINT_NAME",
		resultSet ->
		{
			while(resultSet.next())
			{
				//printRow(resultSet);
				final String tableName = resultSet.getString(1);
				final String constraintName = resultSet.getString(2);
				final String constraintType = resultSet.getString(3);
				final Table table = schema.notifyExistentTable(tableName);
				switch(constraintType)
				{
					case "C":
					{
						if(constraintName.startsWith("SYS_"))
							continue;

						final String searchCondition = resultSet.getString(4);
						table.notifyExistentCheckConstraint(constraintName, searchCondition);
						break;
					}
					case "P":
						table.notifyExistentPrimaryKeyConstraint(constraintName);
						break;
					default:
						throw new RuntimeException(constraintType+'-'+constraintName);
				}
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
				"SELECT " +
				"uc.TABLE_NAME," + // 1
				"uc.CONSTRAINT_NAME," + // 2
				"ucc.COLUMN_NAME " + // 3
				"FROM user_constraints uc " +
				"LEFT OUTER JOIN user_cons_columns ucc " +
					"ON uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
					"AND uc.TABLE_NAME=ucc.TABLE_NAME " +
				"WHERE uc.CONSTRAINT_TYPE='U' " +
				"ORDER BY uc.TABLE_NAME, uc.CONSTRAINT_NAME, ucc.POSITION",
		resultSet ->
		{
			final UniqueConstraintCollector collector =
					new UniqueConstraintCollector(schema);
			while(resultSet.next())
			{
				//printRow(resultSet);
				final String tableName = resultSet.getString(1);
				final String constraintName = resultSet.getString(2);
				final Table table = schema.notifyExistentTable(tableName);
				final String columnName = resultSet.getString(3);
				collector.onColumn(table, constraintName, columnName);
			}
			collector.finish();
		});

		schema.querySQL(
				"SELECT SEQUENCE_NAME, MAX_VALUE " +
				"FROM USER_SEQUENCES",
		resultSet ->
		{
			//printMeta(resultSet);
			while(resultSet.next())
			{
				//printRow(resultSet);
				final String name = resultSet.getString(1);
				final long maxValue = resultSet.getLong(2);
				schema.notifyExistentSequence(name, Sequence.Type.fromMaxValueExact(maxValue));
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
	void createSequence(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		createSequenceStatic(bf, sequenceName, type, start);
	}

	public static void createSequenceStatic(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		bf.append("CREATE SEQUENCE ").
			append(sequenceName).
			append(
					" INCREMENT BY 1" +
					" START WITH ").append(start).append(
					" MAXVALUE ").append(type.MAX_VALUE).append(
					" MINVALUE ").append(start).append(

					// BEWARE:
					// Without NOCACHE com.exedio.cope.OracleDialect#getNextSequence
					// returns wrong results!
					" NOCACHE" +

					" NOCYCLE" +
					" NOORDER");
	}
}

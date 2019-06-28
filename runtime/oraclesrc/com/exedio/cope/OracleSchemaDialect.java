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

package com.exedio.cope;

import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class OracleSchemaDialect extends Dialect
{
	OracleSchemaDialect(final String schema)
	{
		super(schema);
	}

	@Override
	public boolean supportsSemicolon()
	{
		return false;
	}

	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final String withoutNullable = getColumnTypeWithoutNullable(dataType, resultSet);
		if(withoutNullable==null)
			return null;

		return
			((getBooleanStrict(resultSet, resultSet.findColumn("IS_NULLABLE"), "YES", "NO") ||
				"this".equals(resultSet.getString("COLUMN_NAME")))) // TODO does not work with primary key of table 'while'
			? withoutNullable
			: withoutNullable + NOT_NULL;
	}

	private static String getColumnTypeWithoutNullable(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
		switch(dataType)
		{
			case Types.DECIMAL:
				final int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
				return NUMBER(columnSize, decimalDigits);
			case Types.OTHER:
			{
				final String typeName = resultSet.getString("TYPE_NAME");
				if("NVARCHAR2".equals(typeName))
				{
					if((columnSize%2)!=0)
						return "error: NVARCHAR2 with an odd COLUMN_SIZE: "+columnSize;

					return "NVARCHAR2("+(columnSize/2)+')';
				}
				else if(TIMESTAMP_3.equals(typeName))
					return TIMESTAMP_3;
				else if("NCLOB".equals(typeName))
					return "NCLOB";

				return "error: unknown TYPE_NAME for Types.OTHER: "+typeName;
			}
			case Types.VARCHAR:     return VARCHAR2(columnSize);
			case Types.TIMESTAMP:
			case Types.DATE:        return DATE;
			case Types.LONGVARCHAR: return "LONG";
			case Types.BLOB:        return BLOB;
			case Types.CLOB:        return CLOB;
			default:
				return null;
		}
	}

	static String NUMBER(final int size, final int decimalDigits)
	{
		return
				decimalDigits>0
				? "NUMBER("+size+','+decimalDigits+')'
				: "NUMBER("+size+')';
	}
	static final String TIMESTAMP_3 = "TIMESTAMP(3)";
	static String VARCHAR2(final int bytes) { return "VARCHAR2("+bytes+" BYTE)"; }
	static final String DATE = "DATE";
	static final String BLOB = "BLOB";
	static final String CLOB = "CLOB";

	@Override
	protected void verify(final Schema schema)
	{
		verifyTables(schema,
				"SELECT TABLE_NAME FROM user_tables");

		verifyColumnsByMetaData(schema, null);

		querySQL(schema,
				"SELECT " +
				"TABLE_NAME," + // 1
				"CONSTRAINT_NAME," + // 2
				"CONSTRAINT_TYPE," + // 3
				"SEARCH_CONDITION " + // 4
				"FROM user_constraints " +
				"WHERE CONSTRAINT_TYPE in ('C','P') " +
				"AND CONSTRAINT_NAME NOT LIKE 'SYS_%' " +
				"AND CONSTRAINT_NAME NOT LIKE 'BIN$%' " + // needed since Oracle 11.2
				"ORDER BY TABLE_NAME, CONSTRAINT_NAME",
		resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = getTableStrict(schema, resultSet, 1);
				final String constraintName = resultSet.getString(2);
				if(getBooleanStrict(resultSet, 3, "C", "P"))
				{
					notifyExistentCheck(table, constraintName, resultSet.getString(4));
				}
				else
				{
					notifyExistentPrimaryKey(table, constraintName);
				}
			}
		});

		verifyForeignKeyConstraints(schema,
				"SELECT " +
						"uc.CONSTRAINT_NAME, " + // 1
						"uc.TABLE_NAME, " + // 2
						"ucc.COLUMN_NAME, " + // 3
						"uic.TABLE_NAME, " + // 4
						"uic.COLUMN_NAME, " + // 5
						"uc.DELETE_RULE " + // 6
				"FROM USER_CONSTRAINTS uc " +
				"JOIN USER_cons_columns ucc ON uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
				"JOIN USER_IND_COLUMNS uic ON uc.R_CONSTRAINT_NAME=uic.INDEX_NAME " +
				"WHERE uc.CONSTRAINT_TYPE='R'",
				// https://docs.oracle.com/cd/E11882_01/server.112/e40540/datainte.htm#CNCPT1649
				"NO ACTION", null);

		verifyUniqueConstraints(schema,
				"SELECT " +
				"uc.TABLE_NAME," + // 1
				"uc.CONSTRAINT_NAME," + // 2
				"ucc.COLUMN_NAME " + // 3
				"FROM user_constraints uc " +
				"LEFT OUTER JOIN user_cons_columns ucc " +
					"ON uc.CONSTRAINT_NAME=ucc.CONSTRAINT_NAME " +
					"AND uc.TABLE_NAME=ucc.TABLE_NAME " +
				"WHERE uc.CONSTRAINT_TYPE='U' " +
				"AND uc.CONSTRAINT_NAME NOT LIKE 'BIN$%' " + // needed since Oracle 11.2
				"ORDER BY uc.TABLE_NAME, uc.CONSTRAINT_NAME, ucc.POSITION");

		verifySequences(schema,
				"SELECT SEQUENCE_NAME, MAX_VALUE, MIN_VALUE " +
				"FROM USER_SEQUENCES");
	}

	@Override
	public String createColumn(final String tableName, final String columnName, final String columnType)
	{
		return
				"ALTER TABLE " + tableName +
				" ADD (" + columnName + ' ' + columnType + ')';
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		return
				"ALTER TABLE " + tableName +
				" MODIFY " + columnName + ' ' + newColumnType;
	}

	@Override
	protected void appendForeignKeyCreateStatement(final StringBuilder bf)
	{
		bf.append(" DEFERRABLE");
	}

	@Override
	protected void createSequence(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		createSequenceStatic(bf, sequenceName, type, start);
	}

	static void createSequenceStatic(
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

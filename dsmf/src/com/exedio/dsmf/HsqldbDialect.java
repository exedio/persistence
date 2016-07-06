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

public final class HsqldbDialect extends Dialect
{
	public HsqldbDialect()
	{
		super(null);
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
			return withoutNullable + NOT_NULL;
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
		switch(dataType)
		{
			case Types.TINYINT:   return "TINYINT";
			case Types.SMALLINT:  return "SMALLINT";
			case Types.INTEGER:   return "INTEGER";
			case Types.BIGINT:    return "BIGINT";
			case Types.DOUBLE:    return "DOUBLE";
			case Types.TIMESTAMP: return "TIMESTAMP(3) WITHOUT TIME ZONE"; // TODO fetch precision and time zone from resultSet
			case Types.DATE:      return "DATE";
			case Types.BLOB:      return "BLOB";
			case Types.VARCHAR:
				final int columnSize = resultSet.getInt("COLUMN_SIZE");
				return "VARCHAR("+columnSize+')';
			default:
				return null;
		}
	}

	@Override
	void verify(final Schema schema)
	{
		verifyTablesByMetaData(schema);
		verifyColumnsByMetaData(schema);

		schema.querySQL(
				"SELECT tc.CONSTRAINT_NAME, tc.CONSTRAINT_TYPE, tc.TABLE_NAME, cc.CHECK_CLAUSE " +
				"FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME " +
				"WHERE tc.CONSTRAINT_TYPE IN ('CHECK','PRIMARY KEY','UNIQUE')",
		resultSet ->
		{
			while(resultSet.next())
			{
				final String constraintName = resultSet.getString(1);
				final String constraintType = resultSet.getString(2);
				final String tableName = resultSet.getString(3);

				if("BLOCKS".equals(tableName) || "LOBS".equals(tableName) || "LOB_IDS".equals(tableName))
					continue;
				final Table table = schema.notifyExistentTable(tableName);

				if("CHECK".equals(constraintType))
				{
					if(constraintName.startsWith("SYS_CT_"))
						continue;

					final String tablePrefix = quoteName(tableName)+'.';
					String checkClause = resultSet.getString(4);
					for(int pos = checkClause.indexOf(tablePrefix); pos>=0; pos = checkClause.indexOf(tablePrefix))
						checkClause = checkClause.substring(0, pos) + checkClause.substring(pos+tablePrefix.length());

					checkClause = checkClause.replace("PUBLIC.", "");

					table.notifyExistentCheckConstraint(constraintName, checkClause);
				}
				else if("PRIMARY KEY".equals(constraintType))
					table.notifyExistentPrimaryKeyConstraint(constraintName);
				else if("UNIQUE".equals(constraintType))
				{
					final StringBuilder clause = new StringBuilder();
					final StringBuilder bf = new StringBuilder();
					bf.append("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE INDEX_NAME LIKE 'SYS_IDX_").
						append(constraintName).
						append("_%' AND NON_UNIQUE=false ORDER BY ORDINAL_POSITION");

					schema.querySQL(bf.toString(), resultSetUnique ->
					{
						boolean first = true;
						clause.append('(');
						while(resultSetUnique.next())
						{
							if(first)
								first = false;
							else
								clause.append(',');
							final String columnName = resultSetUnique.getString(1);
							clause.append(quoteName(columnName));
						}
						clause.append(')');
					});

					table.notifyExistentUniqueConstraint(constraintName, clause.toString());
				}
				else
					throw new RuntimeException(constraintType+'-'+constraintName);
			}
		});

		verifyForeignKeyConstraints(
				"SELECT tc.CONSTRAINT_NAME, tc.TABLE_NAME, ccu.COLUMN_NAME, kcu.TABLE_NAME, kcu.COLUMN_NAME " +
				"FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu ON tc.CONSTRAINT_NAME=ccu.CONSTRAINT_NAME " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc ON tc.CONSTRAINT_NAME=rc.CONSTRAINT_NAME " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON rc.UNIQUE_CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
				"WHERE tc.CONSTRAINT_TYPE='FOREIGN KEY'",
				schema);

		schema.querySQL(
				"SELECT SEQUENCE_NAME, MAXIMUM_VALUE " +
				"FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES",
		resultSet ->
		{
			while(resultSet.next())
			{
				final String name = resultSet.getString(1);
				if("LOB_ID".equals(name))
					continue;
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
			append(" ALTER COLUMN ").
			append(oldColumnName).
			append(" RENAME TO ").
			append(newColumnName);
		return bf.toString();
	}

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

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" ALTER ").
			append(columnName).
			append(" SET DATA TYPE ").
			append(newColumnType);
		return bf.toString();
	}

	@Override
	void createSequence(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		bf.append("CREATE SEQUENCE ").
			append(sequenceName).
			append(
					" AS ").append(sequenceTypeMapper.map(type)).append(
					" START WITH ").append(start).append(
					" INCREMENT BY 1");
	}

	private static final SequenceTypeMapper sequenceTypeMapper = new SequenceTypeMapper("INTEGER", "BIGINT");
}

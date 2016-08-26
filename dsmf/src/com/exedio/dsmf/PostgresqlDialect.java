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

public final class PostgresqlDialect extends Dialect
{
	public PostgresqlDialect(final String schema)
	{
		super(schema);
	}

	@Override
	String normalizeCheckConstraintCondition(final String x)
	{
		final String s = x.
				replace("::bigint", "").
				replace("::integer", "").
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
				case '\'': // TODO because of "column">='-1'::integer, see bug 14296 https://www.postgresql.org/message-id/20160826144958.15674.41360%40wrigleys.postgresql.org
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

	@Override
	String getColumnType(final int dataType, final ResultSet resultSet)
	{
		throw new RuntimeException();
	}

	@Override
	void verify(final Schema schema)
	{
		verifyTablesByMetaData(schema);

		final String catalog = schema.getCatalog();

		schema.querySQL(
				"SELECT " +
						"table_name, " + // 1
						"column_name, " + // 2
						"is_nullable, " + // 3
						"data_type, " + // 4
						"character_maximum_length, " + // 5
						"datetime_precision " + // 6
				"FROM information_schema.columns " +
				"WHERE table_catalog='" + catalog + "' AND table_schema='" + this.schema + "' " +
				"ORDER BY ordinal_position", // make it deterministic for multiple unused columns in one table
		resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = schema.getTableStrict(resultSet, 1);
				final String columnName = resultSet.getString(2);

				final String notNull =
					(getBooleanStrict(resultSet, 3, "YES", "NO") ||
						"this".equals(columnName)) // TODO does not work with primary key of table 'while'
					? ""
					: NOT_NULL;

				final String dataType = resultSet.getString(4);
				final String type;
				switch(dataType)
				{
					case "character varying":
						type = dataType + '(' + resultSet.getInt(5) + ')' + notNull;
						break;
					case "timestamp without time zone":
						type = "timestamp (" + resultSet.getInt(6) + ") without time zone" + notNull;
						break;
					case "timestamp with time zone": // is never created by cope
						type = "timestamp (" + resultSet.getInt(6) + ") with time zone" + notNull;
						break;
					default:
						type = dataType + notNull;
						break;
				}

				table.notifyExistentColumn(columnName, type);
			}
		});

		schema.querySQL(
				"SELECT " +
						"ut.relname," + // 1
						"uc.conname," + // 2
						"uc.contype," + // 3
						"uc.consrc "  + // 4
				"FROM pg_constraint uc " +
				"INNER JOIN pg_class ut ON uc.conrelid=ut.oid " +
				"WHERE ut.relname NOT LIKE 'pg_%' AND ut.relname NOT LIKE 'pga_%' AND uc.contype IN ('c','p')",
		resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = schema.getTableStrict(resultSet, 1);
				final String constraintName = resultSet.getString(2);
				//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
				if(getBooleanStrict(resultSet, 3, "c", "p"))
				{
					String searchCondition = resultSet.getString(4);
					//System.out.println("searchCondition:>"+searchCondition+"<");
					if(searchCondition.startsWith("(")&& searchCondition.endsWith(")"))
						searchCondition = searchCondition.substring(1, searchCondition.length()-1);
					table.notifyExistentCheckConstraint(constraintName, searchCondition);
				}
				else
					table.notifyExistentPrimaryKeyConstraint(constraintName);

				//System.out.println("EXISTS:"+tableName);
			}
		});

		verifyUniqueConstraints(
				"SELECT tc.table_name, tc.constraint_name, cu.column_name " +
				"FROM information_schema.table_constraints tc " +
				"JOIN information_schema.key_column_usage cu " +
				"ON tc.constraint_name=cu.constraint_name AND tc.table_name=cu.table_name " +
				"WHERE tc.constraint_type='UNIQUE' " +
				"AND tc.table_catalog='" + catalog + "' " +
				"AND cu.table_catalog='" + catalog + "' " +
				"ORDER BY tc.table_name, tc.constraint_name, cu.ordinal_position",
				schema);

		verifyForeignKeyConstraints(
				"SELECT rc.constraint_name, src.table_name, src.column_name, tgt.table_name, tgt.column_name " +
				"FROM information_schema.referential_constraints rc " +
				"JOIN information_schema.key_column_usage src ON rc.constraint_name=src.constraint_name " +
				"JOIN information_schema.key_column_usage tgt ON rc.unique_constraint_name=tgt.constraint_name " +
				"WHERE rc.constraint_catalog='" + catalog + '\'',
				schema);

		verifySequences(
				"SELECT sequence_name, maximum_value " +
				"FROM information_schema.sequences " +
				"WHERE sequence_catalog='" + catalog + '\'',
				schema);
	}

	@Override
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(" WITH (OIDS=FALSE)");
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

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" ALTER ").
			append(columnName).
			append(" TYPE ").
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
					" INCREMENT BY 1" +
					" START WITH ").append(start).append(
					" MAXVALUE ").append(type.MAX_VALUE).append(
					" MINVALUE ").append(start).append(

					// CACHE 1 disables cache
					// http://www.postgresql.org/docs/9.3/static/sql-createsequence.html
					// BEWARE:
					// With CACHE enabled com.exedio.cope.PostgresqlDialect#getNextSequence
					// returns wrong results!
					" CACHE 1" +

					" NO CYCLE");
	}
}

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

import static com.exedio.cope.Dialect.strip;

import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.ResultSet;
import java.util.regex.Pattern;

final class PostgresqlSchemaDialect extends Dialect
{
	PostgresqlSchemaDialect(final String schema)
	{
		super(schema);

		final String digits = "\\d*";
		final Replacements cc = adjustExistingCheckConstraintCondition;
		// TODO do omit outside string literals only
		cc.add(p("(" + digits + ")")+"::bigint\\b", "$1"); // for DateField precision without native date
		cc.add(p("'(-?" + digits + "(?:\\." + digits + ")?)'::numeric")+"::double precision\\b", "$1");
		cc.add("'(-?" + digits + ")'::(?:integer|bigint)\\b", "$1"); // bug 14296 https://www.postgresql.org/message-id/20160826144958.15674.41360%40wrigleys.postgresql.org
		cc.add(  p("("  + digits + "(?:\\." + digits + ")?)") +"::double precision\\b", "$1");
		cc.add("('.*?')::character varying\\b", "$1");
		cc.add("('.*?')::\"text\"", "$1");
		cc.add(p("(\"\\w*\")")+"::\"text\"", "$1");
		cc.add(" (=|<>|>=|<=|>|<) ", "$1");
		cc.add("\"char_length\"(\\(\"\\w*\"\\))", "CHAR_LENGTH$1");
		cc.add("\"octet_length\"(\\(\"\\w*\"\\))", "OCTET_LENGTH$1");
		cc.add("\"floor\"(\\(\"date_part\"\\('SECOND', \"seconds\"\\)\\))", "FLOOR$1");
	}

	private static String p(final String s)
	{
		return "\\(" + s  + "\\)";
	}

	@Override
	protected String adjustExistingCheckConstraintCondition(String s)
	{
		s = adjustExistingCheckConstraintCondition.apply(s);
		s = adjustExistingCheckConstraintInCondition(s, checkClauseIn, "IN");
		s = adjustExistingCheckConstraintInCondition(s, checkClauseInText, "IN");
		s = adjustExistingCheckConstraintInCondition(s, checkClauseNotIn, "NOT IN");
		return s;
	}

	private static String adjustExistingCheckConstraintInCondition(
			final String s,
			final Pattern pattern,
			final String operator)
	{
		return pattern.matcher(s).replaceAll(matchResult ->
				matchResult.group(1) + ' ' + operator + " (" +
				checkClauseInnerComma.matcher(matchResult.group(2)).replaceAll(",") + ')');
	}

	private static final Pattern checkClauseIn = Pattern.compile("(\"\\w*\")=ANY "+p(p("ARRAY\\[(.*?)]")+"::\"text\"\\[\\]"));
	private static final Pattern checkClauseInText = Pattern.compile("(\"\\w*\")=ANY "+p("ARRAY\\[(.*?)]"));
	private static final Pattern checkClauseNotIn = Pattern.compile("(\"\\w*\")<>ALL "+p(p("ARRAY\\[(.*?)]")+"::\"text\"\\[\\]"));
	private static final Pattern checkClauseInnerComma = Pattern.compile(", ");

	private final Replacements adjustExistingCheckConstraintCondition = new Replacements();


	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet)
	{
		throw new RuntimeException();
	}

	@Override
	protected void verify(final Schema schema)
	{
		final String catalog = getCatalogLiteral(schema);
		final String schemaL = getSchemaLiteral();

		verifyTables(schema,
				//language=SQL
				"SELECT table_name " +
				"FROM information_schema.tables " +
				"WHERE table_catalog=" + catalog + " AND table_schema=" + schemaL + " " +
				"AND table_type='BASE TABLE' " +
				"ORDER BY table_name"); // make it deterministic for more than one unused table

		querySQL(schema,
				//language=SQL
				"SELECT " +
						"table_name, " + // 1
						"column_name, " + // 2
						"is_nullable, " + // 3
						"data_type, " + // 4
						"character_maximum_length, " + // 5
						"datetime_precision " + // 6
				"FROM information_schema.columns " +
				"WHERE table_catalog=" + catalog + " AND table_schema=" + schemaL + " " +
				"ORDER BY ordinal_position", // make it deterministic for multiple unused columns in one table
		resultSet ->
		{
			while(resultSet.next())
			{
				final String columnName = resultSet.getString(2);
				final String dataType = resultSet.getString(4);

				final StringBuilder type = new StringBuilder();
				switch(dataType)
				{
					case "character varying":
						type.append(dataType).append('(').append(resultSet.getInt(5)).append(')');
						break;
					case "timestamp without time zone":
						type.append("timestamp (").append(resultSet.getInt(6)).append(") without time zone");
						break;
					case "timestamp with time zone": // is never created by cope
						type.append("timestamp (").append(resultSet.getInt(6)).append(") with time zone");
						break;
					default:
						type.append(dataType);
						break;
				}

				if(!getBooleanStrict(resultSet, 3, "YES", "NO"))
					type.append(NOT_NULL);

				final Table table = getTableStrict(schema, resultSet, 1);
				notifyExistentColumn(table, columnName, type.toString());
			}
		});

		querySQL(schema,
				//language=SQL
				"SELECT " +
						"ut.relname," + // 1
						"uc.conname," + // 2
						"uc.contype," + // 3
						"pg_get_constraintdef(uc.oid) "  + // 4
				"FROM pg_constraint uc " +
				"INNER JOIN pg_class ut ON uc.conrelid=ut.oid " +
				"WHERE ut.relname NOT LIKE 'pg_%' AND ut.relname NOT LIKE 'pga_%' AND uc.contype IN ('c','p')",
		resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = getTableStrict(schema, resultSet, 1);
				final String constraintName = resultSet.getString(2);
				//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
				if(getBooleanStrict(resultSet, 3, "c", "p"))
				{
					final String searchCondition = strip(resultSet.getString(4), "CHECK ((", "))");
					//System.out.println("searchCondition:>"+searchCondition+"<");
					notifyExistentCheck(table, constraintName, searchCondition);
				}
				else
					notifyExistentPrimaryKey(table, constraintName);

				//System.out.println("EXISTS:"+tableName);
			}
		});

		verifyUniqueConstraints(schema,
				//language=SQL
				"SELECT tc.table_name, tc.constraint_name, cu.column_name " +
				"FROM information_schema.table_constraints tc " +
				"JOIN information_schema.key_column_usage cu " +
				"ON tc.constraint_name=cu.constraint_name AND tc.table_name=cu.table_name " +
				"WHERE tc.constraint_type='UNIQUE' " +
				"AND tc.constraint_catalog=" + catalog + " AND tc.constraint_schema=" + schemaL + " " +
				"AND tc.table_catalog="      + catalog + " AND tc.table_schema="      + schemaL + " " +
				"AND cu.constraint_catalog=" + catalog + " AND cu.constraint_schema=" + schemaL + " " +
				"AND cu.table_catalog="      + catalog + " AND cu.table_schema="      + schemaL + " " +
				"ORDER BY tc.table_name, tc.constraint_name, cu.ordinal_position");

		verifyForeignKeyConstraints(schema,
				//language=SQL
				"SELECT " +
						"rc.constraint_name, " + // 1
						"src.table_name, " + // 2
						"src.column_name, " + // 3
						"tgt.table_name, " + // 4
						"tgt.column_name, " + // 5
						"rc.delete_rule, " + // 6
						"rc.update_rule " + // 7
				"FROM information_schema.referential_constraints rc " +
				"JOIN information_schema.key_column_usage src ON rc.constraint_name=src.constraint_name " +
				"JOIN information_schema.key_column_usage tgt ON rc.unique_constraint_name=tgt.constraint_name " +
				"WHERE rc.constraint_catalog=" + catalog + " AND  rc.constraint_schema=" + schemaL + " " +
				"AND  src.constraint_catalog=" + catalog + " AND src.constraint_schema=" + schemaL + " " +
				"AND  src.table_catalog="      + catalog + " AND src.table_schema="      + schemaL + " " +
				"AND  tgt.constraint_catalog=" + catalog + " AND tgt.constraint_schema=" + schemaL + " " +
				"AND  tgt.table_catalog="      + catalog + " AND tgt.table_schema="      + schemaL,
				// https://www.postgresql.org/docs/9.6/sql-createtable.html
				"NO ACTION", "NO ACTION");

		verifySequences(schema,
				//language=SQL
				"SELECT sequence_name, maximum_value, start_value " +
				"FROM information_schema.sequences " +
				"WHERE sequence_catalog=" + catalog + " AND sequence_schema=" + schemaL);
	}

	@Override
	protected void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(" WITH (OIDS=FALSE)");
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		return
				"ALTER TABLE " + tableName +
				" ALTER " + columnName + " TYPE " + newColumnType;
	}

	@Override
	protected void createSequence(
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
					// https://www.postgresql.org/docs/9.6/sql-createsequence.html
					// BEWARE:
					// With CACHE enabled com.exedio.cope.PostgresqlDialect#getNextSequence
					// returns wrong results!
					" CACHE 1" +

					" NO CYCLE");
	}
}

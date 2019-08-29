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
import java.util.ArrayList;
import java.util.regex.Pattern;

final class PostgresqlSchemaDialect extends Dialect
{
	PostgresqlSchemaDialect(final String schema)
	{
		super(schema);

		final String digits = "\\d*";
		// TODO do omit outside string literals only
		add(p("(" + digits + ")")+"::bigint\\b", "$1"); // for DateField precision without native date
		add(p("'(-?" + digits + "(?:\\." + digits + ")?)'::numeric")+"::double precision\\b", "$1");
		add("'(-?" + digits + ")'::(?:integer|bigint)\\b", "$1"); // bug 14296 https://www.postgresql.org/message-id/20160826144958.15674.41360%40wrigleys.postgresql.org
		add(  p("("  + digits + "(?:\\." + digits + ")?)") +"::double precision\\b", "$1");
		add(p(p("(-" + digits +    "\\." + digits +   ")"))+"::double precision\\b", "$1");
		add("('.*?')::character varying\\b", "$1");
		add("('.*?')::\"text\"", "$1");
		add(p("(\"\\w*\")")+"::\"text\"", "$1");
		add( " = ANY "+  p("ARRAY\\[(.*?)]"),                     " IN ($1)");
		add( " = ANY "+p(p("ARRAY\\[(.*?)]")+"::\"text\"\\[\\]"), " IN ($1)");
		add(" <> ALL "+p(p("ARRAY\\[(.*?)]")+"::\"text\"\\[\\]"), " NOT IN ($1)");
		add(" (=|<>|>=|<=|>|<) ", "$1");
	}

	private static String p(final String s)
	{
		return "\\(" + s  + "\\)";
	}

	private void add(final String regex, final String replacement)
	{
		adjustExistingCheckConstraintCondition.add(new Replacement(Pattern.compile(regex), replacement));
	}

	@Override
	protected String adjustExistingCheckConstraintCondition(String s)
	{
		for(final Replacement replacement : adjustExistingCheckConstraintCondition)
			s = replacement.apply(s);
		return s;
	}

	private static final class Replacement
	{
		private final Pattern pattern;
		private final String replacement;

		Replacement(final Pattern pattern, final String replacement)
		{
			this.pattern = pattern;
			this.replacement = replacement;
		}

		String apply(final String s)
		{
			return pattern.matcher(s).replaceAll(replacement);
		}
	}

	private final ArrayList<Replacement> adjustExistingCheckConstraintCondition = new ArrayList<>();

	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet)
	{
		throw new RuntimeException();
	}

	@Override
	protected void verify(final Schema schema)
	{
		verifyTablesByMetaData(schema);

		final String catalog = getCatalog(schema);

		querySQL(schema,
				"SELECT " +
						"table_name, " + // 1
						"column_name, " + // 2
						"is_nullable, " + // 3
						"data_type, " + // 4
						"character_maximum_length, " + // 5
						"datetime_precision " + // 6
				"FROM information_schema.columns " +
				"WHERE table_catalog='" + catalog + "' AND table_schema='" + getSchema() + "' " +
				"ORDER BY ordinal_position", // make it deterministic for multiple unused columns in one table
		resultSet ->
		{
			while(resultSet.next())
			{
				final Table table = getTableStrict(schema, resultSet, 1);
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

				notifyExistentColumn(table, columnName, type);
			}
		});

		querySQL(schema,
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
				final Table table = getTableStrict(schema, resultSet, 1);
				final String constraintName = resultSet.getString(2);
				//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
				if(getBooleanStrict(resultSet, 3, "c", "p"))
				{
					String searchCondition = resultSet.getString(4);
					//System.out.println("searchCondition:>"+searchCondition+"<");
					if(searchCondition.startsWith("(")&& searchCondition.endsWith(")"))
						searchCondition = searchCondition.substring(1, searchCondition.length()-1);
					notifyExistentCheck(table, constraintName, searchCondition);
				}
				else
					notifyExistentPrimaryKey(table, constraintName);

				//System.out.println("EXISTS:"+tableName);
			}
		});

		verifyUniqueConstraints(schema,
				"SELECT tc.table_name, tc.constraint_name, cu.column_name " +
				"FROM information_schema.table_constraints tc " +
				"JOIN information_schema.key_column_usage cu " +
				"ON tc.constraint_name=cu.constraint_name AND tc.table_name=cu.table_name " +
				"WHERE tc.constraint_type='UNIQUE' " +
				"AND tc.table_catalog='" + catalog + "' " +
				"AND cu.table_catalog='" + catalog + "' " +
				"ORDER BY tc.table_name, tc.constraint_name, cu.ordinal_position");

		verifyForeignKeyConstraints(schema,
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
				"WHERE rc.constraint_catalog='" + catalog + '\'',
				// https://www.postgresql.org/docs/9.5/sql-createtable.html
				"NO ACTION", "NO ACTION");

		verifySequences(schema,
				"SELECT sequence_name, maximum_value, start_value " +
				"FROM information_schema.sequences " +
				"WHERE sequence_catalog='" + catalog + '\'');
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
					// https://www.postgresql.org/docs/9.3/static/sql-createsequence.html
					// BEWARE:
					// With CACHE enabled com.exedio.cope.PostgresqlDialect#getNextSequence
					// returns wrong results!
					" CACHE 1" +

					" NO CYCLE");
	}
}

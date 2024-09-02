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

import com.exedio.cope.HsqldbDialect.Props;
import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.regex.Pattern;

final class HsqldbSchemaDialect extends Dialect
{
	private final boolean supportsCheckConstraint;

	HsqldbSchemaDialect(final CopeProbe probe, final Props properties)
	{
		super(probe.properties.disableCheckConstraint);
		this.supportsCheckConstraint = properties.approximate.supportsCheckConstraint();
		final Replacements r = adjustExistingCheckConstraintCondition;
		r.add("(\"\\w*\")!=", "$1<>");
		r.add("NOT (REGEXP_MATCHES\\(\"\\w*\",'.*'\\))", "NOT ($1)");
	}

	@Override
	public boolean supportsCheckConstraint()
	{
		return supportsCheckConstraint;
	}

	@Override
	protected String adjustExistingCheckConstraintCondition(String s)
	{
		s = adjustExistingCheckConstraintInCondition(s, checkClauseNotIn, "NOT IN"); // checkClauseNotIn must come first, is more specific
		s = adjustExistingCheckConstraintInCondition(s, checkClauseIn, "IN");
		return adjustExistingCheckConstraintCondition.apply(s);
	}

	private static String adjustExistingCheckConstraintInCondition(
			final String s,
			final Pattern pattern,
			final String operator)
	{
		// https://sourceforge.net/tracker/?func=detail&atid=378131&aid=3101603&group_id=23316
		return pattern.matcher(s).replaceAll(matchResult ->
				matchResult.group(1) + ' ' + operator + " (" +
				checkClauseInnerComma.matcher(matchResult.group(2)).replaceAll(",") + ')');
	}

	private static final Pattern checkClauseIn = Pattern.compile("\\((\"\\w*\")\\) IN \\(\\((.*)\\)\\)");
	private static final Pattern checkClauseNotIn = Pattern.compile("NOT \\(\\((\"\\w*\")\\) IN \\(\\((.*)\\)\\)\\)");
	private static final Pattern checkClauseInnerComma = Pattern.compile("\\),\\(");

	private final Replacements adjustExistingCheckConstraintCondition = new Replacements();


	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final String withoutNullable = getColumnTypeWithoutNullable(dataType, resultSet);
		if(withoutNullable==null)
			return null;

		return
			getBooleanStrict(resultSet, resultSet.findColumn("IS_NULLABLE"), "YES", "NO")
			? withoutNullable
			: withoutNullable + NOT_NULL;
	}

	private static String getColumnTypeWithoutNullable(final int dataType, final ResultSet resultSet) throws SQLException
	{
		return switch(dataType)
		{
			case Types.TINYINT   -> TINYINT;
			case Types.SMALLINT  -> SMALLINT;
			case Types.INTEGER   -> INTEGER;
			case Types.BIGINT    -> BIGINT;
			case Types.DOUBLE    -> DOUBLE;
			case Types.TIMESTAMP -> TIMESTAMP_3; // TODO fetch precision and time zone from resultSet
			case Types.DATE      -> DATE;
			case Types.BLOB      -> BLOB;
			case Types.VARCHAR   -> VARCHAR(resultSet.getInt("COLUMN_SIZE"));
			default ->
				null;
		};
	}

	static final String TINYINT = "TINYINT";
	static final String SMALLINT = "SMALLINT";
	static final String INTEGER = "INTEGER";
	static final String BIGINT = "BIGINT";
	static final String DOUBLE = "DOUBLE";
	static final String TIMESTAMP_3 = "TIMESTAMP(3) WITHOUT TIME ZONE";
	static final String DATE = "DATE";
	static final String BLOB = "BLOB";
	static String VARCHAR(final int size) { return "VARCHAR(" + size + ')'; }

	@Override
	protected void verify(final Schema schema)
	{
		verifyTables(schema,
				//language=SQL
				"SELECT TABLE_NAME " +
				"FROM INFORMATION_SCHEMA.TABLES " +
				"WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_TYPE='BASE TABLE' " +
				"ORDER BY TABLE_NAME"); // make it deterministic for more than one unused table

		verifyColumnsByMetaData(schema, "PUBLIC");

		querySQL(schema,
				//language=SQL
				"SELECT " +
						"tc.CONSTRAINT_NAME, " + // 1
						"tc.CONSTRAINT_TYPE, " + // 2
						"tc.TABLE_NAME, " + // 3
						"cc.CHECK_CLAUSE " + // 4
				"FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME " +
				"WHERE tc.CONSTRAINT_TYPE IN ('CHECK','PRIMARY KEY','UNIQUE') " +
				"AND tc.CONSTRAINT_SCHEMA='PUBLIC' " +
				"AND tc.TABLE_SCHEMA='PUBLIC' " +
				"AND (tc.CONSTRAINT_TYPE<>'CHECK' OR tc.CONSTRAINT_NAME NOT LIKE 'SYS_CT_%')",
		resultSet ->
		{
			while(resultSet.next())
			{
				final String constraintName = resultSet.getString(1);
				final String constraintType = resultSet.getString(2);
				final Table table = getTableStrict(schema, resultSet, 3);

				if("CHECK".equals(constraintType))
				{
					final String tablePrefix = "PUBLIC." + quoteName(table.getName()) + '.';
					String checkClause = resultSet.getString(4);
					checkClause = checkClause.replace(tablePrefix, "");
					notifyExistentCheck(table, constraintName, checkClause);
				}
				else if("PRIMARY KEY".equals(constraintType))
					notifyExistentPrimaryKey(table, constraintName);
				else if("UNIQUE".equals(constraintType))
				{
					final StringBuilder clause = new StringBuilder();
					querySQL(schema,
							//language=SQL
							"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE INDEX_NAME='" +
							constraintName +
							"' AND NON_UNIQUE=false ORDER BY ORDINAL_POSITION",
					resultSetUnique ->
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

					notifyExistentUnique(table, constraintName, clause.toString());
				}
				else
					throw new RuntimeException(constraintType+'-'+constraintName);
			}
		});

		verifyForeignKeyConstraints(schema,
				//language=SQL
				"SELECT " +
						"tc.CONSTRAINT_NAME, " + // 1
						"tc.TABLE_NAME, " + // 2
						"ccu.COLUMN_NAME, " + // 3
						"kcu.TABLE_NAME, " + // 4
						"kcu.COLUMN_NAME, " + // 5
						"rc.DELETE_RULE, " + // 6
						"rc.UPDATE_RULE " + // 7
				"FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu ON tc.CONSTRAINT_NAME=ccu.CONSTRAINT_NAME " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc ON tc.CONSTRAINT_NAME=rc.CONSTRAINT_NAME " +
				"LEFT OUTER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu ON rc.UNIQUE_CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
				"WHERE tc.CONSTRAINT_TYPE='FOREIGN KEY'",
				// from HyperSQL User Guide, HyperSQL Database Engine 2.5.0, page 63:
				// The default is NO ACTION. This means the SQL statement that causes
				// the DELETE or UPDATE is terminated with an exception.
				// The RESTRICT option is similar and works exactly the same without
				// deferrable constraints (which are not allowed by HyperSQL).
				"NO ACTION", "NO ACTION");

		verifySequences(schema,
				//language=SQL
				"SELECT SEQUENCE_NAME, MAXIMUM_VALUE, START_WITH " +
				"FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES " +
				"WHERE SEQUENCE_SCHEMA='PUBLIC'");
	}

	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		return
				"ALTER TABLE " + tableName +
				" ALTER COLUMN " + oldColumnName + " RENAME TO " + newColumnName;
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		return
				"ALTER TABLE " + tableName +
				" ALTER " + columnName + " SET DATA TYPE " + newColumnType;
	}

	@Override
	protected void createSequence(
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

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

import static com.exedio.cope.MysqlDialect.sequenceColumnName;
import static java.util.Objects.requireNonNull;

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.ForeignKeyConstraint;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

final class MysqlSchemaDialect extends Dialect
{
	private final boolean mysql80;
	private final boolean renameColumn;
	private final String foreignKeyRule;
	private final String rowFormat;

	MysqlSchemaDialect(
			final CopeProbe probe,
			final MysqlProperties properties)
	{
		super(null);
		mysql80 = probe.environmentInfo.isDatabaseVersionAtLeast(8, 0);
		this.renameColumn = mysql80; // supported since MySQL 8.0.3: https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-3.html#mysqld-8-0-3-sql-syntax
		// https://dev.mysql.com/doc/refman/5.7/en/create-table-foreign-keys.html#foreign-keys-referential-actions
		// RESTRICT and NO ACTION are the same, but are reported differently when omitting the ON DELETE / ON UPDATE clauses
		this.foreignKeyRule = mysql80 ? "NO ACTION" : "RESTRICT";
		this.rowFormat = properties.rowFormat.sql();
	}

	private static final char QUOTE_CHARACTER = '`';

	/**
	 * Use backticks to quote names for mysql.
	 */
	@Override
	public String quoteName(final String name)
	{
		// protection against SQL injection https://en.wikipedia.org/wiki/SQL_injection
		if(name.indexOf(QUOTE_CHARACTER)>=0)
			throw new IllegalArgumentException("database name contains forbidden characters: "+name);

		return QUOTE_CHARACTER + name + QUOTE_CHARACTER;
	}

	@Override
	public boolean supportsCheckConstraints()
	{
		return false;
	}

	@Override
	protected String getColumnType(final int dataType, final ResultSet resultSet)
	{
		throw new RuntimeException();
	}

	@Override
	protected void verify(final Schema schema)
	{
		final String catalog = getCatalogLiteral(schema);

		querySQL(schema,
				//language=SQL
				"SELECT TABLE_NAME, ENGINE " +
				"FROM information_schema.TABLES " +
				"WHERE TABLE_SCHEMA=" + catalog + " AND TABLE_TYPE='BASE TABLE' " +
				"ORDER BY TABLE_NAME", // make it deterministic for more than one unused table
		resultSet ->
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final Sequence sequence = schema.getSequence(tableName);
				if(sequence==null || !sequence.required())
				{
					final Table table = notifyExistentTable(schema, tableName);
					final String engine = resultSet.getString(2);
					if(!ENGINE.equals(engine))
						notifyAdditionalError(table, "unexpected engine >" + engine + '<');
				}
			}
		});

		querySQL(schema,
				//language=SQL
				"SELECT " +
						"c.TABLE_NAME," + // 1
						"c.COLUMN_NAME," + // 2
						"c.IS_NULLABLE," + // 3
						"c.DATA_TYPE," + // 4
						"c.CHARACTER_MAXIMUM_LENGTH," + // 5
						"c.DATETIME_PRECISION," + // 6
						"c.CHARACTER_SET_NAME," + // 7
						"c.COLLATION_NAME " + // 8
				"FROM information_schema.COLUMNS c " +
				"WHERE c.TABLE_SCHEMA=" + catalog + " " +
				"AND c.TABLE_NAME IN " +
				"(" +
						// On MySQL 5.7 subquery is twice as fast as a join.
						// On MySQL 8 there is no difference between subquery and join.
						"SELECT t.TABLE_NAME " +
						"FROM information_schema.TABLES t " +
						"WHERE t.TABLE_SCHEMA=" + catalog + " " +
						"AND t.TABLE_TYPE='BASE TABLE'" + // excludes views (CREATE VIEW) from result
				") " +
				"ORDER BY c.ORDINAL_POSITION", // make it deterministic for multiple unused columns in one table
		resultSet ->
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final String columnName = resultSet.getString(2);
				final String dataType = resultSet.getString(4);
				{
					final Sequence sequence = schema.getSequence(tableName);
					if(sequence!=null && sequenceColumnName.equals(columnName))
					{
						notifyExists(sequence, sequenceTypeMapper.unmap(dataType, columnName));
						continue;
					}
				}

				final StringBuilder type = new StringBuilder(dataType);
				if("varchar".equals(dataType))
					type.append('(').append(resultSet.getInt(5)).append(')');

				{
					final int datetimePrecision = resultSet.getInt(6);
					if(!resultSet.wasNull())
						type.append('(').append(datetimePrecision).append(')');
				}
				{
					final String characterSet = resultSet.getString(7);
					if(characterSet!=null)
						type.append(" CHARACTER SET ").append(characterSet);
				}
				{
					final String collation = resultSet.getString(8);
					if(collation!=null)
						type.append(" COLLATE ").append(collation);
				}

				if(!getBooleanStrict(resultSet, 3, "YES", "NO"))
					type.append(NOT_NULL);

				final Table table = getTableStrict(schema, resultSet, 1);
				notifyExistentColumn(table, columnName, type.toString());
			}
		});

		{
			// Querying REFERENTIAL_CONSTRAINTS and KEY_COLUMN_USAGE separately is faster
			// than a database join between both tables.
			// For an example schema with 532 foreign key constraints query times did improve
			// as listed below:
			//   MySQL 5.7.27: 0,4sec -> 0,09sec
			//   MySQL 8.0.27: 8sec   -> 0,02sec
			final HashMap<String, ForeignKeyConstraintCollector> fkCollectors = new HashMap<>();
			querySQL(schema,
					//language=SQL
					"SELECT " +
							"CONSTRAINT_NAME, " + // 1
							"TABLE_NAME, " + // 2
							"REFERENCED_TABLE_NAME, " + // 3
							"DELETE_RULE, " + // 4
							"UPDATE_RULE " + // 5
					"FROM information_schema.REFERENTIAL_CONSTRAINTS " +
					"WHERE CONSTRAINT_SCHEMA=" + catalog + " " +
							"AND UNIQUE_CONSTRAINT_SCHEMA=" + catalog,
			resultSet ->
			{
				while(resultSet.next())
				{
					final String constraintName = resultSet.getString(1);
					final ForeignKeyConstraintCollector collector = new ForeignKeyConstraintCollector(
							getTableStrict(schema, resultSet, 2),
							resultSet.getString(3),
							resultSet.getString(4),
							resultSet.getString(5));
					final ForeignKeyConstraintCollector collision =
							fkCollectors.putIfAbsent(constraintName, collector);
					if(collision!=null)
						throw new RuntimeException(constraintName + '|' + collector + '|' + collision);
				}
			});
			querySQL(schema,
					//language=SQL
					"SELECT " +
							"CONSTRAINT_NAME, " + // 1
							"COLUMN_NAME, " + // 2
							"REFERENCED_COLUMN_NAME " + // 3
					"FROM information_schema.KEY_COLUMN_USAGE " +
					"WHERE CONSTRAINT_SCHEMA=" + catalog + " " +
							"AND REFERENCED_COLUMN_NAME IS NOT NULL",
			resultSet ->
			{
				while(resultSet.next())
				{
					final String constraintName = resultSet.getString(1);
					final ForeignKeyConstraintCollector collector =
							requireNonNull(fkCollectors.get(constraintName), constraintName);
					collector.setKeyColumnUsage(
							resultSet.getString(2),
							resultSet.getString(3));
				}
			});
			for(final Map.Entry<String, ForeignKeyConstraintCollector> e : fkCollectors.entrySet())
			{
				final String constraintName = e.getKey();
				final ForeignKeyConstraintCollector collector = e.getValue();
				final ForeignKeyConstraint constraint = notifyExistentForeignKey(
						collector.table,
						constraintName,
						collector.columnName(), // foreignKeyColumn
						collector.referencedTableName, // targetTable
						collector.referencedColumnName());// targetColumn

				verifyForeignKeyConstraintRule(constraint, "delete", foreignKeyRule, collector.deleteRule);
				verifyForeignKeyConstraintRule(constraint, "update", foreignKeyRule, collector.updateRule);
			}
		}

		final String PRIMARY_KEY = "PRIMARY KEY";
		final String UNIQUE = "UNIQUE";
		querySQL(schema,
				//language=SQL
				"SELECT " +
						"tc.CONSTRAINT_NAME," + // 1
						"tc.TABLE_NAME," + // 2
						"tc.CONSTRAINT_TYPE," + // 3
						"kcu.COLUMN_NAME " + // 4
				"FROM information_schema.TABLE_CONSTRAINTS tc " +
				"LEFT JOIN information_schema.KEY_COLUMN_USAGE kcu " +
						"ON tc.CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
						"AND tc.TABLE_NAME=kcu.TABLE_NAME " +
						"AND kcu.CONSTRAINT_SCHEMA=" + catalog + " " +
				"WHERE tc.CONSTRAINT_SCHEMA=" + catalog + " " +
						"AND tc.TABLE_SCHEMA=" + catalog + " " +
						"AND tc.CONSTRAINT_TYPE IN ('" + PRIMARY_KEY + "','" + UNIQUE + "') " +
				"ORDER BY tc.TABLE_NAME,tc.CONSTRAINT_NAME,kcu.ORDINAL_POSITION ",
		resultSet ->
		{
			final UniqueConstraintCollector uniqueConstraintCollector =
					new UniqueConstraintCollector(schema);
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(2);
				final String columnName = resultSet.getString(4);

				final Sequence sequence = schema.getSequence(tableName);
				if(sequence!=null && sequence.required())
					continue;

				final Table table = getTableStrict(schema, resultSet, 2);

				if(getBooleanStrict(resultSet, 3, PRIMARY_KEY, UNIQUE))
				{
					if(table.required())
					{
						boolean found = false;
						for(final Constraint c : table.getConstraints())
						{
							if(c instanceof PrimaryKeyConstraint &&
								((PrimaryKeyConstraint)c).getPrimaryKeyColumn().equals(columnName))
							{
								notifyExistentPrimaryKey(table, c.getName());
								found = true;
								break;
							}
						}
						if(!found)
							notifyExistentPrimaryKey(table, columnName+"_Pk");
					}
				}
				else
				{
					uniqueConstraintCollector.onColumn(table, resultSet.getString(1), columnName);
				}
			}
			uniqueConstraintCollector.finish();
		});
	}

	private static final String ENGINE = "InnoDB";
	private static final String ENGINE_CLAUSE = " ENGINE=" + ENGINE;

	@Override
	protected void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(ENGINE_CLAUSE);
		appendRowFormat(bf);
	}

	private void appendRowFormat(final StringBuilder bf)
	{
		if(rowFormat!=null)
			bf.append(" ROW_FORMAT=").
				append(rowFormat);
	}

	@Override
	protected boolean needsTargetColumnName()
	{
		return true;
	}

	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		if(renameColumn)
			return super.renameColumn(tableName, oldColumnName, newColumnName, columnType);

		return
				"ALTER TABLE " + tableName +
				" CHANGE " + oldColumnName + ' ' + newColumnName + ' ' + columnType;
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		return
				"ALTER TABLE " + tableName +
				" MODIFY " + columnName + ' ' + newColumnType;
	}

	@Override
	protected void dropPrimaryKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP PRIMARY KEY");
	}

	@Override
	protected void dropForeignKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		if(mysql80) // supported since MySQL 8.0.19: https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-19.html#mysqld-8-0-19-sql-syntax
		{
			super.dropForeignKeyConstraint(bf, tableName, constraintName);
			return;
		}

		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP FOREIGN KEY ").
			append(constraintName);
	}

	@Override
	protected void dropUniqueConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		if(mysql80) // supported since MySQL 8.0.19: https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-19.html#mysqld-8-0-19-sql-syntax
		{
			super.dropUniqueConstraint(bf, tableName, constraintName);
			return;
		}

		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP INDEX ").
			append(constraintName);
	}

	@Override
	protected void createSequence(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		// TODO support CREATE SEQUENCE in MariaDB 10.3 https://mariadb.com/kb/en/library/create-sequence/

		bf.append("CREATE TABLE ").
			append(sequenceName).
			append("(").
				append(quoteName(sequenceColumnName)).
				append(' ').
				// must not use TINYINT, SMALLINT, MEDIUMINT as this is allowed only for
				// MysqlProperties.smallIntegerTypes=true
				append(sequenceTypeMapper.map(type)).
				append(" AUTO_INCREMENT PRIMARY KEY)" +
			ENGINE_CLAUSE +
			" COMMENT='cope_sequence_table'");

		appendRowFormat(bf);
		initializeSequence(bf, sequenceName, start);
	}

	static final SequenceTypeMapper sequenceTypeMapper = new SequenceTypeMapper("int", "bigint");

	static void initializeSequence(
			final StringBuilder bf, final String sequenceName,
			final long start)
	{
		// From the MySQL documentation:
		//
		//    InnoDB supports the AUTO_INCREMENT = N table option in CREATE TABLE
		//    and ALTER TABLE statements, to set the initial counter value or alter
		//    the current counter value. The effect of this option is canceled by
		//    a server restart, for reasons discussed earlier in this section.
		//
		// means that the AUTO_INCREMENT table option cannot be used reliably for cope.
		if(start!=0)
		{
			bf.append(";INSERT INTO ").
				append(sequenceName).
				append(" VALUES(").
				append(start).
				append(')');
		}
	}

	@Override
	protected void dropSequence(final StringBuilder bf, final String sequenceName)
	{
		bf.append("DROP TABLE ").
			append(sequenceName);
	}
}

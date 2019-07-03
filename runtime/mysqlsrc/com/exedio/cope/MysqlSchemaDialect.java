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

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Dialect;
import com.exedio.dsmf.PrimaryKeyConstraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import com.exedio.dsmf.Table;
import java.sql.ResultSet;

final class MysqlSchemaDialect extends Dialect
{
	private final boolean datetime;
	final String sequenceColumnName;
	private final String rowFormat;

	MysqlSchemaDialect(
			final boolean datetime,
			final String sequenceColumnName,
			final String rowFormat)
	{
		super(null);
		this.datetime = datetime;
		this.sequenceColumnName = sequenceColumnName;
		this.rowFormat = rowFormat;
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
		final String catalog = getCatalog(schema);

		querySQL(schema,
				"SELECT TABLE_NAME, ENGINE " +
				"FROM information_schema.TABLES " +
				"WHERE TABLE_SCHEMA='" + catalog + "' AND TABLE_TYPE='BASE TABLE'",
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
				"SELECT " +
						"c.TABLE_NAME," + // 1
						"c.COLUMN_NAME," + // 2
						"c.IS_NULLABLE," + // 3
						"c.DATA_TYPE," + // 4
						"c.CHARACTER_MAXIMUM_LENGTH," + // 5
						(datetime?"c.DATETIME_PRECISION,":"") + // 6
						"c.CHARACTER_SET_NAME," + // 7
						"c.COLLATION_NAME," + // 8
						"c.COLUMN_KEY " + // 9
				"FROM information_schema.COLUMNS c " +
				"JOIN information_schema.TABLES t " +
					"ON c.TABLE_SCHEMA=t.TABLE_SCHEMA " +
					"AND c.TABLE_NAME=t.TABLE_NAME " +
				"WHERE c.TABLE_SCHEMA='" + catalog + "' " +
				"AND t.TABLE_TYPE='BASE TABLE' " +
				"ORDER BY c.ORDINAL_POSITION", // make it deterministic for multiple unused columns in one table
		resultSet ->
		{
			final int datetimeOffset = datetime ? 0 : 1;
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

				if(datetime)
				{
					final int datetimePrecision = resultSet.getInt(6);
					if(!resultSet.wasNull())
						type.append('(').append(datetimePrecision).append(')');
				}
				{
					final String characterSet = resultSet.getString(7-datetimeOffset);
					if(characterSet!=null)
						type.append(" CHARACTER SET ").append(characterSet);
				}
				{
					final String collation = resultSet.getString(8-datetimeOffset);
					if(collation!=null)
						type.append(" COLLATE ").append(collation);
				}

				if(!getBooleanStrict(resultSet, 3, "YES", "NO") &&
						!"PRI".equals(resultSet.getString(9-datetimeOffset)))
					type.append(NOT_NULL);

				final Table table = getTableStrict(schema, resultSet, 1);
				notifyExistentColumn(table, columnName, type.toString());
			}
		});

		verifyForeignKeyConstraints(
				"SELECT " +
						"rc.CONSTRAINT_NAME, " + // 1
						"rc.TABLE_NAME, " + // 2
						"kcu.COLUMN_NAME, " + // 3
						"rc.REFERENCED_TABLE_NAME, " + // 4
						"kcu.REFERENCED_COLUMN_NAME, " + // 5
						"rc.DELETE_RULE, " + // 6
						"rc.UPDATE_RULE " + // 7
				"FROM information_schema.REFERENTIAL_CONSTRAINTS rc " +
				"LEFT JOIN information_schema.KEY_COLUMN_USAGE kcu " +
						"ON rc.CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
						"AND kcu.CONSTRAINT_SCHEMA='" + catalog + "' " +
				"WHERE rc.CONSTRAINT_SCHEMA='" + catalog + "' " +
						"AND rc.UNIQUE_CONSTRAINT_SCHEMA='" + catalog + '\'',
				schema,

				// https://dev.mysql.com/doc/refman/5.7/en/create-table-foreign-keys.html#foreign-keys-referential-actions
				// same as NO ACTION
				"RESTRICT", "RESTRICT");

		final String PRIMARY_KEY = "PRIMARY KEY";
		final String UNIQUE = "UNIQUE";
		querySQL(schema,
				"SELECT " +
						"tc.CONSTRAINT_NAME," + // 1
						"tc.TABLE_NAME," + // 2
						"tc.CONSTRAINT_TYPE," + // 3
						"kcu.COLUMN_NAME " + // 4
				"FROM information_schema.TABLE_CONSTRAINTS tc " +
				"LEFT JOIN information_schema.KEY_COLUMN_USAGE kcu " +
						"ON tc.CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
						"AND tc.TABLE_NAME=kcu.TABLE_NAME " +
						"AND kcu.CONSTRAINT_SCHEMA='" + catalog + "' " +
				"WHERE tc.CONSTRAINT_SCHEMA='" + catalog + "' " +
						"AND tc.TABLE_SCHEMA='" + catalog + "' " +
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
		// TODO use RENAME COLUMN in MySQL 8.0
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
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP FOREIGN KEY ").
			append(constraintName);
	}

	@Override
	protected void dropUniqueConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
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

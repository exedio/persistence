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

public final class MysqlDialect extends Dialect
{
	final String sequenceColumnName;
	private final String rowFormat;

	public MysqlDialect(
			final String sequenceColumnName,
			final String rowFormat)
	{
		super(null);
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
	String getColumnType(final int dataType, final ResultSet resultSet)
	{
		throw new RuntimeException();
	}

	static String unQuoteName(final String quotedName)
	{
		final int length = quotedName.length();
		if(length<3)
			throw new RuntimeException(quotedName);
		if(quotedName.charAt(0)!=QUOTE_CHARACTER)
			throw new RuntimeException(quotedName);
		if(quotedName.charAt(length-1)!=QUOTE_CHARACTER)
			throw new RuntimeException(quotedName);

		return quotedName.substring(1, length-1);
	}

	@Override
	void verify(final Schema schema)
	{
		final String catalog = schema.getCatalog();

		schema.querySQL(
				"SELECT TABLE_NAME " +
				"FROM information_schema.TABLES " +
				"WHERE TABLE_SCHEMA='" + catalog + "' AND TABLE_TYPE='BASE TABLE'",
		resultSet ->
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final Sequence sequence = schema.getSequence(tableName);
				if(sequence==null || !sequence.required())
					schema.notifyExistentTable(tableName);
			}
		});

		schema.querySQL(
				"SELECT " +
						"TABLE_NAME," + // 1
						"COLUMN_NAME," + // 2
						"IS_NULLABLE," + // 3
						"DATA_TYPE," + // 4
						"CHARACTER_MAXIMUM_LENGTH," + // 5
						"CHARACTER_SET_NAME," + // 6
						"COLLATION_NAME," + // 7
						"COLUMN_KEY " + // 8
				"FROM information_schema.COLUMNS " +
				"WHERE TABLE_SCHEMA='" + catalog + '\'',
		resultSet ->
		{
			while(resultSet.next())
			{
				final String tableName = resultSet.getString(1);
				final String columnName = resultSet.getString(2);
				final String isNullable = resultSet.getString(3);
				final String dataType = resultSet.getString(4);
				final String characterSet = resultSet.getString(6);
				final String collation = resultSet.getString(7);

				final StringBuilder type = new StringBuilder(dataType);
				if("varchar".equals(dataType))
					type.append('(').append(resultSet.getInt(5)).append(')');
				if(characterSet!=null)
					type.append(" CHARACTER SET ").append(characterSet);
				if(collation!=null)
					type.append(" COLLATE ").append(collation);

				if("NO".equals(isNullable))
				{
					if(!"PRI".equals(resultSet.getString(8)))
						type.append(NOT_NULL);
				}
				else if(!"YES".equals(isNullable))
					throw new RuntimeException(tableName + '#' + columnName + '#' + isNullable);

				final Table table = schema.getTable(tableName);
				if(table!=null)
					table.notifyExistentColumn(columnName, type.toString());
				else
				{
					final Sequence sequence = schema.getSequence(tableName);
					if(sequence!=null && sequenceColumnName.equals(columnName))
						sequence.notifyExists(sequenceTypeMapper.unmap(dataType, columnName));
				}
			}
		});

		verifyForeignKeyConstraints(
				"SELECT tc.CONSTRAINT_NAME,tc.TABLE_NAME,kcu.COLUMN_NAME,kcu.REFERENCED_TABLE_NAME,kcu.REFERENCED_COLUMN_NAME " +
				"FROM information_schema.TABLE_CONSTRAINTS tc " +
				"LEFT JOIN information_schema.KEY_COLUMN_USAGE kcu " +
						"ON tc.CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
						"AND kcu.CONSTRAINT_SCHEMA='" + catalog + "' " +
				"WHERE tc.CONSTRAINT_SCHEMA='" + catalog + "' " +
						"AND tc.TABLE_SCHEMA='" + catalog + "' " +
						"AND tc.CONSTRAINT_TYPE='FOREIGN KEY'",
				schema);

		final String PRIMARY_KEY = "PRIMARY KEY";
		final String UNIQUE = "UNIQUE";
		schema.querySQL(
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
				final String constraintName = resultSet.getString(1);
				final String tableName = resultSet.getString(2);
				final String constraintType = resultSet.getString(3);
				final String columnName = resultSet.getString(4);

				final Sequence sequence = schema.getSequence(tableName);
				if(sequence!=null && sequence.required())
					continue;

				final Table table = schema.notifyExistentTable(tableName);

				if(PRIMARY_KEY.equals(constraintType))
				{
					if(table.required())
					{
						boolean found = false;
						for(final Constraint c : table.getConstraints())
						{
							if(c instanceof PrimaryKeyConstraint &&
								((PrimaryKeyConstraint)c).primaryKeyColumn.equals(columnName))
							{
								table.notifyExistentPrimaryKeyConstraint(c.name);
								found = true;
								break;
							}
						}
						if(!found)
							table.notifyExistentPrimaryKeyConstraint(columnName+"_Pk");
					}
				}
				else if(UNIQUE.equals(constraintType))
				{
					uniqueConstraintCollector.onColumn(table, constraintName, columnName);
				}
				else
					throw new RuntimeException(constraintType+'-'+constraintName);
			}
			uniqueConstraintCollector.finish();
		});
	}

	private static final String ENGINE = " ENGINE=innodb";

	@Override
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(ENGINE);
		if(rowFormat!=null)
			bf.append(" ROW_FORMAT=").
				append(rowFormat);
	}

	@Override
	boolean needsTargetColumnName()
	{
		return true;
	}

	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" CHANGE ").
			append(oldColumnName).
			append(' ').
			append(newColumnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	// TODO is same as hsqldb
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
			append(" MODIFY ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}

	@Override
	void dropPrimaryKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP PRIMARY KEY");
	}

	@Override
	void dropForeignKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP FOREIGN KEY ").
			append(constraintName);
	}

	@Override
	void dropUniqueConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("ALTER TABLE ").
			append(tableName).
			append(" DROP INDEX ").
			append(constraintName);
	}

	/**
	 * @deprecated not used anymore.
	 * BEWARE:
	 * The actual name of the column may be different.
	 * Internally use {@link #sequenceColumnName} instead.
	 */
	@Deprecated
	public static final String SEQUENCE_COLUMN = "x";

	@Override
	void createSequence(
			final StringBuilder bf, final String sequenceName,
			final Sequence.Type type, final long start)
	{
		bf.append("CREATE TABLE ").
			append(sequenceName).
			append("(").
				append(quoteName(sequenceColumnName)).
				append(' ').
				// must not use TINYINT, SMALLINT, MEDIUMINT as this is allowed only for
				// schema.mysql.smallIntegerTypes=true
				append(sequenceTypeMapper.map(type)).
				append(" AUTO_INCREMENT PRIMARY KEY)" +
			ENGINE +
			" COMMENT='cope_sequence_table'");

		if(rowFormat!=null)
			bf.append(" ROW_FORMAT=").
				append(rowFormat);

		initializeSequence(bf, sequenceName, start);
	}

	static final SequenceTypeMapper sequenceTypeMapper = new SequenceTypeMapper("int", "bigint");

	public static void initializeSequence(
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
	void dropSequence(final StringBuilder bf, final String sequenceName)
	{
		bf.append("DROP TABLE ").
			append(sequenceName);
	}
}

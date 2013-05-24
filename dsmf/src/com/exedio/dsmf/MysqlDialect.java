/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import com.exedio.dsmf.Node.ResultSetHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class MysqlDialect extends Dialect
{
	private final String rowFormat;
	final String primaryKeyColumnName;

	public MysqlDialect(final String rowFormat, final String primaryKeyColumnName)
	{
		super(null);
		this.rowFormat = rowFormat;
		this.primaryKeyColumnName = primaryKeyColumnName;
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
			"select TABLE_NAME " +
				"from information_schema.TABLES " +
				"where TABLE_SCHEMA='" + catalog + "' and TABLE_TYPE='BASE TABLE'",
			new Node.ResultSetHandler() { public void run(final ResultSet resultSet) throws SQLException
			{
				//printMeta(resultSet);
				while(resultSet.next())
				{
					final String tableName = resultSet.getString(1);
					//printRow(resultSet);

					final Sequence sequence = schema.getSequence(tableName);
					if(sequence!=null && sequence.required())
						sequence.notifyExists();
					else
						schema.notifyExistentTable(tableName);
				}
			}
		});
		schema.querySQL(
			"select TABLE_NAME,COLUMN_NAME,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_SET_NAME,COLLATION_NAME,COLUMN_KEY " +
			"from information_schema.COLUMNS " +
			"where TABLE_SCHEMA='" + catalog + '\'',
			new Node.ResultSetHandler() { public void run(final ResultSet resultSet) throws SQLException
			{
				//printMeta(resultSet);
				while(resultSet.next())
				{
					//printRow(resultSet);
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
						type.append(" character set ").append(characterSet);
					if(collation!=null)
						type.append(" collate ").append(collation);

					if("NO".equals(isNullable))
					{
						if(!"PRI".equals(resultSet.getString(8)))
							type.append(" not null");
					}
					else if(!"YES".equals(isNullable))
						throw new RuntimeException(tableName + '#' + columnName + '#' + isNullable);

					final Table table = schema.getTable(tableName);
					if(table!=null)
						table.notifyExistentColumn(columnName, type.toString());
					else
					{
						final Sequence sequence = schema.getSequence(tableName);
						if(sequence!=null)
							sequence.notifyExists();
					}
				}
			}
		});

		verifyForeignKeyConstraints(
			"select tc.CONSTRAINT_NAME,tc.TABLE_NAME,kcu.COLUMN_NAME,kcu.REFERENCED_TABLE_NAME,kcu.REFERENCED_COLUMN_NAME " +
			"from information_schema.TABLE_CONSTRAINTS tc " +
			"left join information_schema.KEY_COLUMN_USAGE kcu " +
				"on tc.CONSTRAINT_TYPE='FOREIGN KEY' " +
				"and tc.CONSTRAINT_NAME=kcu.CONSTRAINT_NAME " +
				"and kcu.CONSTRAINT_SCHEMA='" + catalog + "' " +
			"where tc.CONSTRAINT_SCHEMA='" + catalog + "' " +
				"and tc.TABLE_SCHEMA='" + catalog + "' " +
				"and tc.CONSTRAINT_TYPE in ('FOREIGN KEY')",
			schema);

		{
			for(final Table table : schema.getTables())
			{
				if(!table.exists())
					continue;

				{
					final StringBuilder bf = new StringBuilder();
					bf.append("show columns from ").
						append(quoteName(table.name));

					schema.querySQL(bf.toString(), new Node.ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								//printMeta(resultSet);
								while(resultSet.next())
								{
									//printRow(resultSet);
									final String key = resultSet.getString("Key");
									if("PRI".equals(key))
									{
										final String field = resultSet.getString("Field");
										if(primaryKeyColumnName.equals(field) && table.required())
										{
											for(final Constraint c : table.getConstraints())
											{
												if(c instanceof PrimaryKeyConstraint)
												{
													table.notifyExistentPrimaryKeyConstraint(c.name);
													break;
												}
											}
										}
										else
											table.notifyExistentPrimaryKeyConstraint(field+"_Pk");
									}
								}
							}
						});
				}
				{
					final StringBuilder bf = new StringBuilder();
					bf.append("show create table ").
						append(quoteName(table.name));

					schema.querySQL(bf.toString(), new ResultSetHandler()
						{
							public void run(final ResultSet resultSet) throws SQLException
							{
								while(resultSet.next())
								{
									final String tableName = resultSet.getString("Table");
									final String createTable = resultSet.getString("Create Table");
									final Table table = schema.notifyExistentTable(tableName);
									//System.out.println("----------"+tableName+"----"+createTable);
									final StringTokenizer t = new StringTokenizer(createTable);
									for(String s = t.nextToken(); t.hasMoreTokens(); s = t.nextToken())
									{
										//System.out.println("----------"+tableName+"---------------"+s);
										//UNIQUE KEY `AttriEmptyItem_parKey_Unq` (`parent`,`key`)
										if("UNIQUE".equals(s))
										{
											if(!t.hasMoreTokens() || !"KEY".equals(t.nextToken()) ||
												!t.hasMoreTokens())
												continue;
											final String quotedName = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------quotedName:"+quotedName);
											final String name = unQuoteName(quotedName);
											//System.out.println("----------"+tableName+"--------------------name:"+name);
											if(!t.hasMoreTokens())
												continue;
											final String clause = t.nextToken();
											//System.out.println("----------"+tableName+"--------------------clause:"+clause);

											final int clauseLengthM1 = clause.length()-1;
											table.notifyExistentUniqueConstraint(name, clause.charAt(clauseLengthM1)==',' ? clause.substring(0, clauseLengthM1) : clause);
										}
									}
								}
							}
						});
				}
			}
		}
	}

	private static final String ENGINE = " engine=innodb";

	@Override
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(ENGINE);
		if(rowFormat!=null)
			bf.append(" row_format=").
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
		bf.append("alter table ").
			append(tableName).
			append(" change ").
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
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	@Override
	public String modifyColumn(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" modify ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}

	@Override
	void dropPrimaryKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("alter table ").
			append(tableName).
			append(" drop primary key");
	}

	@Override
	void dropForeignKeyConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("alter table ").
			append(tableName).
			append(" drop foreign key ").
			append(constraintName);
	}

	@Override
	void dropUniqueConstraint(final StringBuilder bf, final String tableName, final String constraintName)
	{
		bf.append("alter table ").
			append(tableName).
			append(" drop index ").
			append(constraintName);
	}

	public static final String SEQUENCE_COLUMN = "x";

	@Override
	void createSequence(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		bf.append("create table ").
			append(sequenceName).
			append("(" + SEQUENCE_COLUMN + " integer auto_increment primary key)" + ENGINE);

		if(rowFormat!=null)
			bf.append(" row_format=").
				append(rowFormat);

		initializeSequence(bf, sequenceName, startWith);
	}

	private static void initializeSequence(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		// From the MySQL documentation:
		//
		//    InnoDB supports the AUTO_INCREMENT = N table option in CREATE TABLE
		//    and ALTER TABLE statements, to set the initial counter value or alter
		//    the current counter value. The effect of this option is canceled by
		//    a server restart, for reasons discussed earlier in this section.
		//
		// means that the AUTO_INCREMENT table option cannot be used reliably for cope.
		if(startWith!=0)
		{
			bf.append(";insert into ").
				append(sequenceName).
				append(" values(").
				append(startWith).
				append(')');
		}
	}

	private void deleteSequence(final StringBuilder bf, final Sequence sequence)
	{
		bf.append("truncate ").
			append(quoteName(sequence.name));

		initializeSequence(bf, sequence.name, sequence.startWith);

		bf.append(';');
	}

	@Override
	void dropSequence(final StringBuilder bf, final String sequenceName)
	{
		bf.append("drop table ").
			append(sequenceName);
	}

	@Override
	public void deleteSchema(final Schema schema)
	{
		final StringBuilder bf = new StringBuilder();

		bf.append("set FOREIGN_KEY_CHECKS=0;");

		for(final Table table : schema.getTables())
		{
			bf.append("truncate ").
				append(quoteName(table.name)).
				append(';');
		}

		bf.append("set FOREIGN_KEY_CHECKS=1;");

		for(final Sequence sequence : schema.getSequences())
			deleteSequence(bf, sequence);

		execute(schema.connectionProvider, bf.toString());
	}

	private static void execute(final ConnectionProvider connectionProvider, final String sql)
	{
		Connection connection = null;
		try
		{
			connection = connectionProvider.getConnection();
			execute(connection, sql);

			// NOTE:
			// until mysql connector 5.0.4 putting connection back into the pool
			// causes exception later:
			// java.sql.SQLException: ResultSet is from UPDATE. No Data.
			connectionProvider.putConnection(connection);
			connection = null;
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					// do not put it into connection pool again
					// because foreign key constraints could be disabled
					connection.close();
				}
				catch(final SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static void execute(final Connection connection, final String sql) throws SQLException
	{
		final java.sql.Statement sqlStatement =
			connection.createStatement();
		try
		{
			sqlStatement.executeUpdate(sql);
		}
		finally
		{
			sqlStatement.close();
		}
	}
}

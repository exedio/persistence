/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.Node.ResultSetHandler;

public final class PostgresqlDialect extends Dialect
{
	public PostgresqlDialect()
	{
		super(null);
	}

	@Override
	String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
		switch(dataType)
		{
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.DOUBLE:
				return "DOUBLE PRECISION";
			case Types.VARCHAR:
				switch(columnSize)
				{
					case -1: return "TEXT";
					default: return "VARCHAR("+columnSize+')';
				}
			case Types.DATE:
				return "DATE";
			case Types.TIMESTAMP:
				return "timestamp (3) without time zone"; // TODO fetch precision and time zone
			case Types.BINARY:
				return "bytea";
			default:
				return null;
		}
	}

	@Override
	void verify(final Schema schema)
	{
		super.verify(schema);

		schema.querySQL(
				"select " +
				"ut.relname," +
				"uc.conname," +
				"uc.contype," +
				"uc.consrc " +
				"from pg_constraint uc " +
				"inner join pg_class ut on uc.conrelid=ut.oid " +
				"where ut.relname not like 'pg_%' and ut.relname not like 'pga_%' and uc.contype in ('c','p','u')",
			new ResultSetHandler()
			{
				public void run(final ResultSet resultSet) throws SQLException
				{
					//printMeta(resultSet);
					while(resultSet.next())
					{
						//printRow(resultSet);
						final String tableName = resultSet.getString(1);
						final String constraintName = resultSet.getString(2);
						final String constraintType = resultSet.getString(3);
						final Table table = schema.notifyExistentTable(tableName);
						//System.out.println("tableName:"+tableName+" constraintName:"+constraintName+" constraintType:>"+constraintType+"<");
						if("c".equals(constraintType))
						{
							String searchCondition = resultSet.getString(4);
							//System.out.println("searchCondition:>"+searchCondition+"<");
							if(searchCondition.startsWith("(")&& searchCondition.endsWith(")"))
								searchCondition = searchCondition.substring(1, searchCondition.length()-1);
							table.notifyExistentCheckConstraint(constraintName, searchCondition);
						}
						else if("p".equals(constraintType))
							table.notifyExistentPrimaryKeyConstraint(constraintName);
						else if("u".equals(constraintType))
							table.notifyExistentUniqueConstraint(constraintName, "postgresql unique constraint dummy clause"); // TODO, still don't know where to get this information
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

						//System.out.println("EXISTS:"+tableName);
					}
				}
			});

		final String catalog = schema.getCatalog();
		schema.querySQL(
				"select rc.constraint_name, src.table_name, src.column_name, tgt.table_name, tgt.column_name " +
				"from information_schema.referential_constraints rc " +
				"join information_schema.key_column_usage src on rc.constraint_name=src.constraint_name " +
				"join information_schema.key_column_usage tgt on rc.unique_constraint_name=tgt.constraint_name " +
				"where rc.constraint_catalog='" + catalog + '\'',
		new ResultSetHandler()
		{
			public void run(final ResultSet resultSet) throws SQLException
			{
				//printMeta(resultSet);
				while(resultSet.next())
				{
					//printRow(resultSet);
					final String tableName = resultSet.getString(2);
					final Table table = schema.getTable(tableName);
					if(table!=null)
						table.notifyExistentForeignKeyConstraint(
								resultSet.getString(1), // constraintName
								resultSet.getString(3), // foreignKeyColumn
								resultSet.getString(4), // targetTable
								resultSet.getString(5)  // targetColumn
						);
				}
			}
		});
	}

	@Override
	void appendTableCreateStatement(final StringBuilder bf)
	{
		bf.append(" without oids");
	}

	// same as oracle
	@Override
	public String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("alter table ").
			append(tableName).
			append(" rename column ").
			append(oldColumnName).
			append(" to ").
			append(newColumnName);
		return bf.toString();
	}

	// same as hsqldb
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

	// same as oracle
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
	void createSequence(final StringBuilder bf, final String sequenceName, final int startWith)
	{
		bf.append("create sequence ").
			append(sequenceName).
			append(
					" increment by 1" +
					" start with " + startWith +
					" maxvalue " + Integer.MAX_VALUE +
					" minvalue " + startWith +
					" no cycle");
	}
}

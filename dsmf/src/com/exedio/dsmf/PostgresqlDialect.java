/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
		super(null, "pga_");
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
	public boolean supportsSequences()
	{
		return false; // TODO implement support
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
				"where ut.relname not like 'pg_%' and ut.relname not like 'pga_%'",
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
						else if("f".equals(constraintType))
							table.notifyExistentForeignKeyConstraint(constraintName);
						else if("u".equals(constraintType))
							table.notifyExistentUniqueConstraint(constraintName, "postgresql unique constraint dummy clause"); // TODO, still don't know where to get this information
						else
							throw new RuntimeException(constraintType+'-'+constraintName);

						//System.out.println("EXISTS:"+tableName);
					}
				}
			});
		/*schema.querySQL(
				"select uit.relname,uic.relname,ui.indisunique,ui.indisprimary,ui.* from pg_index ui " +
				"inner join pg_class uit on ui.indrelid=uit.oid " +
				"inner join pg_class uic on ui.indexrelid=uic.oid " +
				"where uic.relname not like 'pg_%' and uic.relname not like 'pga_%'",
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
						final boolean isUnique = resultSet.getBoolean(3);
						final boolean isPrimary = resultSet.getBoolean(4);
						final Table table = schema.notifyExistentTable(tableName);
						if(isUnique&&!isPrimary)
						{
							System.out.println("---------------unique"+constraintName);
							table.notifyExistentUniqueConstraint(constraintName, null);
						}
					}
				}
			});*/
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
}

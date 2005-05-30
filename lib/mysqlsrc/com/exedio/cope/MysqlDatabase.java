/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints.
 * @author Ralf Wiebicke
 */
public final class MysqlDatabase extends Database

// TODO
//	implements DatabaseTimestampCapable
// would require type "timestamp(14,3) null default null"
// but (14,3) is not yet supported
// "null default null" is needed to allow null and
// make null the default value
// This works with 4.1.6 and higher only

{
	static
	{
		try
		{
			Class.forName(Driver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	protected MysqlDatabase(final Properties properties)
	{
		super(properties, null);
	}

	String getIntegerType(final int precision)
	{
		// TODO: use precision to select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (precision <= 10) ? "integer" : "bigint";
	}

	String getDoubleType(final int precision)
	{
		return "double";
	}

	String getStringType(final int maxLength)
	{
		// TODO:
		// 255 is needed for unique columns only,
		// non-unique can have more,
		// and for longer unique columns you may specify a shorter key length
		
		// IMPLEMENTATION NOTE: "binary" is needed to make string comparisions case sensitive
		return "varchar("+(maxLength!=Integer.MAX_VALUE ? maxLength : 255)+") binary";
	}
	
	protected String protectName(final String name)
	{
		return '`' + name + '`';
	}

	private final String extracteConstraintName(final SQLException e, final int vendorCode, final String start)
	{
		// TODO: MySQL does not deliver constraint name in exception
		//System.out.println("-u-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());

		if(e.getErrorCode()==vendorCode &&
				e.getMessage().startsWith(start))
			return ANY_CONSTRAINT;
		else
			return null;
	}

	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extracteConstraintName(e, 1062, "Duplicate entry ");
	}

	protected String extractIntegrityConstraintName(final SQLException e)
	{
		return extracteConstraintName(e, 1217, "Cannot delete or update a parent row: a foreign key constraint fails");
	}

	protected String getColumnType(final int dataType, final ResultSet resultSet)
			throws SQLException
	{
		final String result = super.getColumnType(dataType, resultSet);

		if(result==null)
			return null;
		else if(result.startsWith("varchar"))
			return result + " binary";
		else
			return result;
	}
	
	protected Statement getDropForeignKeyConstraintStatement(final Table table, final ItemColumn column)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(table.protectedID).
			append(" drop foreign key ").
			append(protectName(column.integrityConstraintName));
		return bf;
	}
	
	Statement getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" change ").
			append(oldColumnName).
			append(' ').
			append(newColumnName).
			append(' ').
			append(columnType);
		return bf;
	}

	// TODO is same as hsqldb
	Statement getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final Statement bf = createStatement();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf;
	}

	Statement getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		throw new RuntimeException("not implemented");
	}

}

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

package com.exedio.dsmf;


public final class MysqlDriver extends Driver
{

	/**
	 * TODO make non-public
	 */
	public static final char PROTECTOR = '`';

	public String protectName(final String name)
	{
		return PROTECTOR + name + PROTECTOR;
	}
	
	public void appendTableCreateStatement(final StringBuffer bf)
	{
		bf.append(" engine=innodb");
	}
	
	public String getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
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
	public String getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" add column ").
			append(columnName).
			append(' ').
			append(columnType);
		return bf.toString();
	}

	public String getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		throw new RuntimeException("not implemented");
	}

	public String getDropForeignKeyConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" drop foreign key ").
			append(constraintName);
		return bf.toString();
	}
	
}

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


public abstract class Driver
{
	
	/**
	 * Protects a database name from being interpreted as a SQL keyword.
	 * This is usually done by enclosing the name with some (database specific) delimiters.
	 * The default implementation uses double quotes as delimiter.
	 */
	public String protectName(final String name)
	{
		return '"' + name + '"';
	}

	public void appendTableCreateStatement(final StringBuffer bf)
	{
	}
	
	public abstract String getRenameColumnStatement(String tableName, String oldColumnName, String newColumnName, String columnType);
	public abstract String getCreateColumnStatement(String tableName, String columnName, String columnType);
	public abstract String getModifyColumnStatement(String tableName, String columnName, String newColumnType);

	public String getDropForeignKeyConstraintStatement(final String tableName, final String constraintName)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" drop constraint ").
			append(constraintName);
		return bf.toString();
	}
	
}

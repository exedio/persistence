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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public final class OracleDriver extends Driver
{

	public String getColumnType(final int dataType, final ResultSet resultSet) throws SQLException
	{
		final int columnSize = resultSet.getInt("COLUMN_SIZE");
		switch(dataType)
		{
			case Types.DECIMAL:
				final int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
				if(decimalDigits>0)
					return "NUMBER("+columnSize+','+decimalDigits+')';
				else
					return "NUMBER("+columnSize+')';
			case Types.OTHER:
				return "TIMESTAMP(3)";
			case Types.VARCHAR:
				return "VARCHAR2("+columnSize+')';
			case Types.TIMESTAMP:
				return "DATE";
			case Types.LONGVARCHAR:
				return "LONG";
			default:
				return null;
		}
	}

	public String getRenameColumnStatement(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" rename column ").
			append(oldColumnName).
			append(" to ").
			append(newColumnName);
		return bf.toString();
	}

	public String getCreateColumnStatement(final String tableName, final String columnName, final String columnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" add (").
			append(columnName).
			append(' ').
			append(columnType).
			append(')');
		return bf.toString();
	}

	public String getModifyColumnStatement(final String tableName, final String columnName, final String newColumnType)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("alter table ").
			append(tableName).
			append(" modify ").
			append(columnName).
			append(' ').
			append(newColumnType);
		return bf.toString();
	}
	
}

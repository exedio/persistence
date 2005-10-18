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

import java.sql.SQLException;

import org.hsqldb.jdbcDriver;

import com.exedio.dsmf.HsqldbDriver;

final class HsqldbDatabase
		extends Database
		implements
			DatabaseTimestampCapable
{
	static
	{
		try
		{
			Class.forName(jdbcDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	protected HsqldbDatabase(final Properties properties)
	{
		super(new HsqldbDriver(), properties);
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
		return "varchar("+maxLength+")";
	}
	
	String getDayType()
	{
		return "date";
	}
	
	public String getDateTimestampType()
	{
		return "timestamp";
	}

	boolean isLimitClauseInSelect()
	{
		return true;
	}

	boolean appendLimitClause(final Statement bf, final int start, final int count)
	{
		// TODO: use appendValue to support prepared statements
		bf.append(" limit ").
			append(start).
			append(' ').
			append(count!=Query.UNLIMITED_COUNT ? count : 0);
		return true;
	}
	
	protected boolean supportsRightOuterJoins()
	{
		return false;
	}

	private final String extractConstraintName(final SQLException e, final int vendorCode, final String start)
	{
		//System.out.println("-u-"+e.getClass()+" "+e.getCause()+" "+e.getErrorCode()+" "+e.getLocalizedMessage()+" "+e.getSQLState()+" "+e.getNextException());

		if(e.getErrorCode()!=vendorCode)
			return null;

		final String m = e.getMessage();
		if(m.startsWith(start))
		{
			final int startLength = start.length();
			final int pos = m.indexOf(' ', startLength);
			return (pos<0) ? m.substring(startLength) : m.substring(startLength, pos);
		}
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, -104, "Unique constraint violation: ");
	}

}

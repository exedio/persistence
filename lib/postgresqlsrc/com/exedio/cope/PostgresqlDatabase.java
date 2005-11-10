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

import org.postgresql.Driver;

import com.exedio.dsmf.PostgresqlDriver;


/**
 * Still does not work.
 */
final class PostgresqlDatabase extends Database
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
	
	protected PostgresqlDatabase(final Properties properties)
	{
		super(new PostgresqlDriver(), properties);
	}
	
	String getIntegerType(final int precision)
	{
		return "INTEGER";
	}

	String getDoubleType(final int precision)
	{
		return "DOUBLE PRECISION";
	}

	String getStringType(final int maxLength)
	{
		return "VARCHAR("+(maxLength!=Integer.MAX_VALUE ? maxLength : 2000)+')';
	}
	
	String getDayType()
	{
		return "DATE";
	}
	
	int getLimitSupport()
	{
		// TODO support limit clause
		return LIMIT_SUPPORT_NONE;
	}

	void appendLimitClause(final Statement bf, final int start, final int count)
	{
		throw new RuntimeException();
	}
	
	void appendLimitClause2(final Statement bf, final int start, final int count)
	{
		throw new RuntimeException();
	}
	
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		// TODO check for full text indexes
		appendMatchClauseByLike(bf, function, value);
	}
	
	private String extractConstraintName(final SQLException e, final int vendorCode, final String start, final String end)
	{
		if(e.getErrorCode()!=vendorCode)
			return null;
		
		final String m = e.getMessage();
		if(m.startsWith(start) && m.endsWith(end))
		{
			final int pos = m.indexOf('.', start.length());
			return m.substring(pos+1, m.length()-end.length());
		}
		else
			return null;
	}
	
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, 1, "ORA-00001: unique constraint (", ") violated\n");
	}

}

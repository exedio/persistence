/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
			throw new RuntimeException(e);
		}
	}
	
	protected PostgresqlDatabase(final Properties properties)
	{
		super(new PostgresqlDriver(), properties);
	}
	
	@Override
	public String getIntegerType(final long minimum, final long maximum)
	{
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "INTEGER" : "BIGINT"; // TODO: smallint
	}

	@Override
	public String getDoubleType()
	{
		return "DOUBLE PRECISION";
	}

	@Override
	public String getStringType(final int maxLength)
	{
		return (maxLength>10485760) ? "TEXT" : "VARCHAR("+maxLength+')'; // in postgres varchar cannot be longer
	}
	
	@Override
	public String getDayType()
	{
		return "DATE";
	}
	
	// TODO check if there is a suitable column type
	@Override
	public String getDateTimestampType()
	{
		return null;
	}
	
	// TODO check if there is a suitable column type
	@Override
	public String getBlobType(final long maximumLength)
	{
		return null;
	}
	
	@Override
	LimitSupport getLimitSupport()
	{
		// TODO support limit clause
		return LimitSupport.NONE;
	}

	@Override
	void appendLimitClause(final Statement bf, final int start, final int count)
	{
		throw new RuntimeException();
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int start, final int count)
	{
		throw new RuntimeException();
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		// TODO check for full text indexes
		appendMatchClauseByLike(bf, function, value);
	}
	
	private String extractConstraintName(final SQLException e, final String sqlState, final int vendorCode)
	{
		if(!sqlState.equals(e.getSQLState()))
			return null;
		if(e.getErrorCode()!=vendorCode)
			return null;
		
		final String m = e.getMessage();
		final int end = m.lastIndexOf('\u00ab'); // left pointing double angle quotation mark
		if(end<0)
			return null;
		final int start = m.lastIndexOf('\u00bb'); // right pointing double angle quotation mark
		if(start<0)
			return null;

		return m.substring(start+1, end);
	}
	
	@Override
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, "23505", 0);
	}

}

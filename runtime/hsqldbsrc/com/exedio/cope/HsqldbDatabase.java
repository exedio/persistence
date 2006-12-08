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

import org.hsqldb.jdbcDriver;

import com.exedio.dsmf.HsqldbDriver;

final class HsqldbDatabase extends Database
{
	static
	{
		try
		{
			Class.forName(jdbcDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected HsqldbDatabase(final Properties properties, final boolean migration)
	{
		super(new HsqldbDriver(), properties, migration);
	}

	@Override
	public String getIntegerType(final long minimum, final long maximum)
	{
		// TODO: select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "integer" : "bigint";
	}

	@Override
	public String getDoubleType()
	{
		return "double";
	}

	@Override
	public String getStringType(final int maxLength)
	{
		return "varchar("+maxLength+")";
	}
	
	@Override
	public String getDayType()
	{
		return "date";
	}
	
	@Override
	public String getDateTimestampType()
	{
		return "timestamp";
	}

	@Override
	public String getBlobType(final long maximumLength)
	{
		return "binary";
	}
	
	@Override
	public int getBlobLengthFactor()
	{
		return 2;
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSE_AFTER_SELECT;
	}

	@Override
	void appendLimitClause(final Statement bf, final int start, final int count)
	{
		if((start==0&&count==Query.UNLIMITED_COUNT)||(count<=0&&count!=Query.UNLIMITED_COUNT)||start<0)
			throw new RuntimeException(start+"-"+count);

		bf.append(" limit ").
			appendParameter(start).
			append(' ').
			appendParameter(count!=Query.UNLIMITED_COUNT ? count : 0);
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int start, final int count)
	{
		throw new RuntimeException(bf.toString());
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		appendMatchClauseByLike(bf, function, value);
	}
	
	@Override
	public boolean fakesSupportReadCommitted()
	{
		return true;
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
	
	@Override
	protected String extractUniqueConstraintName(final SQLException e)
	{
		return extractConstraintName(e, -104, "Unique constraint violation: ");
	}

}

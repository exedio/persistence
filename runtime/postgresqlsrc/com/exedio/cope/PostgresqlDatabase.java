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
	
	protected PostgresqlDatabase(final Properties properties, final boolean migration)
	{
		super(new PostgresqlDriver(), properties, migration);
		
		// version 8 needed for savepoints
		if(databaseMajorVersion<8)
			throw new RuntimeException("postgresql support needs at least database version 8, but was: " + databaseProductVersion + '(' + databaseMajorVersion + '.' + databaseMinorVersion + ')');
		if(driverMajorVersion<8)
			throw new RuntimeException("postgresql support needs at least jdbc driver version 8, but was: " + driverVersion + '(' + driverMajorVersion + '.' + driverMinorVersion + ')');
	}
	
	@Override
	boolean needsSavepoint()
	{
		return true;
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
	
	@Override
	public String getDateTimestampType()
	{
		return "timestamp (3) without time zone"; // "3" are fractional digits retained in the seconds field
	}

	@Override
	public String getBlobType(final long maximumLength)
	{
		return (maximumLength<Integer.MAX_VALUE) ? "bytea" : null;
	}
	
	@Override
	public boolean supportsBlobInResultSet()
	{
		return false;
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSE_AFTER_WHERE;
	}

	@Override
	void appendLimitClause(final Statement bf, final int start, final int count)
	{
		assert start>=0;
		assert count>0 || count==Query.UNLIMITED_COUNT;
		assert start>0 || count>0;
		
		if(count!=Query.UNLIMITED_COUNT)
			bf.append(" limit ").appendParameter(count);

		if(start>0)
			bf.append(" offset ").appendParameter(start);
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
		//final PSQLException pe = (PSQLException)e;
		//final ServerErrorMessage sem = pe.getServerErrorMessage();
		//System.out.println("..........."+sem.getDetail()+"..."+sem.getFile()+"..."+sem.getHint()+"..."+sem.getInternalPosition()+"..."+sem.getInternalQuery()+"..."+sem.getLine()+"..."+sem.getMessage()+"..."+sem.getPosition()+"..."+sem.getRoutine()+"..."+sem.getSeverity()+"..."+sem.getSQLState()+"..."+sem.getWhere()+"...");
		
		if(!sqlState.equals(e.getSQLState()))
			return null;
		if(e.getErrorCode()!=vendorCode)
			return null;
		
		final String m = e.getMessage();
		
		// TODO make name extraction independent of server language
		final String en = extractConstraintName(m, '"', '"');
		if(en!=null)
			return en;
		
		final String de = extractConstraintName(m, '\u00bb', '\u00ab'); // right and left pointing double angle quotation mark
		if(de!=null)
			return de;
		
		return null;
	}
	
	private static final String extractConstraintName(final String m, final char startChar, final char endChar)
	{
		final int end = m.lastIndexOf(endChar);
		if(end<=0)
			return null;
		final int start = m.lastIndexOf(startChar, end-1);
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

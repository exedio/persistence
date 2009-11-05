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

package com.exedio.cope;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.Driver;

final class PostgresqlDialect extends Dialect
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
	
	protected PostgresqlDialect(final DialectParameters parameters)
	{
		super(
				new com.exedio.dsmf.PostgresqlDialect(),
				"LENGTH");
		
		// version 8 needed for savepoints
		if(parameters.databaseMajorVersion<8)
			throw new RuntimeException("postgresql support needs at least database version 8, but was: " + parameters.databaseProductVersion + '(' + parameters.databaseMajorVersion + '.' + parameters.databaseMinorVersion + ')');
		if(parameters.driverMajorVersion<8)
			throw new RuntimeException("postgresql support needs at least jdbc driver version 8, but was: " + parameters.driverVersion + '(' + parameters.driverMajorVersion + '.' + parameters.driverMinorVersion + ')');
	}
	
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "INTEGER" : "BIGINT"; // TODO: smallint
	}

	@Override
	String getDoubleType()
	{
		return "DOUBLE PRECISION";
	}

	/**
	 * Datatype "varchar" can have at most 10485760 characters is postgresql.
	 * <p>
	 * Does never return "char(n)", because even if minChars==maxChars,
	 * in postgresql datatype "char" has no performance advantage compared to "varchar".
	 */
	@Override
	String getStringType(final int maxBytes /* TODO should be maxChars*/)
	{
		return (maxBytes>10485760) ? "TEXT" : "VARCHAR("+maxBytes+')';
	}
	
	@Override
	String getDayType()
	{
		return "DATE";
	}
	
	@Override
	String getDateTimestampType()
	{
		return "timestamp (3) without time zone"; // "3" are fractional digits retained in the seconds field
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return (maximumLength<Integer.MAX_VALUE) ? "bytea" : null;
	}
	
	@Override
	void fetchBlob(
			final ResultSet resultSet, final int columnIndex,
			final Item item, final OutputStream data, final DataField field)
	throws SQLException
	{
		InputStream source = null;
		try
		{
			source = resultSet.getBinaryStream(columnIndex);
			if(source!=null)
				field.copy(source, data, item);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(source!=null)
			{
				try
				{
					source.close();
				}
				catch(IOException e)
				{/*IGNORE*/}
			}
		}
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSE_AFTER_WHERE;
	}

	@Override
	void appendLimitClause(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;
		
		if(limit!=Query.UNLIMITED)
			bf.append(" limit ").appendParameter(limit);

		if(offset>0)
			bf.append(" offset ").appendParameter(offset);
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException();
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		// TODO check for full text indexes
		appendMatchClauseByLike(bf, function, value);
	}
	
	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("encode(substring(").
			append(column, (Join)null).
			append(" from 1 for ").
			appendParameter(value.length).
			append("),'hex')=").
			appendParameter(hexLower(value));
	}
	
	@Override
	boolean subqueryRequiresAlias()
	{
		return true;
	}
}

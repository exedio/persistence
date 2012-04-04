/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.Hex;

final class PostgresqlDialect extends Dialect
{
	protected PostgresqlDialect(final DialectParameters parameters)
	{
		super(
				parameters,
				new com.exedio.dsmf.PostgresqlDialect());

		final EnvironmentInfo ei = parameters.environmentInfo;
		// version 8 needed for savepoints
		if(ei.getDatabaseMajorVersion()<8)
			throw new RuntimeException("postgresql support needs at least database version 8, but was: " + ei.getDatabaseVersionDescription());
		if(ei.getDriverMajorVersion()<8)
			throw new RuntimeException("postgresql support needs at least jdbc driver version 8, but was: " + ei.getDriverVersionDescription());
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
	String getStringLength()
	{
		return "LENGTH";
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
		final InputStream source = resultSet.getBinaryStream(columnIndex);
		try
		{
			if(source!=null)
				field.copy(source, data, item);
		}
		catch(final IOException e)
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
				catch(final IOException e)
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
	protected void appendAsString(final Statement bf, final NumberFunction source, final Join join)
	{
		bf.append("TRIM(TO_CHAR(").
			append(source, join).
			append(", '9999999999999'))");
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
			appendParameter(Hex.encodeLower(value));
	}

	@Override
	boolean subqueryRequiresAlias()
	{
		return true;
	}

	@Override
	protected Integer nextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT nextval('").
			append(dsmfDialect.quoteName(name)).
			append("')");

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((Long)o).intValue();
			}
		});
	}

	@Override
	protected Integer getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT last_value FROM ").
			append(dsmfDialect.quoteName(name));

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((Long)o).intValue() + 1;
			}
		});
	}

	@Override
	boolean supportsSelectingUngrouped()
	{
		return true;
	}
}

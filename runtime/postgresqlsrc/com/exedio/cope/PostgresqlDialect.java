/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.dsmf.PostgresqlDialect.BIGINT;
import static com.exedio.dsmf.PostgresqlDialect.BINARY;
import static com.exedio.dsmf.PostgresqlDialect.DATE;
import static com.exedio.dsmf.PostgresqlDialect.DOUBLE;
import static com.exedio.dsmf.PostgresqlDialect.INTEGER;
import static com.exedio.dsmf.PostgresqlDialect.TIMESTAMP;
import static com.exedio.dsmf.PostgresqlDialect.VARCHAR_LIMIT;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.Hex;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class PostgresqlDialect extends Dialect
{
	protected PostgresqlDialect(final DialectParameters parameters)
	{
		super(
				parameters,
				new com.exedio.dsmf.PostgresqlDialect());
	}

	/**
	 * See http://www.postgresql.org/docs/9.3/static/datatype-numeric.html
	 */
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? INTEGER : BIGINT; // TODO: smallint
	}

	@Override
	String getDoubleType()
	{
		return DOUBLE;
	}

	/**
	 * See http://www.postgresql.org/docs/9.3/static/datatype-character.html
	 * Datatype "varchar" can have at most 10485760 characters in postgresql.
	 * <p>
	 * Does never return "char(n)", because even if minChars==maxChars,
	 * in postgresql datatype "char" has no performance advantage compared to "varchar".
	 */
	@Override
	String getStringType(
			final int maxBytes /* TODO should be maxChars*/,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		return (maxBytes>VARCHAR_LIMIT) ? "TEXT" : "VARCHAR("+maxBytes+')';
	}

	@Override
	String getStringLength()
	{
		return "LENGTH";
	}

	@Override
	String getDayType()
	{
		return DATE;
	}

	@Override
	String getDateTimestampType()
	{
		return TIMESTAMP;
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return (maximumLength<Integer.MAX_VALUE) ? BINARY : null;
	}

	@Override
	void fetchBlob(
			final ResultSet resultSet, final int columnIndex,
			final Item item, final OutputStream data, final DataField field)
	throws SQLException
	{
		try(InputStream source = resultSet.getBinaryStream(columnIndex))
		{
			if(source!=null)
				field.copy(source, data, item);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
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
			bf.append(" LIMIT ").appendParameter(limit);

		if(offset>0)
			bf.append(" OFFSET ").appendParameter(offset);
	}

	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException();
	}

	@Override
	protected void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
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
		bf.append("ENCODE(SUBSTRING(").
			append(column, (Join)null).
			append(" FROM 1 FOR ").
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
	protected void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append("ALTER SEQUENCE ").
			append(quotedName).
			append(" RESTART;");
	}

	@Override
	protected Integer nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT NEXTVAL('").
			append(quotedName).
			append("')");

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + quotedName);
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
		bf.append("SELECT LAST_VALUE FROM ").
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
	protected void deleteSchema(
			final List<Table> tables,
			final List<SequenceX> sequences,
			final ConnectionPool connectionPool)
	{
		final StringBuilder bf = new StringBuilder();

		if(!tables.isEmpty())
		{
			bf.append("TRUNCATE ");
			boolean first = true;
			for(final Table table : tables)
			{
				if(first)
					first = false;
				else
					bf.append(',');

				bf.append(table.quotedID);
			}
			bf.append(" CASCADE;");
		}

		for(final SequenceX sequence : sequences)
			sequence.delete(bf, this);

		if(bf.length()>0)
			execute(connectionPool, bf.toString());
	}

	private static void execute(final ConnectionPool connectionPool, final String sql)
	{
		final Connection connection = connectionPool.get(true);
		try
		{
			Executor.update(connection, sql);
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	boolean supportsNotNull()
	{
		return true;
	}
}

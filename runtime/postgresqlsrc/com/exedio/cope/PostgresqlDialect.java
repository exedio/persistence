/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.Sequence;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class PostgresqlDialect extends Dialect
{
	private final String searchPath;

	/**
	 * @param probe must be there to be called by reflection
	 */
	PostgresqlDialect(final Probe probe)
	{
		super(
				new com.exedio.dsmf.PostgresqlDialect(
						probe.properties.connectionPostgresqlSearchPath));

		searchPath = probe.properties.connectionPostgresqlSearchPath;
	}

	@Override
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	void completeConnection(final Connection connection) throws SQLException
	{
		try(java.sql.Statement st = connection.createStatement())
		{
			// http://www.postgresql.org/docs/9.3/interactive/runtime-config-client.html#GUC-SEARCH-PATH
			st.execute("SET search_path TO " + searchPath);

			// http://www.postgresql.org/docs/9.3/interactive/runtime-config-compatible.html#GUC-QUOTE-ALL-IDENTIFIERS
			st.execute("SET quote_all_identifiers TO ON");
		}
	}

	/**
	 * See http://www.postgresql.org/docs/9.3/static/datatype-numeric.html
	 */
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		if(minimum>=Short.MIN_VALUE && maximum<=Short.MAX_VALUE)
			return "smallint";
		else if(minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE)
			return "integer";
		else
			return "bigint";
	}

	@Override
	String getDoubleType()
	{
		return "double precision";
	}

	@Override
	String format(final double number)
	{
		return PostgresqlFormat.format(number);
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
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		return (maxChars>10485760) ? "\"text\"" : "character varying("+maxChars+')';
	}

	@Override
	String getStringLength()
	{
		return "\"length\"";
	}

	/**
	 * See http://www.postgresql.org/docs/9.3/static/datatype-datetime.html
	 */
	@Override
	String getDayType()
	{
		return "\"date\"";
	}

	@Override
	String getDateTimestampType()
	{
		return "timestamp (3) without time zone"; // "3" are fractional digits retained in the seconds field;
	}

	@Override
	String getDateExtract(final String quotedName, final Precision precision)
	{
		// EXTRACT works as well, but is normalized to date_part
		return "\"date_part\"('" + precision.sql() + "'," + quotedName + ")";
	}

	@Override
	String getFloor(final String quotedName)
	{
		return "\"floor\"(" + quotedName + ')';
	}

	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		return "(" + quotedName + " % " + precision.divisor() + ")=0";
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return (maximumLength<Integer.MAX_VALUE) ? "\"bytea\"" : null;
	}

	/**
	 * See http://www.postgresql.org/docs/9.3/interactive/datatype-binary.html#AEN5318
	 */
	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append("E'\\\\x");
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	final String getBlobLength()
	{
		return "\"octet_length\"";
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
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("TRIM(TO_CHAR(").
			append(source, join).
			append(", '9999999999999'))");
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
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
	String getInComma()
	{
		return ", ";
	}

	@Override
	boolean subqueryRequiresAlias()
	{
		return true;
	}

	@Override
	void deleteSequence(
			final StringBuilder bf, final String quotedName,
			final Sequence.Type type, final long start)
	{
		bf.append("ALTER SEQUENCE ").
			append(quotedName).
			append(" RESTART WITH ").
			append(start).
			append(';');
	}

	@Override
	Long nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT NEXTVAL('").
			append(quotedName).
			append("')");

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + quotedName);
				return (Long)o;
			}
		);
	}

	@Override
	Long getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		final Statement bf = executor.newStatement();
		// BEWARE:
		// LAST_VALUE contains the last result returned by NEXTVAL -
		// except if the sequence has not been used before. Then it contains
		// the next result to be returned by NEXTVAL.
		// This is a problem in either PostgreSQL or cope without any idea for
		// resolution yet.
		bf.append("SELECT LAST_VALUE FROM ").
			append(dsmfDialect.quoteName(name));

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((Long)o).longValue() + 1;
			}
		);
	}

	@Override
	void deleteSchema(
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
}

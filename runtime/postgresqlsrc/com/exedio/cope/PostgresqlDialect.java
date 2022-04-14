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
import com.exedio.cope.util.Day;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.ServiceProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ServiceProperties(PostgresqlProperties.class)
final class PostgresqlDialect extends Dialect
{
	private final String timeZoneStatement;
	private final String schemaStatement;
	private final String pgcryptoSchemaQuoted;

	/**
	 * @param probe must be there to be called by reflection
	 * @param properties must be there to be called by reflection
	 */
	PostgresqlDialect(
			final CopeProbe probe,
			final PostgresqlProperties properties)
	{
		super(
				new PostgresqlSchemaDialect(
						properties.schema(probe.properties)));

		probe.environmentInfo.requireDatabaseVersionAtLeast("PostgreSQL", 11, 12);

		timeZoneStatement = properties.timeZoneStatement();
		schemaStatement = properties.schemaStatement();
		pgcryptoSchemaQuoted = quoteSchema(properties.pgcryptoSchema);
	}

	private String quoteSchema(final String schema)
	{
		return "<disabled>".equals(schema) ? null : dsmfDialect.quoteName(schema);
	}

	@Override
	void completeConnection(final Connection connection) throws SQLException
	{
		try(java.sql.Statement st = connection.createStatement())
		{
			if(timeZoneStatement!=null)
				st.execute(timeZoneStatement);

			st.execute(schemaStatement);

			// https://www.postgresql.org/docs/9.6/runtime-config-compatible.html#GUC-QUOTE-ALL-IDENTIFIERS
			st.execute("SET quote_all_identifiers TO ON");
		}
	}

	/**
	 * See https://www.postgresql.org/docs/9.6/datatype-numeric.html
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
	 * See https://www.postgresql.org/docs/9.6/datatype-character.html
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
	 * See https://www.postgresql.org/docs/9.6/datatype-datetime.html
	 */
	@Override
	String getDayType()
	{
		return "\"date\"";
	}

	@Override
	java.sql.Date marshalDay(final Day cell)
	{
		return marshalDayDeprecated(cell);
	}

	@Override
	String getDateTimestampType()
	{
		return "timestamp (3) without time zone"; // "3" are fractional digits retained in the seconds field;
	}

	@Override
	String toLiteral(final Date value)
	{
		return DateField.format(
				((value.getTime()%1000)!=0)
				? "''yyyy-MM-dd HH:mm:ss.SSS'''::timestamp without time zone'"
				: "''yyyy-MM-dd HH:mm:ss"+ "'''::timestamp without time zone'").format(value);
	}

	/**
	 * Don't use a static instance,
	 * since then access must be synchronized
	 */
	@Override
	String toLiteral(final Day value)
	{
		final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMinimumIntegerDigits(2);
		return "'" + value.getYear() + '-' + nf.format(value.getMonthValue()) + '-' + nf.format(value.getDayOfMonth()) + "'::\"date\"";
	}

	@Override
	String getDateExtract(final String quotedName, final Precision precision)
	{
		// EXTRACT works as well, but is normalized to date_part
		return "\"date_part\"('" + precision.sql() + "', " + quotedName + ")";
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
		return "\"bytea\"";
	}

	/**
	 * See https://www.postgresql.org/docs/9.6/datatype-binary.html#AEN5318
	 */
	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append("E'\\\\x");
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	String getBlobLength()
	{
		return "\"octet_length\"";
	}

	@Override
	void fetchBlob(
			final ResultSet resultSet, final int columnIndex,
			final Item item, final OutputStream sink, final DataField field)
	throws SQLException
	{
		try(InputStream source = resultSet.getBinaryStream(columnIndex))
		{
			if(source!=null)
				field.copy(source, sink, item);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	String[] getBlobHashAlgorithms()
	{
		return
				pgcryptoSchemaQuoted!=null
				? new String[]{HASH_MD5, HASH_SHA, HASH_SHA224, HASH_SHA256, HASH_SHA384, HASH_SHA512}
				: new String[]{HASH_MD5};
	}

	@Override
	void appendBlobHash(
			final Statement bf, final BlobColumn column, final Join join,
			final String algorithm)
	{
		switch(algorithm)
		{
			case HASH_MD5: bf.append("MD5(").append(column, join).append(')'); break;
			case HASH_SHA:    appendDigest(bf, column, join, algorithm, "sha1"  ); break;
			case HASH_SHA224: appendDigest(bf, column, join, algorithm, "sha224"); break;
			case HASH_SHA256: appendDigest(bf, column, join, algorithm, "sha256"); break;
			case HASH_SHA384: appendDigest(bf, column, join, algorithm, "sha384"); break;
			case HASH_SHA512: appendDigest(bf, column, join, algorithm, "sha512"); break;
			default:
				super.appendBlobHash(bf, column, join, algorithm);
		}
	}

	/**
	 * See https://www.postgresql.org/docs/9.6/pgcrypto.html
	 */
	private void appendDigest(
			final Statement bf, final BlobColumn column, final Join join,
			final String algorithm, final String type)
	{
		if(pgcryptoSchemaQuoted==null)
			super.appendBlobHash(bf, column, join, algorithm);

		bf.append("encode(").
			append(pgcryptoSchemaQuoted).
			append(".digest(").
			append(column, join).
			append(",'").
			append(type).
			append("'::\"text\"),'hex')");
	}

	@Override
	void appendPageClauseAfter(final Statement bf, final int offset, final int limit)
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
			append(column).
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
			final long start)
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
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + quotedName);
				return result;
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
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + name);
				return result + 1;
			}
		);
	}

	@Override
	void deleteSchema(
			final List<Table> tables,
			final List<SequenceX> sequences,
			final boolean forTest,
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

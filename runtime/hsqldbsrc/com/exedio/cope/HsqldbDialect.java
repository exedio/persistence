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

import static com.exedio.cope.HsqldbSchemaDialect.BIGINT;
import static com.exedio.cope.HsqldbSchemaDialect.BLOB;
import static com.exedio.cope.HsqldbSchemaDialect.DATE;
import static com.exedio.cope.HsqldbSchemaDialect.DOUBLE;
import static com.exedio.cope.HsqldbSchemaDialect.INTEGER;
import static com.exedio.cope.HsqldbSchemaDialect.SMALLINT;
import static com.exedio.cope.HsqldbSchemaDialect.TIMESTAMP_3;
import static com.exedio.cope.HsqldbSchemaDialect.TINYINT;
import static com.exedio.cope.HsqldbSchemaDialect.VARCHAR;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.dsmf.SQLRuntimeException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ServiceProperties(HsqldbDialect.Props.class)
@DialectProbeInfo({
		// http://hsqldb.org/doc/guide/dbproperties-chapt.html#N15634
		"hsqldb.tx", "mvcc"})
final class HsqldbDialect extends Dialect
{
	static final class Props extends Properties
	{
		final Approximate approximate = value("approximate", Approximate.nothing);

		@SuppressWarnings("unused") // OK: called by reflection
		Props(final Source source) { super(source); }
	}

	enum Approximate
	{
		nothing,
		@SuppressWarnings("unused")
		mysql57
		{
			@Override boolean supportsCheckConstraint() { return false; }
			@Override boolean supportsSchemaSavepoint() { return true; }
		};

		boolean supportsCheckConstraint() { return true; }
		boolean supportsSchemaSavepoint() { return false; }
	}

	private final Approximate approximate;

	/**
	 * @param probe must be there to be called by reflection
	 */
	HsqldbDialect(final CopeProbe probe, final Props props)
	{
		super(new HsqldbSchemaDialect(probe, props));
		probe.environmentInfo.
				requireDatabaseVersionAtLeast("HSQL Database Engine", 2, 5);

		approximate = props.approximate;
	}

	@Override
	boolean supportsUniqueViolation()
	{
		return true;
	}

	@Override
	String extractUniqueViolation(final SQLException exception)
	{
		if(!(exception instanceof SQLIntegrityConstraintViolationException))
			return null;

		final Matcher matcher =
				extractUniqueViolationMessagePattern.matcher(exception.getMessage());

		return matcher.matches()
				? matcher.group(1)
				: null;
	}

	private static final Pattern extractUniqueViolationMessagePattern = Pattern.compile(
			"^integrity constraint violation: unique constraint or index violation ; \"(.*)\" table: \".*\"$");

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		if(minimum>=Byte   .MIN_VALUE && maximum<=Byte   .MAX_VALUE) return TINYINT;
		if(minimum>=Short  .MIN_VALUE && maximum<=Short  .MAX_VALUE) return SMALLINT;
		if(minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) return INTEGER;
		return BIGINT;
	}

	@Override
	String getDoubleType()
	{
		return DOUBLE;
	}

	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		return VARCHAR(maxChars);
	}

	@Override
	String getDayType()
	{
		return DATE;
	}

	@Override
	String getWeekOfYear()
	{
		return "WEEK_OF_YEAR";
	}

	@Override
	String getDateTimestampType()
	{
		return TIMESTAMP_3;
	}

	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		return "MOD(" + quotedName + ',' + precision.divisor() + ")=0";
	}

	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append("X'");
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return BLOB;
	}

	@Override
	void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		if(!ascending)
			bf.append(" NULLS LAST");
	}

	@Override
	void appendPageClauseAfter(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		bf.append(" OFFSET ").
			appendParameter(offset);
		if(limit!=Query.UNLIMITED)
			bf.append(" LIMIT ").
				appendParameter(limit);
	}

	@Override
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("CONVERT(").
			append(source, join).
			append(",VARCHAR(40))");
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		appendMatchClauseByLike(bf, function, value);
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final int offset, final byte[] value)
	{
		bf.append( offset>0 ? "SUBSTR" : "LEFT" ).
			append("(RAWTOHEX(").
			append(column).
			append("),");
		if(offset>0)
			bf.appendParameter(2*offset+1).
				append(',');
		bf.appendParameter(2*value.length).
			append(")=").
			appendParameter(Hex.encodeLower(value));
	}

	@Override
	String getAveragePrefix()
	{
		return "AVG(CAST(";
	}

	@Override
	String getAveragePostfix()
	{
		return " AS DOUBLE))";
	}

	@Override
	String maskLikePattern(final String pattern)
	{
		return maskLikePatternInternal(pattern);
	}

	/**
	 * Without this method a backslash not followed by backslash, percent sign or underscore
	 * causes a
	 * <pre>
	 * org.hsqldb.HsqlException: data exception: invalid escape sequence
	 * </pre>
	 */
	static String maskLikePatternInternal(final String pattern)
	{
		return LIKE_PATTERN.matcher(pattern).replaceAll("$1$2");
	}

	private static final Pattern LIKE_PATTERN = Pattern.compile("([^\\\\]|^)\\\\([^\\\\%_]|$)");

	@Override
	boolean likeRequiresEscapeBackslash()
	{
		return true;
	}

	@Override
	void appendRegexpLike(final Statement bf, final StringFunction function, final String regexp)
	{
		bf.append("REGEXP_MATCHES(").
			append(function).
			append(',').
			appendParameter(RegexpLikeCondition.getIcuRegexp(regexp)).
			append(')');
	}

	@Override
	String getClause(final String column, final String regexp)
	{
		return "REGEXP_MATCHES("+column + "," +
				 StringColumn.cacheToDatabaseStatic(
						 RegexpLikeCondition.getIcuRegexp(regexp)
				 ) + ")";
	}

	@Override
	Long nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final String TEMP_TABLE = dsmfDialect.quoteName("hsqldb_temp_table_for_sequences");
		try
		{
			connection.setAutoCommit(false);
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "setAutoCommit");
		}
		{
			final Statement bf = executor.newStatement();
			bf.append("DECLARE LOCAL TEMPORARY TABLE ").
				append(TEMP_TABLE).
				append(" (x BIGINT)");
			executor.update(connection, null, bf);
		}
		{
			final Statement bf = executor.newStatement();
			bf.append("INSERT INTO ").
				append(TEMP_TABLE).
				append(" VALUES (0)");
			executor.updateStrict(connection, null, bf);
		}
		final Long result;
		{
			final Statement bf = executor.newStatement();
			bf.append("SELECT NEXT VALUE FOR ").
				append(quotedName).
				append(" FROM ").
				append(TEMP_TABLE);

			result = executor.query(connection, bf, null, false, resultSet ->
				{
					if(!resultSet.next())
						throw new RuntimeException("empty in sequence " + quotedName);
					final long resultX = resultSet.getLong(1);
					if(resultSet.wasNull())
						throw new RuntimeException("null in sequence " + quotedName);
					return resultX;
				}
			);
		}
		{
			final Statement bf = executor.newStatement();
			bf.append("DROP TABLE session.").
				append(TEMP_TABLE);
			executor.update(connection, null, bf);
		}
		try
		{
			connection.commit();
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "commit");
		}
		return result;
	}

	@Override
	Long getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		//language=SQL
		final Statement bf = executor.newStatement();
		bf.append("SELECT NEXT_VALUE" +
					" FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES" +
					" WHERE SEQUENCE_NAME='").append(name).append('\'');

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + name);
				return result;
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

		for(final Table table : tables)
		{
			bf.append("TRUNCATE TABLE ").
				append(table.quotedID).
				append(" RESTART IDENTITY AND COMMIT NO CHECK;");
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
	void append(
			final VaultTrail trail,
			final Statement bf,
			final String hashValue,
			final DataConsumer consumer,
			final boolean markPutEnabled,
			final VaultPutInfo putInfo)
	{
		// http://hsqldb.org/doc/2.0/guide/dataaccess-chapt.html#dac_merge_statement
		bf.append("MERGE INTO ").append(trail.tableQuoted).
				append(" USING(VALUES(").
				appendParameter(hashValue).
				append("))AS vals(\"x\")ON ").
				append(trail.tableQuoted).
				append('.').
				append(trail.hashQuoted).
				append("=vals.\"x\" WHEN NOT MATCHED THEN INSERT(");
		trail.appendInsertColumns(bf, markPutEnabled);
		bf.
				append(")VALUES ").
				append("vals.\"x\"");
		trail.appendInsertValuesAfterHash(bf, consumer, markPutEnabled, putInfo);

		if(markPutEnabled)
		{
			bf.append(" WHEN MATCHED THEN UPDATE SET ");
			trail.appendSetMarkPut(bf);
		}
	}

	@Override
	String getSchemaSavepoint(final ConnectionPool connectionPool) throws SchemaSavepointNotAvailableException, SQLException
	{
		if(!approximate.supportsSchemaSavepoint())
			return super.getSchemaSavepoint(connectionPool);

		final Connection connection = connectionPool.get(true);
		try(
				java.sql.Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("VALUES NOW()"))
		{
			if(!rs.next())
				throw new SQLException("empty result");

			final String now = rs.getString(1);
			if(now==null)
				throw new SQLException("null");

			if(rs.next())
				throw new SQLException("multiple lines");

			return "hsqldb approximate schemaSavepoint " + approximate + ' ' + now;
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}

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

import static com.exedio.cope.HsqldbDialect.Approximate.oracle;
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
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Sequence;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumMap;
import java.util.List;

@ServiceProperties(HsqldbDialect.Props.class)
@DialectProbeInfo({
		// see completeConnectionInfo
		"hsqldb.tx", "mvcc"})
final class HsqldbDialect extends Dialect
{
	static final class Props extends Properties
	{
		final Approximate approximate = value("approximate", Approximate.nothing);

		Props(final Source source) { super(source); }
	}

	enum Approximate
	{
		nothing,
		@SuppressWarnings("unused")
		mysql55
		{
			@Override boolean supportsCheckConstraints() { return false; }
			@Override boolean supportsNativeDate() { return false; }
		},
		@SuppressWarnings("unused")
		mysql56
		{
			@Override boolean supportsCheckConstraints() { return false; }
			@Override boolean supportsSchemaSavepoint() { return true; }
		},
		oracle;

		boolean supportsCheckConstraints() { return true; }
		boolean supportsNativeDate() { return true; }
		boolean supportsSchemaSavepoint() { return false; }
	}

	private final Approximate approximate;

	/**
	 * @param probe must be there to be called by reflection
	 */
	HsqldbDialect(final CopeProbe probe, final Props props)
	{
		super(
				new HsqldbSchemaDialect(props.approximate.supportsCheckConstraints()));

		probe.environmentInfo.requireDatabaseVersionAtLeast("HSQL Database Engine", 2, 4);

		this.approximate = props.approximate;
	}

	@Override
	void completeConnectionInfo(final java.util.Properties info)
	{
		// http://hsqldb.org/doc/guide/dbproperties-chapt.html#N15634
		requireConnectionInfo(info, "hsqldb.tx", "mvcc");
	}

	@Override
	void setNameTrimmers(final EnumMap<TrimClass, Trimmer> trimmers)
	{
		super.setNameTrimmers(trimmers);

		if(approximate==oracle) // TODO Oracle 12 Will increase to 128 on Release 12.2 or higher.
		{
			// copied code from OracleDialect
			final Trimmer dataTrimmer = trimmers.get(TrimClass.Data);

			for(final TrimClass c : TrimClass.values())
				if(c!=TrimClass.Data)
					trimmers.put(c, dataTrimmer);
		}
	}

	/**
	 * Additional parenthesis are needed for hsqldb,
	 */
	@Override
	boolean inRequiresParenthesis()
	{
		// https://sourceforge.net/tracker/?func=detail&atid=378131&aid=3101603&group_id=23316
		return true;
	}

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
	boolean supportsEmptyStrings()
	{
		if(approximate==oracle)
			return false; // copied code from OracleDialect

		return super.supportsEmptyStrings();
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
		if(!approximate.supportsNativeDate())
			return null;

		return TIMESTAMP_3;
	}

	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		return "MOD(" + quotedName + ',' + precision.divisor() + ")=0";
	}

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	@Override
	byte[] getBytes(final ResultSet resultSet, final int columnIndex) throws SQLException
	{
		final Blob blob = resultSet.getBlob(columnIndex);
		if(blob==null)
			return null;

		return DataField.copy(blob.getBinaryStream(), blob.length());
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
	PageSupport getPageSupport()
	{
		return PageSupport.CLAUSE_AFTER_WHERE;
	}

	@Override
	void appendPageClause(final Statement bf, final int offset, final int limit)
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
	void appendPageClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException(bf.toString());
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
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("LEFT(RAWTOHEX(").
			append(column).
			append("),").
			appendParameter(2*value.length).
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
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	void deleteSchema(
			final List<Table> tables,
			final List<SequenceX> sequences,
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

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
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
			final Sequence.Type type, final long start)
	{
		bf.append("ALTER SEQUENCE ").
			append(quotedName).
			append(" RESTART WITH ").
			append(start).
			append(';');
	}

	@Override
	String getSchemaSavepoint(final ConnectionPool connectionPool) throws SQLException
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

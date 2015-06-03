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

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

final class HsqldbDialect extends Dialect
{
	/**
	 * @param probe must be there to be called by reflection
	 */
	HsqldbDialect(final Probe probe)
	{
		super(
				new com.exedio.dsmf.HsqldbDialect());
	}

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		// TODO: select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "INTEGER" : "BIGINT";
	}

	@Override
	String getDoubleType()
	{
		return "DOUBLE";
	}

	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		return "VARCHAR("+maxChars+")";
	}

	@Override
	String getDayType()
	{
		return "DATE";
	}

	@Override
	String getDateTimestampType()
	{
		return "TIMESTAMP(3) WITHOUT TIME ZONE";
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
		return "BLOB";
	}

	@Override
	void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		if(!ascending)
			bf.append(" NULLS LAST");
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

		bf.append(" OFFSET ").
			appendParameter(offset);
		if(limit!=Query.UNLIMITED)
			bf.append(" LIMIT ").
				appendParameter(limit);
	}

	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
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
			append(column, (Join)null).
			append("),").
			appendParameter(2*value.length).
			append(")=").
			appendParameter(Hex.encodeLower(value));
	}

	@Override
	Integer nextSequence(
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
				append(" (x INTEGER)");
			executor.update(connection, null, bf);
		}
		{
			final Statement bf = executor.newStatement();
			bf.append("INSERT INTO ").
				append(TEMP_TABLE).
				append(" VALUES (0)");
			executor.updateStrict(connection, null, bf);
		}
		final Integer result;
		{
			final Statement bf = executor.newStatement();
			bf.append("SELECT NEXT VALUE FOR ").
				append(quotedName).
				append(" FROM ").
				append(TEMP_TABLE);

			result = executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
			{
				public Integer handle(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new RuntimeException("empty in sequence " + quotedName);
					final Object o = resultSet.getObject(1);
					if(o==null)
						throw new RuntimeException("null in sequence " + quotedName);
					return (Integer)o;
				}
			});
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
	Integer getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT NEXT_VALUE" +
					" FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES" +
					" WHERE SEQUENCE_NAME='").append(name).append('\'');

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return Integer.valueOf((String)o);
			}
		});
	}

	@Override
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
	void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append("ALTER SEQUENCE ").
			append(quotedName).
			append(" RESTART WITH ").
			append(startWith).
			append(';');
	}
}

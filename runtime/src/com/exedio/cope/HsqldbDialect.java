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

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.SQLRuntimeException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

final class HsqldbDialect extends Dialect
{
	private final boolean nullsAreSortedLow;

	/**
	 * @param parameters must be there to be called by reflection
	 */
	protected HsqldbDialect(final DialectParameters parameters)
	{
		super(
				parameters,
				new com.exedio.dsmf.HsqldbDialect());

		this.nullsAreSortedLow = parameters.properties.hsqldbNullsAreSortedLow;
	}

	@Override
	protected int filterTransationIsolation(final int level)
	{
		switch(level)
		{
			case Connection.TRANSACTION_REPEATABLE_READ: return Connection.TRANSACTION_READ_COMMITTED;
			default: return level;
		}
	}

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		// TODO: select between TINYINT, SMALLINT, INTEGER, BIGINT, NUMBER
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "integer" : "bigint";
	}

	@Override
	String getDoubleType()
	{
		return "double";
	}

	@Override
	String getStringType(final int maxChars)
	{
		return "varchar("+maxChars+")";
	}

	@Override
	String getDayType()
	{
		return "date";
	}

	@Override
	String getDateTimestampType()
	{
		return "timestamp";
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
		statementText.append('X');
		super.addBlobInStatementText(statementText, parameter);
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return "blob";
	}

	@Override
	boolean nullsAreSortedLow()
	{
		if(super.nullsAreSortedLow()==true)
			System.out.println(getClass().getName() + ": nullsAreSortedLow is unexpectedly correct");
		return nullsAreSortedLow;
	}

	@Override
	protected void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		if(ascending)
		{
			if(!nullsAreSortedLow)
				bf.append(" nulls last");
		}
		else
		{
			if(nullsAreSortedLow)
				bf.append(" nulls last");
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

		bf.append(" offset ").
			appendParameter(offset);
		if(limit!=Query.UNLIMITED)
			bf.append(" limit ").
				appendParameter(limit);
	}

	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException(bf.toString());
	}

	@Override
	protected void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("CONVERT(").
			append(source, join).
			append(",VARCHAR(40))");
	}

	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		appendMatchClauseByLike(bf, function, value);
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("left(RAWTOHEX(").
			append(column, (Join)null).
			append("),").
			appendParameter(2*value.length).
			append(")=").
			appendParameter(Hex.encodeLower(value));
	}

	@Override
	boolean fakesSupportTransactionIsolationReadCommitted()
	{
		return true;
	}

	@Override
	protected Integer nextSequence(
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
			bf.append("CREATE TEMPORARY TABLE ").
				append(TEMP_TABLE).
				append(" (x integer)");
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
			bf.append("DROP TABLE ").
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
	protected Integer getNextSequence(
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
	protected void deleteSchema(final Database database, final ConnectionPool connectionPool)
	{
		final StringBuilder bf = new StringBuilder();

		for(final Table table : database.getTables())
		{
			bf.append("truncate table ").
				append(table.quotedID).
				append(" restart identity and commit no check;");
		}

		for(final SequenceX sequence : database.getSequences())
			sequence.delete(bf, this);

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
	protected void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append("alter sequence ").
			append(quotedName).
			append(" restart with ").
			append(startWith).
			append(';');
	}
}

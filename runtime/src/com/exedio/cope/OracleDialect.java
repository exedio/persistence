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

import com.exedio.cope.DayPartView.Part;
import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

final class OracleDialect extends Dialect
{
	/**
	 * See https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html
	 */
	private static final int MAX_BYTES_PER_CHARACTER_UTF8 = 3;

	private static final int VARCHAR_MAX_BYTES = 4000;
	        static final int VARCHAR_MAX_CHARS = VARCHAR_MAX_BYTES / MAX_BYTES_PER_CHARACTER_UTF8;

	OracleDialect(final Probe probe)
	{
		super(
				new com.exedio.dsmf.OracleDialect(
						probe.properties.getConnectionUsername().toUpperCase(Locale.ENGLISH)));
	}

	@Override
	int getTransationIsolation()
	{
		return Connection.TRANSACTION_READ_COMMITTED;
	}

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		return "NUMBER(" + ((minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? 10 : 20) + ')'; // TODO do this more precisely
	}

	@Override
	String getDoubleType()
	{
		return "NUMBER(30,8)";
	}

	/**
	 * The parameter of varchar specifies bytes.
	 * The parameter of nvarchar specifies characters.
	 * The maximum for both varchar and nvarchar is 4000 bytes.
	 */
	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		if(maxChars<=VARCHAR_MAX_CHARS)
			return "VARCHAR2(" + (maxChars*MAX_BYTES_PER_CHARACTER_UTF8) + " BYTE)";
		else
			return "CLOB"; // TODO may be should be (varchar?"CLOB":"NCLOB") , but does not work, gets in charset trouble
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
	void appendDatePartExtraction(final DayPartView view, final Statement bf, final Join join)
	{
		if(Part.WEEK_OF_YEAR.equals(view.getPart()))
		{
			bf.append("TO_NUMBER(TO_CHAR(").
				append(view.getSource(), join).
				append(", 'IW'))");
		}
		else
		{
			super.appendDatePartExtraction(view, bf, join);
		}
	}

	@Override
	String getDateTimestampType()
	{
		return "TIMESTAMP(3)";
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return "BLOB";
	}

	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append('\'');
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final Function<E> dividend,
			final Function<E> divisor,
			final Join join)
	{
		bf.append("TRUNC(").
			append(dividend, join).
			append('/').
			append(divisor, join).
			append(')');
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSES_AROUND;
	}

	@Override
	void appendLimitClause(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		// TODO: check, whether ROW_NUMBER() OVER is faster,
		// see http://www.php-faq.de/q/q-oracle-limit.html
		bf.append("SELECT * FROM(");
		if(offset>0)
			bf.append("SELECT "+com.exedio.cope.Table.SQL_ALIAS_2+".*,ROWNUM "+com.exedio.cope.Table.SQL_ALIAS_1+" FROM(");
	}

	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		bf.append(')');
		if(offset>0)
			bf.append(com.exedio.cope.Table.SQL_ALIAS_2+' ');
		if(limit!=Query.UNLIMITED)
			bf.append("WHERE ROWNUM<=").appendParameter(offset+limit);
		if(offset>0)
			bf.append(")WHERE "+com.exedio.cope.Table.SQL_ALIAS_1+'>').appendParameter(offset);
	}

	@Override
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("TO_CHAR(").
			append(source, join).
			append(')');
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(CONTAINS(").
			append(function, (Join)null).
			append(',').
			appendParameterAny(value).
			append(")>0)");
	}

	@Override
	String getBlobLength()
	{
		return "LENGTH";
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("RAWTOHEX(DBMS_LOB.SUBSTR(").
			append(column, (Join)null).
			append(',').
			appendParameter(value.length).
			append(",1))=").
			appendParameter(Hex.encodeUpper(value));
	}

	@Override
	boolean supportsEmptyStrings()
	{
		return false;
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
	void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append(
			"EXECUTE IMMEDIATE " +
				"'DROP SEQUENCE ").append(quotedName).append("';" +
			"EXECUTE IMMEDIATE '");
				com.exedio.dsmf.OracleDialect.createSequenceStatic(bf, quotedName, startWith);
				bf.append("';");
	}

	@Override
	Integer nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT ").
			append(quotedName).
			append(".NEXTVAL FROM DUAL");

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + quotedName);
				return ((BigDecimal)o).intValue();
			}
		});
	}

	@Override
	Integer getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		// NOTE:
		// Do not use "SELECT name.currval FROM DUAL" because this may cause
		// ORA-08002: sequence NAME.CURRVAL is not yet defined in this session
		final Statement bf = executor.newStatement();
		bf.append(
				"SELECT LAST_NUMBER " +
				"FROM user_sequences " +
				"WHERE sequence_name=").
			appendParameter(name);

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((BigDecimal)o).intValue() + 1;
			}
		});
	}

	@Override
	void deleteSchema(
			final List<Table> tables,
			final List<SequenceX> sequences,
			final ConnectionPool connectionPool)
	{
		final Connection connection = connectionPool.get(false);
		try
		{
			if(!tables.isEmpty())
			{
				Executor.update(connection, "set constraints all deferred");

				final StringBuilder bf = new StringBuilder("BEGIN ");
				for(final Table table : tables)
				{
					bf.append("DELETE FROM ").
						append(table.quotedID).
						append(';');
				}
				bf.append("END;");

				boolean committed = false;
				try
				{
					Executor.update(connection, bf.toString());
					connection.commit();
					committed = true;
				}
				finally
				{
					if(!committed)
						connection.rollback();
				}
			}

			if(!sequences.isEmpty())
			{
				final StringBuilder bf = new StringBuilder();
				for(final SequenceX sequence : sequences)
					sequence.delete(bf, this);

				if(bf.length()>0)
					Executor.update(connection, "BEGIN " + bf.toString() + "END;");
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "commit/rollback");
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}

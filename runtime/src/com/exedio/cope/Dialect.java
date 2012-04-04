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
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;

/**
 * Adapts COPE to different RDBMS.
 */
abstract class Dialect
{
	protected static final int TWOPOW8 = 1<<8;
	protected static final int TWOPOW16 = 1<<16;
	protected static final int TWOPOW24 = 1<<24;
	protected static final int MAX_BYTES_PER_CHARACTER_UTF8 = 3;

	protected static final int ORACLE_VARCHAR_MAX_BYTES = 4000;
	protected static final int ORACLE_VARCHAR_MAX_CHARS = ORACLE_VARCHAR_MAX_BYTES / MAX_BYTES_PER_CHARACTER_UTF8;

	private final boolean nullsAreSortedLow;
	final com.exedio.dsmf.Dialect dsmfDialect;

	protected Dialect(final DialectParameters parameters, final com.exedio.dsmf.Dialect dsmfDialect)
	{
		this.nullsAreSortedLow = parameters.nullsAreSortedLow;
		this.dsmfDialect = dsmfDialect;
	}

	/**
	 * @param info used in subclasses
	 */
	protected void completeConnectionInfo(final Properties info)
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	/**
	 * @param out used in subclasses
	 * @throws IOException thrown by subclasses
	 */
	protected void prepareDumperConnection(final Appendable out) throws IOException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	/**
	 * @param out used in subclasses
	 * @throws IOException thrown by subclasses
	 */
	protected void unprepareDumperConnection(final Appendable out) throws IOException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	protected int filterTransationIsolation(final int level)
	{
		return level;
	}

	protected static final String EXPLAIN_PLAN = "explain plan";

	/**
	 * @param statement used in subclasses
	 * @param connection used in subclasses
	 * @param executor used in subclasses
	 */
	protected QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor executor)
	{
		return null;
	}

	byte[] getBytes(final ResultSet resultSet, final int columnIndex) throws SQLException
	{
		return resultSet.getBytes(columnIndex);
	}

	void fetchBlob(
			final ResultSet resultSet, final int columnIndex,
			final Item item, final OutputStream data, final DataField field)
	throws SQLException
	{
		final Blob blob = resultSet.getBlob(columnIndex);
		if(blob!=null)
		{
			final InputStream source = blob.getBinaryStream();
			try
			{
				field.copy(source, data, blob.length(), item);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(field.toString(), e);
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
	}

	boolean nullsAreSortedLow()
	{
		return nullsAreSortedLow;
	}

	boolean supportsEmptyStrings()
	{
		return true;
	}

	boolean supportsNotNull()
	{
		return false;
	}

	boolean supportsRandom()
	{
		return false;
	}

	abstract boolean supportsSelectingUngrouped();

	boolean fakesSupportTransactionIsolationReadCommitted()
	{
		return false;
	}

	boolean subqueryRequiresAlias()
	{
		return false;
	}

	/**
	 * @see #extractUniqueViolation(SQLException)
	 */
	boolean supportsUniqueViolation()
	{
		return false;
	}

	/**
	 * @param exception used in subclasses
	 * @see #supportsUniqueViolation()
	 */
	String extractUniqueViolation(final SQLException exception)
	{
		throw new RuntimeException("not supported");
	}

	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append('\'');
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final Function<E> dividend,
			final Function<E> divisor,
			final Join join)
	{
		bf.append(dividend, join).
			append("/").
			append(divisor, join);
	}

	abstract String getIntegerType(long minimum, long maximum);
	abstract String getDoubleType();
	abstract String getStringType(int maxChars);

	String getStringLength()
	{
		return "CHAR_LENGTH";
	}

	abstract String getDayType();

	/**
	 * Returns a column type suitable for storing timestamps
	 * with milliseconds resolution.
	 * This method may return null,
	 * if the database does not support such a column type.
	 * The framework will then fall back to store the number of milliseconds.
	 */
	abstract String getDateTimestampType();
	abstract String getBlobType(long maximumLength);

	protected void appendOrderBy(final Statement bf, final Function function, final boolean ascending)
	{
		bf.append(function, (Join)null);
		if(!ascending)
			bf.append(" desc");
	}

	abstract LimitSupport getLimitSupport();

	static enum LimitSupport
	{
		CLAUSE_AFTER_WHERE,
		CLAUSES_AROUND;
	}

	/**
	 * Appends a clause to the statement causing the database limiting the query result.
	 * This method is never called for <tt>offset==0 && limit=={@link Query#UNLIMITED}</tt>.
	 * NOTE: Don't forget the space before the keyword 'limit'!
	 * @param offset the number of rows to be skipped
	 *        or zero, if no rows to be skipped.
	 *        Is never negative.
	 * @param limit the number of rows to be returned
	 *        or {@link Query#UNLIMITED} if all rows to be returned.
	 *        Is always positive (greater zero).
	 */
	abstract void appendLimitClause(Statement bf, int offset, int limit);

	/**
	 * Same as {@link #appendLimitClause(Statement, int, int)}.
	 * Is used for {@link LimitSupport#CLAUSES_AROUND} only,
	 * for the postfix.
	 */
	abstract void appendLimitClause2(Statement bf, int offset, int limit);

	abstract void appendAsString(Statement bf, NumberFunction source, Join join);

	abstract void appendMatchClauseFullTextIndex(Statement bf, StringFunction function, String value);

	protected final void appendMatchClauseByLike(final Statement bf, final StringFunction function, final String value)
	{
		bf.append(function, (Join)null).
			append(" like ").
			appendParameterAny(LikeCondition.WILDCARD + value + LikeCondition.WILDCARD);
	}

	String getBlobLength()
	{
		return "OCTET_LENGTH";
	}

	abstract void appendStartsWith(Statement bf, BlobColumn column, byte[] value);

	/**
	 * Returns null, if the dialect does not support clauses for CharacterSet.
	 */
	protected String getClause(final String column, final CharSet set)
	{
		if(column==null)
			throw new NullPointerException();
		if(set==null)
			throw new NullPointerException();

		return null;
	}

	protected abstract Integer    nextSequence(Executor executor, Connection connection, String name);
	protected abstract Integer getNextSequence(Executor executor, Connection connection, String name);
}

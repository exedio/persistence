/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.exedio.dsmf.Driver;
import com.exedio.dsmf.Schema;

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
	
	final Driver driver;
	final String stringLength;
	
	protected Dialect(final Driver driver, final String stringLength)
	{
		this.driver = driver;
		this.stringLength = stringLength;
	}
	
	protected void completeConnectionInfo(final java.util.Properties info)
	{
		// default implementation does nothing, may be overwritten by subclasses
	}
	
	protected static final String EXPLAIN_PLAN = "explain plan";
	
	protected QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Database database)
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
			InputStream source = null;
			try
			{
				source = blob.getBinaryStream();
				field.copy(source, data, blob.length(), item);
			}
			catch(IOException e)
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
					catch(IOException e)
					{/*IGNORE*/}
				}
			}
		}
	}

	boolean supportsEmptyStrings()
	{
		return true;
	}

	boolean fakesSupportReadCommitted()
	{
		return false;
	}

	/**
	 * Specifies the factor,
	 * the length function of blob columns is wrong.
	 */
	int getBlobLengthFactor()
	{
		return 1;
	}
	
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append('\'');
		DataField.appendAsHex(parameter, parameter.length, statementText);
		statementText.append('\'');
	}

	abstract String getIntegerType(long minimum, long maximum);
	abstract String getDoubleType();
	abstract String getStringType(int maxChars);
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
	
	abstract LimitSupport getLimitSupport();
	
	static enum LimitSupport
	{
		NONE,
		CLAUSE_AFTER_SELECT,
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

	abstract void appendMatchClauseFullTextIndex(Statement bf, StringFunction function, String value);
	
	protected final void appendMatchClauseByLike(final Statement bf, final StringFunction function, final String value)
	{
		bf.append(function, (Join)null).
			append(" like ").
			appendParameter(function, LikeCondition.WILDCARD + value + LikeCondition.WILDCARD);
	}
	
	protected void completeSchema(final Schema schema)
	{
		// empty default implementation
	}
	
	boolean isDefiningColumnTypes()
	{
		return false;
	}
	
	void defineColumnTypes(TIntArrayList columnTypes, java.sql.Statement statement)
			throws SQLException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}
}

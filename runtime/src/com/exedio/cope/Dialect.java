package com.exedio.cope;

import java.sql.Connection;
import java.sql.SQLException;

import bak.pcj.list.IntList;

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
	
	final Driver driver;
	
	protected Dialect(final Driver driver)
	{
		this.driver = driver;
	}
	
	protected void completeConnectionInfo(final java.util.Properties info)
	{
		// default implementation does nothing, may be overwritten by subclasses
	}
	
	protected static final String EXPLAIN_PLAN = "explain plan";
	
	protected StatementInfo explainExecutionPlan(final Statement statement, final Connection connection, final Database database)
	{
		return null;
	}
	
	protected abstract String extractUniqueConstraintName(SQLException e);
	
	protected final static String ANY_CONSTRAINT = "--ANY--";

	boolean supportsGetBytes()
	{
		return true;
	}

	boolean supportsBlobInResultSet()
	{
		return true;
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

	/**
	 * By overriding this method subclasses can enable the use of save points.
	 * Some databases cannot recover from constraint violations in
	 * the same transaction without a little help,
	 * they need a save point set before the modification, that can be
	 * recovered manually.
	 */
	boolean needsSavepoint()
	{
		return false;
	}

	abstract String getIntegerType(long minimum, long maximum);
	abstract String getDoubleType();
	abstract String getStringType(int maxLength);
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
	 * This method is never called for <tt>start==0 && count=={@link Query#UNLIMITED_COUNT}</tt>.
	 * NOTE: Don't forget the space before the keyword 'limit'!
	 * @param start the number of rows to be skipped
	 *        or zero, if no rows to be skipped.
	 *        Is never negative.
	 * @param count the number of rows to be returned
	 *        or {@link Query#UNLIMITED_COUNT} if all rows to be returned.
	 *        Is always positive (greater zero).
	 */
	abstract void appendLimitClause(Statement bf, int start, int count);
	
	/**
	 * Same as {@link #appendLimitClause(Statement, int, int)}.
	 * Is used for {@link LimitSupport#CLAUSES_AROUND} only,
	 * for the postfix.
	 */
	abstract void appendLimitClause2(Statement bf, int start, int count);

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
	
	void defineColumnTypes(IntList columnTypes, java.sql.Statement statement)
			throws SQLException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}
}

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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Day;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Adapts COPE to different RDBMS.
 */
abstract class Dialect
{
	static final int TWOPOW8 = 1<<8;
	static final int TWOPOW16 = 1<<16;
	static final int TWOPOW24 = 1<<24;

	final com.exedio.dsmf.Dialect dsmfDialect;

	Dialect(final com.exedio.dsmf.Dialect dsmfDialect)
	{
		this.dsmfDialect = dsmfDialect;
	}

	/**
	 * @param info used in subclasses
	 */
	void completeConnectionInfo(final Properties info)
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	/**
	 * @param connection used in subclasses
	 * @throws SQLException thrown by subclasses
	 */
	@SuppressWarnings("RedundantThrows") // IDEA bug - SQLException is not redundant
	void completeConnection(final Connection connection) throws SQLException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	/**
	 * @param out used in subclasses
	 * @throws IOException thrown by subclasses
	 */
	@SuppressWarnings("RedundantThrows") // IDEA bug - IOException is not redundant
	void prepareDumperConnection(final Appendable out) throws IOException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	/**
	 * @param out used in subclasses
	 * @throws IOException thrown by subclasses
	 */
	@SuppressWarnings("RedundantThrows") // IDEA bug - IOException is not redundant
	void unprepareDumperConnection(final Appendable out) throws IOException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}

	void setNameTrimmers(final EnumMap<TrimClass, Trimmer> trimmers)
	{
		// MySQL maximum length is 63:
		// https://dev.mysql.com/doc/refman/5.6/en/identifiers.html
		// MySQL does not support check constraints.

		// PostgreSQL maximum length is 63:
		// https://www.postgresql.org/docs/9.6/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS

		final Trimmer dataTrimmer = new Trimmer(25);
		trimmers.put(TrimClass.Data, dataTrimmer);

		final Trimmer constraintTrimmer = new Trimmer(60);
		trimmers.put(TrimClass.ForeignKeyUniqueConstraint, constraintTrimmer);
		trimmers.put(TrimClass. PrimaryKeyCheckConstraint, constraintTrimmer);
	}

	int getTransationIsolation()
	{
		return Connection.TRANSACTION_REPEATABLE_READ;
	}

	static final String EXPLAIN_PLAN = "explain plan";

	/**
	 * @param statement used in subclasses
	 * @param connection used in subclasses
	 * @param executor used in subclasses
	 */
	QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor executor)
	{
		return null;
	}

	void fetchBlob(
			final ResultSet resultSet, final int columnIndex,
			final Item item, final OutputStream sink, final DataField field)
	throws SQLException
	{
		final Blob blob = resultSet.getBlob(columnIndex);
		if(blob!=null)
		{
			try(InputStream source = blob.getBinaryStream())
			{
				field.copy(source, sink, blob.length(), item);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(field.toString(), e);
			}
		}
	}

	boolean supportsEmptyStrings()
	{
		return true;
	}

	boolean supportsUTF8mb4()
	{
		return true;
	}

	boolean supportsRandom()
	{
		return false;
	}

	boolean inRequiresParenthesis()
	{
		return false;
	}

	String getInComma()
	{
		return ",";
	}

	boolean subqueryRequiresAlias()
	{
		return false;
	}

	boolean subqueryRequiresAliasInSelect()
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

	boolean supportsAnyValue()
	{
		return false;
	}

	void  appendIsNullInSelect(
			final Statement bf,
			final BlobColumn column)
	{
		bf.append(column.quotedID).
			append(" IS NULL");
	}

	abstract void addBlobInStatementText(StringBuilder statementText, byte[] parameter);

	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final Function<E> dividend,
			final Function<E> divisor,
			final Join join)
	{
		bf.append(dividend, join).
			append('/').
			append(divisor, join);
	}

	abstract String getIntegerType(long minimum, long maximum);
	abstract String getDoubleType();

	String format(final double number)
	{
		return Double.toString(number);
	}

	abstract String getStringType(int maxChars, MysqlExtendedVarchar mysqlExtendedVarchar);

	String getStringLength()
	{
		return "CHAR_LENGTH";
	}

	String getWeekOfYear()
	{
		return "WEEK";
	}

	void appendDatePartExtraction(final DayPartView view, final Statement bf, final Join join)
	{
		bf.append("EXTRACT(")
				.append(view.getPart().getNameForDialect(this))
				.append(" FROM ")
				.append(view.getSource(), join)
				.append(')');
	}

	abstract String getDayType();

	Object marshalDay(final Day cell)
	{
		return cell.toLocalDate().toString();
	}

	static final java.sql.Date marshalDayDeprecated(final Day cell)
	{
		final int year = cell.getYear()-1900;
		final int month = cell.getMonthValue()-1;
		final int day = cell.getDayOfMonth();
		// OK: need information about the day without taking time into account
		@SuppressWarnings("deprecation")
		final java.sql.Date result = new java.sql.Date(year, month, day);
		return result;
	}

	Day unmarshalDay(final String cell)
	{
		return Day.from(LocalDate.parse(cell));
	}

	/**
	 * Returns a column type suitable for storing timestamps
	 * with milliseconds resolution.
	 * This method may return null,
	 * if the database does not support such a column type.
	 * The framework will then fall back to store the number of milliseconds.
	 */
	abstract String getDateTimestampType();

	/**
	 * Don't use a static instance,
	 * since then access must be synchronized
	 */
	String toLiteral(final Date value)
	{
		return DateField.format("'TIMESTAMP'''yyyy-MM-dd HH:mm:ss.SSS''").format(value);
	}

	/**
	 * Don't use a static instance,
	 * since then access must be synchronized
	 */
	String toLiteral(final Day value)
	{
		final NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMinimumIntegerDigits(2);
		return "DATE'" + value.getYear() + '-' + nf.format(value.getMonthValue()) + '-' + nf.format(value.getDayOfMonth()) + '\'';
	}

	String getDateTimestampPrecisionMinuteSecond(final boolean isSecond, final String quotedName)
	{
		final String seconds = getDateExtract(quotedName, DateField.Precision.SECOND);
		return
				isSecond
				? (seconds + '=' + getFloor(seconds)) // is an integer
				: (seconds + "=0");
	}

	String getDateExtract(final String quotedName, final Precision precision)
	{
		return "EXTRACT(" + precision.sql() + " FROM " + quotedName + ')';
	}

	String getFloor(final String quotedName)
	{
		return "FLOOR(" + quotedName + ')';
	}

	abstract String getDateIntegerPrecision(String quotedName, Precision precision);

	abstract String getBlobType(long maximumLength);

	protected static final String HASH_MD5 = "MD5";
	protected static final String HASH_SHA    = "SHA";
	protected static final String HASH_SHA224 = "SHA-224";
	protected static final String HASH_SHA256 = "SHA-256";
	protected static final String HASH_SHA384 = "SHA-384";
	protected static final String HASH_SHA512 = "SHA-512";

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS") // OK: is converted into empty set by calling code
	String[] getBlobHashAlgorithms()
	{
		return null;
	}

	void appendBlobHash(
			final Statement bf, final BlobColumn column, final Join join,
			final String algorithm)
	{
		throw new UnsupportedQueryException("hash >" + algorithm + "< not supported");
	}


	/**
	 * @param bf the statement, the postfix is to be appended to
	 * @param ascending whether the order by is ascending or descending
	 */
	void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		bf.append(
				ascending
				? " NULLS FIRST"
				: " NULLS LAST" );
	}

	/**
	 * Same as {@link #appendPageClauseAfter(Statement, int, int)},
	 * but called before the statement.
	 */
	@SuppressWarnings("unused") // TODO oracle: remove
	void appendPageClauseBefore(final Statement bf, final int offset, final int limit) {}

	/**
	 * Appends a clause to the statement causing the database paging the query result.
	 * This method is never called for {@code offset==0 &amp;&amp; limit=={@link Query#UNLIMITED}}.
	 * NOTE: Don't forget the space before the keyword 'limit'!
	 * @param offset the number of rows to be skipped
	 *        or zero, if no rows to be skipped.
	 *        Is never negative.
	 * @param limit the number of rows to be returned
	 *        or {@link Query#UNLIMITED} if all rows to be returned.
	 *        Is always positive (greater zero).
	 */
	abstract void appendPageClauseAfter(Statement bf, int offset, int limit);

	String getExistsPrefix()
	{
		return "SELECT COUNT(*) FROM (";
	}

	String getExistsPostfix()
	{
		return " LIMIT 1)";
	}

	abstract void appendAsString(Statement bf, NumberFunction<?> source, Join join);

	abstract void appendMatchClauseFullTextIndex(Statement bf, StringFunction function, String value);

	static final void appendMatchClauseByLike(final Statement bf, final StringFunction function, final String value)
	{
		bf.append(function).
			append(" LIKE ").
			appendParameterAny(LikeCondition.WILDCARD + value + LikeCondition.WILDCARD);
	}

	String getBlobLength()
	{
		return "OCTET_LENGTH";
	}

	abstract void appendStartsWith(Statement bf, BlobColumn column, byte[] value);

	String getAveragePrefix()
	{
		return "AVG(";
	}

	String getAveragePostfix()
	{
		return ")";
	}

	/**
	 * Returns null, if the dialect does not support clauses for CharacterSet.
	 */
	String getClause(final String column, final CharSet set)
	{
		requireNonNull(column);
		requireNonNull(set);

		return null;
	}

	void append(
			final Statement statement,
			final StringFunction function,
			final Join join,
			final CharSet set)
	{
		throw new UnsupportedQueryException(
				"CharSetCondition not supported by " + getClass().getName());
	}

	abstract void deleteSchema(List<Table> tables, List<SequenceX> sequences, ConnectionPool connectionPool);
	abstract void deleteSequence(
			StringBuilder bf, String quotedName,
			long start);
	abstract Long    nextSequence(Executor executor, Connection connection, String quotedName);
	abstract Long getNextSequence(Executor executor, Connection connection, String name);

	/**
	 * @param ctx needed by subclasses
	 * @param database needed by subclasses
	 * @param connectionPool needed by subclasses
	 */
	void purgeSchema(
			final JobContext ctx,
			final Database database,
			final ConnectionPool connectionPool)
	{
		// empty default implementation
	}

	/**
	 * @param connectionPool used by subclasses
	 * @return never returns null
	 */
	String getSchemaSavepoint(final ConnectionPool connectionPool) throws SQLException
	{
		throw new SQLException("not supported");
	}
}

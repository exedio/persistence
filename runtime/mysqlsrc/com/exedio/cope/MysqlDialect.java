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

import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DayPartView.Part;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Sequence;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints,
 * and transactions.
 * @author Ralf Wiebicke
 */
@ServiceProperties(MysqlProperties.class)
final class MysqlDialect extends Dialect
{
	private static final Logger logger = LoggerFactory.getLogger(MysqlDialect.class);

	private final boolean utf8mb4;

	/**
	 * See https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html
	 */
	private final long maxBytesPerChar; // MUST be long to avoid overflow at multiply
	private final String charset;

	private final String deleteTable;
	private final boolean smallIntegerTypes;
	private final boolean shortConstraintNames;
	final String sequenceColumnName;
	private final boolean supportsAnyValue;
	private final boolean supportsNativeDate;
	private final boolean supportsGtid;
	private final Pattern extractUniqueViolationMessagePattern;
	private final int purgeSequenceLimit;

	MysqlDialect(
			final CopeProbe probe,
			final MysqlProperties properties)
	{
		super(
				new MysqlSchemaDialect(
						probe.environmentInfo.isDatabaseVersionAtLeast(5, 6), // supportsNativeDate
						sequenceColumnName(properties),
						properties.rowFormat.sql()));

		probe.environmentInfo.requireDatabaseVersionAtLeast(5, 5);

		this.utf8mb4 = properties.utf8mb4;
		this.maxBytesPerChar = utf8mb4 ? 4 : 3;
		final String mb4 = utf8mb4 ? "mb4" : "";
		this.charset = " CHARACTER SET utf8" + mb4 + " COLLATE utf8" + mb4 + "_bin";
		this.deleteTable = properties.avoidTruncate ? "DELETE FROM " : "TRUNCATE ";
		this.smallIntegerTypes = properties.smallIntegerTypes;
		this.shortConstraintNames = !properties.longConstraintNames;
		this.sequenceColumnName = sequenceColumnName(properties);

		final EnvironmentInfo env = probe.environmentInfo;
		if((!utf8mb4 || !smallIntegerTypes || shortConstraintNames) &&
			env.isDatabaseVersionAtLeast(5, 7))
			throw new IllegalArgumentException(
					"utf8mb4 (="+utf8mb4+"), " +
					"smallIntegerTypes (="+smallIntegerTypes+") and " +
					"longConstraintNames (="+(!shortConstraintNames)+") " +
					"must be enabled on MySQL 5.7 and later: " +
					env.getDatabaseVersionDescription());

		supportsAnyValue = env.isDatabaseVersionAtLeast(5, 7);
		supportsNativeDate = supportsGtid = env.isDatabaseVersionAtLeast(5, 6);
		final boolean mariaDriver = env.getDriverName().startsWith("MariaDB");
		extractUniqueViolationMessagePattern = mariaDriver ? Pattern.compile("^\\(conn=\\p{Digit}+\\) (.*)$") : null;
		purgeSequenceLimit = properties.purgeSequenceLimit;
	}

	private static String sequenceColumnName(final MysqlProperties properties)
	{
		return sequenceColumnName(properties.fullSequenceColumnName);
	}

	static String sequenceColumnName(final boolean full)
	{
		return
				full
				? "COPE_SEQUENCE_AUTO_INCREMENT_COLUMN"
				: "x";
	}

	@Override
	void completeConnectionInfo(final Properties info)
	{
		// https://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
		info.setProperty("useUnicode", "true");
		info.setProperty("characterEncoding", CHARSET);
		info.setProperty("characterSetResults", CHARSET);
		info.setProperty("sessionVariables", "sql_mode='" + SQL_MODE + "',innodb_strict_mode=1");
		info.setProperty("useLocalSessionState", TRUE);
		info.setProperty("allowMultiQueries", TRUE); // needed for deleteSchema

		// Without useSSL=false there is a warning on MySQL 5.7 after
		// upgrading mysql-connector from 5.1.15 to 5.1.45:
		// WARN: Establishing SSL connection without server's identity verification is not recommended.
		// According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be
		// established by default if explicit option isn't set. For compliance with existing
		// applications not using SSL the verifyServerCertificate property is set to 'false'.
		// You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true
		// and provide truststore for server certificate verification.
		requireConnectionInfo(info, "useSSL", "false");

		// Without serverTimezone there is an error on MySQL 5.6 after
		// upgrading mysql-connector from 5.1.45 to 8.0.13:
		// java.sql.SQLException:
		// The server time zone value 'CEST' is unrecognized or represents more than one time zone.
		// You must configure either the server or JDBC driver (via the serverTimezone configuration property)
		// to use a more specifc time zone value if you want to utilize time zone support.
		requireConnectionInfo(info, "serverTimezone", "UTC");

		// Do not allow LOAD DATA LOCAL as it has security issues.
		// https://dev.mysql.com/doc/refman/5.7/en/load-data-local.html
		// https://mariadb.com/kb/en/library/about-mariadb-connector-j/#load-data-infile
		requireConnectionInfo(info, "allowLoadLocalInfile", "false"); // MySQL driver
		requireConnectionInfo(info, "allowLocalInfile", "false"); // MariaDB driver

		//info.setProperty("profileSQL", TRUE);
	}

	@Override
	@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in sql
	void prepareDumperConnection(final Appendable out) throws IOException
	{
		out.append(
				"SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT;\n" +
				"SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS;\n" +
				"SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION;\n" +
				"SET NAMES " + CHARSET + ";\n" +

				"SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='" + SQL_MODE + "';\n" +

				"SET @OLD_TIME_ZONE=@@TIME_ZONE;\n"+
				"SET TIME_ZONE='+00:00';\n");
	}

	@Override
	@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in sql
	void unprepareDumperConnection(final Appendable out) throws IOException
	{
		out.append(
				"SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT;\n" +
				"SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS;\n" +
				"SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION;\n" +

				"SET SQL_MODE=@OLD_SQL_MODE;\n" +

				"SET TIME_ZONE=@OLD_TIME_ZONE;\n");
	}

	@Override
	void completeConnection(final Connection connection) throws SQLException
	{
		if(utf8mb4)
		{
			// for some reason, jdbc parameters cannot be set to utf8mb4
			try(java.sql.Statement st = connection.createStatement())
			{
				st.execute("SET NAMES utf8mb4 COLLATE utf8mb4_bin");
			}
		}
	}

	@Override
	void setNameTrimmers(final EnumMap<TrimClass, Trimmer> trimmers)
	{
		super.setNameTrimmers(trimmers);

		if(shortConstraintNames)
			trimmers.put(TrimClass.ForeignKeyUniqueConstraint, trimmers.get(TrimClass.Data));
	}

	private static final String CHARSET = "utf8";
	private static final String SQL_MODE =
			"STRICT_ALL_TABLES," +
			"NO_ZERO_DATE," +
			"NO_ZERO_IN_DATE," +
			"NO_ENGINE_SUBSTITUTION," +
			"NO_BACKSLASH_ESCAPES," +
			"ONLY_FULL_GROUP_BY";
	private static final String TRUE = "true";

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		// smallIntegerTypes does not save any space in experiments.
		// https://dev.mysql.com/doc/refman/5.5/en/storage-requirements.html
		if(smallIntegerTypes)
		{
			if(minimum>=Byte .MIN_VALUE && maximum<=Byte .MAX_VALUE) return "tinyint";
			if(minimum>=Short.MIN_VALUE && maximum<=Short.MAX_VALUE) return "smallint";
			if(minimum>=-8388608l       && maximum<=8388607l       ) return "mediumint";
		}
		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "int" : "bigint";
	}

	@Override
	String getDoubleType()
	{
		return "double";
	}

	/**
	 * Must be consistent with documentation of {@link MysqlExtendedVarchar}.
	 *
	 * Limits for datatypes are in bytes, but varchar parameter specifies
	 * characters.
	 *
	 * Always returns "binary" types make string comparisons and
	 * unique constraints case sensitive.
	 */
	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		// TODO implement maxBytes==maxChars for strings with character set us-ascii
		final long maxBytes = maxChars * maxBytesPerChar;

		// NOTE:
		// for selecting text types we can calculate with 3 bytes per character even for utf8mb4
		// as all Unicode code points encoded as 4 bytes in UTF-8 are represented by 2 characters
		// in java strings.
		final long maxBytes3 = maxChars * 3l;

		// TODO 255 (TWOPOW8) is needed for unique columns only,
		//      non-unique can have more,
		//      and for longer unique columns you may specify a shorter key length
		// TODO mysql 5.0.3 and later can have varchars up to 64k
		//      but the maximum row size of 64k may require using 'text' for strings less 64k
		// TODO use char instead of varchar, if minChars==maxChars and
		//      no spaces allowed (char drops trailing spaces)
		if(maxChars<=85 || // equivalent to maxBytes<TWOPOW8 for 3 maxBytesPerChar
			(maxBytes<(TWOPOW16-4) && mysqlExtendedVarchar!=null)) // minus 4 is for primary key column
			return "varchar("+maxChars+")" + charset;
		else if(maxBytes3<TWOPOW16)
			return "text" + charset;
		else if(maxBytes3<TWOPOW24)
			return "mediumtext" + charset;
		else
			return "longtext" + charset;
	}

	@Override
	String getDayType()
	{
		return "date";
	}

	@Override
	void appendDatePartExtraction(final DayPartView view, final Statement bf, final Join join)
	{
		if(Part.WEEK_OF_YEAR==view.getPart())
		{
			bf.append("WEEKOFYEAR(").
				append(view.getSource(), join).
				append(')');
		}
		else
		{
			super.appendDatePartExtraction(view, bf, join);
		}
	}

	@Override
	String getDateTimestampType()
	{
		// requires MySQL 5.6.4
		// cannot use timestamp as it supports year 1970-2038 only
		// https://dev.mysql.com/doc/refman/5.6/en/datetime.html
		// https://dev.mysql.com/doc/refman/5.6/en/fractional-seconds.html
		return
				supportsNativeDate
				? "datetime(3)" // 3 digits fractional seconds
				: null;
	}

	@Override
	String getDateTimestampPrecisionMinuteSecond(final boolean isSecond, final String quotedName)
	{
		return
				"EXTRACT(" + (isSecond?"MICROSECOND":"SECOND_MICROSECOND") + ' ' +
				"FROM " + quotedName + ")=0";
	}

	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		return "(" + quotedName + " MOD " + precision.divisor() + ")=0";
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		if(maximumLength<TWOPOW8)
			return "tinyblob";
		else if(maximumLength<TWOPOW16)
			return "blob";
		else if(maximumLength<TWOPOW24)
			return "mediumblob";
		else
			return "longblob";
	}

	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append("x'");
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	String[] getBlobHashAlgorithms()
	{
		return new String[]{"MD5", "SHA", "SHA-224", "SHA-256", "SHA-384", "SHA-512"};
	}

	@Override
	void appendBlobHash(
			final Statement bf, final BlobColumn column, final Join join,
			final String algorithm)
	{
		switch(algorithm)
		{
			case "MD5":     bf.append("MD5(" ).append(column, join).append(')'); break;
			case "SHA":     bf.append("SHA1(").append(column, join).append(')'); break;
			case "SHA-224": bf.append("SHA2(").append(column, join).append(",224)"); break;
			case "SHA-256": bf.append("SHA2(").append(column, join).append(",256)"); break;
			case "SHA-384": bf.append("SHA2(").append(column, join).append(",384)"); break;
			case "SHA-512": bf.append("SHA2(").append(column, join).append(",512)"); break;
			default:
				super.appendBlobHash(bf, column, join, algorithm);
		}
	}

	@Override
	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final Function<E> dividend,
			final Function<E> divisor,
			final Join join)
	{
		bf.append(dividend, join).
			append(" DIV ").
			append(divisor, join);
	}

	@Override
	void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		// Do nothing, as MySQL default behaviour defines behaviour of cope.
		// All other dialects have to adapt.
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

		bf.append(" LIMIT ");

		if(offset>0)
			bf.appendParameter(offset).append(',');

		// using MAX_VALUE is really the recommended usage, see MySQL doc.
		final int countInStatement = limit!=Query.UNLIMITED ? limit : Integer.MAX_VALUE;
		bf.appendParameter(countInStatement);
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
			append(",CHAR)");
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(MATCH(").
			append(function).
			append(")AGAINST(").
			appendParameterAny(value).
			append("))");
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("HEX(SUBSTRING(").
			append(column).
			append(",1,").
			appendParameter(value.length).
			append("))=").
			appendParameter(Hex.encodeUpper(value));
	}

	@Override
	String getClause(final String column, final CharSet set)
	{
		if(set.isSubsetOfAscii())
		{
			return
					column + " REGEXP " +
					StringColumn.cacheToDatabaseStatic(set.getRegularExpression());
		}
		else
		{
			final String re = set.getRegularExpressionForInvalid7BitChars();
			if (re==null)
			{
				return super.getClause(column, set);
			}
			else
			{
				return
						column + " NOT REGEXP " +
						StringColumn.cacheToDatabaseStatic(set.getRegularExpressionForInvalid7BitChars());
			}
		}
	}

	@Override
	void append(
			final Statement statement,
			final StringFunction function,
			final Join join,
			final CharSet set)
	{
		if(!set.isSubsetOfAscii())
			throw new IllegalStateException("not supported: CharSetCondition on MySQL with non-ASCII CharSet: " + set);

		statement.
			append(function, join).
			append(" REGEXP ").
			appendParameter(set.getRegularExpression());
	}

	@Override
	QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor executor)
	{
		final String statementText = statement.getText();
		if(statementText.startsWith("ALTER TABLE "))
			return null;

		final QueryInfo root = new QueryInfo(EXPLAIN_PLAN);
		{
			final Statement bf = executor.newStatement();
			bf.append("EXPLAIN ").
				append(statementText).
				appendParameters(statement);

			executor.query(connection, bf, null, true, resultSet ->
				{
					final ResultSetMetaData metaData = resultSet.getMetaData();
					final int columnCount = metaData.getColumnCount();

					while(resultSet.next())
					{
						final StringBuilder qi = new StringBuilder();

						for(int i = 1; i<=columnCount; i++)
						{
							final Object value = resultSet.getObject(i);
							if(value!=null)
							{
								if(qi.length()>0)
									qi.append(", ");

								qi.append(metaData.getColumnName(i)).
									append('=').
									append(value);
							}
						}
						root.addChild(new QueryInfo(qi.toString()));
					}
					return null;
				}
			);
		}

		return root;
	}

	@Override
	void deleteSequence(
			final StringBuilder bf, final String quotedName,
			final Sequence.Type type, final long start)
	{
		bf.append("TRUNCATE ").
			append(quotedName);

		MysqlSchemaDialect.initializeSequence(bf, quotedName, start);

		bf.append(';');
	}

	@Override
	Long nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("INSERT INTO ").
			append(quotedName).
			append("()VALUES()");

		return executor.insertAndGetGeneratedKeys(connection, bf, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + quotedName);
				return result - 1;
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
		bf.append("SELECT MAX(").
			append(dsmfDialect.quoteName(sequenceColumnName)).
			append(") FROM ").
			append(dsmfDialect.quoteName(name));

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);

				// converts null into long 0
				return resultSet.getLong(1);
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

		if(!tables.isEmpty())
		{
			bf.append("SET FOREIGN_KEY_CHECKS=0;");

			for(final Table table : tables)
			{
				bf.append(deleteTable).
					append(table.quotedID).
					append(';');
			}

			bf.append("SET FOREIGN_KEY_CHECKS=1;");
		}

		for(final SequenceX sequence : sequences)
			sequence.delete(bf, this);

		if(bf.length()>0)
			execute(connectionPool, bf.toString());
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static void execute(final ConnectionPool connectionPool, final String sql)
	{
		@SuppressWarnings("resource") // OK: must not put into connection pool on failure
		Connection connection = null;
		try
		{
			connection = connectionPool.get(true);
			execute(connection, sql);

			// NOTE:
			// until mysql connector 5.0.4 putting connection back into the pool
			// causes exception later:
			// java.sql.SQLException: ResultSet is from UPDATE. No Data.
			connectionPool.put(connection);
			connection = null;
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, sql);
		}
		finally
		{
			if(connection!=null)
			{
				try
				{
					// do not put it into connection pool again
					// because foreign key constraints could be disabled
					connection.close();
				}
				catch(final SQLException ignored)
				{
					// exception is already thrown
				}
			}
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	private static void execute(final Connection connection, final String sql) throws SQLException
	{
		try(java.sql.Statement sqlStatement = connection.createStatement())
		{
			sqlStatement.executeUpdate(sql);
		}
	}

	@Override
	boolean supportsUTF8mb4()
	{
		return utf8mb4;
	}

	@Override
	boolean supportsRandom()
	{
		return true;
	}

	@Override
	boolean subqueryRequiresAlias()
	{
		return true;
	}

	@Override
	boolean subqueryRequiresAliasInSelect()
	{
		return true;
	}

	@Override
	boolean supportsUniqueViolation()
	{
		return true;
	}

	private static final String UNIQUE_PREFIX = "Duplicate entry '";
	private static final String UNIQUE_INFIX  = "' for key '";
	private static final int UNIQUE_PREFIX_LENGTH = UNIQUE_PREFIX.length();
	private static final int UNIQUE_INFIX_LENGTH  = UNIQUE_INFIX.length();

	@Override
	String extractUniqueViolation(final SQLException exception)
	{
		if(!(exception instanceof SQLIntegrityConstraintViolationException))
			return null;

		String message = exception.getMessage();

		if(extractUniqueViolationMessagePattern!=null)
		{
			final Matcher matcher =
					extractUniqueViolationMessagePattern.matcher(message);
			if(matcher.matches())
				message = matcher.group(1);
		}

		if(message==null || !message.startsWith(UNIQUE_PREFIX))
			return null;
		final int infixPosition = message.indexOf(UNIQUE_INFIX, UNIQUE_PREFIX_LENGTH);
		if(infixPosition<0)
			return null;
		final int infixEnd = infixPosition + UNIQUE_INFIX_LENGTH;
		final int postfixPosition = message.indexOf('\'', infixEnd);
		if(postfixPosition<0)
			return null;

		return message.substring(infixEnd, postfixPosition);
	}

	@Override
	boolean supportsAnyValue()
	{
		return supportsAnyValue;
	}

	@Override
	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	void purgeSchema(
			final JobContext ctx,
			final Database database,
			final ConnectionPool connectionPool)
	{
		final ArrayList<String> names = database.getSequenceSchemaNames();
		if(names.isEmpty())
			return;

		final Connection connection = connectionPool.get(true);
		try
		{
			final String column = dsmfDialect.quoteName(sequenceColumnName);
			for(final String name : names)
			{
				final String table = dsmfDialect.quoteName(name);

				if(ctx.supportsMessage())
					ctx.setMessage("sequence " + name + " query");
				deferOrStopIfRequested(ctx);

				final Long maxObject = Executor.query(
						connection,
						"SELECT MAX(" + column + ") FROM " + table,
						resultSet ->
					{
						if(!resultSet.next())
							throw new RuntimeException("empty in sequence " + name);
						final long result = resultSet.getLong(1);
						if(resultSet.wasNull())
							return null;
						return result;
					}
				);
				if(maxObject==null)
					continue;

				final long max = maxObject; // unbox

				do
				{
					if(ctx.supportsMessage())
						ctx.setMessage("sequence " + name + " purge less " + max + " limit " + purgeSequenceLimit);
					deferOrStopIfRequested(ctx);

					final int rows = Executor.update(
							connection,
							"DELETE FROM " + table + " WHERE " + column + " < " + max + " LIMIT " + purgeSequenceLimit);
					ctx.incrementProgress(rows);

					if(rows>0 && logger.isInfoEnabled())
						logger.info("sequence {} purge less {} rows {}", new Object[]{name, max, rows});

					if(rows<purgeSequenceLimit)
						break;
				}
				while(true);
			}
		}
		finally
		{
			connectionPool.put(connection);
		}
	}

	@Override
	String getSchemaSavepoint(final ConnectionPool connectionPool) throws SQLException
	{
		final Connection connection = connectionPool.get(true);
		final String sql = "SHOW MASTER STATUS";
		try(
			java.sql.Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql))
		{
			if(!rs.next())
				throw new SQLException(sql + " returns empty result, probably because binlog is disabled");

			final StringBuilder bf = new StringBuilder(sql);
			boolean first = true;
			do
			{
				if(first)
					first = false;
				else
					bf.append(" newLine");

				if(supportsGtid)
				{
					// https://dev.mysql.com/doc/refman/5.7/en/replication-gtids.html
					final String s = rs.getString("Executed_Gtid_Set");
					if(s!=null && !s.isEmpty())
						bf.append(" Gtid=").append(s);
				}

				bf.append(' ').append(rs.getString("File")).
					append(':').append(rs.getInt   ("Position"));
				{
					final String s = rs.getString("Binlog_Do_DB");
					if(s!=null && !s.isEmpty())
						bf.append(" doDB=").append(s);
				}
				{
					final String s = rs.getString("Binlog_Ignore_DB");
					if(s!=null && !s.isEmpty())
						bf.append(" ignoreDB=").append(s);
				}
			}
			while(rs.next());

			return bf.toString();
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}

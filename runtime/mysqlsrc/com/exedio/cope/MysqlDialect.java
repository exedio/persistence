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
@DialectProbeInfo({

		// Without allowPublicKeyRetrieval one gets a
		// java.sql.SQLNonTransientConnectionException: Public Key Retrieval is not allowed
		// on MySQL 8.0 with authentication via caching_sha2_password instead of mysql_native_password.
		"allowPublicKeyRetrieval", "true",

		// Without useSSL=false there is a warning on MySQL 5.7 after
		// upgrading mysql-connector from 5.1.15 to 5.1.45:
		// WARN: Establishing SSL connection without server's identity verification is not recommended.
		// According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be
		// established by default if explicit option isn't set. For compliance with existing
		// applications not using SSL the verifyServerCertificate property is set to 'false'.
		// You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true
		// and provide truststore for server certificate verification.
		// Later, useSSL has been converted into the more modern sslMode.
		// https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
		"sslMode", "DISABLED",

		// Without serverTimezone there is an error on MySQL 5.6 after
		// upgrading mysql-connector from 5.1.45 to 8.0.13:
		// java.sql.SQLException:
		// The server time zone value 'CEST' is unrecognized or represents more than one time zone.
		// You must configure either the server or JDBC driver (via the serverTimezone configuration property)
		// to use a more specifc time zone value if you want to utilize time zone support.
		"serverTimezone", "UTC",

		// Do not allow LOAD DATA LOCAL as it has security issues.
		// https://dev.mysql.com/doc/refman/5.7/en/load-data-local.html
		// https://mariadb.com/kb/en/library/about-mariadb-connector-j/#load-data-infile
		"allowLoadLocalInfile", "false", // MySQL driver
		"allowLocalInfile", "false"}) // MariaDB driver
final class MysqlDialect extends Dialect
{
	private static final Logger logger = LoggerFactory.getLogger(MysqlDialect.class);

	private final String timeZoneStatement;
	private final boolean connectionCompress;
	private final boolean setStrictMode;
	private final boolean utf8mb4;

	/**
	 * See https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html
	 */
	private final long maxBytesPerChar; // MUST be long to avoid overflow at multiply
	private final String charset;

	private final String deleteTable;
	private final boolean smallIntegerTypes;
	private final boolean shortConstraintNames;
	private final boolean supportsAnyValue;
	private final int purgeSequenceLimit;
	private final boolean regexpICU;
	private final Pattern extractUniqueViolationMessagePattern;

	MysqlDialect(
			final CopeProbe probe,
			final MysqlProperties properties)
	{
		super(
				new MysqlSchemaDialect(
						probe.environmentInfo.isDatabaseVersionAtLeast(8, 0), // foreignKeyRule
						properties.rowFormat.sql()));

		final EnvironmentInfo env = probe.environmentInfo;
		env.requireDatabaseVersionAtLeast("MySQL", 5, 6);

		final boolean mysql8 = env.isDatabaseVersionAtLeast(8, 0);

		this.timeZoneStatement = properties.timeZoneStatement();
		this.connectionCompress = properties.connectionCompress;
		this.setStrictMode = !mysql8;
		this.utf8mb4 = properties.utf8mb4;
		this.maxBytesPerChar = utf8mb4 ? 4 : 3;
		final String mb4 = utf8mb4 ? "mb4" : "";
		this.charset = " CHARACTER SET utf8" + mb4 + " COLLATE utf8" + mb4 + "_bin";
		this.deleteTable = properties.avoidTruncate ? "DELETE FROM " : "TRUNCATE ";
		this.smallIntegerTypes = properties.smallIntegerTypes;
		this.shortConstraintNames = !properties.longConstraintNames;

		if(connectionCompress && !env.isDatabaseVersionAtLeast(5, 7))
			throw new IllegalArgumentException(
					"connection.compress is supported on MySQL 5.7 and later only: " +
					env.getDatabaseVersionDescription());
		if((!utf8mb4 || !smallIntegerTypes || shortConstraintNames) &&
			env.isDatabaseVersionAtLeast(5, 7))
			throw new IllegalArgumentException(
					"utf8mb4 (="+utf8mb4+"), " +
					"smallIntegerTypes (="+smallIntegerTypes+") and " +
					"longConstraintNames (="+(!shortConstraintNames)+") " +
					"must be enabled on MySQL 5.7 and later: " +
					env.getDatabaseVersionDescription());

		supportsAnyValue = env.isDatabaseVersionAtLeast(5, 7);
		purgeSequenceLimit = properties.purgeSequenceLimit;

		// Starting with MySQL 8.0.4 regular expression support uses a library called
		// "International Components for Unicode (ICU)"
		// https://dev.mysql.com/doc/refman/8.0/en/regexp.html
		regexpICU = mysql8;

		extractUniqueViolationMessagePattern = EXTRACT_UNIQUE_VIOLATION_MESSAGE_PATTERN(mysql8);
		assertDriverVersion(env);
	}

	static void assertDriverVersion(final EnvironmentInfo env)
	{
		if("MariaDB connector/J".equals(env.getDriverName()))
			return;

		final String PREFIX = "driver version must be between 8.0.21 and 8.0.27, but was ";
		if(!env.isDriverVersionAtLeast(8, 0) ||
			env.isDriverVersionAtLeast(8, 1))
			throw new IllegalArgumentException(
					PREFIX + "forbidden minor level: " + env.getDriverVersionDescription());

		final String dv = env.getDriverVersion();
		final Matcher matcher = Pattern.
				compile("^mysql-connector-java-8\\.0\\.([0-9]*) \\(Revision: [0-9a-f]*\\)$").
				matcher(dv);
		if(!matcher.matches())
			throw new IllegalArgumentException(
					PREFIX + "illegal pattern: " + dv);

		final String patchLevelString = matcher.group(1);
		final int patchLevel;
		try
		{
			patchLevel = Integer.parseInt(patchLevelString);
		}
		catch(final NumberFormatException e)
		{
			throw new IllegalArgumentException(
					PREFIX + "illegal integer: " + patchLevelString, e);
		}
		if( patchLevel<21 || patchLevel>27 )
			throw new IllegalArgumentException(
					PREFIX + "forbidden patch level " + patchLevel + " in version: " + dv);
	}

	static final String sequenceColumnName = "COPE_SEQUENCE_AUTO_INCREMENT_COLUMN";

	@Override
	void completeConnectionInfo(final Properties info)
	{
		// https://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
		info.setProperty("useUnicode", "true");
		info.setProperty("characterEncoding", CHARSET);
		info.setProperty("characterSetResults", CHARSET);
		// Setting innodb_strict_mode causes failure starting with MySQL 8.0.26:
		//    Access denied; you need (at least one of) the SYSTEM_VARIABLES_ADMIN or
		//    SESSION_VARIABLES_ADMIN privilege(s) for this operation
		// https://dev.mysql.com/doc/relnotes/mysql/8.0/en/news-8-0-26.html#mysqld-8-0-26-server-admin
		info.setProperty("sessionVariables", "sql_mode='" + SQL_MODE + "'" + (setStrictMode?(","+STRICT_MODE+"=1"):""));
		info.setProperty("useLocalSessionState", TRUE);
		info.setProperty("allowMultiQueries", TRUE); // needed for deleteSchema
		if(connectionCompress)
			info.setProperty("useCompression", TRUE);
		//info.setProperty("profileSQL", TRUE);
	}

	private static final String STRICT_MODE = "innodb_strict_mode";

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
		try(java.sql.Statement st = connection.createStatement())
		{
			if(!setStrictMode)
			{
				try(ResultSet rs = st.executeQuery(
						"SHOW VARIABLES WHERE `Variable_name`='" + STRICT_MODE + "'"))
				{
					if(!rs.next())
						throw new IllegalStateException("variable " + STRICT_MODE + " not found");

					final String value = rs.getString(2);
					if(!"ON".equals(value))
						throw new IllegalStateException("variable " + STRICT_MODE + " must be ON, but was >" + value + '<');
				}
			}

			if(timeZoneStatement!=null)
				st.execute(timeZoneStatement);

			// for some reason, jdbc parameters cannot be set to utf8mb4
			if(utf8mb4)
				st.execute("SET NAMES utf8mb4 COLLATE utf8mb4_bin");
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
		return "datetime(3)"; // 3 digits fractional seconds
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
		return new String[]{HASH_MD5, HASH_SHA, HASH_SHA224, HASH_SHA256, HASH_SHA384, HASH_SHA512};
	}

	@Override
	void appendBlobHash(
			final Statement bf, final BlobColumn column, final Join join,
			final String algorithm)
	{
		switch(algorithm)
		{
			case HASH_MD5:    bf.append("MD5(" ).append(column, join).append(')'); break;
			case HASH_SHA:    bf.append("SHA1(").append(column, join).append(')'); break;
			case HASH_SHA224: bf.append("SHA2(").append(column, join).append(",224)"); break;
			case HASH_SHA256: bf.append("SHA2(").append(column, join).append(",256)"); break;
			case HASH_SHA384: bf.append("SHA2(").append(column, join).append(",384)"); break;
			case HASH_SHA512: bf.append("SHA2(").append(column, join).append(",512)"); break;
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
	void appendPageClauseAfter(final Statement bf, final int offset, final int limit)
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
	String getExistsPrefix()
	{
		return "SELECT EXISTS ( ";
	}

	@Override
	String getExistsPostfix()
	{
		return " )";
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
		if(regexpICU || set.isSubsetOfAscii())
		{
			return
					column + " REGEXP " +
					StringColumn.cacheToDatabaseStatic(
							regexpICU
							? ICU.getRegularExpression(set)
							: set.getRegularExpression());
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
						StringColumn.cacheToDatabaseStatic(re);
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
		if(!regexpICU && !set.isSubsetOfAscii())
			throw new UnsupportedQueryException(
					"CharSetCondition not supported by " + getClass().getName() + " " +
					"with non-ASCII CharSet: " + set);

		statement.
			append(function, join).
			append(" REGEXP ").
			// CAST is needed because beginning with MySQL 8.0.22 this expression fails with:
			// Character set 'utf8mb4_bin' cannot be used in conjunction with 'binary' in call to regexp_like.
			// More info here: https://bugs.mysql.com/bug.php?id=104387
			append(regexpICU?"CAST(":"").
			appendParameter(
					regexpICU
					? ICU.getRegularExpression(set)
					: set.getRegularExpression()).
			append(regexpICU?" AS CHAR)":"");
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
			final long start)
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

	private static void execute(final ConnectionPool connectionPool, final String sql)
	{
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

	static Pattern EXTRACT_UNIQUE_VIOLATION_MESSAGE_PATTERN(final boolean mysql8)
	{
		return Pattern.compile(
			"^" +
			"(?:\\(conn[=:]\\p{Digit}+\\) )?" + // is generated by mariadb jdbc driver, colon instead of equal sign since version 2.4.2
			"Duplicate entry '.*' for key '" + (mysql8 ? ".*\\." : "") + "(.*)'" + // MySQL 8.0 prepends table name to constraint id of unique violation
			"$");
	}

	@Override
	boolean supportsAnyValue()
	{
		return supportsAnyValue;
	}

	@Override
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
	String getSchemaSavepoint(final ConnectionPool connectionPool) throws SchemaSavepointNotAvailableException, SQLException
	{
		final Connection connection = connectionPool.get(true);
		final String sql = "SHOW MASTER STATUS";
		try(
			java.sql.Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(sql))
		{
			if(!rs.next())
				throw new SchemaSavepointNotAvailableException(sql + " returns empty result, probably because binlog is disabled");

			final StringBuilder bf = new StringBuilder(sql);
			boolean first = true;
			do
			{
				if(first)
					first = false;
				else
					bf.append(" newLine");

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
		catch(final SQLException e)
		{
			final String message = e.getMessage();
			if(message.contains("REPLICATION CLIENT privilege"))
				throw new SchemaSavepointNotAvailableException(
						"Access denied; you need the REPLICATION CLIENT privilege for this operation", e);
			else
				throw e;
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}

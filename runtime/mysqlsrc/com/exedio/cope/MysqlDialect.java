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
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContext;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints,
 * and transactions.
 * @author Ralf Wiebicke
 */
final class MysqlDialect extends Dialect
{
	private static final Logger logger = LoggerFactory.getLogger(MysqlDialect.class);

	private final String deleteTable;

	MysqlDialect(final DialectParameters parameters)
	{
		super(
				new com.exedio.dsmf.MysqlDialect(
						parameters.properties.mysqlRowFormat.sql));
		this.deleteTable = parameters.properties.mysqlAvoidTruncate ? "delete from " : "truncate ";
	}

	@Override
	void completeConnectionInfo(final Properties info)
	{
		// http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
		info.setProperty("useUnicode", "true");
		info.setProperty("characterEncoding", CHARSET);
		info.setProperty("characterSetResults", CHARSET);
		info.setProperty("sessionVariables", "sql_mode='" + SQL_MODE + "',innodb_strict_mode=1");
		info.setProperty("useLocalSessionState", TRUE);
		info.setProperty("allowMultiQueries", TRUE); // needed for deleteSchema
		//info.setProperty("profileSQL", TRUE);
	}

	@Override
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
	void unprepareDumperConnection(final Appendable out) throws IOException
	{
		out.append(
				"SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT;\n" +
				"SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS;\n" +
				"SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION;\n" +

				"SET SQL_MODE=@OLD_SQL_MODE;\n" +

				"SET TIME_ZONE=@OLD_TIME_ZONE;\n");
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
		// TODO
		// We may want to support TINYINT, SMALLINT, and MEDIUMINT
		// for saving space, but I could not find any saving in experiments.
		// https://dev.mysql.com/doc/refman/5.5/en/storage-requirements.html

		return (minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? "int" : "bigint";
	}

	@Override
	String getDoubleType()
	{
		return "double";
	}

	/**
	 * Limits for datatypes are in bytes, but varchar parameter specifies
	 * characters.
	 *
	 * Always returns "binary" types make string comparisions and
	 * unique constraints case sensitive.
	 */
	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		// TODO implement maxBytes==maxChars for strings with character set us-ascii
		final int maxBytes = maxChars * MAX_BYTES_PER_CHARACTER_UTF8;

		// TODO 255 (TWOPOW8) is needed for unique columns only,
		//      non-unique can have more,
		//      and for longer unique columns you may specify a shorter key length
		// TODO mysql 5.0.3 and later can have varchars up to 64k
		//      but the maximum row size of 64k may require using 'text' for strings less 64k
		// TODO use char instead of varchar, if minChars==maxChars and
		//      no spaces allowed (char drops trailing spaces)
		final String charset = " CHARACTER SET utf8 COLLATE utf8_bin";
		if(maxBytes<TWOPOW8 ||
			(maxBytes<TWOPOW16 && mysqlExtendedVarchar!=null))
			return "varchar("+maxChars+")" + charset;
		else if(maxBytes<TWOPOW16)
			return "text" + charset;
		else if(maxBytes<TWOPOW24)
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
	String getDateTimestampType()
	{
		// TODO
		// would require type "timestamp(14,3) null default null"
		// but (14,3) is not yet supported
		// "null default null" is needed to allow null and
		// make null the default value
		// This works with 4.1.6 and higher only
		return null;
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

		bf.append(" LIMIT ");

		if(offset>0)
			bf.appendParameter(offset).append(',');

		// using MAX_VALUE is really the recommended usage, see MySQL doc.
		final int countInStatement = limit!=Query.UNLIMITED ? limit : Integer.MAX_VALUE;
		bf.appendParameter(countInStatement);
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
			append(",CHAR)");
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(MATCH(").
			append(function, (Join)null).
			append(")AGAINST(").
			appendParameterAny(value).
			append("))");
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("HEX(SUBSTRING(").
			append(column, (Join)null).
			append(",1,").
			appendParameter(value.length).
			append("))=").
			appendParameter(Hex.encodeUpper(value));
	}

	@Override
	String getClause(final String column, final CharSet set)
	{
		if(!set.isSubsetOfAscii())
			return super.getClause(column, set);

		final StringBuilder bf = new StringBuilder();
		bf.append(column).
			append(" REGEXP '").
			append(set.getRegularExpression()).
			append('\'');
		return bf.toString();
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
			append(function, (Join)null).
			append(" REGEXP '").
			append(set.getRegularExpression()).
			append('\'');
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

			executor.query(connection, bf, null, true, new ResultSetHandler<Void>()
			{
				public Void handle(final ResultSet resultSet) throws SQLException
				{
					final ResultSetMetaData metaData = resultSet.getMetaData();
					final int columnCount = metaData.getColumnCount();

					while(resultSet.next())
					{
						final StringBuilder bf = new StringBuilder();

						for(int i = 1; i<=columnCount; i++)
						{
							final Object value = resultSet.getObject(i);
							if(value!=null)
							{
								if(bf.length()>0)
									bf.append(", ");

								bf.append(metaData.getColumnName(i)).
									append('=').
									append(value.toString());
							}
						}
						root.addChild(new QueryInfo(bf.toString()));
					}
					return null;
				}
			});
		}

		return root;
	}

	@Override
	void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append("TRUNCATE ").
			append(quotedName);

		com.exedio.dsmf.MysqlDialect.initializeSequence(bf, quotedName, startWith);

		bf.append(';');
	}

	@Override
	Integer nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("INSERT INTO ").
			append(quotedName).
			append("()VALUES()");

		final long result = executor.insertAndGetGeneratedKeys(connection, bf, new ResultSetHandler<Long>()
		{
			public Long handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + quotedName);
				return (Long)o;
			}
		}).longValue() - 1;

		if(result>Integer.MAX_VALUE || result<Integer.MIN_VALUE)
			throw new RuntimeException(quotedName + '/' + result);
		return (int)result;
	}

	@Override
	Integer getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT MAX(").
			append(dsmfDialect.quoteName(com.exedio.dsmf.MysqlDialect.SEQUENCE_COLUMN)).
			append(") FROM ").
			append(dsmfDialect.quoteName(name));

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);

				// converts null into integer 0
				return resultSet.getInt(1);
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
				catch(final SQLException e)
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
	boolean supportsNotNull()
	{
		return true;
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
		if(!(
				exception instanceof com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException ||
				exception instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException))
			return null;

		final String message = exception.getMessage();
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
			final String column = dsmfDialect.quoteName(com.exedio.dsmf.MysqlDialect.SEQUENCE_COLUMN);
			for(final String name : names)
			{
				final String table = dsmfDialect.quoteName(name);

				if(ctx.supportsMessage())
					ctx.setMessage("sequence " + name + " query");
				ctx.stopIfRequested();

				final Integer maxObject = Executor.query(
						connection,
						"SELECT MAX(" + column + ") FROM " + table,
				new ResultSetHandler<Integer>()
				{
					public Integer handle(final ResultSet resultSet) throws SQLException
					{
						if(!resultSet.next())
							throw new RuntimeException("empty in sequence " + name);
						final Object o = resultSet.getObject(1);
						if(o==null)
							return null;
						return (Integer)o;
					}
				});
				if(maxObject==null)
					continue;

				final int max = maxObject.intValue();

				if(ctx.supportsMessage())
					ctx.setMessage("sequence " + name + " purge less " + max);
				ctx.stopIfRequested();

				final int rows = Executor.update(
						connection,
						"DELETE FROM " + table + " WHERE " + column + " < " + max);
				ctx.incrementProgress(rows);

				if(rows>0 && logger.isInfoEnabled())
					logger.info("sequence {} purge less {} rows {}", new Object[]{name, max, rows});
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

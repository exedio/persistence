/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.util.CharSet;
import com.mysql.jdbc.Driver;

/**
 * This MySQL driver requires the InnoDB engine.
 * It makes no sense supporting older engines,
 * since cope heavily depends on foreign key constraints,
 * and transactions.
 * @author Ralf Wiebicke
 */
final class MysqlDialect extends Dialect
{
	static
	{
		try
		{
			Class.forName(Driver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * mysql supports placeholders in version 5.0.7 and higher
	 */
	private final boolean placeholdersInLimit;

	protected MysqlDialect(final DialectParameters parameters)
	{
		super(
				new com.exedio.dsmf.MysqlDialect(
						Table.PK_COLUMN_NAME),
				"CHAR_LENGTH");
		this.placeholdersInLimit = parameters.databaseMajorVersion>=5;
	}

	@Override
	protected void completeConnectionInfo(final java.util.Properties info)
	{
		info.setProperty("useUnicode", "true");
		info.setProperty("characterEncoding", "utf8");
		info.setProperty("characterSetResults", "utf8");
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

	/**
	 * Limits for datatypes are in bytes, but varchar parameter specifies
	 * characters.
	 *
	 * Always returns "binary" types make string comparisions and
	 * unique constraints case sensitive.
	 */
	@Override
	String getStringType(final int maxChars)
	{
		// TODO implement maxBytes==maxChars for strings with character set us-ascii
		final int maxBytes = maxChars * MAX_BYTES_PER_CHARACTER_UTF8;

		assert TWOPOW8==256;
		assert TWOPOW16==65536;

		// TODO 255 (TWOPOW8) is needed for unique columns only,
		//      non-unique can have more,
		//      and for longer unique columns you may specify a shorter key length
		// TODO mysql 5.0.3 and later can have varchars up to 64k
		//      but the maximum row size of 64k may require using 'text' for strings less 64k
		// TODO use char instead of varchar, if minChars==maxChars and
		//      no spaces allowed (char drops trailing spaces)
		if(maxBytes<TWOPOW8)
			return "varchar("+maxChars+") character set utf8 binary";
		else if(maxBytes<TWOPOW16)
			return "text character set utf8 binary";
		else if(maxBytes<TWOPOW24)
			return "mediumtext character set utf8 binary";
		else
			return "longtext character set utf8 binary";
	}
	
	@Override
	String getDayType()
	{
		return "DATE";
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
			return "TINYBLOB";
		else if(maximumLength<TWOPOW16)
			return "BLOB";
		else if(maximumLength<TWOPOW24)
			return "MEDIUMBLOB";
		else
			return "LONGBLOB";
	}
	
	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append('x');
		super.addBlobInStatementText(statementText, parameter);
	}
	
	@Override
	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final NumberFunction<E> dividend,
			final NumberFunction<E> divisor,
			final Join join)
	{
		bf.append(dividend, join).
			append(" DIV ").
			append(divisor, join);
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
		
		bf.append(" limit ");

		if(offset>0)
		{
			if(placeholdersInLimit)
				bf.appendParameter(offset).append(',');
			else
				bf.append(Integer.toString(offset)).append(',');
		}

		// using MAX_VALUE is really the recommended usage, see MySQL doc.
		final int countInStatement = limit!=Query.UNLIMITED ? limit : Integer.MAX_VALUE;

		if(placeholdersInLimit)
			bf.appendParameter(countInStatement);
		else
			bf.append(Integer.toString(countInStatement));
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException(bf.toString());
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(match(").
			append(function, (Join)null).
			append(")against(").
			appendParameter(function, value).
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
			appendParameter(hexUpper(value));
	}
	
	@Override
	protected String getClause(final String column, final CharSet set)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(column).
			append(" regexp '").
			append(set.getRegularExpression()).
			append('\'');
		return bf.toString();
	}
	
	@Override
	protected QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor database)
	{
		final String statementText = statement.getText();
		if(statementText.startsWith("alter table "))
			return null;
		
		final QueryInfo root = new QueryInfo(EXPLAIN_PLAN);
		{
			final Statement bf = database.createStatement();
			bf.append("explain ").
				append(statementText).
				appendParameters(statement);

			database.executeSQLQuery(connection, bf, null, true, new ResultSetHandler<Void>()
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
	protected Integer nextSequence(
			final Executor database,
			final Connection connection,
			final String name)
	{
		final Statement bf = database.createStatement();
		bf.append("INSERT INTO ").
			append(dsmfDialect.quoteName(name)).
			append(" () VALUES ()");
		
		return (int)(database.executeSQLInsert(connection, bf, new ResultSetHandler<Long>()
		{
			public Long handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return (Long)o;
			}
		}).longValue() - 1);
	}
	
	@Override
	protected Integer getNextSequence(
			final Executor database,
			final Connection connection,
			final String name)
	{
		final Statement bf = database.createStatement();
		bf.append("SELECT MAX(").
			append(dsmfDialect.quoteName(com.exedio.dsmf.MysqlDialect.SEQUENCE_COLUMN)).
			append(") FROM ").
			append(dsmfDialect.quoteName(name));
		
		return database.executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((Integer)o);
			}
		});
	}
	
	@Override
	boolean subqueryRequiresAlias()
	{
		return true;
	}
}

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
import java.sql.SQLException;

import org.hsqldb.jdbcDriver;

import com.exedio.cope.Database.ResultSetHandler;
import com.exedio.dsmf.SQLRuntimeException;

final class HsqldbDialect extends Dialect
{
	static
	{
		try
		{
			Class.forName(jdbcDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param parameters must be there to be called by reflection
	 */
	protected HsqldbDialect(final DialectParameters parameters)
	{
		super(
				new com.exedio.dsmf.HsqldbDialect(),
				"LENGTH");
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

	@Override
	String getBlobType(final long maximumLength)
	{
		return "binary";
	}
	
	@Override
	int getBlobLengthFactor()
	{
		return 2;
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSE_AFTER_SELECT;
	}

	@Override
	void appendLimitClause(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		bf.append(" limit ").
			appendParameter(offset).
			append(' ').
			appendParameter(limit!=Query.UNLIMITED ? limit : 0);
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		throw new RuntimeException(bf.toString());
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		appendMatchClauseByLike(bf, function, value);
	}
	
	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("substring(").
			append(column, (Join)null).
			append(",0,").
			appendParameter(2*value.length).
			append(")=").
			appendParameter(hexLower(value));
	}
	
	@Override
	boolean fakesSupportReadCommitted()
	{
		return true;
	}
	
	@Override
	protected Integer nextSequence(
			final Database database,
			final Connection connection,
			final String name)
	{
		final String TEMP_TABLE = dsmfDialect.quoteName("hsqldb_temp_table_for_sequences");
		try
		{
			connection.setAutoCommit(false);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "setAutoCommit");
		}
		{
			final Statement bf = database.createStatement();
			bf.append("CREATE TEMPORARY TABLE ").
				append(TEMP_TABLE).
				append(" (x integer)");
			database.executeSQLUpdate(connection, bf, false);
		}
		{
			final Statement bf = database.createStatement();
			bf.append("INSERT INTO ").
				append(TEMP_TABLE).
				append(" VALUES (0)");
			database.executeSQLUpdate(connection, bf, true);
		}
		final Integer result;
		{
			final Statement bf = database.createStatement();
			bf.append("SELECT NEXT VALUE FOR ").
				append(dsmfDialect.quoteName(name)).
				append(" FROM ").
				append(TEMP_TABLE);
				
			result = database.executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Integer>()
			{
				public Integer handle(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new RuntimeException("empty in sequence " + name);
					final Object o = resultSet.getObject(1);
					if(o==null)
						throw new RuntimeException("null in sequence " + name);
					return ((Integer)o).intValue();
				}
			});
		}
		{
			final Statement bf = database.createStatement();
			bf.append("DROP TABLE ").
				append(TEMP_TABLE);
			database.executeSQLUpdate(connection, bf, false);
		}
		try
		{
			connection.commit();
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "commit");
		}
		return result;
	}
	
	@Override
	protected Integer getNextSequence(
			final Database database,
			final Connection connection,
			final String name)
	{
		final Statement bf = database.createStatement();
		bf.append("SELECT START_WITH" +
					" FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES" +
					" WHERE SEQUENCE_NAME='").append(name).append('\'');
		
		return database.executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Integer>()
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
}

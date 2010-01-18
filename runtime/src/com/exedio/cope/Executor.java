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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.exedio.cope.misc.DatabaseListener;
import com.exedio.dsmf.SQLRuntimeException;

final class Executor
{
	static final String NO_SUCH_ROW = "no such row";
	
	final Dialect dialect;
	final boolean prepare;
	final boolean fulltextIndex;
	volatile DatabaseListener listener = null;
	
	Executor(
			final Dialect dialect,
			final ConnectProperties properties)
	{
		this.dialect = dialect;
		this.prepare = !properties.getDatabaseDontSupportPreparedStatements();
		this.fulltextIndex = properties.getFulltextIndex();
	}
	
	protected Statement newStatement()
	{
		return newStatement(true);
	}
	
	protected Statement newStatement(final boolean qualifyTable)
	{
		return new Statement(this, qualifyTable);
	}
	
	protected Statement newStatement(final Query<? extends Object> query)
	{
		return new Statement(this, query);
	}
	
	static interface ResultSetHandler<R>
	{
		public R handle(ResultSet resultSet) throws SQLException;
	}

	static final ResultSetHandler<Integer> integerResultSetHandler = new ResultSetHandler<Integer>()
	{
		public Integer handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			
			return resultSet.getInt(1);
		}
	};

	protected <R> R query(
		final Connection connection,
		final Statement statement,
		final ArrayList<QueryInfo> queryInfos,
		final boolean explain,
		final ResultSetHandler<R> resultSetHandler)
	{
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			final DatabaseListener listener = this.listener;
			final boolean takeTimes = !explain && (listener!=null || (queryInfos!=null));
			final String sqlText = statement.getText();
			final long timeStart = takeTimes ? System.currentTimeMillis() : 0;
			final long timePrepared;
			final long timeExecuted;
			
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				
				timePrepared = takeTimes ? System.currentTimeMillis() : 0;
				resultSet = sqlStatement.executeQuery(sqlText);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				
				timePrepared = takeTimes ? System.currentTimeMillis() : 0;
				resultSet = prepared.executeQuery();
			}
			timeExecuted = takeTimes ? System.currentTimeMillis() : 0;
			final R result = resultSetHandler.handle(resultSet);
			final long timeResultRead = takeTimes ? System.currentTimeMillis() : 0;
			
			if(resultSet!=null)
			{
				resultSet.close();
				resultSet = null;
			}
			if(sqlStatement!=null)
			{
				sqlStatement.close();
				sqlStatement = null;
			}

			if(explain)
				return result;

			final long timeEnd = takeTimes ? System.currentTimeMillis() : 0;
			
			if(listener!=null)
				listener.onStatement(statement.text.toString(), statement.getParameters(), timePrepared-timeStart, timeExecuted-timePrepared, timeResultRead-timeExecuted, timeEnd-timeResultRead);
			
			final QueryInfo queryInfo =
				(queryInfos!=null)
				? makeQueryInfo(statement, connection, timeStart, timePrepared, timeExecuted, timeResultRead, timeEnd)
				: null;
			
			if(queryInfos!=null)
				queryInfos.add(queryInfo);
			
			return result;
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, statement.toString());
		}
		finally
		{
			if(resultSet!=null)
			{
				try
				{
					resultSet.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	int update(
			final Connection connection,
			final Statement statement, final boolean checkRows)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		try
		{
			final String sqlText = statement.getText();
			final DatabaseListener listener = this.listener;
			final long timeStart = listener!=null ? System.currentTimeMillis() : 0;
			final int rows;
			
			final long timePrepared;
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				timePrepared = listener!=null ? System.currentTimeMillis() : 0;
				rows = sqlStatement.executeUpdate(sqlText);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				timePrepared = listener!=null ? System.currentTimeMillis() : 0;
				rows = prepared.executeUpdate();
			}
			
			final long timeEnd = listener!=null ? System.currentTimeMillis() : 0;

			if(listener!=null)
				listener.onStatement(statement.text.toString(), statement.getParameters(), timePrepared-timeStart, timePrepared-timeEnd, 0, 0);

			//System.out.println("("+rows+"): "+statement.getText());
			if(checkRows && rows!=1)
				throw new RuntimeException("expected one row, but got " + rows + " on statement " + sqlText);
			return rows;
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, statement.toString());
		}
		finally
		{
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	<R> R insert(
			final Connection connection,
			final Statement statement,
			final ResultSetHandler<R> generatedKeysHandler)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		ResultSet generatedKeysResultSet = null;
		try
		{
			final String sqlText = statement.getText();
			final DatabaseListener listener = this.listener;
			final long timeStart = listener!=null ? System.currentTimeMillis() : 0;
			
			final long timePrepared;
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				timePrepared = listener!=null ? System.currentTimeMillis() : 0;
				sqlStatement.executeUpdate(sqlText);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				timePrepared = listener!=null ? System.currentTimeMillis() : 0;
				prepared.executeUpdate();
			}
			
			final long timeEnd = listener!=null ? System.currentTimeMillis() : 0;

			if(listener!=null)
				listener.onStatement(sqlText, statement.getParameters(), timePrepared-timeStart, timeEnd-timePrepared, 0, 0);

			generatedKeysResultSet = sqlStatement.getGeneratedKeys();
			return generatedKeysHandler.handle(generatedKeysResultSet);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, statement.toString());
		}
		finally
		{
			if(generatedKeysResultSet!=null)
			{
				try
				{
					generatedKeysResultSet.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(sqlStatement!=null)
			{
				try
				{
					sqlStatement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	private QueryInfo makeQueryInfo(
			final Statement statement, final Connection connection,
			final long start, final long prepared, final long executed, final long resultRead, final long end)
	{
		final QueryInfo result = new QueryInfo(statement.getText());
		
		result.addChild(new QueryInfo("timing "+(end-start)+'/'+(prepared-start)+'/'+(executed-prepared)+'/'+(resultRead-executed)+'/'+(end-resultRead)+" (total/prepare/execute/readResult/close in ms)"));
		
		final ArrayList<Object> parameters = statement.parameters;
		if(parameters!=null)
		{
			final QueryInfo parametersChild = new QueryInfo("parameters");
			result.addChild(parametersChild);
			int i = 1;
			for(Object p : parameters)
				parametersChild.addChild(new QueryInfo(String.valueOf(i++) + ':' + p));
		}
			
		final QueryInfo plan = dialect.explainExecutionPlan(statement, connection, this);
		if(plan!=null)
			result.addChild(plan);
		
		return result;
	}
	
	static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}
	
	// TestDatabaseListener ------------------
	
	private static final TestDatabaseListener noopTestListener = new TestDatabaseListener()
	{
		public void load(Connection connection, Item item)
		{/* DOES NOTHING */}
		
		public void search(Connection connection, Query query, boolean totalOnly)
		{/* DOES NOTHING */}
	};

	private TestDatabaseListener testListener = noopTestListener;
	private final Object testListenerLock = new Object();
	
	TestDatabaseListener testListener()
	{
		synchronized(testListenerLock)
		{
			return this.testListener;
		}
	}
	
	TestDatabaseListener setTestListener(TestDatabaseListener testListener)
	{
		if(testListener==null)
			testListener = noopTestListener;
		TestDatabaseListener result;

		synchronized(testListenerLock)
		{
			result = this.testListener;
			this.testListener = testListener;
		}
		
		if(result==noopTestListener)
			result = null;
		return result;
	}
}

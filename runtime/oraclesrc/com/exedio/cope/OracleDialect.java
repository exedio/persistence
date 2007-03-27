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
import gnu.trove.TIntObjectHashMap;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import oracle.jdbc.OracleStatement;

import com.exedio.dsmf.Column;
import com.exedio.dsmf.OracleDriver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

final class OracleDialect extends Dialect
{
	static
	{
		try
		{
			Class.forName(oracle.jdbc.driver.OracleDriver.class.getName());
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final String VARCHAR = "varchar";

	/**
	 * whether to use varchar instead of nvarchar
	 */
	private final boolean varchar;

	private final int varcharLength;

	protected OracleDialect(final DialectParameters parameters)
	{
		super(new OracleDriver(parameters.properties.getDatabaseUser().toUpperCase()));
		this.varchar = "true".equalsIgnoreCase(parameters.properties.getDatabaseCustomProperty(VARCHAR));
		this.varcharLength = varchar ? 4000 : 2000;
	}
	
	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		return "NUMBER(" + ((minimum>=Integer.MIN_VALUE && maximum<=Integer.MAX_VALUE) ? 10 : 20) + ')'; // TODO do this more precisely
	}

	@Override
	String getDoubleType()
	{
		return "NUMBER(30,8)";
	}

	@Override
	String getStringType(final int maxLength)
	{
		if(maxLength<=varcharLength)
			return (varchar?"VARCHAR2(":"NVARCHAR2(")+maxLength+")";
		else
			return "CLOB"; // TODO may be should be (varchar?"CLOB":"NCLOB") , but does not work, gets in charset trouble
	}
	
	@Override
	String getDayType()
	{
		return "DATE";
	}
	
	@Override
	String getDateTimestampType()
	{
		return "TIMESTAMP(3)";
	}
	
	@Override
	String getBlobType(final long maximumLength)
	{
		return "BLOB";
	}
	
	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSES_AROUND;
	}

	@Override
	void appendLimitClause(final Statement bf, final int start, final int count)
	{
		if((start==0&&count==Query.UNLIMITED_COUNT)||(count<=0&&count!=Query.UNLIMITED_COUNT)||start<0)
			throw new RuntimeException(start+"-"+count);

		// TODO: check, whether ROW_NUMBER() OVER is faster,
		// see http://www.php-faq.de/q/q-oracle-limit.html
		bf.append("select * from(");
		if(start>0)
			bf.append("select "+com.exedio.cope.Table.SQL_ALIAS_2+".*,ROWNUM "+com.exedio.cope.Table.SQL_ALIAS_1+" from(");
	}
	
	@Override
	void appendLimitClause2(final Statement bf, final int start, final int count)
	{
		if((start==0&&count==Query.UNLIMITED_COUNT)||(count<=0&&count!=Query.UNLIMITED_COUNT)||start<0)
			throw new RuntimeException(start+"-"+count);

		bf.append(')');
		if(start>0)
			bf.append(com.exedio.cope.Table.SQL_ALIAS_2+' ');
		if(count!=Query.UNLIMITED_COUNT)
			bf.append("where ROWNUM<=").appendParameter(start+count);
		if(start>0)
			bf.append(")where "+com.exedio.cope.Table.SQL_ALIAS_1+'>').appendParameter(start);
	}
	
	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(contains(").
			append(function, (Join)null).
			append(',').
			appendParameter(function, value).
			append(")>0)");
	}
	
	@Override
	boolean supportsEmptyStrings()
	{
		return false;
	}

	@Override
	byte[] getBytes(final ResultSet resultSet, final int columnIndex) throws SQLException
	{
		final Blob blob = resultSet.getBlob(columnIndex);
		if(blob==null)
			return null;

		return DataField.copy(blob.getBinaryStream(), blob.length());
	}

	@Override
	boolean isDefiningColumnTypes()
	{
		return true;
	}
	
	@Override
	void defineColumnTypes(final TIntArrayList columnTypes, final java.sql.Statement statement)
			throws SQLException
	{
		//System.out.println("defineColumnTypes: "+columnTypes);
		final OracleStatement s = (OracleStatement)statement;
		final int columnTypesSize = columnTypes.size();
		int columnIndex = 1;
		for(int i = 0; i<columnTypesSize; i++, columnIndex++)
		{
			final int columnType = columnTypes.get(i);
			s.defineColumnType(columnIndex, columnType);
		}
	}

	@Override
	protected void completeSchema(final Schema schema)
	{
		final Table planTable = new Table(schema, "PLAN_TABLE");
		planTable.makeDefensive();
		new Column(planTable, STATEMENT_ID, "VARCHAR2(30)");
		new Column(planTable, "TIMESTAMP", "DATE");
		new Column(planTable, "REMARKS", "VARCHAR2(80)");
		new Column(planTable, OPERATION, "VARCHAR2(30)");
		new Column(planTable, OPTIONS, "VARCHAR2(30)");
		new Column(planTable, "OBJECT_NODE", "VARCHAR2(128)");
		new Column(planTable, "OBJECT_OWNER", "VARCHAR2(30)");
		new Column(planTable, OBJECT_NAME, "VARCHAR2(30)");
		new Column(planTable, OBJECT_INSTANCE, "NUMBER(22)");
		new Column(planTable, OBJECT_TYPE, "VARCHAR2(30)");
		new Column(planTable, "OPTIMIZER", "VARCHAR2(255)");
		new Column(planTable, "SEARCH_COLUMNS", "NUMBER(22)");
		new Column(planTable, ID, "NUMBER(22)");
		new Column(planTable, PARENT_ID, "NUMBER(22)");
		new Column(planTable, "POSITION", "NUMBER(22)");
		new Column(planTable, "COST", "NUMBER(22)");
		new Column(planTable, "CARDINALITY", "NUMBER(22)");
		new Column(planTable, "BYTES", "NUMBER(22)");
		new Column(planTable, "OTHER_TAG", "VARCHAR2(255)");
		new Column(planTable, "OTHER", "LONG");
	}
	
	private static final Random statementIDCounter = new Random();
	
	private static final String PLAN_TABLE = "PLAN_TABLE";
	private static final String STATEMENT_ID = "STATEMENT_ID";
	private static final String OPERATION = "OPERATION";
	private static final String OPTIONS = "OPTIONS";
	private static final String OBJECT_NAME = "OBJECT_NAME";
	private static final String OBJECT_INSTANCE = "OBJECT_INSTANCE";
	private static final String OBJECT_TYPE = "OBJECT_TYPE";
	private static final String ID = "ID";
	private static final String PARENT_ID = "PARENT_ID";

	private static final String STATEMENT_ID_PREFIX = "cope";

	private static final HashSet<String> skippedColumnNames = new HashSet<String>(Arrays.asList(new String[]{
			STATEMENT_ID,
			OPERATION,
			OPTIONS,
			"TIMESTAMP",
			"OBJECT_OWNER",
			OBJECT_NAME,
			OBJECT_INSTANCE,
			OBJECT_TYPE,
			ID,
			PARENT_ID,
			"POSITION",
		}));
	
	@Override
	protected StatementInfo explainExecutionPlan(final Statement statement, final Connection connection, final Database database)
	{
		final String statementText = statement.getText();
		if(statementText.startsWith("alter table "))
			return null;
		
		final int statementIDNumber;
		synchronized(statementIDCounter)
		{
			statementIDNumber = statementIDCounter.nextInt();
		}
		final String statementID = STATEMENT_ID_PREFIX + Integer.toString(Math.abs(statementIDNumber));
		
		final StatementInfo root;
		{
			final Statement bf = database.createStatement();
			bf.append("explain plan set "+STATEMENT_ID+"='").
				append(statementID). // TODO use placeholders for prepared statements
				append("' for ").
				append(statementText);
			java.sql.Statement sqlExplainStatement = null;
			try
			{
				// TODO: use executeSQLUpdate
				sqlExplainStatement = connection.createStatement();
				sqlExplainStatement.executeUpdate(bf.getText());
			}
			catch(SQLException e)
			{
				throw new SQLRuntimeException(e, bf.toString());
			}
			finally
			{
				if(sqlExplainStatement!=null)
				{
					try
					{
						sqlExplainStatement.close();
					}
					catch(SQLException e)
					{
						// exception is already thrown
					}
				}
			}
		}
		{
			final Statement bf = database.createStatement();
			bf.append("select * from "+PLAN_TABLE+" where "+STATEMENT_ID+'=').
				appendParameter(statementID).
				append(" order by "+ID);

			final PlanResultSetHandler handler = new PlanResultSetHandler();
			database.executeSQLQuery(connection, bf, handler, false, true);
			root = handler.root;
		}
		if(root==null)
			throw new RuntimeException();
		
		final StatementInfo result = new StatementInfo(EXPLAIN_PLAN + " statement_id=" + statementID);
		result.addChild(root);
		
		//System.out.println("######################");
		//System.out.println(statement.getText());
		//root.print(System.out);
		//System.out.println("######################");
		return result;
	}

	private static class PlanResultSetHandler implements Database.ResultSetHandler
	{
		StatementInfo root;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			final TIntObjectHashMap<StatementInfo> infos = new TIntObjectHashMap<StatementInfo>();

			final ResultSetMetaData metaData = resultSet.getMetaData();
			final int columnCount = metaData.getColumnCount();

			while(resultSet.next())
			{
				final String operation = resultSet.getString(OPERATION);
				final String options = resultSet.getString(OPTIONS);
				final String objectName = resultSet.getString(OBJECT_NAME);
				final int objectInstance = resultSet.getInt(OBJECT_INSTANCE);
				final String objectType = resultSet.getString(OBJECT_TYPE);
				final int id = resultSet.getInt(ID);
				final Number parentID = (Number)resultSet.getObject(PARENT_ID);
				
				final StringBuffer bf = new StringBuffer(operation);

				if(options!=null)
					bf.append(" (").append(options).append(')');

				if(objectName!=null)
					bf.append(" on ").append(objectName);
				
				if(objectInstance!=0)
					bf.append('[').append(objectInstance).append(']');

				if(objectType!=null)
					bf.append('[').append(objectType).append(']');
				

				for(int i = 1; i<=columnCount; i++)
				{
					final String columnName = metaData.getColumnName(i);
					if(!skippedColumnNames.contains(columnName))
					{
						final Object value = resultSet.getObject(i);
						if(value!=null)
						{
							bf.append(' ').
								append(columnName.toLowerCase()).
								append('=').
								append(value.toString());
						}
					}
				}

				final StatementInfo info = new StatementInfo(bf.toString());
				if(parentID==null)
				{
					if(root!=null)
						throw new RuntimeException(String.valueOf(id));
					root = info;
				}
				else
				{
					final StatementInfo parent = infos.get(parentID.intValue());
					if(parent==null)
						throw new RuntimeException();
					parent.addChild(info);
				}
				infos.put(id, info);
			}
		}
		
	}
}

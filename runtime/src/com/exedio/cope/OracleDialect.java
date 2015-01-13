/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.SQLRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TIntObjectHashMap;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

final class OracleDialect extends Dialect
{
	protected OracleDialect(final DialectParameters parameters)
	{
		super(
				new com.exedio.dsmf.OracleDialect(
						parameters.properties.getConnectionUsername().toUpperCase(Locale.ENGLISH)));
	}

	@Override
	protected int getTransationIsolation()
	{
		return Connection.TRANSACTION_READ_COMMITTED;
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

	/**
	 * The parameter of varchar specifies bytes.
	 * The parameter of nvarchar specifies characters.
	 * The maximum for both varchar and nvarchar is 4000 bytes.
	 */
	@Override
	String getStringType(
			final int maxChars,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		if(maxChars<=ORACLE_VARCHAR_MAX_CHARS)
			return "VARCHAR2(" + (maxChars*MAX_BYTES_PER_CHARACTER_UTF8) + " BYTE)";
		else
			return "CLOB"; // TODO may be should be (varchar?"CLOB":"NCLOB") , but does not work, gets in charset trouble
	}

	@Override
	String getStringLength()
	{
		return "LENGTH";
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
	<E extends Number> void  appendIntegerDivision(
			final Statement bf,
			final Function<E> dividend,
			final Function<E> divisor,
			final Join join)
	{
		bf.append("TRUNC(").
			append(dividend, join).
			append('/').
			append(divisor, join).
			append(')');
	}

	@Override
	protected void appendOrderByPostfix(final Statement bf, final boolean ascending)
	{
		bf.append(
				ascending
				? " NULLS FIRST"
				: " NULLS LAST" );
	}

	@Override
	LimitSupport getLimitSupport()
	{
		return LimitSupport.CLAUSES_AROUND;
	}

	@Override
	void appendLimitClause(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		// TODO: check, whether ROW_NUMBER() OVER is faster,
		// see http://www.php-faq.de/q/q-oracle-limit.html
		bf.append("SELECT * FROM(");
		if(offset>0)
			bf.append("SELECT "+com.exedio.cope.Table.SQL_ALIAS_2+".*,ROWNUM "+com.exedio.cope.Table.SQL_ALIAS_1+" FROM(");
	}

	@Override
	void appendLimitClause2(final Statement bf, final int offset, final int limit)
	{
		assert offset>=0;
		assert limit>0 || limit==Query.UNLIMITED;
		assert offset>0 || limit>0;

		bf.append(')');
		if(offset>0)
			bf.append(com.exedio.cope.Table.SQL_ALIAS_2+' ');
		if(limit!=Query.UNLIMITED)
			bf.append("WHERE ROWNUM<=").appendParameter(offset+limit);
		if(offset>0)
			bf.append(")WHERE "+com.exedio.cope.Table.SQL_ALIAS_1+'>').appendParameter(offset);
	}

	@Override
	protected void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("TO_CHAR(").
			append(source, join).
			append(')');
	}

	@Override
	protected void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(CONTAINS(").
			append(function, (Join)null).
			append(',').
			appendParameterAny(value).
			append(")>0)");
	}

	@Override
	String getBlobLength()
	{
		return "LENGTH";
	}

	@Override
	void appendStartsWith(final Statement bf, final BlobColumn column, final byte[] value)
	{
		bf.append("RAWTOHEX(DBMS_LOB.SUBSTR(").
			append(column, (Join)null).
			append(',').
			appendParameter(value.length).
			append(",1))=").
			appendParameter(Hex.encodeUpper(value));
	}

	@Override
	boolean supportsEmptyStrings()
	{
		return false;
	}

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	@Override
	byte[] getBytes(final ResultSet resultSet, final int columnIndex) throws SQLException
	{
		final Blob blob = resultSet.getBlob(columnIndex);
		if(blob==null)
			return null;

		return DataField.copy(blob.getBinaryStream(), blob.length());
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

	static final HashSet<String> skippedColumnNames = new HashSet<>(Arrays.asList(new String[]{
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

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	@Override
	protected QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor executor)
	{
		final String statementText = statement.getText();
		if(statementText.startsWith("alter table "))
			return null;

		Executor.update(connection,
			"CREATE GLOBAL TEMPORARY TABLE \"" + PLAN_TABLE + "\"(" +
				"\"" + STATEMENT_ID + "\" VARCHAR2(30 BYTE)," +
				"\"TIMESTAMP\" DATE," +
				"\"REMARKS\" VARCHAR2(80 BYTE)," +
				"\"" + OPERATION + "\" VARCHAR2(30 BYTE)," +
				"\"" + OPTIONS + "\" VARCHAR2(30 BYTE)," +
				"\"OBJECT_NODE\" VARCHAR2(128 BYTE)," +
				"\"OBJECT_OWNER\" VARCHAR2(30 BYTE)," +
				"\"" + OBJECT_NAME + "\" VARCHAR2(30 BYTE)," +
				"\"" + OBJECT_INSTANCE + "\" NUMBER(22)," +
				"\"" + OBJECT_TYPE + "\" VARCHAR2(30 BYTE)," +
				"\"OPTIMIZER\" VARCHAR2(255 BYTE)," +
				"\"SEARCH_COLUMNS\" NUMBER(22)," +
				"\"" + ID + "\" NUMBER(22)," +
				"\"" + PARENT_ID + "\" NUMBER(22)," +
				"\"POSITION\" NUMBER(22)," +
				"\"COST\" NUMBER(22)," +
				"\"CARDINALITY\" NUMBER(22)," +
				"\"BYTES\" NUMBER(22)," +
				"\"OTHER_TAG\" VARCHAR2(255 BYTE)," +
				"\"OTHER\" LONG)");

		final int statementIDNumber;
		synchronized(statementIDCounter)
		{
			statementIDNumber = statementIDCounter.nextInt();
		}
		final String statementID = STATEMENT_ID_PREFIX + Integer.toString(statementIDNumber!=Integer.MIN_VALUE ? Math.abs(statementIDNumber) : Integer.MAX_VALUE);

		{
			final Statement bf = executor.newStatement();
			bf.append("EXPLAIN PLAN SET "+STATEMENT_ID+"='").
				append(statementID). // TODO use placeholders for prepared statements
				append("' FOR ").
				append(statementText);
			try(java.sql.Statement sqlExplainStatement = connection.createStatement())
			{
				// TODO: use executeSQLUpdate
				sqlExplainStatement.executeUpdate(bf.getText());
			}
			catch(final SQLException e)
			{
				throw new SQLRuntimeException(e, bf.toString());
			}
		}
		final QueryInfo root;
		{
			final Statement bf = executor.newStatement();
			bf.append("SELECT * FROM "+PLAN_TABLE+" WHERE "+STATEMENT_ID+'=').
				appendParameter(statementID).
				append(" ORDER BY "+ID);

			root = executor.query(connection, bf, null, true, new ResultSetHandler<QueryInfo>()
			{
				public QueryInfo handle(final ResultSet resultSet) throws SQLException
				{
					QueryInfo root = null;
					final TIntObjectHashMap<QueryInfo> infos = new TIntObjectHashMap<>();

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

						final StringBuilder bf = new StringBuilder(operation);

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
										append(columnName.toLowerCase(Locale.ENGLISH)).
										append('=').
										append(value.toString());
								}
							}
						}

						final QueryInfo info = new QueryInfo(bf.toString());
						if(parentID==null)
						{
							if(root!=null)
								throw new RuntimeException(String.valueOf(id));
							root = info;
						}
						else
						{
							final QueryInfo parent = infos.get(parentID.intValue());
							if(parent==null)
								throw new RuntimeException();
							parent.addChild(info);
						}
						infos.put(id, info);
					}
					return root;
				}
			});
		}
		Executor.update(connection, "DROP TABLE \"" + PLAN_TABLE + "\"");
		if(root==null)
			throw new RuntimeException();

		final QueryInfo result = new QueryInfo(EXPLAIN_PLAN + " statement_id=" + statementID);
		result.addChild(root);

		//System.out.println("######################");
		//System.out.println(statement.getText());
		//root.print(System.out);
		//System.out.println("######################");
		return result;
	}

	@Override
	protected void deleteSequence(final StringBuilder bf, final String quotedName, final int startWith)
	{
		bf.append(
			"EXECUTE IMMEDIATE " +
				"'DROP SEQUENCE ").append(quotedName).append("';" +
			"EXECUTE IMMEDIATE '");
				com.exedio.dsmf.OracleDialect.createSequenceStatic(bf, quotedName, startWith);
				bf.append("';");
	}

	@Override
	protected Integer nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT ").
			append(quotedName).
			append(".NEXTVAL FROM DUAL");

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + quotedName);
				return ((BigDecimal)o).intValue();
			}
		});
	}

	@Override
	protected Integer getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		// NOTE:
		// Do not use "SELECT name.currval FROM DUAL" because this may cause
		// ORA-08002: sequence NAME.CURRVAL is not yet defined in this session
		final Statement bf = executor.newStatement();
		bf.append(
				"SELECT LAST_NUMBER " +
				"FROM user_sequences " +
				"WHERE sequence_name=").
			appendParameter(name);

		return executor.query(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final Object o = resultSet.getObject(1);
				if(o==null)
					throw new RuntimeException("null in sequence " + name);
				return ((BigDecimal)o).intValue() + 1;
			}
		});
	}

	@Override
	protected void deleteSchema(
			final List<Table> tables,
			final List<SequenceX> sequences,
			final ConnectionPool connectionPool)
	{
		final Connection connection = connectionPool.get(false);
		try
		{
			if(!tables.isEmpty())
			{
				Executor.update(connection, "set constraints all deferred");

				final StringBuilder bf = new StringBuilder("BEGIN ");
				for(final Table table : tables)
				{
					bf.append("DELETE FROM ").
						append(table.quotedID).
						append(';');
				}
				bf.append("END;");

				boolean committed = false;
				try
				{
					Executor.update(connection, bf.toString());
					connection.commit();
					committed = true;
				}
				finally
				{
					if(!committed)
						connection.rollback();
				}
			}

			if(!sequences.isEmpty())
			{
				final StringBuilder bf = new StringBuilder();
				for(final SequenceX sequence : sequences)
					sequence.delete(bf, this);

				if(bf.length()>0)
					Executor.update(connection, "BEGIN " + bf.toString() + "END;");
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, "commit/rollback");
		}
		finally
		{
			connectionPool.put(connection);
		}
	}
}

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

import static com.exedio.cope.OracleSchemaDialect.BLOB;
import static com.exedio.cope.OracleSchemaDialect.CLOB;
import static com.exedio.cope.OracleSchemaDialect.DATE;
import static com.exedio.cope.OracleSchemaDialect.NUMBER;
import static com.exedio.cope.OracleSchemaDialect.TIMESTAMP_3;
import static com.exedio.cope.OracleSchemaDialect.VARCHAR2;

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DayPartView.Part;
import com.exedio.cope.util.Hex;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Sequence;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gnu.trove.TIntObjectHashMap;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

final class OracleDialect extends Dialect
{
	/**
	 * See https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8.html
	 */
	private static final int MAX_BYTES_PER_CHARACTER_UTF8 = 3;

	private static final int VARCHAR_MAX_BYTES = 4000;
	        static final int VARCHAR_MAX_CHARS = VARCHAR_MAX_BYTES / MAX_BYTES_PER_CHARACTER_UTF8;

	OracleDialect(final CopeProbe probe)
	{
		super(
				new OracleSchemaDialect(
						probe.properties.getConnectionUsername().toUpperCase(Locale.ENGLISH)));

		requireDatabaseVersionAtLeast(11, 2, probe);
	}

	@Override
	void completeConnection(final Connection connection) throws SQLException
	{
		try(java.sql.Statement st = connection.createStatement())
		{
			st.execute(
					"ALTER SESSION SET " +
							"NLS_LANGUAGE='AMERICAN' " +
							"NLS_TERRITORY='AMERICA' " +

							// BEWARE: lots of objects in recyclebin slow down DDL operations.
							// TODO: connect property for recyclebin=ON/OFF/DEFAULT
							"recyclebin=OFF");
		}
	}

	@Override
	void setNameTrimmers(final EnumMap<TrimClass, Trimmer> trimmers)
	{
		super.setNameTrimmers(trimmers);

		final Trimmer dataTrimmer = trimmers.get(TrimClass.Data);

		for(final TrimClass c : TrimClass.values())
			if(c!=TrimClass.Data)
				trimmers.put(c, dataTrimmer);
	}

	@Override
	int getTransationIsolation()
	{
		return Connection.TRANSACTION_READ_COMMITTED;
	}

	@Override
	String getIntegerType(final long minimum, final long maximum)
	{
		final long max = Math.max(Math.abs(minimum), Math.abs(maximum));
		final int length = max == 0 ? 1 : (int)Math.log10(max)+1;
		return NUMBER(length, 0);
	}

	@Override
	String getDoubleType()
	{
		return NUMBER(30, 8);
	}

	@Override
	String format(final double number)
	{
		final String s = super.format(number);
		return
			(s.indexOf('E')>=0)
			? s + 'd' // https://docs.oracle.com/cd/B12037_01/server.101/b10759/sql_elements003.htm#i139891
			: s;
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
		// TODO Oracle 12 varchar2 with up to 32k characters
		if(maxChars<=VARCHAR_MAX_CHARS)
			return VARCHAR2(maxChars*MAX_BYTES_PER_CHARACTER_UTF8);
		else
			return CLOB; // TODO may be should be (varchar?"CLOB":"NCLOB") , but does not work, gets in charset trouble
	}

	@Override
	String getStringLength()
	{
		return "LENGTH";
	}

	@Override
	String getDayType()
	{
		return DATE;
	}

	@Override
	void appendDatePartExtraction(final DayPartView view, final Statement bf, final Join join)
	{
		if(Part.WEEK_OF_YEAR==view.getPart())
		{
			bf.append("TO_NUMBER(TO_CHAR(").
				append(view.getSource(), join).
				append(", 'IW'))");
		}
		else
		{
			super.appendDatePartExtraction(view, bf, join);
		}
	}

	@Override
	String getDateTimestampType()
	{
		return TIMESTAMP_3;
	}

	@Override
	String getDateIntegerPrecision(final String quotedName, final Precision precision)
	{
		throw new RuntimeException(quotedName + '/' + precision); // TODO
	}

	@Override
	String getBlobType(final long maximumLength)
	{
		return BLOB;
	}

	@Override
	void addBlobInStatementText(final StringBuilder statementText, final byte[] parameter)
	{
		statementText.append('\'');
		Hex.append(statementText, parameter, parameter.length);
		statementText.append('\'');
	}

	@Override
	void  appendIsNullInSelect(
			final Statement bf,
			final BlobColumn column)
	{
		bf.append("CASE WHEN ").
			append(column.quotedID).
			append(" IS NULL THEN 1 ELSE 0 END");
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
	PageSupport getPageSupport()
	{
		return PageSupport.CLAUSES_AROUND;
	}

	@Override
	void appendPageClause(final Statement bf, final int offset, final int limit)
	{
		// TODO Oracle 12 OFFSET/LIMIT
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
	void appendPageClause2(final Statement bf, final int offset, final int limit)
	{
		// TODO Oracle 12 OFFSET/LIMIT
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
	void appendAsString(final Statement bf, final NumberFunction<?> source, final Join join)
	{
		bf.append("TO_CHAR(").
			append(source, join).
			append(')');
	}

	@Override
	void appendMatchClauseFullTextIndex(final Statement bf, final StringFunction function, final String value)
	{
		bf.append("(CONTAINS(").
			append(function).
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
			append(column).
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

	static final HashSet<String> skippedColumnNames = new HashSet<>(Arrays.asList(
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
			"POSITION"
		));

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	@Override
	QueryInfo explainExecutionPlan(final Statement statement, final Connection connection, final Executor executor)
	{
		final String statementText = statement.getText();
		if(statementText.startsWith("alter table "))
			return null;

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

			root = executor.query(connection, bf, null, true, resultSet ->
				{
					QueryInfo currentRoot = null;
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
						final int parentID = resultSet.getInt(PARENT_ID);
						final boolean parentIDNull = resultSet.wasNull();

						final StringBuilder qi = new StringBuilder(operation);

						if(options!=null)
							qi.append(" (").append(options).append(')');

						if(objectName!=null)
							qi.append(" on ").append(objectName);

						if(objectInstance!=0)
							qi.append('[').append(objectInstance).append(']');

						if(objectType!=null)
							qi.append('[').append(objectType).append(']');


						for(int i = 1; i<=columnCount; i++)
						{
							final String columnName = metaData.getColumnName(i);
							if(!skippedColumnNames.contains(columnName))
							{
								final Object value = resultSet.getObject(i);
								if(value!=null)
								{
									qi.append(' ').
										append(columnName.toLowerCase(Locale.ENGLISH)).
										append('=').
										append(value);
								}
							}
						}

						final QueryInfo info = new QueryInfo(qi.toString());
						if(parentIDNull)
						{
							if(currentRoot!=null)
								throw new RuntimeException(String.valueOf(id));
							currentRoot = info;
						}
						else
						{
							final QueryInfo parent = infos.get(parentID);
							if(parent==null)
								throw new RuntimeException();
							parent.addChild(info);
						}
						infos.put(id, info);
					}
					return currentRoot;
				}
			);
		}
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
	void deleteSequence(
			final StringBuilder bf, final String quotedName,
			final Sequence.Type type, final long start)
	{
		// TODO Oracle 12
		// There seems to be a restart command in Oracle 12c:
		// ALTER SEQUENCE SERIAL RESTART START WITH start
		// https://stackoverflow.com/questions/51470/how-do-i-reset-a-sequence-in-oracle
		bf.append(
			"EXECUTE IMMEDIATE " +
				"'DROP SEQUENCE ").append(quotedName).append("';" +
			"EXECUTE IMMEDIATE '");
				OracleSchemaDialect.createSequenceStatic(
						bf, quotedName,
						type, start);
				bf.append("';");
	}

	@Override
	Long nextSequence(
			final Executor executor,
			final Connection connection,
			final String quotedName)
	{
		final Statement bf = executor.newStatement();
		bf.append("SELECT ").
			append(quotedName).
			append(".NEXTVAL FROM DUAL");

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + quotedName);
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + quotedName);
				return result;
			}
		);
	}

	@Override
	Long getNextSequence(
			final Executor executor,
			final Connection connection,
			final String name)
	{
		// NOTE:
		// Do not use "SELECT name.currval FROM DUAL" because this may cause
		// ORA-08002: sequence NAME.CURRVAL is not yet defined in this session
		final Statement bf = executor.newStatement();
		bf.append(
				// BEWARE:
				// In contrast to what the name suggests, LAST_NUMBER does not contain
				// the last result returned by NEXTVAL, but the next result to be returned.
				// https://stackoverflow.com/questions/4596220/how-to-verify-oracle-sequences
				// https://docs.oracle.com/cd/B28359_01/server.111/b28320/statviews_2053.htm#i1588488
				// BEWARE 2:
				// Without NOCACHE in CREATE SEQUENCE wrong results are returned by LAST_NUMBER.
				"SELECT LAST_NUMBER " +
				"FROM user_sequences " +
				"WHERE sequence_name=").
			appendParameter(name);

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new RuntimeException("empty in sequence " + name);
				final long result = resultSet.getLong(1);
				if(resultSet.wasNull())
					throw new RuntimeException("null in sequence " + name);
				return result;
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
					Executor.update(connection, "BEGIN " + bf + "END;");
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

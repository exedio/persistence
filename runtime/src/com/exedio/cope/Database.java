/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exedio.dsmf.ConnectionProvider;
import com.exedio.dsmf.Driver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;

final class Database
{
	private static final String NO_SUCH_ROW = "no such row";
	
	private final ArrayList<Table> tables = new ArrayList<Table>();
	private final HashMap<String, UniqueConstraint> uniqueConstraintsByID = new HashMap<String, UniqueConstraint>();
	private boolean buildStage = true;
	final Driver driver;
	final DialectParameters dialectParameters;
	final Dialect dialect;
	private final boolean revisionEnabled;
	final boolean prepare;
	volatile DatabaseLogConfig log;
	private final boolean logQueryInfo;
	private final boolean fulltextIndex;
	final Pool<Connection> connectionPool;
	final boolean mysqlLowerCaseTableNames;
	final java.util.Properties tableOptions;
	final Dialect.LimitSupport limitSupport;
	final long blobLengthFactor;
	final boolean supportsReadCommitted;
	
	private final boolean oracle; // TODO remove
	
	Database(final Driver driver, final DialectParameters dialectParameters, final Dialect dialect, final boolean revisionEnabled)
	{
		final ConnectProperties properties = dialectParameters.properties;
		this.driver = driver;
		this.dialectParameters = dialectParameters;
		this.dialect = dialect;
		this.revisionEnabled = revisionEnabled;
		this.prepare = !properties.getDatabaseDontSupportPreparedStatements();
		this.log = properties.getDatabaseLog() ? new DatabaseLogConfig(properties.getDatabaseLogThreshold(), null, System.out) : null;
		this.logQueryInfo = properties.getDatabaseLogQueryInfo();
		this.fulltextIndex = properties.getFulltextIndex();
		this.connectionPool = new Pool<Connection>(
				new ConnectionFactory(properties, dialect),
				properties.getConnectionPoolIdleLimit(),
				properties.getConnectionPoolIdleInitial());
		this.mysqlLowerCaseTableNames = properties.getMysqlLowerCaseTableNames();
		this.tableOptions = properties.getDatabaseTableOptions();
		this.limitSupport = properties.getDatabaseDontSupportLimit() ? Dialect.LimitSupport.NONE : dialect.getLimitSupport();
		this.blobLengthFactor = dialect.getBlobLengthFactor();
		this.oracle = dialect.getClass().getName().equals("com.exedio.cope.OracleDialect");
		
		//System.out.println("using database "+getClass());
		assert limitSupport!=null;
		
		this.supportsReadCommitted =
			!dialect.fakesSupportReadCommitted() &&
			dialectParameters.supportsTransactionIsolationLevel;
	}
	
	Driver getDriver()
	{
		return driver;
	}
	
	java.util.Properties getTableOptions()
	{
		return tableOptions;
	}
	
	void addTable(final Table table)
	{
		if(!buildStage)
			throw new RuntimeException();
		tables.add(table);
	}
	
	void addUniqueConstraint(final String constraintID, final UniqueConstraint constraint)
	{
		if(!buildStage)
			throw new RuntimeException();

		final Object collision = uniqueConstraintsByID.put(constraintID, constraint);
		if(collision!=null)
			throw new RuntimeException("ambiguous unique constraint "+constraint+" trimmed to >"+constraintID+"< colliding with "+collision);
	}
	
	final String intern(final String s)
	{
		return Model.intern(s);
	}
	
	protected Statement createStatement()
	{
		return createStatement(true);
	}
	
	protected Statement createStatement(final boolean qualifyTable)
	{
		return new Statement(this, qualifyTable);
	}
	
	protected Statement createStatement(final Query<? extends Object> query)
	{
		return new Statement(this, query);
	}
	
	void createDatabase(final int migrationRevision)
	{
		buildStage = false;
		
		makeSchema().create();
		
		if(revisionEnabled)
		{
			final Pool<Connection> connectionPool = this.connectionPool;
			Connection con = null;
			try
			{
				con = connectionPool.get();
				con.setAutoCommit(true);
				insertMigration(con, migrationRevision, Revision.create(migrationRevision, getHostname(), dialectParameters));
			}
			catch(SQLException e)
			{
				throw new SQLRuntimeException(e, "migrate");
			}
			finally
			{
				if(con!=null)
				{
					connectionPool.put(con);
					con = null;
				}
			}
		}
	}

	void createDatabaseConstraints(final int mask)
	{
		buildStage = false;
		
		makeSchema().createConstraints(mask);
	}

	//private static int checkTableTime = 0;

	void checkDatabase(final Connection connection)
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		
		// IMPLEMENTATION NOTE
		// MySQL can have at most 63 joined tables in one statement
		// and other databases probably have similar constraints as
		// well, so we limit the number of joined table here.
		final int CHUNK_LENGTH = 60;
		final int tablesSize = tables.size();
		
		for(int chunkFromIndex = 0; chunkFromIndex<tablesSize; chunkFromIndex+=CHUNK_LENGTH)
		{
			final int chunkToIndex = Math.min(chunkFromIndex+CHUNK_LENGTH, tablesSize);
			final List<Table> tableChunk = tables.subList(chunkFromIndex, chunkToIndex);
			
			final Statement bf = createStatement(true);
			bf.append("select count(*) from ").defineColumnInteger();
			boolean first = true;
	
			for(final Table table : tableChunk)
			{
				if(first)
					first = false;
				else
					bf.append(',');
	
				bf.append(table.protectedID);
			}
			
			bf.append(" where ");
			first = true;
			for(final Table table : tableChunk)
			{
				if(first)
					first = false;
				else
					bf.append(" and ");
	
				final Column primaryKey = table.primaryKey;
				bf.append(primaryKey).
					append('=').
					appendParameter(PkSource.NaPK);
				
				for(final Column column : table.getColumns())
				{
					bf.append(" and ").
						append(column);
					
					if(column instanceof BlobColumn || (oracle && column instanceof StringColumn && ((StringColumn)column).maximumLength>Dialect.ORACLE_VARCHAR_MAX_CHARS))
					{
						bf.append("is not null");
					}
					else
					{
						bf.append('=').
							appendParameter(column, column.getCheckValue());
					}
				}
			}
			
			//System.out.println("-----------"+chunkFromIndex+"-"+chunkToIndex+"----"+bf);
			executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Void>()
			{
				public Void handle(final ResultSet resultSet) throws SQLException
				{
					if(!resultSet.next())
						throw new SQLException(NO_SUCH_ROW);
					
					return null;
				}
			});
		}
	}

	void dropDatabase()
	{
		buildStage = false;

		makeSchema().drop();
	}
	
	void dropDatabaseConstraints(final int mask)
	{
		buildStage = false;

		makeSchema().dropConstraints(mask);
	}
	
	void tearDownDatabase()
	{
		buildStage = false;

		makeSchema().tearDown();
	}

	void tearDownDatabaseConstraints(final int mask)
	{
		buildStage = false;

		makeSchema().tearDownConstraints(mask);
	}
	
	void checkEmptyDatabase(final Connection connection)
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		for(final Table table : tables)
		{
			final int count = countTable(connection, table);
			if(count>0)
				throw new RuntimeException("there are "+count+" items left for table "+table.id);
		}
		//final long amount = (System.currentTimeMillis()-time);
		//checkEmptyTableTime += amount;
		//System.out.println("CHECK EMPTY TABLES "+amount+"ms  accumulated "+checkEmptyTableTime);
	}
	
	ArrayList<Object> search(
			final Connection connection,
			final Query<? extends Object> query,
			final boolean totalOnly,
			final ArrayList<QueryInfo> queryInfos)
	{
		buildStage = false;

		listener.search(connection, query, totalOnly);
		
		final int offset = query.offset;
		final int limit = query.limit;
		final boolean limitActive = offset>0 || limit!=Query.UNLIMITED;
		final boolean distinct = query.distinct;

		final ArrayList<Join> queryJoins = query.joins;
		final Statement bf = createStatement(query);
		
		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause(bf, offset, limit);
		
		bf.append("select");
		
		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSE_AFTER_SELECT)
			dialect.appendLimitClause(bf, offset, limit);
		
		bf.append(' ');
		
		final Selectable[] selects = query.selects;
		final Column[] selectColumns = new Column[selects.length];
		final Type[] selectTypes = new Type[selects.length];

		if(!distinct&&totalOnly)
		{
			bf.append("count(*)");
		}
		else
		{
			if(totalOnly)
				bf.append("count(");
			if(distinct)
				bf.append("distinct ");

			for(int selectIndex = 0; selectIndex<selects.length; selectIndex++)
			{
				final Selectable select = selects[selectIndex];
				final Column selectColumn;
				final Type selectType = select.getType();
				final Table selectTable;
				final Column selectPrimaryKey;

				if(selectIndex>0)
					bf.append(',');
				
				if(select instanceof Aggregate)
				{
					bf.append(select, null).defineColumn(select);
					final Function selectSource = ((Aggregate)select).getSource();
					
					if(selectSource instanceof FunctionField)
					{
						selectColumn = ((FunctionField)selectSource).getColumn();
					}
					else if(selectSource instanceof Type.This)
					{
						selectTable = selectType.getTable();
						selectPrimaryKey = selectTable.primaryKey;
						selectColumn = selectPrimaryKey;
		
						if(selectColumn.primaryKey)
						{
							final StringColumn selectTypeColumn = selectColumn.getTypeColumn();
							if(selectTypeColumn==null)
								selectTypes[selectIndex] = selectType.getOnlyPossibleTypeOfInstances();
						}
						else
							selectTypes[selectIndex] = selectType.getOnlyPossibleTypeOfInstances();
					}
					else
					{
						selectColumn = null;
						final View view = (View)selectSource;
						bf.append(view, (Join)null).defineColumn(view);
					}
				}
				else if(select instanceof FunctionField)
				{
					selectColumn = ((FunctionField)select).getColumn();
					bf.append(select, (Join)null).defineColumn(select);
					if(select instanceof ItemField)
					{
						final StringColumn typeColumn = ((ItemField)select).getTypeColumn();
						if(typeColumn!=null)
							bf.append(',').append(typeColumn).defineColumn(typeColumn);
					}
				}
				else if(select instanceof Type.This)
				{
					selectTable = selectType.getTable();
					selectPrimaryKey = selectTable.primaryKey;
					selectColumn = selectPrimaryKey;
	
					bf.appendPK(selectType, (Join)null).defineColumn(select);
	
					if(selectColumn.primaryKey)
					{
						final StringColumn selectTypeColumn = selectColumn.getTypeColumn();
						if(selectTypeColumn!=null)
						{
							bf.append(',').
								append(selectTypeColumn).defineColumn(selectTypeColumn);
						}
						else
							selectTypes[selectIndex] = selectType.getOnlyPossibleTypeOfInstances();
					}
					else
						selectTypes[selectIndex] = selectType.getOnlyPossibleTypeOfInstances();
				}
				else
				{
					selectColumn = null;
					final View view = (View)select;
					bf.append(view, (Join)null).defineColumn(view);
				}
	
				selectColumns[selectIndex] = selectColumn;
			}
			
			if(totalOnly)
				bf.append(')');
		}

		bf.append(" from ").
			appendTypeDefinition((Join)null, query.type);

		if(queryJoins!=null)
		{
			for(final Join join : queryJoins)
			{
				final Condition joinCondition = join.condition;
				
				if(joinCondition==null)
				{
					if(join.kind!=Join.Kind.INNER)
						throw new RuntimeException("outer join must have join condition");
					
					bf.append(" cross join ");
				}
				else
				{
					bf.append(' ').
						append(join.kind.sql);
				}
				
				bf.appendTypeDefinition(join, join.type);
				
				if(joinCondition!=null)
				{
					bf.append(" on ");
					joinCondition.append(bf);
				}
			}
		}

		if(query.condition!=null)
		{
			bf.append(" where ");
			query.condition.append(bf);
		}
		
		if(!totalOnly)
		{
			final Function[] orderBy = query.orderBy;
			
			if(orderBy!=null)
			{
				final boolean[] orderAscending = query.orderAscending;
				for(int i = 0; i<orderBy.length; i++)
				{
					if(i==0)
						bf.append(" order by ");
					else
						bf.append(',');
					
					bf.append(orderBy[i], (Join)null);
					
					if(!orderAscending[i])
						bf.append(" desc");

					// TODO break here, if already ordered by some unique function
				}
			}
			
			if(limitActive && limitSupport==Dialect.LimitSupport.CLAUSE_AFTER_WHERE)
				dialect.appendLimitClause(bf, offset, limit);
		}

		if(!totalOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause2(bf, offset, limit);
		
		final Type[] types = selectTypes;
		final Model model = query.model;
		final ArrayList<Object> result = new ArrayList<Object>();

		if(offset<0)
			throw new RuntimeException();
		if(selects.length!=selectColumns.length)
			throw new RuntimeException();
		if(selects.length!=types.length)
			throw new RuntimeException();
		
		//System.out.println(bf.toString());

		executeSQLQuery(connection, bf, queryInfos, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				if(totalOnly)
				{
					resultSet.next();
					result.add(Integer.valueOf(resultSet.getInt(1)));
					if(resultSet.next())
						throw new RuntimeException();
					return null;
				}
				
				if(offset>0 && limitSupport==Dialect.LimitSupport.NONE)
				{
					// TODO: ResultSet.relative
					// Would like to use
					//    resultSet.relative(limitStart+1);
					// but this throws a java.sql.SQLException:
					// Invalid operation for forward only resultset : relative
					for(int i = offset; i>0; i--)
						resultSet.next();
				}
					
				int i = ((limit==Query.UNLIMITED||(limitSupport!=Dialect.LimitSupport.NONE)) ? Integer.MAX_VALUE : limit );
				if(i<=0)
					throw new RuntimeException(String.valueOf(limit));
				
				while(resultSet.next() && (--i)>=0)
				{
					int columnIndex = 1;
					final Object[] resultRow = (selects.length > 1) ? new Object[selects.length] : null;
					final Row dummyRow = new Row();
						
					for(int selectIndex = 0; selectIndex<selects.length; selectIndex++)
					{
						final Selectable select;
						{
							Selectable select0 = selects[selectIndex];
							if(select0 instanceof Aggregate)
								select0 = ((Aggregate)select0).getSource();
							select = select0;
						}
						
						final Object resultCell;
						if(select instanceof FunctionField)
						{
							selectColumns[selectIndex].load(resultSet, columnIndex++, dummyRow);
							final FunctionField selectField = (FunctionField)select;
							if(select instanceof ItemField)
							{
								final StringColumn typeColumn = ((ItemField)selectField).getTypeColumn();
								if(typeColumn!=null)
									typeColumn.load(resultSet, columnIndex++, dummyRow);
							}
							resultCell = selectField.get(dummyRow);
						}
						else if(select instanceof View)
						{
							final View selectFunction = (View)select;
							resultCell = selectFunction.load(resultSet, columnIndex++);
						}
						else
						{
							final Number pk = (Number)resultSet.getObject(columnIndex++);
							//System.out.println("pk:"+pk);
							if(pk==null)
							{
								// can happen when using right outer joins
								resultCell = null;
							}
							else
							{
								final Type type = types[selectIndex];
								final Type currentType;
								if(type==null)
								{
									final String typeID = resultSet.getString(columnIndex++);
									currentType = model.getType(typeID);
									if(currentType==null)
										throw new RuntimeException("no type with type id "+typeID);
								}
								else
									currentType = type;

								final int pkPrimitive = pk.intValue();
								if(!PkSource.isValid(pkPrimitive))
									throw new RuntimeException("invalid primary key " + pkPrimitive + " for type " + type.id);
								resultCell = currentType.getItemObject(pkPrimitive);
							}
						}
						if(resultRow!=null)
							resultRow[selectIndex] = resultCell;
						else
							result.add(resultCell);
					}
					if(resultRow!=null)
						result.add(Collections.unmodifiableList(Arrays.asList(resultRow)));
				}
				
				return null;
			}
		});

		return result;
	}
	
	void load(final Connection connection, final WrittenState state)
	{
		buildStage = false;

		listener.load(connection, state);
		
		final Statement bf = createStatement(state.type.supertype!=null);
		bf.append("select ");

		boolean first = true;
		for(Type type = state.type; type!=null; type = type.supertype)
		{
			for(final Column column : type.getTable().getColumns())
			{
				if(!(column instanceof BlobColumn))
				{
					if(first)
						first = false;
					else
						bf.append(',');

					bf.append(column).defineColumn(column);
				}
			}
		}
		
		if(first)
		{
			// no columns in type
			bf.appendPK(state.type, (Join)null);
		}

		bf.append(" from ");
		first = true;
		for(Type type = state.type; type!=null; type = type.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(type.getTable().protectedID);
		}
			
		bf.append(" where ");
		first = true;
		for(Type type = state.type; type!=null; type = type.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			bf.appendPK(type, (Join)null).
				append('=').
				appendParameter(state.pk).
				appendTypeCheck(type.getTable(), state.type); // Here this also checks additionally for Model#getItem, that the item has the type given in the ID.
		}
			
		//System.out.println(bf.toString());
		executeSQLQuery(connection, bf, null, false, state);
	}

	void store(
			final Connection connection,
			final State state,
			final boolean present,
			final Map<BlobColumn, byte[]> blobs)
	{
		store(connection, state, present, blobs, state.type);
	}

	private void store(
			final Connection connection,
			final State state,
			final boolean present,
			final Map<BlobColumn, byte[]> blobs,
			final Type<?> type)
	{
		buildStage = false;

		final Type supertype = type.supertype;
		if(supertype!=null)
			store(connection, state, present, blobs, supertype);
			
		final Table table = type.getTable();

		final List<Column> columns = table.getColumns();

		final Statement bf = createStatement();
		final StringColumn typeColumn = table.typeColumn;
		if(present)
		{
			bf.append("update ").
				append(table.protectedID).
				append(" set ");

			boolean first = true;
			for(final Column column : columns)
			{
				if(!(column instanceof BlobColumn) || blobs.containsKey(column))
				{
					if(first)
						first = false;
					else
						bf.append(',');
					
					bf.append(column.protectedID).
						append('=');
					
					if(column instanceof BlobColumn)
						bf.appendParameterBlob(blobs.get(column));
					else
						bf.appendParameter(column, state.store(column));
				}
			}
			if(first) // no columns in table
				return;
			
			bf.append(" where ").
				append(table.primaryKey.protectedID).
				append('=').
				appendParameter(state.pk).
				appendTypeCheck(table, state.type);
		}
		else
		{
			bf.append("insert into ").
				append(table.protectedID).
				append("(").
				append(table.primaryKey.protectedID);
			
			if(typeColumn!=null)
			{
				bf.append(',').
					append(typeColumn.protectedID);
			}

			for(final Column column : columns)
			{
				if(!(column instanceof BlobColumn) || blobs.containsKey(column))
				{
					bf.append(',').
						append(column.protectedID);
				}
			}

			bf.append(")values(").
				appendParameter(state.pk);
			
			if(typeColumn!=null)
			{
				bf.append(',').
					appendParameter(state.type.id);
			}

			for(final Column column : columns)
			{
				if(column instanceof BlobColumn)
				{
					if(blobs.containsKey(column))
					{
						bf.append(',').
							appendParameterBlob(blobs.get(column));
					}
				}
				else
				{
					bf.append(',').
						appendParameter(column, state.store(column));
				}
			}
			bf.append(')');
		}

		//System.out.println("storing "+bf.toString());
		executeSQLUpdate(connection, bf, true);
	}

	void delete(final Connection connection, final Item item)
	{
		buildStage = false;
		final Type type = item.type;
		final int pk = item.pk;

		for(Type currentType = type; currentType!=null; currentType = currentType.supertype)
		{
			final Table currentTable = currentType.getTable();
			final Statement bf = createStatement();
			bf.append("delete from ").
				append(currentTable.protectedID).
				append(" where ").
				append(currentTable.primaryKey.protectedID).
				append('=').
				appendParameter(pk);

			//System.out.println("deleting "+bf.toString());

			executeSQLUpdate(connection, bf, true);
		}
	}

	byte[] load(final Connection connection, final BlobColumn column, final Item item)
	{
		// TODO reuse code in load blob methods
		buildStage = false;

		final Table table = column.table;
		final Statement bf = createStatement();
		bf.append("select ").
			append(column.protectedID).defineColumn(column).
			append(" from ").
			append(table.protectedID).
			append(" where ").
			append(table.primaryKey.protectedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
			
		return executeSQLQuery(connection, bf, null, false, new ResultSetHandler<byte[]>()
		{
			public byte[] handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				return dialect.getBytes(resultSet, 1);
			}
		});
	}
	
	void load(final Connection connection, final BlobColumn column, final Item item, final OutputStream data, final DataField field)
	{
		buildStage = false;

		final Table table = column.table;
		final Statement bf = createStatement();
		bf.append("select ").
			append(column.protectedID).defineColumn(column).
			append(" from ").
			append(table.protectedID).
			append(" where ").
			append(table.primaryKey.protectedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
		
		executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				dialect.fetchBlob(resultSet, 1, item, data, field);
				
				return null;
			}
		});
	}
	
	long loadLength(final Connection connection, final BlobColumn column, final Item item)
	{
		buildStage = false;

		final Table table = column.table;
		final Statement bf = createStatement();
		bf.append("select length(").
			append(column.protectedID).defineColumnInteger().
			append(") from ").
			append(table.protectedID).
			append(" where ").
			append(table.primaryKey.protectedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
			
		return executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Long>()
		{
			public Long handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
	
				final Object o = resultSet.getObject(1);
				if(o==null)
					return -1l;
	
				long result = ((Number)o).longValue();
				final long factor = blobLengthFactor;
				if(factor!=1)
				{
					if(result%factor!=0)
						throw new RuntimeException("not dividable "+result+'/'+factor);
					result /= factor;
				}
				return result;
			}
		});
	}
	
	void store(
			final Connection connection, final BlobColumn column, final Item item,
			final DataField.Value data, final DataField field)
	{
		buildStage = false;

		final Table table = column.table;
		final Statement bf = createStatement();
		bf.append("update ").
			append(table.protectedID).
			append(" set ").
			append(column.protectedID).
			append('=');
		
		if(data!=null)
			bf.appendParameterBlob(data.asArray(field, item));
		else
			bf.append("NULL");
		
		bf.append(" where ").
			append(table.primaryKey.protectedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
		
		//System.out.println("storing "+bf.toString());
		executeSQLUpdate(connection, bf, true);
	}
	
	static interface ResultSetHandler<R>
	{
		public R handle(ResultSet resultSet) throws SQLException;
	}

	private static final ResultSetHandler<Integer> integerResultSetHandler = new ResultSetHandler<Integer>()
	{
		public Integer handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			
			return resultSet.getInt(1);
		}
	};
	
	static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}

	protected <X> X executeSQLQuery(
		final Connection connection,
		final Statement statement,
		final ArrayList<QueryInfo> queryInfos,
		final boolean explain,
		final ResultSetHandler<X> resultSetHandler)
	{
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			final DatabaseLogConfig log = this.log;
			final boolean takeTimes = !explain && (log!=null || this.logQueryInfo || (queryInfos!=null));
			final String sqlText = statement.getText();
			final long timeStart = takeTimes ? System.currentTimeMillis() : 0;
			final long timePrepared;
			final long timeExecuted;
			
			if(!prepare)
			{
				sqlStatement = connection.createStatement();

				dialect.defineColumnTypes(statement.columnTypes, sqlStatement);
				
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

				dialect.defineColumnTypes(statement.columnTypes, sqlStatement);
				
				timePrepared = takeTimes ? System.currentTimeMillis() : 0;
				resultSet = prepared.executeQuery();
			}
			timeExecuted = takeTimes ? System.currentTimeMillis() : 0;
			final X result = resultSetHandler.handle(resultSet);
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
			
			if(log!=null)
				log.log(statement, timeStart, timePrepared, timeExecuted, timeResultRead, timeEnd);
			
			final QueryInfo queryInfo =
				(this.logQueryInfo || (queryInfos!=null))
				? makeQueryInfo(statement, connection, timeStart, timePrepared, timeExecuted, timeResultRead, timeEnd)
				: null;
			
			if(this.logQueryInfo)
				queryInfo.print(System.out);
			
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
	
	private int executeSQLUpdate(
			final Connection connection,
			final Statement statement, final boolean checkRows)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		try
		{
			final String sqlText = statement.getText();
			final DatabaseLogConfig log = this.log;
			final long timeStart = log!=null ? System.currentTimeMillis() : 0;
			final int rows;
			
			final long timePrepared;
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				timePrepared = log!=null ? System.currentTimeMillis() : 0;
				rows = sqlStatement.executeUpdate(sqlText);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(final Object p : statement.parameters)
					prepared.setObject(parameterIndex++, p);
				timePrepared = log!=null ? System.currentTimeMillis() : 0;
				rows = prepared.executeUpdate();
			}
			
			final long timeEnd = log!=null ? System.currentTimeMillis() : 0;

			if(log!=null)
				log.log(statement, timeStart, timePrepared, timeEnd);

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
	
	QueryInfo makeQueryInfo(
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
	
	/**
	 * Trims a name to length for being a suitable qualifier for database entities,
	 * such as tables, columns, indexes, constraints, partitions etc.
	 */
	protected static String trimString(final String longString, final int maxLength)
	{
		if(maxLength<=0)
			throw new IllegalArgumentException("maxLength must be greater zero");
		if(longString.length()==0)
			throw new IllegalArgumentException("longString must not be empty");

		if(longString.length()<=maxLength)
			return (longString.indexOf('.')<=0) ? longString : longString.replace('.', '_');

		int longStringLength = longString.length();
		final int[] trimPotential = new int[maxLength];
		final ArrayList<String> words = new ArrayList<String>();
		{
			final StringBuilder buf = new StringBuilder();
			for(int i=0; i<longString.length(); i++)
			{
				char c = longString.charAt(i);
				if(c=='.')
					c = '_';
				if((c=='_' || Character.isUpperCase(c) || Character.isDigit(c)) && buf.length()>0)
				{
					words.add(buf.toString());
					int potential = 1;
					for(int j = buf.length()-1; j>=0; j--, potential++)
						trimPotential[j] += potential;
					buf.setLength(0);
				}
				if(buf.length()<maxLength)
					buf.append(c);
				else
					longStringLength--;
			}
			if(buf.length()>0)
			{
				words.add(buf.toString());
				int potential = 1;
				for(int j = buf.length()-1; j>=0; j--, potential++)
					trimPotential[j] += potential;
				buf.setLength(0);
			}
		}
		
		final int expectedTrimPotential = longStringLength - maxLength;
		//System.out.println("expected trim potential = "+expectedTrimPotential);

		int wordLength;
		int remainder = 0;
		for(wordLength = trimPotential.length-1; wordLength>=0; wordLength--)
		{
			//System.out.println("trim potential ["+wordLength+"] = "+trimPotential[wordLength]);
			remainder = trimPotential[wordLength] - expectedTrimPotential;
			if(remainder>=0)
				break;
		}
		
		final StringBuilder result = new StringBuilder(longStringLength);
		for(final String word : words)
		{
			//System.out.println("word "+word+" remainder:"+remainder);
			if((word.length()>wordLength) && remainder>0)
			{
				result.append(word.substring(0, wordLength+1));
				remainder--;
			}
			else if(word.length()>wordLength)
				result.append(word.substring(0, wordLength));
			else
				result.append(word);
		}
		//System.out.println("---- trimName("+longString+","+maxLength+") == "+result+"     --- "+words);

		if(result.length()!=maxLength)
			throw new RuntimeException(result.toString()+maxLength);

		return result.toString();
	}
	
	String makeName(final String longName)
	{
		return trimString(longName, 25);
	}

	/**
	 * Search full text.
	 */
	void appendMatchClause(final Statement bf, final StringFunction function, final String value)
	{
		if(fulltextIndex)
			dialect.appendMatchClauseFullTextIndex(bf, function, value);
		else
			dialect.appendMatchClauseByLike(bf, function, value);
	}
	
	private int countTable(final Connection connection, final Table table)
	{
		final Statement bf = createStatement();
		bf.append("select count(*) from ").defineColumnInteger().
			append(table.protectedID);

		return executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
	
				return convertSQLResult(resultSet.getObject(1));
			}
		});
	}

	Integer maxPK(final Connection connection, final Table table)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final String primaryKeyProtectedID = table.primaryKey.protectedID;
		bf.append("select max(").
			append(primaryKeyProtectedID).defineColumnInteger().
			append(") from ").
			append(table.protectedID);
			
		return executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Integer>()
		{
			public Integer handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				final Object o = resultSet.getObject(1);
				if(o!=null)
				{
					final int result = convertSQLResult(o);
					if(!PkSource.isValid(result))
						throw new RuntimeException("invalid primary key " + result + " in table " + table.id);
					return result;
				}
				else
				{
					return null;
				}
			}
		});
	}
	
	int checkTypeColumn(final Connection connection, final Type type)
	{
		buildStage = false;
		
		final Table table = type.getTable();
		final Table superTable = type.getSupertype().getTable();
		
		final Statement bf = createStatement(true);
		bf.append("select count(*) from ").
			append(table).append(',').append(superTable).
			append(" where ").
			append(table.primaryKey).append('=').append(superTable.primaryKey).
			append(" and ");
		
		if(table.typeColumn!=null)
			bf.append(table.typeColumn);
		else
			bf.appendParameter(type.id);
			
		bf.append("<>").append(superTable.typeColumn);
		
		//System.out.println("CHECKT:"+bf.toString());
		
		return executeSQLQuery(connection, bf, null, false, integerResultSetHandler);
	}
	
	int checkTypeColumn(final Connection connection, final ItemField field)
	{
		buildStage = false;
		
		final Table table = field.getType().getTable();
		final Table valueTable = field.getValueType().getTable();
		final String alias1 = driver.protectName(Table.SQL_ALIAS_1);
		final String alias2 = driver.protectName(Table.SQL_ALIAS_2);
		
		final Statement bf = createStatement(false);
		bf.append("select count(*) from ").
			append(table).append(' ').append(alias1).
			append(',').
			append(valueTable).append(' ').append(alias2).
			append(" where ").
			append(alias1).append('.').append(field.getColumn()).
			append('=').
			append(alias2).append('.').append(valueTable.primaryKey).
			append(" and ").
			append(alias1).append('.').append(field.getTypeColumn()).
			append("<>").
			append(alias2).append('.').append(valueTable.typeColumn);
		
		//System.out.println("CHECKA:"+bf.toString());
		
		return executeSQLQuery(connection, bf, null, false, integerResultSetHandler);
	}
	
	private static final String MIGRATION_COLUMN_REVISION_NAME = "v";
	private static final String MIGRATION_COLUMN_INFO_NAME = "i";
	private static final int MIGRATION_MUTEX_REVISION = -1;
	
	Schema makeSchema()
	{
		final Schema result = new Schema(driver, new ConnectionProvider()
		{
			public Connection getConnection() throws SQLException
			{
				final Connection result =  connectionPool.get();
				result.setAutoCommit(true);
				return result;
			}

			public void putConnection(Connection connection)
			{
				connectionPool.put(connection);
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);
		
		if(revisionEnabled)
		{
			final com.exedio.dsmf.Table table = new com.exedio.dsmf.Table(result, Table.MIGRATION_TABLE_NAME);
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_REVISION_NAME, dialect.getIntegerType(MIGRATION_MUTEX_REVISION, Integer.MAX_VALUE));
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_INFO_NAME, dialect.getBlobType(100*1000));
			new com.exedio.dsmf.UniqueConstraint(table, Table.MIGRATION_UNIQUE_CONSTRAINT_NAME, '(' + driver.protectName(MIGRATION_COLUMN_REVISION_NAME) + ')');
		}
		
		dialect.completeSchema(result);
		return result;
	}
	
	Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema();
		result.verify();
		return result;
	}
	
	private int getActualMigrationRevision(final Connection connection)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final String revision = driver.protectName(MIGRATION_COLUMN_REVISION_NAME);
		bf.append("select max(").
			append(revision).defineColumnInteger().
			append(") from ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME)).
			append(" where ").
			append(revision).
			append(">=0");
			
		return executeSQLQuery(connection, bf, null, false, integerResultSetHandler);
	}
	
	Map<Integer, byte[]> getRevisionLogs()
	{
		final Pool<Connection> connectionPool = this.connectionPool;
		Connection con = null;
		try
		{
			con = connectionPool.get();
			con.setAutoCommit(true);
			return getMigrationLogs(con);
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "getMigrationLogs");
		}
		finally
		{
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}
	
	private Map<Integer, byte[]> getMigrationLogs(final Connection connection)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final String revision = driver.protectName(MIGRATION_COLUMN_REVISION_NAME);
		bf.append("select ").
			append(revision).defineColumnInteger().
			append(',').
			append(driver.protectName(MIGRATION_COLUMN_INFO_NAME)).defineColumnString().
			append(" from ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME)).
			append(" where ").
			append(revision).
			append(">=0");
		
		final HashMap<Integer, byte[]> result = new HashMap<Integer, byte[]>();
		
		executeSQLQuery(connection, bf, null, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				while(resultSet.next())
				{
					final int revision = resultSet.getInt(1);
					final byte[] info = dialect.getBytes(resultSet, 2);
					final byte[] previous = result.put(revision, info);
					if(previous!=null)
						throw new RuntimeException("duplicate revision " + revision);
				}
				
				return null;
			}
		});
		return Collections.unmodifiableMap(result);
	}
	
	private void insertMigration(final Connection connection, final int revision, final byte[] info)
	{
		assert revisionEnabled;
		
		final Statement bf = createStatement();
		bf.append("insert into ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME)).
			append('(').
			append(driver.protectName(MIGRATION_COLUMN_REVISION_NAME)).
			append(',').
			append(driver.protectName(MIGRATION_COLUMN_INFO_NAME)).
			append(")values(").
			appendParameter(revision).
			append(',').
			appendParameterBlob(info).
			append(')');
		
		executeSQLUpdate(connection, bf, true);
	}
	
	void revise(final int expectedRevision, final Revision[] migrations)
	{
		assert expectedRevision>=0 : expectedRevision;
		assert revisionEnabled;

		final Pool<Connection> connectionPool = this.connectionPool;
		Connection con = null;
		try
		{
			con = connectionPool.get();
			try
			{
				con.setAutoCommit(true);
			}
			catch(SQLException e)
			{
				throw new SQLRuntimeException(e, "setAutoCommit");
			}
			
			final int actualRevision = getActualMigrationRevision(con);
			
			if(actualRevision>expectedRevision)
			{
				throw new IllegalArgumentException("cannot migrate backwards, expected " + expectedRevision + ", but was " + actualRevision);
			}
			else if(actualRevision<expectedRevision)
			{
				final int startMigrationIndex = expectedRevision - actualRevision - 1;
				if(startMigrationIndex>=migrations.length)
					throw new IllegalArgumentException(
							"attempt to migrate from " + actualRevision + " to " + expectedRevision +
							", but declared migrations allow from " + (expectedRevision - migrations.length) + " only");
				
				final Date date = new Date();
				final String hostname = getHostname();
				try
				{
					insertMigration(con, MIGRATION_MUTEX_REVISION, Revision.mutex(date, hostname, dialectParameters, expectedRevision, actualRevision));
				}
				catch(SQLRuntimeException e)
				{
					throw new IllegalStateException(
							"Migration mutex set: " +
							"Either a migration is currently underway, " +
							"or a migration has failed unexpectedly.", e);
				}
				for(int migrationIndex = startMigrationIndex; migrationIndex>=0; migrationIndex--)
				{
					final Revision migration = migrations[migrationIndex];
					final int revision = migration.number;
					assert migration.number == (expectedRevision - migrationIndex);
					final java.util.Properties info = Revision.revise(revision, date, hostname, dialectParameters, migration.comment);
					final String[] body = migration.body;
					for(int bodyIndex = 0; bodyIndex<body.length; bodyIndex++)
					{
						final String sql = body[bodyIndex];
						if(Model.isLoggingEnabled())
							System.out.println("COPE migrating " + revision + ':' + sql);
						final Statement bf = createStatement();
						bf.append(sql);
						final long start = System.currentTimeMillis();
						final int rows = executeSQLUpdate(con, bf, false);
						final long end = System.currentTimeMillis();
						Revision.reviseSql(info, bodyIndex, sql, rows, end-start);
					}
					insertMigration(con, revision, Revision.toBytes(info));
				}
				{
					final Statement bf = createStatement();
					bf.append("delete from ").
						append(driver.protectName(Table.MIGRATION_TABLE_NAME)).
						append(" where ").
						append(driver.protectName(MIGRATION_COLUMN_REVISION_NAME)).
						append('=').
						appendParameter(MIGRATION_MUTEX_REVISION);
					executeSQLUpdate(con, bf, true);
				}
			}
		}
		finally
		{
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}
	
	private static final String getHostname()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch(UnknownHostException e)
		{
			return null;
		}
	}

	
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	protected static void printMeta(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("------"+i+":"+metaData.getColumnName(i)+":"+metaData.getColumnType(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	protected static void printRow(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("----------"+i+":"+resultSet.getObject(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated // OK: for debugging
	@SuppressWarnings("unused") // OK: for debugging
	private static final ResultSetHandler logHandler = new ResultSetHandler<Void>()
	{
		public Void handle(final ResultSet resultSet) throws SQLException
		{
			final int columnCount = resultSet.getMetaData().getColumnCount();
			System.out.println("columnCount:"+columnCount);
			final ResultSetMetaData meta = resultSet.getMetaData();
			for(int i = 1; i<=columnCount; i++)
			{
				System.out.println(meta.getColumnName(i)+"|");
			}
			while(resultSet.next())
			{
				for(int i = 1; i<=columnCount; i++)
				{
					System.out.println(resultSet.getObject(i)+"|");
				}
			}
			return null;
		}
	};
	
	void close()
	{
		connectionPool.flush();
	}
	
	// listeners ------------------
	
	private static final DatabaseListener noopListener = new DatabaseListener()
	{
		public void load(Connection connection, WrittenState state)
		{/* DOES NOTHING */}
		
		public void search(Connection connection, Query query, boolean totalOnly)
		{/* DOES NOTHING */}
	};

	private DatabaseListener listener = noopListener;
	private final Object listenerLock = new Object();
	
	DatabaseListener setListener(DatabaseListener listener)
	{
		if(listener==null)
			listener = noopListener;
		DatabaseListener result;

		synchronized(listenerLock)
		{
			result = this.listener;
			this.listener = listener;
		}
		
		if(result==noopListener)
			result = null;
		return result;
	}
}

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bak.pcj.list.IntArrayList;

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
	private final boolean migrationSupported;
	final boolean prepare;
	volatile DatabaseLogConfig log;
	private final boolean logStatementInfo;
	private final boolean butterflyPkSource;
	private final boolean fulltextIndex;
	final Pool<Connection> connectionPool;
	private final java.util.Properties forcedNames;
	final java.util.Properties tableOptions;
	final Dialect.LimitSupport limitSupport;
	final long blobLengthFactor;
	final boolean supportsReadCommitted;
	final boolean supportsGetBytes;
	final boolean supportsBlobInResultSet;
	final boolean needsSavepoint;
	
	final boolean oracle; // TODO remove
	
	Database(final Driver driver, final DialectParameters dialectParameters, final Dialect dialect, final boolean migrationSupported)
	{
		final Properties properties = dialectParameters.properties;
		this.driver = driver;
		this.dialectParameters = dialectParameters;
		this.dialect = dialect;
		this.migrationSupported = migrationSupported;
		this.prepare = !properties.getDatabaseDontSupportPreparedStatements();
		this.log = properties.getDatabaseLog() ? new DatabaseLogConfig(properties.getDatabaseLogThreshold(), System.out) : null;
		this.logStatementInfo = properties.getDatabaseLogStatementInfo();
		this.butterflyPkSource = properties.getPkSourceButterfly();
		this.fulltextIndex = properties.getFulltextIndex();
		this.connectionPool = new Pool<Connection>(
				new ConnectionFactory(properties, dialect),
				properties.getConnectionPoolIdleLimit(),
				properties.getConnectionPoolIdleInitial());
		this.forcedNames = properties.getDatabaseForcedNames();
		this.tableOptions = properties.getDatabaseTableOptions();
		this.limitSupport = properties.getDatabaseDontSupportLimit() ? Dialect.LimitSupport.NONE : dialect.getLimitSupport();
		this.blobLengthFactor = dialect.getBlobLengthFactor();
		this.oracle = dialect.getClass().getName().equals("com.exedio.cope.OracleDialect");
		
		//System.out.println("using database "+getClass());
		assert limitSupport!=null;
		
		this.supportsReadCommitted =
			!dialect.fakesSupportReadCommitted() &&
			dialectParameters.supportsTransactionIsolationLevel;
		this.supportsGetBytes = dialect.supportsGetBytes();
		this.supportsBlobInResultSet = dialect.supportsBlobInResultSet();
		this.needsSavepoint = dialect.needsSavepoint();
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
	
	void createDatabase(final int migrationVersion)
	{
		buildStage = false;
		
		makeSchema().create();
		
		if(migrationSupported)
		{
			final Pool<Connection> connectionPool = this.connectionPool;
			Connection con = null;
			try
			{
				con = connectionPool.get();
				con.setAutoCommit(true);
				notifyMigration(con, migrationVersion, new Date(), "created schema", false);
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
					appendParameter(Type.NOT_A_PK);
				
				for(final Column column : table.getColumns())
				{
					bf.append(" and ").
						append(column);
					
					if(column instanceof BlobColumn || (oracle && column instanceof StringColumn && ((StringColumn)column).maximumLength>=4000))
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
			executeSQLQuery(connection, bf,
				new ResultSetHandler()
				{
					public void handle(final ResultSet resultSet) throws SQLException
					{
						if(!resultSet.next())
							throw new SQLException(NO_SUCH_ROW);
					}
				},
				false, false
			);
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
	
	ArrayList<Object> search(final Connection connection, final Query<? extends Object> query, final boolean doCountOnly)
	{
		buildStage = false;

		listener.search(connection, query, doCountOnly);
		
		final int limitStart = query.limitStart;
		final int limitCount = query.limitCount;
		final boolean limitActive = limitStart>0 || limitCount!=Query.UNLIMITED_COUNT;
		final boolean distinct = query.distinct;

		final ArrayList<Join> queryJoins = query.joins;
		final Statement bf = createStatement(query);
		
		if(!doCountOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause(bf, limitStart, limitCount);
		
		bf.append("select");
		
		if(!doCountOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSE_AFTER_SELECT)
			dialect.appendLimitClause(bf, limitStart, limitCount);
		
		bf.append(' ');
		
		final Selectable[] selects = query.selects;
		final Column[] selectColumns = new Column[selects.length];
		final Type[] selectTypes = new Type[selects.length];

		if(!distinct&&doCountOnly)
		{
			bf.append("count(*)");
		}
		else
		{
			if(doCountOnly)
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
			
			if(doCountOnly)
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
		
		if(!doCountOnly)
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
					
					if(orderBy[i] instanceof ItemField)
					{
						final ItemField<? extends Item> itemOrderBy = (ItemField<? extends Item>)orderBy[i];
						itemOrderBy.getValueType().getPkSource().appendOrderByExpression(bf, itemOrderBy);
					}
					else if(orderBy[i] instanceof Type.This)
					{
						final Type.This<? extends Item> itemOrderBy = (Type.This<? extends Item>)orderBy[i];
						itemOrderBy.type.getPkSource().appendOrderByExpression(bf, itemOrderBy);
					}
					else
						bf.append(orderBy[i], (Join)null);
					
					if(!orderAscending[i])
						bf.append(" desc");

					// TODO break here, if already ordered by some unique function
				}
			}
			
			if(limitActive && limitSupport==Dialect.LimitSupport.CLAUSE_AFTER_WHERE)
				dialect.appendLimitClause(bf, limitStart, limitCount);
		}

		if(!doCountOnly && limitActive && limitSupport==Dialect.LimitSupport.CLAUSES_AROUND)
			dialect.appendLimitClause2(bf, limitStart, limitCount);
		
		final Type[] types = selectTypes;
		final Model model = query.model;
		final ArrayList<Object> result = new ArrayList<Object>();

		if(limitStart<0)
			throw new RuntimeException();
		if(selects.length!=selectColumns.length)
			throw new RuntimeException();
		if(selects.length!=types.length)
			throw new RuntimeException();
		
		//System.out.println(bf.toString());

		query.addStatementInfo(executeSQLQuery(connection, bf, new ResultSetHandler()
			{
				public void handle(final ResultSet resultSet) throws SQLException
				{
					if(doCountOnly)
					{
						resultSet.next();
						result.add(Integer.valueOf(resultSet.getInt(1)));
						if(resultSet.next())
							throw new RuntimeException();
						return;
					}
					
					if(limitStart>0 && limitSupport==Dialect.LimitSupport.NONE)
					{
						// TODO: ResultSet.relative
						// Would like to use
						//    resultSet.relative(limitStart+1);
						// but this throws a java.sql.SQLException:
						// Invalid operation for forward only resultset : relative
						for(int i = limitStart; i>0; i--)
							resultSet.next();
					}
						
					int i = ((limitCount==Query.UNLIMITED_COUNT||(limitSupport!=Dialect.LimitSupport.NONE)) ? Integer.MAX_VALUE : limitCount );
					if(i<=0)
						throw new RuntimeException(String.valueOf(limitCount));
					
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
										currentType = model.findTypeByID(typeID);
										if(currentType==null)
											throw new RuntimeException("no type with type id "+typeID);
									}
									else
										currentType = type;

									resultCell = currentType.getItemObject(pk.intValue());
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
				}
			}, query.makeStatementInfo, false));

		return result;
	}
	
	void load(final Connection connection, final PersistentState state)
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
				appendTypeCheck(type.getTable(), state.type); // Here this also checks additionally for Model#findByID, that the item has the type given in the ID.
		}
			
		//System.out.println(bf.toString());
		executeSQLQuery(connection, bf, state, false, false);
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
		executeSQLUpdate(connection, bf, 1, type.declaredUniqueConstraints);
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

			executeSQLUpdate(connection, bf, 1);
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
			
		final LoadBlobResultSetHandler handler = new LoadBlobResultSetHandler(supportsGetBytes);
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static final class LoadBlobResultSetHandler implements ResultSetHandler
	{
		final boolean supportsGetBytes;
		
		LoadBlobResultSetHandler(final boolean supportsGetBytes)
		{
			this.supportsGetBytes = supportsGetBytes;
		}
		
		byte[] result;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new SQLException(NO_SUCH_ROW);
			
			result = supportsGetBytes ? resultSet.getBytes(1) : loadBlob(resultSet.getBlob(1));
		}
		
		private static final byte[] loadBlob(final Blob blob) throws SQLException
		{
			if(blob==null)
				return null;

			return DataField.copy(blob.getBinaryStream(), blob.length());
		}
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
		
		executeSQLQuery(connection, bf, new ResultSetHandler(){
			
			public void handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new SQLException(NO_SUCH_ROW);
				
				if(supportsBlobInResultSet)
				{
					final Blob blob = resultSet.getBlob(1);
					if(blob!=null)
					{
						InputStream source = null;
						try
						{
							source = blob.getBinaryStream();
							field.copy(source, data, blob.length(), item);
						}
						catch(IOException e)
						{
							throw new RuntimeException(e);
						}
						finally
						{
							if(source!=null)
							{
								try
								{
									source.close();
								}
								catch(IOException e)
								{/*IGNORE*/}
							}
						}
					}
				}
				else
				{
					InputStream source = null;
					try
					{
						source = resultSet.getBinaryStream(1);
						if(source!=null)
							field.copy(source, data, item);
					}
					catch(IOException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						if(source!=null)
						{
							try
							{
								source.close();
							}
							catch(IOException e)
							{/*IGNORE*/}
						}
					}
				}
			}
			
		}, false, false);
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
			
		final LoadBlobLengthResultSetHandler handler = new LoadBlobLengthResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private final class LoadBlobLengthResultSetHandler implements ResultSetHandler
	{
		long result;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new SQLException(NO_SUCH_ROW);

			final Object o = resultSet.getObject(1);
			if(o!=null)
			{
				long value = ((Number)o).longValue();
				final long factor = blobLengthFactor;
				if(factor!=1)
				{
					if(value%factor!=0)
						throw new RuntimeException("not dividable "+value+'/'+factor);
					value /= factor;
				}
				result = value;
			}
			else
				result = -1;
		}
	}
	
	void store(
			final Connection connection, final BlobColumn column, final Item item,
			final InputStream data, final DataField field)
		throws IOException
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
			bf.appendParameterBlob(data, field, item);
		else
			bf.append("NULL");
		
		bf.append(" where ").
			append(table.primaryKey.protectedID).
			append('=').
			appendParameter(item.pk).
			appendTypeCheck(table, item.type);
		
		//System.out.println("storing "+bf.toString());
		executeSQLUpdate(connection, bf, 1);
	}
	
	static interface ResultSetHandler
	{
		public void handle(ResultSet resultSet) throws SQLException;
	}

	private static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}

	//private static int timeExecuteQuery = 0;

	protected StatementInfo executeSQLQuery(
		final Connection connection,
		final Statement statement,
		final ResultSetHandler resultSetHandler,
		final boolean makeStatementInfo,
		final boolean explain)
	{
		java.sql.Statement sqlStatement = null;
		ResultSet resultSet = null;
		try
		{
			final DatabaseLogConfig log = this.log;
			final boolean takeTimes = !explain && (log!=null || this.logStatementInfo || makeStatementInfo);
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
				timeExecuted = takeTimes ? System.currentTimeMillis() : 0;
				resultSetHandler.handle(resultSet);
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
				timeExecuted = takeTimes ? System.currentTimeMillis() : 0;
				resultSetHandler.handle(resultSet);
			}
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
				return null;

			final long timeEnd = takeTimes ? System.currentTimeMillis() : 0;
			
			if(log!=null)
				log.log(statement, timeStart, timePrepared, timeExecuted, timeResultRead, timeEnd);
			
			final StatementInfo statementInfo =
				(this.logStatementInfo || makeStatementInfo)
				? makeStatementInfo(statement, connection, timeStart, timePrepared, timeExecuted, timeResultRead, timeEnd)
				: null;
			
			if(this.logStatementInfo)
				statementInfo.print(System.out);
			
			return makeStatementInfo ? statementInfo : null;
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
	
	private void executeSQLUpdate(final Connection connection, final Statement statement, final int expectedRows)
			throws UniqueViolationException
	{
		executeSQLUpdate(connection, statement, expectedRows, null);
	}

	private void executeSQLUpdate(
			final Connection connection,
			final Statement statement, final int expectedRows,
			final List<UniqueConstraint> threatenedUniqueConstraints)
		throws UniqueViolationException
	{
		java.sql.Statement sqlStatement = null;
		Savepoint savepoint = null;
		try
		{
			final String sqlText = statement.getText();
			final DatabaseLogConfig log = this.log;
			final long timeStart = log!=null ? System.currentTimeMillis() : 0;
			final int rows;
			
			if(threatenedUniqueConstraints!=null && threatenedUniqueConstraints.size()>0 && needsSavepoint)
				savepoint = connection.setSavepoint();
			
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
			if(rows!=expectedRows)
				throw new RuntimeException("expected "+expectedRows+" rows, but got "+rows+" on statement "+sqlText);
		}
		catch(SQLException e)
		{
			final UniqueViolationException wrappedException = wrapException(e, threatenedUniqueConstraints);
			if(wrappedException!=null)
			{
				if(savepoint!=null)
				{
					try
					{
						connection.rollback(savepoint);
						savepoint = null;
					}
					catch(SQLException ex)
					{
						throw new SQLRuntimeException(e, ex.getMessage() + " on rollback of: " + statement.toString());
					}
				}
				throw wrappedException;
			}
			else
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
	
	StatementInfo makeStatementInfo(
			final Statement statement, final Connection connection,
			final long start, final long prepared, final long executed, final long resultRead, final long end)
	{
		final StatementInfo result = new StatementInfo(statement.getText());
		
		result.addChild(new StatementInfo("timing "+(end-start)+'/'+(prepared-start)+'/'+(executed-prepared)+'/'+(resultRead-executed)+'/'+(end-resultRead)+" (total/prepare/execute/readResult/close in ms)"));
		
		final ArrayList<Object> parameters = statement.parameters;
		if(parameters!=null)
		{
			final StatementInfo parametersChild = new StatementInfo("parameters");
			result.addChild(parametersChild);
			int i = 1;
			for(Object p : parameters)
				parametersChild.addChild(new StatementInfo(String.valueOf(i++) + ':' + p));
		}
			
		final StatementInfo plan = dialect.explainExecutionPlan(statement, connection, this);
		if(plan!=null)
			result.addChild(plan);
		
		return result;
	}
	
	private UniqueViolationException wrapException(
			final SQLException e,
			final List<UniqueConstraint> threatenedUniqueConstraints)
	{
		final String uniqueConstraintID = dialect.extractUniqueConstraintName(e);
		if(uniqueConstraintID!=null)
		{
			final UniqueConstraint constraint;
			if(Dialect.ANY_CONSTRAINT.equals(uniqueConstraintID))
				constraint = (threatenedUniqueConstraints.size()==1) ? threatenedUniqueConstraints.get(0) : null;
			else
			{
				constraint = uniqueConstraintsByID.get(uniqueConstraintID);
				if(constraint==null)
					throw new SQLRuntimeException(e, "no unique constraint found for >"+uniqueConstraintID
																			+"<, has only "+uniqueConstraintsByID.keySet());
			}
			return new UniqueViolationException(constraint, null, e);
		}
		return null;
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
			final StringBuffer buf = new StringBuffer();
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
		
		final StringBuffer result = new StringBuffer(longStringLength);
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
		return makeName(null, longName);
	}

	String makeName(final String prefix, final String longName)
	{
		final String query = prefix==null ? longName : prefix+'.'+longName;
		final String forcedName = forcedNames.getProperty(query);
		//System.out.println("---------"+query+"--"+forcedName);
		if(forcedName!=null)
			return forcedName;
		
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

		final CountResultSetHandler handler = new CountResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static class CountResultSetHandler implements ResultSetHandler
	{
		int result;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new SQLException(NO_SUCH_ROW);

			result = convertSQLResult(resultSet.getObject(1));
		}
	}


	PkSource makePkSource(final Table table)
	{
		return butterflyPkSource ? (PkSource)new ButterflyPkSource(table) : new SequentialPkSource(table);
	}
	
	int[] getMinMaxPK(final Connection connection, final Table table)
	{
		buildStage = false;

		final Statement bf = createStatement();
		final String primaryKeyProtectedID = table.primaryKey.protectedID;
		bf.append("select min(").
			append(primaryKeyProtectedID).defineColumnInteger().
			append("),max(").
			append(primaryKeyProtectedID).defineColumnInteger().
			append(") from ").
			append(table.protectedID);
			
		final NextPKResultSetHandler handler = new NextPKResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static class NextPKResultSetHandler implements ResultSetHandler
	{
		int[] result;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new SQLException(NO_SUCH_ROW);
			
			final Object oLo = resultSet.getObject(1);
			if(oLo!=null)
			{
				result = new int[2];
				result[0] = convertSQLResult(oLo);
				final Object oHi = resultSet.getObject(2);
				result[1] = convertSQLResult(oHi);
			}
		}
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
		
		final CheckTypeColumnResultSetHandler handler = new CheckTypeColumnResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
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
		
		final CheckTypeColumnResultSetHandler handler = new CheckTypeColumnResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static class CheckTypeColumnResultSetHandler implements ResultSetHandler
	{
		int result = Integer.MIN_VALUE;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			if(!resultSet.next())
				throw new RuntimeException();
			
			result = resultSet.getInt(1);
		}
	}
	
	private static final String MIGRATION_COLUMN_VERSION_NAME = "v";
	private static final String MIGRATION_COLUMN_COMMENT_NAME = "c";
	
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

			public void putConnection(Connection connection) throws SQLException
			{
				connectionPool.put(connection);
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);
		
		if(migrationSupported)
		{
			final com.exedio.dsmf.Table table = new com.exedio.dsmf.Table(result, Table.MIGRATION_TABLE_NAME);
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_VERSION_NAME, dialect.getIntegerType(0, Integer.MAX_VALUE));
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_COMMENT_NAME, dialect.getStringType(100));
			new com.exedio.dsmf.UniqueConstraint(table, Table.MIGRATION_UNIQUE_CONSTRAINT_NAME, '(' + driver.protectName(MIGRATION_COLUMN_VERSION_NAME) + ')');
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
	
	int getActualMigrationVersion(final Connection connection)
	{
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select max(").
			append(driver.protectName(MIGRATION_COLUMN_VERSION_NAME)).defineColumnInteger().
			append(") from ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME));
			
		final ActualMigrationVersionResultSetHandler handler = new ActualMigrationVersionResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static class ActualMigrationVersionResultSetHandler implements ResultSetHandler
	{
		int result = -1;

		public void handle(final ResultSet resultSet) throws SQLException
		{
			resultSet.next();
			result = resultSet.getInt(1);
		}
	}
	
	Map<Integer, String> getMigrationLogs()
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
	
	private Map<Integer, String> getMigrationLogs(final Connection connection)
	{
		buildStage = false;

		final Statement bf = createStatement();
		bf.append("select ").
			append(driver.protectName(MIGRATION_COLUMN_VERSION_NAME)).defineColumnInteger().
			append(',').
			append(driver.protectName(MIGRATION_COLUMN_COMMENT_NAME)).defineColumnString().
			append(" from ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME));
			
		final MigrationLogsResultSetHandler handler = new MigrationLogsResultSetHandler();
		executeSQLQuery(connection, bf, handler, false, false);
		return Collections.unmodifiableMap(handler.result);
	}
	
	private static class MigrationLogsResultSetHandler implements ResultSetHandler
	{
		final HashMap<Integer, String> result = new HashMap<Integer, String>();

		public void handle(final ResultSet resultSet) throws SQLException
		{
			while(resultSet.next())
			{
				final int version = resultSet.getInt(1);
				final String comment = resultSet.getString(2);
				final String previous = result.put(version, comment);
				if(previous!=null)
					throw new RuntimeException("duplicate version " + version + ':' + previous + "/" + comment);
			}
		}
	}
	
	private void notifyMigration(final Connection connection, final int version, final Date date, final String comment, final boolean logToConsole)
	{
		assert migrationSupported;
		
		final String fullComment = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(date) + ':' + comment;
		if(logToConsole)
			System.out.println("Migrated to version " + version + ':' + fullComment);

		final Statement bf = createStatement();
		bf.append("insert into ").
			append(driver.protectName(Table.MIGRATION_TABLE_NAME)).
			append('(').
			append(driver.protectName(MIGRATION_COLUMN_VERSION_NAME)).
			append(',').
			append(driver.protectName(MIGRATION_COLUMN_COMMENT_NAME)).
			append(")values(").
			appendParameter(version).
			append(',').
			appendParameter(fullComment).
			append(')');
		
		executeSQLUpdate(connection, bf, 1);
	}
	
	void migrate(final int expectedVersion, final Migration[] migrations)
	{
		assert expectedVersion>=0 : expectedVersion;
		assert migrationSupported;

		final Pool<Connection> connectionPool = this.connectionPool;
		Connection con = null;
		java.sql.Statement stmt = null;
		try
		{
			con = connectionPool.get();
			con.setAutoCommit(true);
			
			final int actualVersion = getActualMigrationVersion(con);
			
			if(actualVersion>expectedVersion)
			{
				throw new IllegalArgumentException("cannot migrate backwards, expected " + expectedVersion + ", but was " + actualVersion);
			}
			else if(actualVersion<expectedVersion)
			{
				final Migration[] relevant = new Migration[expectedVersion-actualVersion];
				for(final Migration migration : migrations)
				{
					final int version = migration.version;
					if(version<=actualVersion || version>expectedVersion)
						continue; // irrelevant
					final int relevantIndex = version - actualVersion - 1;
					assert relevant[relevantIndex]==null : "there is more than one migration for version " + version + ": " + relevant[relevantIndex].comment + " and " + migration.comment;
					relevant[relevantIndex] = migration;
				}
				
				IntArrayList missing = null;
				for(int i = 0; i<relevant.length; i++)
				{
					if(relevant[i]==null)
					{
						if(missing==null)
							missing = new IntArrayList();
						
						missing.add(i + actualVersion + 1);
					}
				}
				if(missing!=null)
					throw new IllegalArgumentException(
							"no migration for versions " + missing.toString() +
							" on migration from " + actualVersion + " to " + expectedVersion);
				
				final Date date = new Date();
				stmt = con.createStatement();
				for(final Migration migration : relevant)
				{
					final String[] body = migration.body;
					final IntArrayList rowCounts = new IntArrayList(body.length);
					final ArrayList<Long> durations = new ArrayList<Long>(body.length);
					for(final String sql : body)
					{
						final long start = System.currentTimeMillis();
						try
						{
							rowCounts.add(stmt.executeUpdate(sql));
						}
						catch(SQLException e)
						{
							throw new SQLRuntimeException(e, sql);
						}
						durations.add(System.currentTimeMillis()-start);
					}
					
					notifyMigration(con, migration.version, date, migration.comment + ' ' + rowCounts + ' ' + durations, true);
				}
				stmt.close();
				stmt = null;
			}
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "migrate");
		}
		finally
		{
			if(stmt!=null)
			{
				try
				{
					stmt.close();
					stmt = null;
				}
				catch(SQLException ex)
				{
					throw new SQLRuntimeException(ex, "close");
				}
			}
			if(con!=null)
			{
				connectionPool.put(con);
				con = null;
			}
		}
	}

	
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	protected static void printMeta(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("------"+i+":"+metaData.getColumnName(i)+":"+metaData.getColumnType(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	protected static void printRow(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();;
		final int columnCount = metaData.getColumnCount();
		for(int i = 1; i<=columnCount; i++)
			System.out.println("----------"+i+":"+resultSet.getObject(i));
	}
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	private static final ResultSetHandler logHandler = new ResultSetHandler()
	{
		public void handle(final ResultSet resultSet) throws SQLException
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
		}
	};
	
	void close()
	{
		connectionPool.flush();
	}
	
	// listeners ------------------
	
	private static final DatabaseListener noopListener = new DatabaseListener()
	{
		public void load(Connection connection, PersistentState state)
		{/* DOES NOTHING */}
		
		public void search(Connection connection, Query query, boolean doCountOnly)
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

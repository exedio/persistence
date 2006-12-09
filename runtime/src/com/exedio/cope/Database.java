/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.sql.DatabaseMetaData;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bak.pcj.list.IntArrayList;
import bak.pcj.list.IntList;

import com.exedio.dsmf.Driver;
import com.exedio.dsmf.SQLRuntimeException;
import com.exedio.dsmf.Schema;

abstract class Database
{
	protected static final int TWOPOW8 = 1<<8;
	protected static final int TWOPOW16 = 1<<16;
	protected static final int TWOPOW24 = 1<<24;
	
	private static final String NO_SUCH_ROW = "no such row";
	
	private final ArrayList<Table> tables = new ArrayList<Table>();
	private final HashMap<String, UniqueConstraint> uniqueConstraintsByID = new HashMap<String, UniqueConstraint>();
	private boolean buildStage = true;
	final Driver driver;
	private final boolean migration;
	final boolean prepare;
	private final boolean log;
	private final boolean logStatementInfo;
	private final boolean butterflyPkSource;
	private final boolean fulltextIndex;
	final ConnectionPool connectionPool;
	private final java.util.Properties forcedNames;
	final java.util.Properties tableOptions;
	final LimitSupport limitSupport;
	final long blobLengthFactor;
	
	// probed on the initial connection
	final boolean supportsReadCommitted;
	final String databaseProductName;
	final String databaseProductVersion;
	final int databaseMajorVersion;
	final int databaseMinorVersion;
	final String driverName;
	final String driverVersion;
	final int driverMajorVersion;
	final int driverMinorVersion;
	
	final boolean oracle; // TODO remove
	
	protected Database(final Driver driver, final Properties properties, final boolean migration)
	{
		this.driver = driver;
		this.migration = migration;
		this.prepare = !properties.getDatabaseDontSupportPreparedStatements();
		this.log = properties.getDatabaseLog();
		this.logStatementInfo = properties.getDatabaseLogStatementInfo();
		this.butterflyPkSource = properties.getPkSourceButterfly();
		this.fulltextIndex = properties.getFulltextIndex();
		this.connectionPool = new ConnectionPool(new CopeConnectionFactory(properties), properties.getConnectionPoolIdleLimit(), properties.getConnectionPoolIdleInitial());
		this.forcedNames = properties.getDatabaseForcedNames();
		this.tableOptions = properties.getDatabaseTableOptions();
		this.limitSupport = properties.getDatabaseDontSupportLimit() ? LimitSupport.NONE : getLimitSupport();
		this.blobLengthFactor = getBlobLengthFactor();
		this.oracle = getClass().getName().equals("com.exedio.cope.OracleDatabase");
		
		//System.out.println("using database "+getClass());
		assert limitSupport!=null;
		
		Connection probeConnection = null;
		try
		{
			probeConnection = connectionPool.getConnection(true);
			
			final DatabaseMetaData dmd = probeConnection.getMetaData();
			
			supportsReadCommitted =
				!fakesSupportReadCommitted() &&
				dmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);

			databaseProductName = dmd.getDatabaseProductName();
			databaseProductVersion = dmd.getDatabaseProductVersion();
			databaseMajorVersion = dmd.getDatabaseMajorVersion();
			databaseMinorVersion = dmd.getDatabaseMinorVersion();
			driverName = dmd.getDriverName();
			driverVersion = dmd.getDriverVersion();
			driverMajorVersion = dmd.getDriverMajorVersion();
			driverMinorVersion = dmd.getDriverMinorVersion();
		}
		catch(SQLException e)
		{
			throw new SQLRuntimeException(e, "getMetaData");
		}
		finally
		{
			if(probeConnection!=null)
			{
				try
				{
					connectionPool.putConnection(probeConnection);
					probeConnection = null;
				}
				catch(SQLException e)
				{
					throw new SQLRuntimeException(e, "putConnection");
				}
			}
		}
	}
	
	public final Driver getDriver()
	{
		return driver;
	}
	
	public final java.util.Properties getTableOptions()
	{
		return tableOptions;
	}
	
	public final ConnectionPool getConnectionPool()
	{
		return connectionPool;
	}
	
	public final void addTable(final Table table)
	{
		if(!buildStage)
			throw new RuntimeException();
		tables.add(table);
	}
	
	public final void addUniqueConstraint(final String constraintID, final UniqueConstraint constraint)
	{
		if(!buildStage)
			throw new RuntimeException();

		final Object collision = uniqueConstraintsByID.put(constraintID, constraint);
		if(collision!=null)
			throw new RuntimeException("ambiguous unique constraint "+constraint+" trimmed to >"+constraintID+"< colliding with "+collision);
	}
	
	protected final Statement createStatement()
	{
		return createStatement(true);
	}
	
	protected final Statement createStatement(final boolean qualifyTable)
	{
		return new Statement(this, qualifyTable);
	}
	
	protected final Statement createStatement(final Query<? extends Object> query)
	{
		return new Statement(this, query);
	}
	
	public void createDatabase()
	{
		buildStage = false;
		
		makeSchema().create();
	}

	public void createDatabaseConstraints(final int mask)
	{
		buildStage = false;
		
		makeSchema().createConstraints(mask);
	}

	//private static int checkTableTime = 0;

	public void checkDatabase(final Connection connection)
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

	public void dropDatabase()
	{
		buildStage = false;

		makeSchema().drop();
	}
	
	public void dropDatabaseConstraints(final int mask)
	{
		buildStage = false;

		makeSchema().dropConstraints(mask);
	}
	
	public void tearDownDatabase()
	{
		buildStage = false;

		makeSchema().tearDown();
	}

	public void tearDownDatabaseConstraints(final int mask)
	{
		buildStage = false;

		makeSchema().tearDownConstraints(mask);
	}
	
	public void checkEmptyDatabase(final Connection connection)
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
	
	public final ArrayList<Object> search(final Connection connection, final Query<? extends Object> query, final boolean doCountOnly)
	{
		buildStage = false;

		listener.search(connection, query, doCountOnly);
		
		final int limitStart = query.limitStart;
		final int limitCount = query.limitCount;
		final boolean limitActive = limitStart>0 || limitCount!=Query.UNLIMITED_COUNT;
		final boolean distinct = query.distinct;

		final ArrayList<Join> queryJoins = query.joins;
		final Statement bf = createStatement(query);
		
		if(!doCountOnly && limitActive && limitSupport==LimitSupport.CLAUSES_AROUND)
			appendLimitClause(bf, limitStart, limitCount);
		
		bf.append("select");
		
		if(!doCountOnly && limitActive && limitSupport==LimitSupport.CLAUSE_AFTER_SELECT)
			appendLimitClause(bf, limitStart, limitCount);
		
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
			
			if(limitActive && limitSupport==LimitSupport.CLAUSE_AFTER_WHERE)
				appendLimitClause(bf, limitStart, limitCount);
		}

		if(!doCountOnly && limitActive && limitSupport==LimitSupport.CLAUSES_AROUND)
			appendLimitClause2(bf, limitStart, limitCount);
		
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
					
					if(limitStart>0 && limitSupport==LimitSupport.NONE)
					{
						// TODO: ResultSet.relative
						// Would like to use
						//    resultSet.relative(limitStart+1);
						// but this throws a java.sql.SQLException:
						// Invalid operation for forward only resultset : relative
						for(int i = limitStart; i>0; i--)
							resultSet.next();
					}
						
					int i = ((limitCount==Query.UNLIMITED_COUNT||(limitSupport!=LimitSupport.NONE)) ? Integer.MAX_VALUE : limitCount );
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
	
	private void log(final long start, final long end, final Statement statement)
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
		System.out.println(df.format(new Date(start)) + "  " + (end-start) + "ms:  " + statement.getText()+"   "+statement.parameters);
	}
	
	public void load(final Connection connection, final PersistentState state)
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

	public void store(
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
						bf.appendParameterBlob((BlobColumn)column, blobs.get(column));
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
							appendParameterBlob((BlobColumn)column, blobs.get(column));
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

	public void delete(final Connection connection, final Item item)
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

	public final byte[] load(final Connection connection, final BlobColumn column, final Item item)
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
			
		final LoadBlobResultSetHandler handler = new LoadBlobResultSetHandler(supportsGetBytes());
		executeSQLQuery(connection, bf, handler, false, false);
		return handler.result;
	}
	
	private static class LoadBlobResultSetHandler implements ResultSetHandler
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
	
	public final void load(final Connection connection, final BlobColumn column, final Item item, final OutputStream data, final DataField field)
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
				
				if(supportsBlobInResultSet())
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
	
	public final long loadLength(final Connection connection, final BlobColumn column, final Item item)
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
	
	private class LoadBlobLengthResultSetHandler implements ResultSetHandler
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
	
	public final void store(
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
			bf.appendParameterBlob(column, data, field, item);
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

	private final static int convertSQLResult(final Object sqlInteger)
	{
		// IMPLEMENTATION NOTE
		// Whether the returned object is an Integer, a Long or a BigDecimal,
		// depends on the database used and for oracle on whether
		// OracleStatement.defineColumnType is used or not, so we support all
		// here.
		return ((Number)sqlInteger).intValue();
	}

	//private static int timeExecuteQuery = 0;

	protected final StatementInfo executeSQLQuery(
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
			final boolean log = !explain && (this.log || this.logStatementInfo || makeStatementInfo);
			final String sqlText = statement.getText();
			final long logStart = log ? System.currentTimeMillis() : 0;
			final long logPrepared;
			final long logExecuted;
			
			if(!prepare)
			{
				sqlStatement = connection.createStatement();

				defineColumnTypes(statement.columnTypes, sqlStatement);
				
				logPrepared = log ? System.currentTimeMillis() : 0;
				resultSet = sqlStatement.executeQuery(sqlText);
				logExecuted = log ? System.currentTimeMillis() : 0;
				resultSetHandler.handle(resultSet);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(Iterator i = statement.parameters.iterator(); i.hasNext(); parameterIndex++)
					setObject(sqlText, prepared, parameterIndex, i.next());

				defineColumnTypes(statement.columnTypes, sqlStatement);
				
				logPrepared = log ? System.currentTimeMillis() : 0;
				resultSet = prepared.executeQuery();
				logExecuted = log ? System.currentTimeMillis() : 0;
				resultSetHandler.handle(resultSet);
			}
			final long logResultRead = log ? System.currentTimeMillis() : 0;
			
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

			final long logEnd = log ? System.currentTimeMillis() : 0;
			
			if(!explain && this.log)
				log(logStart, logEnd, statement);
			
			final StatementInfo statementInfo =
				(!explain && (this.logStatementInfo || makeStatementInfo))
				? makeStatementInfo(statement, connection, logStart, logPrepared, logExecuted, logResultRead, logEnd)
				: null;
			
			if(!explain && this.logStatementInfo)
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
	
	private final void executeSQLUpdate(final Connection connection, final Statement statement, final int expectedRows)
			throws UniqueViolationException
	{
		executeSQLUpdate(connection, statement, expectedRows, null);
	}

	private final void executeSQLUpdate(
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
			final long logStart = log ? System.currentTimeMillis() : 0;
			final int rows;
			
			if(threatenedUniqueConstraints!=null && threatenedUniqueConstraints.size()>0 && needsSavepoint())
				savepoint = connection.setSavepoint();
			
			if(!prepare)
			{
				sqlStatement = connection.createStatement();
				rows = sqlStatement.executeUpdate(sqlText);
			}
			else
			{
				final PreparedStatement prepared = connection.prepareStatement(sqlText);
				sqlStatement = prepared;
				int parameterIndex = 1;
				for(Iterator i = statement.parameters.iterator(); i.hasNext(); parameterIndex++)
					setObject(sqlText, prepared, parameterIndex, i.next());
				rows = prepared.executeUpdate();
			}
			
			final long logEnd = log ? System.currentTimeMillis() : 0;

			if(log)
				log(logStart, logEnd, statement);

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
	
	private static final void setObject(String s, final PreparedStatement statement, final int parameterIndex, final Object value)
		throws SQLException
	{
		//try{
		statement.setObject(parameterIndex, value);
		//}catch(SQLException e){ throw new SQLRuntimeException(e, "setObject("+parameterIndex+","+value+")"+s); }
	}
	
	protected static final String EXPLAIN_PLAN = "explain plan";
	
	protected StatementInfo makeStatementInfo(
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
			
		return result;
	}
	
	protected abstract String extractUniqueConstraintName(SQLException e);
	
	protected final static String ANY_CONSTRAINT = "--ANY--";

	private final UniqueViolationException wrapException(
			final SQLException e,
			final List<UniqueConstraint> threatenedUniqueConstraints)
	{
		final String uniqueConstraintID = extractUniqueConstraintName(e);
		if(uniqueConstraintID!=null)
		{
			final UniqueConstraint constraint;
			if(ANY_CONSTRAINT.equals(uniqueConstraintID))
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
	protected static final String trimString(final String longString, final int maxLength)
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
	
	public String makeName(final String longName)
	{
		return makeName(null, longName);
	}

	public String makeName(final String prefix, final String longName)
	{
		final String query = prefix==null ? longName : prefix+'.'+longName;
		final String forcedName = forcedNames.getProperty(query);
		//System.out.println("---------"+query+"--"+forcedName);
		if(forcedName!=null)
			return forcedName;
		
		return trimString(longName, 25);
	}

	public boolean supportsGetBytes()
	{
		return true;
	}

	public boolean supportsBlobInResultSet()
	{
		return true;
	}

	public boolean supportsEmptyStrings()
	{
		return true;
	}

	public boolean fakesSupportReadCommitted()
	{
		return false;
	}

	/**
	 * Specifies the factor,
	 * the length function of blob columns is wrong.
	 */
	public int getBlobLengthFactor()
	{
		return 1;
	}

	/**
	 * By overriding this method subclasses can enable the use of save points.
	 * Some databases cannot recover from constraint violations in
	 * the same transaction without a little help,
	 * they need a save point set before the modification, that can be
	 * recovered manually.
	 */
	boolean needsSavepoint()
	{
		return false;
	}

	public abstract String getIntegerType(long minimum, long maximum);
	public abstract String getDoubleType();
	public abstract String getStringType(int maxLength);
	public abstract String getDayType();
	
	/**
	 * Returns a column type suitable for storing timestamps
	 * with milliseconds resolution.
	 * This method may return null,
	 * if the database does not support such a column type.
	 * The framework will then fall back to store the number of milliseconds.
	 */
	public abstract String getDateTimestampType();
	public abstract String getBlobType(long maximumLength);
	
	abstract LimitSupport getLimitSupport();
	
	static enum LimitSupport
	{
		NONE,
		CLAUSE_AFTER_SELECT,
		CLAUSE_AFTER_WHERE,
		CLAUSES_AROUND;
	}

	/**
	 * Appends a clause to the statement causing the database limiting the query result.
	 * This method is never called for <tt>start==0 && count=={@link Query#UNLIMITED_COUNT}</tt>.
	 * NOTE: Don't forget the space before the keyword 'limit'!
	 * @param start the number of rows to be skipped
	 *        or zero, if no rows to be skipped.
	 *        Is never negative.
	 * @param count the number of rows to be returned
	 *        or {@link Query#UNLIMITED_COUNT} if all rows to be returned.
	 *        Is always positive (greater zero).
	 */
	abstract void appendLimitClause(Statement bf, int start, int count);
	
	/**
	 * Same as {@link #appendLimitClause(Statement, int, int)}.
	 * Is used for {@link LimitSupport#CLAUSES_AROUND} only,
	 * for the postfix.
	 */
	abstract void appendLimitClause2(Statement bf, int start, int count);

	abstract void appendMatchClauseFullTextIndex(Statement bf, StringFunction function, String value);
	
	/**
	 * Search full text.
	 */
	public final void appendMatchClause(final Statement bf, final StringFunction function, final String value)
	{
		if(fulltextIndex)
			appendMatchClauseFullTextIndex(bf, function, value);
		else
			appendMatchClauseByLike(bf, function, value);
	}
	
	protected final void appendMatchClauseByLike(final Statement bf, final StringFunction function, final String value)
	{
		bf.append(function, (Join)null).
			append(" like ").
			appendParameter(function, '%'+value+'%');
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


	public final PkSource makePkSource(final Table table)
	{
		return butterflyPkSource ? (PkSource)new ButterflyPkSource(table) : new SequentialPkSource(table);
	}
	
	public final int[] getMinMaxPK(final Connection connection, final Table table)
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
	
	final int checkTypeColumn(final Connection connection, final Type type)
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
	
	final int checkTypeColumn(final Connection connection, final ItemField field)
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
	
	public final Schema makeSchema()
	{
		final Schema result = new Schema(driver, connectionPool);
		for(final Table t : tables)
			t.makeSchema(result);
		
		if(migration)
		{
			final com.exedio.dsmf.Table table = new com.exedio.dsmf.Table(result, Table.MIGRATION_TABLE_NAME);
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_VERSION_NAME, getIntegerType(0, Integer.MAX_VALUE));
			new com.exedio.dsmf.Column(table, MIGRATION_COLUMN_COMMENT_NAME, getStringType(100));
			new com.exedio.dsmf.UniqueConstraint(table, Table.MIGRATION_TABLE_NAME, '(' + driver.protectName(MIGRATION_COLUMN_VERSION_NAME) + ')');
		}
		
		completeSchema(result);
		return result;
	}
	
	protected void completeSchema(final Schema schema)
	{
		// empty default implementation
	}
	
	public final Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema();
		result.verify();
		return result;
	}
	
	final void migrate(final int expectedVersion, final MigrationStep[] steps)
	{
		assert expectedVersion>=0 : expectedVersion;
		assert migration;

		final ConnectionPool connectionPool = this.connectionPool;
		Connection con = null;
		java.sql.Statement stmt = null;
		ResultSet rs = null;
		try
		{
			con = connectionPool.getConnection(true);
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select max("+driver.protectName(MIGRATION_COLUMN_VERSION_NAME)+") " +
					"from "+driver.protectName(Table.MIGRATION_TABLE_NAME));
			rs.next();
			final int actualVersion = rs.getInt(1);
			rs.close();
			rs = null;
			
			if(actualVersion>expectedVersion)
			{
				throw new IllegalArgumentException("cannot migrate backwards, expected " + expectedVersion + ", but was " + actualVersion);
			}
			else if(actualVersion<expectedVersion)
			{
				final MigrationStep[] relevantSteps = new MigrationStep[expectedVersion-actualVersion];
				for(final MigrationStep step : steps)
				{
					final int version = step.version;
					if(version<=actualVersion || version>expectedVersion)
						continue; // irrelevant
					final int relevantIndex = version - actualVersion - 1;
					if(relevantSteps[relevantIndex]!=null)
						throw new IllegalArgumentException("there is more than one migration step for version " + version + ": " + relevantSteps[relevantIndex].comment + " and " + step.comment);
					relevantSteps[relevantIndex] = step;
				}
				
				IntArrayList missingSteps = null;
				for(int i = 0; i<relevantSteps.length; i++)
				{
					if(relevantSteps[i]==null)
					{
						if(missingSteps==null)
							missingSteps = new IntArrayList();
						
						missingSteps.add(i - actualVersion + 1);
					}
				}
				if(missingSteps!=null)
					throw new IllegalArgumentException(
							"no migration step for versions " + missingSteps.toString() +
							" on migration from " + actualVersion + " to " + expectedVersion);
				
				for(final MigrationStep step : relevantSteps)
				{
					final IntArrayList rowCounts = new IntArrayList(step.sql.length);
					for(String sql : step.sql)
						rowCounts.add(stmt.executeUpdate(sql));
					System.out.println(step.comment + " affected " + rowCounts + " rows.");// TODO insert into version table
				}
			}
		}
		catch(SQLException e)
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
					rs = null;
				}
				catch(SQLException ex)
				{
					throw new SQLRuntimeException(ex, "close");
				}
			}
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
				try
				{
					connectionPool.putConnection(con);
					con = null;
				}
				catch(SQLException ex)
				{
					throw new SQLRuntimeException(ex, "close");
				}
			}
		}
	}

	
	
	/**
	 * @deprecated for debugging only, should never be used in committed code
	 */
	@Deprecated
	protected static final void printMeta(final ResultSet resultSet) throws SQLException
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
	protected static final void printRow(final ResultSet resultSet) throws SQLException
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
	static final ResultSetHandler logHandler = new ResultSetHandler()
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
	
	public boolean isDefiningColumnTypes()
	{
		return false;
	}
	
	public void defineColumnTypes(IntList columnTypes, java.sql.Statement statement)
			throws SQLException
	{
		// default implementation does nothing, may be overwritten by subclasses
	}
	
	protected void close()
	{
		getConnectionPool().flush();
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
	
	public DatabaseListener setListener(DatabaseListener listener)
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

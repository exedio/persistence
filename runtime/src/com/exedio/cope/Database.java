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

import static com.exedio.cope.Executor.NO_SUCH_ROW;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.cope.info.SequenceInfo;
import com.exedio.dsmf.ConnectionProvider;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

final class Database
{
	private final NameTrimmer nameTrimmer = new NameTrimmer(25);
	private final ArrayList<Table> tables = new ArrayList<Table>();
	private final HashMap<String, UniqueConstraint> uniqueConstraintsByID = new HashMap<String, UniqueConstraint>();
	private final ArrayList<Sequence> sequences = new ArrayList<Sequence>();
	private boolean buildStage = true;
	final com.exedio.dsmf.Dialect dsmfDialect;
	final DialectParameters dialectParameters;
	final Dialect dialect;
	private Revisions revisions; // TODO make final
	private final ConnectionPool connectionPool;
	final Executor executor;
	final boolean mysqlLowerCaseTableNames;
	final java.util.Properties tableOptions;
	final Dialect.LimitSupport limitSupport;
	final long blobLengthFactor;
	final boolean supportsReadCommitted;
	final boolean supportsSequences;
	final boolean cluster;
	
	final boolean oracle; // TODO remove
	
	Database(
			final com.exedio.dsmf.Dialect dsmfDialect,
			final DialectParameters dialectParameters,
			final Dialect dialect,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Revisions revisions)
	{
		final ConnectProperties properties = dialectParameters.properties;
		this.dsmfDialect = dsmfDialect;
		this.dialectParameters = dialectParameters;
		this.dialect = dialect;
		this.revisions = revisions;
		this.connectionPool = connectionPool;
		this.executor = executor;
		this.mysqlLowerCaseTableNames = properties.getMysqlLowerCaseTableNames();
		this.tableOptions = properties.getDatabaseTableOptions();
		this.limitSupport = properties.getDatabaseDontSupportLimit() ? Dialect.LimitSupport.NONE : dialect.getLimitSupport();
		this.blobLengthFactor = dialect.getBlobLengthFactor();
		this.cluster = properties.cluster.getBooleanValue();
		this.oracle = dialect.getClass().getName().equals("com.exedio.cope.OracleDialect");
		
		//System.out.println("using database "+getClass());
		assert limitSupport!=null;
		
		this.supportsReadCommitted =
			!dialect.fakesSupportReadCommitted() &&
			dialectParameters.supportsTransactionIsolationLevel;
		this.supportsSequences = dsmfDialect.supportsSequences();
	}
	
	Map<String, String> revisionEnvironment()
	{
		final HashMap<String, String> store = new HashMap<String, String>();
		
		try
		{
			store.put("hostname", InetAddress.getLocalHost().getHostName());
		}
		catch(UnknownHostException e)
		{
			// do not put in hostname
		}
		
		store.put("jdbc.url",  dialectParameters.properties.getDatabaseUrl());
		store.put("jdbc.user", dialectParameters.properties.getDatabaseUser());
		store.put("database.name",    dialectParameters.databaseProductName);
		store.put("database.version", dialectParameters.databaseProductVersion);
		store.put("database.version.major", String.valueOf(dialectParameters.databaseMajorVersion));
		store.put("database.version.minor", String.valueOf(dialectParameters.databaseMinorVersion));
		store.put("driver.name",    dialectParameters.driverName);
		store.put("driver.version", dialectParameters.driverVersion);
		store.put("driver.version.major", String.valueOf(dialectParameters.driverMajorVersion));
		store.put("driver.version.minor", String.valueOf(dialectParameters.driverMinorVersion));
		
		return store;
	}
	
	java.util.Properties getTableOptions()
	{
		return tableOptions;
	}
	
	SequenceImpl newSequenceImpl(final int start, final IntegerColumn column)
	{
		return
			cluster
			? new SequenceImplSequence(column, start, connectionPool, this)
			: new SequenceImplMax(column, start, connectionPool);
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
	
	void addSequence(final Sequence sequence)
	{
		if(!buildStage)
			throw new RuntimeException();
		sequences.add(sequence);
	}
	
	public List<SequenceInfo> getSequenceInfo()
	{
		final ArrayList<SequenceInfo> result = new ArrayList<SequenceInfo>(sequences.size());
		for(final Sequence sequence : sequences)
			result.add(sequence.getInfo());
		return Collections.unmodifiableList(result);
	}
	
	void createSchema()
	{
		buildStage = false;
		
		makeSchema().create();
		
		if(revisions!=null)
			revisions.inserCreate(connectionPool, executor, revisionEnvironment());
	}

	void createSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;
		
		makeSchema().createConstraints(types);
	}

	//private static int checkTableTime = 0;

	void checkSchema(final Connection connection)
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
			
			final Statement bf = executor.newStatement(true);
			bf.append("select count(*) from ");
			boolean first = true;
	
			for(final Table table : tableChunk)
			{
				if(first)
					first = false;
				else
					bf.append(',');
	
				bf.append(table.quotedID);
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
					appendParameter(PK.NaPK);
				
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
			executor.query(connection, bf, null, false, new ResultSetHandler<Void>()
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

	void dropSchema()
	{
		buildStage = false;

		flushSequences();
		makeSchema().drop();
	}
	
	void dropSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema().dropConstraints(types);
	}
	
	void tearDownSchema()
	{
		buildStage = false;

		makeSchema().tearDown();
	}

	void tearDownSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema().tearDownConstraints(types);
	}
	
	void checkEmptySchema(final Connection connection)
	{
		buildStage = false;

		//final long time = System.currentTimeMillis();
		for(final Table table : tables)
		{
			final int count = table.count(connection, executor);
			if(count>0)
				throw new RuntimeException("there are "+count+" items left for table "+table.id);
		}
		
		// NOTICE
		// The following flushSequences() makes CopeTest work again, so that sequences do start
		// from their initial value for each test. This is rather a hack, so we should deprecate
		// CopeTest in favor of CopeModelTest in the future.
		flushSequences();
		
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

		testListener.search(connection, query, totalOnly);
		
		final int offset = query.offset;
		final int limit = query.limit;
		final boolean limitActive = offset>0 || limit!=Query.UNLIMITED;
		final boolean distinct = query.distinct;
		if(offset<0)
			throw new RuntimeException();

		final ArrayList<Join> queryJoins = query.joins;
		final Statement bf = executor.newStatement(query);
		
		if (totalOnly && distinct)
		{
			bf.append("select count(*) from ( ");
		}
		
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
			if(distinct)
				bf.append("distinct ");
			
			final Holder<Column> selectColumn = new Holder<Column>();
			final Holder<Type  > selectType   = new Holder<Type  >();
			for(int i = 0; i<selects.length; i++)
			{
				if(i>0)
					bf.append(',');
				
				selectColumn.value = null;
				selectType  .value = null;
				bf.appendSelect(selects[i], null, selectColumn, selectType);
				selectColumns[i] = selectColumn.value;
				selectTypes  [i] = selectType  .value;
			}
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
			
			if(limitActive)
			{
				switch(limitSupport)
				{
					case CLAUSE_AFTER_WHERE: dialect.appendLimitClause (bf, offset, limit); break;
					case CLAUSES_AROUND:     dialect.appendLimitClause2(bf, offset, limit); break;
					case CLAUSE_AFTER_SELECT:
					case NONE:
						break;
				}
			}
		}
		
		final Model model = query.model;
		final ArrayList<Object> result = new ArrayList<Object>();
		
		if(totalOnly && distinct)
		{
			bf.append(" )");
			if (dialect.subqueryRequiresAlias())
			{
				bf.append(" as cope_total_distinct");
			}
		}
		
		//System.out.println(bf.toString());

		executor.query(connection, bf, queryInfos, false, new ResultSetHandler<Void>()
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
							if(select0 instanceof BindFunction)
								select0 = ((BindFunction)select0).function;
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
								final Type type = selectTypes[selectIndex];
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
								if(!PK.isValid(pkPrimitive))
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
	
	WrittenState load(final Connection connection, final Item item)
	{
		buildStage = false;
		
		final Type type = item.type;

		testListener.load(connection, item);
		
		final Statement bf = executor.newStatement(type.supertype!=null);
		bf.append("select ");

		boolean first = true;
		for(Type superType = type; superType!=null; superType = superType.supertype)
		{
			for(final Column column : superType.getTable().getColumns())
			{
				if(!(column instanceof BlobColumn))
				{
					if(first)
						first = false;
					else
						bf.append(',');

					bf.append(column);
				}
			}
		}
		
		if(first)
		{
			// no columns in type
			bf.appendPK(type, (Join)null);
		}

		bf.append(" from ");
		first = true;
		for(Type superType = type; superType!=null; superType = superType.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(superType.getTable().quotedID);
		}
			
		bf.append(" where ");
		first = true;
		for(Type superType = type; superType!=null; superType = superType.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(" and ");

			bf.appendPK(superType, (Join)null).
				append('=').
				appendParameter(item.pk).
				appendTypeCheck(superType.getTable(), type); // Here this also checks additionally for Model#getItem, that the item has the type given in the ID.
		}
			
		//System.out.println(bf.toString());
		final Row row = new Row();
		executor.query(connection, bf, null, false, new ResultSetHandler<Void>()
		{
			public Void handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new NoSuchItemException(item);

				int columnIndex = 1;
				for(Type superType = type; superType!=null; superType = superType.supertype)
				{
					for(final Column column : superType.getTable().getColumns())
					{
						if(!(column instanceof BlobColumn))
							column.load(resultSet, columnIndex++, row);
					}
				}
				
				return null;
			}
		});
		
		return new WrittenState(item, row);
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

		final Statement bf = executor.newStatement();
		final StringColumn typeColumn = table.typeColumn;
		if(present)
		{
			bf.append("update ").
				append(table.quotedID).
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
					
					bf.append(column.quotedID).
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
				append(table.primaryKey.quotedID).
				append('=').
				appendParameter(state.pk).
				appendTypeCheck(table, state.type);
		}
		else
		{
			bf.append("insert into ").
				append(table.quotedID).
				append("(").
				append(table.primaryKey.quotedID);
			
			if(typeColumn!=null)
			{
				bf.append(',').
					append(typeColumn.quotedID);
			}

			for(final Column column : columns)
			{
				if(!(column instanceof BlobColumn) || blobs.containsKey(column))
				{
					bf.append(',').
						append(column.quotedID);
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
		executor.update(connection, bf, true);
	}

	void delete(final Connection connection, final Item item)
	{
		buildStage = false;
		final Type type = item.type;
		final int pk = item.pk;

		for(Type currentType = type; currentType!=null; currentType = currentType.supertype)
		{
			final Table currentTable = currentType.getTable();
			final Statement bf = executor.newStatement();
			bf.append("delete from ").
				append(currentTable.quotedID).
				append(" where ").
				append(currentTable.primaryKey.quotedID).
				append('=').
				appendParameter(pk);

			//System.out.println("deleting "+bf.toString());

			executor.update(connection, bf, true);
		}
	}

	String makeName(final String longName)
	{
		return nameTrimmer.trimString(longName);
	}
	
	Schema makeSchema()
	{
		final ConnectionPool connectionPool = this.connectionPool;
		final Schema result = new Schema(dsmfDialect, new ConnectionProvider()
		{
			public Connection getConnection()
			{
				return connectionPool.get(true);
			}

			public void putConnection(Connection connection)
			{
				connectionPool.put(connection);
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);
		
		if(revisions!=null)
			revisions.makeSchema(result, dialect);
		for(final Sequence sequence : sequences)
			sequence.makeSchema(result);
		
		dialect.completeSchema(result);
		return result;
	}
	
	Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema();
		result.verify();
		return result;
	}
	
	void setRevisions(final Revisions revisions) // for test only, not for productive use !!!
	{
		this.revisions = revisions;
	}
	
	void close()
	{
		connectionPool.flush();
	}
	
	void flushSequences()
	{
		for(final Sequence sequence : sequences)
			sequence.flush();
	}
	
	// listeners ------------------
	
	private static final TestDatabaseListener noopTestListener = new TestDatabaseListener()
	{
		public void load(Connection connection, Item item)
		{/* DOES NOTHING */}
		
		public void search(Connection connection, Query query, boolean totalOnly)
		{/* DOES NOTHING */}
	};

	private TestDatabaseListener testListener = noopTestListener;
	private final Object testListenerLock = new Object();
	
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

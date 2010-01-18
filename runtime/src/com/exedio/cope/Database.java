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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		this.blobLengthFactor = dialect.getBlobLengthFactor();
		this.cluster = properties.cluster.getBooleanValue();
		this.oracle = dialect.getClass().getName().equals("com.exedio.cope.OracleDialect");
		
		//System.out.println("using database "+getClass());
		
		this.supportsReadCommitted =
			!dialect.fakesSupportReadCommitted() &&
			dialectParameters.supportsTransactionIsolationLevel;
		this.supportsSequences = dsmfDialect.supportsSequences();
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
	
	List<SequenceInfo> getSequenceInfo()
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
			revisions.inserCreate(connectionPool, executor, dialectParameters.getRevisionEnvironment());
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
	
	WrittenState load(final Connection connection, final Item item)
	{
		buildStage = false;
		
		final Type type = item.type;

		executor.testListener().load(connection, item);
		
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
	
	void flushSequences()
	{
		for(final Sequence sequence : sequences)
			sequence.flush();
	}
}

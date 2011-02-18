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
import java.util.List;
import java.util.Map;

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.ConnectionProvider;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;

final class Database
{
	private final Trimmer nameTrimmer = new Trimmer(25);
	private final ArrayList<Table> tables = new ArrayList<Table>();
	private final ArrayList<Sequence> sequences = new ArrayList<Sequence>();
	private boolean buildStage = true;
	final ConnectProperties properties;
	final com.exedio.dsmf.Dialect dsmfDialect;
	final DialectParameters dialectParameters;
	final Dialect dialect;
	private final Revisions revisions;
	private final ConnectionPool connectionPool;
	final Executor executor;

	final boolean hsqldb; // TODO remove
	final boolean oracle; // TODO remove

	Database(
			final com.exedio.dsmf.Dialect dsmfDialect,
			final DialectParameters dialectParameters,
			final Dialect dialect,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Revisions revisions)
	{
		this.properties = dialectParameters.properties;
		this.dsmfDialect = dsmfDialect;
		this.dialectParameters = dialectParameters;
		this.dialect = dialect;
		this.revisions = revisions;
		this.connectionPool = connectionPool;
		this.executor = executor;
		this.hsqldb = dialect.getClass().getName().equals("com.exedio.cope.HsqldbDialect");
		this.oracle = dialect.getClass().getName().equals("com.exedio.cope.OracleDialect");

		//System.out.println("using database "+getClass());
	}

	SequenceImpl newSequenceImpl(final int start, final IntegerColumn column)
	{
		return
			properties.cluster.booleanValue()
			? new SequenceImplSequence(column, start, connectionPool, this)
			: new SequenceImplMax(column, start, connectionPool);
	}

	void addTable(final Table table)
	{
		if(!buildStage)
			throw new RuntimeException();
		tables.add(table);
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

		makeSchema(true).create();

		if(revisions!=null)
			revisions.insertCreate(properties, connectionPool, executor, dialectParameters.getRevisionEnvironment());
	}

	void createSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema(true).createConstraints(types);
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

				final Column typeColumn = table.typeColumn;
				if(typeColumn!=null)
				{
					if(first)
						first = false;
					else
						bf.append(" and ");

					bf.append(typeColumn).
						append('=').
						appendParameter("x");
				}

				final Column updateCounter = table.updateCounter;
				if(updateCounter!=null)
				{
					if(first)
						first = false;
					else
						bf.append(" and ");

					bf.append(updateCounter).
						append('=').
						appendParameter(Integer.MIN_VALUE);
				}

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
		makeSchema(true).drop();
	}

	void dropSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema(true).dropConstraints(types);
	}

	void tearDownSchema()
	{
		buildStage = false;

		makeSchema(true).tearDown();
	}

	void tearDownSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema(true).tearDownConstraints(types);
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
			final Table table = superType.getTable();

			final IntegerColumn updateCounter = table.updateCounter;
			if(updateCounter!=null)
			{
				if(first)
					first = false;
				else
					bf.append(',');

				bf.append(updateCounter);
			}

			for(final Column column : table.getColumns())
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

		final WrittenState result = executor.query(connection, bf, null, false, new ResultSetHandler<WrittenState>()
		{
			public WrittenState handle(final ResultSet resultSet) throws SQLException
			{
				if(!resultSet.next())
					throw new NoSuchItemException(item);

				final Row row = new Row();
				int columnIndex = 1;
				int updateCount = Integer.MIN_VALUE;
				for(Type superType = type; superType!=null; superType = superType.supertype)
				{
					final Table table = superType.getTable();

					final IntegerColumn updateCounter = table.updateCounter;
					if(updateCounter!=null)
					{
						final int value = resultSet.getInt(columnIndex++);
						if(updateCount==Integer.MIN_VALUE)
						{
							if(value<0)
								throw new RuntimeException("invalid update counter for row " + item.pk + " in table " + table.id + ": " + value);
							updateCount = value;
						}
						else
						{
							if(updateCount!=value)
								throw new RuntimeException(
										"inconsistent update counter for row " + item.pk + " in table " + table.id +
										" compared to " + type.getTable().id + ": " +
										+ value + '/' + updateCount);
						}
					}

					for(final Column column : table.getColumns())
					{
						if(!(column instanceof BlobColumn))
							column.load(resultSet, columnIndex++, row);
					}
				}

				return new WrittenState(item, row, updateCount);
			}
		});

		return result;
	}

	void store(
			final Connection connection,
			final State state,
			final boolean present,
			final boolean incrementUpdateCounter,
			final Map<BlobColumn, byte[]> blobs)
	{
		store(connection, state, present, incrementUpdateCounter, blobs, state.type);
	}

	private void store(
			final Connection connection,
			final State state,
			final boolean present,
			final boolean incrementUpdateCounter,
			final Map<BlobColumn, byte[]> blobs,
			final Type<?> type)
	{
		buildStage = false;
		assert present || incrementUpdateCounter;

		final Type supertype = type.supertype;
		if(supertype!=null)
			store(connection, state, present, incrementUpdateCounter, blobs, supertype);

		final Table table = type.getTable();

		final List<Column> columns = table.getColumns();

		final Statement bf = executor.newStatement();
		final StringColumn typeColumn = table.typeColumn;
		final IntegerColumn updateCounter = incrementUpdateCounter ? table.updateCounter : null;
		if(present)
		{
			bf.append("update ").
				append(table.quotedID).
				append(" set ");

			boolean first = true;

			if(updateCounter!=null)
			{
				bf.append(updateCounter.quotedID).
					append('=').
					appendParameter(updateCounter, state.updateCount+1);
				first = false;
			}

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

			if(updateCounter!=null)
			{
				bf.append(" and ").
					append(updateCounter.quotedID).
					append('=').
					appendParameter(state.updateCount);
			}
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

			if(updateCounter!=null)
			{
				bf.append(',').
					append(updateCounter.quotedID);
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

			if(updateCounter!=null)
			{
				bf.append(',').
					appendParameter(state.updateCount+1); // is initialized to -1
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
		executor.updateStrict(connection, bf);
	}

	String makeName(final String longName)
	{
		return nameTrimmer.trimString(longName);
	}

	Schema makeSchema(final boolean withRevisions)
	{
		final ConnectionPool connectionPool = this.connectionPool;
		final Schema result = new Schema(dsmfDialect, new ConnectionProvider()
		{
			public Connection getConnection()
			{
				return connectionPool.get(true);
			}

			public void putConnection(final Connection connection)
			{
				connectionPool.put(connection);
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);

		if(withRevisions && revisions!=null)
			revisions.makeSchema(result, properties, dialect);
		for(final Sequence sequence : sequences)
			sequence.makeSchema(result);

		dialect.completeSchema(result);
		return result;
	}

	Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema(true);
		result.verify();
		return result;
	}

	void flushSequences()
	{
		for(final Sequence sequence : sequences)
			sequence.flush();
	}
}

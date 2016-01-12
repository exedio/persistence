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

import com.exedio.cope.Executor.ResultSetHandler;
import com.exedio.dsmf.ConnectionProvider;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Database
{
	private final Trimmer nameTrimmer = new Trimmer(25);
	private final ArrayList<Table> tables = new ArrayList<>();
	private final ArrayList<SequenceX> sequences = new ArrayList<>();
	private boolean buildStage = true;
	final ConnectProperties properties;
	final com.exedio.dsmf.Dialect dsmfDialect;
	final Probe probe;
	final Dialect dialect;
	final Transactions transactions;
	private final RevisionsConnect revisions;
	private final ConnectionPool connectionPool;
	final Executor executor;

	final boolean hsqldb; // TODO remove
	final boolean oracle; // TODO remove

	Database(
			final com.exedio.dsmf.Dialect dsmfDialect,
			final Probe probe,
			final Dialect dialect,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Transactions transactions,
			final RevisionsConnect revisions)
	{
		this.properties = probe.properties;
		this.dsmfDialect = dsmfDialect;
		this.probe = probe;
		this.dialect = dialect;
		this.transactions = transactions;
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
			properties.primaryKeyGenerator.newSequenceImpl(column, start, connectionPool, this);
	}

	SequenceImpl newSequenceImplCluster(final int start, final String name)
	{
		return
			new SequenceImplSequence(name, start, properties, connectionPool, executor, dsmfDialect);
	}

	void addTable(final Table table)
	{
		if(!buildStage)
			throw new RuntimeException();
		tables.add(table);
	}

	void addSequence(final SequenceX sequence)
	{
		if(!buildStage)
			throw new RuntimeException();
		sequences.add(sequence);
	}

	List<SequenceInfo> getSequenceInfo()
	{
		final ArrayList<SequenceInfo> result = new ArrayList<>(sequences.size());
		for(final SequenceX sequence : sequences)
			result.add(sequence.getInfo());
		return Collections.unmodifiableList(result);
	}

	void createSchema()
	{
		buildStage = false;

		makeSchema().create();

		if(revisions!=null)
			revisions.get().insertCreate(properties, connectionPool, executor, probe.getRevisionEnvironment());

		for(final Table table : tables)
			table.knownToBeEmptyForTest = true;
		for(final SequenceX sequence : sequences)
			sequence.setKnownToBeEmptyForTest();
	}

	void createSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		buildStage = false;

		makeSchema().createConstraints(types);
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

		final StringBuilder bf = new StringBuilder();
		bf.append("SELECT t,c FROM(");
		int n = 0;
		for(final Table table : tables)
		{
			if(n>0)
				bf.append("union");

			bf.append("(SELECT '").
				append(table.id).
				append("' t, COUNT(*) c, ").
				append(n++).
				append(" n FROM ").
				append(table.quotedID).
				append(')');
		}
		bf.append(") b WHERE c>0 ORDER BY n");

		final String message = Executor.query(connection, bf.toString(), new ResultSetHandler<String>()
		{
			public String handle(final ResultSet resultSet) throws SQLException
			{
				StringBuilder message = null;
				while(resultSet.next())
				{
					if(message==null)
						message = new StringBuilder("schema not empty: ");
					else
						message.append(", ");

					message.
						append(resultSet.getString(1).trim()). // trim needed for hsqldb
						append(':').
						append(resultSet.getInt(2));
				}
				return message!=null ? message.toString() : null;
			}

		});
		if(message!=null)
			throw new IllegalStateException(message);

		// NOTICE
		// The following flushSequences() makes CopeTest work again, so that sequences do start
		// from their initial value for each test. This is rather a hack, so we should deprecate
		// CopeTest in favor of CopeModelTest in the future.
		flushSequences();
	}

	WrittenState load(final Connection connection, final Item item)
	{
		buildStage = false;

		final Type<?> type = item.type;

		executor.testListener().load(connection, item);

		final Statement bf = executor.newStatement(type.supertype!=null);
		bf.append("SELECT ");

		boolean first = true;
		for(Type<?> currentType = type; currentType!=null; currentType = currentType.supertype)
		{
			final Table table = currentType.getTable();

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

		bf.append(" FROM ");
		first = true;
		for(Type<?> superType = type; superType!=null; superType = superType.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(superType.getTable().quotedID);
		}

		bf.append(" WHERE ");
		first = true;
		for(Type<?> currentType = type; currentType!=null; currentType = currentType.supertype)
		{
			if(first)
				first = false;
			else
				bf.append(" AND ");

			bf.appendPK(currentType, (Join)null).
				append('=').
				appendParameter(item.pk).
				appendTypeCheck(currentType.getTable(), type); // Here this also checks additionally for Model#getItem, that the item has the type given in the ID.
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
				for(Type<?> superType = type; superType!=null; superType = superType.supertype)
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

		final Type<?> supertype = type.supertype;
		if(supertype!=null)
			store(connection, state, present, incrementUpdateCounter, blobs, supertype);

		final Table table = type.getTable();

		final List<Column> columns = table.getColumns();

		final Statement bf = executor.newStatement();
		final StringColumn typeColumn = table.typeColumn;
		final IntegerColumn updateCounter = incrementUpdateCounter ? table.updateCounter : null;
		if(present)
		{
			bf.append("UPDATE ").
				append(table.quotedID).
				append(" SET ");

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

			bf.append(" WHERE ").
				append(table.primaryKey.quotedID).
				append('=').
				appendParameter(state.pk).
				appendTypeCheck(table, state.type);

			if(updateCounter!=null)
			{
				bf.append(" AND ").
					append(updateCounter.quotedID).
					append('=').
					appendParameter(state.updateCount);
			}
		}
		else
		{
			bf.append("INSERT INTO ").
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

			bf.append(")VALUES(").
				appendParameter(state.pk);

			if(typeColumn!=null)
			{
				bf.append(',').
					appendParameter(state.type.schemaId);
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
		executor.updateStrict(connection, present ? state.item : null, bf);
	}

	String makeName(final String longName)
	{
		return nameTrimmer.trimString(longName);
	}

	Schema makeSchema()
	{
		final ConnectionPool connectionPool = this.connectionPool;
		final boolean semicolonEnabled = !properties.isSupportDisabledForSemicolon() && dsmfDialect.supportsSemicolon();
		final Schema result = new Schema(dsmfDialect, new ConnectionProvider()
		{
			public Connection getConnection()
			{
				transactions.assertNoCurrentTransaction();

				return connectionPool.get(true);
			}

			public void putConnection(final Connection connection)
			{
				connectionPool.put(connection);
			}

			public boolean isSemicolonEnabled()
			{
				return semicolonEnabled;
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);

		if(revisions!=null)
			Revisions.makeSchema(result, properties, dialect);
		for(final SequenceX sequence : sequences)
			sequence.makeSchema(result);

		return result;
	}

	Schema makeVerifiedSchema()
	{
		final Schema result = makeSchema();
		result.verify();
		return result;
	}

	void flushSequences()
	{
		for(final SequenceX sequence : sequences)
			sequence.flush();
	}

	private static final Logger deleteLogger = LoggerFactory.getLogger(Database.class.getName() + ".deleteSchema");

	void deleteSchema(final boolean forTest)
	{
		final ArrayList<Table> tables;
		final ArrayList<SequenceX> sequences;
		if(forTest)
		{
			tables = new ArrayList<>();
			for(final Table table : this.tables)
				if(!table.knownToBeEmptyForTest)
					tables.add(table);
			sequences = new ArrayList<>();
			for(final SequenceX sequence : this.sequences)
				if(!sequence.isKnownToBeEmptyForTest())
					sequences.add(sequence);
		}
		else
		{
			tables = this.tables;
			sequences = this.sequences;
		}

		if(deleteLogger.isDebugEnabled())
			deleteLogger.debug(
					"deleteSchemaForTest  tables {} {} sequences {} {}",
					new Object[]{tables.size(), tables, sequences.size(), sequences});

		dialect.deleteSchema(
				Collections.unmodifiableList(tables),
				Collections.unmodifiableList(sequences),
				connectionPool);

		for(final Table table : tables)
			table.knownToBeEmptyForTest = true;
		for(final SequenceX sequence : sequences)
			sequence.setKnownToBeEmptyForTest();
	}

	ArrayList<String> getSequenceSchemaNames()
	{
		final ArrayList<String> result = new ArrayList<>();
		for(final SequenceX sequence : this.sequences)
		{
			final String name = sequence.getSchemaName();
			if(name!=null)
				result.add(name);
		}
		return result;
	}
}

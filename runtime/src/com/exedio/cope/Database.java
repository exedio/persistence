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

import com.exedio.cope.vault.VaultProperties;
import com.exedio.dsmf.ConnectionProvider;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class Database
{
	private ArrayList<Table> tablesModifiable = new ArrayList<>();
	private List<Table> tables;
	private ArrayList<SequenceX> sequencesModifiable = new ArrayList<>();
	private List<SequenceX> sequences;
	final ConnectProperties properties;
	final com.exedio.dsmf.Dialect dsmfDialect;
	final CopeProbe probe;
	final Dialect dialect;
	final Transactions transactions;
	private final RevisionsConnect revisions;
	private final ConnectionPool connectionPool;
	final Executor executor;
	final Map<String, VaultTrail> vaultTrails;

	Database(
			final com.exedio.dsmf.Dialect dsmfDialect,
			final CopeProbe probe,
			final Dialect dialect,
			final ConnectionPool connectionPool,
			final Executor executor,
			final Transactions transactions,
			final Map<String, VaultMarkPut> vaultMarkPut,
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

		final VaultProperties vp = properties.getVaultProperties();
		if(vp!=null)
		{
			final LinkedHashMap<String, VaultTrail> vaultTrails = new LinkedHashMap<>();
			for(final Map.Entry<String, VaultMarkPut> e : vaultMarkPut.entrySet())
			{
				final String bucket = e.getKey();
				vaultTrails.put(bucket, new VaultTrail(bucket, connectionPool, executor, e.getValue(), vp));
			}
			this.vaultTrails = Collections.unmodifiableMap(vaultTrails);
		}
		else
			vaultTrails = null;
	}

	SequenceImpl newSequenceImpl(
			final ModelMetrics metrics,
			final Sequence.Type type, final long start, final IntegerColumn column)
	{
		return
			properties.primaryKeyGenerator.newSequenceImpl(metrics, column, type, start, connectionPool, this);
	}

	SequenceImpl newSequenceImplCluster(
			final ModelMetrics metrics,
			final Sequence.Type type, final long start, final String name)
	{
		return
			new SequenceImplSequence(metrics, name, type, start, connectionPool, executor, dsmfDialect);
	}

	void addTable(final Table table)
	{
		tablesModifiable.add(table);
	}

	void addSequence(final SequenceX sequence)
	{
		sequencesModifiable.add(sequence);
	}

	void finish()
	{
		final List<Table> tables = List.copyOf(tablesModifiable);
		tablesModifiable = null;
		final List<SequenceX> sequences = List.copyOf(sequencesModifiable);
		sequencesModifiable = null;
		this.tables = tables;
		this.sequences = sequences;
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
		makeSchema().createConstraints(types);
	}

	void dropSchema()
	{
		flushSequences();
		makeSchema().drop();
	}

	void dropSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		makeSchema().dropConstraints(types);
	}

	void tearDownSchema()
	{
		makeSchema().tearDown();
	}

	void tearDownSchemaConstraints(final EnumSet<Constraint.Type> types)
	{
		makeSchema().tearDownConstraints(types);
	}

	void checkEmptySchema(final Connection connection)
	{
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

		final String message = Executor.query(connection, bf.toString(), resultSet ->
			{
				StringBuilder mb = null;
				while(resultSet.next())
				{
					if(mb==null)
						mb = new StringBuilder("schema not empty: ");
					else
						mb.append(", ");

					mb.
						append(resultSet.getString(1).trim()). // trim needed for hsqldb
						append(':').
						append(resultSet.getInt(2));
				}
				return mb!=null ? mb.toString() : null;
			}
		);
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
		final Type<?> type = item.type;

		executor.testListener().load(connection, item);

		final Statement bf = executor.newStatement(type.supertype!=null, Statement.Mode.NORMAL);
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
			bf.appendPK(type);
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

			bf.appendPK(currentType).
				append('=').
				appendParameter(item.pk).
				appendTypeCheck(currentType.getTable(), type); // Here this also checks additionally for Model#getItem, that the item has the type given in the ID.
		}

		//System.out.println(bf.toString());

		return executor.query(connection, bf, null, false, resultSet ->
			{
				if(!resultSet.next())
					throw new NoSuchItemException(item);

				final Row row = new Row(type);
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
								throw new IllegalStateException(
										"update counter must be positive: " +
										table.quotedID + '.' + updateCounter.quotedID + '=' + value +
										" where " + table.primaryKey.quotedID + '=' + item.pk);
							updateCount = value;
						}
						else
						{
							if(updateCount!=value)
								throw new RuntimeException(
										"inconsistent update counter for row " + item.pk + " in table " + table.id +
										" compared to " + type.getTable().id + ": " +
										value + '/' + updateCount);
						}
					}

					for(final Column column : table.getColumns())
					{
						if(!(column instanceof BlobColumn))
							column.load(resultSet, columnIndex++, row);
					}
				}

				return new WrittenState(item, row, updateCount!=Integer.MIN_VALUE ? updateCount : 0);
			}
		);
	}

	void store(
			final Connection connection,
			final State state,
			final boolean present,
			final boolean incrementUpdateCounter,
			final IdentityHashMap<BlobColumn, byte[]> blobs)
	{
		store(connection, state, present, incrementUpdateCounter, blobs, state.type);
	}

	private void store(
			final Connection connection,
			final State state,
			final boolean present,
			final boolean incrementUpdateCounter,
			final IdentityHashMap<BlobColumn, byte[]> blobs,
			final Type<?> type)
	{
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
					appendParameter(updateCounter, state.updateCountNext());
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
				assert state.updateCount==Integer.MAX_VALUE : state.updateCount; // comes from CreatedState
				bf.append(",0");
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

	Schema makeSchema()
	{
		final ConnectionPool connectionPool = this.connectionPool;
		final Schema result = new Schema(dsmfDialect, new ConnectionProvider()
		{
			@Override
			public Connection getConnection()
			{
				transactions.assertNoCurrentTransaction();

				return connectionPool.get(true);
			}

			@Override
			public void putConnection(final Connection connection)
			{
				connectionPool.put(connection);
			}
		});
		for(final Table t : tables)
			t.makeSchema(result);

		if(revisions!=null)
			Revisions.makeSchema(result, properties, dialect);
		for(final SequenceX sequence : sequences)
			sequence.makeSchema(result);
		if(vaultTrails!=null)
			for(final VaultTrail vt : vaultTrails.values())
				vt.makeSchema(result, dialect);

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
		final List<Table> tables;
		final List<SequenceX> sequences;
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
					tables.size(), tables, sequences.size(), sequences);

		dialect.deleteSchema(
				Collections.unmodifiableList(tables),
				Collections.unmodifiableList(sequences),
				forTest,
				connectionPool);

		for(final Table table : tables)
			table.knownToBeEmptyForTest = true;
		for(final SequenceX sequence : sequences)
			sequence.setKnownToBeEmptyForTest();
	}

	ArrayList<String> getSequenceSchemaNames()
	{
		final ArrayList<String> result = new ArrayList<>();
		for(final SequenceX sequence : sequences)
		{
			final String name = sequence.getSchemaName();
			if(name!=null)
				result.add(name);
		}
		return result;
	}
}

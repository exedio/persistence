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

package com.exedio.dsmf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public final class Schema extends Node
{
	private final HashMap<String, Table> tableMap = new HashMap<>();
	private final ArrayList<Table> tableList = new ArrayList<>();
	private final HashMap<String, Sequence> sequenceMap = new HashMap<>();
	private final ArrayList<Sequence> sequenceList = new ArrayList<>();
	private final HashMap<String, Constraint> requiredConstraintMap = new HashMap<>();
	private boolean verified = false;

	public Schema(final Dialect dialect, final ConnectionProvider connectionProvider)
	{
		super(dialect, connectionProvider);
	}

	void register(final Table table)
	{
		if(tableMap.put(table.name, table)!=null)
			throw new RuntimeException("duplicate table name in schema: " + table.name);
		tableList.add(table);
	}

	Table notifyExistentTable(final String tableName)
	{
		Table result = tableMap.get(tableName);
		if(result==null)
			result = new Table(this, tableName, false);
		else
			result.notifyExists();

		return result;
	}

	public Table getTable(final String name)
	{
		return tableMap.get(name);
	}

	public List<Table> getTables()
	{
		return tableList;
	}

	void register(final Sequence sequence)
	{
		if(sequenceMap.put(sequence.name, sequence)!=null)
			throw new RuntimeException("duplicate sequence name in schema: " + sequence.name);
		sequenceList.add(sequence);
	}

	Sequence notifyExistentSequence(final String sequenceName)
	{
		Sequence result = sequenceMap.get(sequenceName);
		if(result==null)
			result = new Sequence(this, sequenceName, 0, false); // TODO extract startWith from dictionary
		else
			result.notifyExists();

		return result;
	}

	public Sequence getSequence(final String name)
	{
		return sequenceMap.get(name);
	}

	public List<Sequence> getSequences()
	{
		return sequenceList;
	}

	void register(final Constraint constraint)
	{
		if(!constraint.required())
			return;

		if(requiredConstraintMap.put(constraint.name, constraint)!=null)
			throw new RuntimeException("duplicate constraint name in schema : " + constraint.name);
	}

	public void verify()
	{
		if(verified)
			throw new RuntimeException("alread verified");
		verified = true;

		dialect.verify(this);
		finish();
	}

	@Override
	void finish()
	{
		assert particularColor==null;
		assert cumulativeColor==null;

		particularColor = Color.OK;

		cumulativeColor = particularColor;
		for(final Table table : tableList)
		{
			table.finish();
			cumulativeColor = cumulativeColor.max(table.cumulativeColor);
		}
		for(final Sequence sequence : sequenceList)
		{
			sequence.finish();
			cumulativeColor = cumulativeColor.max(sequence.cumulativeColor);
		}
	}

	public void create()
	{
		create(null);
	}

	public void create(final StatementListener listener)
	{
		final Graph graph = new Graph(this);
		final Set<ForeignKeyConstraint> constraintsBroken = graph.getConstraintsBroken();

		if(connectionProvider.isSemicolonEnabled())
		{
			final StringBuilder bf = new StringBuilder();
			boolean first = true;

			for(final Sequence s : sequenceList)
			{
				if(first)
					first = false;
				else
					bf.append(';');

				s.create(bf);
			}

			for(final Table t : graph.getTablesOrdered())
			{
				if(first)
					first = false;
				else
					bf.append(';');

				t.create(bf, constraintsBroken);
			}

			executeSQL(bf.toString(), listener);
		}
		else
		{
			for(final Sequence s : sequenceList)
				s.create(listener);
			for(final Table t : graph.getTablesOrdered())
				t.create(listener, constraintsBroken);
		}

		for(final ForeignKeyConstraint c : constraintsBroken)
			c.create(listener);
	}

	public void drop()
	{
		drop(null);
	}

	public void drop(final StatementListener listener)
	{
		final Graph graph = new Graph(this);

		// must delete in reverse order, to obey integrity constraints

		for(final ForeignKeyConstraint c : graph.getConstraintsBroken())
			c.drop(listener);

		if(connectionProvider.isSemicolonEnabled())
		{
			final StringBuilder bf = new StringBuilder();
			boolean first = true;

			for(final Sequence s : sequenceList)
			{
				if(first)
					first = false;
				else
					bf.append(';');

				s.drop(bf);
			}

			for(final Table t : reverse(graph.getTablesOrdered()))
			{
				if(first)
					first = false;
				else
					bf.append(';');

				t.drop(bf);
			}

			executeSQL(bf.toString(), listener);
		}
		else
		{
			for(final Table t : reverse(graph.getTablesOrdered()))
				t.drop(listener);
			for(final Sequence s : reverse(sequenceList))
				s.drop(listener);
		}
	}

	public void tearDown()
	{
		tearDown(null);
	}

	public void tearDown(final StatementListener listener)
	{
		for(final Sequence sequence : sequenceList)
		{
			try
			{
				sequence.drop(listener);
			}
			catch(final SQLRuntimeException e2)
			{
				// ignored in teardown
				//System.err.println("failed:"+e2.getMessage());
			}
		}
		// IMPLEMENTATION NOTE
		//
		// On MySQL its much faster to drop whole tables instead of
		// foreign key constraints. Therefore we first try to drop as many
		// tables as possible before dropping foreign key constraints.
		tearDownTables(listener);
		tearDownForeignKeys(listener);
		tearDownTables(listener);
	}

	private void tearDownForeignKeys(final StatementListener listener)
	{
		for(final Table table : tableList)
		{
			try
			{
				table.tearDownConstraints(EnumSet.allOf(Constraint.Type.class), true, listener);
			}
			catch(final SQLRuntimeException e2)
			{
				// ignored in teardown
				//System.err.println("failed:"+e2.getMessage());
			}
		}
	}

	private void tearDownTables(final StatementListener listener)
	{
		final ArrayList<Table> tablesToDelete = new ArrayList<>(tableList);

		boolean deleted;
		do
		{
			deleted = false;

			for(final Iterator<Table> i = tablesToDelete.iterator(); i.hasNext(); )
			{
				try
				{
					final Table table = i.next();
					//System.err.print("DROPPING TABLE "+table+" ... ");
					table.drop(listener);
					//System.err.println("done.");
					// remove the table, so it's not tried again
					i.remove();
					// remember there was at least one table deleted
					deleted = true;
				}
				catch(final SQLRuntimeException e2)
				{
					// ignored in teardown
					//System.err.println("failed:"+e2.getMessage());
				}
			}
		}
		while(deleted);
	}

	public void createConstraints(final EnumSet<Constraint.Type> types)
	{
		createConstraints(types, null);
	}

	public void createConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		for(final Table t : tableList)
			t.createConstraints(types, false, listener);
		for(final Table t : tableList)
			t.createConstraints(types, true, listener);
	}

	public void dropConstraints(final EnumSet<Constraint.Type> types)
	{
		dropConstraints(types, null);
	}

	public void dropConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		for(final Table t : reverse(tableList))
			t.dropConstraints(types, true, listener);
		for(final Table t : reverse(tableList))
			t.dropConstraints(types, false, listener);
	}

	public void tearDownConstraints(final EnumSet<Constraint.Type> types)
	{
		tearDownConstraints(types, null);
	}

	public void tearDownConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		System.err.println("TEAR DOWN CONSTRAINTS");
		for(final Table t : reverse(tableList))
			t.tearDownConstraints(types, true, listener);
		for(final Table t : reverse(tableList))
			t.tearDownConstraints(types, false, listener);
	}

	public void checkUnsupportedConstraints()
	{
		for(final Table t : getTables())
			t.checkUnsupportedConstraints();
	}

	private static <E> Iterable<E> reverse(final List<E> l)
	{
		return new Iterable<E>(){
			public Iterator<E> iterator()
			{
				final ListIterator<E> iterator = l.listIterator(l.size());
				return new Iterator<E>()
				{
					public boolean hasNext()
					{
						return iterator.hasPrevious();
					}

					@Override
					public E next()
					{
						return iterator.previous();
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}

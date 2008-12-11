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

package com.exedio.dsmf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class Schema extends Node
{
	private final HashMap<String, Table> tableMap = new HashMap<String, Table>();
	private final ArrayList<Table> tableList = new ArrayList<Table>();
	private final HashMap<String, Sequence> sequenceMap = new HashMap<String, Sequence>();
	private final ArrayList<Sequence> sequenceList = new ArrayList<Sequence>();
	private boolean verified = false;
	
	public Schema(final Driver driver, final ConnectionProvider connectionProvider)
	{
		super(driver, connectionProvider);
	}

	final void register(final Table table)
	{
		if(tableMap.put(table.name, table)!=null)
			throw new RuntimeException("duplicate table name in schema: " + table.name);
		tableList.add(table);
	}
	
	final Table notifyExistentTable(final String tableName)
	{
		Table result = tableMap.get(tableName);
		if(result==null)
			result = new Table(this, tableName, null, false);
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
	
	final void register(final Sequence sequence)
	{
		if(sequenceMap.put(sequence.name, sequence)!=null)
			throw new RuntimeException("duplicate sequence name in schema: " + sequence.name);
		sequenceList.add(sequence);
	}
	
	final Sequence notifyExistentSequence(final String sequenceName)
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
	
	public void verify()
	{
		if(verified)
			throw new RuntimeException("alread verified");
		verified = true;
		
		driver.verify(this);
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
	
	//private static int createTableTime = 0, dropTableTime = 0, checkEmptyTableTime = 0;
	
	public final void create()
	{
		create(null);
	}
	
	public final void create(final StatementListener listener)
	{
		//final long time = System.currentTimeMillis();
		for(final Sequence s : sequenceList)
			s.create(listener);
	
		for(final Table t : tableList)
			t.create(listener);
	
		for(final Table t : tableList)
			t.createConstraints(EnumSet.allOf(Constraint.Type.class), true, listener);
	
		//final long amount = (System.currentTimeMillis()-time);
		//createTableTime += amount;
		//System.out.println("CREATE TABLES "+amount+"ms  accumulated "+createTableTime);
	}

	public final void drop()
	{
		drop(null);
	}
	
	public final void drop(final StatementListener listener)
	{
		//final long time = System.currentTimeMillis();
		// must delete in reverse order, to obey integrity constraints
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().dropConstraints(EnumSet.allOf(Constraint.Type.class), true, listener);
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().drop(listener);
		for(ListIterator<Sequence> i = sequenceList.listIterator(sequenceList.size()); i.hasPrevious(); )
			i.previous().drop(listener);
		//final long amount = (System.currentTimeMillis()-time);
		//dropTableTime += amount;
		//System.out.println("DROP TABLES "+amount+"ms  accumulated "+dropTableTime);
	}
	
	public final void tearDown()
	{
		tearDown(null);
	}
	
	public final void tearDown(final StatementListener listener)
	{
		for(final Sequence sequence : sequenceList)
		{
			try
			{
				sequence.drop(listener);
			}
			catch(SQLRuntimeException e2)
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
	
	private final void tearDownForeignKeys(final StatementListener listener)
	{
		for(final Table table : tableList)
		{
			try
			{
				table.tearDownConstraints(EnumSet.allOf(Constraint.Type.class), true, listener);
			}
			catch(SQLRuntimeException e2)
			{
				// ignored in teardown
				//System.err.println("failed:"+e2.getMessage());
			}
		}
	}
		
	private final void tearDownTables(final StatementListener listener)
	{
		final ArrayList<Table> tablesToDelete = new ArrayList<Table>(tableList);

		boolean deleted;
		//int run = 1;
		do
		{
			deleted = false;
			
			for(Iterator<Table> i = tablesToDelete.iterator(); i.hasNext(); )
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
				catch(SQLRuntimeException e2)
				{
					// ignored in teardown
					//System.err.println("failed:"+e2.getMessage());
				}
			}
			//System.err.println("FINISH STAGE "+(run++));
		}
		while(deleted);
	}

	public final void createConstraints(final EnumSet<Constraint.Type> types)
	{
		createConstraints(types, null);
	}
	
	public final void createConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		for(final Table t : tableList)
			t.createConstraints(types, false, listener);
		for(final Table t : tableList)
			t.createConstraints(types, true, listener);
	}

	public final void dropConstraints(final EnumSet<Constraint.Type> types)
	{
		dropConstraints(types, null);
	}
	
	public final void dropConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().dropConstraints(types, true, listener);
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().dropConstraints(types, false, listener);
	}
	
	public final void tearDownConstraints(final EnumSet<Constraint.Type> types)
	{
		tearDownConstraints(types, null);
	}
	
	public final void tearDownConstraints(final EnumSet<Constraint.Type> types, final StatementListener listener)
	{
		System.err.println("TEAR DOWN CONSTRAINTS");
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().tearDownConstraints(types, true, listener);
		for(ListIterator<Table> i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			i.previous().tearDownConstraints(types, false, listener);
	}
	
	public final void checkUnsupportedConstraints()
	{
		for(final Table t : getTables())
			t.checkUnsupportedConstraints();
	}
}

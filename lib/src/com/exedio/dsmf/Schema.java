/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.exedio.cope.NestingRuntimeException;

public final class Schema extends Node
{
	private final HashMap tableMap = new HashMap();
	private final ArrayList tableList = new ArrayList();
	
	public Schema(final Driver driver, final ConnectionProvider connectionProvider)
	{
		super(driver, connectionProvider);
	}

	final void register(final Table table)
	{
		if(tableMap.put(table.name, table)!=null)
			throw new RuntimeException(table.name);
		tableList.add(table);
	}
	
	final Table notifyExistentTable(final String tableName)
	{
		Table result = (Table)tableMap.get(tableName);
		if(result==null)
			result = new Table(this, tableName, false);
		else
			result.notifyExists();

		return result;
	}
	
	public Table getTable(final String name)
	{
		return (Table)tableMap.get(name);
	}
	
	public List getTables()
	{
		return tableList;
	}
	
	public void verify()
	{
		driver.verify(this);
		finish();
	}

	void finish()
	{
		if(cumulativeColor!=COLOR_NOT_YET_CALC || particularColor!=COLOR_NOT_YET_CALC)
			throw new RuntimeException();
		
		particularColor = COLOR_OK;

		cumulativeColor = particularColor;
		for(Iterator i = tableList.iterator(); i.hasNext(); )
		{
			final Table table = (Table)i.next();
			table.finish();
			cumulativeColor = Math.max(cumulativeColor, table.cumulativeColor);
		}
	}
	
	//private static int createTableTime = 0, dropTableTime = 0, checkEmptyTableTime = 0;
	
	public final void create()
	{
		//final long time = System.currentTimeMillis();
		for(Iterator i = tableList.iterator(); i.hasNext(); )
			((Table)i.next()).create();
	
		for(Iterator i = tableList.iterator(); i.hasNext(); )
			((Table)i.next()).createForeignKeyConstraints();
	
		//final long amount = (System.currentTimeMillis()-time);
		//createTableTime += amount;
		//System.out.println("CREATE TABLES "+amount+"ms  accumulated "+createTableTime);
	}

	public final void drop()
	{
		//final long time = System.currentTimeMillis();
		// must delete in reverse order, to obey integrity constraints
		for(ListIterator i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			((Table)i.previous()).dropForeignKeyConstraints(false);
		for(ListIterator i = tableList.listIterator(tableList.size()); i.hasPrevious(); )
			((Table)i.previous()).drop();
		//final long amount = (System.currentTimeMillis()-time);
		//dropTableTime += amount;
		//System.out.println("DROP TABLES "+amount+"ms  accumulated "+dropTableTime);
	}
	
	public final void tearDown()
	{
		System.err.println("TEAR DOWN ALL DATABASE");
		for(Iterator i = tableList.iterator(); i.hasNext(); )
		{
			try
			{
				final Table table = (Table)i.next();
				table.dropForeignKeyConstraints(true);
			}
			catch(NestingRuntimeException e2)
			{
				System.err.println("failed:"+e2.getMessage());
			}
		}
		
		final ArrayList tablesToDelete = new ArrayList(tableList);

		boolean deleted;
		int run = 1;
		do
		{
			deleted = false;
			
			for(Iterator i = tablesToDelete.iterator(); i.hasNext(); )
			{
				try
				{
					final Table table = (Table)i.next();
					System.err.print("DROPPING TABLE "+table+" ... ");
					table.drop();
					System.err.println("done.");
					// remove the table, so it's not tried again
					i.remove();
					// remember there was at least one table deleted
					deleted = true;
				}
				catch(NestingRuntimeException e2)
				{
					System.err.println("failed:"+e2.getMessage());
				}
			}
			System.err.println("FINISH STAGE "+(run++));
		}
		while(deleted);
	}

}

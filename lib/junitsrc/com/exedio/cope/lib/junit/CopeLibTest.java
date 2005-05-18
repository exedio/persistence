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

package com.exedio.cope.lib.junit;

import java.util.Iterator;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.Properties;
import com.exedio.cope.lib.util.PoolCounter;

public abstract class CopeLibTest extends CopeAssert
{
	private static boolean createdDatabase = false;
	private static boolean registeredDropDatabaseHook = false;
	private static Object lock = new Object();

	public final Model model;
	
	protected CopeLibTest(final Model model)
	{
		this.model = model;
	}
	
	protected final void printConnectionPoolCounter()
	{
		final PoolCounter connectionPoolCounter = model.getConnectionPoolCounter();
		for(Iterator i = connectionPoolCounter.getPools().iterator(); i.hasNext(); )
		{
			final PoolCounter.Pool pool = (PoolCounter.Pool)i.next();
			System.out.println("ConnectionPool:["+pool.getSize()+"]: "+pool.getLevel()+", "+pool.getMaxLevel()+", "+pool.getGetCounter()+", "+pool.getPutCounter()+", "+pool.getCreateCounter()+", "+pool.getDestroyCounter()+", "+pool.getEfficiency());
		}
	}

	private final void createDatabase()
	{
		synchronized(lock)
		{
			if(!createdDatabase)
			{
				model.createDatabase();
				createdDatabase = true;
			}
		}
	}
	
	private final void dropDatabase()
	{
		synchronized(lock)
		{
			if(!registeredDropDatabaseHook)
			{
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
					public void run()
					{
						printConnectionPoolCounter();
						model.dropDatabase();
					}
				}));
				registeredDropDatabaseHook = true;
			}
		}
	}

	protected void setUp() throws Exception
	{
		model.setPropertiesInitially(new Properties());

		super.setUp();
		createDatabase();
		model.checkEmptyDatabase();
	}
	
	protected void tearDown() throws Exception
	{
		super.tearDown();
		model.checkEmptyDatabase();
		dropDatabase();
	}
	
}

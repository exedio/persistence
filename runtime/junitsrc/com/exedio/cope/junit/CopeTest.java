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

package com.exedio.cope.junit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Properties;
import com.exedio.cope.Transaction;
import com.exedio.cope.util.PoolCounter;

/**
 * An abstract test case class for tests creating/using some persistent data.
 * @author Ralf Wiebicke
 */
public abstract class CopeTest extends CopeAssert
{
	private static HashSet<Model> createdDatabase = new HashSet<Model>();
	private static HashSet<Model> registeredDropDatabaseHook = new HashSet<Model>();
	private static Object lock = new Object();

	public final Model model;
	public final boolean exclusive;
	
	private boolean testMethodFinished = false;
	private ArrayList<Item> deleteOnTearDown = null;
	
	
	protected CopeTest(final Model model)
	{
		this(model, false);
	}
	
	/**
	 * @param exclusive
	 * if true, the database schema for the model will be created before each test
	 * and dropped after each test.
	 * This will make tests last much longer, so this is useful for debugging only.
	 */
	protected CopeTest(final Model model, final boolean exclusive)
	{
		this.model = model;
		this.exclusive = exclusive;
	}
	
	protected final void deleteOnTearDown(final Item item)
	{
		deleteOnTearDown.add(item);
	}
	
	protected final void printConnectionPoolCounter()
	{
		final PoolCounter connectionPoolCounter = model.getConnectionPoolInfo().getCounter();

		System.out.println("ConnectionPool: "+connectionPoolCounter.getGetCounter()+", "+connectionPoolCounter.getPutCounter());
		for(Iterator i = connectionPoolCounter.getPools().iterator(); i.hasNext(); )
		{
			final PoolCounter.Pool pool = (PoolCounter.Pool)i.next();
			System.out.println("ConnectionPool:["+pool.getSize()+"]: "+pool.getIdleCount()+", "+pool.getIdleCountMax()+", "+pool.getCreateCounter()+", "+pool.getDestroyCounter()+", "+pool.getLoss());
		}
	}

	private final void createDatabase()
	{
		if(exclusive)
			model.createDatabase();
		else
		{
			synchronized(lock)
			{
				if(!createdDatabase.contains(model))
				{
					model.createDatabase();
					createdDatabase.add(model);
				}
			}
		}
	}
	
	private final void dropDatabase()
	{
		if(exclusive)
			model.dropDatabase();
		else
		{
			synchronized(lock)
			{
				if(!registeredDropDatabaseHook.contains(model))
				{
					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
						public void run()
						{
							//printConnectionPoolCounter();
							model.dropDatabase();
							model.close();
						}
					}));
					registeredDropDatabaseHook.add(model);
				}
			}
		}
	}

	@Override
	public void runBare() throws Throwable
	{
		setUp();
		try
		{
			testMethodFinished = false;
			runTest();
			testMethodFinished = true;
		}
		finally
		{
			tearDown();
		}
	}
	
	public Properties getProperties()
	{
		return new Properties();
	}
	
	@Override
	protected void setUp() throws Exception
	{
		model.connect(getProperties());

		super.setUp();
		deleteOnTearDown = new ArrayList<Item>();
		createDatabase();

		model.startTransaction("CopeTest");
		model.checkEmptyDatabase();
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		final boolean hadTransaction = model.hasCurrentTransaction();
		if ( ! hadTransaction )
		{
			model.startTransaction( "started by tearDown" );
		}
		Transaction current = model.getCurrentTransaction();
		ArrayList<Transaction> openTransactions = null;
		for(Transaction nextTransaction : new HashSet<Transaction>(model.getOpenTransactions()))
		{
			if ( ! nextTransaction.equals(current) )
			{
				if(openTransactions==null)
					openTransactions = new ArrayList<Transaction>();
				openTransactions.add( nextTransaction );
				model.leaveTransaction();
				model.joinTransaction( nextTransaction );
				model.rollback();
				model.joinTransaction( current );
			}
		}
		if( !exclusive )
		{
			try
			{
				if(!deleteOnTearDown.isEmpty())
				{
					RuntimeException deleteException = null;
					for(ListIterator<Item> i = deleteOnTearDown.listIterator(deleteOnTearDown.size()); i.hasPrevious(); )
					{
						try
						{
							i.previous().deleteCopeItem();
						}
						catch(RuntimeException e)
						{
							if(deleteException==null && testMethodFinished)
							{
								deleteException = e;
							}
						}
					}
					deleteOnTearDown.clear();
					if(deleteException!=null)
					{
						throw new RuntimeException("test completed successfully but failed to delete a 'deleteOnTearDown' item", deleteException);
					}
				}
				deleteOnTearDown = null;
				model.checkEmptyDatabase();
			}
			catch ( RuntimeException e )
			{
				if ( testMethodFinished )
				{
					throw new RuntimeException("test completed successfully but didn't clean up database", e);
				}
				else
				{
					model.rollbackIfNotCommitted();
					model.tearDownDatabase();
					model.createDatabase();
					model.startTransaction();
				}
			}
		}
		if ( testMethodFinished || model.hasCurrentTransaction() )
		{
			model.commit();
		}
		if ( testMethodFinished && !hadTransaction )
		{
			fail( "test completed successfully but didn't have current transaction in tearDown" );
		}
		if ( testMethodFinished && openTransactions!=null )
		{
			fail( "test completed successfully but left open transactions: "+openTransactions );
		}

		dropDatabase();
		super.tearDown();
	}
	
	protected boolean testCompletedSuccessfully()
	{
		return testMethodFinished;
	}
	
	protected void restartTransaction()
	{
		final String oldName = model.getCurrentTransaction().getName();
		model.commit();
		model.startTransaction( oldName+"-restart" );
	}
	
}

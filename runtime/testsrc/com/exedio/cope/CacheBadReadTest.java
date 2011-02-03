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


public class CacheBadReadTest extends AbstractRuntimeTest
{
	public CacheBadReadTest()
	{
		super(CacheIsolationTest.MODEL);
		skipTransactionManagement();
	}

	CacheIsolationItem item;
	final ThreadStoppable threads[] = new ThreadStoppable[10];

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		model.startTransaction("CacheBadReadTest setUp");
		try
		{
			item = deleteOnTearDown(new CacheIsolationItem("itemName"));
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}

	@Override
	public void tearDown() throws Exception
	{
		for(int i = 0; i<threads.length; i++)
		{
			if(threads[i]!=null)
			{
				threads[i].join();
				threads[i] = null;
			}
		}

		super.tearDown();
	}

	static class ThreadStoppable extends Thread
	{
		boolean proceed = true;
	}

	public void testIt() throws InterruptedException
	{
		if(hsqldb||oracle) return; // TODO

		for(int i = 0; i<threads.length; i++)
		{
			threads[i] = new ThreadStoppable(){
				@Override
				public void run()
				{
					int i;
					for(i = 0; i<10000000 && proceed; i++)
					{
						//if(i%100==0 || i<20) System.out.println("CacheBadReadTest read " + i);
						//Thread.yield();
						model.startTransaction("CacheBadReadTest  read " + i);
						final String name = item.getName();
						assertTrue(name, name.startsWith("itemName"));
						model.commit();
					}
					//System.out.println("CacheBadReadTest read fertig " + i);
				}
			};
		}

		for(final Thread thread : threads)
			thread.start();

		{
			int i = 0;
			try
			{
				for(; i<10000; i++)
				{
					//if(i%100==0 || i<20) System.out.println("CacheBadReadTest write " + i);
					//Thread.yield();
					model.startTransaction("CacheBadReadTest write " + i);
					item.setName("itemName" + i);
					model.commit();
				}
			}
			catch(final TemporaryTransactionException e)
			{
				System.out.println("" + i + " " + e.getMessage());
			}
		}


		for(final ThreadStoppable thread : threads)
			thread.proceed = false;
		for(int i = 0; i<threads.length; i++)
		{
			threads[i].join();
			threads[i] = null;
		}
	}
}

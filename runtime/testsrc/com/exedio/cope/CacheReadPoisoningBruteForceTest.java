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

import static java.lang.System.lineSeparator;

import com.exedio.cope.tojunit.MainRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class CacheReadPoisoningBruteForceTest extends TestWithEnvironment
{
	public CacheReadPoisoningBruteForceTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	@SuppressWarnings("unused")
	@SuppressFBWarnings("URF_UNREAD_FIELD")
	private final WithinPurgeStampsRule withinPurgeStamps = new WithinPurgeStampsRule(
			CacheIsolationTest.MODEL,
			Thread::yield);

	boolean stamps;
	CacheIsolationItem item;
	final ThreadStoppable threads[] = new ThreadStoppable[10];
	final StringBuilder failures = new StringBuilder();

	@BeforeEach final void setUp()
	{
		stamps = model.getConnectProperties().itemCacheStamps;

		System.out.println("CacheReadPoisoningBruteForceTest stamps " + stamps);

		try(TransactionTry tx = model.startTransactionTry("CacheBadReadTest setUp"))
		{
			item = new CacheIsolationItem("itemName");
			tx.commit();
		}

		failures.setLength(0);
	}

	@AfterEach final void tearDown() throws InterruptedException
	{
		for(final ThreadStoppable thread : threads)
		{
			if(thread!=null)
				thread.proceed = false;
		}
		for(final ThreadStoppable thread : threads)
		{
			if(thread!=null)
				thread.join();
		}
	}

	static class ThreadStoppable extends Thread
	{
		private final int number;

		ThreadStoppable(final int number)
		{
			this.number = number;
		}
		@Override
		public String toString()
		{
			return "thread" + number;
		}

		boolean proceed = true;
		String errorName = null;
		boolean finished = false;
		Throwable failure = null;
	}

	@Test void testIt() throws InterruptedException
	{
		final Model model = this.model; // avoid warning about synthetic-access
		Arrays.setAll(threads, i -> new ThreadStoppable(i)
		{
			@Override
			public void run()
			{
				try
				{
					int i;
					for(i = 0; i<20_000_000 && proceed; i++)
					{
						//if(i%100==0 || i<20) System.out.println("CacheBadReadTest read " + i);
						//Thread.yield();
						model.startTransaction("CacheBadReadTest  read " + i);
						final String name = item.getName();
						if(!name.startsWith("itemName"))
							errorName = name;
						model.commit();
					}
					finished = true;
				}
				catch(final Throwable t)
				{
					failure = t;
					throw new RuntimeException(t);
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
				//System.out.println("CacheBadReadTest read fertig " + i);
			}
		});

		for(final Thread thread : threads)
			thread.start();

		{
			int i = 0;
			try
			{
				for(; i<20_000; i++)
				{
					//if(i%100==0 || i<20) System.out.println("CacheBadReadTest write " + i);
					//Thread.yield();
					model.startTransaction("CacheBadReadTest write " + i);
					item.setName("itemName" + i);
					model.commit();
				}
				assertTrue("stamps " + stamps + " " + i, stamps);
			}
			catch(final TemporaryTransactionException e)
			{
				assertNotNull("TemporaryTransactionException message", e.getMessage());
				assertFalse("stamps " + stamps + " " + i, stamps);
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}


		for(final ThreadStoppable thread : threads)
		{
			assertEquals("finished too early " + thread, false, thread.finished);
			assertNotEquals("not finished " + thread, Thread.State.TERMINATED, thread.getState());
		}
		for(final ThreadStoppable thread : threads)
			thread.proceed = false;
		for(final ThreadStoppable thread : threads)
		{
			thread.join();
			if(thread.failure!=null)
				thread.failure.printStackTrace();
			assertEquals("error name " + thread, null, thread.errorName);
			assertEquals("not finished normally " + thread, true, thread.finished);
			assertEquals("finished with exception " + thread, null, thread.failure);
			assertEquals("state " + thread, Thread.State.TERMINATED, thread.getState());
		}

		Assertions.assertEquals("", failures.toString());
	}

	private void assertNotNull(final String message, final Object actual)
	{
		if(actual==null)
			failures.append(message + " is null" + lineSeparator());
	}

	private void assertTrue(final String message, final boolean actual)
	{
		if(!actual)
			failures.append(message + lineSeparator());
	}

	private void assertFalse(final String message, final boolean actual)
	{
		if(actual)
			failures.append(message + lineSeparator());
	}

	private void assertEquals(final String message, final Object expected, final Object actual)
	{
		if(!Objects.equals(expected,  actual))
			failures.append(message + " expected: " + expected + ", but was: " + actual + lineSeparator());
	}

	private void assertNotEquals(final String message, final Object expected, final Object actual)
	{
		if(Objects.equals(expected,  actual))
			failures.append(message + " expected: " + expected + ", but was: " + actual + lineSeparator());
	}
}

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.misc;

import static com.exedio.cope.CacheIsolationItem.TYPE;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class TransactionRunnableTest extends AbstractRuntimeTest
{
	public TransactionRunnableTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = null;
	}

	public void testIt()
	{
		assertContains(TYPE.search());
		model.commit();

		assertFalse(model.hasCurrentTransaction());
		final TransactionRunnable tr1 =
			new TransactionRunnable(model, new Runnable(){
				// no thread is spawned, thus asserts still can cause the test to fail.
				@SuppressFBWarnings("IJU_ASSERT_METHOD_INVOKED_FROM_RUN_METHOD")
				public void run()
				{
					assertEquals("name1", model.currentTransaction().getName());
					item = new CacheIsolationItem("item1");
				}
			},
			"name1");
		final TransactionRunnable tr2 =
			new TransactionRunnable(model, new Runnable(){
				// no thread is spawned, thus asserts still can cause the test to fail.
				@SuppressFBWarnings("IJU_ASSERT_METHOD_INVOKED_FROM_RUN_METHOD")
				public void run()
				{
					assertEquals(null, model.currentTransaction().getName());
					new CacheIsolationItem("item2");
					throw new RuntimeException("zack");
				}
			});
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction("TransactionRunnableTest");
		assertContains(TYPE.search());
		model.commit();

		assertFalse(model.hasCurrentTransaction());
		tr1.run();
		assertFalse(model.hasCurrentTransaction());
		deleteOnTearDown(item);

		model.startTransaction("TransactionRunnableTest");
		assertContains(item, TYPE.search());
		model.commit();

		assertFalse(model.hasCurrentTransaction());
		try
		{
			tr2.run();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("zack", e.getMessage());
		}
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction("TransactionRunnableTest");
		assertContains(item, TYPE.search());
		model.commit();

		try
		{
			new TransactionRunnable(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
		try
		{
			new TransactionRunnable(model, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("runnable", e.getMessage());
		}

		model.startTransaction("TransactionRunnableTest");
	}
}

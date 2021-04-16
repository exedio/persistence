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

package com.exedio.cope.misc;

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionRunnableTest extends TestWithEnvironment
{
	public TransactionRunnableTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	CacheIsolationItem item = null;

	@BeforeEach final void setUp()
	{
		item = null;
	}

	@Test void testIt()
	{
		assertContains(TYPE.search());
		model.commit();

		assertFalse(model.hasCurrentTransaction());
		// no thread is spawned, thus asserts still can cause the test to fail.
		final TransactionRunnable tr1 =
			new TransactionRunnable(model, () ->
			{
				assertEquals("name1", model.currentTransaction().getName());
				item = new CacheIsolationItem("item1");
			},
			"name1");
		// no thread is spawned, thus asserts still can cause the test to fail.
		final TransactionRunnable tr2 =
			new TransactionRunnable(model, () ->
			{
				assertEquals(null, model.currentTransaction().getName());
				new CacheIsolationItem("item2");
				throw new RuntimeException("zack");
			});
		assertFalse(model.hasCurrentTransaction());

		model.startTransaction("TransactionRunnableTest");
		assertContains(TYPE.search());
		model.commit();

		assertFalse(model.hasCurrentTransaction());
		tr1.run();
		assertFalse(model.hasCurrentTransaction());

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

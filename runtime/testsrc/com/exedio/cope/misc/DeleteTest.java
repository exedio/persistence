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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import java.util.ArrayList;
import org.junit.Test;

public class DeleteTest extends TestWithEnvironment
{
	public DeleteTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testIt()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();

		model.startTransaction("setUp");
		final CacheIsolationItem i1 = new CacheIsolationItem("item1");
		final CacheIsolationItem i2 = new CacheIsolationItem("item1");
		assertEquals(true, i1.existsCopeItem());
		assertEquals(true, i2.existsCopeItem());
		model.commit();

		final Context ctx = new Context(2);
		Delete.delete(q, "tx", ctx);
		model.startTransaction("setUp");
		assertEquals(false, i1.existsCopeItem());
		assertEquals(false, i2.existsCopeItem());
		model.commit();
		ctx.assertProgress(2);
	}

	@Test public void testEmpty()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		final Context ctx = new Context(1);

		Delete.delete(q, "tx", ctx);
		ctx.assertProgress(0);
	}

	@Test public void testError()
	{
		try
		{
			Delete.delete(TYPE.newQuery(), "tx", (JobContext)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}

		try
		{
			Delete.delete(null, "tx", new AssertionErrorJobContext());
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testAbort()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		q.setOrderBy(TYPE.getThis(), true);

		model.startTransaction("setUp");
		final CacheIsolationItem i1 = new CacheIsolationItem("item1");
		final CacheIsolationItem i2 = new CacheIsolationItem("item1");
		assertEquals(true, i1.existsCopeItem());
		assertEquals(true, i2.existsCopeItem());
		model.commit();

		final Context ctx = new Context(1);
		try
		{
			Delete.delete(q, "tx", ctx);
			fail();
		}
		catch(final JobStop js)
		{
			assertEquals("progress 1 excessed with 1", js.getMessage());
		}
		model.startTransaction("setUp");
		assertEquals(false, i1.existsCopeItem());
		assertEquals(true,  i2.existsCopeItem());
		i2.deleteCopeItem();
		model.commit();
		ctx.assertProgress(1);
	}

	@Test public void testTransactions()
	{
		assertPurge(  0, 1);
		assertPurge(  1, 1);
		assertPurge( 99, 1);
		assertPurge(100, 2);
		assertPurge(101, 2);
	}

	private void assertPurge(final int itemNumber, final int transactionNumber)
	{
		final ArrayList<CacheIsolationItem> items = new ArrayList<>();
		model.startTransaction("DeleteTest");
		for(int n = 0; n<itemNumber; n++)
			items.add(new CacheIsolationItem("item"+n));
		model.commit();
		final long transactionIdBefore = model.getNextTransactionId();
		final CountJobContext ctx = new CountJobContext();
		Delete.delete(TYPE.newQuery(), "DeleteTest", ctx);
		assertEquals(itemNumber, ctx.progress);
		assertEquals(transactionIdBefore+transactionNumber, model.getNextTransactionId());
		model.startTransaction("DeleteTest");
		for(final CacheIsolationItem token : items)
			assertFalse(token.existsCopeItem());
		model.commit();
	}

	private static final class Context extends AssertionErrorJobContext
	{
		private int progress = 0;
		private final int maximumProgress;

		Context(final int maximumProgress)
		{
			this.maximumProgress = maximumProgress;
		}

		@Override
		public void stopIfRequested()
		{
			if(progress>=maximumProgress) throw new JobStop("progress " + maximumProgress + " excessed with " + progress);
		}

		@Override
		public void incrementProgress()
		{
			progress++;
		}

		void assertProgress(final int expected)
		{
			assertEquals(expected, progress);
		}
	}
}

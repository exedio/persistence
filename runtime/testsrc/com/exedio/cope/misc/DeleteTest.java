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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.JobStop;
import java.util.ArrayList;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class DeleteTest extends TestWithEnvironment
{
	public DeleteTest()
	{
		super(CacheIsolationTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void testIt()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();

		model.startTransaction("setUp");
		final CacheIsolationItem i1 = new CacheIsolationItem("item1");
		final CacheIsolationItem i2 = new CacheIsolationItem("item1");
		assertEquals(true, i1.existsCopeItem());
		assertEquals(true, i2.existsCopeItem());
		model.commit();

		final Context ctx = new Context(2);
		Delete.delete(q, 100, "tx", ctx);
		model.startTransaction("setUp");
		assertEquals(false, i1.existsCopeItem());
		assertEquals(false, i2.existsCopeItem());
		model.commit();
		ctx.assertProgress(2);
	}

	@Test void testEmpty()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		final Context ctx = new Context(1);

		Delete.delete(q, 100, "tx", ctx);
		ctx.assertProgress(0);
	}

	@Test void testAbort()
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
			Delete.delete(q, 100, "tx", ctx);
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

	@Test void testTransactions()
	{
		assertPurge( 0, 1);
		assertPurge( 1, 1);
		assertPurge( 5, 1);
		assertPurge( 6, 2);
		assertPurge(11, 2);
		assertPurge(12, 3);
	}

	private void assertPurge(final int itemNumber, final int transactionNumber)
	{
		final ArrayList<CacheIsolationItem> items = new ArrayList<>();
		model.startTransaction("DeleteTest");
		for(int n = 0; n<itemNumber; n++)
			items.add(new CacheIsolationItem("item"+n));
		model.commit();
		final long transactionIdBefore = model.getNextTransactionId();
		final DeleteJobContext ctx = new DeleteJobContext(CacheIsolationTest.MODEL);
		Delete.delete(TYPE.newQuery(), 6, "DeleteTest", ctx);
		assertEquals(itemNumber, ctx.getProgress());
		assertEquals(transactionIdBefore+transactionNumber, model.getNextTransactionId());
		model.startTransaction("DeleteTest");
		for(final CacheIsolationItem token : items)
			assertFalse(token.existsCopeItem());
		model.commit();
	}

	private static final class Context extends DeleteJobContext
	{
		private final int maximumProgress;

		Context(final int maximumProgress)
		{
			super(CacheIsolationTest.MODEL);
			this.maximumProgress = maximumProgress;
		}

		@Override
		public void stopIfRequested()
		{
			super.stopIfRequested();
			final int progress = getProgress();
			if(progress>=maximumProgress) throw new JobStop("progress " + maximumProgress + " excessed with " + progress);
		}

		@Override
		protected void stopIfRequestedStackTraceOffset(final Iterator<StackTraceElement> st)
		{
			assertIt(Context.class, "stopIfRequested", st.next());
		}

		void assertProgress(final int expected)
		{
			assertEquals(expected, getProgress());
		}
	}
}

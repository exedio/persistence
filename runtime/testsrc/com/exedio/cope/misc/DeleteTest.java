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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.CacheIsolationItem;
import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.JobStop;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
		assertFails(
				() -> Delete.delete(q, 100, "tx", ctx),
				JobStop.class,
				"progress 1 excessed with 1");
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

	@Test void pageNone() { assertPage(TYPE.newQuery(), 3, 3); }
	@Test void pageOff0() { assertPage(queryPage(0), 3, 3); }
	@Test void pageOff1() { assertPage(queryPage(1), 3, 3, "it1"); }
	@Test void pageOff2() { assertPage(queryPage(2), 3, 2, "it1", "it2"); }
	@Test void pageOff3() { assertPage(queryPage(3), 3, 2, "it1", "it2", "it3"); }
	@Test void pageOff4() { assertPage(queryPage(4), 3, 2, "it1", "it2", "it3", "it4"); }
	@Test void pageOff5() { assertPage(queryPage(5), 3, 1, "it1", "it2", "it3", "it4", "it5"); }
	@Test void pageOff6() { assertPage(queryPage(6), 3, 1, "it1", "it2", "it3", "it4", "it5", "it6"); }
	@Test void pageOff7() { assertPage(queryPage(7), 3, 1, "it1", "it2", "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageOff8() { assertPage(queryPage(8), 3, 1, "it1", "it2", "it3", "it4", "it5", "it6", "it7"); }

	@Test void pageLim0() { assertPage(queryPage(0, 0), 3, 1, "it1", "it2", "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageLim1() { assertPage(queryPage(0, 1), 3, 1,        "it2", "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageLim2() { assertPage(queryPage(0, 2), 3, 1,               "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageLim3() { assertPage(queryPage(0, 3), 3, 1,                      "it4", "it5", "it6", "it7"); }
	@Test void pageLim4() { assertPage(queryPage(0, 4), 3, 2,                             "it5", "it6", "it7"); }
	@Test void pageLim5() { assertPage(queryPage(0, 5), 3, 2,                                    "it6", "it7"); }
	@Test void pageLim6() { assertPage(queryPage(0, 6), 3, 2,                                           "it7"); }
	@Test void pageLim7() { assertPage(queryPage(0, 7), 3, 3                                                 ); }
	@Test void pageLim8() { assertPage(queryPage(0, 8), 3, 3                                                 ); }

	@Test void pageOff1Lim0() { assertPage(queryPage(1, 0), 3, 1, "it1", "it2", "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim1() { assertPage(queryPage(1, 1), 3, 1, "it1",        "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim2() { assertPage(queryPage(1, 2), 3, 1, "it1",               "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim3() { assertPage(queryPage(1, 3), 3, 1, "it1",                      "it5", "it6", "it7"); }
	@Test void pageOff1Lim4() { assertPage(queryPage(1, 4), 3, 2, "it1",                             "it6", "it7"); }
	@Test void pageOff1Lim5() { assertPage(queryPage(1, 5), 3, 2, "it1",                                    "it7"); }
	@Test void pageOff1Lim6() { assertPage(queryPage(1, 6), 3, 2, "it1"                                          ); }
	@Test void pageOff1Lim7() { assertPage(queryPage(1, 7), 3, 3, "it1"                                          ); }

	@Test void pageOff1Lim0s() { assertPage(queryPage(1, 0), 1, 1, "it1", "it2", "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim1s() { assertPage(queryPage(1, 1), 1, 1, "it1",        "it3", "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim2s() { assertPage(queryPage(1, 2), 1, 2, "it1",               "it4", "it5", "it6", "it7"); }
	@Test void pageOff1Lim3s() { assertPage(queryPage(1, 3), 1, 3, "it1",                      "it5", "it6", "it7"); }
	@Test void pageOff1Lim4s() { assertPage(queryPage(1, 4), 1, 4, "it1",                             "it6", "it7"); }
	@Test void pageOff1Lim5s() { assertPage(queryPage(1, 5), 1, 5, "it1",                                    "it7"); }
	@Test void pageOff1Lim6s() { assertPage(queryPage(1, 6), 1, 6, "it1"                                          ); }
	@Test void pageOff1Lim7s() { assertPage(queryPage(1, 7), 1, 7, "it1"                                          ); }

	private static Query<CacheIsolationItem> queryPage(final int offset)
	{
		final Query<CacheIsolationItem> result = TYPE.newQuery();
		result.setPageUnlimited(offset);
		return result;
	}

	private static Query<CacheIsolationItem> queryPage(final int offset, final int limit)
	{
		final Query<CacheIsolationItem> result = TYPE.newQuery();
		result.setPage(offset, limit);
		return result;
	}

	private void assertPage(
			final Query<CacheIsolationItem> query,
			final int itemsPerTransaction,
			final int transactionCount,
			final String... codes)
	{
		model.startTransaction(DeleteTest.class.getName() + "#setUp");
		for(int i = 1; i<=7; i++)
			new CacheIsolationItem("it"+i);
		model.commit();

		final long transactionIdBefore = model.getNextTransactionId();
		Delete.delete(query, itemsPerTransaction, "tx", JobContexts.EMPTY);
		assertEquals(transactionCount, model.getNextTransactionId()-transactionIdBefore, "transactionCount");
		final Query<String> countQuery = new Query<>(CacheIsolationItem.name);
		countQuery.setOrderBy(TYPE.getThis(), true);
		model.startTransaction(DeleteTest.class.getName() + "#count");
		assertEquals(List.of(codes), countQuery.search());
		model.commit();
	}
}

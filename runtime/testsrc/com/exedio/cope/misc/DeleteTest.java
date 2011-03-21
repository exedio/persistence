/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.Query;
import com.exedio.cope.util.AssertionErrorJobContext;

public class DeleteTest extends AbstractRuntimeTest
{
	public DeleteTest()
	{
		super(CacheIsolationTest.MODEL);
		skipTransactionManagement();
	}

	public void testIt()
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

	public void testEmpty()
	{
		final Query<CacheIsolationItem> q = TYPE.newQuery();
		final Context ctx = new Context(0);

		Delete.delete(q, "tx", ctx);
		ctx.assertProgress(0);
	}

	public void testAbort()
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
		Delete.delete(q, "tx", ctx);
		model.startTransaction("setUp");
		assertEquals(false, i1.existsCopeItem());
		assertEquals(true,  i2.existsCopeItem());
		model.commit();
		ctx.assertProgress(1);
	}

	private static final class Context extends AssertionErrorJobContext
	{
		private int progress = 0;;
		private final int maximumProgress;

		Context(final int maximumProgress)
		{
			this.maximumProgress = maximumProgress;
		}

		@Override
		public boolean requestedToStop()
		{
			return progress>=maximumProgress;
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

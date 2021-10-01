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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Transaction;
import org.junit.jupiter.api.Test;

public class TransactionSlicerTest extends TestWithEnvironment
{
	public TransactionSlicerTest()
	{
		super(CacheIsolationTest.MODEL);
	}

	@Test void testIt()
	{
		final Transaction t1 = model.currentTransaction();

		final TransactionSlicer ts = new TransactionSlicer(model, 3);
		assertEquals(0, ts.getSliceCount());
		assertSame(t1, model.currentTransaction());

		assertEquals(false, ts.commitAndStartPossibly());
		assertEquals(0, ts.getSliceCount());
		assertSame(t1, model.currentTransaction());
		assertFalse(t1.isClosed());

		assertEquals(false, ts.commitAndStartPossibly());
		assertEquals(0, ts.getSliceCount());
		assertSame(t1, model.currentTransaction());
		assertFalse(t1.isClosed());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(1, ts.getSliceCount());
		final Transaction t2 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertTrue(t1.isClosed());
		assertFalse(t2.isClosed());
		assertEquals(t1.getName() + " slice1", t2.getName());

		assertEquals(false, ts.commitAndStartPossibly());
		assertEquals(1, ts.getSliceCount());
		assertSame(t2, model.currentTransaction());
		assertTrue(t1.isClosed());
		assertFalse(t2.isClosed());

		assertEquals(false, ts.commitAndStartPossibly());
		assertEquals(1, ts.getSliceCount());
		assertSame(t2, model.currentTransaction());
		assertTrue(t1.isClosed());
		assertFalse(t2.isClosed());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(2, ts.getSliceCount());
		final Transaction t3 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertNotSame(t1, t3);
		assertNotSame(t2, t3);
		assertTrue(t1.isClosed());
		assertTrue(t2.isClosed());
		assertFalse(t3.isClosed());
		assertEquals(t1.getName() + " slice2", t3.getName());
	}

	@Test void testEmpty()
	{
		final Transaction t1 = model.currentTransaction();

		final TransactionSlicer ts = new TransactionSlicer(model, 1);
		assertEquals(0, ts.getSliceCount());
		assertSame(t1, model.currentTransaction());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(1, ts.getSliceCount());
		final Transaction t2 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertTrue(t1.isClosed());
		assertFalse(t2.isClosed());
		assertEquals(t1.getName() + " slice1", t2.getName());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(2, ts.getSliceCount());
		final Transaction t3 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertNotSame(t1, t3);
		assertNotSame(t2, t3);
		assertTrue(t1.isClosed());
		assertTrue(t2.isClosed());
		assertFalse(t3.isClosed());
		assertEquals(t1.getName() + " slice2", t3.getName());

		try
		{
			new TransactionSlicer(null, 0);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("model", e.getMessage());
		}
		try
		{
			new TransactionSlicer(model, 0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("bitesPerSlice must be greater zero, but was 0", e.getMessage());
		}
	}

	@Test void testNoname()
	{
		model.commit();
		model.startTransaction(null);

		final Transaction t1 = model.currentTransaction();

		final TransactionSlicer ts = new TransactionSlicer(model, 1);
		assertEquals(0, ts.getSliceCount());
		assertSame(t1, model.currentTransaction());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(1, ts.getSliceCount());
		final Transaction t2 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertTrue(t1.isClosed());
		assertFalse(t2.isClosed());
		assertEquals("slice1", t2.getName());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(2, ts.getSliceCount());
		final Transaction t3 = model.currentTransaction();
		assertNotSame(t1, t2);
		assertNotSame(t1, t3);
		assertNotSame(t2, t3);
		assertTrue(t1.isClosed());
		assertTrue(t2.isClosed());
		assertFalse(t3.isClosed());
		assertEquals("slice2", t3.getName());
	}

	@Test void testQueryCacheDisabled()
	{
		assertEquals(false, model.currentTransaction().isQueryCacheDisabled());

		final TransactionSlicer ts = new TransactionSlicer(model, 1);
		assertEquals(false, model.currentTransaction().isQueryCacheDisabled());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(false, model.currentTransaction().isQueryCacheDisabled());

		model.currentTransaction().setQueryCacheDisabled(true);
		assertEquals(true, model.currentTransaction().isQueryCacheDisabled());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(true, model.currentTransaction().isQueryCacheDisabled());

		model.currentTransaction().setQueryCacheDisabled(false);
		assertEquals(false, model.currentTransaction().isQueryCacheDisabled());

		assertEquals(true, ts.commitAndStartPossibly());
		assertEquals(false, model.currentTransaction().isQueryCacheDisabled());
	}
}

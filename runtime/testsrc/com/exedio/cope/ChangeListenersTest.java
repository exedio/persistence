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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChangeListenersTest
{
	/**
	 * Makes tests tolerate previous tests.
	 * Needed for JDK 1.7
	 */
	private ChangeListenerInfo baselineInfo = model.getChangeListenersInfo();

	@Before public final void setUp()
	{
		baselineInfo = model.getChangeListenersInfo();
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		model.removeAllChangeListeners();
	}

	@Test public void testAddRemove()
	{
		assertInfo(0, 0, 0);

		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		final L l = new L();
		model.addChangeListener(l);
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(1, 0, 0);

		model.removeChangeListener(l);
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 1);
	}

	@Test public void testAddNull()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		try
		{
			model.addChangeListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);
	}

	@Test public void testRemoveNull()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		try
		{
			model.removeChangeListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);
	}

	@Test public void testRemoveMismatch()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		model.removeChangeListener(new L());
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		final L l = new L();
		model.addChangeListener(l);
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(1, 0, 0);

		model.removeChangeListener(new L());
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(1, 0, 0);
	}

	@Test public void testRemoveAll()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		final L l1 = new L();
		model.addChangeListener(l1);
		assertEqualsUnmodifiable(list(l1), model.getChangeListeners());
		assertInfo(1, 0, 0);

		final L l2 = new L();
		model.addChangeListener(l2);
		assertEqualsUnmodifiable(list(l1, l2), model.getChangeListeners());
		assertInfo(2, 0, 0);

		model.removeAllChangeListeners();
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 2);
	}

	@Test public void testRemoveAllEmpty()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);

		model.removeAllChangeListeners();
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0);
	}

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE_OF_NULL") // release to GC
	@Test public void testWeakness()
	{
		assertInfo(0, 0, 0);

		L l1 = new L();
		model.addChangeListener(l1);
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(1, 0, 0);

		System.gc();
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(1, 0, 0);

		l1 = null; // release to GC
		System.gc();
		assertInfo(1, 0, 0);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(0, 1, 0);

		final L l2 = new L();
		model.addChangeListener(l2);
		model.addChangeListener(new L());
		System.gc();
		model.removeChangeListener(l2);
		assertInfo(0, 2, 1);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(0, 2, 1);
	}

	private static final class L implements ChangeListener
	{
		L()
		{
			// make constructor non-private
		}

		public void onChange(final ChangeEvent event)
		{
			System.out.println("ERROR: MockListener.onChange");
			fail();
		}
	}

	private void assertInfo(final int size, final int cleared, final int removed)
	{
		final ChangeListenerInfo info = model.getChangeListenersInfo();
		assertEquals("size",    size,    info.getSize());
		assertEquals("cleared", cleared, info.getCleared() - baselineInfo.getCleared());
		assertEquals("removed", removed, info.getRemoved() - baselineInfo.getRemoved());
		assertEquals("failed",  0,       info.getFailed()  - baselineInfo.getFailed() );

		@SuppressWarnings("deprecation")
		final int clearedDeprecated = model.getChangeListenersCleared() - baselineInfo.getCleared();
		assertEquals(cleared, clearedDeprecated);

		try
		{
			model.getChangeListenerDispatcherInfo();
			fail();
		}
		catch(final Model.NotConnectedException e)
		{
			assertEquals(model, e.getModel());
		}
	}

	private static final Model model = new Model(TypesBound.newType(AnItem.class));

	private static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;

		private AnItem(final ActivationParameters ap) { super(ap); }
	}
}
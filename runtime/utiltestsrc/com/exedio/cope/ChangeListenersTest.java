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

package com.exedio.cope;

import com.exedio.cope.junit.CopeAssert;

public class ChangeListenersTest extends CopeAssert
{
	public void testIt()
	{
		final FailListener l = new FailListener();

		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0);

		model.addChangeListener(l);
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(0, 0);

		try
		{
			model.addChangeListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(0, 0);

		try
		{
			model.removeChangeListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(0, 0);

		assertInfo(0, 0);
		model.removeChangeListener(l);
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 1);
	}

	public void testWeakness()
	{
		FailListener l1 = new FailListener();
		model.addChangeListener(l1);
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(0, 1);

		System.gc();
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(0, 1);

		l1 = null;
		System.gc();
		assertInfo(0, 1);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(1, 1);

		final FailListener l2 = new FailListener();
		model.addChangeListener(l2);
		model.addChangeListener(new FailListener());
		System.gc();
		model.removeChangeListener(l2);
		assertInfo(2, 2);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(2, 2);
	}

	private final class FailListener implements ChangeListener
	{
		FailListener()
		{
			// make constructor non-private
		}

		public void onChange(final ChangeEvent event)
		{
			System.out.println("ERROR: MockListener.onChange");
			fail();
		}
	}

	private static void assertInfo(final int cleared, final int removed)
	{
		final ChangeListenerInfo info = model.getChangeListenersInfo();
		assertEquals(cleared, info.getCleared());
		assertEquals(removed, info.getRemoved());
		assertEquals(0,       info.getFailed());

		@SuppressWarnings("deprecation")
		final int clearedDeprecated = model.getChangeListenersCleared();
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

		private AnItem(final ActivationParameters ap)
		{
			super(ap);
		}
	}
}

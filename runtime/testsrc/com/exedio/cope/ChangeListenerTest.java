/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Collection;
import java.util.List;

public class ChangeListenerTest extends AbstractRuntimeTest
{
	public ChangeListenerTest()
	{
		super(MatchTest.MODEL);
	}

	final MockListener l = new MockListener();

	public void testIt()
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertEquals(0, model.getChangeListenersCleared());

		model.addChangeListener(l);
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertEquals(0, model.getChangeListenersCleared());

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
		assertEquals(0, model.getChangeListenersCleared());

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
		assertEquals(0, model.getChangeListenersCleared());

		final MatchItem item1 = deleteOnTearDown(new MatchItem("item1"));
		l.assertIt(null, null);
		final Transaction firstTransaction = model.currentTransaction();
		model.commit();
		l.assertIt(list(item1), firstTransaction);
		l.assertIt(null, null);

		model.startTransaction("CommitListenerTest2");
		assertEquals("item1", item1.getText());
		l.assertIt(null, null);
		model.commit();
		l.assertIt(null, null);

		final Transaction t3 = model.startTransaction("CommitListenerTest3");
		final MatchItem item2 = deleteOnTearDown(new MatchItem("item2"));
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item2), t3);

		final Transaction t4 = model.startTransaction("CommitListenerTest4");
		item1.setText("item1x");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1), t4);

		final Transaction t5 = model.startTransaction("CommitListenerTest5");
		item1.setText("item1y");
		item2.setText("item2y");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1, item2), t5);

		model.startTransaction("CommitListenerTest6");
		item1.setText("item1R");
		item2.setText("item2R");
		l.assertIt(null, null);
		model.rollback();
		l.assertIt(null, null);

		final Transaction t7 = model.startTransaction("CommitListenerTest7");
		final MatchItem item3 = new MatchItem("item3");
		item1.setText("item1z");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1, item3), t7);

		final Transaction t8 = model.startTransaction("CommitListenerTest8");
		item3.deleteCopeItem();
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item3), t8);

		final Transaction te = model.startTransaction("CommitListenerTestE");
		item1.setText("item1Exception");
		l.assertIt(null, null);
		l.exception = true;
		model.commit();
		l.assertIt(list(item1), te);
		assertEquals(false, l.exception);

		model.removeChangeListener(l);
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertEquals(0, model.getChangeListenersCleared());

		// test weakness
		FailListener l1 = new FailListener();
		model.addChangeListener(l1);
		assertEquals(list(l1), model.getChangeListeners());
		assertEquals(0, model.getChangeListenersCleared());

		System.gc();
		assertEquals(list(l1), model.getChangeListeners());
		assertEquals(0, model.getChangeListenersCleared());

		l1 = null;
		System.gc();
		assertEquals(0, model.getChangeListenersCleared());
		assertEquals(list(), model.getChangeListeners());
		assertEquals(1, model.getChangeListenersCleared());

		final FailListener l2 = new FailListener();
		model.addChangeListener(l2);
		model.addChangeListener(new FailListener());
		System.gc();
		model.removeChangeListener(l2);
		assertEquals(2, model.getChangeListenersCleared());
		assertEquals(list(), model.getChangeListeners());
		assertEquals(2, model.getChangeListenersCleared());

		model.startTransaction("CommitListenerTestX");
	}

	private final class MockListener implements ChangeListener
	{
		ChangeEvent event = null;
		boolean exception = false;

		MockListener()
		{
			// make constructor non-private
		}

		public void onChange(final ChangeEvent event)
		{
			final Collection<Item> items = event.getItems();

			assertTrue(items!=null);
			assertTrue(!items.isEmpty());
			assertUnmodifiable(items);

			assertTrue(event.getTransactionID()>=0);
			assertNotNull(event.getTransactionName());
			assertNotNull(event.getTransactionStartDate());

			assertTrue(this.event==null);

			assertContains(model.getOpenTransactions());
			try
			{
				model.currentTransaction();
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals("there is no cope transaction bound to this thread, see Model#startTransaction", e.getMessage());
			}

			this.event = event;

			if(exception)
			{
				exception = false;
				throw new NullPointerException("ChangeListener exception");
			}
		}

		void assertIt(final List<? extends Object> expectedItems, final Transaction expectedTransaction)
		{
			if(expectedTransaction!=null)
			{
				assertContainsList(expectedItems, event.getItems());
				assertEquals(expectedTransaction.getID(), event.getTransactionID());
				assertEquals(expectedTransaction.getName(), event.getTransactionName());
				assertEquals(expectedTransaction.getStartDate(), event.getTransactionStartDate());
				assertNull(expectedTransaction.getBoundThread());
				assertTrue(expectedTransaction.isClosed());
			}
			else
			{
				assertNull(expectedItems);
				assertNull(event);
			}

			event = null;
		}
	}

	private final class FailListener implements ChangeListener
	{
		FailListener()
		{
			// make constructor non-private
		}

		public void onChange(final ChangeEvent event)
		{
			throw new RuntimeException();
		}
	}
}

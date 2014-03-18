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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ChangeListenerTest extends AbstractRuntimeTest
{
	private static final Logger logger = Logger.getLogger(ChangeListeners.class);

	public ChangeListenerTest()
	{
		super(MatchTest.MODEL);
	}

	final MockListener l = new MockListener();

	TestLogAppender log = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		log = new TestLogAppender();
		logger.addAppender(log);
	}

	@Override
	protected void tearDown() throws Exception
	{
		logger.removeAppender(log);
		log = null;
		super.tearDown();
	}

	// dead store is needed to assign null for testing garbage collection
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE_OF_NULL")

	public void testIt() throws ChangeEvent.NotAvailableException
	{
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 0, 0);

		model.addChangeListener(l);
		assertEqualsUnmodifiable(list(l), model.getChangeListeners());
		assertInfo(1, 0, 0, 0);

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
		assertInfo(1, 0, 0, 0);

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
		assertInfo(1, 0, 0, 0);

		final MatchItem item1 = deleteOnTearDown(new MatchItem("item1"));
		l.assertIt(null, null);
		final Transaction firstTransaction = model.currentTransaction();
		model.commit();
		waitWhilePending();
		l.assertIt(list(item1), firstTransaction);
		l.assertIt(null, null);

		model.startTransaction("CommitListenerTest2");
		assertEquals("item1", item1.getText());
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(null, null);

		final Transaction t3 = model.startTransaction("CommitListenerTest3");
		final MatchItem item2 = deleteOnTearDown(new MatchItem("item2"));
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(list(item2), t3);

		final Transaction t4 = model.startTransaction("CommitListenerTest4");
		item1.setText("item1x");
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(list(item1), t4);

		final Transaction t5 = model.startTransaction("CommitListenerTest5");
		item1.setText("item1y");
		item2.setText("item2y");
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(list(item1, item2), t5);

		model.startTransaction("CommitListenerTest6");
		item1.setText("item1R");
		item2.setText("item2R");
		l.assertIt(null, null);
		model.rollback();
		waitWhilePending();
		l.assertIt(null, null);

		final Transaction t7 = model.startTransaction("CommitListenerTest7");
		final MatchItem item3 = new MatchItem("item3");
		item1.setText("item1z");
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(list(item1, item3), t7);

		final Transaction t8 = model.startTransaction("CommitListenerTest8");
		item3.deleteCopeItem();
		l.assertIt(null, null);
		model.commit();
		waitWhilePending();
		l.assertIt(list(item3), t8);

		log.assertEmpty();
		final Transaction te = model.startTransaction("CommitListenerTestE");
		item1.setText("item1Exception");
		l.assertIt(null, null);
		l.exception = true;
		model.commit();
		waitWhilePending();
		l.assertIt(list(item1), te);
		assertEquals(false, l.exception);
		log.assertMessage(Level.ERROR, "change listener [" + item1.toString() + "] " + l.toString());

		assertInfo(1, 0, 0, 1);
		model.removeChangeListener(l);
		assertEqualsUnmodifiable(list(), model.getChangeListeners());
		assertInfo(0, 0, 1, 1);

		// test weakness
		FailListener l1 = new FailListener();
		model.addChangeListener(l1);
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(1, 0, 1, 1);

		System.gc();
		assertEquals(list(l1), model.getChangeListeners());
		assertInfo(1, 0, 1, 1);

		l1 = null;
		System.gc();
		assertInfo(1, 0, 1, 1);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(0, 1, 1, 1);

		final FailListener l2 = new FailListener();
		model.addChangeListener(l2);
		model.addChangeListener(new FailListener());
		System.gc();
		model.removeChangeListener(l2);
		assertInfo(0, 2, 2, 1);
		assertEquals(list(), model.getChangeListeners());
		assertInfo(0, 2, 2, 1);

		model.startTransaction("CommitListenerTestX");
		log.assertEmpty();
	}

	private final class MockListener implements ChangeListener
	{
		ChangeEvent event = null;
		boolean exception = false;

		MockListener()
		{
			// make constructor non-private
		}

		public void onChange(final ChangeEvent event) throws IOException
		{
			final Collection<Item> items = event.getItems();

			assertNotNull(items);
			assertTrue(!items.isEmpty());
			assertUnmodifiable(items);
			assertEquals(items.toString(), event.toString());

			try
			{
				assertTrue(event.getTransactionID()>=0);
				assertNotNull(event.getTransactionName());
				assertNotNull(event.getTransactionStartDate());
			}
			catch(final ChangeEvent.NotAvailableException e)
			{
				throw new RuntimeException(e);
			}

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
				throw new IOException("ChangeListener exception");
			}
		}

		void assertIt(final List<? extends Object> expectedItems, final Transaction expectedTransaction) throws ChangeEvent.NotAvailableException
		{
			if(expectedTransaction!=null)
			{
				assertContainsList(expectedItems, event.getItems());
				assertEquals(expectedTransaction.getID(), event.getTransactionID());
				assertEquals(expectedTransaction.getName(), event.getTransactionName());
				assertEquals(expectedTransaction.getStartDate(), event.getTransactionStartDate());
				assertEquals(event.getItems().toString(), event.toString());
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

	private static final class FailListener implements ChangeListener
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

	private void waitWhilePending()
	{
		int shortcut = 50;
		while(model.getChangeListenerDispatcherInfo().getPending()>0)
		{
			try
			{
				Thread.sleep(1);
			}
			catch (final InterruptedException e)
			{
				throw new RuntimeException(e);
			}
			if((shortcut--)<0)
				fail("shortcut");
		}
		//System.out.println("-- " + (shortcut));

		// Sleep even longer, because the dispatcher thread
		// needs some more time after taking the event
		// out of the queue.
		try
		{
			sleepLongerThan(50);
		}
		catch (final InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void assertInfo(final int size, final int cleared, final int removed, final int failed)
	{
		final ChangeListenerInfo info = model.getChangeListenersInfo();
		assertEquals(size,    info.getSize());
		assertEquals(cleared, info.getCleared());
		assertEquals(removed, info.getRemoved());
		assertEquals(failed,  info.getFailed ());

		@SuppressWarnings("deprecation")
		final int clearedDeprecated = model.getChangeListenersCleared();
		assertEquals(cleared, clearedDeprecated);

		final ChangeListenerDispatcherInfo dispatcherInfo = model.getChangeListenerDispatcherInfo();
		assertEquals(0, dispatcherInfo.getOverflow ());
		assertEquals(0, dispatcherInfo.getException());
		assertEquals(0, dispatcherInfo.getPending  ());
	}

	public void testThreadControllers()
	{
		final String prefix = "COPE Change Listener Dispatcher ";
		final String prefix2 = prefix + model.toString() + ' ';

		final ArrayList<String> expectedAlive = new ArrayList<>();
		final ArrayList<String> expectedIdle  = new ArrayList<>();
		{
			final int num = model.getConnectProperties().changeListenersThreads;
			final int max = model.getConnectProperties().changeListenersThreadsMax;
			assert num<=max;
			for(int n = 0; n<max; n++)
				((n<num) ? expectedAlive : expectedIdle).add(prefix2 + String.valueOf(n+1) + '/' + max);
		}

		final ArrayList<String> actualAlive = new ArrayList<>();
		final ArrayList<String> actualIdle  = new ArrayList<>();
		for(final ThreadController c : model.getThreadControllers())
		{
			final String name = c.getName();
			if(name.startsWith(prefix))
			{
				assertTrue(name.startsWith(prefix2));
				(c.isAlive() ? actualAlive : actualIdle).add(name);
			}
		}
		assertEquals(expectedAlive, actualAlive);
		assertEquals(expectedIdle,  actualIdle);
	}
}

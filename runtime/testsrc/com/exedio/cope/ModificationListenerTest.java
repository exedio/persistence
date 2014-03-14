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

import com.exedio.cope.util.ModificationListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ModificationListenerTest extends AbstractRuntimeTest
{
	private static final Logger logger = Logger.getLogger(ModificationListeners.class);

	public ModificationListenerTest()
	{
		super(MatchTest.MODEL);
	}

	final MockListener l = new MockListener();

	TestLogAppender log = null;

	@Override()
	protected void setUp() throws Exception
	{
		super.setUp();
		log = new TestLogAppender();
		logger.addAppender(log);
	}

	@Override()
	protected void tearDown() throws Exception
	{
		logger.removeAppender(log);
		log = null;
		super.tearDown();
	}

	// dead store is needed to assign null for testing garbage collection
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE_OF_NULL")

	public void testIt()
	{
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		model.addModificationListener(l);
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		try
		{
			model.addModificationListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		try
		{
			model.removeModificationListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

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

		log.assertEmpty();
		final Transaction te = model.startTransaction("CommitListenerTestE");
		item1.setText("item1Exception");
		l.assertIt(null, null);
		l.exception = true;
		model.commit();
		l.assertIt(list(item1), te);
		assertEquals(false, l.exception);
		log.assertMessage(Level.ERROR, "Suppressing exception from modification listener " + MockListener.class.getName());

		model.removeModificationListener(l);
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		// test weakness
		FailListener l1 = new FailListener();
		model.addModificationListener(l1);
		assertEquals(list(l1), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		System.gc();
		assertEquals(list(l1), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		l1 = null;
		System.gc();
		assertEquals(0, model.getModificationListenersCleared());
		assertEquals(list(), model.getModificationListeners());
		assertEquals(1, model.getModificationListenersCleared());

		final FailListener l2 = new FailListener();
		model.addModificationListener(l2);
		model.addModificationListener(new FailListener());
		System.gc();
		model.removeModificationListener(l2);
		assertEquals(2, model.getModificationListenersCleared());
		assertEquals(list(), model.getModificationListeners());
		assertEquals(2, model.getModificationListenersCleared());

		model.startTransaction("CommitListenerTestX");
		log.assertEmpty();
	}

	private final class MockListener implements ModificationListener
	{
		Collection<Item> modifiedItems = null;
		Transaction transaction = null;
		boolean exception = false;

		MockListener()
		{
			// make constructor non-private
		}

		@Override()
		@Deprecated
		public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			assertTrue(modifiedItems!=null);
			assertTrue(!modifiedItems.isEmpty());
			assertUnmodifiable(modifiedItems);

			assertTrue(transaction.getID()>=0);
			assertNotNull(transaction.getName());
			assertNotNull(transaction.getStartDate());
			assertNull(transaction.getBoundThread());
			assertTrue(transaction.isClosed());

			assertTrue(this.modifiedItems==null);
			assertTrue(this.transaction==null);

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

			this.modifiedItems = modifiedItems;
			this.transaction = transaction;

			if(exception)
			{
				exception = false;
				throw new NullPointerException("ModificationListener exception");
			}
		}

		void assertIt(final List<? extends Object> expectedItems, final Transaction expectedTransaction)
		{
			assertContainsList(expectedItems, modifiedItems);
			assertSame(expectedTransaction, transaction);
			modifiedItems = null;
			transaction = null;
		}
	}

	private static final class FailListener implements ModificationListener
	{
		FailListener()
		{
			// make constructor non-private
		}

		@Override()
		@Deprecated
		public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			throw new RuntimeException();
		}
	}
}

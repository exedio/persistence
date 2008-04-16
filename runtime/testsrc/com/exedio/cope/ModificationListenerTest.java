/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.ModificationListener;

public class ModificationListenerTest extends AbstractLibTest
{
	public ModificationListenerTest()
	{
		super(MatchTest.MODEL);
	}
	
	final TestListener l = new TestListener();
	
	public void testCommitListener()
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
		catch(NullPointerException e)
		{
			assertEquals("listener must not be null", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());
		
		try
		{
			model.removeModificationListener(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("listener must not be null", e.getMessage());
		}
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());
		
		final MatchItem item1 = deleteOnTearDown(new MatchItem("item1"));
		l.assertIt(null, null);
		final Transaction firstTransaction = model.getCurrentTransaction();
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

		model.removeModificationListener(l);
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		// test weakness
		FailModificationListener l1 = new FailModificationListener();
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

		final FailModificationListener l2 = new FailModificationListener();
		model.addModificationListener(l2);
		model.addModificationListener(new FailModificationListener());
		System.gc();
		model.removeModificationListener(l2);
		assertEquals(2, model.getModificationListenersCleared());
		assertEquals(list(), model.getModificationListeners());
		assertEquals(2, model.getModificationListenersCleared());
		
		model.startTransaction("CommitListenerTestX");
	}
	
	private final class TestListener implements ModificationListener
	{
		Collection<Item> modifiedItems = null;
		Transaction transaction = null;
		boolean exception = false;
		
		TestListener()
		{
			// make constructor non-private
		}
		
		public void onModifyingCommit(final Collection<Item> modifiedItems, final Transaction transaction)
		{
			assertTrue(modifiedItems!=null);
			assertTrue(!modifiedItems.isEmpty());
			assertUnmodifiable(modifiedItems);
			assertTrue(transaction.isClosed());
			
			assertTrue(this.modifiedItems==null);
			assertTrue(this.transaction==null);
			
			assertContains(model.getOpenTransactions());
			try
			{
				model.getCurrentTransaction();
				fail();
			}
			catch(IllegalStateException e)
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

	private final class FailModificationListener implements ModificationListener
	{
		FailModificationListener()
		{
			// make constructor non-private
		}
		
		public void onModifyingCommit(Collection<Item> modifiedItems, Transaction transaction)
		{
			throw new RuntimeException();
		}
	}
}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
		assertEquals(0, model.getModificationListenersRemoved());

		model.addModificationListener(l);
		assertEqualsUnmodifiable(list(l), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersRemoved());

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
		assertEquals(0, model.getModificationListenersRemoved());
		
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
		assertEquals(0, model.getModificationListenersRemoved());
		
		final MatchItem item1 = new MatchItem("item1");
		deleteOnTearDown(item1);
		l.assertIt(null, null);
		final String firstTransactionName = model.getCurrentTransaction().getName();
		model.commit();
		l.assertIt(list(item1), firstTransactionName);
		l.assertIt(null, null);

		model.startTransaction("CommitListenerTest2");
		assertEquals("item1", item1.getText());
		l.assertIt(null, null);
		model.commit();
		l.assertIt(null, null);

		model.startTransaction("CommitListenerTest3");
		final MatchItem item2 = new MatchItem("item2");
		deleteOnTearDown(item2);
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item2), "CommitListenerTest3");
		
		model.startTransaction("CommitListenerTest4");
		item1.setText("item1x");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1), "CommitListenerTest4");
		
		model.startTransaction("CommitListenerTest5");
		item1.setText("item1y");
		item2.setText("item2y");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1, item2), "CommitListenerTest5");

		model.startTransaction("CommitListenerTest6");
		item1.setText("item1R");
		item2.setText("item2R");
		l.assertIt(null, null);
		model.rollback();
		l.assertIt(null, null);

		model.startTransaction("CommitListenerTest7");
		final MatchItem item3 = new MatchItem("item3");
		item1.setText("item1z");
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item1, item3), "CommitListenerTest7");

		model.startTransaction("CommitListenerTest8");
		item3.deleteCopeItem();
		l.assertIt(null, null);
		model.commit();
		l.assertIt(list(item3), "CommitListenerTest8");

		model.removeModificationListener(l);
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersRemoved());

		// test weakness
		model.addModificationListener(new FailModificationListener());
		assertEquals(1, model.getModificationListeners().size());
		assertEquals(0, model.getModificationListenersRemoved());
		
		System.gc();
		assertEquals(0, model.getModificationListenersRemoved());
		assertEquals(list(), model.getModificationListeners());
		assertEquals(1, model.getModificationListenersRemoved());

		final FailModificationListener l2 = new FailModificationListener();
		model.addModificationListener(l2);
		model.addModificationListener(new FailModificationListener());
		System.gc();
		model.removeModificationListener(l2);
		assertEquals(2, model.getModificationListenersRemoved());
		assertEquals(list(), model.getModificationListeners());
		assertEquals(2, model.getModificationListenersRemoved());
		
		model.startTransaction("CommitListenerTestX");
	}
	
	private final class TestListener implements ModificationListener
	{
		Collection<Item> modifiedItems = null;
		String transactionName = null;
		
		public void onModifyingCommit(final Collection<Item> modifiedItems, final String transactionName)
		{
			assertTrue(modifiedItems!=null);
			assertTrue(!modifiedItems.isEmpty());
			assertUnmodifiable(modifiedItems);
			
			assertTrue(this.modifiedItems==null);
			
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
			this.transactionName = transactionName;
		}
		
		void assertIt(final List<? extends Object> expectedItems, final String expectedTransactionName)
		{
			assertContainsList(expectedItems, modifiedItems);
			assertEquals(expectedTransactionName, transactionName);
			modifiedItems = null;
			transactionName = null;
		}
	}

	private final class FailModificationListener implements ModificationListener
	{
		public void onModifyingCommit(Collection<Item> modifiedItems, String transactionName)
		{
			throw new RuntimeException();
		}
	}
}

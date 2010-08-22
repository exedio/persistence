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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.exedio.cope.util.ChangeListener;

public class ClusterNetworkChangeListenerTest extends ClusterNetworkTest
{
	@Override
	protected void tearDown() throws Exception
	{
		modelB.rollbackIfNotCommitted();
		modelA.rollbackIfNotCommitted();
		modelA.tearDownSchema();
		super.tearDown();
	}

	public void testSinglecast() throws InterruptedException
	{
		modelA.connect(getProperties(false, 14446, 14447));
		modelB.connect(getProperties(false, 14447, 14446));
		modelA.createSchema();

		assertEquals("Connect Properties Context (14446>14447)", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context (14447>14446)", modelB.getConnectProperties().getContext().getDescription());

		final MockListener listenerA = new MockListener(modelA);
		final MockListener listenerB = new MockListener(modelB);
		modelA.addChangeListener(listenerA);
		modelB.addChangeListener(listenerB);

		final Transaction transactionA =
			modelA.startTransaction("ClusterNetworkChangeListenerTest#transactionA");
		final TypeA itemA = new TypeA();
		listenerA.assertEmpty();
		listenerB.assertEmpty();
		modelA.commit();
		listenerA.assertLocal(listg(itemA), transactionA);
		sleepLongerThan(50);
		listenerB.assertRemote(listg("TypeB-0"));
	}

	private final class MockListener implements ChangeListener
	{
		final Model model;
		Collection<Item> modifiedItems = null;
		TransactionInfo transaction = null;
		boolean exception = false;

		MockListener(final Model model)
		{
			this.model = model;
		}

		public void onModifyingCommit(final Collection<Item> modifiedItems, final TransactionInfo transaction)
		{
			assertTrue(modifiedItems!=null);
			assertTrue(!modifiedItems.isEmpty());
			assertUnmodifiable(modifiedItems);

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
				throw new NullPointerException("ChangeListener exception");
			}
		}

		void assertEmpty()
		{
			assertNull(modifiedItems);
			assertNull(transaction);
		}

		void assertLocal(final List<? extends Item> expectedItems, final Transaction expectedTransaction)
		{
			assertNotNull(modifiedItems);
			assertContainsList(expectedItems, modifiedItems);

			assertNotNull(transaction);
			assertEquals(false, transaction.isRemote());
			assertEquals(expectedTransaction.getID(), transaction.getID());
			assertEquals(expectedTransaction.getName(), transaction.getName());
			assertEquals(expectedTransaction.getStartDate(), transaction.getStartDate());
			assertNull(expectedTransaction.getBoundThread());
			assertTrue(expectedTransaction.isClosed());

			modifiedItems = null;
			transaction = null;
		}

		void assertRemote(final List<String> expectedItems)
		{
			assertNotNull(modifiedItems);

			final ArrayList<String> modifiedItemIds = new ArrayList<String>();
			for(final Item item : modifiedItems)
				modifiedItemIds.add(item.getCopeID());
			assertContainsList(expectedItems, modifiedItemIds);

			assertNotNull(transaction);
			assertEquals(true, transaction.isRemote());
			try
			{
				transaction.getID();
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals("not available", e.getMessage());
			}
			try
			{
				transaction.getName();
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals("not available", e.getMessage());
			}
			try
			{
				transaction.getStartDate();
				fail();
			}
			catch(final IllegalStateException e)
			{
				assertEquals("not available", e.getMessage());
			}

			modifiedItems = null;
			transaction = null;
		}
	}
}

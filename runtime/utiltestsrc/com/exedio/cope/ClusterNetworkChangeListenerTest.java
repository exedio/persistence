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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	@Test public void testSinglecast() throws InterruptedException, ChangeEvent.NotAvailableException
	{
		modelA.connect(getProperties(false, 14446, 14447));
		modelB.connect(getProperties(false, 14447, 14446));
		modelA.createSchema();

		assertEquals("Connect Properties Context (14446>14447)", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context (14447>14446)", modelB.getConnectProperties().getContext().getDescription());

		final MockListener listenerA = new MockListener(modelA, modelB);
		final MockListener listenerB = new MockListener(modelB, modelA);
		modelA.addChangeListener(listenerA);
		modelB.addChangeListener(listenerB);

		final Transaction transactionA =
			modelA.startTransaction("ClusterNetworkChangeListenerTest#transactionA");
		final TypeA itemA = new TypeA();
		listenerA.assertEmpty();
		listenerB.assertEmpty();
		modelA.commit();
		sleepLongerThan(50);
		listenerA.assertLocal(asList(itemA), transactionA);
		listenerB.assertRemote(asList("TypeB-0"));

		assertInfo(modelA);
		assertInfo(modelB);
	}

	private final class MockListener implements ChangeListener
	{
		final Model model;
		final Model remoteModel;
		ChangeEvent event = null;

		MockListener(final Model model, final Model remoteModel)
		{
			this.model = model;
			this.remoteModel = remoteModel;
		}

		public void onChange(final ChangeEvent event)
		{
			assertNotNull(event);
			final Collection<Item> items = event.getItems();
			assertTrue(items!=null);
			assertTrue(!items.isEmpty());
			assertUnmodifiable(items);

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
		}

		void assertEmpty()
		{
			assertInfo(modelA);
			assertInfo(modelB);

			assertNull(event);
		}

		void assertLocal(final List<? extends Item> expectedItems, final Transaction expectedTransaction) throws ChangeEvent.NotAvailableException
		{
			assertInfo(modelA);
			assertInfo(modelB);

			assertNotNull(event);
			assertContainsList(expectedItems, event.getItems());


			assertEquals(false, event.isRemote());
			try
			{
				event.getRemoteNodeID();
				fail();
			}
			catch(final ChangeEvent.NotAvailableException e)
			{
				assertEquals("not remote", e.getMessage());
			}
			assertEquals(expectedTransaction.getID(), event.getTransactionID());
			assertEquals(expectedTransaction.getName(), event.getTransactionName());
			assertEquals(expectedTransaction.getStartDate(), event.getTransactionStartDate());
			assertEquals("" + expectedItems + ' ' + expectedTransaction.getID() + " ClusterNetworkChangeListenerTest#transactionA", event.toString());
			assertNull(expectedTransaction.getBoundThread());
			assertTrue(expectedTransaction.isClosed());

			event = null;
		}

		void assertRemote(final List<String> expectedItems) throws ChangeEvent.NotAvailableException
		{
			assertInfo(modelA);
			assertInfo(modelB);

			assertNotNull(event);

			final ArrayList<String> itemIds = new ArrayList<>();
			for(final Item item : event.getItems())
				itemIds.add(item.getCopeID());
			assertContainsList(expectedItems, itemIds);


			assertEquals(true, event.isRemote());
			assertEquals(remoteModel.getClusterSenderInfo().getNodeID(), event.getRemoteNodeID());
			assertEquals(remoteModel.getClusterSenderInfo().getNodeIDString(), event.getRemoteNodeIDString());
			try
			{
				event.getTransactionID();
				fail();
			}
			catch(final ChangeEvent.NotAvailableException e)
			{
				assertEquals("remote", e.getMessage());
			}
			try
			{
				event.getTransactionName();
				fail();
			}
			catch(final ChangeEvent.NotAvailableException e)
			{
				assertEquals("remote", e.getMessage());
			}
			try
			{
				event.getTransactionStartDate();
				fail();
			}
			catch(final ChangeEvent.NotAvailableException e)
			{
				assertEquals("remote", e.getMessage());
			}
			assertEquals(expectedItems + " remote " + modelA.getClusterSenderInfo().getNodeIDString(), event.toString());

			event = null;
		}
	}

	static void assertInfo(final Model model)
	{
		assertEquals(0, model.getChangeListenersInfo().getFailed());

		final ChangeListenerDispatcherInfo dispatcherInfo =
			model.getChangeListenerDispatcherInfo();
		assertEquals("overflow",  0, dispatcherInfo.getOverflow ());
		assertEquals("exception", 0, dispatcherInfo.getException());
		assertEquals("pending",   0, dispatcherInfo.getPending  ());
	}
}

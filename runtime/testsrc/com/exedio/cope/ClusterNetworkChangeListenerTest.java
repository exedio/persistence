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

import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertContainsList;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.sleepLongerThan;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ChangeEvent.NotAvailableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class ClusterNetworkChangeListenerTest extends ClusterNetworkTest
{
	@AfterEach final void tearDown()
	{
		modelB.rollbackIfNotCommitted();
		modelA.rollbackIfNotCommitted();
		modelA.tearDownSchema();
	}

	@Test void testMulticast() throws InterruptedException, NotAvailableException
	{
		// when running this test alone, it fails on Windows if modelA is connected before modelB
		modelB.connect(getPropertiesMulticast());
		modelA.connect(getPropertiesMulticast());
		modelA.createSchema();

		assertEquals("Connect Properties Source (multicast)", modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (multicast)", modelB.getConnectProperties().getSource());

		test();
	}

	@Test void testSinglecast() throws InterruptedException, NotAvailableException
	{
		modelA.connect(getPropertiesSinglecast(true));
		modelB.connect(getPropertiesSinglecast(false));
		modelA.createSchema();

		assertEquals("Connect Properties Source (singlecast forward)",  modelA.getConnectProperties().getSource());
		assertEquals("Connect Properties Source (singlecast backward)", modelB.getConnectProperties().getSource());

		test();
	}

	private static void test() throws InterruptedException, NotAvailableException
	{
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

	private static final class MockListener implements ChangeListener
	{
		final Model model;
		final Model remoteModel;
		ChangeEvent event = null;

		MockListener(final Model model, final Model remoteModel)
		{
			this.model = model;
			this.remoteModel = remoteModel;
		}

		@Override
		public void onChange(final ChangeEvent event)
		{
			assertNotNull(event);
			final Collection<Item> items = event.getItems();
			assertNotNull(items);
			assertTrue(!items.isEmpty());
			assertUnmodifiable(items);

			assertSame(null, this.event);

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

			assertSame(null, event);
		}

		void assertLocal(final List<? extends Item> expectedItems, final Transaction expectedTransaction) throws NotAvailableException
		{
			assertInfo(modelA);
			assertInfo(modelB);

			assertNotNull(event);
			assertContainsList(expectedItems, event.getItems());


			assertEquals("local", event.getNodeID());
			assertEquals(false, event.isRemote());
			assertFails(() -> event.getRemoteNodeID(), NotAvailableException.class, "local");
			assertEquals(expectedTransaction.getID(), event.getTransactionID());
			assertEquals(expectedTransaction.getName(), event.getTransactionName());
			assertEquals(expectedTransaction.getStartDate(), event.getTransactionStartDate());
			assertEquals("" + expectedItems + ' ' + expectedTransaction.getID() + " ClusterNetworkChangeListenerTest#transactionA", event.toString());
			assertNull(expectedTransaction.getBoundThread());
			assertTrue(expectedTransaction.isClosed());

			event = null;
		}

		void assertRemote(final List<String> expectedItems) throws NotAvailableException
		{
			assertInfo(modelA);
			assertInfo(modelB);

			assertNotNull(event);

			final ArrayList<String> itemIds = new ArrayList<>();
			for(final Item item : event.getItems())
				itemIds.add(item.getCopeID());
			assertContainsList(expectedItems, itemIds);


			assertEquals(remoteModel.getClusterSenderInfo().getNodeIDString(), event.getNodeID());
			assertEquals(true, event.isRemote());
			assertEquals(remoteModel.getClusterSenderInfo().getNodeID(), event.getRemoteNodeID());
			assertEquals(remoteModel.getClusterSenderInfo().getNodeIDString(), event.getRemoteNodeIDString());
			assertFails(() -> event.getTransactionID(),        NotAvailableException.class, "remote");
			assertFails(() -> event.getTransactionName(),      NotAvailableException.class, "remote");
			assertFails(() -> event.getTransactionStartDate(), NotAvailableException.class, "remote");
			assertEquals(expectedItems + " remote " + modelA.getClusterSenderInfo().getNodeIDString(), event.toString());

			event = null;
		}
	}

	static void assertInfo(final Model model)
	{
		assertEquals(0, model.getChangeListenersInfo().getFailed());

		final ChangeListenerDispatcherInfo dispatcherInfo =
			model.getChangeListenerDispatcherInfo();
		assertEquals(0, dispatcherInfo.getOverflow (), "overflow");
		assertEquals(0, dispatcherInfo.getException(), "exception");
		assertEquals(0, dispatcherInfo.getPending  (), "pending");
	}
}

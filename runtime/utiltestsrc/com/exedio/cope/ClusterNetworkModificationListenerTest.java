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
import java.util.Collection;
import java.util.List;

public class ClusterNetworkModificationListenerTest extends ClusterNetworkTest
{
	@Override
	protected void tearDown() throws Exception
	{
		modelB.rollbackIfNotCommitted();
		modelA.rollbackIfNotCommitted();
		modelA.tearDownSchema();
		super.tearDown();
	}

	public void testSinglecast()
	{
		modelA.connect(getProperties(false, 14446, 14447));
		modelB.connect(getProperties(false, 14447, 14446));
		modelA.createSchema();

		assertEquals("Connect Properties Context (14446>14447)", modelA.getConnectProperties().getContext().getDescription());
		assertEquals("Connect Properties Context (14447>14446)", modelB.getConnectProperties().getContext().getDescription());

		final MockListener listenerA = new MockListener(modelA);
		final MockListener listenerB = new MockListener(modelB);
		modelA.addModificationListener(listenerA);
		modelB.addModificationListener(listenerB);

		final Transaction transactionA =
			modelA.startTransaction("ClusterNetworkModificationListenerTest#transactionA");
		final TypeA itemA = new TypeA();
		listenerA.assertIt(null, null);
		listenerB.assertIt(null, null);
		modelA.commit();
		listenerA.assertIt(list(itemA), transactionA);
		listenerB.assertIt(null, null); // this is a serious problem, but we cannot fix it without changing API incompatibly
	}

	private final class MockListener implements ModificationListener
	{
		final Model model;
		Collection<Item> modifiedItems = null;
		Transaction transaction = null;

		MockListener(final Model model)
		{
			this.model = model;
		}

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
		}

		void assertIt(final List<? extends Object> expectedItems, final Transaction expectedTransaction)
		{
			assertContainsList(expectedItems, modifiedItems);
			assertSame(expectedTransaction, transaction);
			modifiedItems = null;
			transaction = null;
		}
	}
}

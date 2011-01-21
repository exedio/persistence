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

import java.util.Arrays;

public class TracedItemsTest extends AbstractRuntimeTest
{
	public TracedItemsTest()
	{
		super(InstanceOfTest.MODEL);
	}

	InstanceOfAItem item1, item2;
	InstanceOfB1Item itemb1, itemb2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new InstanceOfAItem("itema1"));
		item2 = deleteOnTearDown(new InstanceOfAItem("itema2"));
		itemb1 = deleteOnTearDown(new InstanceOfB1Item("itemb1"));
		itemb2 = deleteOnTearDown(new InstanceOfB1Item("itemb2"));
	}

	public void testIt()
	{
		assertTraced();

		model.addTracedItem(item1);
		assertTraced(item1);

		model.addTracedItem(item2);
		assertTraced(item1, item2);

		model.removeTracedItem(item1);
		assertTraced(item2);

		model.clearTracedItems();
		assertTraced();

		model.addTracedItem(itemb1);
		assertTraced(itemb1);

		model.addTracedItem(itemb2);
		assertTraced(itemb1, itemb2);

		model.addTracedItem(item2);
		assertTraced(itemb1, itemb2, item2);

		model.clearTracedItems();
		assertTraced();
	}

	private void assertTraced(final Item... expected)
	{
		assertEqualsUnmodifiable(Arrays.asList(expected), model.getTracedItems());
		assertEquals(Arrays.asList(expected).contains(item1), model.tracedItems.matches(item1));
		assertEquals(Arrays.asList(expected).contains(item2), model.tracedItems.matches(item2));
		assertEquals(Arrays.asList(expected).contains(itemb1), model.tracedItems.matches(itemb1));
		assertEquals(Arrays.asList(expected).contains(itemb2), model.tracedItems.matches(itemb2));
	}
}

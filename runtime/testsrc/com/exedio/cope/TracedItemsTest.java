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

public class TracedItemsTest extends AbstractRuntimeTest
{
	public TracedItemsTest()
	{
		super(MatchTest.MODEL);
	}

	MatchItem item1, item2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new MatchItem());
		item2 = deleteOnTearDown(new MatchItem());
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(list(), model.getTracedItems());

		model.addTracedItem(item1);
		assertEqualsUnmodifiable(list(item1), model.getTracedItems());

		model.addTracedItem(item2);
		assertEqualsUnmodifiable(list(item1, item2), model.getTracedItems());

		model.removeTracedItem(item1);
		assertEqualsUnmodifiable(list(item2), model.getTracedItems());

		model.clearTracedItems();
		assertEqualsUnmodifiable(list(), model.getTracedItems());
	}
}

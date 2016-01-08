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

import static com.exedio.cope.PlusIntegerItem.TYPE;
import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numB;
import static com.exedio.cope.PlusIntegerItem.numC;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class PlusIntegerOrderNullTest extends AbstractRuntimeModelTest
{
	public PlusIntegerOrderNullTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	PlusIntegerItem item0;
	PlusIntegerItem item1;
	PlusIntegerItem item2;

	@Before public final void setUp()
	{
		item0 = new PlusIntegerItem(1, null, null);
		item1 = new PlusIntegerItem(2, 12,   null);
		item2 = new PlusIntegerItem(3, 13,   null);
	}

	@Test public void testIt()
	{
		assertOrder(list(item0, item1, item2), numA);
		assertOrder(list(item0, item1, item2), numC, numA);
		assertOrder(list(item0, item1, item2), numA, numC);

		assertOrder(list(item0, item1, item2), numB);
		assertOrder(list(item0, item1, item2), numB, numA);
		assertOrder(list(item0, item1, item2), numB, numC);
	}

	private static void assertOrder(final List<? extends Object> expectedOrder, final Function<?>... orderBy)
	{
		final Query<PlusIntegerItem> query = TYPE.newQuery(null);
		final boolean[] ascending = new boolean[orderBy.length];
		Arrays.fill(ascending, true);
		query.setOrderBy(orderBy, ascending);
		assertEquals(expectedOrder, query.search());

		final List<? extends Object> expectedReverseOrder = new ArrayList<>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		final boolean[] descending = new boolean[orderBy.length];
		Arrays.fill(descending, false);
		query.setOrderBy(orderBy, descending);
		assertEquals(expectedReverseOrder, query.search());
	}
}

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

import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numB;
import static com.exedio.cope.PlusIntegerItem.numC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeModelTest;

public class PlusIntegerOrderNullTest extends CopeModelTest
{
	public PlusIntegerOrderNullTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	boolean nullsLow;
	PlusIntegerItem item0;
	PlusIntegerItem item1;
	PlusIntegerItem item2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		nullsLow = model.nullsAreSortedLow();
		item0 = new PlusIntegerItem(1, null, null);
		item1 = new PlusIntegerItem(2, 12,   null);
		item2 = new PlusIntegerItem(3, 13,   null);
	}

	public void testIt()
	{
		assertOrder(list(item0, item1, item2), numA);
		assertOrder(list(item0, item1, item2), numC, numA);
		assertOrder(list(item0, item1, item2), numA, numC);

		assertOrder(nullsLow ? list(item0, item1, item2) : list(item1, item2, item0), numB);
		assertOrder(nullsLow ? list(item0, item1, item2) : list(item1, item2, item0), numB, numA);
		assertOrder(nullsLow ? list(item0, item1, item2) : list(item1, item2, item0), numB, numC);
	}

	private void assertOrder(final List<? extends Object> expectedOrder, final Function... orderBy)
	{
		final Query query = item0.TYPE.newQuery(null);
		final boolean[] ascending = new boolean[orderBy.length];
		Arrays.fill(ascending, true);
		query.setOrderBy(orderBy, ascending);
		assertEquals(expectedOrder, query.search());

		final List<? extends Object> expectedReverseOrder = new ArrayList<Object>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		final boolean[] descending = new boolean[orderBy.length];
		Arrays.fill(descending, false);
		query.setOrderBy(orderBy, descending);
		assertEquals(expectedReverseOrder, query.search());
	}
}

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

import static com.exedio.cope.AbstractRuntimeTest.l3;
import static com.exedio.cope.AbstractRuntimeTest.l7;
import static com.exedio.cope.AbstractRuntimeTest.l8;
import static com.exedio.cope.PlusLongItem.TYPE;
import static com.exedio.cope.PlusLongItem.multiplyBC;
import static com.exedio.cope.PlusLongItem.numA;
import static com.exedio.cope.PlusLongItem.numB;
import static com.exedio.cope.PlusLongItem.plusAB;
import static com.exedio.cope.tojunit.Assert.list;
import static java.lang.Long.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlusLongOrderTest extends TestWithEnvironment
{
	public PlusLongOrderTest()
	{
		super(PlusLongTest.MODEL);
	}

	PlusLongItem item1;
	PlusLongItem item2;
	PlusLongItem item3;

	@BeforeEach final void setUp()
	{
		item1 = new PlusLongItem(1l, 6l, -1000l);
		item2 = new PlusLongItem(2l, 1l, -1000l);
		item3 = new PlusLongItem(6l, 2l, -1000l);
	}

	@Test void testSumOrder()
	{
		assertEquals(l7, item1.getPlusAB());
		assertEquals(l3, item2.getPlusAB());
		assertEquals(l8, item3.getPlusAB());
		assertEquals(valueOf(-6000l), item1.getMultiplyBC());
		assertEquals(valueOf(-1000l), item2.getMultiplyBC());
		assertEquals(valueOf(-2000l), item3.getMultiplyBC());

		assertOrder(list(item1, item2, item3), numA);
		assertOrder(list(item2, item3, item1), numB);
		assertOrder(list(item2, item1, item3), plusAB);
		assertOrder(list(item1, item3, item2), multiplyBC);
	}

	private static void assertOrder(final List<?> expectedOrder, final Function<?> orderBy)
	{
		final Query<PlusLongItem> query = TYPE.newQuery(null);
		query.setOrderBy(orderBy, true);
		assertEquals(expectedOrder, query.search());

		final List<?> expectedReverseOrder = new ArrayList<>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		query.setOrderBy(orderBy, false);
		assertEquals(expectedReverseOrder, query.search());
	}
}

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

import static com.exedio.cope.AbstractRuntimeTest.d3;
import static com.exedio.cope.AbstractRuntimeTest.d7;
import static com.exedio.cope.AbstractRuntimeTest.d8;
import static com.exedio.cope.PlusDoubleItem.TYPE;
import static com.exedio.cope.PlusDoubleItem.multiplyBC;
import static com.exedio.cope.PlusDoubleItem.numA;
import static com.exedio.cope.PlusDoubleItem.numB;
import static com.exedio.cope.PlusDoubleItem.plusAB;
import static com.exedio.cope.tojunit.Assert.list;
import static java.lang.Double.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlusDoubleOrderTest extends TestWithEnvironment
{
	public PlusDoubleOrderTest()
	{
		super(PlusDoubleTest.MODEL);
	}

	PlusDoubleItem item1;
	PlusDoubleItem item2;
	PlusDoubleItem item3;

	@BeforeEach final void setUp()
	{
		item1 = new PlusDoubleItem(1.1, 6.6, -1000.99);
		item2 = new PlusDoubleItem(2.2, 1.1, -1000.99);
		item3 = new PlusDoubleItem(6.6, 2.2, -1000.99);
	}

	private static final double EPS = 0.000000000000001d;

	@Test void testSumOrder()
	{
		assertEquals(d7, item1.getPlusAB(), EPS);
		assertEquals(d3, item2.getPlusAB(), EPS);
		assertEquals(d8, item3.getPlusAB());
		assertEquals(valueOf(6.6 * -1000.99), item1.getMultiplyBC());
		assertEquals(valueOf(1.1 * -1000.99), item2.getMultiplyBC());
		assertEquals(valueOf(2.2 * -1000.99), item3.getMultiplyBC());

		assertOrder(list(item1, item2, item3), numA);
		assertOrder(list(item2, item3, item1), numB);
		assertOrder(list(item2, item1, item3), plusAB);
		assertOrder(list(item1, item3, item2), multiplyBC);
	}

	private static void assertOrder(final List<?> expectedOrder, final Function<?> orderBy)
	{
		final Query<PlusDoubleItem> query = TYPE.newQuery(null);
		query.setOrderBy(orderBy, true);
		assertEquals(expectedOrder, query.search());

		final List<?> expectedReverseOrder = new ArrayList<>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		query.setOrderBy(orderBy, false);
		assertEquals(expectedReverseOrder, query.search());
	}
}

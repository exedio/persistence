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
import java.util.Collections;
import java.util.List;

public class PlusDoubleOrderTest extends AbstractRuntimeTest
{
	public PlusDoubleOrderTest()
	{
		super(PlusDoubleTest.MODEL);
	}
	
	PlusDoubleItem item1;
	PlusDoubleItem item2;
	PlusDoubleItem item3;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new PlusDoubleItem(1.1, 6.6, -1000.99));
		item2 = deleteOnTearDown(new PlusDoubleItem(2.2, 1.1, -1000.99));
		item3 = deleteOnTearDown(new PlusDoubleItem(6.6, 2.2, -1000.99));
	}
	
	private static final double EPS = 0.000000000000001d;
	
	public void testSumOrder()
	{
		assertEquals(d7.doubleValue(), item1.getPlusAB(), EPS);
		assertEquals(d3.doubleValue(), item2.getPlusAB(), EPS);
		assertEquals(d8, item3.getPlusAB());
		assertEquals(new Double(6.6 * -1000.99), item1.getMultiplyBC());
		assertEquals(new Double(1.1 * -1000.99), item2.getMultiplyBC());
		assertEquals(new Double(2.2 * -1000.99), item3.getMultiplyBC());

		assertOrder(list(item1, item2, item3), item1.numA);
		assertOrder(list(item2, item3, item1), item1.numB);
		assertOrder(list(item2, item1, item3), item1.plusAB);
		assertOrder(list(item1, item3, item2), item1.multiplyBC);
	}

	private void assertOrder(final List<? extends Object> expectedOrder, final Function searchFunction)
	{
		final Query query = item1.TYPE.newQuery(null);
		query.setOrderBy(searchFunction, true);
		assertEquals(expectedOrder, query.search());

		final List<? extends Object> expectedReverseOrder = new ArrayList<Object>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		query.setOrderBy(searchFunction, false);
		assertEquals(expectedReverseOrder, query.search());
	}
}

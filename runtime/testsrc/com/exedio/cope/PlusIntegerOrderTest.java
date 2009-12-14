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

import static java.lang.Integer.valueOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.junit.CopeModelTest;

public class PlusIntegerOrderTest extends CopeModelTest
{
	public PlusIntegerOrderTest()
	{
		super(PlusIntegerTest.MODEL);
	}
	
	PlusIntegerItem item1;
	PlusIntegerItem item2;
	PlusIntegerItem item3;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = new PlusIntegerItem(1, 6, -1000);
		item2 = new PlusIntegerItem(2, 1, -1000);
		item3 = new PlusIntegerItem(6, 2, -1000);
	}
	
	public void testSumOrder()
	{
		assertEquals(valueOf(    7), item1.getPlusAB());
		assertEquals(valueOf(    3), item2.getPlusAB());
		assertEquals(valueOf(    8), item3.getPlusAB());
		assertEquals(valueOf(-6000), item1.getMultiplyBC());
		assertEquals(valueOf(-1000), item2.getMultiplyBC());
		assertEquals(valueOf(-2000), item3.getMultiplyBC());

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

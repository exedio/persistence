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

import static com.exedio.cope.CompareAssert.assertCompare;
import static com.exedio.cope.HierarchySuper.TYPE;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class HierarchyCompareTest extends TestWithEnvironment
{
	public HierarchyCompareTest()
	{
		super(HierarchyTest.MODEL);
	}

	List<Item> items;
	List<Item> orderby;

	@Before public final void setUp()
	{
		items = new ArrayList<>();
		final HierarchySingleSub c1 = new HierarchySingleSub(2, "x");
		final HierarchyFirstSub a1 = new HierarchyFirstSub(0);
		final HierarchySecondSub b1 = new HierarchySecondSub(1);
		final HierarchyFirstSub a2 = new HierarchyFirstSub(3);
		final HierarchySecondSub b2 = new HierarchySecondSub(4);
		final HierarchySingleSub c2 = new HierarchySingleSub(2, "x");
		items   = Arrays.asList(      a1, b1, a2, b2, c1, c2);
		orderby = Arrays.asList(a1, b1, a2, b2);
	}

	@Test public void testIt()
	{
		assertEquals(orderby, TYPE.search(null, TYPE.getThis(), true));
		assertCompare(items);
	}
}

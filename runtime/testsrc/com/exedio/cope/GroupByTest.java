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

import static com.exedio.cope.testmodel.FinalItem.TYPE;
import static com.exedio.cope.testmodel.FinalItem.finalString;
import static com.exedio.cope.testmodel.FinalItem.nonFinalInteger;

import com.exedio.cope.testmodel.FinalItem;
import java.util.List;

public class GroupByTest extends TestmodelTest
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		new FinalItem("foo", 1);
		new FinalItem("foo", 2);
		new FinalItem("foo", 3);
		new FinalItem("bar", 4);
		new FinalItem("bar", 5);
		new FinalItem("goo", 6);
		new FinalItem("car", 7);
		new FinalItem("car", 8);
	}

	@Test public void testSimpleCount()
	{
		final Query<FinalItem> items = TYPE.newQuery();
		assertCount(items, 8, 8);
	}

	@Test public void testSimpleCountWithLimit()
	{
		final Query<FinalItem> items = TYPE.newQuery();
		items.setLimit(0, 3);
		assertCount(items, 3, 8);
	}

	@Test public void testGroupByCount()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{finalString, nonFinalInteger.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(finalString);
		assertCount(items, 4, 4);
	}

	@Test public void testGroupByCountWithLimit()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{finalString, nonFinalInteger.min()};
		final Query<List<Object>> items = Query.newQuery(selection, TYPE, null);
		items.setGroupBy(finalString);
		items.setLimit(0, 3);
		assertCount(items, 3, 4);
	}

	private static void assertCount(final Query<?> items, final int expectedSize, final int expectedTotal)
	{
		assertEquals(expectedSize, items.search().size());
		assertEquals(expectedTotal, items.total());
	}
}

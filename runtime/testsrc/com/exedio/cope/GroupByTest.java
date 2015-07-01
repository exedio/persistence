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

import com.exedio.cope.search.ExtremumAggregate;
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

	public void testSimpleCount()
	{
		final Query<FinalItem> items = FinalItem.TYPE.newQuery();
		assertCount(items, 8, 8);
	}

	public void testSimpleCountWithLimit()
	{
		final Query<FinalItem> items = FinalItem.TYPE.newQuery();
		items.setLimit(0, 3);
		assertCount(items, 3, 8);
	}

	public void testGroupByCount()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{FinalItem.finalString, new ExtremumAggregate<>(FinalItem.nonFinalInteger, true)};
		final Query<List<Object>> items = Query.newQuery(selection, FinalItem.TYPE, null);
		items.setGroupBy(FinalItem.finalString);
		assertCount(items, 4, 4);
	}

	public void testGroupByCountWithLimit()
	{
		final Selectable<?>[] selection = new Selectable<?>[]{FinalItem.finalString, new ExtremumAggregate<>(FinalItem.nonFinalInteger, true)};
		final Query<List<Object>> items = Query.newQuery(selection, FinalItem.TYPE, null);
		items.setGroupBy(FinalItem.finalString);
		items.setLimit(0, 3);
		assertCount(items, 3, 4);
	}

	private void assertCount(final Query<?> items, final int expectedSize, final int expectedTotal)
	{
		assertEquals(expectedSize, items.search().size());
		assertEquals(expectedSize, items.searchAndTotal().getData().size());
		assertEquals(expectedTotal, items.searchAndTotal().getTotal());
	}
}

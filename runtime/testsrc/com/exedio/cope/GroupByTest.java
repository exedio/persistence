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

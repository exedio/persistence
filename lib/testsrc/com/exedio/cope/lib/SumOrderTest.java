package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.testmodel.SumItem;


public class SumOrderTest extends DatabaseLibTest
{
	SumItem item1;
	SumItem item2;
	SumItem item3;
	
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = new SumItem(1, 6, -1000);
		item2 = new SumItem(2, 1, -1000);
		item3 = new SumItem(6, 2, -1000);
	}
	
	public void tearDown() throws Exception
	{
		item1.delete();
		item2.delete();
		item3.delete();
		super.tearDown();
	}
	
	public void testSumOrder()
	{
		assertEquals(i7, item1.getSum12());
		assertEquals(i3, item2.getSum12());
		assertEquals(i8, item3.getSum12());

		assertOrder(list(item1, item2, item3), item1.num1);
		assertOrder(list(item2, item3, item1), item1.num2);
		assertOrder(list(item2, item1, item3), item1.sum12);
	}

	private void assertOrder(final List expectedOrder, final Function searchFunction)
	{
		final Query query = new Query(item1.TYPE, null);
		query.setOrderBy(searchFunction, true);
		assertEquals(expectedOrder, Search.search(query));

		final List expectedReverseOrder = new ArrayList(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		query.setOrderBy(searchFunction, false);
		assertEquals(expectedReverseOrder, Search.search(query));
	}
}

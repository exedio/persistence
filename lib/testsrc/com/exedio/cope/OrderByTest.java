/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public class OrderByTest extends TestmodelTest
{

	protected EmptyItem someItem, someItem2;
	protected AttributeItem item1, item2, item3, item4, item5, item;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(someItem2 = new EmptyItem());
		deleteOnTearDown(item1 = new AttributeItem("someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnum.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnum.enumValue2));
		deleteOnTearDown(item3 = new AttributeItem("someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue3));
		deleteOnTearDown(item4 = new AttributeItem("someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2));
		deleteOnTearDown(item5 = new AttributeItem("someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3));
		item = item1;
	}
	
	public void testOrderBy()
	{
		// no order at all
		assertContains(item4, item2, item1, item3, item5, new Query(item1.TYPE, null).search());

		// deterministic order only
		{
			final Query query = new Query(item1.TYPE, null);
			query.setDeterministicOrder(true);
			assertEquals(list(item1, item2, item3, item4, item5), query.search());
		}
		
		// simple order
		assertOrder(list(item5, item4, item3, item2, item1), item.someNotNullString);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item3, item5, item2, item4), item.someNotNullDouble);

		// limit
		{
			final Query q = new Query(item1.TYPE, null);
			try
			{
				q.setLimit(-1, 10);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("start must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.limitStart);
				assertEquals(q.UNLIMITED_COUNT, q.limitCount);
			}
			try
			{
				q.setLimit(-1);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("start must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.limitStart);
				assertEquals(q.UNLIMITED_COUNT, q.limitCount);
			}
			try
			{
				q.setLimit(0, -1);
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("count must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.limitStart);
				assertEquals(q.UNLIMITED_COUNT, q.limitCount);
			}
		}
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), item.someNotNullInteger, 0, -1);
		assertOrder(list(item1, item5), list(item3, item4), item.someNotNullInteger, 0, 2);
		assertOrder(list(), list(), item.someNotNullInteger, 0, 0);
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), item.someNotNullInteger, 0, 5);
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), item.someNotNullInteger, 0, 2000);

		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), item.someNotNullInteger, 1, -1);
		assertOrder(list(item5, item2), list(item4, item2), item.someNotNullInteger, 1, 2);
		assertOrder(list(), list(), item.someNotNullInteger, 1, 0);
		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), item.someNotNullInteger, 1, 4);
		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), item.someNotNullInteger, 1, 2000);

		assertOrder(list(), list(), item.someNotNullInteger, 5, -1);
		assertOrder(list(), list(), item.someNotNullInteger, 5, 2);
		assertOrder(list(), list(), item.someNotNullInteger, 5, 0);
		assertOrder(list(), list(), item.someNotNullInteger, 0, 0);
	}
	
	private void assertOrder(final List expectedOrder, final ObjectAttribute searchAttribute)
	{
		final List expectedReverseOrder = new ArrayList(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		assertOrder(expectedOrder, expectedReverseOrder, searchAttribute, 0, -1);
	}
	
	private void assertOrder(final List expectedOrder, final List expectedReverseOrder,
													final ObjectAttribute searchAttribute,
													final int limitStart, final int limitCount)
	{
		final Query query = new Query(item1.TYPE, null);
		query.setOrderBy(searchAttribute, true);
		query.setDeterministicOrder(true);

		if(limitCount==-1)
			query.setLimit(limitStart);
		else
			query.setLimit(limitStart, limitCount);
		
		assertEquals(expectedOrder, query.search());

		query.setOrderBy(searchAttribute, false);
		assertEquals(expectedReverseOrder, query.search());
		query.setOrderBy(searchAttribute, true);
		
		final Query.Result resultWithSizeWithoutRange = query.searchAndCountWithoutLimit();
		assertEquals(expectedOrder, resultWithSizeWithoutRange.getData());
		query.setLimit(0);
		final Collection resultWithoutRange = query.search();
		assertEquals(resultWithoutRange.size(), resultWithSizeWithoutRange.getCountWithoutRange());

		if(limitCount==-1)
			query.setLimit(limitStart);
		else
			query.setLimit(limitStart, limitCount);
	}
	
}

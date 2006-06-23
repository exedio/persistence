/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public class OrderByTest extends TestmodelTest
{

	protected EmptyItem someItem, someItem2;
	protected AttributeItem item1, item2, item3, item4, item5, item;

	@Override
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
		assertContains(item4, item2, item1, item3, item5, item1.TYPE.newQuery(null).search());

		// order by this only
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByThis(true);
			assertEquals(list(item1, item2, item3, item4, item5), query.search());
			assertEquals("select AttributeItem#this from AttributeItem order by AttributeItem#this", query.toString());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByThis(false);
			assertEquals(list(item5, item4, item3, item2, item1), query.search());
			assertEquals("select AttributeItem#this from AttributeItem order by AttributeItem#this desc", query.toString());
		}
		
		// simple order
		assertOrder(list(item5, item4, item3, item2, item1), item.someNotNullString);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item3, item5, item2, item4), item.someNotNullDouble);
		
		// order with multiple functions
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderBy(new Function[]{item1.someNotNullBoolean,item1.someNotNullInteger}, new boolean[]{true, true});
			assertEquals(list(item5, item4, item3, item1, item2), query.search());
			assertEquals("select AttributeItem#this from AttributeItem order by " + item1.someNotNullBoolean + ", " + item.someNotNullInteger, query.toString());
			
			query.setOrderBy(new Function[]{item1.someNotNullBoolean,item1.someNotNullInteger}, new boolean[]{false, true});
			assertEquals(list(item1, item2, item5, item4, item3), query.search());
			assertEquals("select AttributeItem#this from AttributeItem order by " + item1.someNotNullBoolean + " desc, " + item.someNotNullInteger, query.toString());
			
			query.setOrderBy(new Function[]{item1.someNotNullEnum,item1.someNotNullString}, new boolean[]{true, true});
			assertEquals(list(item1, item4, item2, item5, item3), query.search());
			
			// bad queries
			try
			{
				query.setOrderBy(new Function[]{item1.someNotNullBoolean,item1.someNotNullInteger}, new boolean[]{true});
				fail();
			}
			catch(RuntimeException e)
			{
				assertEquals("orderBy and ascending must have same length, but was 2 and 1", e.getMessage());
			}
			try
			{
				query.setOrderBy(new Function[]{item1.someNotNullBoolean,null}, new boolean[]{true, true});
				fail();
			}
			catch(NullPointerException e)
			{
				assertEquals("orderBy contains null at index 1", e.getMessage());
			}
			try
			{
				query.setOrderBy(null, true);
				fail();
			}
			catch(NullPointerException e)
			{
				assertEquals("orderBy is null", e.getMessage());
			}
		}

		// limit
		{
			final Query q = item1.TYPE.newQuery(null);
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
		assertOrder(list(), list(), item.someNotNullInteger, 9, 2); // important test for searchAndCountWithoutLimit
		assertOrder(list(), list(), item.someNotNullInteger, 9, -1); // important test for searchAndCountWithoutLimit
		assertOrder(list(), list(), item.someNotNullInteger, 5, 0);
		assertOrder(list(), list(), item.someNotNullInteger, 0, 0);
	}
	
	private void assertOrder(final List<? extends Object> expectedOrder, final FunctionAttribute orderFunction)
	{
		final List<? extends Object> expectedReverseOrder = new ArrayList<Object>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		assertOrder(expectedOrder, expectedReverseOrder, orderFunction, 0, -1);
	}
	
	private void assertOrder(final List<? extends Object> expectedOrder, final List<? extends Object> expectedReverseOrder,
													final FunctionAttribute orderFunction,
													final int limitStart, final int limitCount)
	{
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);
	
			if(limitCount==-1)
				query.setLimit(limitStart);
			else
				query.setLimit(limitStart, limitCount);
			
			assertEquals(expectedOrder, query.search());
			assertNotNull(query.toString());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, false);
	
			if(limitCount==-1)
				query.setLimit(limitStart);
			else
				query.setLimit(limitStart, limitCount);

			assertEquals(expectedReverseOrder, query.search());
		}
		{
			final Query query2 = new Query<String>(item.someNotNullString, item1.TYPE, null);
			query2.setOrderByAndThis(orderFunction, true);

			if(limitCount==-1)
				query2.setLimit(limitStart);
			else
				query2.setLimit(limitStart, limitCount);
			
			final ArrayList<String> expected = new ArrayList<String>(expectedOrder.size());
			for(Iterator<? extends Object> i = expectedOrder.iterator(); i.hasNext(); )
				expected.add(((AttributeItem)i.next()).getSomeNotNullString());

			assertEquals(expected, query2.search());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);
	
			if(limitCount==-1)
				query.setLimit(limitStart);
			else
				query.setLimit(limitStart, limitCount);
			
			final Query.Result resultWithSizeWithoutLimit = query.searchAndCountWithoutLimit();
			assertEquals(expectedOrder, resultWithSizeWithoutLimit.getData());
			query.setLimit(0);
			final Collection resultWithoutLimit = query.search();
			assertEquals(resultWithoutLimit.size(), resultWithSizeWithoutLimit.getCountWithoutLimit());
		}
	}
	
}

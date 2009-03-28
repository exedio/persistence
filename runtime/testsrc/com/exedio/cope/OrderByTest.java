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
		someItem = deleteOnTearDown(new EmptyItem());
		someItem2 = deleteOnTearDown(new EmptyItem());
		item1 = deleteOnTearDown(new AttributeItem("someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnum.enumValue1));
		item2 = deleteOnTearDown(new AttributeItem("someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnum.enumValue2));
		item3 = deleteOnTearDown(new AttributeItem("someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue3));
		item4 = deleteOnTearDown(new AttributeItem("someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2));
		item5 = deleteOnTearDown(new AttributeItem("someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3));
		item = item1;
	}
	
	public void testOrderBy()
	{
		{
			final Query q = item1.TYPE.newQuery(null);
			assertEquals(list(), q.getOrderByFunctions());
			assertEquals(list(), q.getOrderByAscending());
			
			q.setOrderByThis(false);
			assertEquals(list(item1.TYPE.getThis()), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());
			
			q.setOrderBy(item1.day, true);
			assertEquals(list(item1.day), q.getOrderByFunctions());
			assertEquals(list(true), q.getOrderByAscending());
			
			q.setOrderBy(item1.someBoolean, false);
			assertEquals(list(item1.someBoolean), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());
			
			q.setOrderByAndThis(item1.someBoolean, false);
			assertEquals(list(item1.someBoolean, item.TYPE.getThis()), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());
			
			q.setOrderBy(new Function[]{item1.someString, item1.someDate}, new boolean[]{false, true});
			assertEquals(list(item1.someString, item1.someDate), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());
			
			q.addOrderBy(item.someEnum, true);
			assertEquals(list(item1.someString, item1.someDate, item.someEnum), q.getOrderByFunctions());
			assertEquals(list(false, true, true), q.getOrderByAscending());

			q.resetOrderBy();
			assertEquals(list(), q.getOrderByFunctions());
			assertEquals(list(), q.getOrderByAscending());

			q.addOrderBy(item.someDouble, false);
			assertEquals(list(item1.someDouble), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());

			q.addOrderBy(item.someItem);
			assertEquals(list(item1.someDouble, item.someItem), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());

			q.addOrderByDescending(item.someLong);
			assertEquals(list(item1.someDouble, item.someItem, item.someLong), q.getOrderByFunctions());
			assertEquals(list(false, true, false), q.getOrderByAscending());
		}
		
		// no order at all
		assertContains(item4, item2, item1, item3, item5, item1.TYPE.newQuery(null).search());

		// order by this only
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByThis(true);
			assertEquals(list(item1, item2, item3, item4, item5), query.search());
			assertEquals("select this from AttributeItem order by this", query.toString());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByThis(false);
			assertEquals(list(item5, item4, item3, item2, item1), query.search());
			assertEquals("select this from AttributeItem order by this desc", query.toString());
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
			assertEquals("select this from AttributeItem order by " + item1.someNotNullBoolean.getName() + ", " + item.someNotNullInteger.getName(), query.toString());
			
			query.setOrderBy(new Function[]{item1.someNotNullBoolean,item1.someNotNullInteger}, new boolean[]{false, true});
			assertEquals(list(item1, item2, item5, item4, item3), query.search());
			assertEquals("select this from AttributeItem order by " + item1.someNotNullBoolean.getName() + " desc, " + item.someNotNullInteger.getName(), query.toString());
			
			query.setOrderBy(new Function[]{item1.someNotNullEnum,item1.someNotNullString}, new boolean[]{true, true});
			assertEquals(list(item1, item4, item2, item5, item3), query.search());
			
			// bad queries
			try
			{
				query.setOrderBy(new Function[]{item1.someNotNullBoolean,item1.someNotNullInteger}, new boolean[]{true});
				fail();
			}
			catch(IllegalArgumentException e)
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
				assertEquals("orderBy[1]", e.getMessage());
			}
			try
			{
				query.setOrderBy(null, true);
				fail();
			}
			catch(NullPointerException e)
			{
				assertEquals("orderBy", e.getMessage());
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
			catch(IllegalArgumentException e)
			{
				assertEquals("offset must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.offset);
				assertEquals(q.UNLIMITED, q.limit);
			}
			try
			{
				q.setLimit(-1);
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals("offset must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.offset);
				assertEquals(q.UNLIMITED, q.limit);
			}
			try
			{
				q.setLimit(0, -1);
				fail();
			}
			catch(IllegalArgumentException e)
			{
				assertEquals("limit must not be negative, but was -1", e.getMessage());
				assertEquals(0, q.offset);
				assertEquals(q.UNLIMITED, q.limit);
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
	
	private void assertOrder(final List<? extends Object> expectedOrder, final FunctionField orderFunction)
	{
		final List<? extends Object> expectedReverseOrder = new ArrayList<Object>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		assertOrder(expectedOrder, expectedReverseOrder, orderFunction, 0, -1);
	}
	
	private void assertOrder(final List<? extends Object> expectedOrder, final List<? extends Object> expectedReverseOrder,
													final FunctionField orderFunction,
													final int offset, final int limit)
	{
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);
	
			if(limit==-1)
				query.setLimit(offset);
			else
				query.setLimit(offset, limit);
			
			assertEquals(expectedOrder, query.search());
			assertNotNull(query.toString());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, false);
	
			if(limit==-1)
				query.setLimit(offset);
			else
				query.setLimit(offset, limit);

			assertEquals(expectedReverseOrder, query.search());
		}
		{
			final Query query2 = new Query<String>(item.someNotNullString, item1.TYPE, null);
			query2.setOrderByAndThis(orderFunction, true);

			if(limit==-1)
				query2.setLimit(offset);
			else
				query2.setLimit(offset, limit);
			
			final ArrayList<String> expected = new ArrayList<String>(expectedOrder.size());
			for(Iterator<? extends Object> i = expectedOrder.iterator(); i.hasNext(); )
				expected.add(((AttributeItem)i.next()).getSomeNotNullString());

			assertEquals(expected, query2.search());
		}
		{
			final Query query = item1.TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);
	
			if(limit==-1)
				query.setLimit(offset);
			else
				query.setLimit(offset, limit);
			
			final Query.Result resultAndTotal = query.searchAndTotal();
			assertEquals(expectedOrder, resultAndTotal.getData());
			assertEquals(5, resultAndTotal.getTotal());
			assertEquals(offset, resultAndTotal.getOffset());
			assertEquals(limit, resultAndTotal.getLimit());
			query.setLimit(0);
			final Collection resultWithoutLimit = query.search();
			assertEquals(5, resultWithoutLimit.size());
		}
	}
}

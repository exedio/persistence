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

import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.day;
import static com.exedio.cope.testmodel.AttributeItem.someBoolean;
import static com.exedio.cope.testmodel.AttributeItem.someDate;
import static com.exedio.cope.testmodel.AttributeItem.someDouble;
import static com.exedio.cope.testmodel.AttributeItem.someEnum;
import static com.exedio.cope.testmodel.AttributeItem.someItem;
import static com.exedio.cope.testmodel.AttributeItem.someLong;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullBoolean;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullDouble;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullEnum;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullInteger;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullString;
import static com.exedio.cope.testmodel.AttributeItem.someString;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderByTest extends TestmodelTest
{
	EmptyItem emptyItem, emptyItem2;
	AttributeItem item1, item2, item3, item4, item5;

	@BeforeEach final void setUp()
	{
		emptyItem = new EmptyItem();
		emptyItem2 = new EmptyItem();
		item1 = new AttributeItem("someString9", 1, 4l, 2.1, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		item2 = new AttributeItem("someString8", 3, 5l, 2.4, true, emptyItem, AttributeItem.SomeEnum.enumValue2);
		item3 = new AttributeItem("someString7", 5, 7l, 2.2, false, emptyItem, AttributeItem.SomeEnum.enumValue3);
		item4 = new AttributeItem("someString6", 4, 6l, 2.5, false, emptyItem2, AttributeItem.SomeEnum.enumValue2);
		item5 = new AttributeItem("someString5", 2, 3l, 2.3, false, emptyItem2, AttributeItem.SomeEnum.enumValue3);
	}

	@Test void testOrderBy()
	{
		{
			final Query<AttributeItem> q = TYPE.newQuery(null);
			assertEquals(list(), q.getOrderByFunctions());
			assertEquals(list(), q.getOrderByAscending());

			q.setOrderByThis(false);
			assertEquals(list(TYPE.getThis()), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());

			q.setOrderBy(day, true);
			assertEquals(list(day), q.getOrderByFunctions());
			assertEquals(list(true), q.getOrderByAscending());

			q.setOrderBy(someBoolean, false);
			assertEquals(list(someBoolean), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());

			q.setOrderByAndThis(someBoolean, false);
			assertEquals(list(someBoolean, TYPE.getThis()), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());

			q.setOrderBy(new Function<?>[]{someString, someDate}, new boolean[]{false, true});
			assertEquals(list(someString, someDate), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());

			q.addOrderBy(someEnum, true);
			assertEquals(list(someString, someDate, someEnum), q.getOrderByFunctions());
			assertEquals(list(false, true, true), q.getOrderByAscending());

			q.resetOrderBy();
			assertEquals(list(), q.getOrderByFunctions());
			assertEquals(list(), q.getOrderByAscending());

			q.addOrderBy(someDouble, false);
			assertEquals(list(someDouble), q.getOrderByFunctions());
			assertEquals(list(false), q.getOrderByAscending());

			q.addOrderBy(someItem);
			assertEquals(list(someDouble, someItem), q.getOrderByFunctions());
			assertEquals(list(false, true), q.getOrderByAscending());

			q.addOrderByDescending(someLong);
			assertEquals(list(someDouble, someItem, someLong), q.getOrderByFunctions());
			assertEquals(list(false, true, false), q.getOrderByAscending());
		}

		// no order at all
		assertContains(item4, item2, item1, item3, item5, TYPE.newQuery(null).search());

		// order by this only
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderByThis(true);
			assertEquals(list(item1, item2, item3, item4, item5), query.search());
			assertEquals("select this from AttributeItem order by this", query.toString());
		}
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderByThis(false);
			assertEquals(list(item5, item4, item3, item2, item1), query.search());
			assertEquals("select this from AttributeItem order by this desc", query.toString());
		}

		// simple order
		assertOrder(list(item5, item4, item3, item2, item1), someNotNullString);
		assertOrder(list(item1, item5, item2, item4, item3), someNotNullInteger);
		assertOrder(list(item1, item5, item2, item4, item3), someNotNullInteger);
		assertOrder(list(item1, item3, item5, item2, item4), someNotNullDouble);

		// order with multiple functions
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderBy(new Function<?>[]{someNotNullBoolean,someNotNullInteger}, new boolean[]{true, true});
			assertEquals(list(item5, item4, item3, item1, item2), query.search());
			assertEquals("select this from AttributeItem order by " + someNotNullBoolean.getName() + ", " + someNotNullInteger.getName(), query.toString());

			query.setOrderBy(new Function<?>[]{someNotNullBoolean,someNotNullInteger}, new boolean[]{false, true});
			assertEquals(list(item1, item2, item5, item4, item3), query.search());
			assertEquals("select this from AttributeItem order by " + someNotNullBoolean.getName() + " desc, " + someNotNullInteger.getName(), query.toString());

			query.setOrderBy(new Function<?>[]{someNotNullEnum,someNotNullString}, new boolean[]{true, true});
			assertEquals(list(item1, item4, item2, item5, item3), query.search());

			// bad queries
			try
			{
				query.setOrderBy(new Function<?>[]{someNotNullBoolean,someNotNullInteger}, new boolean[]{true});
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals("orderBy and ascending must have same length, but was 2 and 1", e.getMessage());
			}
			try
			{
				query.setOrderBy(new Function<?>[]{someNotNullBoolean,null}, new boolean[]{true, true});
				fail();
			}
			catch(final NullPointerException e)
			{
				assertEquals("orderBy[1]", e.getMessage());
			}
			try
			{
				query.setOrderBy(null, true);
				fail();
			}
			catch(final NullPointerException e)
			{
				assertEquals("orderBy", e.getMessage());
			}
		}
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), someNotNullInteger, 0, -1);
		assertOrder(list(item1, item5), list(item3, item4), someNotNullInteger, 0, 2);
		assertOrder(list(), list(), someNotNullInteger, 0, 0);
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), someNotNullInteger, 0, 5);
		assertOrder(list(item1, item5, item2, item4, item3), list(item3, item4, item2, item5, item1), someNotNullInteger, 0, 2000);

		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), someNotNullInteger, 1, -1);
		assertOrder(list(item5, item2), list(item4, item2), someNotNullInteger, 1, 2);
		assertOrder(list(), list(), someNotNullInteger, 1, 0);
		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), someNotNullInteger, 1, 4);
		assertOrder(list(item5, item2, item4, item3), list(item4, item2, item5, item1), someNotNullInteger, 1, 2000);

		assertOrder(list(), list(), someNotNullInteger, 5, -1);
		assertOrder(list(), list(), someNotNullInteger, 5, 2);
		assertOrder(list(), list(), someNotNullInteger, 9, 2); // important test for searchAndCountWithoutLimit
		assertOrder(list(), list(), someNotNullInteger, 9, -1); // important test for searchAndCountWithoutLimit
		assertOrder(list(), list(), someNotNullInteger, 5, 0);
		assertOrder(list(), list(), someNotNullInteger, 0, 0);
	}

	private static void assertOrder(final List<?> expectedOrder, final FunctionField<?> orderFunction)
	{
		final List<?> expectedReverseOrder = new ArrayList<>(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		assertOrder(expectedOrder, expectedReverseOrder, orderFunction, 0, -1);
	}

	private static void assertOrder(final List<?> expectedOrder, final List<?> expectedReverseOrder,
													final FunctionField<?> orderFunction,
													final int offset, final int limit)
	{
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);

			if(limit==-1)
				query.setPageUnlimited(offset);
			else
				query.setPage(offset, limit);

			assertEquals(offset, query.getPageOffset());
			assertEquals(limit, query.getPageLimitOrMinusOne());
			assertEquals(expectedOrder, query.search());
			assertEquals(5, query.total());
			assertEquals(true, query.exists());
			assertNotNull(query.toString());
		}
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, false);

			if(limit==-1)
				query.setPageUnlimited(offset);
			else
				query.setPage(offset, limit);

			assertEquals(offset, query.getPageOffset());
			assertEquals(limit, query.getPageLimitOrMinusOne());
			assertEquals(expectedReverseOrder, query.search());
			assertEquals(5, query.total());
			assertEquals(true, query.exists());
		}
		{
			final Query<String> query2 = new Query<>(someNotNullString, TYPE, null);
			query2.setOrderByAndThis(orderFunction, true);

			if(limit==-1)
				query2.setPageUnlimited(offset);
			else
				query2.setPage(offset, limit);

			assertEquals(offset, query2.getPageOffset());
			assertEquals(limit, query2.getPageLimitOrMinusOne());

			final ArrayList<String> expected = new ArrayList<>(expectedOrder.size());
			for(final Object object : expectedOrder)
				expected.add(((AttributeItem)object).getSomeNotNullString());

			assertEquals(expected, query2.search());
			assertEquals(5, query2.total());
			assertEquals(true, query2.exists());
		}
		{
			final Query<AttributeItem> query = TYPE.newQuery(null);
			query.setOrderByAndThis(orderFunction, true);

			if(limit==-1)
				query.setPageUnlimited(offset);
			else
				query.setPage(offset, limit);

			assertEquals(offset, query.getPageOffset());
			assertEquals(limit, query.getPageLimitOrMinusOne());

			final Query.Result<AttributeItem> resultAndTotal = query.searchAndTotal();
			assertEquals(expectedOrder, resultAndTotal.getData());
			assertEquals(5, resultAndTotal.getTotal());
			assertEquals(offset, resultAndTotal.getPageOffset());
			assertEquals(limit, resultAndTotal.getPageLimitOrMinusOne());
			query.setPageUnlimited(0);
			final Collection<AttributeItem> resultWithoutLimit = query.search();
			assertEquals(5, resultWithoutLimit.size());
		}
	}
}

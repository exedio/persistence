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

import static com.exedio.cope.RuntimeAssert.assertCondition;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someInteger;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullInteger;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullString;
import static com.exedio.cope.tojunit.Assert.assertContainsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import org.junit.jupiter.api.Test;

public class SearchTest extends TestmodelTest
{
	@Test void testIllegalSearch()
	{
		final Query<EmptyItem> illegalQuery = EmptyItem.TYPE.newQuery(someInteger.is(0));
		assertFails(
				illegalQuery::search,
				IllegalArgumentException.class,
				"AttributeItem.someInteger does not belong to a type of the query: " + illegalQuery);
		assertFails(
				illegalQuery::total,
				IllegalArgumentException.class,
				"AttributeItem.someInteger does not belong to a type of the query: " + illegalQuery);
		assertFails(
				illegalQuery::exists,
				IllegalArgumentException.class,
				"AttributeItem.someInteger does not belong to a type of the query: " + illegalQuery);
	}

	@Test void testSearch()
	{
		final EmptyItem someItem = new EmptyItem();
		final AttributeItem item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
		final AttributeItem item2 = new AttributeItem("someString2", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue2);
		item.setSomeNotNullInteger(0);
		assertCondition(item, TYPE, someNotNullInteger.is(0));
		assertCondition(item2, TYPE, someNotNullInteger.is(0).not());

		assertContainsUnmodifiable(item, item2, TYPE.search());
		assertContainsUnmodifiable(item, item2, TYPE.search(null));
		assertCondition(item, item2,
			TYPE,
				Cope.or(
					someNotNullString.is("someString"),
					someNotNullString.is("someString2")));
		assertCondition(
			TYPE,
				Cope.and(
					someNotNullString.is("someString"),
					someNotNullString.is("someString2")));

		// test Query#searchSingleton
		assertEquals(null, TYPE.searchSingleton(someNotNullString.is("someStringx")));
		assertEquals(item, TYPE.searchSingleton(someNotNullString.is("someString")));
		final Query<?> q = TYPE.newQuery();
		q.setOrderBy(TYPE.getThis(), true);
		try
		{
			q.searchSingleton();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"expected result of size one or less, " +
					"but was " + list(item, item2) + " for query: " +
					"select this from AttributeItem order by this",
				e.getMessage());
		}

		// test Query#searchSingletonStrict
		try
		{
			assertEquals(null, TYPE.searchSingletonStrict(someNotNullString.is("someStringx")));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"expected result of size one, " +
					"but was empty for query: " +
					"select this from AttributeItem where someNotNullString='someStringx'",
				e.getMessage());
		}
		assertEquals(item, TYPE.searchSingletonStrict(someNotNullString.is("someString")));
		q.setOrderBy(TYPE.getThis(), true);
		try
		{
			q.searchSingletonStrict();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"expected result of size one, " +
					"but was " + list(item, item2) + " for query: " +
					"select this from AttributeItem order by this",
				e.getMessage());
		}

		// Condition.Literal.get
		assertEquals(true,  Condition.ofTrue() .get(item));
		assertEquals(false, Condition.ofFalse().get(item));
		try
		{
			Condition.ofTrue().get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("item", e.getMessage());
		}
		try
		{
			Condition.ofFalse().get(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("item", e.getMessage());
		}
	}
}

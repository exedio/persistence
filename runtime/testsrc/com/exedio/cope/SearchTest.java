/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

public class SearchTest extends TestmodelTest
{
	public void testSearch()
	{
		// test conditions
		final AttributeItem x = null;
		assertEquals(
				Cope.and(x.someString.equal("a"),x.someNotNullString.equal("b")),
				Cope.and(x.someString.equal("a"),x.someNotNullString.equal("b")));
		assertNotEquals(
				Cope.and(x.someString.equal("aX"),x.someNotNullString.equal("b")),
				Cope.and(x.someString.equal("a"),x.someNotNullString.equal("b")));
		assertNotEquals(
				Cope.and(x.someString.equal("a"),x.someNotNullString.like("b")),
				Cope.and(x.someString.equal("a"),x.someNotNullString.equal("b")));
		assertNotEquals( // not commutative
				Cope.and(x.someString.equal("a"),x.someNotNullString.equal("b")),
				Cope.and(x.someNotNullString.equal("b"),x.someString.equal("a")));

		// test illegal searches
		final Query<EmptyItem> illegalQuery = EmptyItem.TYPE.newQuery(x.someInteger.equal(0));
		try
		{
			illegalQuery.search();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("AttributeItem.someInteger does not belong to a type of the query: " + illegalQuery, e.getMessage());
		}
		try
		{
			illegalQuery.total();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("AttributeItem.someInteger does not belong to a type of the query: " + illegalQuery, e.getMessage());
		}

		final EmptyItem someItem = new EmptyItem();
		final AttributeItem item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
		final AttributeItem item2 = new AttributeItem("someString2", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue2);
		item.setSomeNotNullInteger(0);
		assertContainsUnmodifiable(item, item.TYPE.search(item.someNotNullInteger.equal(0)));
		assertContainsUnmodifiable(item2, item.TYPE.search(item.someNotNullInteger.equal(0).not()));
		
		assertContainsUnmodifiable(item, item2, item.TYPE.search());
		assertContainsUnmodifiable(item, item2, item.TYPE.search(null));
		assertContainsUnmodifiable(item, item2,
			item.TYPE.search(
				Cope.or(
					item.someNotNullString.equal("someString"),
					item.someNotNullString.equal("someString2"))));
		assertContainsUnmodifiable(
			item.TYPE.search(
				Cope.and(
					item.someNotNullString.equal("someString"),
					item.someNotNullString.equal("someString2"))));
		
		// test Query#searchSingleton
		assertEquals(null, item.TYPE.searchSingleton(item.someNotNullString.equal("someStringx")));
		assertEquals(item, item.TYPE.searchSingleton(item.someNotNullString.equal("someString")));
		final Query q = item.TYPE.newQuery();
		q.setOrderBy(item.TYPE.getThis(), true);
		try
		{
			q.searchSingleton();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected result of size one or less, but was " + list(item, item2) + " for query: select this from AttributeItem order by this", e.getMessage());
		}
		
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
	}
}

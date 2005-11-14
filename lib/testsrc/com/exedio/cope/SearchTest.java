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

import com.exedio.cope.search.AndCondition;
import com.exedio.cope.search.Condition;
import com.exedio.cope.search.OrCondition;
import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

public class SearchTest extends TestmodelTest
{
	public void testUnmodifiableSearchResult()
			throws IntegrityViolationException
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
		try
		{
			EmptyItem.TYPE.search(AttributeItem.someInteger.equal(0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"function AttributeItem#someInteger belongs to type AttributeItem, which is not a type of the query: EmptyItem, []",
				e.getMessage());
		}
		
		try
		{
			new AndCondition(null);
			fail();
		}
		catch(NullPointerException e)
		{
			// fine
		}
		try
		{
			new OrCondition(null);
			fail();
		}
		catch(NullPointerException e)
		{
			// fine
		}
		try
		{
			new AndCondition(new Condition[0]);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}
		try
		{
			new OrCondition(new Condition[0]);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("composite condition must have at least one subcondition", e.getMessage());
		}

		final EmptyItem someItem = new EmptyItem();
		final AttributeItem item;
		final AttributeItem item2;
		try
		{
			item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
			item2 = new AttributeItem("someString2", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue2);
		}
		catch(MandatoryViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
		item.setSomeNotNullInteger(0);
		assertContainsUnmodifiable(item, item.TYPE.search(item.someNotNullInteger.equal(0)));
		assertContainsUnmodifiable(item2, item.TYPE.search(item.someNotNullInteger.equal(0).not()));
		
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
		
		assertDelete(item);
		assertDelete(item2);
		assertDelete(someItem);
	}
	
}

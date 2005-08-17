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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

public class SearchTest extends TestmodelTest
{
	public void testUnmodifiableSearchResult()
			throws IntegrityViolationException
	{
		final EmptyItem someItem = new EmptyItem();
		final AttributeItem item;
		final AttributeItem item2;
		try
		{
			item = new AttributeItem("someString", 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
			item2 = new AttributeItem("someString2", 5, 6l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue2);
		}
		catch(NotNullViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
		item.setSomeNotNullInteger(0);
		assertContainsUnmodifiable(item, item.TYPE.search(item.someNotNullInteger.equal(0)));
		
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

	public void testIllegalSearch()
	{
		try
		{
			EmptyItem.TYPE.search(AttributeItem.someInteger.equal(0));
			fail("should have thrown RuntimeException");
		}
		catch(RuntimeException e)
		{
			assertEquals(
				"function AttributeItem#someInteger{} belongs to type "+AttributeItem.class.getName()+", which is not a type of the query: "+EmptyItem.class.getName()+", []",
				e.getMessage());
		}
	}
	
}

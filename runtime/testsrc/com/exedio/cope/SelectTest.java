/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.AttributeItem.SomeEnum;

public class SelectTest extends TestmodelTest
{
	protected EmptyItem someItem, someItem2;
	protected AttributeItem item1, item2, item3, item4, item5, item;
	
	private AttributeItem newItem(
			final String initialSomeString,
			final String initialSomeNotNullString,
			final int initialSomeNotNullInteger,
			final long initialSomeNotNullLong,
			final double initialSomeNotNullDouble,
			final boolean initialSomeNotNullBoolean,
			final EmptyItem initialSomeNotNullItem,
			final SomeEnum initialSomeNotNullEnumeration) throws Exception
	{
		final AttributeItem result =
			deleteOnTearDown(new AttributeItem(
					initialSomeNotNullString, initialSomeNotNullInteger, initialSomeNotNullLong, initialSomeNotNullDouble,
					initialSomeNotNullBoolean, initialSomeNotNullItem, initialSomeNotNullEnumeration));
		result.setSomeString(initialSomeString);
		return result;
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		someItem = deleteOnTearDown(new EmptyItem());
		someItem2 = deleteOnTearDown(new EmptyItem());
		item1 = newItem("1z",     "someString9", 1, 4l, 2.1, true,  someItem,  AttributeItem.SomeEnum.enumValue1);
		item2 = newItem("1zz",    "someString8", 3, 5l, 2.4, true,  someItem,  AttributeItem.SomeEnum.enumValue2);
		item3 = newItem("1zzz",   "someString7", 5, 7l, 2.2, false, someItem,  AttributeItem.SomeEnum.enumValue3);
		item4 = newItem("1zzzz",  "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2);
		item5 = newItem("1zzzzz", "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3);
		item = item1;
	}

	public void testSelect()
	{
		final Query query = new Query(
				new Function[]{
						item.someString, item.someStringUpperCase, item.someStringLength, item.someNotNullString,
						item.someNotNullInteger, item.someNotNullLong, item.someNotNullDouble,
						item.someNotNullBoolean, item.someNotNullItem, item.someNotNullEnum},
				item.TYPE,
				null);
		query.setOrderBy(item.someNotNullString, false);
		final Collection result = query.search();
		assertNotNull(query.toString());
		final Iterator i = result.iterator();
		
		assertRow(i, "1z",     "1Z",     2, "someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnum.enumValue1);
		assertRow(i, "1zz",    "1ZZ",    3, "someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnum.enumValue2);
		assertRow(i, "1zzz",   "1ZZZ",   4, "someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue3);
		assertRow(i, "1zzzz",  "1ZZZZ",  5, "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2);
		assertRow(i, "1zzzzz", "1ZZZZZ", 6, "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3);
	}
	
	private static final void assertRow(
			final Iterator i,
			final String someString,
			final String someStringUppercase,
			final int someStringLength,
			final String someNotNullString,
			final int someNotNullInteger,
			final long someNotNullLong,
			final double someNotNullDouble,
			final boolean someNotNullBoolean,
			final EmptyItem someNotNullItem,
			final SomeEnum someNotNullEnumeration)
	{
		assertEqualsUnmodifiable(list(
				someString, someStringUppercase, Integer.valueOf(someStringLength),
				someNotNullString, Integer.valueOf(someNotNullInteger), Long.valueOf(someNotNullLong),
				new Double(someNotNullDouble), new Boolean(someNotNullBoolean), someNotNullItem, someNotNullEnumeration),
			(List<?>)i.next());
	}
	
}

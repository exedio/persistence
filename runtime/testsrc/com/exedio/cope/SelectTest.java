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

import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullBoolean;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullDouble;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullEnum;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullInteger;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullItem;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullLong;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullString;
import static com.exedio.cope.testmodel.AttributeItem.someString;
import static com.exedio.cope.testmodel.AttributeItem.someStringLength;
import static com.exedio.cope.testmodel.AttributeItem.someStringUpperCase;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.AttributeItem.SomeEnum;
import com.exedio.cope.testmodel.EmptyItem;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SelectTest extends TestmodelTest
{
	EmptyItem someItem, someItem2;
	@SuppressWarnings("unused") // OK: items must exist in database
	AttributeItem item1, item2, item3, item4, item5, item;

	private static AttributeItem newItem(
			final String initialSomeString,
			final String initialSomeNotNullString,
			final int initialSomeNotNullInteger,
			final long initialSomeNotNullLong,
			final double initialSomeNotNullDouble,
			final boolean initialSomeNotNullBoolean,
			final EmptyItem initialSomeNotNullItem,
			final SomeEnum initialSomeNotNullEnumeration)
	{
		final AttributeItem result =
			new AttributeItem(
					initialSomeNotNullString, initialSomeNotNullInteger, initialSomeNotNullLong, initialSomeNotNullDouble,
					initialSomeNotNullBoolean, initialSomeNotNullItem, initialSomeNotNullEnumeration);
		result.setSomeString(initialSomeString);
		return result;
	}

	@BeforeEach final void setUp()
	{
		someItem = new EmptyItem();
		someItem2 = new EmptyItem();
		item1 = newItem("1z",     "someString9", 1, 4l, 2.1, true,  someItem,  AttributeItem.SomeEnum.enumValue1);
		item2 = newItem("1zz",    "someString8", 3, 5l, 2.4, true,  someItem,  AttributeItem.SomeEnum.enumValue2);
		item3 = newItem("1zzz",   "someString7", 5, 7l, 2.2, false, someItem,  AttributeItem.SomeEnum.enumValue3);
		item4 = newItem("1zzzz",  "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2);
		item5 = newItem("1zzzzz", "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3);
		item = item1;
	}

	@Test void testSelect()
	{
		final Query<List<Object>> query = newQuery(
				new Function<?>[]{
						someString, someStringUpperCase, someStringLength, someNotNullString,
						someNotNullInteger, someNotNullLong, someNotNullDouble,
						someNotNullBoolean, someNotNullItem, someNotNullEnum},
				TYPE,
				null);
		query.setOrderBy(someNotNullString, false);
		final Collection<List<Object>> result = query.search();
		assertNotNull(query.toString());
		final Iterator<List<Object>> i = result.iterator();

		assertRow(i, "1z",     "1Z",     2, "someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnum.enumValue1);
		assertRow(i, "1zz",    "1ZZ",    3, "someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnum.enumValue2);
		assertRow(i, "1zzz",   "1ZZZ",   4, "someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnum.enumValue3);
		assertRow(i, "1zzzz",  "1ZZZZ",  5, "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnum.enumValue2);
		assertRow(i, "1zzzzz", "1ZZZZZ", 6, "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnum.enumValue3);
	}

	private static void assertRow(
			final Iterator<List<Object>> i,
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
				Double.valueOf(someNotNullDouble), Boolean.valueOf(someNotNullBoolean), someNotNullItem, someNotNullEnumeration),
			i.next());
	}

}

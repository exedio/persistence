
package com.exedio.cope.lib;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.AttributeItem.SomeEnumeration;

public class SelectTest extends DatabaseLibTest
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
			final SomeEnumeration initialSomeNotNullEnumeration) throws Exception
	{
		final AttributeItem result =
			new AttributeItem(
					initialSomeNotNullString, initialSomeNotNullInteger, initialSomeNotNullLong, initialSomeNotNullDouble,
					initialSomeNotNullBoolean, initialSomeNotNullItem, initialSomeNotNullEnumeration);
		deleteOnTearDown(result);
		result.setSomeString(initialSomeString);
		return result;
	}

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(someItem2 = new EmptyItem());
		item1 = newItem("1z",     "someString9", 1, 4l, 2.1, true,  someItem,  AttributeItem.SomeEnumeration.enumValue1);
		item2 = newItem("1zz",    "someString8", 3, 5l, 2.4, true,  someItem,  AttributeItem.SomeEnumeration.enumValue2);
		item3 = newItem("1zzz",   "someString7", 5, 7l, 2.2, false, someItem,  AttributeItem.SomeEnumeration.enumValue3);
		item4 = newItem("1zzzz",  "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnumeration.enumValue2);
		item5 = newItem("1zzzzz", "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue3);
		item = item1;
	}

	public void testSelect()
	{
		final Query query = new Query(
				new Selectable[]{
						item.someString, item.someStringUpperCase, item.someStringLength, item.someNotNullString,
						item.someNotNullInteger, item.someNotNullLong, item.someNotNullDouble,
						item.someNotNullBoolean, item.someNotNullItem, item.someNotNullEnumeration},
				item.TYPE,
				null);
		query.setOrderBy(item.someNotNullString, false);
		final Collection result = query.search();
		final Iterator i = result.iterator();
		
		assertRow(i, "1z",     "1Z",     2, "someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		assertRow(i, "1zz",    "1ZZ",    3, "someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue2);
		assertRow(i, "1zzz",   "1ZZZ",   4, "someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue3);
		assertRow(i, "1zzzz",  "1ZZZZ",  5, "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnumeration.enumValue2);
		assertRow(i, "1zzzzz", "1ZZZZZ", 6, "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue3);
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
			final SomeEnumeration someNotNullEnumeration)
	{
		final List actual = (List)i.next();
		assertUnmodifiable(actual);
		assertEquals(list(
				someString, someStringUppercase, new Integer(someStringLength),
				someNotNullString, new Integer(someNotNullInteger), new Long(someNotNullLong),
				new Double(someNotNullDouble), new Boolean(someNotNullBoolean), someNotNullItem, someNotNullEnumeration),
			actual);
		
	}

}

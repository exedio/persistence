
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

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(someItem2 = new EmptyItem());
		deleteOnTearDown(item1 = new AttributeItem("someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue2));
		deleteOnTearDown(item3 = new AttributeItem("someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue3));
		deleteOnTearDown(item4 = new AttributeItem("someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnumeration.enumValue2));
		deleteOnTearDown(item5 = new AttributeItem("someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue3));
		item = item1;
	}

	public void testSelect()
	{
		final Query query = new Query(
				new Selectable[]{item.someNotNullString, item.someNotNullInteger, item.someNotNullLong, item.someNotNullDouble, item.someNotNullBoolean, item.someNotNullItem, item.someNotNullEnumeration},
				new Type[]{item.TYPE},
				null);
		query.setOrderBy(item.someNotNullString, false);
		final Collection result = Cope.search(query);
		final Iterator i = result.iterator();
		
		assertRow(i, "someString9", 1, 4l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		assertRow(i, "someString8", 3, 5l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue2);
		assertRow(i, "someString7", 5, 7l, 2.2, false, someItem, AttributeItem.SomeEnumeration.enumValue3);
		assertRow(i, "someString6", 4, 6l, 2.5, false, someItem2, AttributeItem.SomeEnumeration.enumValue2);
		assertRow(i, "someString5", 2, 3l, 2.3, false, someItem2, AttributeItem.SomeEnumeration.enumValue3);
	}
	
	private static final void assertRow(
			final Iterator i,
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
				someNotNullString, new Integer(someNotNullInteger), new Long(someNotNullLong),
				new Double(someNotNullDouble), new Boolean(someNotNullBoolean), someNotNullItem, someNotNullEnumeration),
			actual);
		
	}

}

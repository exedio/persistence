package com.exedio.cope.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.AttributeItem.SomeEnumeration;


public class OrderByTest extends DatabaseLibTest
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
	
	public void testOrderBy()
	{
		// no order at all
		assertContains(item4, item2, item1, item3, item5, Cope.search(new Query(item1.TYPE, null)));

		// deterministic order only
		{
			final Query query = new Query(item1.TYPE, null);
			query.setDeterministicOrder(true);
			assertEquals(list(item1, item2, item3, item4, item5), Cope.search(query));
		}
		
		// simple order
		assertOrder(list(item5, item4, item3, item2, item1), item.someNotNullString);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item5, item2, item4, item3), item.someNotNullInteger);
		assertOrder(list(item1, item3, item5, item2, item4), item.someNotNullDouble);

		// range
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
		assertOrder(list(), list(), item.someNotNullInteger, 5, 0);
	}
	
	private void assertOrder(final List expectedOrder, final ObjectAttribute searchAttribute)
	{
		final List expectedReverseOrder = new ArrayList(expectedOrder);
		Collections.reverse(expectedReverseOrder);
		assertOrder(expectedOrder, expectedReverseOrder, searchAttribute, 0, -1);
	}
	
	private void assertOrder(final List expectedOrder, final List expectedReverseOrder,
													final ObjectAttribute searchAttribute, final int start, final int count)
	{
		final Query query = new Query(item1.TYPE, null);
		query.setOrderBy(searchAttribute, true);
		query.setDeterministicOrder(true);
		query.setRange(start, count);
		assertEquals(expectedOrder, Cope.search(query));

		query.setOrderBy(searchAttribute, false);
		assertEquals(expectedReverseOrder, Cope.search(query));
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
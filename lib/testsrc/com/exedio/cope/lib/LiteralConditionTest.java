
package com.exedio.cope.lib;

import java.util.Date;

public class LiteralConditionTest extends DatabaseLibTest
{
	EmptyItem someItem;
	AttributeItem item1;
	AttributeItem item2;
	AttributeItem item3;
	AttributeItem item4;
	AttributeItem item5;
	Date date;
	
	private static int LONG_OFFSET = 4000;
	
	private void setDate(final AttributeItem item, final Date date)
	{
		item.setSomeDate(date);
		item.setSomeLongDate(offset(date, LONG_OFFSET));
	}
	
	private Date offset(final Date date, final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		someItem = new EmptyItem();
		item1 = new AttributeItem("string1", 1, 11l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item2 = new AttributeItem("string2", 2, 12l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1);
		item3 = new AttributeItem("string3", 3, 13l, 2.3, true, someItem, AttributeItem.SomeEnumeration.enumValue2);
		item4 = new AttributeItem("string4", 4, 14l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue3);
		item5 = new AttributeItem("string5", 5, 15l, 2.5, true, someItem, AttributeItem.SomeEnumeration.enumValue3);
		date = new Date(1087365298214l);
		setDate(item1, offset(date, -2));
		setDate(item2, offset(date, -1));
		setDate(item3, date);
		setDate(item4, offset(date, 1));
		setDate(item5, offset(date, 2));
	}
	
	public void tearDown() throws Exception
	{
		item1.delete();
		item2.delete();
		item3.delete();
		item4.delete();
		item5.delete();
		someItem.delete();
		super.tearDown();
	}

	public void testLiteralConditions()
	{
		// less
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someNotNullString, "string3")));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someNotNullInteger, 3)));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someNotNullLong, 13l)));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someNotNullDouble, 2.3)));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someDate, date)));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item1, item2,
			item1.TYPE.search(Search.less(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// less or equal
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someNotNullString, "string3")));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someNotNullInteger, 3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someNotNullLong, 13l)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someNotNullDouble, 2.3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someDate, date)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Search.lessOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// greater
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someNotNullString, "string3")));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someNotNullInteger, 3)));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someNotNullLong, 13l)));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someNotNullDouble, 2.3)));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someDate, date)));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item4, item5,
			item1.TYPE.search(Search.greater(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// greater or equal
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someNotNullString, "string3")));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someNotNullInteger, 3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someNotNullLong, 13l)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someNotNullDouble, 2.3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someDate, date)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Search.greaterOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));
	}

}

package com.exedio.cope.lib;

import java.util.Date;


public class AttributeDateTest extends AttributeTest
{

	public void testSomeDate()
	{
		final Date date = new Date(1087365298214l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someDate.getType());
		assertEquals(null, item.getSomeDate());
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.equal(item.someDate, (Date)null))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNull(item.someDate))));
		assertEquals(set(), toSet(item.TYPE.search(Search.notEqual(item.someDate, (Date)null))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNotNull(item.someDate))));

		item.setSomeDate(date);
		assertEquals(date, item.getSomeDate());

		item.passivate();
		assertEquals(date, item.getSomeDate());
		assertEquals(
			list(item),
			item.TYPE.search(Search.equal(item.someDate, date)));
		assertEquals(
			list(item2),
			item.TYPE.search(Search.notEqual(item.someDate, date)));
		assertEquals(list(item2), item.TYPE.search(Search.equal(item.someDate, (Date)null)));
		assertEquals(list(item2), item.TYPE.search(Search.isNull(item.someDate)));
		assertEquals(list(item), item.TYPE.search(Search.notEqual(item.someDate, (Date)null)));
		assertEquals(list(item), item.TYPE.search(Search.isNotNull(item.someDate)));

		item.setSomeDate(nextDate);
		item.passivate();
		assertEquals(nextDate, item.getSomeDate());

		item.setSomeDate(null);
		assertEquals(null, item.getSomeDate());
		
		item.passivate();
		assertEquals(null, item.getSomeDate());
	}
	
	public void testSomeLongDate()
	{
		final Date date = new Date(1087368298214l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someLongDate.getType());
		assertEquals(null, item.getSomeLongDate());
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.equal(item.someLongDate, (Date)null))));
		assertEquals(set(item, item2), toSet(item.TYPE.search(Search.isNull(item.someLongDate))));
		assertEquals(set(), toSet(item.TYPE.search(Search.notEqual(item.someLongDate, (Date)null))));
		assertEquals(set(), toSet(item.TYPE.search(Search.isNotNull(item.someLongDate))));

		item.setSomeLongDate(date);
		assertEquals(date, item.getSomeLongDate());

		item.passivate();
		assertEquals(date, item.getSomeLongDate());
		assertEquals(
			list(item),
			item.TYPE.search(Search.equal(item.someLongDate, date)));
		assertEquals(
			list(item2),
			item.TYPE.search(Search.notEqual(item.someLongDate, date)));
		assertEquals(list(item2), item.TYPE.search(Search.equal(item.someLongDate, (Date)null)));
		assertEquals(list(item2), item.TYPE.search(Search.isNull(item.someLongDate)));
		assertEquals(list(item), item.TYPE.search(Search.notEqual(item.someLongDate, (Date)null)));
		assertEquals(list(item), item.TYPE.search(Search.isNotNull(item.someLongDate)));

		item.setSomeLongDate(nextDate);
		item.passivate();
		assertEquals(nextDate, item.getSomeLongDate());

		item.setSomeLongDate(null);
		assertEquals(null, item.getSomeLongDate());
		
		item.passivate();
		assertEquals(null, item.getSomeLongDate());
	}
	
	public static String toString(final Date date)
	{
		return date==null ? "NULL" : String.valueOf(date.getTime());
	}
	
	public static void assertEquals(final Date expectedDate, final Date actualDate)
	{
		assertEquals("ts: "+toString(expectedDate)+" "+toString(actualDate), (Object)expectedDate, (Object)actualDate);
	}

}

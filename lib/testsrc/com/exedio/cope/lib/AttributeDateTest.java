package com.exedio.cope.lib;

import java.util.Date;


public class AttributeDateTest extends AttributeTest
{
	public void testSomeDate()
	{
		final Date date = new Date();
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someDate.getType());
		assertEquals(null, item.getSomeDate());
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.equal(item.someDate, (Date)null))));
		assertEquals(set(item, item2), toSet(Search.search(item.TYPE, Search.isNull(item.someDate))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.notEqual(item.someDate, (Date)null))));
		assertEquals(set(), toSet(Search.search(item.TYPE, Search.isNotNull(item.someDate))));

		item.setSomeDate(date);
		assertEquals(date, item.getSomeDate());

		item.passivate();
		assertEquals(date, item.getSomeDate());
		assertEquals(
			list(item),
			Search.search(item.TYPE, Search.equal(item.someDate, date)));
		assertEquals(
			list(item2),
			Search.search(item.TYPE, Search.notEqual(item.someDate, date)));
		assertEquals(list(item2), Search.search(item.TYPE, Search.equal(item.someDate, (Date)null)));
		assertEquals(list(item2), Search.search(item.TYPE, Search.isNull(item.someDate)));
		assertEquals(list(item), Search.search(item.TYPE, Search.notEqual(item.someDate, (Date)null)));
		assertEquals(list(item), Search.search(item.TYPE, Search.isNotNull(item.someDate)));

		item.setSomeDate(nextDate);
		item.passivate();
		assertEquals(nextDate, item.getSomeDate());

		item.setSomeDate(null);
		assertEquals(null, item.getSomeDate());
		
		item.passivate();
		assertEquals(null, item.getSomeDate());
	}

}

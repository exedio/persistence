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

import java.util.Date;


public class AttributeDateTest extends AttributeTest
{

	public void testSomeDate() throws ConstraintViolationException
	{
		final Date date = new Date(1087365298214l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someDate.getType());
		assertEquals(null, item.getSomeDate());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someDate, (Date)null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someDate)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someDate, (Date)null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someDate)));

		item.setSomeDate(date);
		assertEquals(date, item.getSomeDate());

		assertContains(date, null, search(item.someDate));
		assertContains(date, search(item.someDate, Cope.equal(item.someDate, date)));

		item.passivateCopeItem();
		assertEquals(date, item.getSomeDate());
		assertEquals(
			list(item),
			item.TYPE.search(Cope.equal(item.someDate, date)));
		assertEquals(
			list(item2),
			item.TYPE.search(Cope.notEqual(item.someDate, date)));
		assertEquals(list(item2), item.TYPE.search(Cope.equal(item.someDate, (Date)null)));
		assertEquals(list(item2), item.TYPE.search(Cope.isNull(item.someDate)));
		assertEquals(list(item), item.TYPE.search(Cope.notEqual(item.someDate, (Date)null)));
		assertEquals(list(item), item.TYPE.search(Cope.isNotNull(item.someDate)));

		item.setSomeDate(nextDate);
		item.passivateCopeItem();
		assertEquals(nextDate, item.getSomeDate());

		item.setSomeDate(null);
		assertEquals(null, item.getSomeDate());
		
		item.passivateCopeItem();
		assertEquals(null, item.getSomeDate());
		
		final Date beforeTouch = new Date();
		item.touchSomeDate();
		final Date afterTouch = new Date();
		assertTrue(!beforeTouch.after(item.getSomeDate()));
		assertTrue(!afterTouch.before(item.getSomeDate()));

		try
		{
			item.set(item.someDate, new Integer(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected date, got " + Integer.class.getName() + " for someDate", e.getMessage());
		}
	}
	
	public void testSomeLongDate()
	{
		final Date date = new Date(1087368298214l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someLongDate.getType());
		assertEquals(null, item.getSomeLongDate());
		assertContains(item, item2, item.TYPE.search(Cope.equal(item.someLongDate, (Date)null)));
		assertContains(item, item2, item.TYPE.search(Cope.isNull(item.someLongDate)));
		assertContains(item.TYPE.search(Cope.notEqual(item.someLongDate, (Date)null)));
		assertContains(item.TYPE.search(Cope.isNotNull(item.someLongDate)));

		item.setSomeLongDate(date);
		assertEquals(date, item.getSomeLongDate());

		item.passivateCopeItem();
		assertEquals(date, item.getSomeLongDate());
		assertEquals(
			list(item),
			item.TYPE.search(Cope.equal(item.someLongDate, date)));
		assertEquals(
			list(item2),
			item.TYPE.search(Cope.notEqual(item.someLongDate, date)));
		assertEquals(list(item2), item.TYPE.search(Cope.equal(item.someLongDate, (Date)null)));
		assertEquals(list(item2), item.TYPE.search(Cope.isNull(item.someLongDate)));
		assertEquals(list(item), item.TYPE.search(Cope.notEqual(item.someLongDate, (Date)null)));
		assertEquals(list(item), item.TYPE.search(Cope.isNotNull(item.someLongDate)));

		assertContains(date, null, search(item.someLongDate));
		assertContains(date, search(item.someLongDate, Cope.equal(item.someLongDate, date)));

		item.setSomeLongDate(nextDate);
		item.passivateCopeItem();
		assertEquals(nextDate, item.getSomeLongDate());

		item.setSomeLongDate(null);
		assertEquals(null, item.getSomeLongDate());
		
		item.passivateCopeItem();
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

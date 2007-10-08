/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.util.Day;


public class DayFieldTest extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(DayItem.TYPE);

	DayItem item, item2;
	static final Day DEFAULT = new Day(2005, 8, 14);
	static final Day DEFAULT2 = new Day(2005, 8, 15);
	
	public DayFieldTest()
	{
		super(MODEL);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DayItem(DEFAULT));
		item2 = deleteOnTearDown(new DayItem(DEFAULT2));
	}

	public void testIt()
	{
		final Day day = new Day(2005, 9, 23);
		final Day beforeDay = new Day(2005, 9, 22);
		final Day nextDay = new Day(2005, 9, 24);

		assertEquals(item.TYPE, item.day.getType());
		assertEquals(Day.class, item.day.getValueClass());

		assertEquals(DEFAULT, item.getDay());
		assertContains(item.TYPE.search(item.day.equal((Day)null)));
		assertContains(item.TYPE.search(item.day.isNull()));
		assertContains(item, item2, item.TYPE.search(item.day.notEqual((Day)null)));
		assertContains(item, item2, item.TYPE.search(item.day.isNotNull()));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, item.TYPE.search(item.optionalDay.equal((Day)null)));
		assertContains(item, item2, item.TYPE.search(item.optionalDay.isNull()));
		assertContains(item.TYPE.search(item.optionalDay.notEqual((Day)null)));
		assertContains(item.TYPE.search(item.optionalDay.isNotNull()));

		item.setDay(day);
		assertEquals(day, item.getDay());

		assertContains(day, DEFAULT2, search(item.day));
		assertContains(day, search(item.day, item.day.equal(day)));
		assertContains(null, null, search(item.optionalDay));

		restartTransaction();
		assertEquals(day, item.getDay());
		assertEquals(list(item), item.TYPE.search(item.day.equal(day)));
		assertEquals(list(item), item.TYPE.search(item.day.greaterOrEqual(day).and(item.day.lessOrEqual(day))));
		assertEquals(list(item2), item.TYPE.search(item.day.notEqual(day)));
		assertEquals(list(), item.TYPE.search(item.day.equal((Day)null)));
		assertEquals(list(), item.TYPE.search(item.day.isNull()));
		assertContains(item, item2, item.TYPE.search(item.day.notEqual((Day)null)));
		assertContains(item, item2, item.TYPE.search(item.day.isNotNull()));
		assertEquals(list(), item.TYPE.search(item.day.equal(beforeDay)));
		assertEquals(list(), item.TYPE.search(item.day.equal(nextDay)));
		assertEquals(list(), item.TYPE.search(item.day.greaterOrEqual(beforeDay).and(item.day.lessOrEqual(beforeDay))));
		assertEquals(list(), item.TYPE.search(item.day.greaterOrEqual(nextDay).and(item.day.lessOrEqual(nextDay))));
		assertEquals(list(item), item.TYPE.search(item.day.greaterOrEqual(day).and(item.day.lessOrEqual(nextDay))));
		assertEquals(list(item), item.TYPE.search(item.day.greaterOrEqual(beforeDay).and(item.day.lessOrEqual(day))));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, item.TYPE.search(item.optionalDay.equal((Day)null)));
		assertContains(item, item2, item.TYPE.search(item.optionalDay.isNull()));
		assertEquals(list(), item.TYPE.search(item.optionalDay.notEqual((Day)null)));
		assertEquals(list(), item.TYPE.search(item.optionalDay.isNotNull()));

		item.setDay(nextDay);
		restartTransaction();
		assertEquals(nextDay, item.getDay());

		final Day firstDay = new Day(1000, 1, 1);
		item.setDay(firstDay);
		restartTransaction();
		assertEquals(firstDay, item.getDay());

		final Day lastDay = new Day(9999, 12, 31);
		item.setDay(lastDay);
		restartTransaction();
		assertEquals(lastDay, item.getDay());

		final Day optionalDay = new Day(5555, 12, 31);
		item.setOptionalDay(optionalDay);
		assertEquals(optionalDay, item.getOptionalDay());
		restartTransaction();
		assertEquals(optionalDay, item.getOptionalDay());

		item.setOptionalDay(null);
		assertEquals(null, item.getOptionalDay());
		restartTransaction();
		assertEquals(null, item.getOptionalDay());
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.day, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Day.class.getName() + ", but was a " + Integer.class.getName() + " for " + item.day + '.', e.getMessage());
		}
	}
	
	protected static List<? extends Day> search(final DayField selectAttribute)
	{
		return search(selectAttribute, null);
	}
	
	protected static List<? extends Day> search(final DayField selectAttribute, final Condition condition)
	{
		return new Query<Day>(selectAttribute, condition).search();
	}
	
}

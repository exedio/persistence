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

import static com.exedio.cope.DayItem.TYPE;
import static com.exedio.cope.DayItem.day;
import static com.exedio.cope.DayItem.optionalDay;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Day;
import java.util.List;

public class DayFieldTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(DayItem.TYPE);

	static
	{
		MODEL.enableSerialization(DayFieldTest.class, "MODEL");
	}

	DayItem item, item2;
	private AbsoluteMockClockStrategy clock;
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
		clock = new AbsoluteMockClockStrategy();
		Clock.override(clock);
	}

	@Override
	protected void tearDown() throws Exception
	{
		Clock.clearOverride();
		super.tearDown();
	}

	public void testIt()
	{
		final Day thisDay = new Day(2005, 9, 23);
		final Day beforeDay = new Day(2005, 9, 22);
		final Day nextDay = new Day(2005, 9, 24);

		assertEquals(TYPE, day.getType());
		assertEquals(Day.class, day.getValueClass());
		assertSerializedSame(day, 364);

		// test persistence
		assertEquals(DEFAULT, item.getDay());
		assertContains(TYPE.search(day.equal((Day)null)));
		assertContains(TYPE.search(day.isNull()));
		assertContains(item, item2, TYPE.search(day.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(day.isNotNull()));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, TYPE.search(optionalDay.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optionalDay.isNull()));
		assertContains(TYPE.search(optionalDay.notEqual((Day)null)));
		assertContains(TYPE.search(optionalDay.isNotNull()));

		item.setDay(thisDay);
		assertEquals(thisDay, item.getDay());

		assertContains(thisDay, DEFAULT2, search(day));
		assertContains(thisDay, search(day, day.equal(thisDay)));
		assertContains(null, null, search(optionalDay));

		restartTransaction();
		assertEquals(thisDay, item.getDay());
		assertEquals(list(item), TYPE.search(day.equal(thisDay)));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(thisDay).and(day.lessOrEqual(thisDay))));
		assertEquals(list(item2), TYPE.search(day.notEqual(thisDay)));
		assertEquals(list(), TYPE.search(day.equal((Day)null)));
		assertEquals(list(), TYPE.search(day.isNull()));
		assertContains(item, item2, TYPE.search(day.notEqual((Day)null)));
		assertContains(item, item2, TYPE.search(day.isNotNull()));
		assertEquals(list(), TYPE.search(day.equal(beforeDay)));
		assertEquals(list(), TYPE.search(day.equal(nextDay)));
		assertEquals(list(), TYPE.search(day.greaterOrEqual(beforeDay).and(day.lessOrEqual(beforeDay))));
		assertEquals(list(), TYPE.search(day.greaterOrEqual(nextDay).and(day.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(thisDay).and(day.lessOrEqual(nextDay))));
		assertEquals(list(item), TYPE.search(day.greaterOrEqual(beforeDay).and(day.lessOrEqual(thisDay))));
		assertEquals(null, item.getOptionalDay());
		assertContains(item, item2, TYPE.search(optionalDay.equal((Day)null)));
		assertContains(item, item2, TYPE.search(optionalDay.isNull()));
		assertEquals(list(), TYPE.search(optionalDay.notEqual((Day)null)));
		assertEquals(list(), TYPE.search(optionalDay.isNotNull()));

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

		clock.add(988888888888l);
		item.touchOptionalDay(getTimeZone("Europe/Berlin"));
		clock.assertEmpty();
		clock.add(988888888888l);
		assertEquals(new Day(getTimeZone("Europe/Berlin")), item.getOptionalDay());

		item.setOptionalDay(null);
		assertEquals(null, item.getOptionalDay());
		restartTransaction();
		assertEquals(null, item.getOptionalDay());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)day, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Day.class.getName() + ", but was a " + Integer.class.getName() + " for " + day + '.', e.getMessage());
		}
	}

	protected static List<? extends Day> search(final DayField selectField)
	{
		return search(selectField, null);
	}

	protected static List<? extends Day> search(final DayField selectField, final Condition condition)
	{
		return new Query<>(selectField, condition).search();
	}

	public void testSchema()
	{
		assertSchema();
	}
}

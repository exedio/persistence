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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Clock;
import com.exedio.cope.util.Day;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This test checks, whether {@link Clock#currentTimeMillis()} is called just once,
 * even for multiple defaultToNow fields.
 */
@MainRule.Tag
public class DefaultToNowClockTest extends TestWithEnvironment
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@BeforeEach
	final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testAll()
	{
		clock.add(millisNow);
		final MyItem item = new MyItem(
		);
		clock.assertEmpty();

		assertEquals(new Date(millisNow), item.getDate1());
		assertEquals(new Date(millisNow), item.getDate2());
		assertEquals(dayNow, item.getDay1());
		assertEquals(dayNow, item.getDay2());
	}
	@Test void testNotNow()
	{
		clock.assertEmpty();
		final MyItem item = new MyItem(
				MyItem.date1.map(new Date(millisA)),
				MyItem.date2.map(new Date(millisB)),
				MyItem.day1.map(dayA),
				MyItem.day2.map(dayB)
		);
		clock.assertEmpty();

		assertEquals("notNow", item.getNotNow());
		assertEquals(new Date(millisA), item.getDate1());
		assertEquals(new Date(millisB), item.getDate2());
		assertEquals(dayA, item.getDay1());
		assertEquals(dayB, item.getDay2());
	}
	@Test void testFirst()
	{
		clock.add(millisNow);
		final MyItem item = new MyItem(
				MyItem.date2.map(new Date(millisA)),
				MyItem.day2.map(dayB)
		);
		clock.assertEmpty();

		assertEquals(new Date(millisNow), item.getDate1());
		assertEquals(new Date(millisA), item.getDate2());
		assertEquals(dayNow, item.getDay1());
		assertEquals(dayB, item.getDay2());
	}
	@Test void testDateOnly()
	{
		clock.add(millisNow);
		final MyItem item = new MyItem(
				MyItem.day1.map(dayA),
				MyItem.day2.map(dayB)
		);
		clock.assertEmpty();

		assertEquals(new Date(millisNow), item.getDate1());
		assertEquals(new Date(millisNow), item.getDate2());
		assertEquals(dayA, item.getDay1());
		assertEquals(dayB, item.getDay2());
	}
	@Test void testDayOnly()
	{
		clock.add(millisNow);
		final MyItem item = new MyItem(
				MyItem.date1.map(new Date(millisA)),
				MyItem.date2.map(new Date(millisB))
		);
		clock.assertEmpty();

		assertEquals(new Date(millisA), item.getDate1());
		assertEquals(new Date(millisB), item.getDate2());
		assertEquals(dayNow, item.getDay1());
		assertEquals(dayNow, item.getDay2());
	}

	private static final long millisNow = 1283412452753l;
	private static final long millisA   = 1253412452783l;
	private static final long millisB   = 1233412452793l;
	private static final Day dayNow = new Day(2010,  9,  2);
	private static final Day dayA   = new Day(2011, 10, 12);
	private static final Day dayB   = new Day(2012, 11, 22);

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final StringField notNow = new StringField().defaultTo("notNow");
		static final DateField date1 = new DateField().defaultToNow();
		static final DateField date2 = new DateField().defaultToNow();
		static final DayField day1 = new DayField().defaultToNow(getTimeZone("Europe/Berlin"));
		static final DayField day2 = new DayField().defaultToNow(getTimeZone("Europe/Berlin"));


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getNotNow()
		{
			return MyItem.notNow.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setNotNow(@javax.annotation.Nonnull final java.lang.String notNow)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.notNow.set(this,notNow);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.util.Date getDate1()
		{
			return MyItem.date1.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDate1(@javax.annotation.Nonnull final java.util.Date date1)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.date1.set(this,date1);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDate1()
		{
			MyItem.date1.touch(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.util.Date getDate2()
		{
			return MyItem.date2.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDate2(@javax.annotation.Nonnull final java.util.Date date2)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.date2.set(this,date2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDate2()
		{
			MyItem.date2.touch(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		com.exedio.cope.util.Day getDay1()
		{
			return MyItem.day1.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDay1(@javax.annotation.Nonnull final com.exedio.cope.util.Day day1)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.day1.set(this,day1);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDay1(@javax.annotation.Nonnull final java.util.TimeZone zone)
		{
			MyItem.day1.touch(this,zone);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		com.exedio.cope.util.Day getDay2()
		{
			return MyItem.day2.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDay2(@javax.annotation.Nonnull final com.exedio.cope.util.Day day2)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.day2.set(this,day2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDay2(@javax.annotation.Nonnull final java.util.TimeZone zone)
		{
			MyItem.day2.touch(this,zone);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	public DefaultToNowClockTest()
	{
		super(MODEL);
	}
}

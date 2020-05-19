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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someDate;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class FieldDateTest extends FieldTest
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@BeforeEach final void setUp()
	{
		clockRule.override(clock);
	}

	@Test void testSomeDate()
	{
		final Date date = new Date(1087365298214l);
		final Date beforeDate = new Date(date.getTime()-1l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(TYPE, someDate.getType());
		assertEquals(Date.class, someDate.getValueClass());
		assertSerializedSame(someDate, 377);

		assertEqualsVerbose(null, item.getSomeDate());
		assertContains(item, item2, TYPE.search(someDate.equal((Date)null)));
		assertContains(item, item2, TYPE.search(someDate.isNull()));
		assertContains(TYPE.search(someDate.notEqual((Date)null)));
		assertContains(TYPE.search(someDate.isNotNull()));

		item.setSomeDate(date);
		final Date date2 = item.getSomeDate();
		assertEqualsVerbose(date, date2);

		// important, since Date is not immutable
		assertNotSame(date, date2);
		assertNotSame(date2, item.getSomeDate());

		assertContains(date, null, search(someDate));
		assertContains(date, search(someDate, someDate.equal(date)));

		restartTransaction();
		assertEqualsVerbose(date, item.getSomeDate());
		assertEquals(list(item), TYPE.search(someDate.equal(date)));
		assertEquals(list(item), TYPE.search(someDate.greaterOrEqual(date).and(someDate.lessOrEqual(date))));
		assertEquals(list(), TYPE.search(someDate.notEqual(date)));
		assertEquals(list(item2), TYPE.search(someDate.equal((Date)null)));
		assertEquals(list(item2), TYPE.search(someDate.isNull()));
		assertEquals(list(item), TYPE.search(someDate.notEqual((Date)null)));
		assertEquals(list(item), TYPE.search(someDate.isNotNull()));
		assertEquals(list(), TYPE.search(someDate.equal(beforeDate)));
		assertEquals(list(), TYPE.search(someDate.equal(nextDate)));
		assertEquals(list(), TYPE.search(someDate.greaterOrEqual(beforeDate).and(someDate.lessOrEqual(beforeDate))));
		assertEquals(list(), TYPE.search(someDate.greaterOrEqual(nextDate).and(someDate.lessOrEqual(nextDate))));
		assertEquals(list(item), TYPE.search(someDate.greaterOrEqual(date).and(someDate.lessOrEqual(nextDate))));
		assertEquals(list(item), TYPE.search(someDate.greaterOrEqual(beforeDate).and(someDate.lessOrEqual(date))));

		item.setSomeDate(nextDate);
		restartTransaction();
		assertEqualsVerbose(nextDate, item.getSomeDate());

		final Date dateWithLittleMilliseconds = new Date(1087365298004l);
		item.setSomeDate(dateWithLittleMilliseconds);
		restartTransaction();
		assertEqualsVerbose(dateWithLittleMilliseconds, item.getSomeDate());

		item.setSomeDate(null);
		assertEqualsVerbose(null, item.getSomeDate());

		restartTransaction();
		assertEqualsVerbose(null, item.getSomeDate());

		final Date touch = clock.add(1111);
		item.touchSomeDate();
		clock.assertEmpty();
		assertEqualsVerbose(touch, item.getSomeDate());

		// special test of Model#getItem for items without any attributes
		assertIDFails("EmptyItem-51", "item <51> does not exist", false);
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someDate, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + Date.class.getName() + ", but was a " + Integer.class.getName() + " for " + someDate + '.', e.getMessage());
		}
	}

	@Test void testOrder() throws MandatoryViolationException
	{
		final Date[] dates = new Date[9];
		final AttributeItem item3 = new AttributeItem("item3", 0, 0L, 0.0, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		final AttributeItem item4 = new AttributeItem("item4", 0, 0L, 0.0, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		dates[0] = new Date();
		for ( int i=1; i<dates.length; i++ )
		{
			do
			{
				dates[i] = new Date();
			}
			while ( ! dates[i].after(dates[i-1]) );
		}
		item.setSomeDate( dates[1] );
		item2.setSomeDate( dates[3] );
		item3.setSomeDate( dates[5] );
		item4.setSomeDate( dates[7] );
		final StringBuilder message = new StringBuilder();
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		for ( int i=0; i<dates.length; i++ )
		{
			if ( i!=0 ) message.append( "; " );
			message.append( "date"+i+": "+format.format(dates[i]) );
		}
		assertEquals(
			list( item, item2, item3, item4 ),
			TYPE.search( null, someDate, true ),
			message.toString()
		);
		assertEquals(
			list( item4, item3, item2, item ),
			TYPE.search( null, someDate, false ),
			message.toString()
		);
		assertEquals(
			list( item3, item4 ),
			TYPE.search( someDate.greater(dates[4]), someDate, true ),
			message.toString()
		);
		assertEquals(
			list( item, item2, item3 ),
			TYPE.search( someDate.less(dates[6]), someDate, true ),
			message.toString()
		);
	}

	@Test void testOrderWithFixedDates() throws MandatoryViolationException, ParseException
	{
		final Date[] dates = new Date[9];
		final AttributeItem item3 = new AttributeItem("item3", 0, 0L, 0.0, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		final AttributeItem item4 = new AttributeItem("item4", 0, 0L, 0.0, true, emptyItem, AttributeItem.SomeEnum.enumValue1);
		final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		dates[0] = format.parse("2005-09-22 10:26:46.031");
		dates[1] = format.parse("2005-09-22 10:26:46.046"); // item
		dates[2] = format.parse("2005-09-22 10:26:46.062");
		dates[3] = format.parse("2005-09-22 10:26:46.078"); // item2
		dates[4] = format.parse("2005-09-22 10:26:46.093");
		dates[5] = format.parse("2005-09-22 10:26:46.109"); // item3
		dates[6] = format.parse("2005-09-22 10:26:46.125");
		dates[7] = format.parse("2005-09-22 10:26:46.140"); // item4
		dates[8] = format.parse("2005-09-22 10:26:46.156");

		item.setSomeDate( dates[1] );
		item2.setSomeDate( dates[3] );
		item3.setSomeDate( dates[5] );
		item4.setSomeDate( dates[7] );
		final StringBuilder message = new StringBuilder();
		for ( int i=0; i<dates.length; i++ )
		{
			if ( i!=0 ) message.append( "; " );
			message.append( "date"+i+": "+format.format(dates[i]) );
		}
		assertEquals(
			list( item, item2, item3, item4 ),
			TYPE.search( null, someDate, true ),
			message.toString()
		);
		assertEquals(
			list( item4, item3, item2, item ),
			TYPE.search( null, someDate, false ),
			message.toString()
		);
		assertEquals(
			list( item3, item4 ),
			TYPE.search( someDate.greater(dates[4]), someDate, true ),
			message.toString()
		);
		assertEquals(
			list( item, item2, item3 ),
			TYPE.search( someDate.less(dates[6]), someDate, true ),
			message.toString()
		);
	}

	@Test void testDateColumnType()
	{
		final EnvironmentInfo env = model.getEnvironmentInfo();
		assertEquals(
				propertiesHsqldbMysql55() || (mysql && !env.isDatabaseVersionAtLeast(5, 6)) ? null : dialect.dateTimestampType,
				model.connect().database.dialect.getDateTimestampType());
	}

	private static String toString(final Date date)
	{
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		return date==null ? "NULL" : df.format(date);
	}

	private static void assertEqualsVerbose(final Date expectedDate, final Date actualDate)
	{
		assertEquals(expectedDate, actualDate, "ts: "+toString(expectedDate)+" "+toString(actualDate));
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}

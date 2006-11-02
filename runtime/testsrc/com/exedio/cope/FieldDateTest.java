/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.exedio.cope.testmodel.AttributeItem;


public class FieldDateTest extends FieldTest
{

	public void testSomeDate()
	{
		final Date date = new Date(1087365298214l);
		final Date beforeDate = new Date(date.getTime()-1l);
		final Date nextDate = new Date(date.getTime()+1l);

		assertEquals(item.TYPE, item.someDate.getType());
		assertEquals(Date.class, item.someDate.getValueClass());

		assertEquals(null, item.getSomeDate());
		assertContains(item, item2, item.TYPE.search(item.someDate.equal((Date)null)));
		assertContains(item, item2, item.TYPE.search(item.someDate.isNull()));
		assertContains(item.TYPE.search(item.someDate.notEqual((Date)null)));
		assertContains(item.TYPE.search(item.someDate.isNotNull()));

		item.setSomeDate(date);
		assertEquals(date, item.getSomeDate());

		assertContains(date, null, search(item.someDate));
		assertContains(date, search(item.someDate, item.someDate.equal(date)));

		restartTransaction();
		assertEquals(date, item.getSomeDate());
		assertEquals(list(item), item.TYPE.search(item.someDate.equal(date)));
		assertEquals(list(item), item.TYPE.search(item.someDate.greaterOrEqual(date).and(item.someDate.lessOrEqual(date))));
		assertEquals(list(), item.TYPE.search(item.someDate.notEqual(date)));
		assertEquals(list(item2), item.TYPE.search(item.someDate.equal((Date)null)));
		assertEquals(list(item2), item.TYPE.search(item.someDate.isNull()));
		assertEquals(list(item), item.TYPE.search(item.someDate.notEqual((Date)null)));
		assertEquals(list(item), item.TYPE.search(item.someDate.isNotNull()));
		assertEquals(list(), item.TYPE.search(item.someDate.equal(beforeDate)));
		assertEquals(list(), item.TYPE.search(item.someDate.equal(nextDate)));
		assertEquals(list(), item.TYPE.search(item.someDate.greaterOrEqual(beforeDate).and(item.someDate.lessOrEqual(beforeDate))));
		assertEquals(list(), item.TYPE.search(item.someDate.greaterOrEqual(nextDate).and(item.someDate.lessOrEqual(nextDate))));
		assertEquals(list(item), item.TYPE.search(item.someDate.greaterOrEqual(date).and(item.someDate.lessOrEqual(nextDate))));
		assertEquals(list(item), item.TYPE.search(item.someDate.greaterOrEqual(beforeDate).and(item.someDate.lessOrEqual(date))));

		item.setSomeDate(nextDate);
		restartTransaction();
		assertEquals(nextDate, item.getSomeDate());

		final Date dateWithLittleMilliseconds = new Date(1087365298004l);
		item.setSomeDate(dateWithLittleMilliseconds);
		restartTransaction();
		assertEquals(dateWithLittleMilliseconds, item.getSomeDate());

		item.setSomeDate(null);
		assertEquals(null, item.getSomeDate());
		
		restartTransaction();
		assertEquals(null, item.getSomeDate());
		
		final Date beforeTouch = new Date();
		item.touchSomeDate();
		final Date afterTouch = new Date();
		assertWithin(beforeTouch, afterTouch, item.getSomeDate());
		
		// special test of Model#findByID for items without any attributes
		assertIDFails("EmptyItem.51", "item <51> does not exist", false);
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			item.set((FunctionField)item.someDate, Integer.valueOf(10));
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Date.class.getName() + ", but was a " + Integer.class.getName(), e.getMessage());
		}
	}
	
	public void testOrder() throws MandatoryViolationException
	{
		Date[] dates = new Date[9];
		AttributeItem item3, item4;
		deleteOnTearDown( item3 = new AttributeItem("item3", 0, 0L, 0.0, true, someItem, AttributeItem.SomeEnum.enumValue1) );
		deleteOnTearDown( item4 = new AttributeItem("item4", 0, 0L, 0.0, true, someItem, AttributeItem.SomeEnum.enumValue1) );
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
		StringBuffer message = new StringBuffer();
		DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
		for ( int i=0; i<dates.length; i++ )
		{
			if ( i!=0 ) message.append( "; " );
			message.append( "date"+i+": "+format.format(dates[i]) );
		}
		assertEquals(
			message.toString(),
			list( item, item2, item3, item4 ),
			AttributeItem.TYPE.search( null, AttributeItem.someDate, true )
		);
		assertEquals(
			message.toString(),
			list( item4, item3, item2, item ),
			AttributeItem.TYPE.search( null, AttributeItem.someDate, false )
		);
		assertEquals(
			message.toString(),
			list( item3, item4 ),
			AttributeItem.TYPE.search( AttributeItem.someDate.greater(dates[4]), AttributeItem.someDate, true )
		);
		assertEquals(
			message.toString(),
			list( item, item2, item3 ),
			AttributeItem.TYPE.search( AttributeItem.someDate.less(dates[6]), AttributeItem.someDate, true )
		);
	}
	
	public void testOrderWithFixedDates() throws MandatoryViolationException, ParseException
	{
		Date[] dates = new Date[9];
		AttributeItem item3, item4;
		deleteOnTearDown( item3 = new AttributeItem("item3", 0, 0L, 0.0, true, someItem, AttributeItem.SomeEnum.enumValue1) );
		deleteOnTearDown( item4 = new AttributeItem("item4", 0, 0L, 0.0, true, someItem, AttributeItem.SomeEnum.enumValue1) );
		DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS" );
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
		StringBuffer message = new StringBuffer();
		for ( int i=0; i<dates.length; i++ )
		{
			if ( i!=0 ) message.append( "; " );
			message.append( "date"+i+": "+format.format(dates[i]) );
		}
		assertEquals(
			message.toString(),
			list( item, item2, item3, item4 ),
			AttributeItem.TYPE.search( null, AttributeItem.someDate, true )
		);
		assertEquals(
			message.toString(),
			list( item4, item3, item2, item ),
			AttributeItem.TYPE.search( null, AttributeItem.someDate, false )
		);
		assertEquals(
			message.toString(),
			list( item3, item4 ),
			AttributeItem.TYPE.search( AttributeItem.someDate.greater(dates[4]), AttributeItem.someDate, true )
		);
		assertEquals(
			message.toString(),
			list( item, item2, item3 ),
			AttributeItem.TYPE.search( AttributeItem.someDate.less(dates[6]), AttributeItem.someDate, true )
		);
	}
	
	public void testDateColumnType()
	{
		final Database database = model.getDatabase();
		final String databaseClassName = database.getClass().getName();
		final String expectedColumnType;

		if(databaseClassName.endsWith("OracleDatabase"))
			expectedColumnType = "TIMESTAMP(3)";
		else if(databaseClassName.endsWith("MysqlDatabase"))
			expectedColumnType = null;
		else if(databaseClassName.endsWith("HsqldbDatabase"))
			expectedColumnType = "timestamp";
		else if(databaseClassName.endsWith("PostgresqlDatabase"))
			expectedColumnType = "timestamp (3) without time zone";
		else
			throw new RuntimeException(databaseClassName);
		
		assertEquals(expectedColumnType, database.getDateTimestampType());
	}
	
	public static String toString(final Date date)
	{
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return date==null ? "NULL" : df.format(date);
	}
	
	public static void assertEquals(final Date expectedDate, final Date actualDate)
	{
		assertEquals("ts: "+toString(expectedDate)+" "+toString(actualDate), expectedDate, actualDate);
	}

}

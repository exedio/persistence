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

package com.exedio.cope.lib;

import java.util.Date;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;

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
		deleteOnTearDown(someItem = new EmptyItem());
		deleteOnTearDown(item1 = new AttributeItem("string1", 1, 11l, 2.1, true, someItem, AttributeItem.SomeEnumeration.enumValue1));
		deleteOnTearDown(item2 = new AttributeItem("string2", 2, 12l, 2.2, true, someItem, AttributeItem.SomeEnumeration.enumValue1));
		deleteOnTearDown(item3 = new AttributeItem("string3", 3, 13l, 2.3, true, someItem, AttributeItem.SomeEnumeration.enumValue2));
		deleteOnTearDown(item4 = new AttributeItem("string4", 4, 14l, 2.4, true, someItem, AttributeItem.SomeEnumeration.enumValue3));
		deleteOnTearDown(item5 = new AttributeItem("string5", 5, 15l, 2.5, true, someItem, AttributeItem.SomeEnumeration.enumValue3));
		date = new Date(1087365298214l);
		setDate(item1, offset(date, -2));
		setDate(item2, offset(date, -1));
		setDate(item3, date);
		setDate(item4, offset(date, 1));
		setDate(item5, offset(date, 2));
	}
	
	public void testLiteralConditions()
	{
		// less
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someNotNullString, "string3")));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someNotNullInteger, 3)));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someNotNullLong, 13l)));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someNotNullDouble, 2.3)));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someDate, date)));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item1, item2,
			item1.TYPE.search(Cope.less(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// less or equal
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someNotNullString, "string3")));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someNotNullInteger, 3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someNotNullLong, 13l)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someNotNullDouble, 2.3)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someDate, date)));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item1, item2, item3,
			item1.TYPE.search(Cope.lessOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// greater
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someNotNullString, "string3")));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someNotNullInteger, 3)));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someNotNullLong, 13l)));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someNotNullDouble, 2.3)));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someDate, date)));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item4, item5,
			item1.TYPE.search(Cope.greater(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));

		// greater or equal
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someNotNullString, "string3")));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someNotNullInteger, 3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someNotNullLong, 13l)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someNotNullDouble, 2.3)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someDate, date)));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someLongDate, offset(date, LONG_OFFSET))));
		assertContains(item3, item4, item5,
			item1.TYPE.search(Cope.greaterOrEqual(item1.someNotNullEnumeration, AttributeItem.SomeEnumeration.enumValue2)));
	}

}

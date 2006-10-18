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

import java.util.Date;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;

public class CompareFunctionConditionTest extends AbstractLibTest
{
	public CompareFunctionConditionTest()
	{
		super(Main.compareFunctionConditionModel);
	}
	
	CompareFunctionConditionItem item1, item2, item3, item4, item5;
	final Date date = CompareFunctionConditionItem.date;
	
	private Date offset(final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item1 = new CompareFunctionConditionItem("string1", 1, 11l, 2.1, offset(-2), XEnum.V1));
		deleteOnTearDown(item2 = new CompareFunctionConditionItem("string2", 2, 12l, 2.2, offset(-1), XEnum.V2));
		deleteOnTearDown(item3 = new CompareFunctionConditionItem("string3", 3, 13l, 2.3, offset( 0), XEnum.V3));
		deleteOnTearDown(item4 = new CompareFunctionConditionItem("string4", 4, 14l, 2.4, offset(+1), XEnum.V4));
		deleteOnTearDown(item5 = new CompareFunctionConditionItem("string5", 5, 15l, 2.5, offset(+2), XEnum.V5));
	}
	
	public void testCompareConditions()
	{
		// test equals/hashCode
		assertEquals(item1.leftString.less(item1.rightString), item1.leftString.less(item1.rightString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.leftString.less(item1.leftString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.rightString.less(item1.rightString));
		assertNotEquals(item1.leftString.less(item1.rightString), item1.leftString.lessOrEqual(item1.rightString));
		
		
		// less
		assertContains(item1, item2, item1.TYPE.search(item1.leftString.less(item1.rightString)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftInt.less(item1.rightInt)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftLong.less(item1.rightLong)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftDouble.less(item1.rightDouble)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftDate.less(item1.rightDate)));
		assertContains(item1, item2, item1.TYPE.search(item1.leftEnum.less(item1.rightEnum)));

		// less or equal
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftString.lessOrEqual(item1.rightString)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftInt.lessOrEqual(item1.rightInt)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftLong.lessOrEqual(item1.rightLong)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftDouble.lessOrEqual(item1.rightDouble)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftDate.lessOrEqual(item1.rightDate)));
		assertContains(item1, item2, item3, item1.TYPE.search(item1.leftEnum.lessOrEqual(item1.rightEnum)));

		// greater
		assertContains(item4, item5, item1.TYPE.search(item1.leftString.greater(item1.rightString)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftInt.greater(item1.rightInt)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftLong.greater(item1.rightLong)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftDouble.greater(item1.rightDouble)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftDate.greater(item1.rightDate)));
		assertContains(item4, item5, item1.TYPE.search(item1.leftEnum.greater(item1.rightEnum)));

		// greater or equal
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftString.greaterOrEqual(item1.rightString)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftInt.greaterOrEqual(item1.rightInt)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftLong.greaterOrEqual(item1.rightLong)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftDouble.greaterOrEqual(item1.rightDouble)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftDate.greaterOrEqual(item1.rightDate)));
		assertContains(item3, item4, item5, item1.TYPE.search(item1.leftEnum.greaterOrEqual(item1.rightEnum)));
	}
}

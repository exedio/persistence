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

import static com.exedio.cope.CompareFunctionConditionItem.TYPE;
import static com.exedio.cope.CompareFunctionConditionItem.dateA;
import static com.exedio.cope.CompareFunctionConditionItem.dateB;
import static com.exedio.cope.CompareFunctionConditionItem.dayA;
import static com.exedio.cope.CompareFunctionConditionItem.dayB;
import static com.exedio.cope.CompareFunctionConditionItem.doubleA;
import static com.exedio.cope.CompareFunctionConditionItem.doubleB;
import static com.exedio.cope.CompareFunctionConditionItem.enumA;
import static com.exedio.cope.CompareFunctionConditionItem.enumB;
import static com.exedio.cope.CompareFunctionConditionItem.intA;
import static com.exedio.cope.CompareFunctionConditionItem.intB;
import static com.exedio.cope.CompareFunctionConditionItem.itemA;
import static com.exedio.cope.CompareFunctionConditionItem.itemB;
import static com.exedio.cope.CompareFunctionConditionItem.longA;
import static com.exedio.cope.CompareFunctionConditionItem.longB;
import static com.exedio.cope.CompareFunctionConditionItem.stringA;
import static com.exedio.cope.CompareFunctionConditionItem.stringB;
import static com.exedio.cope.RuntimeAssert.assertCondition;
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CompareFunctionConditionItem.XEnum;
import com.exedio.cope.util.Day;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompareFunctionConditionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);
	private static final This<CompareFunctionConditionItem> THIS = TYPE.getThis();

	public CompareFunctionConditionTest()
	{
		super(MODEL);
	}

	CompareFunctionConditionItem item1, item2, item3, item4, item5;
	final Date date = CompareFunctionConditionItem.date;
	final Day day = CompareFunctionConditionItem.day;

	private Date date(final long offset)
	{
		return new Date(date.getTime()+offset);
	}

	private Day day(final int offset)
	{
		return day.plusDays(offset);
	}

	@BeforeEach final void setUp()
	{
		item1 = new CompareFunctionConditionItem("string1", 1, 11l, 2.1, date(-2), day(-2), XEnum.V1);
		item2 = new CompareFunctionConditionItem("string2", 2, 12l, 2.2, date(-1), day(-1), XEnum.V2);
		item3 = new CompareFunctionConditionItem("string3", 3, 13l, 2.3, date( 0), day( 0), XEnum.V3);
		item4 = new CompareFunctionConditionItem("string4", 4, 14l, 2.4, date(+1), day(+1), XEnum.V4);
		item5 = new CompareFunctionConditionItem("string5", 5, 15l, 2.5, date(+2), day(+2), XEnum.V5);
		        new CompareFunctionConditionItem(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		        new CompareFunctionConditionItem(null, "string3", null, 3, null, 13l, null, 2.3, null, date(0), null, day(0), null, XEnum.V3);
		        new CompareFunctionConditionItem("string3", null, 3, null, 13l, null, 2.3, null, date(0), null, day(0), null, XEnum.V3, null);
		item1.setItemB(item3);
		item2.setItemB(item3);
		item3.setItemB(item3);
		item4.setItemB(item3);
		item5.setItemB(item3);
	}

	@Test void testEqualsHashCode()
	{
		assertEqualsAndHash(stringA.less(stringB), stringA.less(stringB));
		assertNotEqualsAndHash(
				stringA.less(stringB),
				stringA.less(stringA),
				stringB.less(stringB),
				stringA.lessOrEqual(stringB),
				stringA.equal(stringB));
	}

	@Test void testFieldsCovered()
	{
		assertFieldsCovered(asList(stringA, stringB), stringA.equal(stringB));
		assertFieldsCovered(asList(stringA, stringB), stringA.less(stringB));
		assertFieldsCovered(asList(stringA, stringB), stringA.lessOrEqual(stringB));
		assertFieldsCovered(asList(stringA, stringB), stringA.greater(stringB));
		assertFieldsCovered(asList(stringA, stringB), stringA.greaterOrEqual(stringB));
	}

	@Test void testToString()
	{
		assertEquals("CompareFunctionConditionItem.stringA=CompareFunctionConditionItem.stringB",  stringA.equal(stringB).toString());
		assertEquals("CompareFunctionConditionItem.stringA<CompareFunctionConditionItem.stringB",  stringA.less(stringB).toString());
		assertEquals("CompareFunctionConditionItem.stringA<=CompareFunctionConditionItem.stringB", stringA.lessOrEqual(stringB).toString());
		assertEquals("CompareFunctionConditionItem.stringA>CompareFunctionConditionItem.stringB",  stringA.greater(stringB).toString());
		assertEquals("CompareFunctionConditionItem.stringA>=CompareFunctionConditionItem.stringB", stringA.greaterOrEqual(stringB).toString());
	}

	@Test void testEqual()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item3);
		assertCondition(expected, TYPE, stringA.equal(stringB));
		assertCondition(expected, TYPE, intA   .equal(intB));
		assertCondition(expected, TYPE, longA  .equal(longB));
		assertCondition(expected, TYPE, doubleA.equal(doubleB));
		assertCondition(expected, TYPE, dateA  .equal(dateB));
		assertCondition(expected, TYPE, dayA   .equal(dayB));
		assertCondition(expected, TYPE, enumA  .equal(enumB));
		assertCondition(expected, TYPE, itemA  .equal(itemB));
		assertCondition(expected, TYPE, THIS   .equal(itemB));
	}

	@Test void testNotEqual()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item1, item2, item4, item5);
		assertCondition(expected, TYPE, stringA.notEqual(stringB));
		assertCondition(expected, TYPE, intA   .notEqual(intB));
		assertCondition(expected, TYPE, longA  .notEqual(longB));
		assertCondition(expected, TYPE, doubleA.notEqual(doubleB));
		assertCondition(expected, TYPE, dateA  .notEqual(dateB));
		assertCondition(expected, TYPE, dayA   .notEqual(dayB));
		assertCondition(expected, TYPE, enumA  .notEqual(enumB));
		assertCondition(expected, TYPE, itemA  .notEqual(itemB));
		assertCondition(expected, TYPE, THIS   .notEqual(itemB));
	}

	@Test void testLess()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item1, item2);
		assertCondition(expected, TYPE, stringA.less(stringB));
		assertCondition(expected, TYPE, intA   .less(intB));
		assertCondition(expected, TYPE, longA  .less(longB));
		assertCondition(expected, TYPE, doubleA.less(doubleB));
		assertCondition(expected, TYPE, dateA  .less(dateB));
		assertCondition(expected, TYPE, dayA   .less(dayB));
		assertCondition(expected, TYPE, enumA  .less(enumB));
		assertCondition(expected, TYPE, itemA  .less(itemB));
		assertCondition(expected, TYPE, THIS   .less(itemB));
	}

	@Test void testLessOrEqual()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item1, item2, item3);
		assertCondition(expected, TYPE, stringA.lessOrEqual(stringB));
		assertCondition(expected, TYPE, intA   .lessOrEqual(intB));
		assertCondition(expected, TYPE, longA  .lessOrEqual(longB));
		assertCondition(expected, TYPE, doubleA.lessOrEqual(doubleB));
		assertCondition(expected, TYPE, dateA  .lessOrEqual(dateB));
		assertCondition(expected, TYPE, dayA   .lessOrEqual(dayB));
		assertCondition(expected, TYPE, enumA  .lessOrEqual(enumB));
		assertCondition(expected, TYPE, itemA  .lessOrEqual(itemB));
		assertCondition(expected, TYPE, THIS   .lessOrEqual(itemB));
	}

	@Test void testGreater()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item4, item5);
		assertCondition(expected, TYPE, stringA.greater(stringB));
		assertCondition(expected, TYPE, intA   .greater(intB));
		assertCondition(expected, TYPE, longA  .greater(longB));
		assertCondition(expected, TYPE, doubleA.greater(doubleB));
		assertCondition(expected, TYPE, dateA  .greater(dateB));
		assertCondition(expected, TYPE, dayA   .greater(dayB));
		assertCondition(expected, TYPE, enumA  .greater(enumB));
		assertCondition(expected, TYPE, itemA  .greater(itemB));
		assertCondition(expected, TYPE, THIS   .greater(itemB));
	}

	@Test void testGreaterOrEqual()
	{
		final List<CompareFunctionConditionItem> expected =
				asList(item3, item4, item5);
		assertCondition(expected, TYPE, stringA.greaterOrEqual(stringB));
		assertCondition(expected, TYPE, intA   .greaterOrEqual(intB));
		assertCondition(expected, TYPE, longA  .greaterOrEqual(longB));
		assertCondition(expected, TYPE, doubleA.greaterOrEqual(doubleB));
		assertCondition(expected, TYPE, dateA  .greaterOrEqual(dateB));
		assertCondition(expected, TYPE, dayA   .greaterOrEqual(dayB));
		assertCondition(expected, TYPE, enumA  .greaterOrEqual(enumB));
		assertCondition(expected, TYPE, itemA  .greaterOrEqual(itemB));
		assertCondition(expected, TYPE, THIS   .greaterOrEqual(itemB));
	}

	@Test void testNot()
	{
		assertEquals(intA+"<>"+intB, intA.   equal      (intB) .not().toString());
		assertEquals(intA+ "="+intB, intA.notEqual      (intB) .not().toString());
		assertEquals(intA+">="+intB, intA.less          (intB) .not().toString());
		assertEquals(intA+ ">"+intB, intA.lessOrEqual   (intB) .not().toString());
		assertEquals(intA+"<="+intB, intA.greater       (intB) .not().toString());
		assertEquals(intA+ "<"+intB, intA.greaterOrEqual(intB) .not().toString());
		assertCondition(item1, item2,        item4, item5, TYPE, intA.equal(intB).not());
		assertCondition(              item3,               TYPE, intA.notEqual(intB).not());
		assertCondition(              item3, item4, item5, TYPE, intA.less(intB).not());
		assertCondition(                     item4, item5, TYPE, intA.lessOrEqual(intB).not());
		assertCondition(item1, item2, item3,               TYPE, intA.greater(intB).not());
		assertCondition(item1, item2,                      TYPE, intA.greaterOrEqual(intB).not());
	}
}

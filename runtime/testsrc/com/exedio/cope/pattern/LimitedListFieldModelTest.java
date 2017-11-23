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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.LimitedListFieldItem.TYPE;
import static com.exedio.cope.pattern.LimitedListFieldItem.dates;
import static com.exedio.cope.pattern.LimitedListFieldItem.num1;
import static com.exedio.cope.pattern.LimitedListFieldItem.num2;
import static com.exedio.cope.pattern.LimitedListFieldItem.num3;
import static com.exedio.cope.pattern.LimitedListFieldItem.nums;
import static com.exedio.cope.pattern.LimitedListFieldItem.strings;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.misc.Computed;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LimitedListFieldModelTest
{
	public static final Model MODEL = new Model(
		TYPE,
		LimitedListFieldItemFieldItem.TYPE
	);

	static
	{
		MODEL.enableSerialization(LimitedListFieldModelTest.class, "MODEL");
	}

	protected static final Integer i0 = 0;
	protected static final Integer i1 = 1;
	protected static final Integer i2 = 2;
	protected static final Integer i3 = 3;
	protected static final Integer i4 = 4;

	@Test void testIt()
	{
		final IntegerField numsL = nums.getLength();
		final IntegerField datesL = dates.getLength();
		final IntegerField stringsL = strings.getLength();
		final CheckConstraint numsU = nums.getUnison();
		final CheckConstraint datesU = dates.getUnison();
		final CheckConstraint stringsU = strings.getUnison();

		assertEquals(asList(new Feature[]{
				TYPE.getThis(),
				num1,
				num2,
				num3,
				nums,
				numsL,
				numsU,
				dates,
				datesL,
				dates.getListSources().get(0),
				dates.getListSources().get(1),
				datesU,
				strings,
				stringsL,
				strings.getListSources().get(0),
				strings.getListSources().get(1),
				strings.getListSources().get(2),
				strings.getListSources().get(3),
				stringsU,
			}), TYPE.getFeatures());

		assertEquals(TYPE, num1.getType());
		assertEquals(TYPE, num2.getType());
		assertEquals(TYPE, num3.getType());
		assertEquals("num1", num1.getName());
		assertEquals("num2", num2.getName());
		assertEquals("num3", num3.getName());
		assertEquals(TYPE, nums.getType());
		assertEquals("nums", nums.getName());
		assertEqualsUnmodifiable(list(numsL, num1, num2, num3, numsU), nums.getSourceFeatures());
		assertEquals(nums, num1.getPattern());
		assertEquals(nums, num2.getPattern());
		assertEquals(nums, num3.getPattern());
		assertEqualsUnmodifiable(list(num1, num2, num3), nums.getListSources());
		assertEquals(false, nums.isInitial());
		assertEquals(false, nums.isFinal());
		assertEquals(true,  nums.isMandatory());
		assertContains(nums.getInitialExceptions());

		assertEquals(3, nums   .getMaximumSize());
		assertEquals(2, dates  .getMaximumSize());
		assertEquals(4, strings.getMaximumSize());

		assertEquals(TYPE, dates.getType());
		assertEquals("dates", dates.getName());
		final List<FunctionField<Date>> dateSources = dates.getListSources();
		assertEquals(2, dateSources.size());
		assertUnmodifiable(dateSources);
		final Iterator<?> dateSourcesIterator = dateSources.iterator();
		final DateField date0 = assertDate(dateSourcesIterator, 0);
		final DateField date1 = assertDate(dateSourcesIterator, 1);
		assertTrue(!dateSourcesIterator.hasNext());
		assertEquals(dates, date0.getPattern());
		assertEquals(dates, date1.getPattern());
		assertEqualsUnmodifiable(list(datesL, date0, date1, datesU), dates.getSourceFeatures());
		assertEqualsUnmodifiable(list(date0, date1), dates.getListSources());
		assertEquals(false, dates.isInitial());
		assertEquals(false, dates.isFinal());
		assertEquals(true,  dates.isMandatory());
		assertContains(dates.getInitialExceptions());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());
		final List<FunctionField<String>> stringSources = strings.getListSources();
		assertEquals(4, stringSources.size());
		assertUnmodifiable(stringSources);
		final Iterator<?> stringSourcesIterator = stringSources.iterator();
		final StringField string0 = assertString(stringSourcesIterator, 0);
		final StringField string1 = assertString(stringSourcesIterator, 1);
		final StringField string2 = assertString(stringSourcesIterator, 2);
		final StringField string3 = assertString(stringSourcesIterator, 3);
		assertTrue(!stringSourcesIterator.hasNext());
		assertEquals(strings, string0.getPattern());
		assertEquals(strings, string1.getPattern());
		assertEquals(strings, string2.getPattern());
		assertEquals(strings, string3.getPattern());
		assertEqualsUnmodifiable(list(stringsL, string0, string1, string2, string3, stringsU), strings.getSourceFeatures());
		assertEqualsUnmodifiable(list(string0, string1, string2, string3), strings.getListSources());
		assertEquals(false, strings.isInitial());
		assertEquals(false, strings.isFinal());
		assertEquals(true,  strings.isMandatory());
		assertContains(StringLengthViolationException.class, strings.getInitialExceptions());

		assertEquals(
				list(num1, num2, num3, numsL, datesL, date0, date1, stringsL, string0, string1, string2, string3),
				TYPE.getDeclaredFields());

		assertFalse(num1.isAnnotationPresent(Computed.class));
		assertFalse(num2.isAnnotationPresent(Computed.class));
		assertFalse(num3.isAnnotationPresent(Computed.class));
		assertTrue (date0    .isAnnotationPresent(Computed.class));
		assertTrue (date1    .isAnnotationPresent(Computed.class));
		assertTrue (string0  .isAnnotationPresent(Computed.class));
		assertTrue (string1  .isAnnotationPresent(Computed.class));
		assertTrue (string2  .isAnnotationPresent(Computed.class));
		assertTrue (string3  .isAnnotationPresent(Computed.class));

		assertEquals(
			"select this from LimitedListFieldItem where (" +
				"(nums-Len>'0' OR num1 is null) AND " +
				"(nums-Len>'1' OR num2 is null) AND " +
				"(nums-Len>'2' OR num3 is null)" +
			")",
			TYPE.newQuery(nums.getUnison().getCondition()).toString());

		assertSerializedSame(nums   , 399);
		assertSerializedSame(dates  , 400);
		assertSerializedSame(strings, 402);

		// test persistence
		final Date ts1 = new Date(8172541283976l);
		final Date ts2 = new Date(3874656234632l);

		// TODO return Condition.FALSE instead
		try
		{
			nums.equal(asList(i1, i2, i3, i4));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("3", e.getMessage());
		}
		try
		{
			dates.equal(asList(ts1, ts2, ts1));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("2", e.getMessage());
		}
		try
		{
			strings.equal(asList("one", "two", "three", "four", "five"));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("4", e.getMessage());
		}
		// TODO return Condition.TRUE instead
		try
		{
			nums.notEqual(asList(i1, i2, i3, i4));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("3", e.getMessage());
		}
		try
		{
			dates.notEqual(asList(ts1, ts2, ts1));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("2", e.getMessage());
		}
		try
		{
			strings.notEqual(asList("one", "two", "three", "four", "five"));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("4", e.getMessage());
		}
		try
		{
			LimitedListField.create(new StringField(), 1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumSize must be greater 1, but was 1", e.getMessage());
		}
	}

	private static DateField assertDate(final Iterator<?> i, final int num)
	{
		final DateField date = (DateField)i.next();
		assertEquals(TYPE, date.getType());
		assertEquals("dates-"+num, date.getName());
		assertEquals(false, date.isMandatory());
		assertEquals(false, date.isFinal());
		return date;
	}

	private static StringField assertString(final Iterator<?> i, final int num)
	{
		final StringField string = (StringField)i.next();
		assertEquals(TYPE, string.getType());
		assertEquals("strings-"+num, string.getName());
		assertEquals(false, string.isMandatory());
		assertEquals(false, string.isFinal());
		assertEquals(1, string.getMinimumLength());
		assertEquals(11, string.getMaximumLength());
		return string;
	}

	@Test void testInitialType()
	{
		assertEquals("java.util.List<java.lang.Integer>", nums   .getInitialType().toString());
		assertEquals("java.util.List<java.util.Date>"   , dates  .getInitialType().toString());
		assertEquals("java.util.List<java.lang.String>" , strings.getInitialType().toString());
	}
}

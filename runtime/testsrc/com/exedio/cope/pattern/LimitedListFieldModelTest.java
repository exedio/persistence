/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class LimitedListFieldModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(LimitedListFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(LimitedListFieldModelTest.class, "MODEL");
	}

	LimitedListFieldItem item;
	protected static final Integer i0 = Integer.valueOf(0);
	protected static final Integer i1 = Integer.valueOf(1);
	protected static final Integer i2 = Integer.valueOf(2);
	protected static final Integer i3 = Integer.valueOf(3);
	protected static final Integer i4 = Integer.valueOf(4);

	public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.num1,
				item.num2,
				item.num3,
				item.nums,
				item.dates,
				item.dates.getListSources().get(0),
				item.dates.getListSources().get(1),
				item.strings,
				item.strings.getListSources().get(0),
				item.strings.getListSources().get(1),
				item.strings.getListSources().get(2),
				item.strings.getListSources().get(3),
			}), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.num1.getType());
		assertEquals(item.TYPE, item.num2.getType());
		assertEquals(item.TYPE, item.num3.getType());
		assertEquals("num1", item.num1.getName());
		assertEquals("num2", item.num2.getName());
		assertEquals("num3", item.num3.getName());
		assertEquals(item.TYPE, item.nums.getType());
		assertEquals("nums", item.nums.getName());
		assertEqualsUnmodifiable(list(item.num1, item.num2, item.num3), item.nums.getSourceFeatures());
		assertEquals(item.nums, item.num1.getPattern());
		assertEquals(item.nums, item.num2.getPattern());
		assertEquals(item.nums, item.num3.getPattern());
		assertEqualsUnmodifiable(list(item.num1, item.num2, item.num3), item.nums.getListSources());
		assertEquals(false, item.nums.isInitial());
		assertEquals(false, item.nums.isFinal());
		assertEquals(true,  item.nums.isMandatory());
		assertContains(item.nums.getInitialExceptions());

		assertEquals(3, item.nums   .getMaximumSize());
		assertEquals(2, item.dates  .getMaximumSize());
		assertEquals(4, item.strings.getMaximumSize());

		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());
		final List<FunctionField<Date>> dateSources = item.dates.getListSources();
		assertEquals(2, dateSources.size());
		assertUnmodifiable(dateSources);
		final Iterator<?> dateSourcesIterator = dateSources.iterator();
		final DateField date0 = assertDate(dateSourcesIterator, 0);
		final DateField date1 = assertDate(dateSourcesIterator, 1);
		assertTrue(!dateSourcesIterator.hasNext());
		assertEquals(item.dates, date0.getPattern());
		assertEquals(item.dates, date1.getPattern());
		assertEqualsUnmodifiable(list(date0, date1), item.dates.getSourceFeatures());
		assertEqualsUnmodifiable(list(date0, date1), item.dates.getListSources());
		assertEquals(false, item.dates.isInitial());
		assertEquals(false, item.dates.isFinal());
		assertEquals(true,  item.dates.isMandatory());
		assertContains(item.dates.getInitialExceptions());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		final List<FunctionField<String>> stringSources = item.strings.getListSources();
		assertEquals(4, stringSources.size());
		assertUnmodifiable(stringSources);
		final Iterator<?> stringSourcesIterator = stringSources.iterator();
		final StringField string0 = assertString(stringSourcesIterator, 0);
		final StringField string1 = assertString(stringSourcesIterator, 1);
		final StringField string2 = assertString(stringSourcesIterator, 2);
		final StringField string3 = assertString(stringSourcesIterator, 3);
		assertTrue(!stringSourcesIterator.hasNext());
		assertEquals(item.strings, string0.getPattern());
		assertEquals(item.strings, string1.getPattern());
		assertEquals(item.strings, string2.getPattern());
		assertEquals(item.strings, string3.getPattern());
		assertEqualsUnmodifiable(list(string0, string1, string2, string3), item.strings.getSourceFeatures());
		assertEqualsUnmodifiable(list(string0, string1, string2, string3), item.strings.getListSources());
		assertEquals(false, item.strings.isInitial());
		assertEquals(false, item.strings.isFinal());
		assertEquals(true,  item.strings.isMandatory());
		assertContains(StringLengthViolationException.class, item.strings.getInitialExceptions());

		assertEquals(
				list(item.num1, item.num2, item.num3, date0, date1, string0, string1, string2, string3),
				item.TYPE.getDeclaredFields());

		assertFalse(item.num1.isAnnotationPresent(Computed.class));
		assertFalse(item.num2.isAnnotationPresent(Computed.class));
		assertFalse(item.num3.isAnnotationPresent(Computed.class));
		assertTrue (date0    .isAnnotationPresent(Computed.class));
		assertTrue (date1    .isAnnotationPresent(Computed.class));
		assertTrue (string0  .isAnnotationPresent(Computed.class));
		assertTrue (string1  .isAnnotationPresent(Computed.class));
		assertTrue (string2  .isAnnotationPresent(Computed.class));
		assertTrue (string3  .isAnnotationPresent(Computed.class));

		assertSerializedSame(item.nums   , 399);
		assertSerializedSame(item.dates  , 400);
		assertSerializedSame(item.strings, 402);

		// test persistence
		final Date ts1 = new Date(8172541283976l);
		final Date ts2 = new Date(3874656234632l);

		// TODO return Condition.FALSE instead
		try
		{
			item.nums.equal(listg(i1, i2, i3, i4));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("3", e.getMessage());
		}
		try
		{
			item.dates.equal(listg(ts1, ts2, ts1));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("2", e.getMessage());
		}
		try
		{
			item.strings.equal(listg("one", "two", "three", "four", "five"));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("4", e.getMessage());
		}
		// TODO return Condition.TRUE instead
		try
		{
			item.nums.notEqual(listg(i1, i2, i3, i4));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("3", e.getMessage());
		}
		try
		{
			item.dates.notEqual(listg(ts1, ts2, ts1));
			fail();
		}
		catch(final ArrayIndexOutOfBoundsException e)
		{
			assertEquals("2", e.getMessage());
		}
		try
		{
			item.strings.notEqual(listg("one", "two", "three", "four", "five"));
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

	private final DateField assertDate(final Iterator<?> i, final int num)
	{
		final DateField date = (DateField)i.next();
		assertEquals(item.TYPE, date.getType());
		assertEquals("dates-"+num, date.getName());
		assertEquals(false, date.isMandatory());
		assertEquals(false, date.isFinal());
		return date;
	}

	private final StringField assertString(final Iterator<?> i, final int num)
	{
		final StringField string = (StringField)i.next();
		assertEquals(item.TYPE, string.getType());
		assertEquals("strings-"+num, string.getName());
		assertEquals(false, string.isMandatory());
		assertEquals(false, string.isFinal());
		assertEquals(1, string.getMinimumLength());
		assertEquals(11, string.getMaximumLength());
		return string;
	}

}

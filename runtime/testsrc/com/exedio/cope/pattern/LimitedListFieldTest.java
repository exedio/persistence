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

import static com.exedio.cope.pattern.LimitedListFieldItem.TYPE;
import static com.exedio.cope.pattern.LimitedListFieldItem.nums;
import static com.exedio.cope.pattern.LimitedListFieldItem.strings;
import static com.exedio.cope.pattern.LimitedListFieldItemFieldItem.limitedListFieldItem;
import static java.util.Arrays.asList;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Join;
import com.exedio.cope.Query;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class LimitedListFieldTest extends AbstractRuntimeTest
{
	public LimitedListFieldTest()
	{
		super(LimitedListFieldModelTest.MODEL);
	}

	LimitedListFieldItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new LimitedListFieldItem(1, 2, 3));
	}

	public void testNum()
	{
		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, TYPE.search(nums.equal(asList(i1, i2, i3))));
		assertContains(TYPE.search(nums.equal(asList(i1, i2))));
		assertContains(TYPE.search(nums.notEqual(asList(i1, i2, i3))));
		assertContains(item, TYPE.search(nums.notEqual(asList(i1, i2))));
		assertContains(item, TYPE.search(nums.contains(i1)));
		assertContains(item, TYPE.search(nums.contains(i2)));
		assertContains(item, TYPE.search(nums.contains(i3)));
		assertContains(TYPE.search(nums.contains(null)));

		item.setNums(asList(i3, i2, i1));
		assertEqualsUnmodifiable(list(i3, i2, i1), item.getNums());
		assertEquals(i3, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i1, item.getNum3());

		item.setNums(asList(i2, i1));
		assertEqualsUnmodifiable(list(i2, i1), item.getNums());
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		try
		{
			item.setNums(asList(i2, i1, i3, i1));
			fail();
		}
		catch(final ListSizeViolationException e)
		{
			assertSame(nums, e.getFeature());
			assertEquals(4, e.getSize());
			assertEquals(
					"size violation on " + item.getCopeID() + ", " +
					"value is too long for LimitedListFieldItem.nums, " +
					"must be at most 3 elements, but was 4.", e.getMessage());
		}
		assertEqualsUnmodifiable(list(i2, i1), item.getNums());
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		item.setNums(Arrays.<Integer>asList());
		assertEquals(null, item.getNum1());
		assertEquals(null, item.getNum2());
		assertEquals(null, item.getNum3());
		assertContains(item, TYPE.search(nums.equal(Arrays.<Integer>asList())));
		assertContains(TYPE.search(nums.equal(asList(i1))));
		assertContains(TYPE.search(nums.notEqual(Arrays.<Integer>asList())));
		assertContains(item, TYPE.search(nums.notEqual(asList(i1))));
		assertContains(TYPE.search(nums.contains(i1)));
		assertContains(TYPE.search(nums.contains(i2)));
		assertContains(TYPE.search(nums.contains(i3)));
		assertContains(item, TYPE.search(nums.contains(null)));

		item.setNums(asList(i1, i2, i3));
		assertEqualsUnmodifiable(list(i1, i2, i3), item.getNums());

		item.setNums(asList(null, i2, i3));
		assertEqualsUnmodifiable(list(null, i2, i3), item.getNums());

		item.setNums(asList(i1, null, i3));
		assertEqualsUnmodifiable(list(i1, null, i3), item.getNums());

		item.setNums(asList((Integer)null, null, null));
		assertEqualsUnmodifiable(list(null, null, null), item.getNums());
	}

	public void testDate()
	{
		final Date ts1 = new Date(8172541283976l);
		final Date ts2 = new Date(3874656234632l);
		item.setDates(asList(ts1, ts2));
		assertEqualsUnmodifiable(list(ts1, ts2), item.getDates());
		assertEquals(ts1, item.getDate0());
		assertEquals(ts2, item.getDate1());
	}

	public void testString()
	{
		item.setStrings(asList("hallo", "bello"));
		assertEqualsUnmodifiable(list("hallo", "bello"), item.getStrings());
		assertEquals("hallo", item.getString0());
		assertEquals("bello", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertContains(TYPE.search(strings.equal(Arrays.<String>asList())));
		assertContains(TYPE.search(strings.equal(asList("hallo"))));
		assertContains(item, TYPE.search(strings.equal(asList("hallo", "bello"))));
		assertContains(TYPE.search(strings.equal(asList("bello", "hallo", "zollo"))));
		assertContains(TYPE.search(strings.equal(asList("bello", "hallo"))));
		assertContains(item, TYPE.search(strings.notEqual(Arrays.<String>asList())));
		assertContains(item, TYPE.search(strings.notEqual(asList("hallo"))));
		assertContains(TYPE.search(strings.notEqual(asList("hallo", "bello"))));
		assertContains(item, TYPE.search(strings.notEqual(asList("bello", "hallo", "zollo"))));
		assertContains(item, TYPE.search(strings.notEqual(asList("bello", "hallo"))));
		assertContains(item, TYPE.search(strings.contains("hallo")));
		assertContains(item, TYPE.search(strings.contains("bello")));
		assertContains(item, TYPE.search(strings.contains(null)));
		assertContains(TYPE.search(strings.contains("zollo")));

		item.set(strings.map(asList("zicko", "zacko", "zocko")));
		assertEqualsUnmodifiable(list("zicko", "zacko", "zocko"), item.getStrings());
		assertEquals("zicko", item.getString0());
		assertEquals("zacko", item.getString1());
		assertEquals("zocko", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		final LimitedListFieldItem item2 = deleteOnTearDown(new LimitedListFieldItem(strings.map(asList("lets1", "lets2", "lets3", "lets4"))));
		assertEqualsUnmodifiable(list("lets1", "lets2", "lets3", "lets4"), item2.getStrings());
		assertEquals("lets1", item2.getString0());
		assertEquals("lets2", item2.getString1());
		assertEquals("lets3", item2.getString2());
		assertEquals("lets4", item2.getString3());
		assertEquals(4, item2.getStringLength());

		final LimitedListFieldItem item3 = deleteOnTearDown(LimitedListFieldItem.TYPE.newItem(strings.map(asList("fetz1", null, null, null))));
		assertEqualsUnmodifiable(list("fetz1", null, null, null), item3.getStrings());
		assertEquals("fetz1", item3.getString0());
		assertEquals(null, item3.getString1());
		assertEquals(null, item3.getString2());
		assertEquals(null, item3.getString3());
		assertEquals(4, item3.getStringLength());
	}

	public void testNull()
	{
		assertEqualsUnmodifiable(list(), item.getStrings());

		item.setStrings(asList("a", "b", null));
		assertEqualsUnmodifiable(list("a", "b", null), item.getStrings());
		assertEquals("a", item.getString0());
		assertEquals("b", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(asList("a", null, "c"));
		assertEqualsUnmodifiable(list("a", null, "c"), item.getStrings());
		assertEquals("a", item.getString0());
		assertEquals(null, item.getString1());
		assertEquals("c", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(asList(null, "b", "c"));
		assertEqualsUnmodifiable(list(null, "b", "c"), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals("b", item.getString1());
		assertEquals("c", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(asList(null, "b", null));
		assertEqualsUnmodifiable(list(null, "b", null), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals("b", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(asList((String)null, null, null));
		assertEqualsUnmodifiable(list(null, null, null), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals(null, item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());
	}

	public void testContainsInJoin()
	{
		final Query<LimitedListFieldItemFieldItem> q = LimitedListFieldItemFieldItem.TYPE.newQuery();
		final Join j = q.join(LimitedListFieldItem.TYPE, limitedListFieldItem.equalTarget());

		final String f = "l1.LimitedListFieldItem.strings-";
		assertEquals("("+f+"0='a' OR "+f+"1='a' OR "+f+"2='a' OR "+f+"3='a')",
			strings.contains(j, "a").toString());

		assertEquals("("+f+"0 is null OR "+f+"1 is null OR "+f+"2 is null OR "+f+"3 is null)",
			strings.contains(j, null).toString());
	}

	public void testContainsAny()
	{
		final String f = "LimitedListFieldItem.strings-";
		assertEquals("FALSE", strings.containsAny(Collections.<String>emptyList()).toString());
		assertEquals("("+f+"0='a' OR "+f+"1='a' OR "+f+"2='a' OR "+f+"3='a')",
			strings.containsAny(Arrays.asList("a")).toString());
		assertEquals(
			"(("+f+"0='a' OR "+f+"1='a' OR "+f+"2='a' OR "+f+"3='a') " +
			 "OR ("+f+"0='b' OR "+f+"1='b' OR "+f+"2='b' OR "+f+"3='b'))",
			strings.containsAny(Arrays.asList("a","b")).toString());
	}

	public void testContainsAnyInJoin()
	{
		final Query<LimitedListFieldItemFieldItem> q = LimitedListFieldItemFieldItem.TYPE.newQuery();
		final Join join = q.join(LimitedListFieldItem.TYPE, limitedListFieldItem.equalTarget());

		final String f = "l1.LimitedListFieldItem.strings-";

		assertEquals("FALSE", strings.containsAny(join, Collections.<String>emptyList()).toString());
		assertEquals("("+f+"0='a' OR "+f+"1='a' OR "+f+"2='a' OR "+f+"3='a')",
			strings.containsAny(join, Arrays.asList("a")).toString());
		assertEquals(
			"(("+f+"0='a' OR "+f+"1='a' OR "+f+"2='a' OR "+f+"3='a') " +
				"OR ("+f+"0='b' OR "+f+"1='b' OR "+f+"2='b' OR "+f+"3='b'))",
			strings.containsAny(join, Arrays.asList("a","b")).toString());
	}
}

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

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.SetValue;
import com.exedio.cope.junit.CopeAssert;
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
		assertContains(item, TYPE.search(nums.equal(listg(i1, i2, i3))));
		assertContains(TYPE.search(nums.equal(listg(i1, i2))));
		assertContains(TYPE.search(nums.notEqual(listg(i1, i2, i3))));
		assertContains(item, TYPE.search(nums.notEqual(listg(i1, i2))));
		assertContains(item, TYPE.search(nums.contains(i1)));
		assertContains(item, TYPE.search(nums.contains(i2)));
		assertContains(item, TYPE.search(nums.contains(i3)));
		assertContains(TYPE.search(nums.contains(null)));

		item.setNums(listg(i3, i2, i1));
		assertEqualsUnmodifiable(list(i3, i2, i1), item.getNums());
		assertEquals(i3, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i1, item.getNum3());

		item.setNums(listg(i2, i1));
		assertEqualsUnmodifiable(list(i2, i1), item.getNums());
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		try
		{
			item.setNums(listg(i2, i1, i3, i1));
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

		item.setNums(CopeAssert.<Integer>listg());
		assertEquals(null, item.getNum1());
		assertEquals(null, item.getNum2());
		assertEquals(null, item.getNum3());
		assertContains(item, TYPE.search(nums.equal(CopeAssert.<Integer>listg())));
		assertContains(TYPE.search(nums.equal(listg(i1))));
		assertContains(TYPE.search(nums.notEqual(CopeAssert.<Integer>listg())));
		assertContains(item, TYPE.search(nums.notEqual(listg(i1))));
		assertContains(TYPE.search(nums.contains(i1)));
		assertContains(TYPE.search(nums.contains(i2)));
		assertContains(TYPE.search(nums.contains(i3)));
		assertContains(item, TYPE.search(nums.contains(null)));

		item.setNums(listg(i1, i2, i3));
		assertEqualsUnmodifiable(list(i1, i2, i3), item.getNums());

		item.setNums(listg(null, i2, i3));
		assertEqualsUnmodifiable(list(null, i2, i3), item.getNums());

		item.setNums(listg(i1, null, i3));
		assertEqualsUnmodifiable(list(i1, null, i3), item.getNums());

		item.setNums(listg((Integer)null, null, null));
		assertEqualsUnmodifiable(list(null, null, null), item.getNums());
	}

	public void testDate()
	{
		final Date ts1 = new Date(8172541283976l);
		final Date ts2 = new Date(3874656234632l);
		item.setDates(listg(ts1, ts2));
		assertEqualsUnmodifiable(list(ts1, ts2), item.getDates());
		assertEquals(ts1, item.getDate0());
		assertEquals(ts2, item.getDate1());
	}

	public void testString()
	{
		item.setStrings(listg("hallo", "bello"));
		assertEqualsUnmodifiable(list("hallo", "bello"), item.getStrings());
		assertEquals("hallo", item.getString0());
		assertEquals("bello", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertContains(TYPE.search(strings.equal(CopeAssert.<String>listg())));
		assertContains(TYPE.search(strings.equal(listg("hallo"))));
		assertContains(item, TYPE.search(strings.equal(listg("hallo", "bello"))));
		assertContains(TYPE.search(strings.equal(listg("bello", "hallo", "zollo"))));
		assertContains(TYPE.search(strings.equal(listg("bello", "hallo"))));
		assertContains(item, TYPE.search(strings.notEqual(CopeAssert.<String>listg())));
		assertContains(item, TYPE.search(strings.notEqual(listg("hallo"))));
		assertContains(TYPE.search(strings.notEqual(listg("hallo", "bello"))));
		assertContains(item, TYPE.search(strings.notEqual(listg("bello", "hallo", "zollo"))));
		assertContains(item, TYPE.search(strings.notEqual(listg("bello", "hallo"))));
		assertContains(item, TYPE.search(strings.contains("hallo")));
		assertContains(item, TYPE.search(strings.contains("bello")));
		assertContains(item, TYPE.search(strings.contains(null)));
		assertContains(TYPE.search(strings.contains("zollo")));

		item.set(strings.map(listg("zicko", "zacko", "zocko")));
		assertEqualsUnmodifiable(list("zicko", "zacko", "zocko"), item.getStrings());
		assertEquals("zicko", item.getString0());
		assertEquals("zacko", item.getString1());
		assertEquals("zocko", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		final LimitedListFieldItem item2 = deleteOnTearDown(new LimitedListFieldItem(new SetValue<?>[]{strings.map(listg("lets1", "lets2", "lets3", "lets4"))}));
		assertEqualsUnmodifiable(list("lets1", "lets2", "lets3", "lets4"), item2.getStrings());
		assertEquals("lets1", item2.getString0());
		assertEquals("lets2", item2.getString1());
		assertEquals("lets3", item2.getString2());
		assertEquals("lets4", item2.getString3());
		assertEquals(4, item2.getStringLength());

		final LimitedListFieldItem item3 = deleteOnTearDown(LimitedListFieldItem.TYPE.newItem(strings.map(listg("fetz1", null, null, null))));
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

		item.setStrings(listg("a", "b", null));
		assertEqualsUnmodifiable(list("a", "b", null), item.getStrings());
		assertEquals("a", item.getString0());
		assertEquals("b", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(listg("a", null, "c"));
		assertEqualsUnmodifiable(list("a", null, "c"), item.getStrings());
		assertEquals("a", item.getString0());
		assertEquals(null, item.getString1());
		assertEquals("c", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(listg(null, "b", "c"));
		assertEqualsUnmodifiable(list(null, "b", "c"), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals("b", item.getString1());
		assertEquals("c", item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(listg(null, "b", null));
		assertEqualsUnmodifiable(list(null, "b", null), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals("b", item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());

		item.setStrings(listg((String)null, null, null));
		assertEqualsUnmodifiable(list(null, null, null), item.getStrings());
		assertEquals(null, item.getString0());
		assertEquals(null, item.getString1());
		assertEquals(null, item.getString2());
		assertEquals(null, item.getString3());
		assertEquals(3, item.getStringLength());
	}
}

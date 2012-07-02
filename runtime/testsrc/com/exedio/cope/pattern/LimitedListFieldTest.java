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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.DateField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;

public class LimitedListFieldTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(LimitedListFieldItem.TYPE);

	static
	{
		MODEL.enableSerialization(LimitedListFieldTest.class, "MODEL");
	}

	public LimitedListFieldTest()
	{
		super(MODEL);
	}

	LimitedListFieldItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new LimitedListFieldItem(1, 2, 3));
	}

	public void testIt()
	{
		final List<FunctionField<Date>> dateSources = item.dates.getListSources();
		assertEquals(2, dateSources.size());
		assertUnmodifiable(dateSources);
		final Iterator<?> dateSourcesIterator = dateSources.iterator();
		final DateField date0 = (DateField)dateSourcesIterator.next();
		final DateField date1 = (DateField)dateSourcesIterator.next();
		final List<FunctionField<String>> stringSources = item.strings.getListSources();
		final Iterator<?> stringSourcesIterator = stringSources.iterator();
		final StringField string0 = (StringField)stringSourcesIterator.next();
		final StringField string1 = (StringField)stringSourcesIterator.next();
		final StringField string2 = (StringField)stringSourcesIterator.next();
		final StringField string3 = (StringField)stringSourcesIterator.next();

		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, item.TYPE.search(item.nums.equal(listg(i1, i2, i3))));
		assertContains(item.TYPE.search(item.nums.equal(listg(i1, i2))));
		assertContains(item.TYPE.search(item.nums.notEqual(listg(i1, i2, i3))));
		assertContains(item, item.TYPE.search(item.nums.notEqual(listg(i1, i2))));
		assertContains(item, item.TYPE.search(item.nums.contains(i1)));
		assertContains(item, item.TYPE.search(item.nums.contains(i2)));
		assertContains(item, item.TYPE.search(item.nums.contains(i3)));
		assertContains(item.TYPE.search(item.nums.contains(null)));

		item.setNums(listg(i3, i2, i1));
		assertEquals(list(i3, i2, i1), item.getNums());
		assertEquals(i3, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i1, item.getNum3());

		item.setNums(listg(i2, i1));
		assertEquals(list(i2, i1), item.getNums());
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
			assertSame(item.nums, e.getFeature());
			assertEquals(4, e.getSize());
			assertEquals(
					"size violation on " + item.getCopeID() + ", " +
					"value is too long for LimitedListFieldItem.nums, " +
					"must be at most 3 elements, but was 4.", e.getMessage());
		}
		assertEquals(list(i2, i1), item.getNums());
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		item.setNums(LimitedListFieldTest.<Integer>listg());
		assertEquals(null, item.getNum1());
		assertEquals(null, item.getNum2());
		assertEquals(null, item.getNum3());
		assertContains(item, item.TYPE.search(item.nums.equal(LimitedListFieldTest.<Integer>listg())));
		assertContains(item.TYPE.search(item.nums.equal(listg(i1))));
		assertContains(item.TYPE.search(item.nums.notEqual(LimitedListFieldTest.<Integer>listg())));
		assertContains(item, item.TYPE.search(item.nums.notEqual(listg(i1))));
		assertContains(item.TYPE.search(item.nums.contains(i1)));
		assertContains(item.TYPE.search(item.nums.contains(i2)));
		assertContains(item.TYPE.search(item.nums.contains(i3)));
		assertContains(item, item.TYPE.search(item.nums.contains(null)));

		item.setNum1(i1);
		item.setNum2(i2);
		item.setNum3(i3);
		assertEquals(list(i1, i2, i3), item.getNums());

		item.setNum1(null);
		item.setNum2(i2);
		item.setNum3(i3);
		assertEquals(list(i2, i3), item.getNums());

		item.setNum1(i1);
		item.setNum2(null);
		item.setNum3(i3);
		assertEquals(list(i1, i3), item.getNums());

		item.setNum1(null);
		item.setNum2(null);
		item.setNum3(null);
		assertEquals(list(), item.getNums());

		final Date ts1 = new Date(8172541283976l);
		final Date ts2 = new Date(3874656234632l);
		item.setDates(listg(ts1, ts2));
		assertEquals(list(ts1, ts2), item.getDates());
		assertEquals(ts1, item.get(date0));
		assertEquals(ts2, item.get(date1));

		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertEquals("hallo", item.get(string0));
		assertEquals("bello", item.get(string1));
		assertEquals(null, item.get(string2));
		assertEquals(null, item.get(string3));
		assertContains(item.TYPE.search(item.strings.equal(LimitedListFieldTest.<String>listg())));
		assertContains(item.TYPE.search(item.strings.equal(listg("hallo"))));
		assertContains(item, item.TYPE.search(item.strings.equal(listg("hallo", "bello"))));
		assertContains(item.TYPE.search(item.strings.equal(listg("bello", "hallo", "zollo"))));
		assertContains(item.TYPE.search(item.strings.equal(listg("bello", "hallo"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(LimitedListFieldTest.<String>listg())));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("hallo"))));
		assertContains(item.TYPE.search(item.strings.notEqual(listg("hallo", "bello"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("bello", "hallo", "zollo"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("bello", "hallo"))));
		assertContains(item, item.TYPE.search(item.strings.contains("hallo")));
		assertContains(item, item.TYPE.search(item.strings.contains("bello")));
		assertContains(item, item.TYPE.search(item.strings.contains(null)));
		assertContains(item.TYPE.search(item.strings.contains("zollo")));

		item.set(item.strings.map(listg("zicko", "zacko", "zocko")));
		assertEquals(list("zicko", "zacko", "zocko"), item.getStrings());
		assertEquals("zicko", item.get(string0));
		assertEquals("zacko", item.get(string1));
		assertEquals("zocko", item.get(string2));
		assertEquals(null, item.get(string3));

		final LimitedListFieldItem item2 = deleteOnTearDown(new LimitedListFieldItem(new SetValue[]{item.strings.map(listg("lets1", "lets2", "lets3", "lets4"))}));
		assertEquals(list("lets1", "lets2", "lets3", "lets4"), item2.getStrings());
		assertEquals("lets1", item2.get(string0));
		assertEquals("lets2", item2.get(string1));
		assertEquals("lets3", item2.get(string2));
		assertEquals("lets4", item2.get(string3));

		final LimitedListFieldItem item3 = deleteOnTearDown(LimitedListFieldItem.TYPE.newItem(item.strings.map(listg("fetz1", null, null, null))));
		assertEquals(list("fetz1"), item3.getStrings());
		assertEquals("fetz1", item3.get(string0));
		assertEquals(null, item3.get(string1));
		assertEquals(null, item3.get(string2));
		assertEquals(null, item3.get(string3));
	}
}

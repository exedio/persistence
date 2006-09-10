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

package com.exedio.cope.pattern;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.Main;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;

public class AttributeListLimitedTest extends AbstractLibTest
{
	
	public AttributeListLimitedTest()
	{
		super(Main.attributeListLimitedModel);
	}

	AttributeListLimitedItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new AttributeListLimitedItem(1, 2, 3));
	}
	
	public void testIt()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.num1,
				item.num2,
				item.num3,
				item.nums,
				item.dates,
				item.dates.getSources().get(0),
				item.dates.getSources().get(1),
				item.strings,
				item.strings.getSources().get(0),
				item.strings.getSources().get(1),
				item.strings.getSources().get(2),
				item.strings.getSources().get(3),
			}), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.num1.getType());
		assertEquals(item.TYPE, item.num2.getType());
		assertEquals(item.TYPE, item.num3.getType());
		assertEquals("num1", item.num1.getName());
		assertEquals("num2", item.num2.getName());
		assertEquals("num3", item.num3.getName());
		assertEquals(item.TYPE, item.nums.getType());
		assertEquals("nums", item.nums.getName());
		assertEqualsUnmodifiable(list(item.num1, item.num2, item.num3), item.nums.getSources());
		assertEqualsUnmodifiable(list(item.nums), item.num1.getPatterns());
		assertEqualsUnmodifiable(list(item.nums), item.num2.getPatterns());
		assertEqualsUnmodifiable(list(item.nums), item.num3.getPatterns());
		assertEquals(false, item.nums.isInitial());
		assertEquals(false, item.nums.isFinal());
		assertContains(item.nums.getSetterExceptions());

		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());
		final List<FunctionAttribute<Date>> dateSources = item.dates.getSources();
		assertEquals(2, dateSources.size());
		assertUnmodifiable(dateSources);
		final Iterator dateSourcesIterator = dateSources.iterator();
		final DateField date1 = assertDate(dateSourcesIterator, 1);
		final DateField date2 = assertDate(dateSourcesIterator, 2);
		assertTrue(!dateSourcesIterator.hasNext());
		assertEqualsUnmodifiable(list(item.dates), date1.getPatterns());
		assertEqualsUnmodifiable(list(item.dates), date2.getPatterns());
		assertEquals(false, item.dates.isInitial());
		assertEquals(false, item.dates.isFinal());
		assertContains(item.dates.getSetterExceptions());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		final List<FunctionAttribute<String>> stringSources = item.strings.getSources();
		assertEquals(4, stringSources.size());
		assertUnmodifiable(stringSources);
		final Iterator stringSourcesIterator = stringSources.iterator();
		final StringField string1 = assertString(stringSourcesIterator, 1);
		final StringField string2 = assertString(stringSourcesIterator, 2);
		final StringField string3 = assertString(stringSourcesIterator, 3);
		final StringField string4 = assertString(stringSourcesIterator, 4);
		assertTrue(!stringSourcesIterator.hasNext());
		assertEqualsUnmodifiable(list(item.strings), string1.getPatterns());
		assertEqualsUnmodifiable(list(item.strings), string2.getPatterns());
		assertEqualsUnmodifiable(list(item.strings), string3.getPatterns());
		assertEqualsUnmodifiable(list(item.strings), string4.getPatterns());
		assertEquals(false, item.strings.isInitial());
		assertEquals(false, item.strings.isFinal());
		assertContains(LengthViolationException.class, item.strings.getSetterExceptions());

		assertEquals(
				list(item.num1, item.num2, item.num3, date1, date2, string1, string2, string3, string4),
				item.TYPE.getDeclaredAttributes());

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
		
		// test persistence
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
		catch(RuntimeException e)
		{
			assertEquals("value exceeds limit 3 for " + item.nums + ": " + listg(i2, i1, i3, i1), e.getMessage());
		}
		assertEquals(list(i2, i1), item.getNums());
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		item.setNums(AttributeListLimitedTest.<Integer>listg());
		assertEquals(null, item.getNum1());
		assertEquals(null, item.getNum2());
		assertEquals(null, item.getNum3());
		assertContains(item, item.TYPE.search(item.nums.equal(AttributeListLimitedTest.<Integer>listg())));
		assertContains(item.TYPE.search(item.nums.equal(listg(i1))));
		assertContains(item.TYPE.search(item.nums.notEqual(AttributeListLimitedTest.<Integer>listg())));
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
		assertEquals(ts1, item.get(date1));
		assertEquals(ts2, item.get(date2));
		
		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertEquals("hallo", item.get(string1));
		assertEquals("bello", item.get(string2));
		assertEquals(null, item.get(string3));
		assertEquals(null, item.get(string4));
		assertContains(item.TYPE.search(item.strings.equal(AttributeListLimitedTest.<String>listg())));
		assertContains(item.TYPE.search(item.strings.equal(listg("hallo"))));
		assertContains(item, item.TYPE.search(item.strings.equal(listg("hallo", "bello"))));
		assertContains(item.TYPE.search(item.strings.equal(listg("bello", "hallo", "zollo"))));
		assertContains(item.TYPE.search(item.strings.equal(listg("bello", "hallo"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(AttributeListLimitedTest.<String>listg())));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("hallo"))));
		assertContains(item.TYPE.search(item.strings.notEqual(listg("hallo", "bello"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("bello", "hallo", "zollo"))));
		assertContains(item, item.TYPE.search(item.strings.notEqual(listg("bello", "hallo"))));
		assertContains(item, item.TYPE.search(item.strings.contains("hallo")));
		assertContains(item, item.TYPE.search(item.strings.contains("bello")));
		assertContains(item, item.TYPE.search(item.strings.contains(null)));
		assertContains(item.TYPE.search(item.strings.contains("zollo")));
		
		item.set(new SetValue[]{item.strings.map(listg("zicko", "zacko", "zocko"))});
		assertEquals(list("zicko", "zacko", "zocko"), item.getStrings());
		assertEquals("zicko", item.get(string1));
		assertEquals("zacko", item.get(string2));
		assertEquals("zocko", item.get(string3));
		assertEquals(null, item.get(string4));
		
		final AttributeListLimitedItem item2 = new AttributeListLimitedItem(new SetValue[]{item.strings.map(listg("lets1", "lets2", "lets3", "lets4"))});
		deleteOnTearDown(item2);
		assertEquals(list("lets1", "lets2", "lets3", "lets4"), item2.getStrings());
		assertEquals("lets1", item2.get(string1));
		assertEquals("lets2", item2.get(string2));
		assertEquals("lets3", item2.get(string3));
		assertEquals("lets4", item2.get(string4));
		
		final AttributeListLimitedItem item3 = AttributeListLimitedItem.TYPE.newItem(new SetValue[]{item.strings.map(listg("fetz1", null, null, null))});
		deleteOnTearDown(item3);
		assertEquals(list("fetz1"), item3.getStrings());
		assertEquals("fetz1", item3.get(string1));
		assertEquals(null, item3.get(string2));
		assertEquals(null, item3.get(string3));
		assertEquals(null, item3.get(string4));
	}
	
	private final DateField assertDate(final Iterator i, final int num)
	{
		final DateField date = (DateField)i.next();
		assertEquals(item.TYPE, date.getType());
		assertEquals("dates"+num, date.getName());
		assertEquals(false, date.isMandatory());
		assertEquals(false, date.isFinal());
		return date;
	}

	private final StringField assertString(final Iterator i, final int num)
	{
		final StringField string = (StringField)i.next();
		assertEquals(item.TYPE, string.getType());
		assertEquals("strings"+num, string.getName());
		assertEquals(false, string.isMandatory());
		assertEquals(false, string.isFinal());
		assertEquals(1, string.getMinimumLength());
		assertEquals(11, string.getMaximumLength());
		return string;
	}

}

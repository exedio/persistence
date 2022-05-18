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

import static com.exedio.cope.pattern.SetFieldItem.TYPE;
import static com.exedio.cope.pattern.SetFieldItem.getParentsOfStrings;
import static com.exedio.cope.pattern.SetFieldItem.strings;
import static com.exedio.cope.pattern.SetFieldModelTest.MODEL;
import static com.exedio.cope.pattern.SetFieldModelTest.datesType;
import static com.exedio.cope.pattern.SetFieldModelTest.stringsElement;
import static com.exedio.cope.pattern.SetFieldModelTest.stringsType;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetFieldTest extends TestWithEnvironment
{
	public SetFieldTest()
	{
		super(MODEL);
	}

	SetFieldItem item;
	SetFieldItem otherItem;

	@BeforeEach final void setUp()
	{
		item = new SetFieldItem();
		otherItem = new SetFieldItem();
	}

	@Test void testQuery()
	{
		assertEquals("select element from SetFieldItem-strings" + " where parent='" + item + "' order by element", item.getStringsQuery().toString());
		assertEquals("select element from SetFieldItem-dates"   + " where parent='" + item + "' order by element", item.getDatesQuery  ().toString());
	}

	@Test void testSet()
	{
		item.assertStrings();
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(listOf("1hallo", "2bello", String.class));
		item.assertStrings("1hallo", "2bello");
		assertContains(item, getParentsOfStrings("1hallo"));
		assertContains(item, getParentsOfStrings("2bello"));
		assertContains(getParentsOfStrings("3knollo"));
		assertContains(getParentsOfStrings(null));
		final Item r0;
		final Item r1;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r0 = i.next();
			r1 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("1hallo", r0.get(stringsElement));
		assertEquals("2bello", r1.get(stringsElement));

		item.setStrings(listOf("2bello", "3knollo", String.class));
		item.assertStrings("2bello", "3knollo");
		assertContains(getParentsOfStrings("1hallo"));
		assertContains(item, getParentsOfStrings("2bello"));
		assertContains(item, getParentsOfStrings("3knollo"));
		assertContains(getParentsOfStrings(null));
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("3knollo", r0.get(stringsElement));
		assertEquals("2bello", r1.get(stringsElement));

		item.setStrings(listOf("3knollo", String.class));
		item.assertStrings("3knollo");
		assertContains(getParentsOfStrings("1hallo"));
		assertContains(getParentsOfStrings("2bello"));
		assertContains(item, getParentsOfStrings("3knollo"));
		assertContains(getParentsOfStrings(null));
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("3knollo", r0.get(stringsElement));
		assertFalse(r1.existsCopeItem());

		item.setStrings(listOf("zack1", "zack2", "zack3", String.class));
		item.assertStrings("zack1", "zack2", "zack3");
		final Item r1x;
		final Item r2;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			r1x = i.next();
			r2 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("zack1", r0.get(stringsElement));
		assertFalse(r1.existsCopeItem());
		assertEquals("zack2", r1x.get(stringsElement));
		assertEquals("zack3", r2.get(stringsElement));

		item.setStrings(listOf("null1", "null2", "null3", "null4", String.class));
		item.assertStrings("null1", "null2", "null3", "null4");
		assertContains(item, getParentsOfStrings("null1"));
		assertContains(getParentsOfStrings(null));
		assertContains(item, getParentsOfStrings("null2"));
		final Item r3;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1x, i.next());
			assertSame(r2, i.next());
			r3 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("null1", r0.get(stringsElement));
		assertFalse(r1.existsCopeItem());
		assertEquals("null2", r1x.get(stringsElement));
		assertEquals("null3", r2.get(stringsElement));
		assertEquals("null4", r3.get(stringsElement));

		item.setStrings(listOf(String.class));
		item.assertStrings();
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r1x.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());
	}

	@Test void testSetAndCast()
	{
		strings.setAndCast(item, listOf("1hallo", "2bello", String.class));
		item.assertStrings("1hallo", "2bello");

		final List<Integer> integers = listOf(1, 2, Integer.class);
		assertFails(
				() -> strings.setAndCast(item, integers),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to java.lang.String");
		item.assertStrings("1hallo", "2bello");
	}

	@Test void testAddRemove()
	{
		assertEquals(true, item.addToStrings("bing"));
		item.assertStrings("bing");
		final Item r4;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r4 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("bing", r4.get(strings.getElement()));

		assertEquals(false, item.addToStrings("bing"));
		item.assertStrings("bing");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r4, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("bing", r4.get(strings.getElement()));

		assertEquals(true, item.addToStrings("bong"));
		item.assertStrings("bing", "bong");
		final Item r5;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r4, i.next());
			r5 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("bing", r4.get(strings.getElement()));
		assertEquals("bong", r5.get(strings.getElement()));

		assertEquals(true, item.removeFromStrings("bing"));
		item.assertStrings("bong");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", r5.get(strings.getElement()));

		assertEquals(false, item.removeFromStrings("bing"));
		item.assertStrings("bong");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", r5.get(strings.getElement()));

		assertEquals(true, item.removeFromStrings("bong"));
		item.assertStrings();
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertFalse(r5.existsCopeItem());
	}

	@Test void testDate()
	{
		item.assertDates();
		assertEquals(0, datesType.newQuery(null).search().size());

		final Date date1 = new Date(918756915152l);
		final Date date2 = new Date(918756915153l);
		item.setDates(listOf(date1, date2, Date.class));
		item.assertDates(date1, date2);
		assertEquals(2, datesType.newQuery(null).search().size());
	}

	@Test void testMandatoryViolation()
	{
		assertFails(
				() -> item.setStrings(listOf("one1", null, "three3", String.class)),
				MandatoryViolationException.class,
				"mandatory violation for SetFieldItem-strings.element",
				strings.getElement());
		item.assertStrings();
	}

	@Test void testOtherViolation()
	{
		final StringLengthViolationException e = assertFails(
				() -> item.setStrings(listOf("one1", "two", "three3", String.class)),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for SetFieldItem-strings.element, " +
				"must be at least 4 characters, but was 3",
				strings.getElement());
		assertEquals("two", e.getValue());
		item.assertStrings();
	}

	@Test void testDuplicates()
	{
		item.setStrings(listOf("1one", "2dupl", "2dupl", "3two", String.class));
		item.assertStrings("1one", "2dupl", "3two");

		item.addToStrings("2dupl");
		item.assertStrings("1one", "2dupl", "3two");
	}

	@Test void testOrder()
	{
		item.setStrings(listOf("4four", "1one", "2two", String.class));
		item.assertStrings("1one", "2two", "4four");

		item.addToStrings("3three");
		item.assertStrings("1one", "2two", "3three", "4four");
	}

	@Test void testReorder()
	{
		item.setStrings(listOf("4four", "1one", "3three", "2two", String.class));
		item.assertStrings("1one", "2two", "3three", "4four");

		item.setStrings(listOf("4four", "3three", "1one", "2two", String.class));
		item.assertStrings("1one", "2two", "3three", "4four");
	}

	@Test void testMultipleItems()
	{
		final String rot = "1hellrot";
		final String blau = "2blau";
		final String gelb = "3gelb";

		item.setStrings(listOf(rot, blau, String.class));
		item.assertStrings(rot, blau);
		otherItem.setStrings(listOf(rot, String.class));
		otherItem.assertStrings(rot);

		assertContains(item, otherItem, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));

		item.setStrings(listOf(rot, gelb, blau, String.class));
		item.assertStrings(rot, blau, gelb);
		otherItem.setStrings(listOf(gelb, String.class));
		otherItem.assertStrings(gelb);

		assertContains(item, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(item, otherItem, getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));
	}

	@Test void testEmpty()
	{
		final Query<SetFieldItem> q = TYPE.newQuery(strings.getElement().isNull());
		q.joinOuterLeft(strings.getRelationType(), strings.getParent().equalTarget());

		assertContains(item, otherItem, q.search());
		assertEquals(2, q.total());

		item.addToStrings("itemS1");
		assertContains(otherItem, q.search());
		assertEquals(1, q.total());

		item.addToStrings("itemS2");
		assertContains(otherItem, q.search());
		assertEquals(1, q.total());

		otherItem.addToStrings("oItemS1");
		assertContains(q.search());
		assertEquals(0, q.total());

		otherItem.addToStrings("oItemS2");
		assertContains(q.search());
		assertEquals(0, q.total());
	}

	@Test void testListSetNull()
	{
		item.setStrings(listOf("1hallo", "2bello", String.class));

		assertFails(
				() -> item.setStrings(null),
				MandatoryViolationException.class,
				"mandatory violation on " + item + " for SetFieldItem.strings",
				strings, item);
		item.assertStrings("1hallo", "2bello");
	}
}

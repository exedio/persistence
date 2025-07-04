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

import static com.exedio.cope.pattern.SetOrderedFieldItem.TYPE;
import static com.exedio.cope.pattern.SetOrderedFieldItem.getParentsOfStrings;
import static com.exedio.cope.pattern.SetOrderedFieldItem.strings;
import static com.exedio.cope.pattern.SetOrderedFieldModelTest.MODEL;
import static com.exedio.cope.pattern.SetOrderedFieldModelTest.stringsElement;
import static com.exedio.cope.pattern.SetOrderedFieldModelTest.stringsType;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetOrderedFieldTest extends TestWithEnvironment
{
	public SetOrderedFieldTest()
	{
		super(MODEL);
	}

	SetOrderedFieldItem item;
	SetOrderedFieldItem otherItem;

	@BeforeEach final void setUp()
	{
		item = new SetOrderedFieldItem();
		otherItem = new SetOrderedFieldItem();
	}

	@Test void testQuery()
	{
		assertEquals(
				"select element " +
				"from SetOrderedFieldItem-strings " +
				"where parent='" + item + "' " +
				"order by order",
				item.getStringsQuery().toString());
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
		assertEquals("1hallo", stringsElement.get(r0));
		assertEquals("2bello", stringsElement.get(r1));

		item.setStrings(listOf("2bello", "3knollo", String.class));
		item.assertStrings("2bello", "3knollo");
		assertContains(getParentsOfStrings("1hallo"));
		assertContains(item, getParentsOfStrings("2bello"));
		assertContains(item, getParentsOfStrings("3knollo"));
		assertContains(getParentsOfStrings(null));
		final Item r2;
		final Item r3;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r2 = i.next();
			r3 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("3knollo", stringsElement.get(r3));
		assertEquals("2bello", stringsElement.get(r2));
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());

		item.setStrings(listOf("3knollo", String.class));
		item.assertStrings("3knollo");
		assertContains(getParentsOfStrings("1hallo"));
		assertContains(getParentsOfStrings("2bello"));
		assertContains(item, getParentsOfStrings("3knollo"));
		assertContains(getParentsOfStrings(null));
		final Item r4;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r4 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("3knollo", stringsElement.get(r4));
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());

		item.setStrings(listOf("zack1", "zack2", "zack3", String.class));
		item.assertStrings("zack1", "zack2", "zack3");
		final Item r5;
		final Item r6;
		final Item r7;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r5 = i.next();
			r6 = i.next();
			r7 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("zack1", stringsElement.get(r5));
		assertFalse(r4.existsCopeItem());
		assertEquals("zack2", stringsElement.get(r6));
		assertEquals("zack3", stringsElement.get(r7));

		item.setStrings(listOf("null1", "null2", "null3", "null4", String.class));
		item.assertStrings("null1", "null2", "null3", "null4");
		assertContains(item, getParentsOfStrings("null1"));
		assertContains(getParentsOfStrings(null));
		assertContains(item, getParentsOfStrings("null2"));
		final Item r8;
		final Item r9;
		final Item r10;
		final Item r11;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r8 = i.next();
			r9 = i.next();
			r10 = i.next();
			r11 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("null1", stringsElement.get(r8));
		assertEquals("null2", stringsElement.get(r9));
		assertEquals("null3", stringsElement.get(r10));
		assertEquals("null4", stringsElement.get(r11));
		assertFalse(r5.existsCopeItem());
		assertFalse(r6.existsCopeItem());
		assertFalse(r7.existsCopeItem());

		item.setStrings(listOf(String.class));
		item.assertStrings();
		assertFalse(r8.existsCopeItem());
		assertFalse(r9.existsCopeItem());
		assertFalse(r10.existsCopeItem());
		assertFalse(r11.existsCopeItem());
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
		assertEquals("bing", strings.getElement().get(r4));

		assertEquals(false, item.addToStrings("bing"));
		item.assertStrings("bing");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r4, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("bing", strings.getElement().get(r4));

		assertEquals(true, item.addToStrings("bong"));
		item.assertStrings("bing", "bong");
		final Item r5;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r4, i.next());
			r5 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("bing", strings.getElement().get(r4));
		assertEquals("bong", strings.getElement().get(r5));

		assertEquals(true, item.removeFromStrings("bing"));
		item.assertStrings("bong");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", strings.getElement().get(r5));

		assertEquals(false, item.removeFromStrings("bing"));
		item.assertStrings("bong");
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", strings.getElement().get(r5));

		assertEquals(true, item.removeFromStrings("bong"));
		item.assertStrings();
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertFalse(r5.existsCopeItem());
	}

	@Test void testMandatoryViolation()
	{
		assertFails(
				() -> item.setStrings(listOf("one1", null, "three3", String.class)),
				MandatoryViolationException.class,
				"mandatory violation for SetOrderedFieldItem-strings.element",
				strings.getElement());
		item.assertStrings();
	}

	@Test void testOtherViolation()
	{
		final StringLengthViolationException e = assertFails(
				() -> item.setStrings(listOf("one1", "two", "three3", String.class)),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for SetOrderedFieldItem-strings.element, " +
				"must be at least 4 characters, but was 3",
				strings.getElement());
		assertEquals("two", e.getValue());
		item.assertStrings();
	}

	@Test void testDuplicates()
	{
		item.setStrings(listOf("1one", "2dupl", "2dupl", "3two", String.class));
		item.assertStrings("1one", "2dupl", "3two");
	}

	@Test void testOrder()
	{
		item.setStrings(listOf("4four", "1one", "2two", String.class));
		item.assertStrings("4four", "1one", "2two");

		item.addToStrings("3three");
		item.assertStrings("4four", "1one", "2two", "3three");
	}

	@Test void testReorder()
	{
		item.setStrings(listOf("4four", "1one", "3three", "2two", String.class));
		item.assertStrings("4four", "1one", "3three", "2two");

		item.setStrings(listOf("4four", "3three", "1one", "2two", String.class));
		item.assertStrings("4four", "3three", "1one", "2two");
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
		item.assertStrings(rot, gelb, blau);
		otherItem.setStrings(listOf(gelb, String.class));
		otherItem.assertStrings(gelb);

		assertContains(item, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(item, otherItem, getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));
	}

	@Test void testEmpty()
	{
		final Query<SetOrderedFieldItem> q = TYPE.newQuery(strings.getElement().isNull());
		q.joinOuterLeft(strings.getEntryType(), strings.getParent().isTarget());

		assertContains(item, otherItem, q.search());
		assertEquals(2, q.total());
		assertTrue(q.exists());

		item.addToStrings("itemS1");
		assertContains(otherItem, q.search());
		assertEquals(1, q.total());
		assertTrue(q.exists());

		item.addToStrings("itemS2");
		assertContains(otherItem, q.search());
		assertEquals(1, q.total());
		assertTrue(q.exists());

		otherItem.addToStrings("oItemS1");
		assertContains(q.search());
		assertEquals(0, q.total());
		assertFalse(q.exists());

		otherItem.addToStrings("oItemS2");
		assertContains(q.search());
		assertEquals(0, q.total());
		assertFalse(q.exists());
	}
}

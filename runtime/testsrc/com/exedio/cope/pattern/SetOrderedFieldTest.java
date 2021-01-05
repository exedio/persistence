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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

	@SuppressFBWarnings({"NP_NONNULL_PARAM_VIOLATION","NP_NULL_PARAM_DEREF_NONVIRTUAL"})
	@Test void testSet()
	{
		item.assertStrings();
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(asList("1hallo", "2bello"));
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

		item.setStrings(asList("2bello", "3knollo"));
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
		assertEquals("3knollo", r3.get(stringsElement));
		assertEquals("2bello", r2.get(stringsElement));
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());

		item.setStrings(asList("3knollo"));
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
		assertEquals("3knollo", r4.get(stringsElement));
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());

		item.setStrings(asList("zack1", "zack2", "zack3"));
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
		assertEquals("zack1", r5.get(stringsElement));
		assertFalse(r4.existsCopeItem());
		assertEquals("zack2", r6.get(stringsElement));
		assertEquals("zack3", r7.get(stringsElement));

		item.setStrings(asList("null1", "null2", "null3", "null4"));
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
		assertEquals("null1", r8.get(stringsElement));
		assertEquals("null2", r9.get(stringsElement));
		assertEquals("null3", r10.get(stringsElement));
		assertEquals("null4", r11.get(stringsElement));
		assertFalse(r5.existsCopeItem());
		assertFalse(r6.existsCopeItem());
		assertFalse(r7.existsCopeItem());

		item.setStrings(asList());
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

	@Test void testMandatoryViolation()
	{
		assertFails(
				() -> item.setStrings(asList("one1", null, "three3")),
				MandatoryViolationException.class,
				"mandatory violation for SetOrderedFieldItem-strings.element",
				strings.getElement());
		item.assertStrings();
	}

	@Test void testOtherViolation()
	{
		final StringLengthViolationException e = assertFails(
				() -> item.setStrings(asList("one1", "two", "three3")),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for SetOrderedFieldItem-strings.element, " +
				"must be at least 4 characters, but was 3.",
				strings.getElement());
		assertEquals("two", e.getValue());
		item.assertStrings();
	}

	@Test void testDuplicates()
	{
		item.setStrings(asList("1one", "2dupl", "2dupl", "3two"));
		item.assertStrings("1one", "2dupl", "3two");
	}

	@Test void testOrder()
	{
		item.setStrings(asList("4four", "1one", "2two"));
		item.assertStrings("4four", "1one", "2two");

		item.addToStrings("3three");
		item.assertStrings("4four", "1one", "2two", "3three");
	}

	@Test void testReorder()
	{
		item.setStrings(asList("4four", "1one", "3three", "2two"));
		item.assertStrings("4four", "1one", "3three", "2two");

		item.setStrings(asList("4four", "3three", "1one", "2two"));
		item.assertStrings("4four", "3three", "1one", "2two");
	}

	@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_NONVIRTUAL", "NP_NONNULL_PARAM_VIOLATION"})
	@Test void testMultipleItems()
	{
		final String rot = "1hellrot";
		final String blau = "2blau";
		final String gelb = "3gelb";

		item.setStrings(asList(rot, blau));
		item.assertStrings(rot, blau);
		otherItem.setStrings(asList(rot));
		otherItem.assertStrings(rot);

		assertContains(item, otherItem, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));

		item.setStrings(asList(rot, gelb, blau));
		item.assertStrings(rot, gelb, blau);
		otherItem.setStrings(asList(gelb));
		otherItem.assertStrings(gelb);

		assertContains(item, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(item, otherItem, getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));
	}

	@Test void testEmpty()
	{
		final Query<SetOrderedFieldItem> q = TYPE.newQuery(strings.getElement().isNull());
		q.joinOuterLeft(strings.getRelationType(), strings.getParent().equalTarget());

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

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
import static com.exedio.cope.tojunit.Assert.assertContainsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;

public class SetFieldTest extends TestWithEnvironment
{
	public SetFieldTest()
	{
		super(MODEL);
	}

	SetFieldItem item;
	SetFieldItem otherItem;

	@Before public final void setUp()
	{
		item = new SetFieldItem();
		otherItem = new SetFieldItem();
	}

	@Test public void testQuery()
	{
		assertEquals("select element from SetFieldItem-strings" + " where parent='" + item + "'", item.getStringsQuery().toString());
		assertEquals("select element from SetFieldItem-dates"   + " where parent='" + item + "'", item.getDatesQuery  ().toString());
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testSet()
	{
		assertContainsUnmodifiable(item.getStrings());
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(asList("hallo", "bello"));
		assertContainsUnmodifiable("hallo", "bello", item.getStrings());
		assertContains(item, getParentsOfStrings("hallo"));
		assertContains(item, getParentsOfStrings("bello"));
		assertContains(getParentsOfStrings("knollo"));
		assertContains(getParentsOfStrings(null));
		final Item r0;
		final Item r1;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r0 = i.next();
			r1 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("hallo", r0.get(stringsElement));
		assertEquals("bello", r1.get(stringsElement));

		item.setStrings(asList("bello", "knollo"));
		assertContainsUnmodifiable("bello", "knollo", item.getStrings());
		assertContains(getParentsOfStrings("hallo"));
		assertContains(item, getParentsOfStrings("bello"));
		assertContains(item, getParentsOfStrings("knollo"));
		assertContains(getParentsOfStrings(null));
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("knollo", r0.get(stringsElement));
		assertEquals("bello", r1.get(stringsElement));

		item.setStrings(asList("knollo"));
		assertContainsUnmodifiable("knollo", item.getStrings());
		assertContains(getParentsOfStrings("hallo"));
		assertContains(getParentsOfStrings("bello"));
		assertContains(item, getParentsOfStrings("knollo"));
		assertContains(getParentsOfStrings(null));
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("knollo", r0.get(stringsElement));
		assertFalse(r1.existsCopeItem());

		item.setStrings(asList("zack1", "zack2", "zack3"));
		assertContainsUnmodifiable("zack1", "zack2", "zack3", item.getStrings());
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

		item.setStrings(asList("null1", "null2", "null3", "null4"));
		assertContainsUnmodifiable("null1", "null2", "null3", "null4", item.getStrings());
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

		item.setStrings(asList());
		assertContainsUnmodifiable(item.getStrings());
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r1x.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());
	}

	@Test public void testAddRemove()
	{
		assertEquals(true, item.addToStrings("bing"));
		assertContainsUnmodifiable("bing", item.getStrings());
		final Item r4;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			r4 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("bing", r4.get(strings.getElement()));

		assertEquals(false, item.addToStrings("bing"));
		assertContainsUnmodifiable("bing", item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r4, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("bing", r4.get(strings.getElement()));

		assertEquals(true, item.addToStrings("bong"));
		assertContainsUnmodifiable("bing", "bong", item.getStrings());
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
		assertContainsUnmodifiable("bong", item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", r5.get(strings.getElement()));

		assertEquals(false, item.removeFromStrings("bing"));
		assertContainsUnmodifiable("bong", item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r5, i.next());
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertEquals("bong", r5.get(strings.getElement()));

		assertEquals(true, item.removeFromStrings("bong"));
		assertContainsUnmodifiable(item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertFalse(i.hasNext());
		}
		assertFalse(r4.existsCopeItem());
		assertFalse(r5.existsCopeItem());
	}

	@Test public void testDate()
	{
		assertContainsUnmodifiable(item.getDates());
		assertEquals(0, datesType.newQuery(null).search().size());

		final Date date1 = new Date(918756915152l);
		final Date date2 = new Date(918756915153l);
		item.setDates(asList(date1, date2));
		assertContainsUnmodifiable(date1, date2, item.getDates());
		assertEquals(2, datesType.newQuery(null).search().size());
	}

	@Test public void testMandatoryViolation()
	{
		try
		{
			item.setStrings(asList("one1", null, "three3"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(strings.getElement(), e.getFeature());
		}
		assertContainsUnmodifiable("one1", item.getStrings()); // TODO should be empty
	}

	@Test public void testOtherViolation()
	{
		try
		{
			item.setStrings(asList("one1", "two", "three3"));
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(strings.getElement(), e.getFeature());
			assertEquals("two", e.getValue());
		}
		assertContainsUnmodifiable("one1", item.getStrings()); // TODO should be empty
	}

	@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_NONVIRTUAL", "NP_NONNULL_PARAM_VIOLATION"})
	@Test public void testMultipleItems() throws Exception
	{
		final String rot = "hellrot";
		final String blau = "blau";
		final String gelb = "gelb";

		item.setStrings(asList(rot, blau));
		assertContainsUnmodifiable(rot, blau, item.getStrings());
		otherItem.setStrings(asList(rot));
		assertContainsUnmodifiable(rot, otherItem.getStrings());

		assertContains(item, otherItem, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));

		item.setStrings(asList(rot, gelb, blau));
		assertContainsUnmodifiable(rot, blau, gelb, item.getStrings());
		otherItem.setStrings(asList(gelb));
		assertContainsUnmodifiable(gelb, otherItem.getStrings());

		assertContains(item, getParentsOfStrings(rot));
		assertContains(item, getParentsOfStrings(blau));
		assertContains(item, otherItem, getParentsOfStrings(gelb));
		assertContains(getParentsOfStrings(null));
	}

	@Test public void testEmpty() throws Exception
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

	@SuppressFBWarnings({"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS", "NP_NONNULL_PARAM_VIOLATION"})
	@Test public void testListSetNull()
	{
		item.setStrings(asList("hallo", "bello"));

		try
		{
			item.setStrings(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(strings, e.getFeature());
			assertEquals(item, e.getItem());
		}
		assertContainsUnmodifiable("hallo", "bello", item.getStrings());
	}
}

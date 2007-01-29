/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;

public class FieldSetTest extends AbstractLibTest
{
	static final Model MODEL = new Model(FieldSetItem.TYPE);
	
	public FieldSetTest()
	{
		super(MODEL);
	}

	FieldSetItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new FieldSetItem());
	}
	
	public void testIt()
	{
		final Type<?> stringsType = item.strings.getRelationType();
		final Type<?> datesType = item.dates.getRelationType();
		final FunctionField<String> stringsElement = item.strings.getElement();
		
		// test model
		assertEqualsUnmodifiable(list(
				item.TYPE,
				stringsType,
				datesType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				stringsType,
				datesType
			), model.getTypesSortedByHierarchy());
		assertEquals(FieldSetItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.strings,
				item.dates
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				stringsType.getThis(),
				item.strings.getParent(),
				item.strings.getElement(),
				item.strings.getUniqueConstraint()
			), stringsType.getFeatures());
		assertEqualsUnmodifiable(list(
				datesType.getThis(),
				item.dates.getParent(),
				item.dates.getElement(),
				item.dates.getUniqueConstraint()
			), datesType.getFeatures());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());

		assertEquals("FieldSetItem.strings", stringsType.getID());
		assertEquals(Item.class, stringsType.getJavaClass().getSuperclass());
		assertEquals(false, stringsType.hasUniqueJavaClass());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubTypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(model, stringsType.getModel());

		assertEquals("FieldSetItem.dates", datesType.getID());
		assertEquals(Item.class, datesType.getJavaClass().getSuperclass());
		assertEquals(false, datesType.hasUniqueJavaClass());
		assertEquals(null, datesType.getSupertype());
		assertEqualsUnmodifiable(list(), datesType.getSubTypes());
		assertEquals(false, datesType.isAbstract());
		assertEquals(Item.class, datesType.getThis().getValueClass().getSuperclass());
		assertEquals(datesType, datesType.getThis().getValueType());
		assertEquals(model, datesType.getModel());

		assertEquals(stringsType, item.strings.getParent().getType());
		assertEquals(stringsType, item.strings.getElement().getType());
		assertEquals(stringsType, item.strings.getUniqueConstraint().getType());
		assertEquals(datesType, item.dates.getParent().getType());
		assertEquals(datesType, item.dates.getElement().getType());
		assertEquals(datesType, item.dates.getUniqueConstraint().getType());

		assertEquals("parent", item.strings.getParent().getName());
		assertEquals("element", item.strings.getElement().getName());
		assertEquals("uniqueConstraint", item.strings.getUniqueConstraint().getName());
		assertEquals("parent", item.dates.getParent().getName());
		assertEquals("element", item.dates.getElement().getName());
		assertEquals("uniqueConstraint", item.dates.getUniqueConstraint().getName());

		assertEqualsUnmodifiable(list(item.strings.getParent(), item.strings.getElement()), item.strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(item.dates.getParent(), item.dates.getElement()), item.dates.getUniqueConstraint().getFields());

		assertTrue(stringsType.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(datesType));
		assertTrue(!item.TYPE.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(item.TYPE));

		try
		{
			FieldSet.newSet(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("element must not be null", e.getMessage());
		}
		try
		{
			FieldSet.newSet(new StringField(Item.FINAL));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be final", e.getMessage());
		}
		try
		{
			FieldSet.newSet(new StringField(Item.UNIQUE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be unique", e.getMessage());
		}
		
		// test persistence
		assertContains(item.getStrings());
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertContains("hallo", "bello", item.getStrings());
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

		item.setStrings(listg("bello", "knollo"));
		assertContains("bello", "knollo", item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("knollo", r0.get(stringsElement));
		assertEquals("bello", r1.get(stringsElement));

		item.setStrings(listg("knollo"));
		assertContains("knollo", item.getStrings());
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsType.getThis(), true).iterator();
			assertSame(r0, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("knollo", r0.get(stringsElement));
		assertFalse(r1.existsCopeItem());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertContains("zack1", "zack2", "zack3", item.getStrings());
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

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertContains("null1", null, "null3", "null4", item.getStrings());
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
		assertEquals(null, r1x.get(stringsElement));
		assertEquals("null3", r2.get(stringsElement));
		assertEquals("null4", r3.get(stringsElement));

		item.setStrings(CopeAssert.<String>listg());
		assertContains(item.getStrings());
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r1x.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());

		assertContains(item.getDates());
		assertEquals(0, datesType.newQuery(null).search().size());

		final Date date1 = new Date(918756915152l);
		final Date date2 = new Date(918756915153l);
		item.setDates(listg(date1, date2));
		assertContains(date1, date2, item.getDates());
		assertEquals(2, datesType.newQuery(null).search().size());

		try
		{
			item.setDates(listg(date1, null, date2));
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.dates.getElement(), e.getFeature());
		}
		assertContains(date1, date2, item.getDates());
		assertEquals(2, datesType.newQuery(null).search().size());
	}
	
}

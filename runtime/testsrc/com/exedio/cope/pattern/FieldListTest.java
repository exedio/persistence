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
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;

public class FieldListTest extends AbstractLibTest
{
	static final Model MODEL = new Model(FieldListItem.TYPE);
	
	static final Date date1 = new Date(918756915152l);
	static final Date date2 = new Date(918756915153l);
	
	public FieldListTest()
	{
		super(MODEL);
	}

	FieldListItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new FieldListItem());
	}
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(list(
				item.TYPE,
				item.strings.getRelationType(),
				item.dates.getRelationType(),
				item.items.getRelationType()
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				item.strings.getRelationType(),
				item.dates.getRelationType(),
				item.items.getRelationType()
			), model.getTypesSortedByHierarchy());
		assertEquals(FieldListItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.strings,
				item.dates,
				item.items
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				item.strings.getRelationType().getThis(),
				item.strings.getParent(),
				item.strings.getOrder(),
				item.strings.getUniqueConstraint(),
				item.strings.getElement()
			), item.strings.getRelationType().getFeatures());
		assertEqualsUnmodifiable(list(
				item.dates.getRelationType().getThis(),
				item.dates.getParent(),
				item.dates.getOrder(),
				item.dates.getUniqueConstraint(),
				item.dates.getElement()
			), item.dates.getRelationType().getFeatures());
		assertEqualsUnmodifiable(list(
				item.items.getRelationType().getThis(),
				item.items.getParent(),
				item.items.getOrder(),
				item.items.getUniqueConstraint(),
				item.items.getElement()
			), item.items.getRelationType().getFeatures());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());
		assertEquals(item.TYPE, item.items.getType());
		assertEquals("items", item.items.getName());

		assertEquals("FieldListItem.strings", item.strings.getRelationType().getID());
		assertEquals(Item.class, item.strings.getRelationType().getJavaClass().getSuperclass());
		assertEquals(false, item.strings.getRelationType().hasUniqueJavaClass());
		assertEquals(null, item.strings.getRelationType().getSupertype());
		assertEqualsUnmodifiable(list(), item.strings.getRelationType().getSubTypes());
		assertEquals(false, item.strings.getRelationType().isAbstract());
		assertEquals(Item.class, item.strings.getRelationType().getThis().getValueClass().getSuperclass());
		assertEquals(item.strings.getRelationType(), item.strings.getRelationType().getThis().getValueType());
		assertEquals(model, item.strings.getRelationType().getModel());

		assertEquals("FieldListItem.dates", item.dates.getRelationType().getID());
		assertEquals(Item.class, item.dates.getRelationType().getJavaClass().getSuperclass());
		assertEquals(false, item.dates.getRelationType().hasUniqueJavaClass());
		assertEquals(null, item.dates.getRelationType().getSupertype());
		assertEqualsUnmodifiable(list(), item.dates.getRelationType().getSubTypes());
		assertEquals(false, item.dates.getRelationType().isAbstract());
		assertEquals(Item.class, item.dates.getRelationType().getThis().getValueClass().getSuperclass());
		assertEquals(item.dates.getRelationType(), item.dates.getRelationType().getThis().getValueType());
		assertEquals(model, item.dates.getRelationType().getModel());

		assertEquals("FieldListItem.items", item.items.getRelationType().getID());
		assertEquals(Item.class, item.items.getRelationType().getJavaClass().getSuperclass());
		assertEquals(false, item.items.getRelationType().hasUniqueJavaClass());
		assertEquals(null, item.items.getRelationType().getSupertype());
		assertEqualsUnmodifiable(list(), item.items.getRelationType().getSubTypes());
		assertEquals(false, item.items.getRelationType().isAbstract());
		assertEquals(Item.class, item.items.getRelationType().getThis().getValueClass().getSuperclass());
		assertEquals(item.items.getRelationType(), item.items.getRelationType().getThis().getValueType());
		assertEquals(model, item.items.getRelationType().getModel());

		assertEquals(item.strings.getRelationType(), item.strings.getParent().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getOrder().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getUniqueConstraint().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getElement().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getParent().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getOrder().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getUniqueConstraint().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getElement().getType());
		assertEquals(item.items.getRelationType(), item.items.getParent().getType());
		assertEquals(item.items.getRelationType(), item.items.getOrder().getType());
		assertEquals(item.items.getRelationType(), item.items.getUniqueConstraint().getType());
		assertEquals(item.items.getRelationType(), item.items.getElement().getType());

		assertEquals("parent", item.strings.getParent().getName());
		assertEquals("order", item.strings.getOrder().getName());
		assertEquals("uniqueConstraint", item.strings.getUniqueConstraint().getName());
		assertEquals("element", item.strings.getElement().getName());
		assertEquals("parent", item.dates.getParent().getName());
		assertEquals("order", item.dates.getOrder().getName());
		assertEquals("uniqueConstraint", item.dates.getUniqueConstraint().getName());
		assertEquals("element", item.dates.getElement().getName());
		assertEquals("parent", item.items.getParent().getName());
		assertEquals("order", item.items.getOrder().getName());
		assertEquals("uniqueConstraint", item.items.getUniqueConstraint().getName());
		assertEquals("element", item.items.getElement().getName());

		assertEqualsUnmodifiable(list(item.strings.getParent(), item.strings.getOrder()), item.strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(item.dates.getParent(), item.dates.getOrder()), item.dates.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(item.items.getParent(), item.items.getOrder()), item.items.getUniqueConstraint().getFields());

		assertTrue(item.strings.getRelationType().isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.dates.getRelationType()));
		assertTrue(!item.TYPE.isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.TYPE));
		
		try
		{
			FieldList.newList(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("element must not be null", e.getMessage());
		}
		try
		{
			FieldList.newList(new StringField(Item.FINAL));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be final", e.getMessage());
		}
		try
		{
			FieldList.newList(new StringField(Item.UNIQUE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be unique", e.getMessage());
		}

		// test persistence
		// test searching
		final Query<FieldListItem> q = item.TYPE.newQuery();
		q.join(item.strings.getRelationType(), item.strings.getParent().equalTarget());
		assertEquals(list(), q.search());
		
		q.setCondition(item.strings.getElement().equal("zack"));
		assertEquals(list(), q.search());
		
		q.setCondition(item.dates.getElement().equal(new Date()));
		try
		{
			q.search();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("FieldListItem.dates.element does not belong to a type of the query: " + q, e.getMessage());
		}
		try
		{
			q.countWithoutLimit();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("FieldListItem.dates.element does not belong to a type of the query: " + q.toString(), e.getMessage());
		}

		// strings
		assertEquals(list(), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item.getDistinctParentsOfStrings("hallo"));
		assertContains(item.getDistinctParentsOfStrings("bello"));
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("hallo"));
		assertContains(item, item.getDistinctParentsOfStrings("bello"));
		assertContains(item.getDistinctParentsOfStrings("zack1"));
		final Item r0;
		final Item r1;
		{
			final Iterator<? extends Item> i = item.strings.getRelationType().search(null, item.strings.getOrder(), true).iterator();
			r0 = i.next();
			r1 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("hallo", r0.get(item.strings.getElement()));
		assertEquals("bello", r1.get(item.strings.getElement()));
		assertEquals(0, r0.get(item.strings.getOrder()).intValue());
		assertEquals(1, r1.get(item.strings.getOrder()).intValue());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertEquals(list("zack1", "zack2", "zack3"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("zack1"));
		assertContains(item, item.getDistinctParentsOfStrings("zack2"));
		assertContains(item, item.getDistinctParentsOfStrings("zack3"));
		assertContains(item.getDistinctParentsOfStrings("zackx"));
		final Item r2;
		{
			final Iterator<? extends Item> i = item.strings.getRelationType().search(null, item.strings.getOrder(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			r2 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("zack1", r0.get(item.strings.getElement()));
		assertEquals("zack2", r1.get(item.strings.getElement()));
		assertEquals("zack3", r2.get(item.strings.getElement()));
		assertEquals(0, r0.get(item.strings.getOrder()).intValue());
		assertEquals(1, r1.get(item.strings.getOrder()).intValue());
		assertEquals(2, r2.get(item.strings.getOrder()).intValue());

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertEquals(list("null1", null, "null3", "null4"), item.getStrings());
		assertContains(item, item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("null1"));
		assertContains(      item.getDistinctParentsOfStrings("null2"));
		assertContains(item, item.getDistinctParentsOfStrings("null3"));
		assertContains(item, item.getDistinctParentsOfStrings("null4"));
		final Item r3;
		{
			final Iterator<? extends Item> i = item.strings.getRelationType().search(null, item.strings.getOrder(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertSame(r2, i.next());
			r3 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("null1", r0.get(item.strings.getElement()));
		assertEquals(null, r1.get(item.strings.getElement()));
		assertEquals("null3", r2.get(item.strings.getElement()));
		assertEquals("null4", r3.get(item.strings.getElement()));
		assertEquals(0, r0.get(item.strings.getOrder()).intValue());
		assertEquals(1, r1.get(item.strings.getOrder()).intValue());
		assertEquals(2, r2.get(item.strings.getOrder()).intValue());
		assertEquals(3, r3.get(item.strings.getOrder()).intValue());

		item.setStrings(listg("dup1", "dup2", "dup1"));
		assertEquals(list("dup1", "dup2", "dup1"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("dup1"));
		assertContains(item, item.getDistinctParentsOfStrings("dup2"));
		assertContains(      item.getDistinctParentsOfStrings("dup3"));
		{
			final Iterator<? extends Item> i = item.strings.getRelationType().search(null, item.strings.getOrder(), true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertSame(r2, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("dup1", r0.get(item.strings.getElement()));
		assertEquals("dup2", r1.get(item.strings.getElement()));
		assertEquals("dup1", r2.get(item.strings.getElement()));
		assertEquals(0, r0.get(item.strings.getOrder()).intValue());
		assertEquals(1, r1.get(item.strings.getOrder()).intValue());
		assertEquals(2, r2.get(item.strings.getOrder()).intValue());
		assertFalse(r3.existsCopeItem());

		item.setStrings(CopeAssert.<String>listg());
		assertEquals(list(), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item.getDistinctParentsOfStrings("null1"));
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());

		// dates
		assertEquals(list(), item.getDates());
		assertContains(item.getDistinctParentsOfDates(date1));
		assertContains(item.getDistinctParentsOfDates(date2));
		assertEquals(0, item.dates.getRelationType().newQuery(null).search().size());

		item.setDates(listg(date1, date2));
		assertEquals(list(date1, date2), item.getDates());
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item, item.getDistinctParentsOfDates(date2));
		assertEquals(2, item.dates.getRelationType().newQuery(null).search().size());

		try
		{
			item.setDates(listg(date1, null, date2));
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.dates.getElement(), e.getFeature());
		}
		assertEquals(list(date1, date2), item.getDates());
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item, item.getDistinctParentsOfDates(date2));
		assertEquals(2, item.dates.getRelationType().newQuery(null).search().size());
		
		// items
		assertEquals(list(), item.getItems());
		assertContains(item.getDistinctParentsOfItems(null));
		assertContains(item.getDistinctParentsOfItems(item));
		assertEquals(0, item.items.getRelationType().newQuery(null).search().size());

		item.setItems(listg(item));
		assertEquals(list(item), item.getItems());
		assertContains(item.getDistinctParentsOfItems(null));
		assertContains(item, item.getDistinctParentsOfItems(item));
		assertEquals(1, item.items.getRelationType().newQuery(null).search().size());
	}
	
}

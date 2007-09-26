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
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;

public class FieldListTest extends AbstractLibTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(FieldListItem.TYPE);
	
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
		final Type<?> stringsType = item.strings.getRelationType();
		final Type<?> datesType = item.dates.getRelationType();
		final Type<?> itemsType = item.items.getRelationType();
		final IntegerField stringsOrder = item.strings.getOrder();
		final FunctionField<String> stringsElement = item.strings.getElement();
		
		// test model
		assertEqualsUnmodifiable(list(
				item.TYPE,
				stringsType,
				datesType,
				itemsType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				stringsType,
				datesType,
				itemsType
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
				stringsType.getThis(),
				item.stringsParent(),
				stringsOrder,
				item.strings.getUniqueConstraint(),
				stringsElement
			), stringsType.getFeatures());
		assertEqualsUnmodifiable(list(
				datesType.getThis(),
				item.datesParent(),
				item.dates.getOrder(),
				item.dates.getUniqueConstraint(),
				item.dates.getElement()
			), datesType.getFeatures());
		assertEqualsUnmodifiable(list(
				itemsType.getThis(),
				item.itemsParent(),
				item.items.getOrder(),
				item.items.getUniqueConstraint(),
				item.items.getElement()
			), itemsType.getFeatures());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());
		assertEquals(item.TYPE, item.items.getType());
		assertEquals("items", item.items.getName());

		assertEquals("FieldListItem.strings", stringsType.getID());
		assertEquals(Item.class, stringsType.getJavaClass().getSuperclass());
		assertEquals(false, stringsType.hasUniqueJavaClass());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubTypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(model, stringsType.getModel());

		assertEquals("FieldListItem.dates", datesType.getID());
		assertEquals(Item.class, datesType.getJavaClass().getSuperclass());
		assertEquals(false, datesType.hasUniqueJavaClass());
		assertEquals(null, datesType.getSupertype());
		assertEqualsUnmodifiable(list(), datesType.getSubTypes());
		assertEquals(false, datesType.isAbstract());
		assertEquals(Item.class, datesType.getThis().getValueClass().getSuperclass());
		assertEquals(datesType, datesType.getThis().getValueType());
		assertEquals(model, datesType.getModel());

		assertEquals("FieldListItem.items", itemsType.getID());
		assertEquals(Item.class, itemsType.getJavaClass().getSuperclass());
		assertEquals(false, itemsType.hasUniqueJavaClass());
		assertEquals(null, itemsType.getSupertype());
		assertEqualsUnmodifiable(list(), itemsType.getSubTypes());
		assertEquals(false, itemsType.isAbstract());
		assertEquals(Item.class, itemsType.getThis().getValueClass().getSuperclass());
		assertEquals(itemsType, itemsType.getThis().getValueType());
		assertEquals(model, itemsType.getModel());

		assertEquals(stringsType, item.stringsParent().getType());
		assertEquals(stringsType, stringsOrder.getType());
		assertEquals(stringsType, item.strings.getUniqueConstraint().getType());
		assertEquals(stringsType, stringsElement.getType());
		assertEquals(datesType, item.datesParent().getType());
		assertEquals(datesType, item.dates.getOrder().getType());
		assertEquals(datesType, item.dates.getUniqueConstraint().getType());
		assertEquals(datesType, item.dates.getElement().getType());
		assertEquals(itemsType, item.itemsParent().getType());
		assertEquals(itemsType, item.items.getOrder().getType());
		assertEquals(itemsType, item.items.getUniqueConstraint().getType());
		assertEquals(itemsType, item.items.getElement().getType());

		assertEquals("parent", item.stringsParent().getName());
		assertEquals("order", stringsOrder.getName());
		assertEquals("uniqueConstraint", item.strings.getUniqueConstraint().getName());
		assertEquals("element", stringsElement.getName());
		assertEquals("parent", item.datesParent().getName());
		assertEquals("order", item.dates.getOrder().getName());
		assertEquals("uniqueConstraint", item.dates.getUniqueConstraint().getName());
		assertEquals("element", item.dates.getElement().getName());
		assertEquals("parent", item.itemsParent().getName());
		assertEquals("order", item.items.getOrder().getName());
		assertEquals("uniqueConstraint", item.items.getUniqueConstraint().getName());
		assertEquals("element", item.items.getElement().getName());

		assertEqualsUnmodifiable(list(item.stringsParent(), stringsOrder), item.strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(item.datesParent(), item.dates.getOrder()), item.dates.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(item.itemsParent(), item.items.getOrder()), item.items.getUniqueConstraint().getFields());

		assertTrue(stringsType.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(datesType));
		assertTrue(!item.TYPE.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(item.TYPE));
		
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
			FieldList.newList(new StringField().toFinal());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be final", e.getMessage());
		}
		try
		{
			FieldList.newList(new StringField().unique());
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("element must not be unique", e.getMessage());
		}

		// test persistence
		// test searching
		final Query<FieldListItem> q = item.TYPE.newQuery();
		q.join(stringsType, item.stringsParent().equalTarget());
		assertEquals(list(), q.search());
		
		q.setCondition(stringsElement.equal("zack"));
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
			q.total();
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
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("hallo"));
		assertContains(item, item.getDistinctParentsOfStrings("bello"));
		assertContains(item.getDistinctParentsOfStrings("zack1"));
		final Item r0;
		final Item r1;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			r0 = i.next();
			r1 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("hallo", r0.get(stringsElement));
		assertEquals("bello", r1.get(stringsElement));
		assertEquals(0, r0.get(stringsOrder).intValue());
		assertEquals(1, r1.get(stringsOrder).intValue());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertEquals(list("zack1", "zack2", "zack3"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("zack1"));
		assertContains(item, item.getDistinctParentsOfStrings("zack2"));
		assertContains(item, item.getDistinctParentsOfStrings("zack3"));
		assertContains(item.getDistinctParentsOfStrings("zackx"));
		final Item r2;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			r2 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("zack1", r0.get(stringsElement));
		assertEquals("zack2", r1.get(stringsElement));
		assertEquals("zack3", r2.get(stringsElement));
		assertEquals(0, r0.get(stringsOrder).intValue());
		assertEquals(1, r1.get(stringsOrder).intValue());
		assertEquals(2, r2.get(stringsOrder).intValue());

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertEquals(list("null1", null, "null3", "null4"), item.getStrings());
		assertContains(item, item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("null1"));
		assertContains(      item.getDistinctParentsOfStrings("null2"));
		assertContains(item, item.getDistinctParentsOfStrings("null3"));
		assertContains(item, item.getDistinctParentsOfStrings("null4"));
		final Item r3;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertSame(r2, i.next());
			r3 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("null1", r0.get(stringsElement));
		assertEquals(null, r1.get(stringsElement));
		assertEquals("null3", r2.get(stringsElement));
		assertEquals("null4", r3.get(stringsElement));
		assertEquals(0, r0.get(stringsOrder).intValue());
		assertEquals(1, r1.get(stringsOrder).intValue());
		assertEquals(2, r2.get(stringsOrder).intValue());
		assertEquals(3, r3.get(stringsOrder).intValue());

		item.setStrings(listg("dup1", "dup2", "dup1"));
		assertEquals(list("dup1", "dup2", "dup1"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("dup1"));
		assertContains(item, item.getDistinctParentsOfStrings("dup2"));
		assertContains(      item.getDistinctParentsOfStrings("dup3"));
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertSame(r2, i.next());
			assertFalse(i.hasNext());
		}
		assertEquals("dup1", r0.get(stringsElement));
		assertEquals("dup2", r1.get(stringsElement));
		assertEquals("dup1", r2.get(stringsElement));
		assertEquals(0, r0.get(stringsOrder).intValue());
		assertEquals(1, r1.get(stringsOrder).intValue());
		assertEquals(2, r2.get(stringsOrder).intValue());
		assertFalse(r3.existsCopeItem());

		item.setStrings(CopeAssert.<String>listg());
		assertEquals(list(), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item.getDistinctParentsOfStrings("null1"));
		assertEquals(0, stringsType.newQuery(null).search().size());
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());

		// dates
		assertEquals(list(), item.getDates());
		assertContains(item.getDistinctParentsOfDates(date1));
		assertContains(item.getDistinctParentsOfDates(date2));
		assertEquals(0, datesType.newQuery(null).search().size());

		item.setDates(listg(date1, date2));
		assertEquals(list(date1, date2), item.getDates());
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item, item.getDistinctParentsOfDates(date2));
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
		assertEquals(list(date1, date2), item.getDates());
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item, item.getDistinctParentsOfDates(date2));
		assertEquals(2, datesType.newQuery(null).search().size());
		
		// items
		assertEquals(list(), item.getItems());
		assertContains(item.getDistinctParentsOfItems(null));
		assertContains(item.getDistinctParentsOfItems(item));
		assertEquals(0, itemsType.newQuery(null).search().size());

		item.setItems(listg(item));
		assertEquals(list(item), item.getItems());
		assertContains(item.getDistinctParentsOfItems(null));
		assertContains(item, item.getDistinctParentsOfItems(item));
		assertEquals(1, itemsType.newQuery(null).search().size());

		try
		{
			item.strings.getParent(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
		try
		{
			item.dates.getParent(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
		try
		{
			item.items.getParent(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
		try
		{
			item.strings.getDistinctParents("hallo", Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
		try
		{
			item.dates.getDistinctParents(new Date(), Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
		try
		{
			item.items.getDistinctParents(item, Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("parent class must be " + item.getClass().getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
	}
}

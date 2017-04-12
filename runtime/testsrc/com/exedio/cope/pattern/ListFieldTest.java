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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.ListFieldItem.TYPE;
import static com.exedio.cope.pattern.ListFieldItem.dates;
import static com.exedio.cope.pattern.ListFieldItem.datesParent;
import static com.exedio.cope.pattern.ListFieldItem.getDistinctParentsOfDates;
import static com.exedio.cope.pattern.ListFieldItem.getDistinctParentsOfItems;
import static com.exedio.cope.pattern.ListFieldItem.getDistinctParentsOfStrings;
import static com.exedio.cope.pattern.ListFieldItem.items;
import static com.exedio.cope.pattern.ListFieldItem.itemsParent;
import static com.exedio.cope.pattern.ListFieldItem.strings;
import static com.exedio.cope.pattern.ListFieldItem.stringsParent;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;

public class ListFieldTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ListFieldTest.class, "MODEL");
	}

	static final Date date1 = new Date(918756915152l);
	static final Date date2 = new Date(918756915153l);

	public ListFieldTest()
	{
		super(MODEL);
	}

	ListFieldItem item;

	@Before public final void setUp()
	{
		item = new ListFieldItem();
	}

	@Test public void testIt()
	{
		final Type<?> stringsType = strings.getRelationType();
		final Type<?> datesType = dates.getRelationType();
		final Type<?> itemsType = items.getRelationType();
		final IntegerField stringsOrder = strings.getOrder();
		final FunctionField<String> stringsElement = strings.getElement();

		// test model
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType,
				itemsType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType,
				itemsType
			), model.getTypesSortedByHierarchy());
		assertEquals(ListFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				strings,
				dates,
				items
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				stringsType.getThis(),
				stringsParent(),
				stringsOrder,
				strings.getUniqueConstraint(),
				stringsElement
			), stringsType.getFeatures());
		assertEqualsUnmodifiable(list(
				datesType.getThis(),
				datesParent(),
				dates.getOrder(),
				dates.getUniqueConstraint(),
				dates.getElement()
			), datesType.getFeatures());
		assertEqualsUnmodifiable(list(
				itemsType.getThis(),
				itemsParent(),
				items.getOrder(),
				items.getUniqueConstraint(),
				items.getElement()
			), itemsType.getFeatures());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());
		assertEquals(TYPE, dates.getType());
		assertEquals("dates", dates.getName());
		assertEquals(TYPE, items.getType());
		assertEquals("items", items.getName());

		assertEquals("ListFieldItem-strings", stringsType.getID());
		assertEquals(PatternItem.class, stringsType.getJavaClass());
		assertEquals(false, stringsType.isBound());
		assertSame(strings, stringsType.getPattern());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubtypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(model, stringsType.getModel());

		assertEquals(0                , stringsOrder.getMinimum());
		assertEquals(Integer.MAX_VALUE, stringsOrder.getMaximum());

		assertEquals("ListFieldItem-dates", datesType.getID());
		assertEquals(PatternItem.class, datesType.getJavaClass());
		assertEquals(false, datesType.isBound());
		assertSame(dates, datesType.getPattern());
		assertEquals(null, datesType.getSupertype());
		assertEqualsUnmodifiable(list(), datesType.getSubtypes());
		assertEquals(false, datesType.isAbstract());
		assertEquals(Item.class, datesType.getThis().getValueClass().getSuperclass());
		assertEquals(datesType, datesType.getThis().getValueType());
		assertEquals(model, datesType.getModel());

		assertEquals("ListFieldItem-items", itemsType.getID());
		assertEquals(PatternItem.class, itemsType.getJavaClass());
		assertEquals(false, itemsType.isBound());
		assertSame(items, itemsType.getPattern());
		assertEquals(null, itemsType.getSupertype());
		assertEqualsUnmodifiable(list(), itemsType.getSubtypes());
		assertEquals(false, itemsType.isAbstract());
		assertEquals(Item.class, itemsType.getThis().getValueClass().getSuperclass());
		assertEquals(itemsType, itemsType.getThis().getValueType());
		assertEquals(model, itemsType.getModel());

		assertEquals(stringsType, stringsParent().getType());
		assertEquals(stringsType, stringsOrder.getType());
		assertEquals(stringsType, strings.getUniqueConstraint().getType());
		assertEquals(stringsType, stringsElement.getType());
		assertEquals(datesType, datesParent().getType());
		assertEquals(datesType, dates.getOrder().getType());
		assertEquals(datesType, dates.getUniqueConstraint().getType());
		assertEquals(datesType, dates.getElement().getType());
		assertEquals(itemsType, itemsParent().getType());
		assertEquals(itemsType, items.getOrder().getType());
		assertEquals(itemsType, items.getUniqueConstraint().getType());
		assertEquals(itemsType, items.getElement().getType());
		assertSame(stringsParent(), strings.getParent());
		assertSame(datesParent(), dates.getParent());
		assertSame(itemsParent(), items.getParent());

		assertEquals("parent", stringsParent().getName());
		assertEquals("order", stringsOrder.getName());
		assertEquals("uniqueConstraint", strings.getUniqueConstraint().getName());
		assertEquals("element", stringsElement.getName());
		assertEquals("parent", datesParent().getName());
		assertEquals("order", dates.getOrder().getName());
		assertEquals("uniqueConstraint", dates.getUniqueConstraint().getName());
		assertEquals("element", dates.getElement().getName());
		assertEquals("parent", itemsParent().getName());
		assertEquals("order", items.getOrder().getName());
		assertEquals("uniqueConstraint", items.getUniqueConstraint().getName());
		assertEquals("element", items.getElement().getName());

		assertEqualsUnmodifiable(list(stringsParent(), stringsOrder), strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(datesParent(), dates.getOrder()), dates.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(itemsParent(), items.getOrder()), items.getUniqueConstraint().getFields());

		assertTrue(stringsType.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(datesType));
		assertTrue(!TYPE.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(TYPE));

		assertEquals(Integer.MAX_VALUE, strings.getMaximumSize());
		assertEquals(Integer.MAX_VALUE, dates  .getMaximumSize());
		assertEquals(Integer.MAX_VALUE, items  .getMaximumSize());

		assertTrue(stringsType.isAnnotationPresent(Computed.class));
		assertTrue(  datesType.isAnnotationPresent(Computed.class));
		assertTrue(  itemsType.isAnnotationPresent(Computed.class));

		assertSerializedSame(strings, 383);
		assertSerializedSame(dates  , 381);
		assertSerializedSame(items  , 381);

		try
		{
			ListField.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
		try
		{
			ListField.create(new StringField().toFinal());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("element must not be final", e.getMessage());
		}
		try
		{
			ListField.create(new StringField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("element must not be unique", e.getMessage());
		}

		// test persistence
		// test searching
		assertEquals("select element from ListFieldItem-strings" + " where parent='" + item + "' order by order", item.getStringsQuery().toString());
		assertEquals("select element from ListFieldItem-dates"   + " where parent='" + item + "' order by order", item.getDatesQuery  ().toString());
		assertEquals("select element from ListFieldItem-items"   + " where parent='" + item + "' order by order", item.getItemsQuery  ().toString());

		final Query<ListFieldItem> q = TYPE.newQuery();
		q.join(stringsType, stringsParent().equalTarget());
		assertEquals(list(), q.search());

		q.setCondition(stringsElement.equal("zack"));
		assertEquals(list(), q.search());

		q.setCondition(dates.getElement().equal(new Date()));
		try
		{
			q.search();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("ListFieldItem-dates.element does not belong to a type of the query: " + q, e.getMessage());
		}
		try
		{
			q.total();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("ListFieldItem-dates.element does not belong to a type of the query: " + q.toString(), e.getMessage());
		}

		// strings
		assertEqualsUnmodifiable(list(), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(getDistinctParentsOfStrings("hallo"));
		assertContains(getDistinctParentsOfStrings("bello"));
		assertEquals(0, stringsType.newQuery(null).search().size());

		item.setStrings(unmodifiableList(asList("hallo", "bello")));
		assertEqualsUnmodifiable(list("hallo", "bello"), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(item, getDistinctParentsOfStrings("hallo"));
		assertContains(item, getDistinctParentsOfStrings("bello"));
		assertContains(getDistinctParentsOfStrings("zack1"));
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

		item.setStrings(unmodifiableList(asList("zack1", "zack2", "zack3")));
		assertEqualsUnmodifiable(list("zack1", "zack2", "zack3"), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(item, getDistinctParentsOfStrings("zack1"));
		assertContains(item, getDistinctParentsOfStrings("zack2"));
		assertContains(item, getDistinctParentsOfStrings("zack3"));
		assertContains(getDistinctParentsOfStrings("zackx"));
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

		item.setStrings(unmodifiableList(asList("null1", null, "null3", "null4")));
		assertEqualsUnmodifiable(list("null1", null, "null3", "null4"), item.getStrings());
		assertContains(item, getDistinctParentsOfStrings(null));
		assertContains(item, getDistinctParentsOfStrings("null1"));
		assertContains(      getDistinctParentsOfStrings("null2"));
		assertContains(item, getDistinctParentsOfStrings("null3"));
		assertContains(item, getDistinctParentsOfStrings("null4"));
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

		item.setStrings(unmodifiableList(asList("dup1", "dup2", "dup1")));
		assertEqualsUnmodifiable(list("dup1", "dup2", "dup1"), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(item, getDistinctParentsOfStrings("dup1"));
		assertContains(item, getDistinctParentsOfStrings("dup2"));
		assertContains(      getDistinctParentsOfStrings("dup3"));
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

		item.addToStrings("dup3");
		assertEqualsUnmodifiable(list("dup1", "dup2", "dup1", "dup3"), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(item, getDistinctParentsOfStrings("dup1"));
		assertContains(item, getDistinctParentsOfStrings("dup2"));
		assertContains(item, getDistinctParentsOfStrings("dup3"));
		final Item r4;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			assertSame(r0, i.next());
			assertSame(r1, i.next());
			assertSame(r2, i.next());
			r4 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("dup1", r0.get(stringsElement));
		assertEquals("dup2", r1.get(stringsElement));
		assertEquals("dup1", r2.get(stringsElement));
		assertEquals("dup3", r4.get(stringsElement));
		assertEquals(0, r0.get(stringsOrder).intValue());
		assertEquals(1, r1.get(stringsOrder).intValue());
		assertEquals(2, r2.get(stringsOrder).intValue());
		assertEquals(3, r4.get(stringsOrder).intValue());
		assertFalse(r3.existsCopeItem());

		item.setStrings(unmodifiableList(asList()));
		assertEqualsUnmodifiable(list(), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(getDistinctParentsOfStrings("null1"));
		assertEquals(0, stringsType.newQuery(null).search().size());
		assertFalse(r0.existsCopeItem());
		assertFalse(r1.existsCopeItem());
		assertFalse(r2.existsCopeItem());
		assertFalse(r3.existsCopeItem());
		assertFalse(r4.existsCopeItem());

		item.addToStrings("dup4");
		assertEqualsUnmodifiable(list("dup4"), item.getStrings());
		assertContains(getDistinctParentsOfStrings(null));
		assertContains(      getDistinctParentsOfStrings("dup1"));
		assertContains(item, getDistinctParentsOfStrings("dup4"));
		final Item r5;
		{
			final Iterator<? extends Item> i = stringsType.search(null, stringsOrder, true).iterator();
			r5 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("dup4", r5.get(stringsElement));
		assertEquals(0, r5.get(stringsOrder).intValue());

		// dates
		assertEqualsUnmodifiable(list(), item.getDates());
		assertContains(getDistinctParentsOfDates(date1));
		assertContains(getDistinctParentsOfDates(date2));
		assertEquals(0, datesType.newQuery(null).search().size());

		item.setDates(asList(date1, date2));
		assertEqualsUnmodifiable(list(date1, date2), item.getDates());
		assertContains(item, getDistinctParentsOfDates(date1));
		assertContains(item, getDistinctParentsOfDates(date2));
		assertEquals(2, datesType.newQuery(null).search().size());

		try
		{
			item.setDates(asList(date1, null, date2));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(dates.getElement(), e.getFeature());
		}
		assertEqualsUnmodifiable(list(date1, date2), item.getDates());
		assertContains(item, getDistinctParentsOfDates(date1));
		assertContains(item, getDistinctParentsOfDates(date2));
		assertEquals(2, datesType.newQuery(null).search().size());

		// items
		assertEqualsUnmodifiable(list(), item.getItems());
		assertContains(getDistinctParentsOfItems(null));
		assertContains(getDistinctParentsOfItems(item));
		assertEquals(0, itemsType.newQuery(null).search().size());

		item.setItems(asList(item));
		assertEqualsUnmodifiable(list(item), item.getItems());
		assertContains(getDistinctParentsOfItems(null));
		assertContains(item, getDistinctParentsOfItems(item));
		assertEquals(1, itemsType.newQuery(null).search().size());

		try
		{
			strings.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
		try
		{
			dates.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
		try
		{
			items.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
		try
		{
			strings.getDistinctParents(Item.class, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
		try
		{
			dates.getDistinctParents(Item.class, new Date());
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
		try
		{
			items.getDistinctParents(Item.class, item);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
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
		assertEquals(asList("hallo", "bello"), item.getStrings());
	}
}

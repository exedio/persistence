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
import static com.exedio.cope.pattern.ListFieldItem.itemsSameValue;
import static com.exedio.cope.pattern.ListFieldItem.itemsSameValueParent;
import static com.exedio.cope.pattern.ListFieldItem.strings;
import static com.exedio.cope.pattern.ListFieldItem.stringsParent;
import static com.exedio.cope.pattern.ListFieldItem.value;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListFieldTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	private static final CopyConstraint copyParent = (CopyConstraint)itemsSameValue.getRelationType().getFeature("valueCopyFromparent");
	private static final CopyConstraint copyElement = (CopyConstraint)itemsSameValue.getRelationType().getFeature("valueCopyFromelement");

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

	@BeforeEach final void setUp()
	{
		item = new ListFieldItem();
	}

	@Test void testIt()
	{
		final Type<?> stringsType = strings.getRelationType();
		final Type<?> datesType = dates.getRelationType();
		final Type<?> itemsType = items.getRelationType();
		final Type<?> itemsSameValueType = itemsSameValue.getRelationType();
		final IntegerField stringsOrder = strings.getOrder();
		final FunctionField<String> stringsElement = strings.getElement();

		// test model
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType,
				itemsType,
				itemsSameValueType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType,
				itemsType,
				itemsSameValueType
			), model.getTypesSortedByHierarchy());
		assertEquals(ListFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				value,
				strings,
				dates,
				items,
				itemsSameValue
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
		assertEqualsUnmodifiable(list(
				itemsSameValueType.getThis(),
				itemsSameValueParent(),
				itemsSameValue.getOrder(),
				itemsSameValue.getUniqueConstraint(),
				itemsSameValue.getElement(),
				itemsSameValue.getCopyWithCopyField(value),
				copyParent,
				copyElement
			), itemsSameValueType.getFeatures());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());
		assertEquals(TYPE, dates.getType());
		assertEquals("dates", dates.getName());
		assertEquals(TYPE, items.getType());
		assertEquals("items", items.getName());
		assertEquals(TYPE, itemsSameValue.getType());
		assertEquals("itemsSameValue", itemsSameValue.getName());

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

		assertEquals("ListFieldItem-itemsSameValue", itemsSameValueType.getID());
		assertEquals(PatternItem.class, itemsSameValueType.getJavaClass());
		assertEquals(false, itemsSameValueType.isBound());
		assertSame(itemsSameValue, itemsSameValueType.getPattern());
		assertEquals(null, itemsSameValueType.getSupertype());
		assertEqualsUnmodifiable(list(), itemsSameValueType.getSubtypes());
		assertEquals(false, itemsSameValueType.isAbstract());
		assertEquals(Item.class, itemsSameValueType.getThis().getValueClass().getSuperclass());
		assertEquals(itemsSameValueType, itemsSameValueType.getThis().getValueType());
		assertEquals(model, itemsSameValueType.getModel());
		assertEquals(itemsSameValueType, itemsSameValue.getCopyWithCopyField(value).getType());

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
		assertEquals("parent", itemsSameValueParent().getName());
		assertEquals("order", itemsSameValue.getOrder().getName());
		assertEquals("uniqueConstraint", itemsSameValue.getUniqueConstraint().getName());
		assertEquals("element", itemsSameValue.getElement().getName());
		assertEquals("value", itemsSameValue.getCopyWithCopyField(value).getName());

		assertEqualsUnmodifiable(list(stringsParent(), stringsOrder), strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(datesParent(), dates.getOrder()), dates.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(itemsParent(), items.getOrder()), items.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(itemsSameValueParent(), itemsSameValue.getOrder()), itemsSameValue.getUniqueConstraint().getFields());

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
		assertSerializedSame(itemsSameValue, 390);

		assertFails(
				() -> ListField.create(null),
				NullPointerException.class,
				"element");
		assertFails(
				() -> ListField.create(new StringField().toFinal()),
				IllegalArgumentException.class,
				"element must not be final");
		assertFails(
				() -> ListField.create(new StringField().unique()),
				IllegalArgumentException.class,
				"element must not be unique");

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
		assertFails(
				q::search,
				IllegalArgumentException.class,
				"ListFieldItem-dates.element does not belong to a type of the query: " + q);
		assertFails(
				q::total,
				IllegalArgumentException.class,
				"ListFieldItem-dates.element does not belong to a type of the query: " + q);

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

		assertFails(
				() -> item.setDates(asList(date1, null, date2)),
				MandatoryViolationException.class,
				"mandatory violation for ListFieldItem-dates.element",
				dates.getElement());
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

		assertFails(
				() -> strings.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
		assertFails(
				() -> dates.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
		assertFails(
				() -> items.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
		assertFails(
				() -> strings.getDistinctParents(Item.class, "hallo"),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
		assertFails(
				() -> dates.getDistinctParents(Item.class, new Date()),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
		assertFails(
				() -> items.getDistinctParents(Item.class, item),
				ClassCastException.class,
				"parentClass requires " + item.getClass().getName() + ", " +
				"but was " + Item.class.getName());
	}

	@Test void testSetAndCast()
	{
		strings.setAndCast(item, asList("1hallo", "2bello"));
		assertEquals(asList("1hallo", "2bello"), item.getStrings());

		final List<Integer> integers = asList(1, 2);
		assertFails(
				() -> strings.setAndCast(item, integers),
				ClassCastException.class,
				"Cannot cast java.lang.Integer to java.lang.String");
		assertEquals(asList("1hallo", "2bello"), item.getStrings());
	}

	@Test void testRemoveAllSingle()
	{
		item.setStrings(asList("one1", "two2"));
		assertEquals(asList("one1", "two2"), item.getStrings());

		assertEquals(false, item.removeAllFromStrings("other"));
		assertEquals(asList("one1", "two2"), item.getStrings());

		assertEquals(true, item.removeAllFromStrings("two2"));
		assertEquals(asList("one1"), item.getStrings());

		assertEquals(true, item.removeAllFromStrings("one1"));
		assertEquals(asList(), item.getStrings());
	}

	@Test void testRemoveAllDuplicates()
	{
		item.setStrings(asList("one1", "two2", "two2", "two2"));
		assertEquals(asList("one1", "two2", "two2", "two2"), item.getStrings());

		assertEquals(true, item.removeAllFromStrings("two2"));
		assertEquals(asList("one1"), item.getStrings());
	}

	@Test void testRemoveAllNull()
	{
		item.setStrings(asList("one1", null, null, "two2"));
		assertEquals(asList("one1", null, null, "two2"), item.getStrings());

		assertEquals(true, item.removeAllFromStrings(null));
		assertEquals(asList("one1", "two2"), item.getStrings());

		assertEquals(false, item.removeAllFromStrings(null));
		assertEquals(asList("one1", "two2"), item.getStrings());
	}

	@Test void testListSetNull()
	{
		item.setStrings(asList("hallo", "bello"));

		assertFails(
				() -> item.setStrings(null),
				MandatoryViolationException.class,
				"mandatory violation on " + item + " for ListFieldItem.strings",
				strings, item);
		assertEquals(asList("hallo", "bello"), item.getStrings());
	}

	@Test void testOtherViolation()
	{
		final StringLengthViolationException e = assertFails(
				() -> item.setStrings(asList("one1", "two", "three3")),
				StringLengthViolationException.class,
				"length violation, 'two' is too short for ListFieldItem-strings.element, " +
				"must be at least 4 characters, but was 3.",
				strings.getElement());
		assertEquals("two", e.getValue());
		assertEquals(asList(), item.getStrings());
	}

	@Test void testSetCopyNull()
	{
		checkSetCopy(item, new ListFieldItem(), new ListFieldItem("x"));
	}

	@Test void testSetCopyNonnull()
	{
		checkSetCopy(new ListFieldItem("A"), new ListFieldItem("A"), item, new ListFieldItem("B"));
	}

	private static void checkSetCopy(final ListFieldItem valid1, final ListFieldItem valid2, final ListFieldItem... invalids)
	{
		final String validValue = valid1.getValue();
		assertEquals(validValue, valid2.getValue());
		valid1.setItemsSameValue(singletonList(valid1));
		assertEquals(asList(valid1), valid1.getItemsSameValue());
		valid1.setItemsSameValue(singletonList(valid2));
		assertEquals(asList(valid2), valid1.getItemsSameValue());
		valid1.addToItemsSameValue(valid1);
		assertEquals(asList(valid2, valid1), valid1.getItemsSameValue());
		valid1.setItemsSameValue(singletonList(null));
		assertEquals(singletonList(null), valid1.getItemsSameValue());
		for (final ListFieldItem invalid: invalids)
		{
			final String invalidValue = invalid.getValue();
			assertTrue(!Objects.equals(validValue, invalidValue));
			assertFails(
					() -> valid1.setItemsSameValue(asList(invalid)),
					CopyViolationException.class,
					"copy violation on ListFieldItem-itemsSameValue-0 for " + copyElement + ", " +
					"expected " + (invalidValue==null ? "null" : ("'" + invalidValue + "'")) + " " +
					"from target " + invalid + ", " +
					"but was " + (validValue==null ? "null" : ("'" + validValue + "'")));
			assertEquals(singletonList(null), valid1.getItemsSameValue());
			assertFails(
					() -> valid1.addToItemsSameValue(invalid),
					CopyViolationException.class,
					"copy violation for " + copyParent + " and " + copyElement + ", " +
					"expected " + (validValue==null ? "null" : ("'" + validValue + "'")) + " " +
					"from target " + valid1 + " " +
					"but also " + (invalidValue==null ? "null" : ("'" + invalidValue + "'")) + " " +
					"from target " + invalid);
			assertEquals(singletonList(null), valid1.getItemsSameValue());
		}
	}

	@Test void testCheckSetFieldCopyConstraint()
	{
		final Type<? extends Item> itemsSameValueType = itemsSameValue.getRelationType();
		assertEquals(asList(copyParent, copyElement), itemsSameValueType.getDeclaredCopyConstraints());
		assertEquals(0, copyParent.check());
		assertEquals(0, copyElement.check());
	}

	@Test void testCopyWithTemplates()
	{
		assertEquals(singletonList(value), itemsSameValue.getCopyWithTemplateFields());
	}
}

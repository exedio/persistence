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
import static com.exedio.cope.pattern.SetFieldItem.TYPE;
import static com.exedio.cope.pattern.SetFieldItem.dates;
import static com.exedio.cope.pattern.SetFieldItem.datesParent;
import static com.exedio.cope.pattern.SetFieldItem.strings;
import static com.exedio.cope.pattern.SetFieldItem.stringsParent;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.LocalizationKeys;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SetFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(SetFieldModelTest.class, "MODEL");
	}

	static final Type<?> stringsType = strings.getEntryType();
	static final FunctionField<String> stringsElement = strings.getElement();
	static final Type<?> datesType = dates.getEntryType();

	@Test void testModel()
	{
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType,
				datesType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(SetFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				strings,
				dates
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				stringsType.getThis(),
				stringsParent(),
				stringsElement,
				strings.getUniqueConstraint(),
				strings.getEntries()
			), stringsType.getFeatures());
		assertEqualsUnmodifiable(list(
				datesType.getThis(),
				datesParent(),
				dates.getElement(),
				dates.getUniqueConstraint(),
				dates.getEntries()
			), datesType.getFeatures());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());
		assertEquals(TYPE, dates.getType());
		assertEquals("dates", dates.getName());

		assertEquals("SetFieldItem-strings", stringsType.getID());
		assertEquals(Entry.class, stringsType.getJavaClass());
		assertEquals(false, stringsType.isBound());
		assertSame(strings, stringsType.getPattern());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubtypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(MODEL, stringsType.getModel());

		assertEquals("SetFieldItem-dates", datesType.getID());
		assertEquals(Entry.class, datesType.getJavaClass());
		assertEquals(false, datesType.isBound());
		assertSame(dates, datesType.getPattern());
		assertEquals(null, datesType.getSupertype());
		assertEqualsUnmodifiable(list(), datesType.getSubtypes());
		assertEquals(false, datesType.isAbstract());
		assertEquals(Item.class, datesType.getThis().getValueClass().getSuperclass());
		assertEquals(datesType, datesType.getThis().getValueType());
		assertEquals(MODEL, datesType.getModel());

		assertEquals(stringsType, stringsParent().getType());
		assertEquals(stringsType, stringsElement.getType());
		assertEquals(stringsType, strings.getUniqueConstraint().getType());
		assertEquals(datesType, datesParent().getType());
		assertEquals(datesType, dates.getElement().getType());
		assertEquals(datesType, dates.getUniqueConstraint().getType());
		assertSame(stringsParent(), strings.getParent());
		assertSame(datesParent(), dates.getParent());

		assertEquals("parent", stringsParent().getName());
		assertEquals("element", stringsElement.getName());
		assertEquals("uniqueConstraint", strings.getUniqueConstraint().getName());
		assertEquals("parent", datesParent().getName());
		assertEquals("element", dates.getElement().getName());
		assertEquals("uniqueConstraint", dates.getUniqueConstraint().getName());

		assertEquals(false, strings.isOrdered());
		assertEquals(null, strings.getOrder());
		assertEquals(null, strings.getUniqueConstraintForOrder());
		assertEquals(false, dates.isOrdered());
		assertEquals(null, dates.getOrder());
		assertEquals(null, dates.getUniqueConstraintForOrder());

		assertEqualsUnmodifiable(list(stringsParent(), stringsElement), strings.getUniqueConstraint().getFields());
		assertEqualsUnmodifiable(list(datesParent(), dates.getElement()), dates.getUniqueConstraint().getFields());

		assertSame(stringsParent(), strings.getEntries().getContainer());
		assertEquals("[" + strings.getElement() + " asc]", strings.getEntries().getOrders().toString());
		assertEquals("entries", strings.getEntries().getName());
		assertSame(strings.getEntryType(), strings.getEntries().getType());

		assertTrue(stringsType.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(datesType));
		assertTrue(!TYPE.isAssignableFrom(stringsType));
		assertTrue(!stringsType.isAssignableFrom(TYPE));

		assertEqualsUnmodifiable(list(), strings.getSourceFeatures());
		assertEqualsUnmodifiable(list(), dates.getSourceFeatures());
	}

	@Test void testComputed()
	{
		assertTrue(stringsType.isAnnotationPresent(Computed.class));
		assertTrue(  datesType.isAnnotationPresent(Computed.class));
	}

	@Test void testSerialize()
	{
		assertSerializedSame(strings, 394);
		assertSerializedSame(dates  , 392);
	}

	@Test void testElementNull()
	{
		assertFails(
				() -> SetField.create(null),
				NullPointerException.class,
				"element");
	}

	@Test void testElementFinal()
	{
		final StringField element = new StringField().toFinal();
		assertFails(
				() -> SetField.create(element),
				IllegalArgumentException.class,
				"element must not be final");
	}

	@Test void testElementOptional()
	{
		final StringField element = new StringField().optional();
		assertFails(
				() -> SetField.create(element),
				IllegalArgumentException.class,
				"element must be mandatory");
	}

	@Test void testElementDefault()
	{
		assertFails(
				() -> SetField.create(new StringField().defaultTo("someDefault")),
				IllegalArgumentException.class,
				"element must not have any default");
	}

	@Test void testElementUnique()
	{
		final StringField element = new StringField().unique();
		assertFails(
				() -> SetField.create(element),
				IllegalArgumentException.class,
				"element must not be unique");
	}

	@Test void testGetParentFieldStrings()
	{
		assertFails(
				() -> strings.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + SetFieldItem.class.getName() + ", " +
				"but was " + Item.class.getName());
	}

	@Test void testGetParentFieldDates()
	{
		assertFails(
				() -> dates.getParent(Item.class),
				ClassCastException.class,
				"parentClass requires " + SetFieldItem.class.getName() + ", " +
				"but was " + Item.class.getName());
	}

	@Test void testGetParentsStrings()
	{
		assertFails(
				() -> strings.getParents(Item.class, "hallo"),
				ClassCastException.class,
				"parentClass requires " + SetFieldItem.class.getName() + ", " +
				"but was " + Item.class.getName());
	}

	@Test void testGetParentsDates()
	{
		assertFails(
				() -> dates.getParents(Item.class, new Date()),
				ClassCastException.class,
				"parentClass requires " + SetFieldItem.class.getName() + ", " +
				"but was " + Item.class.getName());
	}

	/**
	 * @see com.exedio.cope.LocalizationKeysPatternTest#testVerbose()
	 */
	@Test public void testLocalizationKeys()
	{
		assertEquals(List.of(
				"com.exedio.cope.pattern.SetFieldItem",
				"SetFieldItem"),
				TYPE.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.SetFieldItem",
				"SetFieldItem"),
				LocalizationKeys.get(SetFieldItem.class));
		assertEquals(List.of(
				"com.exedio.cope.pattern.SetFieldItem.strings",
				"SetFieldItem.strings",
				"strings"),
				strings.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.SetFieldItem.strings.SourceType",
				"SetFieldItem.strings.SourceType",
				"strings.SourceType",
				"com.exedio.cope.pattern.Entry",
				"Entry"),
				stringsType.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.SetFieldItem.strings.SourceType.element",
				"SetFieldItem.strings.SourceType.element",
				"strings.SourceType.element",
				"com.exedio.cope.pattern.Entry.element",
				"Entry.element",
				"element"),
				stringsElement.getLocalizationKeys());
	}
}

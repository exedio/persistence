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
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class SetFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(SetFieldModelTest.class, "MODEL");
	}

	static final Type<?> stringsType = strings.getRelationType();
	static final FunctionField<String> stringsElement = strings.getElement();
	static final Type<?> datesType = dates.getRelationType();

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
				strings.getUniqueConstraint()
			), stringsType.getFeatures());
		assertEqualsUnmodifiable(list(
				datesType.getThis(),
				datesParent(),
				dates.getElement(),
				dates.getUniqueConstraint()
			), datesType.getFeatures());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());
		assertEquals(TYPE, dates.getType());
		assertEquals("dates", dates.getName());

		assertEquals("SetFieldItem-strings", stringsType.getID());
		assertEquals(PatternItem.class, stringsType.getJavaClass());
		assertEquals(false, stringsType.isBound());
		assertSame(strings, stringsType.getPattern());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubtypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(MODEL, stringsType.getModel());

		assertEquals("SetFieldItem-dates", datesType.getID());
		assertEquals(PatternItem.class, datesType.getJavaClass());
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

		assertSerializedSame(strings, 386);
		assertSerializedSame(dates  , 384);
	}

	@Test void testElementNull()
	{
		try
		{
			SetField.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("element", e.getMessage());
		}
	}

	@Test void testElementFinal()
	{
		try
		{
			SetField.create(new StringField().toFinal());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("element must not be final", e.getMessage());
		}
	}

	@Test void testElementOptional()
	{
		try
		{
			SetField.create(new StringField().optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("element must be mandatory", e.getMessage());
		}
	}

	@Test void testElementUnique()
	{
		try
		{
			SetField.create(new StringField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("element must not be unique", e.getMessage());
		}
	}

	@Test void testGetParentFieldStrings()
	{
		try
		{
			strings.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("parentClass requires " + SetFieldItem.class.getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
	}

	@Test void testGetParentFieldDates()
	{
		try
		{
			dates.getParent(Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("parentClass requires " + SetFieldItem.class.getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
	}

	@Test void testGetParentsStrings()
	{
		try
		{
			strings.getParents(Item.class, "hallo");
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("parentClass requires " + SetFieldItem.class.getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
	}

	@Test void testGetParentsDates()
	{
		try
		{
			dates.getParents(Item.class, new Date());
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("parentClass requires " + SetFieldItem.class.getName() + ", but was " + Item.class.getName(), e.getMessage());
		}
	}
}

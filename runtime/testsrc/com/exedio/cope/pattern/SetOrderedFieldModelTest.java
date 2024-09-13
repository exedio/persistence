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
import static com.exedio.cope.pattern.SetOrderedFieldItem.TYPE;
import static com.exedio.cope.pattern.SetOrderedFieldItem.strings;
import static com.exedio.cope.pattern.SetOrderedFieldItem.stringsParent;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import org.junit.jupiter.api.Test;

public class SetOrderedFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(SetOrderedFieldModelTest.class, "MODEL");
	}

	static final Type<?> stringsType = strings.getEntryType();
	static final IntegerField stringsOrder = strings.getOrder();
	static final FunctionField<String> stringsElement = strings.getElement();

	@Test void testModel()
	{
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				stringsType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(SetOrderedFieldItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				strings
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				stringsType.getThis(),
				stringsParent(),
				stringsOrder,
				strings.getUniqueConstraintForOrder(),
				stringsElement,
				strings.getUniqueConstraint()
			), stringsType.getFeatures());

		assertEquals(TYPE, strings.getType());
		assertEquals("strings", strings.getName());

		assertEquals("SetOrderedFieldItem-strings", stringsType.getID());
		assertEquals(PatternItem.class, stringsType.getJavaClass());
		assertEquals(false, stringsType.isBound());
		assertSame(strings, stringsType.getPattern());
		assertEquals(null, stringsType.getSupertype());
		assertEqualsUnmodifiable(list(), stringsType.getSubtypes());
		assertEquals(false, stringsType.isAbstract());
		assertEquals(Item.class, stringsType.getThis().getValueClass().getSuperclass());
		assertEquals(stringsType, stringsType.getThis().getValueType());
		assertEquals(MODEL, stringsType.getModel());

		assertEquals(stringsType, stringsParent().getType());
		assertEquals(stringsType, stringsOrder.getType());
		assertEquals(stringsType, strings.getUniqueConstraintForOrder().getType());
		assertEquals(stringsType, stringsElement.getType());
		assertEquals(stringsType, strings.getUniqueConstraint().getType());

		assertEquals("parent", stringsParent().getName());
		assertEquals("order", stringsOrder.getName());
		assertEquals("uniqueOrder", strings.getUniqueConstraintForOrder().getName());
		assertEquals("element", stringsElement.getName());
		assertEquals("uniqueConstraint", strings.getUniqueConstraint().getName());

		assertEquals(true, strings.isOrdered());

		assertEqualsUnmodifiable(list(stringsParent(), stringsOrder), strings.getUniqueConstraintForOrder().getFields());
		assertEqualsUnmodifiable(list(stringsParent(), stringsElement), strings.getUniqueConstraint().getFields());

		assertEqualsUnmodifiable(list(), strings.getSourceFeatures());
	}

	@Test void testSerialize()
	{
		assertSerializedSame(strings, 400);
	}
}

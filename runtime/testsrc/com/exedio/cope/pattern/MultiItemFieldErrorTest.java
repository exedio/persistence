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

import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MultiItemFieldErrorTest
{
	@Test void testCreateNoInterface()
	{
		assertFails(
				() -> MultiItemField.create(null),
				NullPointerException.class,
				"valueClass");
	}

	@Test void testCreateNoComponentClass()
	{
		assertFails(
				() -> TypesBound.newType(TestCreateNoComponentClass.class, failingActivator()),
				IllegalArgumentException.class,
				"must use at least 2 componentClasses in TestCreateNoComponentClass.field");
	}

	@WrapperType(indent=2, comments=false, type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE)
	private static final class TestCreateNoComponentClass extends Item
	{
		@SuppressWarnings("unused")
		@Wrapper(wrap="*", visibility=NONE)
		private static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	@Test void testCreateOnlyOneComponentClass()
	{
		assertFails(
				() -> TypesBound.newType(TestCreateOnlyOneComponent.class, failingActivator()),
				IllegalArgumentException.class,
				"must use at least 2 componentClasses in TestCreateOnlyOneComponent.field");
	}

	@WrapperType(indent=2, comments=false, type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE)
	private static final class TestCreateOnlyOneComponent extends Item
	{
		@SuppressWarnings("unused")
		@Wrapper(wrap="*", visibility=NONE)
		private static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	@Test void testCreateNull()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class);
		assertFails(
				() -> field.canBe(null),
				NullPointerException.class,
				"componentClass");
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	@Test void testCreateNotAssignable()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class);
		assertFails(
				() -> field.canBe((Class)AnotherItem1.class),
				IllegalArgumentException.class,
				"valueClass >" + MultiItemFieldValue.class + "< " +
				"must be assignable from componentClass >" + AnotherItem1.class + "<");
	}

	@Test void testCreateComponentClassesNotAllowedToBeSuperClassesOfEachOther()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).canBe(MultiItemFieldComponentA.class);
		assertFails(
				() -> field.canBe(MultiItemFieldComponentASub.class),
				IllegalArgumentException.class,
				"componentClasses must not be super-classes of each other: " + MultiItemFieldComponentA.class +
				" is assignable from " + MultiItemFieldComponentASub.class);
	}

	@Test void testCreateCopyNullComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		assertFails(
				() -> field.copyTo(null, null),
				NullPointerException.class,
				"componentClass");
	}

	@Test void testCreateCopyNoSuchComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		assertFails(
				() -> field.copyTo(MultiItemFieldComponentC.class, null),
				IllegalArgumentException.class,
				"illegal componentClass class com.exedio.cope.pattern.MultiItemFieldComponentC, " +
				"must be one of [" +
				"class com.exedio.cope.pattern.MultiItemFieldComponentA, " +
				"class com.exedio.cope.pattern.MultiItemFieldComponentB].");
	}

	@Test void testCreateCopyNullCopy()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		assertFails(
				() -> field.copyTo(MultiItemFieldComponentA.class, null),
				NullPointerException.class,
				"copyTo");
	}

	@Test void testNonItem()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		assertFails(
				() -> field.canBe(NonItemMultiItemFieldValue.class),
				IllegalArgumentException.class,
				"is not a subclass of " + Item.class.getName() + ": " +
				NonItemMultiItemFieldValue.class.getName());
	}

	@Test void testNullifyNotFinal()
	{
		final MultiItemField<?> finalField = MultiItemField.create(MultiItemFieldValuex.class).toFinal();
		assertFails(
				finalField::nullify,
				IllegalArgumentException.class,
				"final multi-item field cannot have delete policy nullify"
		);
		final MultiItemField<?> nullifyField = MultiItemField.create(MultiItemFieldValuex.class).nullify();
		assertFails(
				nullifyField::toFinal,
				IllegalArgumentException.class,
				"final multi-item field cannot have delete policy nullify"
		);
	}

	@Test void testNullifyIsOptional()
	{
		assertEquals(false, MultiItemField.create(MultiItemFieldValuex.class).nullify().isMandatory());
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static final class AnotherItem1 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	static final class NonItemMultiItemFieldValue implements MultiItemFieldValue
	{
		private static final long serialVersionUID = 1l;
	}
}

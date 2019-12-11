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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class MultiItemFieldErrorTest
{
	@Test void testCreateNoInterface()
	{
		try
		{
			MultiItemField.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}

	@Test void testCreateNoComponentClass()
	{
		try
		{
			TypesBound.newType(TestCreateNoComponentClass.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 componentClasses in TestCreateNoComponentClass.field";
			assertEquals(expected, e.getMessage());
		}
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
		try
		{
			TypesBound.newType(TestCreateOnlyOneComponent.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "must use at least 2 componentClasses in TestCreateOnlyOneComponent.field";
			assertEquals(expected, e.getMessage());
		}
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
		try
		{
			field.canBe(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			final String expected = "componentClass";
			assertEquals(expected, e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Test void testCreateNotAssignable()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class);
		try
		{
			field.canBe((Class)AnotherItem1.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "valueClass >"+MultiItemFieldValue.class
					+"< must be assignable from componentClass >"+AnotherItem1.class+"<";
			assertEquals(expected, e.getMessage());
		}
	}

	@Test void testCreateComponentClassesNotAllowedToBeSuperClassesOfEachOther()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).canBe(MultiItemFieldComponentA.class);
		try
		{
			field.canBe(MultiItemFieldComponentASub.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			final String expected = "componentClasses must not be super-classes of each other: "
					+MultiItemFieldComponentA.class+" is assignable from "
					+MultiItemFieldComponentASub.class+"";
			assertEquals(expected, e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNullComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("componentClass", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNoSuchComponent()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(MultiItemFieldComponentC.class, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"illegal componentClass class com.exedio.cope.pattern.MultiItemFieldComponentC, " +
				"must be one of [" +
				"class com.exedio.cope.pattern.MultiItemFieldComponentA, " +
				"class com.exedio.cope.pattern.MultiItemFieldComponentB].",
				e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testCreateCopyNullCopy()
	{
		final MultiItemField<?> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		try
		{
			field.copyTo(MultiItemFieldComponentA.class, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("copyTo", e.getMessage());
		}
	}

	@Test void testNonItem()
	{
		final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);
		try
		{
			field.canBe(NonItemMultiItemFieldValue.class);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("is not a subclass of " + Item.class.getName() + ": " + NonItemMultiItemFieldValue.class.getName(), e.getMessage());
		}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	static final class AnotherItem1 extends com.exedio.cope.Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	static final class NonItemMultiItemFieldValue implements MultiItemFieldValue
	{
		private static final long serialVersionUID = 1l;
	}
}

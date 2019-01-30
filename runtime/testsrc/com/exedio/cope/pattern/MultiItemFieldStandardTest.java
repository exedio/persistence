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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MultiItemFieldStandardTest
{
	@WrapperType(indent=2, comments=false)
	static final class AnMandatoryItem extends com.exedio.cope.Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnMandatoryItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnMandatoryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnMandatoryItem> TYPE = com.exedio.cope.TypesBound.newType(AnMandatoryItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnMandatoryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class AnOptionalItem extends com.exedio.cope.Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).optional();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnOptionalItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnOptionalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnOptionalItem> TYPE = com.exedio.cope.TypesBound.newType(AnOptionalItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnOptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class AnFinalItem extends com.exedio.cope.Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).toFinal();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnFinalItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnFinalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnFinalItem> TYPE = com.exedio.cope.TypesBound.newType(AnFinalItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class ThreeItem extends com.exedio.cope.Item
	{
		static final MultiItemField<MultiItemFieldValue> mandatory = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class,
				MultiItemFieldComponentC.class);

		static final MultiItemField<MultiItemFieldValue> optional = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class,
				MultiItemFieldComponentC.class).
				optional();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		ThreeItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ThreeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ThreeItem> TYPE = com.exedio.cope.TypesBound.newType(ThreeItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ThreeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class AnCascadeItem extends com.exedio.cope.Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).cascade();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnCascadeItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnCascadeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnCascadeItem> TYPE = com.exedio.cope.TypesBound.newType(AnCascadeItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnCascadeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Test void testGetComponentClasses()
	{
		assertEqualsUnmodifiable(
				asList(
						MultiItemFieldComponentA.class,
						MultiItemFieldComponentB.class),
				AnMandatoryItem.field.getComponentClasses());
		assertEqualsUnmodifiable(
				asList(
						MultiItemFieldComponentA.class,
						MultiItemFieldComponentB.class,
						MultiItemFieldComponentC.class),
				ThreeItem.mandatory.getComponentClasses());
	}

	@Test void testOf()
	{
		final List<ItemField<?>> c = AnMandatoryItem.field.getComponents();
		assertEquals(2, c.size());

		assertSame(c.get(0), AnMandatoryItem.field.of(MultiItemFieldComponentA.class));
		assertSame(c.get(1), AnMandatoryItem.field.of(MultiItemFieldComponentB.class));
		try
		{
			AnMandatoryItem.field.of(MultiItemFieldComponentC.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"class >class com.exedio.cope.pattern.MultiItemFieldComponentC< is not supported by AnMandatoryItem.field",
				e.getMessage());
		}
		try
		{
			AnMandatoryItem.field.of(MultiItemFieldComponentASub.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"class >class com.exedio.cope.pattern.MultiItemFieldComponentASub< is not supported by AnMandatoryItem.field",
				e.getMessage());
		}
	}

	@Test void testGetInitialExceptionsMandatory()
	{
		assertContains(MandatoryViolationException.class, AnMandatoryItem.field.getInitialExceptions());
	}

	@Test void testGetInitialExceptionsOptional()
	{
		assertContains(AnOptionalItem.field.getInitialExceptions());
	}

	@Test void testGetInitialType()
	{
		assertEquals(MultiItemFieldValue.class, AnMandatoryItem.field.getInitialType());
	}

	@Test void testIsFinal()
	{
		assertEquals(true, AnFinalItem.field.isFinal());
	}

	@Test void testIsFinalFalse()
	{
		assertEquals(false, AnMandatoryItem.field.isFinal());
	}

	@Test void testIsInitial()
	{
		assertEquals(true, AnMandatoryItem.field.isInitial());
		assertEquals(false, AnOptionalItem.field.isInitial());
		assertEquals(true, AnFinalItem.field.isInitial());
	}

	@Test void testIsMandatory()
	{
		assertEquals(true, AnMandatoryItem.field.isMandatory());
	}

	@Test void testIsMandatoryFalse()
	{
		assertEquals(false, AnOptionalItem.field.isMandatory());
	}

	@Test void testMandatoryCheckConstraint()
	{
		assertEquals(
			"(" +
			"(AnMandatoryItem.field-MultiItemFieldComponentA is not null AND" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is null) OR " +
			"(AnMandatoryItem.field-MultiItemFieldComponentA is null AND" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is not null)" +
			")",
			check(AnMandatoryItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is not null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is not null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is not null)" +
			")",
			check(ThreeItem.mandatory).getCondition().toString());
	}

	@Test void testOptionalCheckConstraint()
	{
		assertEquals(
			"(AnOptionalItem.field-MultiItemFieldComponentB is null OR" +
			" AnOptionalItem.field-MultiItemFieldComponentA is null)",
			check(AnOptionalItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.optional-MultiItemFieldComponentB is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentB is null)" +
			")",
			check(ThreeItem.optional).getCondition().toString());
	}

	private static CheckConstraint check(final MultiItemField<?> field)
	{
		return (CheckConstraint)field.getSourceFeatures().get(field.getSourceFeatures().size()-1);
	}

	@Test void testDefaultPolicyForbid()
	{
		assertEquals(FORBID, AnMandatoryItem.field.getDeletePolicy());
		assertEquals(FORBID, AnMandatoryItem.field.getComponents().get(0).getDeletePolicy());
		assertEquals(FORBID, AnMandatoryItem.field.getComponents().get(1).getDeletePolicy());
	}

	@Test void testCascadePolicy()
	{
		assertEquals(CASCADE, AnCascadeItem.field.getDeletePolicy());
		assertEquals(CASCADE, AnCascadeItem.field.getComponents().get(0).getDeletePolicy());
		assertEquals(CASCADE, AnCascadeItem.field.getComponents().get(1).getDeletePolicy());
	}

	@Test void testEqualConditionNull3Classes()
	{
		assertEquals(
				"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
				" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
				" ThreeItem.mandatory-MultiItemFieldComponentC is null)",
				ThreeItem.mandatory.equal(null).toString());
	}
}

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.instrument.WrapperType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MultiItemFieldStandardTest
{
	@WrapperType(indent=2, comments=false)
	private static final class AnMandatoryItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnMandatoryItem(
					@javax.annotation.Nonnull final MultiItemFieldValue field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnMandatoryItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnMandatoryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValue getField()
		{
			return AnMandatoryItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final MultiItemFieldValue field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			AnMandatoryItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnMandatoryItem> TYPE = com.exedio.cope.TypesBound.newType(AnMandatoryItem.class,AnMandatoryItem::new);

		@com.exedio.cope.instrument.Generated
		private AnMandatoryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnOptionalItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnOptionalItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		private AnOptionalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		MultiItemFieldValue getField()
		{
			return AnOptionalItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final MultiItemFieldValue field)
		{
			AnOptionalItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnOptionalItem> TYPE = com.exedio.cope.TypesBound.newType(AnOptionalItem.class,AnOptionalItem::new);

		@com.exedio.cope.instrument.Generated
		private AnOptionalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnFinalItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnFinalItem(
					@javax.annotation.Nonnull final MultiItemFieldValue field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnFinalItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnFinalItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValue getField()
		{
			return AnFinalItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnFinalItem> TYPE = com.exedio.cope.TypesBound.newType(AnFinalItem.class,AnFinalItem::new);

		@com.exedio.cope.instrument.Generated
		private AnFinalItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class ThreeItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> mandatory = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				canBe(MultiItemFieldComponentC.class);

		static final MultiItemField<MultiItemFieldValue> optional = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				canBe(MultiItemFieldComponentC.class).
				optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ThreeItem(
					@javax.annotation.Nonnull final MultiItemFieldValue mandatory)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ThreeItem.mandatory.map(mandatory),
			});
		}

		@com.exedio.cope.instrument.Generated
		private ThreeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValue getMandatory()
		{
			return ThreeItem.mandatory.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setMandatory(@javax.annotation.Nonnull final MultiItemFieldValue mandatory)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			ThreeItem.mandatory.set(this,mandatory);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		MultiItemFieldValue getOptional()
		{
			return ThreeItem.optional.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setOptional(@javax.annotation.Nullable final MultiItemFieldValue optional)
		{
			ThreeItem.optional.set(this,optional);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ThreeItem> TYPE = com.exedio.cope.TypesBound.newType(ThreeItem.class,ThreeItem::new);

		@com.exedio.cope.instrument.Generated
		private ThreeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class FourItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> mandatory = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				canBe(MultiItemFieldComponentC.class).
				canBe(MultiItemFieldComponentD.class);

		static final MultiItemField<MultiItemFieldValue> optional = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				canBe(MultiItemFieldComponentC.class).
				canBe(MultiItemFieldComponentD.class).
				optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private FourItem(
					@javax.annotation.Nonnull final MultiItemFieldValue mandatory)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				FourItem.mandatory.map(mandatory),
			});
		}

		@com.exedio.cope.instrument.Generated
		private FourItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValue getMandatory()
		{
			return FourItem.mandatory.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setMandatory(@javax.annotation.Nonnull final MultiItemFieldValue mandatory)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			FourItem.mandatory.set(this,mandatory);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		MultiItemFieldValue getOptional()
		{
			return FourItem.optional.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setOptional(@javax.annotation.Nullable final MultiItemFieldValue optional)
		{
			FourItem.optional.set(this,optional);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<FourItem> TYPE = com.exedio.cope.TypesBound.newType(FourItem.class,FourItem::new);

		@com.exedio.cope.instrument.Generated
		private FourItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnCascadeItem extends Item
	{
		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(MultiItemFieldValue.class).
				canBe(MultiItemFieldComponentA.class).
				canBe(MultiItemFieldComponentB.class).
				cascade();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnCascadeItem(
					@javax.annotation.Nonnull final MultiItemFieldValue field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnCascadeItem.field.map(field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnCascadeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValue getField()
		{
			return AnCascadeItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final MultiItemFieldValue field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			AnCascadeItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnCascadeItem> TYPE = com.exedio.cope.TypesBound.newType(AnCascadeItem.class,AnCascadeItem::new);

		@com.exedio.cope.instrument.Generated
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
		assertEqualsUnmodifiable(
				asList(
						MultiItemFieldComponentA.class,
						MultiItemFieldComponentB.class,
						MultiItemFieldComponentC.class,
						MultiItemFieldComponentD.class),
				FourItem.mandatory.getComponentClasses());
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
			"(AnMandatoryItem.field-MultiItemFieldComponentA is not null and" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is null) or " +
			"(AnMandatoryItem.field-MultiItemFieldComponentA is null and" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is not null)" +
			")",
			check(AnMandatoryItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is not null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) or " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is not null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) or " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null and" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is not null)" +
			")",
			check(ThreeItem.mandatory).getCondition().toString());
		assertEquals(
				"(" +
				"(FourItem.mandatory-MultiItemFieldComponentA is not null and" +
				" FourItem.mandatory-MultiItemFieldComponentB is null and" +
				" FourItem.mandatory-MultiItemFieldComponentC is null and" +
				" FourItem.mandatory-MultiItemFieldComponentD is null) or " +
				"(FourItem.mandatory-MultiItemFieldComponentA is null and" +
				" FourItem.mandatory-MultiItemFieldComponentB is not null and" +
				" FourItem.mandatory-MultiItemFieldComponentC is null and" +
				" FourItem.mandatory-MultiItemFieldComponentD is null) or " +
				"(FourItem.mandatory-MultiItemFieldComponentA is null and" +
				" FourItem.mandatory-MultiItemFieldComponentB is null and" +
				" FourItem.mandatory-MultiItemFieldComponentC is not null and" +
				" FourItem.mandatory-MultiItemFieldComponentD is null) or " +
				"(FourItem.mandatory-MultiItemFieldComponentA is null and" +
				" FourItem.mandatory-MultiItemFieldComponentB is null and" +
				" FourItem.mandatory-MultiItemFieldComponentC is null and" +
				" FourItem.mandatory-MultiItemFieldComponentD is not null)" +
				")",
				check(FourItem.mandatory).getCondition().toString());
	}

	@Test void testOptionalCheckConstraint()
	{
		assertEquals(
			"(AnOptionalItem.field-MultiItemFieldComponentB is null or" +
			" AnOptionalItem.field-MultiItemFieldComponentA is null)",
			check(AnOptionalItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.optional-MultiItemFieldComponentB is null and" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) or " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null and" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) or " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null and" +
			" ThreeItem.optional-MultiItemFieldComponentB is null)" +
			")",
			check(ThreeItem.optional).getCondition().toString());
		assertEquals(
				"(" +
				"(FourItem.optional-MultiItemFieldComponentB is null and" +
				" FourItem.optional-MultiItemFieldComponentC is null and" +
				" FourItem.optional-MultiItemFieldComponentD is null) or " +
				"(FourItem.optional-MultiItemFieldComponentA is null and" +
				" FourItem.optional-MultiItemFieldComponentC is null and" +
				" FourItem.optional-MultiItemFieldComponentD is null) or " +
				"(FourItem.optional-MultiItemFieldComponentA is null and" +
				" FourItem.optional-MultiItemFieldComponentB is null and" +
				" FourItem.optional-MultiItemFieldComponentD is null) or " +
				"(FourItem.optional-MultiItemFieldComponentA is null and" +
				" FourItem.optional-MultiItemFieldComponentB is null and" +
				" FourItem.optional-MultiItemFieldComponentC is null)" +
				")",
				check(FourItem.optional).getCondition().toString());
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
				"(ThreeItem.mandatory-MultiItemFieldComponentA is null and" +
				" ThreeItem.mandatory-MultiItemFieldComponentB is null and" +
				" ThreeItem.mandatory-MultiItemFieldComponentC is null)",
				ThreeItem.mandatory.equal(null).toString());
	}

	@Test void testEqualConditionNull4Classes()
	{
		assertEquals(
				"(FourItem.mandatory-MultiItemFieldComponentA is null and" +
				" FourItem.mandatory-MultiItemFieldComponentB is null and" +
				" FourItem.mandatory-MultiItemFieldComponentC is null and" +
				" FourItem.mandatory-MultiItemFieldComponentD is null)",
				FourItem.mandatory.equal(null).toString());
	}
}

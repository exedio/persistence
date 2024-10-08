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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import java.util.Iterator;
import org.junit.jupiter.api.Test;

public class MultiItemFieldCopyConstraintTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(
			A.TYPE, B.TYPE, C.TYPE, PartialCopyConstraintItem.TYPE, AllCopyConstraintItem.TYPE, DoubleCopyConstraintItem.TYPE
	);

	static
	{
		MODEL.enableSerialization(MultiItemFieldCopyConstraintTest.class, "MODEL");
	}

	public MultiItemFieldCopyConstraintTest()
	{
		super(MODEL);
	}


	@Test void testModelAll()
	{
		final Iterator<? extends CopyConstraint> i = AllCopyConstraintItem.TYPE.getCopyConstraints().iterator();
		assertIt(A.value, AllCopyConstraintItem.value, AllCopyConstraintItem.field.of(A.class), i.next());
		assertIt(B.value, AllCopyConstraintItem.value, AllCopyConstraintItem.field.of(B.class), i.next());
		assertFalse(i.hasNext());
	}

	@Test void testModelDouble()
	{
		final Iterator<? extends CopyConstraint> i = DoubleCopyConstraintItem.TYPE.getCopyConstraints().iterator();
		assertIt(C.value,    DoubleCopyConstraintItem.value,    DoubleCopyConstraintItem.field.of(C.class), i.next());
		assertIt(C.template, DoubleCopyConstraintItem.template, DoubleCopyConstraintItem.field.of(C.class), i.next());
		assertFalse(i.hasNext());
	}

	@Test void testModelPartial()
	{
		final Iterator<? extends CopyConstraint> i = PartialCopyConstraintItem.TYPE.getCopyConstraints().iterator();
		assertIt(A.value, PartialCopyConstraintItem.value, PartialCopyConstraintItem.field.of(A.class), i.next());
		assertFalse(i.hasNext());
	}

	private static void assertIt(
			final FunctionField<?> template,
			final FunctionField<?> copy,
			final ItemField<?> target,
			final CopyConstraint actual)
	{
		assertEquals(template, actual.getTemplate());
		assertEquals(false, actual.isChoice());
		assertEquals(copy, actual.getCopyField());
		assertEquals(copy, actual.getCopyFunction());
		assertEquals(target, actual.getTarget());
	}


	@Test void testOk()
	{
		final A a = new A("value1");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", a);
		assertEquals("value1", test.getValue());
		assertEquals("value1", a.getValue());
	}

	@Test void testNoCopyConstraintOnB()
	{
		final B b = new B("value2");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", b);
		assertEquals("value1", test.getValue());
		assertEquals("value2", b.getValue());
	}

	@Test void testCopyConstraintOnAllFields()
	{
		final A a = new A("value1");
		final B b = new B("value2");
		final AllCopyConstraintItem test1 = new AllCopyConstraintItem("value1", a);
		final AllCopyConstraintItem test2 = new AllCopyConstraintItem("value2", b);
		assertEquals("value1", test1.getValue());
		assertEquals("value1", a.getValue());
		assertEquals("value2", test2.getValue());
		assertEquals("value2", b.getValue());
	}

	@Test void testCopyConstraintOnAllFieldsInvalid()
	{
		final A a = new A("value2");
		final B b = new B("value1");

		try
		{
			new AllCopyConstraintItem("value1", a);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation for AllCopyConstraintItem.valueCopyFromfield-A, expected 'value2' from target A-0, but was 'value1'", e.getMessage());
		}
		try
		{
			new AllCopyConstraintItem("value2", b);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation for AllCopyConstraintItem.valueCopyFromfield-B, expected 'value1' from target B-0, but was 'value2'", e.getMessage());
		}
	}

	@Test void testDoubleCopyConstraintOnFields()
	{
		final C c = new C("value2", "value3");
		final DoubleCopyConstraintItem item = new DoubleCopyConstraintItem("value2", "value3", c);
		assertEquals("value2", item.getValue());
		assertEquals("value3", item.getTemplate());
	}

	@Test void testDoubleCopyConstraintOnFieldsInvalid()
	{
		final C c = new C("value2", "value3");
		try
		{
			new DoubleCopyConstraintItem("x", "value3", c);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation for DoubleCopyConstraintItem.valueCopyFromfield-C, expected 'value2' from target C-0, but was 'x'", e.getMessage());
		}
		try
		{
			new DoubleCopyConstraintItem("value2", "x", c);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation for DoubleCopyConstraintItem.templateCopyFromfield-C, expected 'value3' from target C-0, but was 'x'", e.getMessage());
		}
	}

	@WrapperType(indent=2)
	private static final class DoubleCopyConstraintItem extends Item
	{
		static final StringField value = new StringField().toFinal();
		static final StringField template = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
				canBe(A.class).canBe(C.class).
				toFinal().copyTo(C.class, value).copyTo(C.class, template);


		/**
		 * Creates a new DoubleCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param template the initial value for field {@link #template}.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, template, field is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private DoubleCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final java.lang.String template,
					@javax.annotation.Nonnull final MultiItemFieldValuex field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(DoubleCopyConstraintItem.value,value),
				com.exedio.cope.SetValue.map(DoubleCopyConstraintItem.template,template),
				com.exedio.cope.SetValue.map(DoubleCopyConstraintItem.field,field),
			});
		}

		/**
		 * Creates a new DoubleCopyConstraintItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private DoubleCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return DoubleCopyConstraintItem.value.get(this);
		}

		/**
		 * Returns the value of {@link #template}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getTemplate()
		{
			return DoubleCopyConstraintItem.template.get(this);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValuex getField()
		{
			return DoubleCopyConstraintItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for doubleCopyConstraintItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<DoubleCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(DoubleCopyConstraintItem.class,DoubleCopyConstraintItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private DoubleCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class AllCopyConstraintItem extends Item
	{
		static final StringField value = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
				canBe(A.class).canBe(B.class).
				toFinal().copyTo(A.class, value).copyTo(B.class, value);


		/**
		 * Creates a new AllCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, field is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AllCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final MultiItemFieldValuex field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AllCopyConstraintItem.value,value),
				com.exedio.cope.SetValue.map(AllCopyConstraintItem.field,field),
			});
		}

		/**
		 * Creates a new AllCopyConstraintItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private AllCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return AllCopyConstraintItem.value.get(this);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValuex getField()
		{
			return AllCopyConstraintItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for allCopyConstraintItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<AllCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(AllCopyConstraintItem.class,AllCopyConstraintItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private AllCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class PartialCopyConstraintItem extends Item
	{
		static final StringField value = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(MultiItemFieldValuex.class).
				canBe(A.class).canBe(B.class).
				toFinal().copyTo(A.class, value);


		/**
		 * Creates a new PartialCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, field is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private PartialCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final MultiItemFieldValuex field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(PartialCopyConstraintItem.value,value),
				com.exedio.cope.SetValue.map(PartialCopyConstraintItem.field,field),
			});
		}

		/**
		 * Creates a new PartialCopyConstraintItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private PartialCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return PartialCopyConstraintItem.value.get(this);
		}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		MultiItemFieldValuex getField()
		{
			return PartialCopyConstraintItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for partialCopyConstraintItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<PartialCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(PartialCopyConstraintItem.class,PartialCopyConstraintItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private PartialCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class A extends Item implements MultiItemFieldValuex
	{
		public static final StringField value = new StringField().toFinal();



		/**
		 * Creates a new A with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private A(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(A.value,value),
			});
		}

		/**
		 * Creates a new A and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private A(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public java.lang.String getValue()
		{
			return A.value.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for a.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<A> TYPE = com.exedio.cope.TypesBound.newType(A.class,A::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private A(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class B extends Item implements MultiItemFieldValuex
	{
		public static final StringField value = new StringField().toFinal();



		/**
		 * Creates a new B with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private B(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(B.value,value),
			});
		}

		/**
		 * Creates a new B and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private B(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public java.lang.String getValue()
		{
			return B.value.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for b.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<B> TYPE = com.exedio.cope.TypesBound.newType(B.class,B::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private B(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class C extends Item implements MultiItemFieldValuex
	{
		public static final StringField value = new StringField().toFinal();
		public static final StringField template = new StringField().toFinal();



		/**
		 * Creates a new C with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param template the initial value for field {@link #template}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, template is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private C(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final java.lang.String template)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(C.value,value),
				com.exedio.cope.SetValue.map(C.template,template),
			});
		}

		/**
		 * Creates a new C and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private C(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #value}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public java.lang.String getValue()
		{
			return C.value.get(this);
		}

		/**
		 * Returns the value of {@link #template}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public java.lang.String getTemplate()
		{
			return C.template.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for c.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<C> TYPE = com.exedio.cope.TypesBound.newType(C.class,C::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private C(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
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
	static final class DoubleCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();
		static final StringField template = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				C.class).toFinal().copyTo(C.class, value).copyTo(C.class, template);

		DoubleCopyConstraintItem(final String value, final String template, final MultiItemFieldValuex field)
		{
			this(DoubleCopyConstraintItem.value.map(value), DoubleCopyConstraintItem.template.map(template), DoubleCopyConstraintItem.field.map(field));
		}



		/**
		 * Creates a new DoubleCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param template the initial value for field {@link #template}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, template is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		DoubleCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final java.lang.String template)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				DoubleCopyConstraintItem.value.map(value),
				DoubleCopyConstraintItem.template.map(template),
			});
		}

		/**
		 * Creates a new DoubleCopyConstraintItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private DoubleCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return DoubleCopyConstraintItem.value.get(this);
		}

		/**
		 * Returns the value of {@link #template}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		java.lang.String getTemplate()
		{
			return DoubleCopyConstraintItem.template.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for doubleCopyConstraintItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<DoubleCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(DoubleCopyConstraintItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private DoubleCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class AllCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value).copyTo(B.class, value);

		AllCopyConstraintItem(final String value, final MultiItemFieldValuex field)
		{
			this(AllCopyConstraintItem.value.map(value), AllCopyConstraintItem.field.map(field));
		}



		/**
		 * Creates a new AllCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		AllCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AllCopyConstraintItem.value.map(value),
			});
		}

		/**
		 * Creates a new AllCopyConstraintItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private AllCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return AllCopyConstraintItem.value.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for allCopyConstraintItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<AllCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(AllCopyConstraintItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private AllCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class PartialCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();

		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value);

		PartialCopyConstraintItem(final String template, final MultiItemFieldValuex field)
		{
			this(PartialCopyConstraintItem.value.map(template), PartialCopyConstraintItem.field.map(field));
		}



		/**
		 * Creates a new PartialCopyConstraintItem with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		PartialCopyConstraintItem(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				PartialCopyConstraintItem.value.map(value),
			});
		}

		/**
		 * Creates a new PartialCopyConstraintItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private PartialCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		java.lang.String getValue()
		{
			return PartialCopyConstraintItem.value.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for partialCopyConstraintItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<PartialCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(PartialCopyConstraintItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private PartialCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class A extends com.exedio.cope.Item implements MultiItemFieldValuex
	{
		@Wrapper(wrap="get", override=true)
		public static final StringField value = new StringField().toFinal();



		/**
		 * Creates a new A with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		A(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				A.value.map(value),
			});
		}

		/**
		 * Creates a new A and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private A(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		@java.lang.Override
		public java.lang.String getValue()
		{
			return A.value.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for a.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<A> TYPE = com.exedio.cope.TypesBound.newType(A.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private A(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class B extends com.exedio.cope.Item implements MultiItemFieldValuex
	{
		@Wrapper(wrap="get", override=true)
		public static final StringField value = new StringField().toFinal();



		/**
		 * Creates a new B with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @throws com.exedio.cope.MandatoryViolationException if value is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		B(
					@javax.annotation.Nonnull final java.lang.String value)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				B.value.map(value),
			});
		}

		/**
		 * Creates a new B and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private B(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		@java.lang.Override
		public java.lang.String getValue()
		{
			return B.value.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for b.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<B> TYPE = com.exedio.cope.TypesBound.newType(B.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private B(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class C extends com.exedio.cope.Item implements MultiItemFieldValuex
	{
		@Wrapper(wrap="get", override=true)
		public static final StringField value = new StringField().toFinal();
		public static final StringField template = new StringField().toFinal();



		/**
		 * Creates a new C with all the fields initially needed.
		 * @param value the initial value for field {@link #value}.
		 * @param template the initial value for field {@link #template}.
		 * @throws com.exedio.cope.MandatoryViolationException if value, template is null.
		 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		C(
					@javax.annotation.Nonnull final java.lang.String value,
					@javax.annotation.Nonnull final java.lang.String template)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				C.value.map(value),
				C.template.map(template),
			});
		}

		/**
		 * Creates a new C and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private C(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #value}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		@java.lang.Override
		public java.lang.String getValue()
		{
			return C.value.get(this);
		}

		/**
		 * Returns the value of {@link #template}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		public java.lang.String getTemplate()
		{
			return C.template.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for c.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<C> TYPE = com.exedio.cope.TypesBound.newType(C.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private C(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

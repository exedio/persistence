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

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

public class MultiItemFieldCopyConstraintTest extends AbstractRuntimeModelTest
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

	public void testOk()
	{
		final A a = new A("value1");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", a);
		assertEquals("value1", test.getValue());
		assertEquals("value1", a.getValue());
	}

	public void testNoCopyConstraintOnB()
	{
		final B b = new B("value2");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", b);
		assertEquals("value1", test.getValue());
		assertEquals("value2", b.getValue());
	}

	public void testCopyConstraintOnAllFields()
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

	public void testCopyConstraintOnAllFieldsInvalid()
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
			assertEquals("copy violation on AllCopyConstraintItem.valueCopyFromfield-A, expected 'value2' from target A-0, but was 'value1'", e.getMessage());
		}
		try
		{
			new AllCopyConstraintItem("value2", b);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation on AllCopyConstraintItem.valueCopyFromfield-B, expected 'value1' from target B-0, but was 'value2'", e.getMessage());
		}
	}

	public void testDoubleCopyConstraintOnFields()
	{
		final C c = new C("value2", "value3");
		final DoubleCopyConstraintItem item = new DoubleCopyConstraintItem("value2", "value3", c);
		assertEquals("value2", item.getValue());
		assertEquals("value3", item.getTemplate());
	}

	public void testDoubleCopyConstraintOnFieldsInvalid()
	{
		final C c = new C("value2", "value3");
		try
		{
			new DoubleCopyConstraintItem("x", "value3", c);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation on DoubleCopyConstraintItem.valueCopyFromfield-C, expected 'value2' from target C-0, but was 'x'", e.getMessage());
		}
		try
		{
			new DoubleCopyConstraintItem("value2", "x", c);
			fail("exception expected");
		}
		catch(final CopyViolationException e)
		{
			assertEquals("copy violation on DoubleCopyConstraintItem.templateCopyFromfield-C, expected 'value3' from target C-0, but was 'x'", e.getMessage());
		}
	}

	static final class DoubleCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();
		static final StringField template = new StringField().toFinal();

		/**
		 * @cope.ignore
		 */
		static final MultiItemField<MultiItemFieldItemInterface> field = MultiItemField.create(
				MultiItemFieldItemInterface.class,
				A.class,
				C.class).toFinal().copyTo(C.class, value).copyTo(C.class, template);

		DoubleCopyConstraintItem(final String value, final String template, final MultiItemFieldItemInterface field)
		{
			this(DoubleCopyConstraintItem.value.map(value), DoubleCopyConstraintItem.template.map(template), DoubleCopyConstraintItem.field.map(field));
		}



	/**

	 **
	 * Creates a new DoubleCopyConstraintItem with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @param template the initial value for field {@link #template}.
	 * @throws com.exedio.cope.MandatoryViolationException if value, template is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	DoubleCopyConstraintItem(
				final java.lang.String value,
				final java.lang.String template)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DoubleCopyConstraintItem.value.map(value),
			DoubleCopyConstraintItem.template.map(template),
		});
	}/**

	 **
	 * Creates a new DoubleCopyConstraintItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private DoubleCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getValue()
	{
		return DoubleCopyConstraintItem.value.get(this);
	}/**

	 **
	 * Returns the value of {@link #template}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getTemplate()
	{
		return DoubleCopyConstraintItem.template.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for doubleCopyConstraintItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<DoubleCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(DoubleCopyConstraintItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DoubleCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class AllCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();

		/**
		 * @cope.ignore
		 */
		static final MultiItemField<MultiItemFieldItemInterface> field = MultiItemField.create(
				MultiItemFieldItemInterface.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value).copyTo(B.class, value);

		AllCopyConstraintItem(final String value, final MultiItemFieldItemInterface field)
		{
			this(AllCopyConstraintItem.value.map(value), AllCopyConstraintItem.field.map(field));
		}



	/**

	 **
	 * Creates a new AllCopyConstraintItem with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.MandatoryViolationException if value is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AllCopyConstraintItem(
				final java.lang.String value)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AllCopyConstraintItem.value.map(value),
		});
	}/**

	 **
	 * Creates a new AllCopyConstraintItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AllCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getValue()
	{
		return AllCopyConstraintItem.value.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for allCopyConstraintItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AllCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(AllCopyConstraintItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AllCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class PartialCopyConstraintItem extends com.exedio.cope.Item
	{
		static final StringField value = new StringField().toFinal();

		/**
		 * @cope.ignore
		 */
		static final MultiItemField<MultiItemFieldItemInterface> field = MultiItemField.create(
				MultiItemFieldItemInterface.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value);

		PartialCopyConstraintItem(final String template, final MultiItemFieldItemInterface field)
		{
			this(PartialCopyConstraintItem.value.map(template), PartialCopyConstraintItem.field.map(field));
		}



	/**

	 **
	 * Creates a new PartialCopyConstraintItem with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.MandatoryViolationException if value is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	PartialCopyConstraintItem(
				final java.lang.String value)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			PartialCopyConstraintItem.value.map(value),
		});
	}/**

	 **
	 * Creates a new PartialCopyConstraintItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private PartialCopyConstraintItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getValue()
	{
		return PartialCopyConstraintItem.value.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for partialCopyConstraintItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<PartialCopyConstraintItem> TYPE = com.exedio.cope.TypesBound.newType(PartialCopyConstraintItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private PartialCopyConstraintItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class A extends com.exedio.cope.Item implements MultiItemFieldItemInterface
	{
		public static final StringField value = new StringField().toFinal();



	/**

	 **
	 * Creates a new A with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.MandatoryViolationException if value is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	A(
				final java.lang.String value)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			A.value.map(value),
		});
	}/**

	 **
	 * Creates a new A and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private A(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final java.lang.String getValue()
	{
		return A.value.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for a.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<A> TYPE = com.exedio.cope.TypesBound.newType(A.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private A(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class B extends com.exedio.cope.Item implements MultiItemFieldItemInterface
	{
		public static final StringField value = new StringField().toFinal();



	/**

	 **
	 * Creates a new B with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @throws com.exedio.cope.MandatoryViolationException if value is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	B(
				final java.lang.String value)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			B.value.map(value),
		});
	}/**

	 **
	 * Creates a new B and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private B(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final java.lang.String getValue()
	{
		return B.value.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for b.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<B> TYPE = com.exedio.cope.TypesBound.newType(B.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private B(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	static final class C extends com.exedio.cope.Item implements MultiItemFieldItemInterface
	{
		public static final StringField value = new StringField().toFinal();
		public static final StringField template = new StringField().toFinal();



	/**

	 **
	 * Creates a new C with all the fields initially needed.
	 * @param value the initial value for field {@link #value}.
	 * @param template the initial value for field {@link #template}.
	 * @throws com.exedio.cope.MandatoryViolationException if value, template is null.
	 * @throws com.exedio.cope.StringLengthViolationException if value, template violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	C(
				final java.lang.String value,
				final java.lang.String template)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			C.value.map(value),
			C.template.map(template),
		});
	}/**

	 **
	 * Creates a new C and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private C(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #value}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final java.lang.String getValue()
	{
		return C.value.get(this);
	}/**

	 **
	 * Returns the value of {@link #template}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	public final java.lang.String getTemplate()
	{
		return C.template.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for c.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<C> TYPE = com.exedio.cope.TypesBound.newType(C.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private C(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}

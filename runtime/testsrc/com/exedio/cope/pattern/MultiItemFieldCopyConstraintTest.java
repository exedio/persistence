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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import java.util.Iterator;
import org.junit.Test;

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


	@Test public void testModelAll()
	{
		final Iterator<CopyConstraint> i = AllCopyConstraintItem.TYPE.getCopyConstraints().iterator();
		assertIt(A.value, AllCopyConstraintItem.value, AllCopyConstraintItem.field.of(A.class), i.next());
		assertIt(B.value, AllCopyConstraintItem.value, AllCopyConstraintItem.field.of(B.class), i.next());
		assertFalse(i.hasNext());
	}

	@Test public void testModelDouble()
	{
		final Iterator<CopyConstraint> i = DoubleCopyConstraintItem.TYPE.getCopyConstraints().iterator();
		assertIt(C.value,    DoubleCopyConstraintItem.value,    DoubleCopyConstraintItem.field.of(C.class), i.next());
		assertIt(C.template, DoubleCopyConstraintItem.template, DoubleCopyConstraintItem.field.of(C.class), i.next());
		assertFalse(i.hasNext());
	}

	@Test public void testModelPartial()
	{
		final Iterator<CopyConstraint> i = PartialCopyConstraintItem.TYPE.getCopyConstraints().iterator();
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
		assertEquals(copy, actual.getCopy());
		assertEquals(target, actual.getTarget());
	}


	@Test public void testOk()
	{
		final A a = new A("value1");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", a);
		assertEquals("value1", test.getValue());
		assertEquals("value1", a.getValue());
	}

	@Test public void testNoCopyConstraintOnB()
	{
		final B b = new B("value2");
		final PartialCopyConstraintItem test = new PartialCopyConstraintItem("value1", b);
		assertEquals("value1", test.getValue());
		assertEquals("value2", b.getValue());
	}

	@Test public void testCopyConstraintOnAllFields()
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

	@Test public void testCopyConstraintOnAllFieldsInvalid()
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

	@Test public void testDoubleCopyConstraintOnFields()
	{
		final C c = new C("value2", "value3");
		final DoubleCopyConstraintItem item = new DoubleCopyConstraintItem("value2", "value3", c);
		assertEquals("value2", item.getValue());
		assertEquals("value3", item.getTemplate());
	}

	@Test public void testDoubleCopyConstraintOnFieldsInvalid()
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
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				C.class).toFinal().copyTo(C.class, value).copyTo(C.class, template);

		DoubleCopyConstraintItem(final String value, final String template, final MultiItemFieldValuex field)
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
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value).copyTo(B.class, value);

		AllCopyConstraintItem(final String value, final MultiItemFieldValuex field)
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
		static final MultiItemField<MultiItemFieldValuex> field = MultiItemField.create(
				MultiItemFieldValuex.class,
				A.class,
				B.class).toFinal().copyTo(A.class, value);

		PartialCopyConstraintItem(final String template, final MultiItemFieldValuex field)
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

	static final class A extends com.exedio.cope.Item implements MultiItemFieldValuex
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

	static final class B extends com.exedio.cope.Item implements MultiItemFieldValuex
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

	static final class C extends com.exedio.cope.Item implements MultiItemFieldValuex
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

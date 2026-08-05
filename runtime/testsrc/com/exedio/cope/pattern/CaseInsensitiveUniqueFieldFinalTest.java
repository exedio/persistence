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

import static com.exedio.cope.pattern.CaseInsensitiveUniqueField.create;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

/**
 * @see CaseInsensitiveUniqueFieldTest
 */
public class CaseInsensitiveUniqueFieldFinalTest extends TestWithEnvironment
{
	public CaseInsensitiveUniqueFieldFinalTest()
	{
		super(model);
	}

	private static final Model model = new Model(MyItem.TYPE);

	@Test void test()
	{
		assertEquals(true,  MyItem.field               .isFinal());
		assertEquals(true,  MyItem.field.getPristine() .isFinal());
		assertEquals(true,  MyItem.field.getLowerCase().isFinal());
		assertEquals(false, MyItem.field               .isMandatory());
		assertEquals(false, MyItem.field.getPristine ().isMandatory());
		assertEquals(false, MyItem.field.getLowerCase().isMandatory());
		assertEquals(true,  MyItem.field.isInitial());

		final MyItem i1 = new MyItem("Alpha");
		assertEquals("Alpha", i1.getField());
		assertEquals("alpha", MyItem.field.getLowerCase().get(i1));

		assertFails(
				() -> MyItem.field.set(i1, "Beta"),
				FinalViolationException.class,
				"final violation on " + i1 + " for MyItem.field");
		assertEquals("Alpha", i1.getField());
		assertEquals("alpha", MyItem.field.getLowerCase().get(i1));
	}

	@WrapperType(indent=2)
	static class MyItem extends Item
	{
		@WrapperInitial
		static final CaseInsensitiveUniqueField field = create(new StringField().optional().toFinal());

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.StringCharSetViolationException if field violates its character set constraint.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 * @throws com.exedio.cope.UniqueViolationException if field is not unique.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringCharSetViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Finds a myItem by its {@link #field}, ignoring case.
		 * @param field shall be equal to field {@link #field}, ignoring case.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static final MyItem forField(@javax.annotation.Nonnull final java.lang.String field)
		{
			return MyItem.field.searchUnique(MyItem.class,field);
		}

		/**
		 * Finds a myItem by its {@link #field}, ignoring case.
		 * @param field shall be equal to field {@link #field}, ignoring case.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final MyItem forFieldStrict(@javax.annotation.Nonnull final java.lang.String field)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.field.searchUniqueStrict(MyItem.class,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for myItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.CompositeCheckConstraintTest.MyComposite;
import org.junit.jupiter.api.Test;

public class CompositeCheckConstraintFieldTest
{
	@Test void testTypeGetFeatures()
	{
		assertEqualsUnmodifiable(asList(
				MyItem.TYPE.getThis(),
				MyItem.field,
				fieldAlpha, fieldGamma, fieldCheck,
				MyItem.field.getUnison()),
				MyItem.TYPE.getFeatures());
	}

	@Test void testSourceFeatures()
	{
		assertEqualsUnmodifiable(asList(
				fieldAlpha, fieldGamma, fieldCheck,
				MyItem.field.getUnison()),
				MyItem.field.getSourceFeatures());
	}

	@Test void testGetName()
	{
		assertEquals("field-alpha", fieldAlpha.getName());
		assertEquals("field-gamma", fieldGamma.getName());
		assertEquals("field-check", fieldCheck.getName());
	}

	@Test void testGetID()
	{
		assertEquals("MyItem.field-alpha", fieldAlpha.getID());
		assertEquals("MyItem.field-gamma", fieldGamma.getID());
		assertEquals("MyItem.field-check", fieldCheck.getID());
	}

	@Test void testMapping()
	{
		assertSame(MyComposite.alpha, MyItem.field.getTemplate(fieldAlpha));
		assertSame(MyComposite.gamma, MyItem.field.getTemplate(fieldGamma));
		assertSame(MyComposite.alpha, MyItem.field.getTemplate((Feature)fieldAlpha));
		assertSame(MyComposite.gamma, MyItem.field.getTemplate((Feature)fieldGamma));
		assertSame(MyComposite.check, MyItem.field.getTemplate((Feature)fieldCheck));
		assertSame(fieldAlpha, MyItem.field.of((Feature)MyComposite.alpha));
		assertSame(fieldGamma, MyItem.field.of((Feature)MyComposite.gamma));
		assertSame(fieldCheck, MyItem.field.of((Feature)MyComposite.check));
	}

	@Test void testGetCondition()
	{
		assertEquals(
				fieldAlpha + "<" + fieldGamma,
				fieldCheck.getCondition().toString());
	}

	@WrapperType(indent=2)
	static final class MyItem extends Item
	{
		@WrapperInitial
		static final CompositeField<MyComposite> field = CompositeField.create(MyComposite.class).optional();


		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nullable final MyComposite field)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		MyComposite getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final MyComposite field)
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
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
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);

	static final IntegerField    fieldAlpha = MyItem.field.of(MyComposite.alpha);
	static final IntegerField    fieldGamma = MyItem.field.of(MyComposite.gamma);
	static final CheckConstraint fieldCheck = MyItem.field.of(MyComposite.check);
}

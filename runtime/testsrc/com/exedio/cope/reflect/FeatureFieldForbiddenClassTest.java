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

package com.exedio.cope.reflect;

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Pattern;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.PriceField;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class FeatureFieldForbiddenClassTest extends TestWithEnvironment
{
	FeatureFieldForbiddenClassTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(new HashSet<>(asList(
				IntegerField.class,
				Pattern.class)),
				MyItem.field.getForbiddenValueClasses());
		assertEqualsUnmodifiable(asList(
				MyItem.TYPE.getThis(),
				MyItem.field.getIdField(),
				MyItem.booleanF,
				MyItem.priceF.getInt()),
				MyItem.field.getValues());
		assertEquals(
				"(" +
				"MyItem.field-id<>'MyItem.this' and " +
				"MyItem.field-id<>'MyItem.field-id' and " +
				"MyItem.field-id<>'MyItem.booleanF' and " +
				"MyItem.field-id<>'MyItem.priceF-int')",
				MyItem.field.isInvalid().toString());

		final MyItem i = new MyItem(MyItem.booleanF);
		assertEquals(MyItem.booleanF, i.getField());

		final ForbiddenFeatureException e = assertFails(
				() -> i.setField(MyItem.integerF),
				ForbiddenFeatureException.class,
				"forbidden feature MyItem.integerF " +
				"which is a com.exedio.cope.IntegerField " +
				"on " + i + " for MyItem.field");
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(i, e.getItem());
		assertEquals(MyItem.integerF, e.getValue());
		assertEquals(IntegerField.class, e.getForbiddenValueClass());
	}

	@Test void testForbiddenInteger()
	{
		final ForbiddenFeatureException e = assertFails(
				() -> new MyItem(MyItem.integerF),
				ForbiddenFeatureException.class,
				"forbidden feature MyItem.integerF " +
				"which is a com.exedio.cope.IntegerField " +
				"for MyItem.field");
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(null, e.getItem());
		assertEquals(MyItem.integerF, e.getValue());
		assertEquals(IntegerField.class, e.getForbiddenValueClass());
	}

	@Test void testForbiddenPrice()
	{
		final ForbiddenFeatureException e = assertFails(
				() -> new MyItem(MyItem.priceF),
				ForbiddenFeatureException.class,
				"forbidden feature MyItem.priceF " +
				"which is a com.exedio.cope.pattern.PriceField " +
				"for MyItem.field, " +
				"is forbidden by com.exedio.cope.Pattern");
		assertEquals(MyItem.field, e.getFeature());
		assertEquals(null, e.getItem());
		assertEquals(MyItem.priceF, e.getValue());
		assertEquals(Pattern.class, e.getForbiddenValueClass());
	}

	@WrapperType(indent=2)
	static final class MyItem extends Item
	{
		static final FeatureField<Feature> field = FeatureField.create().forbid(IntegerField.class).forbid(Pattern.class);

		@WrapperIgnore static final BooleanField booleanF = new BooleanField().toFinal().optional();
		@WrapperIgnore static final IntegerField integerF = new IntegerField().toFinal().optional();
		@WrapperIgnore static final PriceField   priceF   = new PriceField()  .toFinal().optional();

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param field the initial value for field {@link #field}.
		 * @throws com.exedio.cope.MandatoryViolationException if field is null.
		 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
		 * @throws com.exedio.cope.reflect.ForbiddenFeatureException if field is forbidden.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nonnull final Feature field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.reflect.ForbiddenFeatureException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
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
		@javax.annotation.Nonnull
		Feature getField()
		{
			return MyItem.field.get(this);
		}

		/**
		 * Sets a new value for {@link #field}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final Feature field)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.reflect.ForbiddenFeatureException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for myItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

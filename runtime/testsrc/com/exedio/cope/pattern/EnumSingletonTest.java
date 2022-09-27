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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.EnumField;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.WrapperType;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class EnumSingletonTest extends TestWithEnvironment
{
	EnumSingletonTest()
	{
		super(model);
	}

	@Test void instanceFailsIfNoItemExists()
	{
		assertFails(
				() -> MyItem.instance(OneDigitPrime.seven),
				IllegalArgumentException.class, "seven");
	}

	@Test void instanceFailsOnNull()
	{
		assertFails(
				() -> MyItem.instance(null),
				NullPointerException.class, "value");
	}

	@Test void cantCreateDuplicate()
	{
		final MyItem item = new MyItem(OneDigitPrime.two);
		assertEquals(OneDigitPrime.two, item.getPrime());
		assertFails(
				() -> new MyItem(OneDigitPrime.two),
				UniqueViolationException.class,
				"unique violation for MyItem.prime-onceImplicitUnique");
		assertEquals(item, MyItem.instance(OneDigitPrime.two));
	}

	@Test void model()
	{
		assertEquals(true, MyItem.prime.isInitial());
		assertEquals(true, MyItem.prime.isFinal());
		assertEquals(true, MyItem.prime.isMandatory());
		assertEquals(OneDigitPrime.class, MyItem.prime.getInitialType());
		assertEquals(new HashSet<>(asList(
				FinalViolationException.class,
				MandatoryViolationException.class,
				UniqueViolationException.class)),
				MyItem.prime.getInitialExceptions());

		final EnumField<OneDigitPrime> once = MyItem.prime.getOnce();
		assertEquals(true, once.isInitial());
		assertEquals(true, once.isFinal());
		assertEquals(true, once.isMandatory());
		assertEquals(OneDigitPrime.class, once.getValueClass());
		assertEquals("prime", SchemaInfo.getColumnName(once));
	}

	@WrapperType(indent=2)
	static class MyItem extends Item
	{
		static final EnumSingleton<OneDigitPrime> prime = EnumSingleton.create(OneDigitPrime.class);

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param prime the initial value for field {@link #prime}.
		 * @throws com.exedio.cope.MandatoryViolationException if prime is null.
		 * @throws com.exedio.cope.UniqueViolationException if prime is not unique.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		MyItem(
					@javax.annotation.Nonnull final OneDigitPrime prime)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.prime.map(prime),
			});
		}

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #prime}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final OneDigitPrime getPrime()
		{
			return MyItem.prime.get(this);
		}

		/**
		 * Gets the instance of myItem for the given value.
		 * @return never returns null.
		 * @throws java.lang.IllegalArgumentException if no such instance exists
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="instance")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static final MyItem instance(@javax.annotation.Nonnull final OneDigitPrime prime)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.prime.instance(MyItem.class,prime);
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
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unused")
	enum OneDigitPrime
	{
		two,
		three,
		five,
		seven
	}

	private static final Model model = new Model(MyItem.TYPE);
}

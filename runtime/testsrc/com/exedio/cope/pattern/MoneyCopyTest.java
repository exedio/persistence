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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.pattern.Money.valueOf;
import static com.exedio.cope.pattern.MoneyCopyTest.Currency.euro;
import static com.exedio.cope.pattern.MoneyCopyTest.Currency.pounds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class MoneyCopyTest extends TestWithEnvironment
{
	public MoneyCopyTest()
	{
		super(MODEL);
	}

	@Test public void testProvideAllOk()
	{
		final Target target = new Target(euro);
		final Source source = Source.create(target, euro, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());
	}

	@Test public void testProvideAllWrongFixed()
	{
		final Target target = new Target(euro);
		try
		{
			Source.create(target, euro, valueOf(4.44, pounds), valueOf(5.55, euro));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(
					"illegal currency at '4.44pounds' for Source.fixed, allowed is 'euro'.",
					e.getMessage());
		}
	}

	@Test public void testProvideAllWrongShared()
	{
		final Target target = new Target(euro);
		try
		{
			Source.create(target, euro, valueOf(4.44, euro), valueOf(5.55, pounds));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(
					"illegal currency at '5.55pounds' for Source.shared, allowed is 'euro'.",
					e.getMessage());
		}
	}

	@Test public void testOmitCopy()
	{
		final Target target = new Target(euro);
		final Source source = Source.create(target, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());
	}

	@Test public void testOmitTarget()
	{
		final Source source = Source.create(euro, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(null, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());
	}


	@WrapperType(constructor=PRIVATE, indent=2)
	static final class Source extends Item
	{
		static final ItemField<Target> target = ItemField.create(Target.class).toFinal().optional();

		static final EnumField<Currency> currency = EnumField.create(Currency.class).toFinal().copyFrom(target);

		static final MoneyField<Currency> fixed  = MoneyField.fixed(Currency.euro);
		static final MoneyField<Currency> shared = MoneyField.shared(currency);


		static Source create(
				final Target target,
				final Currency currency,
				final Money<Currency> fixed,
				final Money<Currency> shared)
		{
			return new Source(target, currency, fixed, shared);
		}

		static Source create(
				final Target target,
				final Money<Currency> fixed,
				final Money<Currency> shared)
		{
			return new Source(
					Source.target.map(target),
					Source.fixed.map(fixed),
					Source.shared.map(shared));
		}

		static Source create(
				final Currency currency,
				final Money<Currency> fixed,
				final Money<Currency> shared)
		{
			return new Source(
					Source.currency.map(currency),
					Source.fixed.map(fixed),
					Source.shared.map(shared));
		}


		/**
		 * Creates a new Source with all the fields initially needed.
		 * @param target the initial value for field {@link #target}.
		 * @param currency the initial value for field {@link #currency}.
		 * @param fixed the initial value for field {@link #fixed}.
		 * @param shared the initial value for field {@link #shared}.
		 * @throws com.exedio.cope.MandatoryViolationException if currency, fixed, shared is null.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		private Source(
					@javax.annotation.Nullable final Target target,
					@javax.annotation.Nonnull final Currency currency,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> fixed,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> shared)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Source.target.map(target),
				Source.currency.map(currency),
				Source.fixed.map(fixed),
				Source.shared.map(shared),
			});
		}

		/**
		 * Creates a new Source and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private Source(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		/**
		 * Returns the value of {@link #target}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final Target getTarget()
		{
			return Source.target.get(this);
		}

		/**
		 * Returns the value of {@link #currency}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		final Currency getCurrency()
		{
			return Source.currency.get(this);
		}

		/**
		 * Returns the value of {@link #fixed}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final com.exedio.cope.pattern.Money<Currency> getFixed()
		{
			return Source.fixed.get(this);
		}

		/**
		 * Sets a new value for {@link #fixed}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setFixed(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> fixed)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.fixed.set(this,fixed);
		}

		/**
		 * Returns the value of {@link #shared}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		final com.exedio.cope.pattern.Money<Currency> getShared()
		{
			return Source.shared.get(this);
		}

		/**
		 * Sets a new value for {@link #shared}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		final void setShared(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> shared)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.shared.set(this,shared);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for source.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	static final class Target extends Item
	{
		@Wrapper(wrap="get", visibility=NONE)
		static final EnumField<Currency> currency = EnumField.create(Currency.class).toFinal();

		/**
		 * Creates a new Target with all the fields initially needed.
		 * @param currency the initial value for field {@link #currency}.
		 * @throws com.exedio.cope.MandatoryViolationException if currency is null.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		Target(
					@javax.annotation.Nonnull final Currency currency)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				Target.currency.map(currency),
			});
		}

		/**
		 * Creates a new Target and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private Target(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for target.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	enum Currency implements com.exedio.cope.pattern.Money.Currency
	{
		euro, pounds
	}

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);
}

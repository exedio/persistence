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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MoneyCopyTest extends TestWithEnvironment
{
	public MoneyCopyTest()
	{
		super(MODEL);
	}

	@Test void testProvideAllOk()
	{
		final Target target = new Target(euro);
		final Source source = Source.create(target, euro, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());

		final Target targetSet = new Target(pounds);
		source.setShared(targetSet, pounds, valueOf(6.66, pounds));
		assertEquals(targetSet, source.getTarget());
		assertEquals(pounds, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(6.66, pounds), source.getShared());
	}

	@Test void testProvideAllWrongFixed()
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
					"illegal currency at '4.44pounds' for Source.fixed, allowed is 'euro'",
					e.getMessage());
		}
	}

	@Test void testProvideAllWrongShared()
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
					"illegal currency at '5.55pounds' for Source.shared, allowed is 'euro'",
					e.getMessage());
		}
	}

	@Test void testOmitCopy()
	{
		final Target target = new Target(euro);
		final Source source = Source.create(target, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());

		final Target targetSet = new Target(pounds);
		source.setShared(targetSet, valueOf(6.66, pounds));
		assertEquals(targetSet, source.getTarget());
		assertEquals(pounds, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(6.66, pounds), source.getShared());

		source.setTarget(target);
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(6.66, euro), source.getShared());
	}

	@Test void testOmitCopyWrong()
	{
		final Target target = new Target(euro);
		final Source source = Source.create(target, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());

		final Target targetSet = new Target(pounds);
		try
		{
			source.setShared(targetSet, valueOf(6.66, euro));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(
					"illegal currency at '6.66euro' on " + source + " for Source.shared, allowed is 'pounds'",
					e.getMessage());
		}
		assertEquals(target, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());
	}

	@Test void testOmitTarget()
	{
		final Source source = Source.create(euro, valueOf(4.44, euro), valueOf(5.55, euro));
		assertEquals(null, source.getTarget());
		assertEquals(euro, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(5.55, euro), source.getShared());

		source.setShared(pounds, valueOf(6.66, pounds));
		assertEquals(null, source.getTarget());
		assertEquals(pounds, source.getCurrency());
		assertEquals(valueOf(4.44, euro), source.getFixed());
		assertEquals(valueOf(6.66, pounds), source.getShared());
	}


	@WrapperType(constructor=PRIVATE, indent=2)
	private static final class Source extends Item
	{
		@WrapperInitial
		static final ItemField<Target> target = ItemField.create(Target.class).optional();

		static final EnumField<Currency> currency = EnumField.create(Currency.class).copyFrom(target, () -> Target.currency);

		@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // TODO instrumentor does not support static imports
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
					SetValue.map(Source.target, target),
					SetValue.map(Source.fixed, fixed),
					SetValue.map(Source.shared, shared));
		}

		static Source create(
				final Currency currency,
				final Money<Currency> fixed,
				final Money<Currency> shared)
		{
			return new Source(
					SetValue.map(Source.currency, currency),
					SetValue.map(Source.fixed, fixed),
					SetValue.map(Source.shared, shared));
		}

		void setShared(
				final Target target,
				final Money<Currency> shared)
		{
			set(
					SetValue.map(Source.target, target),
					SetValue.map(Source.shared, shared));
		}

		void setShared(
				final Currency currency,
				final Money<Currency> shared)
		{
			set(
					SetValue.map(Source.currency, currency),
					SetValue.map(Source.shared, shared));
		}

		void setShared(
				final Target target,
				final Currency currency,
				final Money<Currency> shared)
		{
			set(
					SetValue.map(Source.target, target),
					SetValue.map(Source.currency, currency),
					SetValue.map(Source.shared, shared));
		}


		/**
		 * Creates a new Source with all the fields initially needed.
		 * @param target the initial value for field {@link #target}.
		 * @param currency the initial value for field {@link #currency}.
		 * @param fixed the initial value for field {@link #fixed}.
		 * @param shared the initial value for field {@link #shared}.
		 * @throws com.exedio.cope.MandatoryViolationException if currency, fixed, shared is null.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Source(
					@javax.annotation.Nullable final Target target,
					@javax.annotation.Nonnull final Currency currency,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> fixed,
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> shared)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Source.target,target),
				com.exedio.cope.SetValue.map(Source.currency,currency),
				com.exedio.cope.SetValue.map(Source.fixed,fixed),
				com.exedio.cope.SetValue.map(Source.shared,shared),
			});
		}

		/**
		 * Creates a new Source and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private Source(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #target}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Target getTarget()
		{
			return Source.target.get(this);
		}

		/**
		 * Sets a new value for {@link #target}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTarget(@javax.annotation.Nullable final Target target)
		{
			Source.target.set(this,target);
		}

		/**
		 * Returns the value of {@link #currency}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Currency getCurrency()
		{
			return Source.currency.get(this);
		}

		/**
		 * Sets a new value for {@link #currency}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setCurrency(@javax.annotation.Nonnull final Currency currency)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.currency.set(this,currency);
		}

		/**
		 * Returns the value of {@link #fixed}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.Money<Currency> getFixed()
		{
			return Source.fixed.get(this);
		}

		/**
		 * Sets a new value for {@link #fixed}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setFixed(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> fixed)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.fixed.set(this,fixed);
		}

		/**
		 * Returns the value of {@link #shared}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.Money<Currency> getShared()
		{
			return Source.shared.get(this);
		}

		/**
		 * Sets a new value for {@link #shared}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setShared(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> shared)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			Source.shared.set(this,shared);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for source.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<Source> TYPE = com.exedio.cope.TypesBound.newType(Source.class,Source::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private Source(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2)
	private static final class Target extends Item
	{
		@Wrapper(wrap="get", visibility=NONE)
		static final EnumField<Currency> currency = EnumField.create(Currency.class).toFinal();

		/**
		 * Creates a new Target with all the fields initially needed.
		 * @param currency the initial value for field {@link #currency}.
		 * @throws com.exedio.cope.MandatoryViolationException if currency is null.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Target(
					@javax.annotation.Nonnull final Currency currency)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Target.currency,currency),
			});
		}

		/**
		 * Creates a new Target and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for target.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	enum Currency implements com.exedio.cope.pattern.Money.Currency
	{
		euro, pounds
	}

	private static final Model MODEL = new Model(Source.TYPE, Target.TYPE);
}

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

import static com.exedio.cope.pattern.Money.valueOf;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.eur;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.gbp;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;

public final class MoneyFieldItem extends Item
{
	static final MoneyField<CurrFix> fixeOpt = MoneyField.fixed(CurrFix.fix).optional();

	static MoneyFieldItem fixeOpt(final Money<CurrFix> fixeOpt)
	{
		return new MoneyFieldItem(
			MoneyFieldItem.fixeOpt.map(fixeOpt),
			MoneyFieldItem.currency.map(gbp),
			MoneyFieldItem.sharMan.map(valueOf(8888.88, gbp)),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}


	enum Currency implements Money.Currency {eur,gbp}

	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // TODO instrumentor does not support static imports
	static final MoneyField<Currency> fixeEnu = MoneyField.fixed(Currency.eur).optional();

	static MoneyFieldItem fixeEnu(final Money<Currency> fixeEnu)
	{
		return new MoneyFieldItem(
			MoneyFieldItem.fixeEnu.map(fixeEnu),
			MoneyFieldItem.currency.map(gbp),
			MoneyFieldItem.sharMan.map(valueOf(8888.88, gbp)),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}


	static final EnumField<Currency> currency = EnumField.create(Currency.class);

	static final MoneyField<Currency> sharOpt = MoneyField.shared(currency).optional();

	static MoneyFieldItem sharOpt(final Currency currency, final Money<Currency> sharOpt)
	{
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(currency),
			MoneyFieldItem.sharOpt.map(sharOpt),
			MoneyFieldItem.sharMan.map(eurX),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}


	static final MoneyField<Currency> sharMan = MoneyField.shared(currency);

	static MoneyFieldItem sharMan(
			final Currency currency,
			final Money<Currency> sharMan)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(currency),
			MoneyFieldItem.sharOpt.map(eurX),
			MoneyFieldItem.sharMan.map(sharMan),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}

	static MoneyFieldItem sharMan(
			final Currency currency,
			final Money<Currency> sharOpt,
			final Money<Currency> sharMan)
	{
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(currency),
			MoneyFieldItem.sharOpt.map(sharOpt),
			MoneyFieldItem.sharMan.map(sharMan),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}


	static final MoneyField<Currency> exclOpt = MoneyField.exclusive(EnumField.create(Currency.class)).optional();

	static MoneyFieldItem exclOpt(final Money<Currency> exclOpt)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(eur),
			MoneyFieldItem.sharMan.map(eurX),
			MoneyFieldItem.exclOpt.map(exclOpt),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp))
		);
	}

	Currency getExclOptCurrency()
	{
		return exclOpt.getCurrencyField().get(this);
	}


	static final MoneyField<Currency> exclMan = MoneyField.exclusive(EnumField.create(Currency.class));

	static MoneyFieldItem exclMan(final Money<Currency> exclMan)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(eur),
			MoneyFieldItem.sharMan.map(eurX),
			MoneyFieldItem.exclMan.map(exclMan)
		);
	}

	Currency getExclManCurrency()
	{
		return exclMan.getCurrencyField().get(this);
	}


	// test special cases in instrumentor
	static final MoneyField<CurrencyItem> byItem = MoneyField.exclusive(ItemField.create(CurrencyItem.class)).optional();

	static MoneyFieldItem byItem(final Money<CurrencyItem> byItem)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		return new MoneyFieldItem(
			MoneyFieldItem.currency.map(eur),
			MoneyFieldItem.sharMan.map(eurX),
			MoneyFieldItem.exclMan.map(valueOf(9999.99, gbp)),
			MoneyFieldItem.byItem.map(byItem)
		);
	}


	private static final Money<Currency> eurX = valueOf(8888.88, eur);

	/**
	 * Creates a new MoneyFieldItem with all the fields initially needed.
	 * @param currency the initial value for field {@link #currency}.
	 * @param sharMan the initial value for field {@link #sharMan}.
	 * @param exclMan the initial value for field {@link #exclMan}.
	 * @throws com.exedio.cope.MandatoryViolationException if currency, sharMan, exclMan is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	MoneyFieldItem(
				@javax.annotation.Nonnull final Currency currency,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> sharMan,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> exclMan)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MoneyFieldItem.currency.map(currency),
			MoneyFieldItem.sharMan.map(sharMan),
			MoneyFieldItem.exclMan.map(exclMan),
		});
	}

	/**
	 * Creates a new MoneyFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MoneyFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #fixeOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Money<CurrFix> getFixeOpt()
	{
		return MoneyFieldItem.fixeOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #fixeOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFixeOpt(@javax.annotation.Nullable final com.exedio.cope.pattern.Money<CurrFix> fixeOpt)
	{
		MoneyFieldItem.fixeOpt.set(this,fixeOpt);
	}

	/**
	 * Returns the value of {@link #fixeEnu}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Money<Currency> getFixeEnu()
	{
		return MoneyFieldItem.fixeEnu.get(this);
	}

	/**
	 * Sets a new value for {@link #fixeEnu}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setFixeEnu(@javax.annotation.Nullable final com.exedio.cope.pattern.Money<Currency> fixeEnu)
	{
		MoneyFieldItem.fixeEnu.set(this,fixeEnu);
	}

	/**
	 * Returns the value of {@link #currency}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Currency getCurrency()
	{
		return MoneyFieldItem.currency.get(this);
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
		MoneyFieldItem.currency.set(this,currency);
	}

	/**
	 * Returns the value of {@link #sharOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Money<Currency> getSharOpt()
	{
		return MoneyFieldItem.sharOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #sharOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSharOpt(@javax.annotation.Nullable final com.exedio.cope.pattern.Money<Currency> sharOpt)
	{
		MoneyFieldItem.sharOpt.set(this,sharOpt);
	}

	/**
	 * Returns the value of {@link #sharMan}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Money<Currency> getSharMan()
	{
		return MoneyFieldItem.sharMan.get(this);
	}

	/**
	 * Sets a new value for {@link #sharMan}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setSharMan(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> sharMan)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		MoneyFieldItem.sharMan.set(this,sharMan);
	}

	/**
	 * Returns the value of {@link #exclOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Money<Currency> getExclOpt()
	{
		return MoneyFieldItem.exclOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #exclOpt}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setExclOpt(@javax.annotation.Nullable final com.exedio.cope.pattern.Money<Currency> exclOpt)
	{
		MoneyFieldItem.exclOpt.set(this,exclOpt);
	}

	/**
	 * Returns the value of {@link #exclMan}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Money<Currency> getExclMan()
	{
		return MoneyFieldItem.exclMan.get(this);
	}

	/**
	 * Sets a new value for {@link #exclMan}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setExclMan(@javax.annotation.Nonnull final com.exedio.cope.pattern.Money<Currency> exclMan)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		MoneyFieldItem.exclMan.set(this,exclMan);
	}

	/**
	 * Returns the value of {@link #byItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Money<CurrencyItem> getByItem()
	{
		return MoneyFieldItem.byItem.get(this);
	}

	/**
	 * Sets a new value for {@link #byItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByItem(@javax.annotation.Nullable final com.exedio.cope.pattern.Money<CurrencyItem> byItem)
	{
		MoneyFieldItem.byItem.set(this,byItem);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for moneyFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MoneyFieldItem> TYPE = com.exedio.cope.TypesBound.newType(MoneyFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MoneyFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

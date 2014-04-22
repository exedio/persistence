/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

public final class Money<C extends Money.Currency>
	implements Serializable // TODO Comparable
{
	private static final long serialVersionUID = 1l;

	/**
	 * Empty interface.
	 * Make sure, that {@link equals(Object)} and {@link #hashCode()} are
	 * implemented correctly.
	 * For good exception messages, {@link #toString()} should be implemented as well.
	 */
	public static interface Currency
	{
		// empty
	}

	// TODO currency deprecate
	public static <C extends Currency> Money<C> valueOf(final Price amount, final C currency)
	{
		return new Money<>(amount, currency);
	}

	public static <C extends Currency> Money<C> valueOf(final double amount, final C currency)
	{
		return valueOf(Price.valueOf(amount), currency);
	}

	public static <C extends Currency> Money<C> storeOf(final int amountStore, final C currency)
	{
		return valueOf(Price.storeOf(amountStore), currency);
	}

	public static <C extends Currency> Money<C> zero(final C currency)
	{
		return new Money<>(Price.ZERO, currency);
	}


	private final Price amount;
	private final C currency;

	private Money(final Price amount, final C currency)
	{
		this.amount = requireNonNull(amount, "amount");
		this.currency = requireNonNull(currency, "currency");
	}

	// TODO currency deprecate
	public Price getAmount(final Currency currency)
	{
		check(currency);
		return amount;
	}

	public double doubleValue(final Currency currency)
	{
		return getAmount(currency).doubleValue();
	}

	/**
	 * <b>BEWARE</b>:
	 * You might rather want to use {@link #getAmount(Currency)}.
	 */
	// TODO currency deprecate
	public Price getAmount()
	{
		return amount;
	}

	public C getCurrency()
	{
		return currency;
	}

	private Price unwrap(final Money<C> other)
	{
		check(other.currency);
		return other.amount;
	}

	private Money<C> wrap(final Price amount)
	{
		return valueOf(amount, currency);
	}

	private void check(final Currency currency)
	{
		if(!this.currency.equals(currency))
			throw new IllegalArgumentException("currency mismatch " + this + '/' + currency);
	}

	private static <C extends Money.Currency> Price[] unwrap(final C currency, final Money<C>[] value)
	{
		final Price[] result = new Price[value.length];
		for(int i = 0; i<value.length; i++)
			result[i] = value[i].getAmount(currency);
		return result;
	}

	private static <C extends Money.Currency> Money<C>[] wrap(final C currency, final Price[] value)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Money<C>[] result = new Money[value.length];
		for(int i = 0; i<result.length; i++)
			result[i] = valueOf(value[i], currency);
		return result;
	}

	public static final <CURRENCY extends Money.Currency> Money<CURRENCY>[] array(final int size)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Money<CURRENCY>[] result = new Money[size];
		return result;
	}


	// computation

	public boolean equalsZero()
	{
		return amount.equals(Price.ZERO);
	}

	public boolean lessThanZero()
	{
		return amount.lessThan(Price.ZERO);
	}

	public boolean lessThanOrEqual(final Money<C> other)
	{
		return amount.lessThanOrEqual(unwrap(other));
	}

	public boolean greaterThan(final Money<C> other)
	{
		return amount.greaterThan(unwrap(other));
	}

	public Money<C> negative()
	{
		return wrap( amount.negative() );
	}

	public Money<C> add(final Money<C> other)
	{
		return wrap( amount.add(unwrap(other)) );
	}

	public Money<C> subtract(final Money<C> other)
	{
		return wrap( amount.subtract(unwrap(other)) );
	}

	public Money<C> multiply(final int other)
	{
		return wrap( amount.multiply(other) );
	}

	public Money<C> multiply(final double other)
	{
		return wrap( amount.multiply(other) );
	}

	public Money<C> grossToNetPercent(final int rate)
	{
		return wrap( amount.grossToNetPercent(rate) );
	}

	public Money<C> grossToTaxPercent(final int rate)
	{
		return wrap( amount.grossToTaxPercent(rate) );
	}

	public static <C extends Money.Currency> Money<C>[] splitProportionately(final Money<C> total, final Money<C>[] weights)
	{
		final C currency = total.currency;
		return wrap(currency, Price.splitProportionately(total.getAmount(currency), unwrap(currency, weights)));
	}


	// identity

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Money<?>))
			return false;

		final Money<?> o = (Money<?>)other;
		return
				amount  .equals(o.amount  ) &&
				currency.equals(o.currency);
	}

	@Override
	public int hashCode()
	{
		return amount.hashCode() ^ currency.hashCode() ^ 2764712;
	}

	@Override
	public String toString()
	{
		return amount.toString() + currency.toString();
	}
}

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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.instrument.WrapImplementsInterim;
import com.exedio.cope.pattern.Money.Currency;
import java.io.Serializable;
import java.math.BigDecimal;

public final class Money<C extends Currency>
	implements Serializable, Comparable<Money<C>>
{
	/**
	 * Empty interface.
	 * Make sure, that {@link Object#equals(Object) equals} and {@link Object#hashCode() hashCode} are
	 * implemented correctly.
	 * {@link Object#toString() toString} should be implemented as well,
	 * otherwise {@link Money#toString() Money.toString} will not be useful.
	 */
	@SuppressWarnings("MarkerInterface") // OK: maybe we get methods in the future
	@WrapImplementsInterim(addMethods=true)
	public interface Currency
	{
		// empty
	}


	public static <C extends Currency> Money<C> storeOf(final long amountStore, final C currency)
	{
		return valueOf(Price.storeOf(amountStore), currency);
	}

	public static <C extends Currency> Money<C> storeOf(final Long amountStore, final C currency)
	{
		return amountStore!=null ? storeOf(amountStore.longValue(), currency) : null;
	}

	public static <C extends Currency> Money<C> storeOf(final Integer amountStore, final C currency)
	{
		return amountStore!=null ? storeOf(amountStore.intValue(), currency) : null;
	}


	private static final long serialVersionUID = 1l;
	private final Price amount;
	@SuppressWarnings("NonSerializableFieldInSerializableClass") // OK: container is serializable if part is serializable
	private final C currency;

	private Money(final Price amount, final C currency)
	{
		this.amount = requireNonNull(amount, "amount");
		this.currency = requireNonNull(currency, "currency");
	}

	public long amountStore(final C currency)
	{
		return getAmount(currency).store();
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
		if(this.amount.equals(amount))
			return this;

		return valueOf(amount, currency);
	}

	private Money<C> wrap(final Price amount, final Money<C> other)
	{
		if(this.amount.equals(amount))
			return this;
		if(other.amount.equals(amount))
			return other;

		return valueOf(amount, currency);
	}

	private void check(final Currency currency)
	{
		if(!this.currency.equals(currency))
			throw new IllegalArgumentException("currency mismatch " + this + '/' + currency);
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
		return amount.toString() + currency;
	}

	public String toStringShort()
	{
		return amount.toStringShort() + currency;
	}


	// zero

	public static <C extends Currency> Money<C> zero(final C currency)
	{
		return new Money<>(Price.ZERO, currency);
	}

	public static <C extends Currency> Money<C> nullToZero(final Money<C> value, final C currency)
	{
		if(value!=null)
			value.check(currency);

		return value!=null ? value : zero(currency);
	}


	// comparison

	@Override
	public int compareTo(final Money<C> other)
	{
		return amount.compareTo(unwrap(other));
	}

	public boolean equalsZero()
	{
		return amount.equalsZero();
	}

	public boolean lessThanZero()
	{
		return amount.lessThanZero();
	}

	public boolean greaterThanZero()
	{
		return amount.greaterThanZero();
	}

	public boolean lessThanOrEqualZero()
	{
		return amount.lessThanOrEqualZero();
	}

	public boolean greaterThanOrEqualZero()
	{
		return amount.greaterThanOrEqualZero();
	}

	public boolean lessThan(final Money<C> other)
	{
		return amount.lessThan(unwrap(other));
	}

	public boolean greaterThan(final Money<C> other)
	{
		return amount.greaterThan(unwrap(other));
	}

	public boolean lessThanOrEqual(final Money<C> other)
	{
		return amount.lessThanOrEqual(unwrap(other));
	}

	public boolean greaterThanOrEqual(final Money<C> other)
	{
		return amount.greaterThanOrEqual(unwrap(other));
	}

	public Money<C> min(final Money<C> other)
	{
		return wrap( amount.min(unwrap(other)), other );
	}

	public Money<C> max(final Money<C> other)
	{
		return wrap( amount.max(unwrap(other)), other );
	}


	// computation

	public Money<C> negate()
	{
		return wrap( amount.negate() );
	}

	public Money<C> add(final Money<C> other)
	{
		return wrap( amount.add(unwrap(other)), other );
	}

	public Money<C> subtract(final Money<C> other)
	{
		return wrap( amount.subtract(unwrap(other)), other );
	}

	public Money<C> multiply(final int other)
	{
		return wrap( amount.multiply(other) );
	}

	public Money<C> multiply(final double other)
	{
		return wrap( amount.multiply(other) );
	}

	public Money<C> divide(final double other)
	{
		return wrap( amount.divide(other) );
	}

	public Money<C> grossToNetPercent(final int rate)
	{
		return wrap( amount.grossToNetPercent(rate) );
	}

	public Money<C> grossToTaxPercent(final int rate)
	{
		return wrap( amount.grossToTaxPercent(rate) );
	}

	public Money<C> grossToTaxPercent(final double rate)
	{
		return wrap( amount.grossToTaxPercent(rate) );
	}

	public static <C extends Currency> Money<C>[] splitProportionately(final Money<C> total, final Money<C>[] weights)
	{
		final C currency = total.currency;
		return wrap(currency, Price.splitProportionately(total.getAmount(currency), unwrap(currency, weights)));
	}

	private static <C extends Currency> Price[] unwrap(final C currency, final Money<C>[] value)
	{
		final Price[] result = new Price[value.length];
		for(int i = 0; i<value.length; i++)
			result[i] = value[i].getAmount(currency);
		return result;
	}

	private static <C extends Currency> Money<C>[] wrap(final C currency, final Price[] value)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Money<C>[] result = new Money[value.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<result.length; i++)
			result[i] = valueOf(value[i], currency);
		return result;
	}

	public static <CURRENCY extends Currency> Money<CURRENCY>[] array(final int size)
	{
		@SuppressWarnings({"unchecked","rawtypes"})
		final Money<CURRENCY>[] result = new Money[size];
		return result;
	}


	// conversion double

	public static <C extends Currency> Money<C> valueOf(final double amount, final C currency)
	{
		return valueOf(Price.valueOf(amount), currency);
	}

	public double doubleAmount(final Currency currency)
	{
		return getAmount(currency).doubleValue();
	}


	// conversion BigDecimal

	public static <C extends Currency> Money<C> valueOf(final BigDecimal value, final C currency)
	{
		return valueOf(Price.valueOf(value), currency);
	}

	public BigDecimal bigAmount(final C currency)
	{
		return getAmount(currency).bigValue();
	}


	// conversion Price

	// TODO deprecate
	public static <C extends Currency> Money<C> valueOf(final Price amount, final C currency)
	{
		return new Money<>(amount, currency);
	}

	// TODO deprecate
	public Price getAmount(final Currency currency)
	{
		check(currency);
		return amount;
	}

	/**
	 * <b>BEWARE</b>:
	 * Use {@link #getAmount(Currency)} instead
	 */
	@Deprecated
	public Price getAmount()
	{
		return amount;
	}

	// TODO remove
	Price amountWithoutCurrency()
	{
		return amount;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #amountStore(Currency)} instead
	 */
	@Deprecated
	public long store(final C currency)
	{
		return amountStore(currency);
	}

	/**
	 * @deprecated Use {@link #doubleAmount(Currency)} instead
	 */
	@Deprecated
	public double doubleValue(final Currency currency)
	{
		return doubleAmount(currency);
	}

	/**
	 * @deprecated Use {@link #bigAmount(Currency)} instead
	 */
	@Deprecated
	public BigDecimal bigValue(final C currency)
	{
		return bigAmount(currency);
	}

	/**
	 * @deprecated Use {@link #min(Money)} instead
	 */
	@Deprecated
	public Money<C> getLower(final Money<C> other)
	{
		return min(other);
	}

	/**
	 * @deprecated Use {@link #max(Money)} instead
	 */
	@Deprecated
	public Money<C> getGreater(final Money<C> other)
	{
		return max(other);
	}

	/**
	 * @deprecated Use {@link #negate()} instead
	 */
	@Deprecated
	public Money<C> negative()
	{
		return negate();
	}
}

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

import com.exedio.cope.util.Check;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class Price implements Serializable, Comparable<Price>
{
	private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

	private static final int FACTOR_I = 100;

	private static final long NOT_A_STORE = Long.MIN_VALUE;
	private static final long MIN_STORE = Long.MIN_VALUE + 1l;
	private static final long MAX_STORE = Long.MAX_VALUE;

	public static final Price MIN_VALUE = new Price(MIN_STORE);
	public static final Price MAX_VALUE = new Price(MAX_STORE);


	// cache

	private static final int CACHE_MAX = 1001;

	private static final class Cache
	{
		static final Price[] value = newCache();

		private static Price[] newCache()
		{
			final Price[] result = new Price[CACHE_MAX];
			result[0] = Price.ZERO;
			for(int i = 1; i<CACHE_MAX; i++)
				result[i] = new Price(i);
			return result;
		}
	}

	private static Price fromCache(final long store)
	{
		return
			(0<=store && store<CACHE_MAX)
			? Cache.value[(int)store]
			: null;
	}


	// store

	public static Price storeOf(final long store)
	{
		{
			final Price fromCache = fromCache(store);
			if(fromCache!=null)
				return fromCache;
		}

		if(store==MIN_STORE)
			return MIN_VALUE;
		else if(store==MAX_STORE)
			return MAX_VALUE;
		else if(store==NOT_A_STORE)
			throw new IllegalArgumentException("Long.MIN_VALUE not allowed");

		return new Price(store);
	}

	public static Price storeOf(final Long store)
	{
		return store!=null ? storeOf(store.longValue()) : null;
	}

	public static Price storeOf(final Integer store)
	{
		return store!=null ? storeOf(store.intValue()) : null;
	}

	private static final long serialVersionUID = 2l;
	private final long store;

	Price(final long store)
	{
		this.store = store;
		assert store!=NOT_A_STORE;
	}

	public long store()
	{
		return store;
	}

	public int storeIntExact()
	{
		if(store<Integer.MIN_VALUE || store>Integer.MAX_VALUE)
			throw new ArithmeticException("not an integer: " + store);

		return (int)store;
	}


	// identity

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Price))
			return false;

		return store==((Price)other).store;
	}

	@Override
	public int hashCode()
	{
		return Long.hashCode(store) ^ 827345123;
	}

	/**
	 * Returns a string representation of this price.
	 *	The result has trailing zeros, such as "1.20" or "1.00".
	 * If you don't want trailing zeros, use {@link #toStringShort()} instead.
	 */
	@Override
	public String toString()
	{
		final long minor = Math.abs(store%FACTOR_I);
		return
			((store<0 && store>(-FACTOR_I)) ? "-" : "") +
			(store/FACTOR_I) + '.' +
			(minor<10?"0":"") +
			minor;
	}

	/**
	 * Returns a string representation of this price without trailing zeros.
	 * If you want trailing zeros, use {@link #toString()} instead.
	 */
	public String toStringShort()
	{
		final StringBuilder bf = new StringBuilder();
		if((store<0 && store>(-FACTOR_I)))
			bf.append('-');
		bf.append(store/FACTOR_I);
		final long minor = Math.abs(store%FACTOR_I);
		if(minor!=0)
		{
			bf.append('.');
			final long x = minor % 10;
			if(x==0)
				bf.append(minor/10);
			else
			{
				if(minor<=10)
					bf.append('0');
				bf.append(minor);
			}
		}

		return bf.toString();
	}

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
	 */
	private Object readResolve()
	{
		final Price fromCache = fromCache(store);
		return (fromCache!=null) ? fromCache : this;
	}


	// format / parse

	public String format(final NumberFormat format)
	{
		return format.format(bigValue());
	}

	public static Price parse(final String source, final DecimalFormat format) throws ParseException
	{
		if(!format.isParseBigDecimal())
			throw new IllegalArgumentException("format does not support BigDecimal");

		final BigDecimal bd = (BigDecimal)format.parse(source);

		try
		{
			return valueOf(bd, RoundingMode.UNNECESSARY);
		}
		catch(final IllegalArgumentException e)
		{
			throw new ParseException(e.getMessage(), 0);
		}
		catch(final ArithmeticException e)
		{
			throw new ParseException(e.getMessage() + ':' + bd, 0);
		}
	}


	// zero

	/**
	 * @see BigDecimal#ZERO
	 */
	public static final Price ZERO = new Price(0);

	public static Price nullToZero(final Price value)
	{
		return value!=null ? value : ZERO;
	}


	// comparison

	@Override
	public int compareTo(final Price other)
	{
		return Long.compare(store, other.store);
	}

	public boolean equalsZero()
	{
		return store==0;
	}

	public boolean lessThanZero()
	{
		return store<0;
	}

	public boolean greaterThanZero()
	{
		return store>0;
	}

	public boolean lessThanOrEqualZero()
	{
		return store<=0;
	}

	public boolean greaterThanOrEqualZero()
	{
		return store>=0;
	}

	public boolean lessThan(final Price other)
	{
		return store<other.store;
	}

	public boolean greaterThan(final Price other)
	{
		return store>other.store;
	}

	public boolean lessThanOrEqual(final Price other)
	{
		return store<=other.store;
	}

	public boolean greaterThanOrEqual(final Price other)
	{
		return store>=other.store;
	}

	/**
	 * @return this if this price is lower than the other one; otherwise the other one
	 * @see BigDecimal#min(BigDecimal)
	 */
	public Price min(final Price other)
	{
		return lessThan(other) ? this : other;
	}

	/**
	 * @return this if this price is greater than the other one; otherwise the other one
	 * @see BigDecimal#max(BigDecimal)
	 */
	public Price max(final Price other)
	{
		return greaterThan(other) ? this : other;
	}


	// computation

	/**
	 * @see BigDecimal#negate()
	 */
	public Price negate()
	{
		return storeOf(-store);
	}

	private static Price operate(
			final LongSupplier operation,
			final Supplier<String> exceptionMessage)
	{
		final long r;
		try
		{
			r = operation.getAsLong();
		}
		catch(final ArithmeticException ignored)
		{
			throw new ArithmeticException(exceptionMessage.get());
		}
		if(r==NOT_A_STORE)
			throw new ArithmeticException(exceptionMessage.get());

		return storeOf(r);
	}

	/**
	 * @see BigDecimal#add(BigDecimal)
	 */
	public Price add(final Price other)
	{
		final long b = other.store;
		if(b==0)
			return this;

		final long a = store;
		if(a==0)
			return other;

		return operate(
				() -> Math.addExact(a, b),
				() -> "overflow " + this + " plus " + other);
	}

	/**
	 * @see BigDecimal#subtract(BigDecimal)
	 */
	public Price subtract(final Price other)
	{
		final long b = other.store;
		if(b==0)
			return this;

		return operate(
				() -> Math.subtractExact(store, b),
				() -> "overflow " + this + " minus " + other);
	}

	/**
	 * @see BigDecimal#multiply(BigDecimal)
	 */
	public Price multiply(final int other)
	{
		if(other==1)
			return this;

		return operate(
				() -> Math.multiplyExact(store, other),
				() -> "overflow " + this + " multiply " + other);
	}

	/**
	 * @see BigDecimal#multiply(BigDecimal)
	 */
	public Price multiply(final double other)
	{
		return multiply(other, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @see BigDecimal#multiply(BigDecimal)
	 */
	public Price multiply(final double other, final RoundingMode roundingMode)
	{
		//noinspection FloatingPointEquality
		if(other==1.0)
			return this;

		return valueOf(bigValue().multiply(BigDecimal.valueOf(other)), roundingMode);
	}

	/**
	 * @see BigDecimal#divide(BigDecimal)
	 */
	public Price divide(final double other)
	{
		return divide(other, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @see BigDecimal#divide(BigDecimal)
	 */
	public Price divide(final double other, final RoundingMode roundingMode)
	{
		//noinspection FloatingPointEquality
		if(other==1.0)
			return this;
		//backward compatibility
		if(other==0.0)
			throw new IllegalArgumentException("Infinity not allowed");
		return valueOf(bigValue().divide(BigDecimal.valueOf(other), 2, roundingMode), RoundingMode.UNNECESSARY);
	}

	/**
	 * @see BigDecimal#divide(BigDecimal)
	 */
	private Price divide(final int other, final RoundingMode roundingMode)
	{
		if(other==1)
			return this;
		//backward compatibility
		if(other==0)
			throw new IllegalArgumentException("Infinity not allowed");
		return valueOf(bigValue().divide(new BigDecimal(other), 2, roundingMode), RoundingMode.UNNECESSARY);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToNetPercent(final int rate)
	{
		return grossToNetPercent(rate, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToNetPercent(final int rate, final RoundingMode roundingMode)
	{
		checkRatePercent(rate);

		// shortcut for computation below
		if(rate==0)
			return this;

		return multiply(100).divide(100 + rate, roundingMode);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToTaxPercent(final int rate)
	{
		return grossToTaxPercent(rate, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToTaxPercent(final double rate)
	{
		return grossToTaxPercent(rate, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToTaxPercent(final int rate, final RoundingMode roundingMode)
	{
		checkRatePercent(rate);

		// shortcut for computation below
		if(rate==0)
			return ZERO;

		return multiply(rate).divide(100 + rate, roundingMode);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToTaxPercent(final double rate, final RoundingMode roundingMode)
	{
		checkRatePercent(rate);

		// shortcut for computation below
		if(rate==0)
			return ZERO;

		return multiply(rate).divide(100d + rate, roundingMode);
	}

	private static void checkRatePercent(final int rate)
	{
		Check.requireNonNegative(rate, "rate");
	}

	private static void checkRatePercent(final double rate)
	{
		Check.requireNonNegative(rate, "rate");
	}

	/**
	 * @throws IllegalArgumentException if weights has length of 0.
	 */
	public static Price[] splitProportionately(final Price total, final Price[] weights)
	{
		if(weights.length==0)
			throw new IllegalArgumentException("weights must not be empty");

		Price weightSum = ZERO;
		for(final Price weight : weights)
		{
			if(weight.lessThanZero())
				throw new IllegalArgumentException("negative weight " + weight);

			weightSum = weightSum.add(weight);
		}
		// TODO
		// if weightSum=ZERO, the algorithm is very ineffective, as everything is done
		// via remainingPence-distribution

		Price assigned = ZERO;
		final Price[] result = new Price[weights.length];
		for(int i = 0; i < weights.length; i++)
		{
			final Price source = weights[i];
			// do not round here, remaining pence will be distributed below
			final Price x = storeOf((int)(total.store() * source.store() / (weightSum.store() * 1.0)));
			assigned = assigned.add(x);
			result[i] = x;
		}

		// distributing remaining pence
		long remainingPence = total.subtract(assigned).store();
		final Price pence = storeOf(remainingPence>0 ? 1 : -1);
		final int penceD = remainingPence>0 ? -1 : 1;
		while(remainingPence!=0)
		{
			for(int i = 0; i<weights.length && remainingPence!=0; i++)
			{
				result[i] = result[i].add(pence);
				remainingPence += penceD;
			}
		}

		return result;
	}


	// conversion double

	/**
	 * @see BigDecimal#valueOf(double)
	 */
	public static Price valueOf(final double value)
	{
		return valueOf(value, DEFAULT_ROUNDING_MODE);
	}

	/**
	 * @see BigDecimal#valueOf(double)
	 */
	public static Price valueOf(final double value, final RoundingMode roundingMode)
	{
		if(Double.isNaN(value))
			throw new IllegalArgumentException("NaN not allowed");
		if(Double.isInfinite(value))
			throw new IllegalArgumentException("Infinity not allowed");
		if(value<DOUBLE_MIN_VALUE)
			throw new IllegalArgumentException("too small: " + value);
		if(value>DOUBLE_MAX_VALUE)
			throw new IllegalArgumentException("too big: " + value);

		return valueOf(BigDecimal.valueOf(value), roundingMode);
	}

	/**
	 * @see BigDecimal#doubleValue()
	 */
	public double doubleValue()
	{
		return store / DOUBLE_FACTOR;
	}

	private static final double DOUBLE_FACTOR = 100d;
	private static final double DOUBLE_MIN_VALUE = MIN_STORE/DOUBLE_FACTOR;
	private static final double DOUBLE_MAX_VALUE = MAX_STORE/DOUBLE_FACTOR;

	// conversion BigDecimal

	public static Price valueOf(final BigDecimal value)
	{
		return valueOf(value, DEFAULT_ROUNDING_MODE);
	}

	public static Price valueOf(final BigDecimal value, final RoundingMode roundingMode)
	{
		if(value.compareTo(BIG_MIN_VALUE)<0)
			throw new IllegalArgumentException("too small: " + value);
		if(value.compareTo(BIG_MAX_VALUE)>0)
			throw new IllegalArgumentException("too big: " + value);

		return storeOf(value.movePointRight(2).setScale(0, roundingMode).longValueExact());
	}

	public BigDecimal bigValue()
	{
		if(store%10==0)
		{
			// create normalized BigDecimals
			if(store%100==0)
				return BigDecimal.valueOf(store/100, 0);
			else
				return BigDecimal.valueOf(store/10, 1);
		}
		else
			return BigDecimal.valueOf(store, 2);
	}

	private static final BigDecimal BIG_MIN_VALUE = BigDecimal.valueOf(MIN_STORE, 2);
	private static final BigDecimal BIG_MAX_VALUE = BigDecimal.valueOf(MAX_STORE, 2);
}

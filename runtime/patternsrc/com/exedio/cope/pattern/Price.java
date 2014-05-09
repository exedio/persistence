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

import com.exedio.cope.misc.Compare;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

// TODO cache of common small values
public final class Price implements Serializable, Comparable<Price>
{
	private static final int FACTOR_I = 100;

	public static final Price MIN_VALUE = new Price(Integer.MIN_VALUE);
	public static final Price MAX_VALUE = new Price(Integer.MAX_VALUE);

	public static Price storeOf(final int store)
	{
		// TODO reuse common small values
		switch(store)
		{
			case 0: return ZERO;
			case Integer.MIN_VALUE: return MIN_VALUE;
			case Integer.MAX_VALUE: return MAX_VALUE;
		}

		return new Price(store);
	}

	public static Price storeOf(final Integer store)
	{
		return store!=null ? storeOf(store.intValue()) : null;
	}

	private static final long serialVersionUID = 1l;
	private final int store;

	private Price(final int store)
	{
		this.store = store;
	}

	public int store()
	{
		return store;
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
		return store ^ 827345123;
	}

	/**
	 * Returns a string representation of this price.
	 *	The result has trailing zeros, such as "1.20" or "1.00".
	 * If you don't want trailing zeros, use {@link #toStringShort()} instead.
	 */
	@Override
	public String toString()
	{
		final int minor = Math.abs(store%FACTOR_I);
		return
			((store<0 && store>(-FACTOR_I)) ? "-" : "") +
			String.valueOf(store/FACTOR_I) + '.' +
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
		final int minor = Math.abs(store%FACTOR_I);
		if(minor!=0)
		{
			bf.append('.');
			final int x = minor % 10;
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


	// zero

	public static final Price ZERO = new Price(0);

	public static Price nullToZero(final Price value)
	{
		return value!=null ? value : ZERO;
	}


	// comparison

	public int compareTo(final Price other)
	{
		return Compare.compare(store, other.store);
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

	/** @return this if this price is lower than the other one; otherwise the other one */
	public Price getLower(final Price other)
	{
		return lessThan(other) ? this : other;
	}

	/** @return this if this price is greater than the other one; otherwise the other one */
	public Price getGreater(final Price other)
	{
		return greaterThan(other) ? this : other;
	}


	// computation

	public Price negative()
	{
		if(store==Integer.MIN_VALUE)
			throw new ArithmeticException("no negative for " + this);

		return storeOf(-store);
	}

	public Price add(final Price other)
	{
		// TODO shortcut for neutral element

		if(
			(store>0)
			? (Integer.MAX_VALUE-store<other.store)
			: (Integer.MIN_VALUE-store>other.store)
			)
		{
			throw new ArithmeticException("overflow " + this + " plus " + other);
		}

		return storeOf(store + other.store);
	}

	public Price subtract(final Price other)
	{
		// TODO shortcut for neutral element

		// TODO check overflow without using long
		{
			final long a = store;
			final long b = other.store;
			final long r = a - b;
			if(r>Integer.MAX_VALUE || r<Integer.MIN_VALUE)
				throw new ArithmeticException("overflow " + this + " minus " + other);
		}

		return storeOf(store - other.store);
	}

	public Price multiply(final int other)
	{
		// TODO shortcut for neutral element
		// TODO check overflow
		return storeOf(store * other);
	}

	public Price multiply(final double other)
	{
		// TODO shortcut for neutral element
		return valueOf(doubleValue() * other);
	}

	public Price divide(final double other)
	{
		// TODO shortcut for neutral element
		return valueOf(doubleValue() / other);
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToNetPercent(final int rate)
	{
		checkRatePercent(rate);

		// shortcut for computation below
		if(rate==0)
			return this;

		return valueOf(doubleValue() *        100d / (100 + rate));
	}

	/**
	 * @throws IllegalArgumentException if rate is negative
	 */
	public Price grossToTaxPercent(final int rate)
	{
		checkRatePercent(rate);

		// shortcut for computation below
		if(rate==0)
			return ZERO;

		return valueOf(doubleValue() * (1d - (100d / (100 + rate))));
	}

	private static void checkRatePercent(final int rate)
	{
		if(rate<0)
			throw new IllegalArgumentException("rate must not be negative, but was " + rate);
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
			final Price x = Price.storeOf((int)(total.store() * source.store() / (weightSum.store() * 1.0)));
			assigned = assigned.add(x);
			result[i] = x;
		}

		// distributing remaining pence
		int remainingPence = total.subtract(assigned).store();
		final Price pence = Price.storeOf(remainingPence>0 ? 1 : -1);
		final int penceD = remainingPence>0 ? -1 : 1;
		while(remainingPence!=0)
		{
			for(int i = 0; i<weights.length && remainingPence!=0; i++)
			{
				result[i] = result[i].add(pence);
				remainingPence += penceD;
			}
		}

		assert remainingPence==0 :
					String.valueOf(remainingPence) +
					'T' + total +
					Arrays.toString(weights) +
					Arrays.toString(result);

		return result;
	}


	// conversion double

	public static Price valueOf(final double value)
	{
		// TODO reuse common small values
		if(Double.isNaN(value))
			throw new IllegalArgumentException("NaN not allowed");
		if(Double.isInfinite(value))
			throw new IllegalArgumentException("Infinity not allowed");
		if(value<DOUBLE_MIN_VALUE)
			throw new IllegalArgumentException("too small: " + value);
		if(value>DOUBLE_MAX_VALUE)
			throw new IllegalArgumentException("too big: " + value);

		return storeOf((int)Math.rint(100d * value));
	}

	public double doubleValue()
	{
		return store / DOUBLE_FACTOR;
	}

	private static final double DOUBLE_FACTOR = 100d;
	private static final double DOUBLE_MIN_VALUE = Integer.MIN_VALUE/DOUBLE_FACTOR;
	private static final double DOUBLE_MAX_VALUE = Integer.MAX_VALUE/DOUBLE_FACTOR;


	// conversion BigDecimal

	public static Price valueOf(final BigDecimal value)
	{
		// TODO reuse common small values
		if(value.compareTo(BIG_MIN_VALUE)<0)
			throw new IllegalArgumentException("too small: " + value);
		if(value.compareTo(BIG_MAX_VALUE)>0)
			throw new IllegalArgumentException("too big: " + value);

		return storeOf(value.movePointRight(2).setScale(0, RoundingMode.HALF_EVEN).intValue());
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

	private static final BigDecimal BIG_MIN_VALUE = BigDecimal.valueOf(Integer.MIN_VALUE, 2);
	private static final BigDecimal BIG_MAX_VALUE = BigDecimal.valueOf(Integer.MAX_VALUE, 2);
}

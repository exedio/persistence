/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.exedio.cope.misc.Compare;

// TODO cache of common small values
public final class Price implements Serializable, Comparable<Price>
{
	private static final long serialVersionUID = 1l;

	private static final double FACTOR_D = 100d;
	private static final int    FACTOR_I = 100;
	private static final double MIN_VALUE_D = Integer.MIN_VALUE/FACTOR_D;
	private static final double MAX_VALUE_D = Integer.MAX_VALUE/FACTOR_D;
	private static final BigDecimal MIN_VALUE_B = BigDecimal.valueOf(Integer.MIN_VALUE, 2);
	private static final BigDecimal MAX_VALUE_B = BigDecimal.valueOf(Integer.MAX_VALUE, 2);

	public static final Price ZERO = new Price(0);
	public static final Price MIN_VALUE = new Price(Integer.MIN_VALUE);
	public static final Price MAX_VALUE = new Price(Integer.MAX_VALUE);

	final int store;

	Price(final int store)
	{
		this.store = store;
	}

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

	public static Price valueOf(final double value)
	{
		// TODO reuse common small values
		if(Double.isNaN(value))
			throw new IllegalArgumentException("NaN not allowed");
		if(Double.isInfinite(value))
			throw new IllegalArgumentException("Infinity not allowed");
		if(value<MIN_VALUE_D)
			throw new IllegalArgumentException("too small: " + value);
		if(value>MAX_VALUE_D)
			throw new IllegalArgumentException("too big: " + value);

		return storeOf(new BigDecimal(value).movePointRight(2).setScale(0, RoundingMode.HALF_EVEN).intValue()); // TODO manage without BigDecimal
	}

	public static Price valueOf(final BigDecimal value)
	{
		// TODO reuse common small values
		if(value.compareTo(MIN_VALUE_B)<0)
			throw new IllegalArgumentException("too small: " + value);
		if(value.compareTo(MAX_VALUE_B)>0)
			throw new IllegalArgumentException("too big: " + value);

		return storeOf(value.movePointRight(2).setScale(0, RoundingMode.HALF_EVEN).intValue());
	}

	public int store()
	{
		return store;
	}

	public double doubleValue()
	{
		return store / FACTOR_D;
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

	public Price add(final Price other)
	{
		// TODO shortcut for neutral element
		return storeOf(store + other.store);
	}

	public Price subtract(final Price other)
	{
		// TODO shortcut for neutral element
		return storeOf(store - other.store);
	}

	public Price multiply(final int other)
	{
		// TODO shortcut for neutral element
		return storeOf(store * other);
	}

	public Price multiply(final double other)
	{
		// TODO shortcut for neutral element
		return valueOf(doubleValue() * other);
	}

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

	public int compareTo(final Price o)
	{
		return Compare.compare(store, o.store);
	}
	
	public boolean lessThan(final Price o)
	{
		return store<o.store;
	}

	public boolean greaterThan(final Price o)
	{
		return store>o.store;
	}

	public boolean lessThanOrEqual(final Price o)
	{
		return store<=o.store;
	}

	public boolean greaterThanOrEqual(final Price o)
	{
		return store>=o.store;
	}
	
	/** @return this if this price is lower than the other one; otherwise the other one */
	public Price getLower(final Price o)
	{
		if ( lessThan(o) )
		{
			return this;
		}
		else
		{
			return o;
		}
	}

	/** @return this if this price is greater than the other one; otherwise the other one */
	public Price getGreater(final Price o)
	{
		if ( greaterThan(o) )
		{
			return this;
		}
		else
		{
			return o;
		}
	}
	
	@Override
	public String toString()
	{
		final int minor = Math.abs(store%FACTOR_I);
		return ((store<0 && store>(-FACTOR_I)) ? "-" : "") + String.valueOf(store/FACTOR_I) + '.' + (minor<10?"0":"") + minor;
	}
}

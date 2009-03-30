/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class Price implements Serializable
{
	private static final long serialVersionUID = 1l;
	
	private static final double FACTOR_D = 100d;
	private static final int    FACTOR_I = 100;
	private static final double MIN_VALUE_D = Integer.MIN_VALUE/FACTOR_D;
	private static final double MAX_VALUE_D = Integer.MAX_VALUE/FACTOR_D;
	private static final BigDecimal MIN_VALUE_B = BigDecimal.valueOf(Integer.MIN_VALUE, 2);
	private static final BigDecimal MAX_VALUE_B = BigDecimal.valueOf(Integer.MAX_VALUE, 2);
	
	public static final Price ZERO = new Price(0);
	
	final int store;
	
	Price(final int store)
	{
		this.store = store;
	}
	
	public static Price storeOf(final int store)
	{
		if(store==0)
			return ZERO;
		
		return new Price(store);
	}
	
	public static Price storeOf(final Integer store)
	{
		return store!=null ? storeOf(store.intValue()) : null;
	}
	
	public static Price valueOf(final double value)
	{
		if(Double.isNaN(value))
			throw new IllegalArgumentException("NaN not allowed");
		if(Double.isInfinite(value))
			throw new IllegalArgumentException("Infinity not allowed");
		if(value<MIN_VALUE_D)
			throw new IllegalArgumentException("too small: " + value);
		if(value>MAX_VALUE_D)
			throw new IllegalArgumentException("too big: " + value);
		
		return storeOf(new BigDecimal(value).movePointRight(2).setScale(0, RoundingMode.HALF_EVEN).intValue());
	}
	
	public static Price valueOf(final BigDecimal value)
	{
		if(value.compareTo(MIN_VALUE_B)<0)
			throw new IllegalArgumentException("too small: " + value);
		if(value.compareTo(MAX_VALUE_B)>0)
			throw new IllegalArgumentException("too big: " + value);
		
		return storeOf(value.movePointRight(2).setScale(0, RoundingMode.HALF_EVEN).intValue());
	}
	
	int store()
	{
		return store;
	}
	
	public double doubleValue()
	{
		return store / FACTOR_D;
	}
	
	public Price add(final Price other)
	{
		return storeOf(store + other.store);
	}
	
	public Price multiply(final int other)
	{
		return storeOf(store * other);
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
	
	@Override
	public String toString()
	{
		final int minor = Math.abs(store%FACTOR_I);
		return ((store<0 && store>(-FACTOR_I)) ? "-" : "") + String.valueOf(store/FACTOR_I) + '.' + (minor<10?"0":"") + minor;
	}
}

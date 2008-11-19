/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class Price implements Serializable
{
	private static final long serialVersionUID = 1l;
	
	private static final double FACTOR_D = 100d;
	private static final int    FACTOR_I = 100;
	private static final double MIN_VALUE_D = Integer.MIN_VALUE;
	private static final double MAX_VALUE_D = Integer.MAX_VALUE;
	
	public static final Price ZERO = new Price(0);
	
	final int value;
	
	public Price(final int value)
	{
		if(value<0)
			throw new RuntimeException("negative not yet supported: " + value); // TODO
		
		this.value = value;
	}
	
	public static final Price valueOf(final int value)
	{
		if(value==0)
			return ZERO;
		
		return new Price(value);
	}
	
	public static final Price valueOf(final Integer value)
	{
		return value!=null ? valueOf(value.intValue()) : null;
	}
	
	public static final Price valueOf(final double value)
	{
		if(Double.isNaN(value))
			throw new IllegalArgumentException("NaN not allowed");
		if(Double.isInfinite(value))
			throw new IllegalArgumentException("infinite not allowed");
		if(value<MIN_VALUE_D)
			throw new IllegalArgumentException("too small: " + value);
		if(value>MAX_VALUE_D)
			throw new IllegalArgumentException("too big: " + value);
		
		return valueOf((int)Math.round(value * FACTOR_D));
	}
	
	public int value()
	{
		return value;
	}
	
	public double doubleValue()
	{
		return value / FACTOR_D;
	}
	
	public Price add(final Price other)
	{
		return valueOf(value + other.value);
	}
	
	public Price multiply(final int other)
	{
		return valueOf(value * other);
	}
	
	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Price))
			return false;
		
		return value==((Price)other).value;
	}
	
	@Override
	public int hashCode()
	{
		return value ^ 827345123;
	}
	
	@Override
	public String toString()
	{
		final int minor = value%FACTOR_I;
		return String.valueOf(value/FACTOR_I) + '.' + (minor<10?"0":"") + minor;
	}
}

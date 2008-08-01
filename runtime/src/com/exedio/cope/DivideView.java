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

package com.exedio.cope;

public final class DivideView<E extends Number> extends NumberView<E> implements NumberFunction<E>
{
	private final NumberFunction<E> dividend;
	private final NumberFunction<E> divisor;
	
	/**
	 * Creates a new MultiplyView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods.
	 * @see NumberFunction#divide(NumberFunction)
	 */
	public DivideView(final NumberFunction<E> dividend, final NumberFunction<E> divisor)
	{
		super(new NumberFunction[]{dividend, divisor}, "divide", dividend.getValueClass());
		
		if(dividend==null)
			throw new NullPointerException("dividend function must not be null");
		if(divisor==null)
			throw new NullPointerException("divisor function must not be null");
		
		this.dividend = dividend;
		this.divisor = divisor;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==2;
		final Number dividend = (Number)sourceValues[0];
		if(dividend==null)
			return null;
		final Number divisor = (Number)sourceValues[1];
		if(divisor==null)
			return null;
		final Class<E> vc = valueClass;
		if(valueClass==Integer.class)
		{
			return (E)Integer.valueOf(((Integer)dividend).intValue() / ((Integer)divisor).intValue());
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf(((Long)dividend).longValue() / ((Long)divisor).longValue());
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf(((Double)dividend).doubleValue() / ((Double)divisor).doubleValue());
		}
		else
			throw new RuntimeException(vc.getName());
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append('(');
		if(valueClass==Double.class)
		{
			bf.append(dividend, join).
				append('/').
				append(divisor, join);
		}
		else
		{
			bf.appendIntegerDivisionOperator(dividend, divisor, join);
		}
		bf.append(')');
	}
}

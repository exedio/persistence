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

package com.exedio.cope;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class DivideView<E extends Number> extends NumberView<E>
{
	public static final <E extends Number> DivideView<E> divide(final Function<E> dividend, final Function<E> divisor)
	{
		return new DivideView<>(dividend, divisor);
	}


	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings("SE_BAD_FIELD") // TODO Function should implement Serializable
	private final Function<E> dividend;
	@SuppressFBWarnings("SE_BAD_FIELD") // TODO Function should implement Serializable
	private final Function<E> divisor;

	private DivideView(final Function<E> dividend, final Function<E> divisor)
	{
		super(new Function<?>[]{dividend, divisor}, "divide", PlusView.checkClass(Number.class, dividend.getValueClass()));

		this.dividend = dividend;
		this.divisor = divisor;
	}

	public SelectType<E> getValueType()
	{
		return dividend.getValueType();
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
			bf.dialect.appendIntegerDivision(bf, dividend, divisor, join);
		}
		bf.append(')');
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #DivideView(Function,Function)} instead.
	 */
	@Deprecated
	public DivideView(final NumberFunction<E> dividend, final NumberFunction<E> divisor)
	{
		this((Function<E>)dividend, (Function<E>)divisor);
	}
}

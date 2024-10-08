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

import java.io.Serial;

public final class DivideView<E extends Number> extends NumberView<E>
{
	public static <E extends Number> DivideView<E> divide(final Function<E> dividend, final Function<E> divisor)
	{
		return new DivideView<>(dividend, divisor);
	}


	@Serial
	private static final long serialVersionUID = 1l;

	private final Function<E> dividend;
	private final Function<E> divisor;

	private DivideView(final Function<E> dividend, final Function<E> divisor)
	{
		super(new Function<?>[]{dividend, divisor}, "divide", PlusView.checkClass(Number.class, dividend.getValueClass()));

		this.dividend = dividend;
		this.divisor = divisor;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<E> getValueType()
	{
		return dividend.getValueType();
	}

	@Override
	public NumberFunction<E> bind(final Join join)
	{
		return new DivideView<>(dividend.bind(join), divisor.bind(join));
	}

	@Override
	@SuppressWarnings("unchecked")
	public E mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==2;
		final Number dividend = (Number)sourceValues[0];
		if(dividend==null)
			return null;
		final Number divisor = (Number)sourceValues[1];
		if(divisor==null)
			return null;

		if(valueClass==Integer.class)
		{
			return (E)Integer.valueOf((Integer)dividend / (Integer)divisor);
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf((Long)dividend / (Long)divisor);
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf((Double)dividend / (Double)divisor);
		}
		else
			throw new RuntimeException(valueClass.getName());
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
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
}

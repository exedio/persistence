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

package com.exedio.cope;

public final class MinusView<E extends Number> extends NumberView<E>
{
	public static final <E extends Number> MinusView<E> minus(final Function<E> minuend, final Function<E> subtrahend)
	{
		return new MinusView<E>(minuend, subtrahend);
	}


	private static final long serialVersionUID = 1l;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Function<E> minuend;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Function<E> subtrahend;

	private MinusView(final Function<E> minuend, final Function<E> subtrahend)
	{
		super(new Function<?>[]{minuend, subtrahend}, "minus", PlusView.checkClass(Number.class, minuend.getValueClass()));

		this.minuend = minuend;
		this.subtrahend = subtrahend;
	}

	public SelectType<E> getValueType()
	{
		return minuend.getValueType();
	}

	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==2;
		final Number minuend = (Number)sourceValues[0];
		if(minuend==null)
			return null;
		final Number subtrahend = (Number)sourceValues[1];
		if(subtrahend==null)
			return null;
		final Class<E> vc = valueClass;
		if(valueClass==Integer.class)
		{
			return (E)Integer.valueOf(((Integer)minuend).intValue() - ((Integer)subtrahend).intValue());
		}
		else if(valueClass==Long.class)
		{
			return (E)Long.valueOf(((Long)minuend).longValue() - ((Long)subtrahend).longValue());
		}
		else if(valueClass==Double.class)
		{
			return (E)Double.valueOf(((Double)minuend).doubleValue() - ((Double)subtrahend).doubleValue());
		}
		else
			throw new RuntimeException(vc.getName());
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		bf.append('(').
			append(minuend, join).
			append('-').
			append(subtrahend, join).
			append(')');
	}
}

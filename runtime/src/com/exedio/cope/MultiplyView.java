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

public final class MultiplyView<E extends Number> extends NumberView<E>
{
	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic arrays
	public static <E extends Number> MultiplyView<E> multiply(final Function<E> multiplier1, final Function<E> multiplier2)
	{
		return new MultiplyView<>(new Function[]{multiplier1, multiplier2});
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic arrays
	public static <E extends Number> MultiplyView<E> multiply(final Function<E> multiplier1, final Function<E> multiplier2, final Function<E> multiplier3)
	{
		return new MultiplyView<>(new Function[]{multiplier1, multiplier2, multiplier3});
	}


	private static final long serialVersionUID = 1l;

	private final Function<E>[] multipliers;

	private MultiplyView(final Function<E>[] multipliers)
	{
		super(multipliers, "multiply", PlusView.checkClass(Number.class, PlusView.valueClass(multipliers)));
		this.multipliers = com.exedio.cope.misc.Arrays.copyOf(multipliers);
	}

	@Override
	public SelectType<E> getValueType()
	{
		return PlusView.selectType(multipliers);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E mapJava(final Object[] sourceValues)
	{
		if(valueClass==Integer.class)
		{
			int result = 1;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result *= (Integer)sourceValue;
			}
			return (E)Integer.valueOf(result);
		}
		else if(valueClass==Long.class)
		{
			long result = 1l;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result *= (Long)sourceValue;
			}
			return (E)Long.valueOf(result);
		}
		else if(valueClass==Double.class)
		{
			double result = 1.0;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result *= (Double)sourceValue;
			}
			return (E)Double.valueOf(result);
		}
		else
			throw new RuntimeException(valueClass.getName());
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.append('(');
		for(int i = 0; i<multipliers.length; i++)
		{
			if(i>0)
				bf.append('*');
			bf.append(multipliers[i], join);
		}
		bf.append(')');
	}


	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link MultiplyView#MultiplyView(Function[])} instead.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Deprecated
	public MultiplyView(final NumberFunction[] multipliers)
	{
		this((Function<E>[])multipliers);
	}
}

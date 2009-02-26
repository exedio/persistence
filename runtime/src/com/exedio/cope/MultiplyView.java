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

package com.exedio.cope;

public final class MultiplyView<E extends Number> extends NumberView<E> implements NumberFunction<E>
{
	private final NumberFunction[] multipliers;
	
	/**
	 * Creates a new MultiplyView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods.
	 * @see NumberFunction#multiply(NumberFunction)
	 * @see Cope#multiply(NumberFunction,NumberFunction)
	 * @see Cope#multiply(NumberFunction,NumberFunction,NumberFunction)
	 */
	@SuppressWarnings("unchecked")
	public MultiplyView(final NumberFunction[] multipliers)
	{
		super(multipliers, "multiply", PlusView.valueClass(multipliers));
		this.multipliers = multipliers;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final E mapJava(final Object[] sourceValues)
	{
		final Class<E> vc = valueClass;
		if(valueClass==Integer.class)
		{
			int result = 1;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result *= ((Integer)sourceValue).intValue();
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
				result *= ((Long)sourceValue).longValue();
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
				result *= ((Double)sourceValue).doubleValue();
			}
			return (E)Double.valueOf(result);
		}
		else
			throw new RuntimeException(vc.getName());
	}

	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
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
}

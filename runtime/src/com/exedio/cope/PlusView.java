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


public final class PlusView<E extends Number> extends IntegerView<E> implements IntegerFunction<E>
{
	private final IntegerFunction[] addends;
	
	private static Class valueClass(final IntegerFunction[] sources)
	{
		final Class result = sources[0].getValueClass();
		for(int i = 1; i<sources.length; i++)
			if(!result.equals(sources[i].getValueClass()))
				throw new RuntimeException(result.getName()+'/'+sources[i].getValueClass().getName()+'/'+i);
		return result;
	}

	/**
	 * Creates a new PlusView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper methods.
	 * @see IntegerFunction#plus(IntegerFunction)
	 * @see Cope#plus(IntegerFunction,IntegerFunction)
	 * @see Cope#plus(IntegerFunction,IntegerFunction,IntegerFunction)
	 */
	public PlusView(final IntegerFunction[] addends)
	{
		super(addends, "plus", valueClass(addends));
		this.addends = addends;
	}
	
	@Override
	public final E mapJava(final Object[] sourceValues)
	{
		final Class<E> vc = valueClass;
		if(valueClass==Integer.class)
		{
		int result = 0;
		for(int i=0; i<sourceValues.length; i++)
		{
			if(sourceValues[i]==null)
				return null;
			result += ((Integer)sourceValues[i]).intValue();
		}
		return (E)Integer.valueOf(result);
		}
		else if(valueClass==Long.class)
		{
			long result = 0;
			for(int i=0; i<sourceValues.length; i++)
			{
				if(sourceValues[i]==null)
					return null;
				result += ((Long)sourceValues[i]).longValue();
			}
			return (E)Long.valueOf(result);
		}
		else if(valueClass==Double.class)
		{
			double result = 0;
			for(int i=0; i<sourceValues.length; i++)
			{
				if(sourceValues[i]==null)
					return null;
				result += ((Double)sourceValues[i]).doubleValue();
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
		for(int i = 0; i<addends.length; i++)
		{
			if(i>0)
				bf.append('+');
			bf.append(addends[i], join);
		}
		bf.append(')');
	}
}

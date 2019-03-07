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

public final class PlusView<E extends Number> extends NumberView<E>
{
	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic arrays
	public static <E extends Number> PlusView<E> plus(final Function<E> addend1, final Function<E> addend2)
	{
		return new PlusView<>(new Function[]{addend1, addend2});
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: no generic arrays
	public static <E extends Number> PlusView<E> plus(final Function<E> addend1, final Function<E> addend2, final Function<E> addend3)
	{
		return new PlusView<>(new Function[]{addend1, addend2, addend3});
	}


	private static final long serialVersionUID = 1l;

	private final Function<E>[] addends;

	static <E> Class<E> valueClass(final Function<E>[] sources)
	{
		final Class<E> result = sources[0].getValueClass();
		for(int i = 1; i<sources.length; i++)
			if(!result.equals(sources[i].getValueClass()))
				throw new RuntimeException(result.getName()+'/'+sources[i].getValueClass().getName()+'/'+i);
		return result;
	}

	static <E> SelectType<E> selectType(final Function<E>[] sources)
	{
		final SelectType<E> result = sources[0].getValueType();
		for(int i = 1; i<sources.length; i++)
			if(result!=sources[i].getValueType())
				throw new RuntimeException(result.toString()+'/'+sources[i].getValueType()+'/'+i);
		return result;
	}

	static <E> Class<E> checkClass(final Class<? super E> limit, final Class<E> clazz)
	{
		if(!limit.isAssignableFrom(clazz))
			throw new ClassCastException(clazz.getName());

		return clazz;
	}

	private PlusView(final Function<E>[] addends)
	{
		super(addends, "plus", checkClass(Number.class, valueClass(addends)));
		this.addends = com.exedio.cope.misc.Arrays.copyOf(addends);
	}

	@Override
	public SelectType<E> getValueType()
	{
		return selectType(addends);
	}

	@Override
	@SuppressWarnings("unchecked")
	public E mapJava(final Object[] sourceValues)
	{
		if(valueClass==Integer.class)
		{
			int result = 0;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result += (Integer)sourceValue;
			}
			return (E)Integer.valueOf(result);
		}
		else if(valueClass==Long.class)
		{
			long result = 0;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result += (Long)sourceValue;
			}
			return (E)Long.valueOf(result);
		}
		else if(valueClass==Double.class)
		{
			double result = 0;
			for(final Object sourceValue : sourceValues)
			{
				if(sourceValue==null)
					return null;
				result += (Double)sourceValue;
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
		for(int i = 0; i<addends.length; i++)
		{
			if(i>0)
				bf.append('+');
			bf.append(addends[i], join);
		}
		bf.append(')');
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link PlusView#PlusView(Function[])} instead.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Deprecated
	public PlusView(final NumberFunction[] addends)
	{
		this((Function<E>[])addends);
	}
}

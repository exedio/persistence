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

import com.exedio.cope.misc.EnumAnnotatedElement;
import gnu.trove.TIntObjectHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

final class EnumFieldType<E extends Enum<E>> implements SelectType<E>
{
	private final Class<E> valueClass;
	final List<E> values;
	private final TIntObjectHashMap<E> numbersToValues;
	private final int[] ordinalsToNumbers;
	final EnumMarshaller<E> marshaller;

	private EnumFieldType(final Class<E> valueClass)
	{
		this.valueClass = valueClass;
		assert valueClass.isEnum() : valueClass;

		final TIntObjectHashMap<E> numbersToValues = new TIntObjectHashMap<>();

		final E[] enumConstants = valueClass.getEnumConstants();
		if(enumConstants==null)
			throw new RuntimeException("must have at least one enum value: " + valueClass);
		final int[] ordinalsToNumbers = new int[enumConstants.length];

		int schemaValue = 0;
		for(final E e : enumConstants)
		{
			final CopeSchemaValue annotation = schemaValue(e);
			schemaValue = annotation!=null ? annotation.value() : roundUpTo10(schemaValue+1);
			numbersToValues.put(schemaValue, e);
			ordinalsToNumbers[e.ordinal()] = schemaValue;
		}
		final int l = ordinalsToNumbers.length-1;
		int i = 0;
		for(final E e : enumConstants)
		{
			if(schemaValue(e)!=null)
			{
				if((i>0 && ordinalsToNumbers[i]<=ordinalsToNumbers[i-1]) ||
					(i<l && ordinalsToNumbers[i]>=ordinalsToNumbers[i+1]))
				{
					final StringBuilder bf = new StringBuilder();
					bf.append(valueClass.getName()).
						append(": @").
						append(CopeSchemaValue.class.getSimpleName()).
						append(" for ").
						append(e.name()).
						append(" must be");
					if(i>0)
						bf.append(" greater than ").append(ordinalsToNumbers[i-1]);
					if(i>0 && i<ordinalsToNumbers.length-1)
						bf.append(" and");
					if(i<ordinalsToNumbers.length-1)
						bf.append(" less than ").append(ordinalsToNumbers[i+1]);
					bf.append(", but was ").
						append(ordinalsToNumbers[i]).
						append('.');
					throw new IllegalArgumentException(bf.toString());
				}
			}
			i++;
		}
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(Arrays.asList(enumConstants));
		this.numbersToValues = numbersToValues;
		this.ordinalsToNumbers = ordinalsToNumbers;
		this.marshaller = new EnumMarshaller<>(this);
	}

	static int roundUpTo10(final int n)
	{
		final int mod = n % 10;
		if(mod==0)
			return n;

		return n - mod + 10;
	}

	private static final CopeSchemaValue schemaValue(final Enum<?> e)
	{
		return EnumAnnotatedElement.get(e).getAnnotation(CopeSchemaValue.class);
	}

	public Class<E> getJavaClass()
	{
		return valueClass;
	}

	boolean isValid(final E value)
	{
		if(value==null)
			return true;

		final Class<?> actualValueClass = value.getClass();
      return actualValueClass == valueClass || actualValueClass.getSuperclass() == valueClass;
	}

	int getNumber(final E value)
	{
		if(!isValid(value))
			throw new IllegalArgumentException(
					"expected " + valueClass.getName() +
					", but was a " + value.getClass().getName());
		return ordinalsToNumbers[value.ordinal()];
	}

	E getValueByNumber(final int number)
	{
		final E result = numbersToValues.get(number);
		if(result==null)
			throw new RuntimeException(valueClass.getName() + '/' + number);
		return result;
	}

	boolean isSingle()
	{
		return ordinalsToNumbers.length==1;
	}

	int[] getNumbers()
	{
		return com.exedio.cope.misc.Arrays.copyOf(ordinalsToNumbers);
	}

	@Override
	public String toString()
	{
		return valueClass.getName();
	}

	// static registry

	private static final HashMap<Class<?>, EnumFieldType<?>> types = new HashMap<>();

	static final <E extends Enum<E>> EnumFieldType<E> get(final Class<E> valueClass)
	{
		if(!valueClass.isEnum())
			throw new IllegalArgumentException("not an enum: " + valueClass);

		synchronized(types)
		{
			@SuppressWarnings({"unchecked", "rawtypes"})
			EnumFieldType<E> result = (EnumFieldType)types.get(valueClass);
			if(result==null)
			{
				result = new EnumFieldType<>(valueClass);
				types.put(valueClass, result);
			}
			return result;
		}
	}
}

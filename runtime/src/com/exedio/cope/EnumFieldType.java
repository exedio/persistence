/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import gnu.trove.TIntObjectHashMap;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

final class EnumFieldType<E extends Enum<E>> implements SelectType<E>
{
	private final Class<E> valueClass;
	final List<E> values;
	private final TIntObjectHashMap<E> numbersToValues;
	final int[] ordinalsToNumbers;
	final EnumMarshaller<E> marshaller;

	private EnumFieldType(final Class<E> valueClass)
	{
		this.valueClass = valueClass;
		assert valueClass.isEnum() : valueClass;

		final TIntObjectHashMap<E> numbersToValues = new TIntObjectHashMap<E>();

		final E[] enumConstants = valueClass.getEnumConstants();
		if(enumConstants==null)
			throw new RuntimeException("must have at least one enum value: " + valueClass);
		final int[] ordinalsToNumbers = new int[enumConstants.length];

		int schemaValue = 0;
		for(final E e : enumConstants)
		{
			final CopeSchemaValue annotation = getAnnotation(e, CopeSchemaValue.class);
			final int number = annotation!=null ? annotation.value() : (schemaValue+=10);
			numbersToValues.put(number, e);
			ordinalsToNumbers[e.ordinal()] = number;
		}
		final int l = ordinalsToNumbers.length-1;
		int i = 0;
		for(final E e : enumConstants)
		{
			if(getAnnotation(e, CopeSchemaValue.class)!=null)
			{
				if((i>0 && ordinalsToNumbers[i]<=ordinalsToNumbers[i-1]) ||
					(i<l && ordinalsToNumbers[i]>=ordinalsToNumbers[i+1]))
				{
					final StringBuilder bf = new StringBuilder();
					bf.append(valueClass.getName()).
						append(": @CopeSchemaValue for ").
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
		this.marshaller = new EnumMarshaller<E>(this);
	}

	private static final <A extends Annotation> A getAnnotation(final Enum e, final Class<A> annotationClass)
	{
		try
		{
			return e.getDeclaringClass().getDeclaredField(e.name()).getAnnotation(annotationClass);
		}
		catch(final NoSuchFieldException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public Class<E> getJavaClass()
	{
		return valueClass;
	}

	boolean isValid(final E value)
	{
		if(value==null)
			return true;

		final Class actualValueClass = value.getClass();
      return actualValueClass == valueClass || actualValueClass.getSuperclass() == valueClass;
	}

	int columnValue(final E value)
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

	@Override
	public String toString()
	{
		return valueClass.getName();
	}

	// static registry

	private static final HashMap<Class, EnumFieldType> types = new HashMap<Class, EnumFieldType>();

	static final <E extends Enum<E>> EnumFieldType<E> get(final Class<E> valueClass)
	{
		if(!valueClass.isEnum())
			throw new IllegalArgumentException("not an enum: " + valueClass);

		synchronized(types)
		{
			@SuppressWarnings("unchecked")
			EnumFieldType<E> result = types.get(valueClass);
			if(result==null)
			{
				result = new EnumFieldType<E>(valueClass);
				types.put(valueClass, result);
			}
			return result;
		}
	}
}

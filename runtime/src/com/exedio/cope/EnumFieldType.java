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

import gnu.trove.TIntObjectHashMap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class EnumFieldType<E extends Enum<E>>
{
	private final Class<E> valueClass;
	final List<E> values;
	final TIntObjectHashMap<E> numbersToValues;
	final int[] ordinalsToNumbers;
	
	EnumFieldType(final Class<E> valueClass)
	{
		this.valueClass = valueClass;
		if(!valueClass.isEnum())
			throw new RuntimeException("must be an enum: " + valueClass);

		// TODO compute all this once for an Enum class, as in Composite.Type
		final ArrayList<E> values = new ArrayList<E>();
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
			values.add(e);
			numbersToValues.put(number, e);
			ordinalsToNumbers[e.ordinal()] = number;
		}
		final int l = ordinalsToNumbers.length-1;
		int i = 0;
		for(final E e : values)
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
		values.trimToSize();
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(values);
		this.numbersToValues = numbersToValues;
		this.ordinalsToNumbers = ordinalsToNumbers;
	}
	
	private static final <T extends Annotation> T getAnnotation(final Enum e, Class<T> annotationClass)
	{
		try
		{
			return e.getDeclaringClass().getField(e.name()).getAnnotation(annotationClass);
		}
		catch(NoSuchFieldException ex)
		{
			throw new RuntimeException(ex);
		}
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
}

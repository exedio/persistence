/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

public final class EnumAttribute<E extends Enum> extends FunctionAttribute<E>
{
	private final List<E> values;
	private final IntKeyOpenHashMap numbersToValues;
	private final HashMap<E, Integer> valuesToNumbers;
	private final HashMap<String, E> codesToValues;
	
	private EnumAttribute(final boolean isfinal, final boolean optional, final boolean unique, final Class<E> valueClass, final E defaultConstant)
	{
		super(isfinal, optional, unique, valueClass, defaultConstant);
		checkValueClass(Enum.class);

		final ArrayList<E> values = new ArrayList<E>();
		final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
		final HashMap<E, Integer> valuesToNumbers = new HashMap<E, Integer>();
		final HashMap<String, E> codesToValues = new HashMap<String, E>();
		
		final E[] enumConstants = valueClass.getEnumConstants();
		if(enumConstants==null)
			throw new RuntimeException("must have at least one enum value: " + valueClass);
		
		for(int j = 0; j<enumConstants.length; j++)
		{
			final E enumConstant = enumConstants[j];
			final String code = enumConstant.name();
			final int number = (enumConstant.ordinal() + 1) * 10;
			values.add(enumConstant);

			if(numbersToValues.put(number, enumConstant)!=null)
				throw new RuntimeException("duplicate number " + number + " for enum attribute on " + valueClass);
			if(valuesToNumbers.put(enumConstant, number)!=null)
				throw new RuntimeException("duplicate value " + enumConstant + " for enum attribute on " + valueClass);
				
			if(codesToValues.put(code, enumConstant)!=null)
				throw new RuntimeException("duplicate code " + code + " for enum attribute on " + valueClass);
		}
		values.trimToSize();
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(values);
		this.numbersToValues = numbersToValues;
		this.valuesToNumbers = valuesToNumbers;
		this.codesToValues = codesToValues;
		
		checkDefaultValue();
	}
	
	public EnumAttribute(final Option option, final Class<E> valueClass)
	{
		this(option.isFinal, option.optional, option.unique, valueClass, null);
	}
	
	public FunctionAttribute<E> copyFunctionAttribute()
	{
		return new EnumAttribute<E>(isfinal, optional, implicitUniqueConstraint!=null, valueClass, defaultConstant);
	}
	
	/* TODO does not work with the instrumentor yet
	public EnumAttribute<E> defaultTo(final E defaultConstant)
	{
		return new EnumAttribute<E>(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant);
	}
	*/
	
	public List<E> getValues()
	{
		assert values!=null;
		return values;
	}
	
	private E getValue(final int number)
	{
		final E result = cast(numbersToValues.get(number));
		assert result!=null : toString() + number;
		return result;
	}

	@SuppressWarnings("unchecked") // TODO pcj.IntKeyOpenHashMap does not support generics
	private E cast(final Object o)
	{
		return (E)o;
	}
	
	private Integer getNumber(final E value)
	{
		final Integer result = valuesToNumbers.get(value);
		assert result!=null : toString() + value;
		return result;
	}

	public E getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return codesToValues.get(code);
	}

	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(E value : values)
			allowedValues[in++] = getNumber(value).intValue();

		return new IntegerColumn(table, name, optional, 10, false, allowedValues);
	}
	
	E get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return
			cell==null ?
				null :
				getValue(((Integer)cell).intValue());
	}
		
	void set(final Row row, final E surface)
	{
		row.put(getColumn(), surface==null ? null : getNumber(surface));
	}
	
}

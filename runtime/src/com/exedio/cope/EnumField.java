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
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

public final class EnumField<E extends Enum<E>> extends FunctionField<E>
{
	private final List<E> values;
	private final IntKeyOpenHashMap numbersToValues;
	private final int[] valuesToNumbers;
	
	private EnumField(final boolean isfinal, final boolean optional, final boolean unique, final Class<E> valueClass, final E defaultConstant)
	{
		super(isfinal, optional, unique, valueClass, defaultConstant);
		checkValueClass(Enum.class);

		final ArrayList<E> values = new ArrayList<E>();
		final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
		
		final E[] enumConstants = valueClass.getEnumConstants();
		if(enumConstants==null)
			throw new RuntimeException("must have at least one enum value: " + valueClass);
		final int[] valuesToNumbers = new int[enumConstants.length];
		
		for(int j = 0; j<enumConstants.length; j++)
		{
			final E enumConstant = enumConstants[j];
			final int number = (enumConstant.ordinal() + 1) * 10;
			values.add(enumConstant);

			if(numbersToValues.put(number, enumConstant)!=null)
				throw new RuntimeException("duplicate number " + number + " for enum field on " + valueClass);
			valuesToNumbers[enumConstant.ordinal()] = number;
		}
		values.trimToSize();
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(values);
		this.numbersToValues = numbersToValues;
		this.valuesToNumbers = valuesToNumbers;
		
		checkDefaultValue();
	}
	
	/**
	 * @deprecated
	 * use {@link Item#newEnumField(Class)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new EnumField&lt;Target&gt;(Target.class)</tt>
	 * one can write <tt>newEnumField(Target.class)</tt>
	 */
	@Deprecated
	public EnumField(final Class<E> valueClass)
	{
		this(Item.MANDATORY, valueClass);
	}
	
	/**
	 * @deprecated
	 * use {@link Item#newEnumField(com.exedio.cope.Field.Option, Class)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new EnumField&lt;Target&gt;(OPTIONAL, Target.class)</tt>
	 * one can write <tt>newEnumField(OPTIONAL, Target.class)</tt>
	 */
	@Deprecated
	public EnumField(final Option option, final Class<E> valueClass)
	{
		this(option.isFinal, option.optional, option.unique, valueClass, null);
	}
	
	@Override
	public EnumField<E> copyFunctionField()
	{
		return new EnumField<E>(isfinal, optional, implicitUniqueConstraint!=null, valueClass, defaultConstant);
	}
	
	public EnumField<E> defaultTo(final E defaultConstant)
	{
		assert isValid(defaultConstant);
		return new EnumField<E>(isfinal, optional, implicitUniqueConstraint!=null, valueClass, defaultConstant);
	}
	
	public List<E> getValues()
	{
		assert values!=null;
		return values;
	}
	
	private E getValue(final int number)
	{
		final E result = Cope.verboseCast(valueClass, numbersToValues.get(number)); // TODO remove cast when pcj.IntKeyOpenHashMap supports generics
		assert result!=null : toString() + number;
		return result;
	}

	private int getNumber(final E value)
	{
		assert isValid(value);
		return valuesToNumbers[value.ordinal()];
	}

	/**
	 * @see Enum#valueOf(Class, String)
	 */
	public E getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return Enum.valueOf(valueClass, code);
	}

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		return new IntegerColumn(table, name, optional, valuesToNumbers);
	}
	
	@Override
	E get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return
			cell==null ?
				null :
				getValue(((Integer)cell).intValue());
	}
		
	@Override
	void set(final Row row, final E surface)
	{
		assert isValid(surface);
		row.put(getColumn(), surface==null ? null : getNumber(surface));
	}
	
	private boolean isValid(final E value)
	{
		if(value==null)
			return true;

		final Class actualValueClass = value.getClass();
      return actualValueClass == valueClass || actualValueClass.getSuperclass() == valueClass;
	}
}

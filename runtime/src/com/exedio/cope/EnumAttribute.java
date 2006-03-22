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

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class EnumAttribute<E extends Enum> extends FunctionAttribute
{
	private final Class<E> enumClass;
	
	private EnumAttribute(final boolean isfinal, final boolean mandatory, final boolean unique, final Class<E> enumClass)
	{
		super(isfinal, mandatory, unique, enumClass);
		this.enumClass = enumClass;
		if(!Enum.class.isAssignableFrom(enumClass))
			throw new RuntimeException("is not a subclass of " + Enum.class.getName() + ": "+enumClass.getName());

	}
	
	public EnumAttribute(final Option option, final Class<E> enumClass)
	{
		this(option.isFinal, option.mandatory, option.unique, enumClass);
	}
	
	private List<E> values = null;
	private IntKeyOpenHashMap numbersToValues = null;
	private HashMap<E, Integer> valuesToNumbers = null;
	private HashMap<String, E> codesToValues = null;
	
	void initialize(final Type type, final String name)
	{
		super.initialize(type, name);
		
		final ArrayList<E> values = new ArrayList<E>();
		final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
		final HashMap<E, Integer> valuesToNumbers = new HashMap<E, Integer>();
		final HashMap<String, E> codesToValues = new HashMap<String, E>();
		final E[] enumConstants = enumClass.getEnumConstants();
		for(int j = 0; j<enumConstants.length; j++)
		{
			final E enumConstant = enumConstants[j];
			final String code = enumConstant.name();
			final int number = (enumConstant.ordinal() + 1) * 10;
			values.add(enumConstant);

			if(numbersToValues.put(number, enumConstant)!=null)
				throw new RuntimeException("duplicate number " + number + " for enum attribute on "+enumClass.toString());
			if(valuesToNumbers.put(enumConstant, number)!=null)
				throw new RuntimeException("duplicate value " + enumConstant + " for enum attribute on "+enumClass.toString());
				
			if(codesToValues.put(code, enumConstant)!=null)
				throw new RuntimeException("duplicate code " + code + " for enum attribute on "+enumClass.toString());
		}
		values.trimToSize();
		numbersToValues.trimToSize();
		this.values = Collections.unmodifiableList(values);
		this.numbersToValues = numbersToValues;
		this.valuesToNumbers = valuesToNumbers;
		this.codesToValues = codesToValues;
	}
	
	public FunctionAttribute copyFunctionAttribute()
	{
		return new EnumAttribute<E>(isfinal, mandatory, implicitUniqueConstraint!=null, enumClass);
	}
	
	public List<E> getValues()
	{
		assert values!=null;
		return values;
	}
	
	@SuppressWarnings("unchecked")
	private E getValue(final int number)
	{
		final E result = (E)numbersToValues.get(number);
		assert result!=null : toString() + number;
		return result;
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

	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(E value : values)
			allowedValues[in++] = getNumber(value).intValue();

		return new IntegerColumn(table, name, notNull, 10, false, allowedValues);
	}
	
	Object get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return
			cell==null ?
				null :
				getValue(((Integer)cell).intValue());
	}
		
	@SuppressWarnings("unchecked")
	void set(final Row row, final Object surface)
	{
		row.put(getColumn(), surface==null ? null : getNumber((E)surface));
	}
	
	@SuppressWarnings("unchecked")
	public final E get(final Item item)
	{
		return (E)getObject(item);
	}
	
	public final void set(final Item item, final E value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			FinalViolationException
	{
		try
		{
			item.set(this, value);
		}
		catch(LengthViolationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public final AttributeValue map(final E value)
	{
		return new AttributeValue(this, value);
	}
	
	public final EqualCondition equal(final E value)
	{
		return new EqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final E value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LessCondition less(final E value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final E value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final E value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final E value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
}

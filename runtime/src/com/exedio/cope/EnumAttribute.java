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

public final class EnumAttribute extends FunctionAttribute
{
	private final Class enumClass;
	private final List<Enum> values;
	private final IntKeyOpenHashMap numbersToValues;
	private final HashMap<Enum, Integer> valuesToNumbers;
	private final HashMap<String, Enum> codesToValues;
	
	private EnumAttribute(final boolean isfinal, final boolean mandatory, final boolean unique, final Class enumClass)
	{
		super(isfinal, mandatory, unique, enumClass);
		this.enumClass = enumClass;
		if(!Enum.class.isAssignableFrom(enumClass))
			throw new RuntimeException("is not a subclass of " + Enum.class.getName() + ": "+enumClass.getName());

		final ArrayList<Enum> values = new ArrayList<Enum>();
		final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
		final HashMap<Enum, Integer> valuesToNumbers = new HashMap<Enum, Integer>();
		final HashMap<String, Enum> codesToValues = new HashMap<String, Enum>();
		final Object[] enumConstants = enumClass.getEnumConstants();
		for(int j = 0; j<enumConstants.length; j++)
		{
			final Enum enumConstant = (Enum)enumConstants[j];
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
	
	public EnumAttribute(final Option option, final Class enumClass)
	{
		this(option.isFinal, option.mandatory, option.unique, enumClass);
	}
	
	public FunctionAttribute copyFunctionAttribute()
	{
		return new EnumAttribute(isfinal, mandatory, implicitUniqueConstraint!=null, enumClass);
	}
	
	public List<Enum> getValues()
	{
		return values;
	}
	
	private Enum getValue(final int number)
	{
		final Enum result = (Enum)numbersToValues.get(number);
		assert result!=null : toString() + number;
		return result;
	}

	private Integer getNumber(final Enum value)
	{
		final Integer result = valuesToNumbers.get(value);
		assert result!=null : toString() + value;
		return result;
	}

	public Enum getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return codesToValues.get(code);
	}

	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(Enum value : values)
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
		
	void set(final Row row, final Object surface)
	{
		row.put(getColumn(), surface==null ? null : getNumber((Enum)surface));
	}
	
	public final Enum get(final Item item)
	{
		return (Enum)getObject(item);
	}
	
	public final void set(final Item item, final Enum value)
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

	public final AttributeValue map(final Enum value)
	{
		return new AttributeValue(this, value);
	}
	
	public final EqualCondition equal(final Enum value)
	{
		return new EqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final Enum value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LessCondition less(final Enum value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final Enum value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final Enum value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final Enum value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
}

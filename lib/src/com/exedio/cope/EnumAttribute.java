/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;
import com.exedio.cope.search.NotEqualCondition;

import bak.pcj.map.IntKeyOpenHashMap;

public final class EnumAttribute extends ObjectAttribute
{
	private final Class enumClass;
	private final List values;
	private final IntKeyOpenHashMap numbersToValues;
	private final HashMap codesToValues;
	
	/**
	 * @see Item#enumAttribute(Option, Class)
	 */
	EnumAttribute(final Option option, final Class enumClass)
	{
		super(option, enumClass, enumClass.getName());
		this.enumClass = enumClass;
		if(!EnumValue.class.isAssignableFrom(enumClass))
			throw new RuntimeException("is not an enumeration value class: "+enumClass.getName());

		try
		{
			final ArrayList values = new ArrayList();
			final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
			final HashMap codesToValues = new HashMap();
			final Field[] fields = enumClass.getDeclaredFields();
			for(int j = 0; j<fields.length; j++)
			{
				final Field field = fields[j];
				final int mandatoryModifiers = Modifier.STATIC | Modifier.FINAL;
				//System.out.println("-----------field:"+field.getName());
				if(EnumValue.class.isAssignableFrom(field.getType()) &&
					(field.getModifiers()&mandatoryModifiers) == mandatoryModifiers)
				{
					final EnumValue value = (EnumValue)field.get(null);
					if(value==null)
						throw new NullPointerException("is null: "+field);
					//System.out.println("-------------value:"+value);
					if(!value.isInitialized())
					{
						final String name = field.getName();
						final String numName = name+"NUM";
						final int num;
						try
						{
							final Field numField = enumClass.getDeclaredField(numName);
							if((numField.getModifiers()&Modifier.STATIC)==0)
								throw new RuntimeException("field "+enumClass.getName()+"#"+numName+" must be static");
							if((numField.getModifiers()&Modifier.FINAL)==0)
								throw new RuntimeException("field "+enumClass.getName()+"#"+numName+" must be final");
							if(numField.getType()!=int.class)
								throw new RuntimeException("field "+enumClass.getName()+"#"+numName+" must have type int, but has "+numField.getClass());
							
							num = ((Integer)numField.get(null)).intValue();
						}
						catch(NoSuchFieldException e)
						{
							throw new RuntimeException("no such field "+enumClass.getName()+"#"+numName);
						}
						value.initialize(enumClass, name, num);
					}
					values.add(value);
					numbersToValues.put(value.getNumber(), value);
					codesToValues.put(value.getCode(), value);
				}
			}
			values.trimToSize();
			numbersToValues.trimToSize();
			this.values = Collections.unmodifiableList(values);
			this.numbersToValues = numbersToValues;
			this.codesToValues = codesToValues;
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new EnumAttribute(getTemplateOption(), enumClass);
	}
	
	public List getValues()
	{
		return values;
	}
	
	public EnumValue getValue(final int number)
	{
		return (EnumValue)numbersToValues.get(number);
	}

	public EnumValue getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return (EnumValue)codesToValues.get(code);
	}

	protected Column createColumn(final Table table, final String name, final boolean notNull)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(Iterator i = values.iterator(); i.hasNext(); in++)
			allowedValues[in] = ((EnumValue)i.next()).getNumber();

		return new IntegerColumn(table, name, notNull, 10, false, allowedValues);
	}
	
	Object cacheToSurface(final Object cache)
	{
		return
			cache==null ?
				null :
				getValue(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ?
				null :
				((EnumValue)surface).getNumberObject();
	}
	
	public final EqualCondition equal(final EnumValue value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final NotEqualCondition notEqual(final EnumValue value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LessCondition less(final EnumValue value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final EnumValue value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final EnumValue value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final EnumValue value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
}

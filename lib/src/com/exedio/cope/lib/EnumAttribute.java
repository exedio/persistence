package com.exedio.cope.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

public final class EnumAttribute extends ObjectAttribute
{
	private final Class enumerationClass;
	private final List values;
	private final IntKeyOpenHashMap numbersToValues;
	private final HashMap codesToValues;
	
	/**
	 * @see Item#enumerationAttribute(Option, Class)
	 */
	EnumAttribute(final Option option, final Class enumerationClass)
	{
		super(option);
		this.enumerationClass = enumerationClass;
		if(!EnumValue.class.isAssignableFrom(enumerationClass))
			throw new RuntimeException("is not an enumeration value class: "+enumerationClass.getName());

		try
		{
			final ArrayList values = new ArrayList();
			final IntKeyOpenHashMap numbersToValues = new IntKeyOpenHashMap();
			final HashMap codesToValues = new HashMap();
			final Field[] fields = enumerationClass.getDeclaredFields();
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
							final Field numField = enumerationClass.getDeclaredField(numName);
							if((numField.getModifiers()&Modifier.STATIC)==0)
								throw new RuntimeException("field "+enumerationClass.getName()+"#"+numName+" must be static");
							if((numField.getModifiers()&Modifier.FINAL)==0)
								throw new RuntimeException("field "+enumerationClass.getName()+"#"+numName+" must be final");
							if(numField.getType()!=int.class)
								throw new RuntimeException("field "+enumerationClass.getName()+"#"+numName+" must have type int, but has "+numField.getClass());
							
							num = ((Integer)numField.get(null)).intValue();
						}
						catch(NoSuchFieldException e)
						{
							throw new RuntimeException("no such field "+enumerationClass.getName()+"#"+numName);
						}
						value.initialize(enumerationClass, name, num);
					}
					values.add(value);
					numbersToValues.put(value.getNumber(), value);
					codesToValues.put(value.getCode(), value);
				}
			}
			this.values = Collections.unmodifiableList(values);
			this.numbersToValues = numbersToValues;
			this.codesToValues = codesToValues;
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
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

	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(Iterator i = values.iterator(); i.hasNext(); in++)
			allowedValues[in] = ((EnumValue)i.next()).getNumber();

		return Collections.singletonList(new IntegerColumn(table, name, notNull, 10, false, allowedValues));
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
	
}

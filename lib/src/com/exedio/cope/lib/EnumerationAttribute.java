
package com.exedio.cope.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class EnumerationAttribute extends ObjectAttribute
{
	private final Class enumerationClass;
	private final List values;
	private final HashMap numbersToValues; // TODO: use special integer map
	private final HashMap codesToValues;
	
	public EnumerationAttribute(final Option option, final Class enumerationClass)
	{
		super(option);
		this.enumerationClass = enumerationClass;
		if(!EnumerationValue.class.isAssignableFrom(enumerationClass))
			throw new RuntimeException("is not an enumeration value class: "+enumerationClass.getName());

		try
		{
			final ArrayList values = new ArrayList();
			final HashMap numbersToValues = new HashMap();
			final HashMap codesToValues = new HashMap();
			final Field[] fields = enumerationClass.getDeclaredFields();
			for(int j = 0; j<fields.length; j++)
			{
				final Field field = fields[j];
				final int mandatoryModifiers = Modifier.STATIC | Modifier.FINAL;
				//System.out.println("-----------field:"+field.getName());
				if(EnumerationValue.class.isAssignableFrom(field.getType()) &&
					(field.getModifiers()&mandatoryModifiers) == mandatoryModifiers)
				{
					final EnumerationValue value = (EnumerationValue)field.get(null);
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
								throw new InitializerRuntimeException("field "+enumerationClass.getName()+"#"+numName+" must be static");
							if((numField.getModifiers()&Modifier.FINAL)==0)
								throw new InitializerRuntimeException("field "+enumerationClass.getName()+"#"+numName+" must be final");
							if(numField.getType()!=int.class)
								throw new InitializerRuntimeException("field "+enumerationClass.getName()+"#"+numName+" must have type int, but has "+numField.getClass());
							
							num = ((Integer)numField.get(null)).intValue();
						}
						catch(NoSuchFieldException e)
						{
							throw new InitializerRuntimeException("no such field "+enumerationClass.getName()+"#"+numName);
						}
						value.initialize(enumerationClass, name, num);
					}
					values.add(value);
					numbersToValues.put(value.getNumberObject(), value);
					codesToValues.put(value.getCode(), value);
				}
			}
			this.values = Collections.unmodifiableList(values);
			this.numbersToValues = numbersToValues;
			this.codesToValues = codesToValues;
		}
		catch(IllegalAccessException e)
		{
			throw new SystemException(e);
		}
	}
	
	public List getValues()
	{
		return values;
	}
	
	public EnumerationValue getValue(final int number)
	{
		return (EnumerationValue)numbersToValues.get(new Integer(number));
	}

	public EnumerationValue getValue(final String code)
	{
		//System.out.println("EnumerationValue#getValue("+code+") from "+codesToValues);
		return (EnumerationValue)codesToValues.get(code);
	}

	protected List createColumns(final String name, final boolean notNull)
	{
		final int[] allowedValues = new int[values.size()];
		int in = 0;
		for(Iterator i = values.iterator(); i.hasNext(); in++)
			allowedValues[in] = ((EnumerationValue)i.next()).getNumber();

		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, 10, false, allowedValues));
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
				((EnumerationValue)surface).getNumberObject();
	}
	
}


package com.exedio.cope.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class EnumerationAttribute extends Attribute
{
	private final Class enumerationClass;
	private final List values;
	private final HashMap numbersToValues; // TODO: use special integer map
	
	public EnumerationAttribute(final Search.Option option, final Class enumerationClass)
	{
		super(option);
		this.enumerationClass = enumerationClass;
		if(!EnumerationValue.class.isAssignableFrom(enumerationClass))
			throw new RuntimeException("is not an enumeration value class: "+enumerationClass.getName());

		try
		{
			final ArrayList values = new ArrayList();
			final HashMap numbersToValues = new HashMap();
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
					numbersToValues.put(value.getNumberObject(), value);
				}
			}
			this.values = Collections.unmodifiableList(values);
			this.numbersToValues = numbersToValues;
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

	protected List createColumns(final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, notNull, 10));
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

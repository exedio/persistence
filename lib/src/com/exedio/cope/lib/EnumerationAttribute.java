
package com.exedio.cope.lib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class EnumerationAttribute extends Attribute
{
	
	private List values;
	private HashMap numbersToValues; // TODO: use special integer map

	public void initialize(final String name, final boolean readOnly, final boolean notNull)
	{
		super.initialize(name, readOnly, notNull);
		final Class javaClass = getType().getJavaClass();
		final Class[] innerClasses = javaClass.getDeclaredClasses();
		final String upperCaseName = javaClass.getName() + '$' + Character.toUpperCase(name.charAt(0)) + name.substring(1);

		try
		{
			innerClasses:for(int i = 0; i<innerClasses.length; i++)
			{
				final Class innerClass = innerClasses[i];
				//System.out.println("---------innerClass:"+innerClass.getName());
				if(upperCaseName.equals(innerClass.getName()))
				{
					final ArrayList values = new ArrayList();
					final HashMap numbersToValues = new HashMap();
					final Field[] fields = innerClass.getDeclaredFields();
					for(int j = 0; j<fields.length; j++)
					{
						final Field field = fields[j];
						//System.out.println("-----------field:"+field.getName());
						if(!field.getName().endsWith("NUM"))
						{
							final EnumerationValue value = (EnumerationValue)field.get(null);
							//System.out.println("-------------value:"+value);
							values.add(value);
							numbersToValues.put(new Integer(value.number), value);
						}
					}
					this.values = Collections.unmodifiableList(values);
					this.numbersToValues = numbersToValues;
					break innerClasses;
				}
			}
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

	protected List createColumns(final String name)
	{
		return Collections.singletonList(new IntegerColumn(getType(), name, 10));
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
				((EnumerationValue)surface).numberObject;
	}
	
}

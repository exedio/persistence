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

package com.exedio.cope.pattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.UniqueViolationException;

public final class CustomAttribute extends Pattern
{
	final ObjectAttribute storage;
	Method getter; 
	Method setter; 
	Class valueType;
	
	public CustomAttribute(final ObjectAttribute storage)
	{
		this.storage = storage;
		if(storage==null)
			throw new NullPointerException("storage must not be null");
		registerSource(storage);
	}
	
	public ObjectAttribute getStorage()
	{
		return storage;
	}
	
	public void initialize()
	{
		final String name = getName();
		if(!storage.isInitialized())
			initialize(storage, name+"Storage");
		
		final String nameUpper =
			name.length()==1
			? Character.toString(Character.toUpperCase(name.charAt(0)))
			: (Character.toUpperCase(name.charAt(0)) + name.substring(1));
		final String nameGetter = "get" + nameUpper;
		final String nameSetter = "set" + nameUpper;
		
		Method getter = null; 
		Method setter = null; 
		final Method[] methods = getType().getJavaClass().getDeclaredMethods();
		for(int i = 0; i<methods.length; i++)
		{
			final Method m = methods[i];
			if(nameGetter.equals(m.getName()))
			{
				final Class[] params = m.getParameterTypes();
				if(params.length==0 && m.getReturnType()!=void.class)
				{
					if(getter!=null)
						throw new RuntimeException("ambigous getter: "+getter+" and "+m);
					getter = m;
				}
			}
			if(nameSetter.equals(m.getName()))
			{
				final Class[] params = m.getParameterTypes();
				if(params.length==1 && m.getReturnType()==void.class)
				{
					if(setter!=null)
						throw new RuntimeException("ambigous setter: "+setter+" and "+m);
					setter = m;
				}
			}
		}
		if(getter==null)
			throw new RuntimeException("no getter found");
		if(setter==null)
			throw new RuntimeException("no setter found");

		final Class getterType = getter.getReturnType();
		final Class setterType = setter.getParameterTypes()[0];
		if(getterType!=setterType)
			throw new RuntimeException("getter incompatible to setter: "+getterType.getName()+" - "+setterType.getName());
		
		this.getter = getter;
		this.setter = setter;
		this.valueType = getterType;
	}
	
	final Class getValueType()
	{
		assert valueType!=null;
		return valueType;
	}
	
	final Object get(final Item item)
	{
		try
		{
			return getter.invoke(item, (Object[])null);
		}
		catch(IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	final void set(final Item item, final Object value)
	throws
		UniqueViolationException,
		MandatoryViolationException,
		LengthViolationException,
		ReadOnlyViolationException
	{
		try
		{
			setter.invoke(item, new Object[]{value});
		}
		catch(IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
	
}

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
import com.exedio.cope.FunctionAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.UniqueViolationException;

public final class CustomAttribute extends Pattern
{
	private final FunctionAttribute storage;
	private Method getter;
	private Method setter;
	private Class valueType;
	
	public CustomAttribute(final FunctionAttribute storage)
	{
		this.storage = storage;
		if(storage==null)
			throw new NullPointerException("storage must not be null");
		registerSource(storage);
	}
	
	public FunctionAttribute getStorage()
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
		
		final Class javaClass = getType().getJavaClass();
		final Method getter; 
		try
		{
			getter = javaClass.getDeclaredMethod("get"+nameUpper, (Class[])null); 
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("no suitable getter method found for custom attribute "+name, e);
		}
		
		final Class valueType = getter.getReturnType();
		
		final Method setter;
		try
		{
			setter = javaClass.getDeclaredMethod("set"+nameUpper, new Class[]{valueType});
		}
		catch(NoSuchMethodException e)
		{
			throw new RuntimeException("no suitable setter method found for custom attribute "+name, e);
		}

		this.getter = getter;
		this.setter = setter;
		this.valueType = valueType;
	}
	
	final Class getValueType()
	{
		assert valueType!=null;
		return valueType;
	}
	
	public final Object get(final Item item)
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

	public final void set(final Item item, final Object value)
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

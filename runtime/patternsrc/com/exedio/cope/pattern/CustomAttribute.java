/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.exedio.cope.Cope;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;

public abstract class CustomAttribute<E>
	extends Pattern
	implements Settable<E>
{
	private final FunctionField<?>[] storages;
	private final List<FunctionField<?>> storageList;
	private final boolean initial;
	private final boolean isFinal;
	private final Method getter;
	private final Method setter;
	private final Class<E> valueClass;
	
	public CustomAttribute(final FunctionField<?> storage)
	{
		this(new FunctionField[]{storage});
	}
	
	public CustomAttribute(final FunctionField<?> storage1, final FunctionField<?> storage2)
	{
		this(new FunctionField[]{storage1, storage2});
	}
	
	public CustomAttribute(final FunctionField<?>[] storages)
	{
		this.storages = storages;
		this.storageList = Collections.unmodifiableList(Arrays.asList(storages));

		{
			boolean initial = false;
			boolean isFinal = false;
			for(FunctionField<?> storage : storages)
			{
				registerSource(storage);
				initial = initial || storage.isInitial();
				isFinal = isFinal || storage.isFinal();
			}
			this.initial = initial;
			this.isFinal = isFinal;
		}
		
		final Method[] methods = getClass().getDeclaredMethods();
		Method getter = null;
		Method setter = null;
		for(int i = 0; i<methods.length; i++)
		{
			final Method m = methods[i];
			final String methodName = m.getName();
			if("get".equals(methodName))
			{
				// TODO test all the exceptions
				if(getter!=null)
					throw new RuntimeException("more than one getter method:"+getter+" and "+m);
				if(m.getParameterTypes().length!=storages.length)
					throw new RuntimeException("number of parameters of getter method must be equal to number of storages");
				getter = m;
			}
			else if("set".equals(methodName))
			{
				if(setter!=null)
					throw new RuntimeException("more than one setter method:"+setter+" and "+m);
				if(m.getParameterTypes().length!=1)
					throw new RuntimeException("number of parameters of setter method must be 1");
				if(m.getReturnType()!=(new SetValue[0]).getClass() && storages.length!=1)
					throw new RuntimeException("oops");
				setter = m;
			}
		}
		if(getter==null)
			throw new RuntimeException("no suitable getter method found for custom attribute");
		if(setter==null)
			throw new RuntimeException("no suitable setter method found for custom attribute");
		
		getter.setAccessible(true);
		setter.setAccessible(true);
		
		this.getter = getter;
		this.setter = setter;
		this.valueClass = castClass(getter.getReturnType());
	}

	@SuppressWarnings("unchecked") // TODO check this cast with runtime information
	private final Class<E> castClass(Class<?> c)
	{
		return (Class<E>)c;
	}
	
	public final List<FunctionField<?>> getStorages()
	{
		return storageList;
	}
	
	public final boolean isInitial()
	{
		return initial;
	}
	
	public final boolean isFinal()
	{
		return isFinal;
	}
	
	public final Set<Class> getSetterExceptions()
	{
		final Set<Class> result = storages[0].getSetterExceptions();
		for(int i = 1; i<storages.length; i++)
			result.addAll(storages[i].getSetterExceptions());
		return result;
	}
	
	@Override
	public final void initialize()
	{
		final String name = getName();

		for(int i = 0; i<storages.length; i++)
			if(!storages[i].isInitialized())
				initialize(storages[i], name+"Storage"+i);
	}
	
	public final Class getValueClass()
	{
		assert valueClass!=null;
		return valueClass;
	}

	public final E cast(final Object o)
	{
		return Cope.verboseCast(valueClass, o);
	}

	public final E get(final Item item)
	{
		final Object[] params = new Object[storages.length];
		for(int i = 0; i<params.length; i++)
			params[i] = storages[i].get(item);
		
		try
		{
			return cast(getter.invoke(this, params));
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

	public final void set(final Item item, final E value) throws CustomAttributeException
	{
		item.set(execute(value, item));
	}
	
	/**
	 * {@link #cast(Object) Casts}
	 * <tt>value</tt> to <tt>E</tt> before calling
	 * {@link #set(Item, Object)}
	 * @throws ClassCastException if <tt>value</tt> is not assignable to <tt>E</tt>
	 */
	// TODO put into Settable
	public final void setAndCast(final Item item, final Object value)
	{
		set(item, cast(value));
	}

	public final SetValue[] execute(final E value, final Item exceptionItem) throws CustomAttributeException
	{
		final Object result;
		try
		{
			result = setter.invoke(this, new Object[]{value});
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
			final Throwable t = e.getCause();
			if(t instanceof RuntimeException)
				throw (RuntimeException)t;
			else
				throw new CustomAttributeException(this, exceptionItem, e.getCause());
		}
		
		if(storages.length==1)
			return new SetValue[]{ Cope.mapAndCast(storages[0], result) };
		else
			return (SetValue[])result;
	}
	
	public final SetValue<E> map(final E value)
	{
		return new SetValue<E>(this, value);
	}

}

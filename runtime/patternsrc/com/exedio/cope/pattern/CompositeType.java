/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.InstrumentContext;

final class CompositeType<X>
{
	private final Constructor<X> constructor;
	final LinkedHashMap<String, FunctionField> templates = new LinkedHashMap<String, FunctionField>();
	final HashMap<FunctionField, Integer> templatePositions = new HashMap<FunctionField, Integer>();
	final List<FunctionField> templateList;
	final int componentSize;

	private CompositeType(final Class<X> valueClass)
	{
		//System.out.println("---------------new Composite.Type(" + vc + ')');
		try
		{
			constructor = valueClass.getDeclaredConstructor(SetValue[].class);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					valueClass.getName() + " does not have a constructor " +
					valueClass.getSimpleName() + '(' + SetValue.class.getName() + "[])", e);
		}
		constructor.setAccessible(true);

		try
		{
			int position = 0;
			for(final java.lang.reflect.Field field : valueClass.getDeclaredFields())
			{
				if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
					continue;
				if(!Feature.class.isAssignableFrom(field.getType()))
					continue;

				field.setAccessible(true);
				final Feature feature = (Feature)field.get(null);
				if(feature==null)
					throw new NullPointerException(valueClass.getName() + '#' + field.getName());
				if(!(feature instanceof FunctionField))
					throw new IllegalArgumentException(valueClass.getName() + '#' + field.getName() + " must be an instance of " + FunctionField.class);
				final FunctionField template = (FunctionField)feature;
				if(template.isFinal())
					throw new IllegalArgumentException("final fields not supported: " + valueClass.getName() + '#' + field.getName());
				templates.put(field.getName(), template);
				templatePositions.put(template, position++);
			}
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(valueClass.getName(), e);
		}
		this.templateList = Collections.unmodifiableList(new ArrayList<FunctionField>(templates.values()));
		this.componentSize = templates.size();
	}

	private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;


	public List<FunctionField> getTemplates()
	{
		return templateList;
	}

	public X newValue(final SetValue... setValues)
	{
		try
		{
			return constructor.newInstance(new Object[]{setValues});
		}
		catch(final IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		catch(final InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(final InvocationTargetException e)
		{
			final Throwable cause = e.getCause();
			if(cause instanceof ConstraintViolationException)
				throw (ConstraintViolationException)cause;
			else if(cause instanceof IllegalArgumentException)
				throw (IllegalArgumentException)cause;
			else
				throw new RuntimeException(e);
		}
	}


	private static final HashMap<Class, CompositeType> types = new HashMap<Class, CompositeType>();

	static final <E> CompositeType<E> get(final Class<E> valueClass)
	{
		if(valueClass==null)
			throw new NullPointerException("valueClass");
		if(!Composite.class.isAssignableFrom(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + ": "+valueClass.getName());
		if(Composite.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + " but Composite itself");

		synchronized(types)
		{
			@SuppressWarnings("unchecked")
			CompositeType<E> result = types.get(valueClass);
			if(result==null)
			{
				result = new CompositeType<E>(valueClass);
				types.put(valueClass, result);
			}

			if(result.componentSize==0 && !InstrumentContext.isRunning())
				throw new IllegalArgumentException("composite has no templates");

			return result;
		}
	}
}

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.CopeName;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.InstrumentContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class CompositeType<E>
{
	private final Constructor<E> constructor;
	private final LinkedHashMap<String, FunctionField<?>> templates = new LinkedHashMap<String, FunctionField<?>>();
	private final HashMap<FunctionField<?>, Integer> templatePositions = new HashMap<FunctionField<?>, Integer>();
	final List<FunctionField<?>> templateList;
	final int componentSize;

	private static final HashMap<FunctionField<?>, String> templateNames = new HashMap<FunctionField<?>, String>();

	private CompositeType(final Class<E> valueClass)
	{
		//System.out.println("---------------new Composite.Type(" + vc + ')');
		final String classID = valueClass.getName();
		try
		{
			constructor = valueClass.getDeclaredConstructor(SetValue[].class);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					classID + " does not have a constructor " +
					valueClass.getSimpleName() + '(' + SetValue.class.getName() + "[])", e);
		}
		constructor.setAccessible(true);

		{
			int position = 0;
			for(final Map.Entry<Feature, java.lang.reflect.Field> entry : TypesBound.getFeatures(valueClass).entrySet())
			{
				final Feature feature = entry.getKey();
				final java.lang.reflect.Field field = entry.getValue();
				final String fieldID = classID + '#' + field.getName();
				if(!(feature instanceof FunctionField<?>))
					throw new IllegalArgumentException(fieldID + " must be an instance of " + FunctionField.class);
				final FunctionField<?> template = (FunctionField<?>)feature;
				if(template.isFinal())
					throw new IllegalArgumentException("final fields not supported: " + fieldID);
				final String fieldName = name(field);
				templates.put(fieldName, template);
				templatePositions.put(template, position++);
				template.mount(fieldID, SerializedReflectionField.make(feature, field), field);
				templateNames.put(template, fieldName);
			}
		}
		this.templateList = Collections.unmodifiableList(new ArrayList<FunctionField<?>>(templates.values()));
		this.componentSize = templates.size();
	}

	private static String name(final java.lang.reflect.Field field)
	{
		final CopeName annotation = field.getAnnotation(CopeName.class);
		return
			annotation!=null
			? annotation.value()
			: field.getName();
	}

	Object[] values(final SetValue<?>... setValues)
	{
		final Object[] values = new Object[componentSize];
		final boolean[] valueSet = new boolean[values.length];
		for(final SetValue<?> v : setValues)
		{
			final int position = position((FunctionField<?>)v.settable);
			values[position] = v.value;
			valueSet[position] = true;
		}
		for(int i = 0; i<valueSet.length; i++)
			if(!valueSet[i])
				values[i] = templateList.get(i).getDefaultConstant();

		int i = 0;
		for(final FunctionField<?> ff : templateList)
			check(ff, values[i++]);

		return values;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static final <E> void check(final FunctionField field, final Object value)
	{
		field.check(value);
	}

	Map<String,FunctionField<?>> getTemplateMap()
	{
		return Collections.unmodifiableMap(templates);
	}

	int position(final FunctionField<?> member)
	{
		final Integer result = templatePositions.get(member);
		if(result==null)
			throw new IllegalArgumentException("not a member");
		return result.intValue();
	}

	public E newValue(final SetValue<?>... setValues)
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

	// static registry

	private static final HashMap<Class<?>, CompositeType<?>> types = new HashMap<Class<?>, CompositeType<?>>();

	static final <E> CompositeType<E> get(final Class<E> valueClass)
	{
		if(valueClass==null)
			throw new NullPointerException("valueClass");
		if(!Composite.class.isAssignableFrom(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + ": "+valueClass.getName());
		if(Composite.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + " but Composite itself");
		if(!Modifier.isFinal(valueClass.getModifiers()))
			throw new IllegalArgumentException("is not final: " + valueClass.getName());

		synchronized(types)
		{
			@SuppressWarnings({"unchecked", "rawtypes"})
			CompositeType<E> result = (CompositeType)types.get(valueClass);
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

	static String getTemplateName(final FunctionField<?> template)
	{
		if(template==null)
			throw new NullPointerException("template");

		final String result = templateNames.get(template);
		if(result==null)
			throw new IllegalStateException("feature not mounted to a composite: " + template);

		return result;
	}
}

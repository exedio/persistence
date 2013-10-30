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
import com.exedio.cope.Copyable;
import com.exedio.cope.Feature;
import com.exedio.cope.Item;
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

public final class BlockType<E> // TODO make Serializable as singleton
{
	final Class<E> javaClass;
	private final Constructor<E> constructor;
	private final LinkedHashMap<String, Feature> templates = new LinkedHashMap<String, Feature>();
	final List<Feature> templateList;
	final int componentSize;

	private BlockType(final Class<E> javaClass)
	{
		this.javaClass = javaClass;
		final String classID = javaClass.getName();
		try
		{
			constructor = javaClass.getDeclaredConstructor(BlockActivationParameters.class);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					classID + " does not have a constructor " +
					javaClass.getSimpleName() + '(' + BlockActivationParameters.class.getName() + ')', e);
		}
		constructor.setAccessible(true);

		{
			for(final Map.Entry<Feature, java.lang.reflect.Field> entry : TypesBound.getFeatures(javaClass).entrySet())
			{
				final Feature feature = entry.getKey();
				final java.lang.reflect.Field field = entry.getValue();
				final String fieldID = classID + '#' + field.getName();
				// TODO test
				if(!(feature instanceof Copyable))
					throw new IllegalArgumentException(fieldID + " must be an instance of " + Copyable.class);
				final String fieldName = name(field);

				templates.put(fieldName, feature);
				feature.mount(fieldID, SerializedReflectionField.make(feature, field), field);
			}
		}
		this.templateList = Collections.unmodifiableList(new ArrayList<Feature>(templates.values()));
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

	Map<String,Feature> getTemplateMap()
	{
		return Collections.unmodifiableMap(templates);
	}

	E newValue(final BlockField<?> field, final Item item)
	{
		try
		{
			return constructor.newInstance(new BlockActivationParameters(field, item));
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

	private static final HashMap<Class<?>, BlockType<?>> types = new HashMap<Class<?>, BlockType<?>>();

	static <T extends Block> BlockType<T> forClass(final Class<T> javaClass)
	{
		final BlockType<?> result = types.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		@SuppressWarnings("unchecked")
		final BlockType<T> casted = (BlockType<T>)result;
		return casted;
	}

	public static <T extends Block> BlockType<T> newType(final Class<T> javaClass)
	{
		if(javaClass==null)
			throw new NullPointerException("valueClass");
		if(types.containsKey(javaClass))
			throw new IllegalArgumentException("class is already bound to a type: " + javaClass.getName());
		if(!Block.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException("is not a subclass of " + Block.class.getName() + ": "+javaClass.getName());
		if(Block.class.equals(javaClass))
			throw new IllegalArgumentException("is not a subclass of " + Block.class.getName() + " but Block itself");
		if(!Modifier.isFinal(javaClass.getModifiers()))
			throw new IllegalArgumentException("is not final: " + javaClass.getName());

		@SuppressWarnings({"unchecked", "rawtypes"})
		final BlockType<T> result = new BlockType<T>(javaClass);
		types.put(javaClass, result);

		if(result.componentSize==0 && !InstrumentContext.isRunning())
			throw new IllegalArgumentException("composite has no templates");

		return result;
	}
}

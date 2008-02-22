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

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exedio.cope.Field;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrapper;

public final class Composite<E extends Composite.Value> extends Pattern implements Settable<E>
{
	private final boolean isfinal;
	private final boolean optional;
	private final Class<E> valueClass;
	
	private final ValueType<E> valueType;
	private final Constructor<E> valueConstructor;
	private final LinkedHashMap<String, FunctionField> templates;
	private final int componentSize;
	
	private LinkedHashMap<FunctionField, FunctionField> templateToComponent = null;
	
	private Composite(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = valueClass;
		
		if(valueClass==null)
			throw new NullPointerException("valueClass must not be null");
		if(!Value.class.isAssignableFrom(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Value.class.getName() + ": "+valueClass.getName());
		if(Value.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Value.class.getName() + " but Composite.Value itself");

		this.valueType = getValueType(valueClass);
		this.valueConstructor = valueType.valueConstructor;
		this.templates = valueType.templates;
		this.componentSize = valueType.componentSize;
	}
	
	public static <E extends Composite.Value> Composite<E> newComposite(final Class<E> valueClass)
	{
		return new Composite<E>(false, false, valueClass);
	}
	
	public Composite<E> toFinal()
	{
		return new Composite<E>(true, optional, valueClass);
	}
	
	public Composite<E> optional()
	{
		return new Composite<E>(isfinal, true, valueClass);
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		final LinkedHashMap<FunctionField, FunctionField> templateToComponent =
			new LinkedHashMap<FunctionField, FunctionField>();
		
		for(Map.Entry<String, FunctionField> e : templates.entrySet())
		{
			final String templateName = e.getKey();
			final FunctionField template = e.getValue();
			final FunctionField component = copy(template);
			registerSource(component);
			initialize(component, name + toCamelCase(templateName));
			templateToComponent.put(template, component);
		}
		this.templateToComponent = templateToComponent;
	}
	
	private FunctionField copy(final FunctionField template)
	{
		if(isfinal)
			if(optional)
				return (FunctionField)template.toFinal().optional();
			else
				return (FunctionField)template.toFinal();
		else
			if(optional)
				return (FunctionField)template.optional();
			else
				return template.copy();
	}
	
	private static final String toCamelCase(final String name)
	{
		final char first = name.charAt(0);
		if(Character.isUpperCase(first))
			return name;
		else
			return Character.toUpperCase(first) + name.substring(1);
	}

	public FunctionField getComponent(final FunctionField template)
	{
		final FunctionField result = templateToComponent.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + toString());
		return result;
	}
	
	public List<FunctionField> getComponents()
	{
		return Collections.unmodifiableList(new ArrayList<FunctionField>(templateToComponent.values()));
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper(
				valueClass,
				"get",
				"Returns the value of {0}.",
				"getter"));
		
		if(!isfinal)
		{
			result.add(
				new Wrapper(
					void.class,
					"set",
					"Sets a new value for {0}.",
					"setter").
				addThrows(getSetterExceptions()).
				addParameter(valueClass));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	@SuppressWarnings("unchecked")
	public E get(final Item item)
	{
		final SetValue[] initargs = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField, FunctionField> e : templateToComponent.entrySet())
		{
			initargs[i++] = e.getKey().map(e.getValue().get(item));
		}
		try
		{
			return valueConstructor.newInstance(new Object[]{initargs});
		}
		catch(IllegalArgumentException e)
		{
			throw new RuntimeException(e);
		}
		catch(InstantiationException e)
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
	
	@SuppressWarnings("unchecked")
	public void set(final Item item, final E value)
	{
		final SetValue[] setValues = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField, FunctionField> e : templateToComponent.entrySet())
			setValues[i++] = e.getValue().map(value.get(e.getKey()));
		item.set(setValues);
	}

	@SuppressWarnings("unchecked")
	public SetValue[] execute(E value, Item exceptionItem)
	{
		final SetValue[] result = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField, FunctionField> e : templateToComponent.entrySet())
			result[i++] = e.getValue().map(value.get(e.getKey()));
		return result;
	}

	@SuppressWarnings("unchecked")
	public Set<Class> getSetterExceptions()
	{
		final HashSet<Class> result = new HashSet<Class>();
		for(final FunctionField member : templates.values())
			result.addAll(member.getSetterExceptions());
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	public Class getWrapperSetterType()
	{
		return valueClass;
	}

	public boolean isFinal()
	{
		return isfinal;
	}

	public boolean isInitial()
	{
		return isfinal || !optional;
	}

	public SetValue map(E value)
	{
		return new SetValue<E>(this, value);
	}
	
	private static final class ValueType<X>
	{
		final Class<X> valueClass;
		final Constructor<X> valueConstructor;
		final LinkedHashMap<String, FunctionField> templates = new LinkedHashMap<String, FunctionField>();
		final HashMap<FunctionField, Integer> templatePositions = new HashMap<FunctionField, Integer>();
		final int componentSize;
		
		ValueType(final Class<X> valueClass)
		{
			//System.out.println("---------------new ValueType(" + vc + ')');
			this.valueClass = valueClass;
			try
			{
				valueConstructor = valueClass.getDeclaredConstructor(SetValue[].class);
				valueConstructor.setAccessible(true);
			}
			catch(NoSuchMethodException e)
			{
				throw new IllegalArgumentException(valueClass.getName() + " does not have a constructor " + Arrays.toString(new Class[]{SetValue[].class}), e);
			}
			
			final java.lang.reflect.Field[] fields = valueClass.getDeclaredFields();
			final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
			try
			{
				int position = 0;
				for(final java.lang.reflect.Field field : fields)
				{
					if((field.getModifiers()&expectedModifier)==expectedModifier)
					{
						final Class fieldType = field.getType();
						if(Field.class.isAssignableFrom(fieldType))
						{
							field.setAccessible(true);
							final FunctionField template = (FunctionField)field.get(null);
							if(template==null)
								throw new RuntimeException(field.getName());
							templates.put(field.getName(), template);
							templatePositions.put(template, position++);
						}
					}
				}
			}
			catch(IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
			this.componentSize = templates.size();
		}
	}
	
	static final HashMap<Class, ValueType> valueTypes = new HashMap<Class, ValueType>();

	@SuppressWarnings("unchecked")
	private static final <E> ValueType<E> getValueType(final Class valueClass)
	{
		assert valueClass!=null;
		
		synchronized(valueTypes)
		{
			ValueType<E> result = valueTypes.get(valueClass);
			if(result==null)
			{
				result = new ValueType(valueClass);
				valueTypes.put(valueClass, result);
			}
			return result;
		}
	}
	
	public static abstract class Value implements Serializable
	{
		private final Object[] values;

		protected Value(final SetValue... setValues)
		{
			final ValueType<?> valueType = valueType();
			values = new Object[valueType.componentSize];
			for(final SetValue v : setValues)
				values[valueType.templatePositions.get(v.settable)] = v.value;
		}

		@SuppressWarnings("unchecked")
		protected <X> X get(final FunctionField<X> member)
		{
			final ValueType<?> valueType = valueType();
			return (X)values[valueType.templatePositions.get(member)];
		}
		
		protected <X> void set(final FunctionField<X> member, final X value)
		{
			final ValueType<?> valueType = valueType();
			values[valueType.templatePositions.get(member)] = value;
		}
		
		private final ValueType<?> valueType()
		{
			final ValueType result;
			synchronized(valueTypes)
			{
				result = valueTypes.get(getClass());
			}
			assert result!=null;
			return result;
		}

		@Override
		public final boolean equals(final Object other)
		{
			if(this==other)
				return true;
			
			return
				other!=null &&
				getClass().equals(other.getClass()) &&
				Arrays.equals(values, ((Value)other).values);
		}
		
		@Override
		public final int hashCode()
		{
			return getClass().hashCode() ^ Arrays.hashCode(values);
		}
	}
}

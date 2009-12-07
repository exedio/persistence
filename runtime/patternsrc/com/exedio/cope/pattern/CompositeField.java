/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.ComputedInstance;

public final class CompositeField<E extends Composite> extends Pattern implements Settable<E>
{
	private final boolean isfinal;
	private final boolean optional;
	private final Class<E> valueClass;
	
	private final Composite.Type<E> valueType;
	private final Constructor<E> valueConstructor;
	private final LinkedHashMap<String, FunctionField> templates;
	private final int componentSize;
	
	private LinkedHashMap<FunctionField, FunctionField> templateToComponent = null;
	private FunctionField mandatoryComponent = null;
	
	private CompositeField(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = valueClass;
		
		if(valueClass==null)
			throw new NullPointerException("valueClass");
		if(!Composite.class.isAssignableFrom(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + ": "+valueClass.getName());
		if(Composite.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Composite.class.getName() + " but Composite itself");

		this.valueType = Composite.getType(valueClass);
		this.valueConstructor = valueType.constructor;
		this.templates = valueType.templates;
		this.componentSize = valueType.componentSize;
	}
	
	public static <E extends Composite> CompositeField<E> newComposite(final Class<E> valueClass)
	{
		return new CompositeField<E>(false, false, valueClass);
	}
	
	public CompositeField<E> toFinal()
	{
		return new CompositeField<E>(true, optional, valueClass);
	}
	
	public CompositeField<E> optional()
	{
		return new CompositeField<E>(isfinal, true, valueClass);
	}
	
	@Override
	protected void onMount()
	{
		final LinkedHashMap<FunctionField, FunctionField> templateToComponent =
			new LinkedHashMap<FunctionField, FunctionField>();
		FunctionField mandatoryComponent = null;
		
		for(Map.Entry<String, FunctionField> e : templates.entrySet())
		{
			final FunctionField template = e.getValue();
			final FunctionField component = copy(template);
			addSource(component, toCamelCase(e.getKey()), ComputedInstance.get());
			templateToComponent.put(template, component);
			if(optional && mandatoryComponent==null && template.isMandatory())
				mandatoryComponent = component;
		}
		if(optional && mandatoryComponent==null)
			throw new IllegalArgumentException("valueClass of optional composite must have at least one mandatory field in " + valueClass.getName());
		
		this.templateToComponent = templateToComponent;
		this.mandatoryComponent = mandatoryComponent;
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

	@SuppressWarnings("unchecked")
	public <X extends FunctionField> X of(final X template)
	{
		final X result = (X)templateToComponent.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + toString());
		return result;
	}
	
	public List<FunctionField> getTemplates()
	{
		return Collections.unmodifiableList(new ArrayList<FunctionField>(templateToComponent.keySet()));
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
			new Wrapper("get").
			addComment("Returns the value of {0}.").
			setReturn(valueClass));
		
		if(!isfinal)
		{
			result.add(
				new Wrapper("set").
				addComment("Sets a new value for {0}.").
				addThrows(getInitialExceptions()).
				addParameter(valueClass));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	@SuppressWarnings("unchecked")
	public E get(final Item item)
	{
		if(mandatoryComponent!=null && mandatoryComponent.get(item)==null)
			return null;
		
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
			setValues[i++] = e.getValue().map(value!=null ? value.get(e.getKey()) : null);
		item.set(setValues);
	}

	@SuppressWarnings("unchecked")
	public SetValue[] execute(E value, Item exceptionItem)
	{
		final SetValue[] result = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField, FunctionField> e : templateToComponent.entrySet())
			result[i++] = e.getValue().map(value!=null ? value.get(e.getKey()) : null);
		return result;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<Class<? extends Throwable>>();
		for(final FunctionField<?> member : templates.values())
			result.addAll(member.getInitialExceptions());
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	public Class getInitialType()
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

	public boolean isMandatory()
	{
		return !optional;
	}

	public SetValue map(E value)
	{
		return new SetValue<E>(this, value);
	}
	
	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #of(FunctionField)} instead
	 */
	@Deprecated
	public <X extends FunctionField> X getComponent(final X template)
	{
		return of(template);
	}
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.InstrumentContext;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.misc.ComputedElement;

public final class CompositeField<E extends Composite> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final boolean optional;
	private final Class<E> valueClass;

	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace 
	private final CompositeType<E> valueType;
	private final LinkedHashMap<String, FunctionField> templates;
	private final int componentSize;

	private final LinkedHashMap<FunctionField, FunctionField> templateToComponent;
	private final HashMap<FunctionField, FunctionField> componentToTemplate;
	private final List<FunctionField> componentList;
	private final FunctionField mandatoryComponent;
	private final FunctionField isNullComponent;
	private final CheckConstraint unison;

	private CompositeField(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = valueClass;

		this.valueType = CompositeType.get(valueClass);
		this.templates = valueType.templates;
		this.componentSize = valueType.componentSize;

		if(!InstrumentContext.isRunning())
		{
			final LinkedHashMap<FunctionField, FunctionField> templateToComponent =
				new LinkedHashMap<FunctionField, FunctionField>();
			final HashMap<FunctionField, FunctionField> componentToTemplate =
				new HashMap<FunctionField, FunctionField>();
			FunctionField mandatoryComponent = null;
			final ArrayList<Condition> isNull    = optional ? new ArrayList<Condition>() : null;
			final ArrayList<Condition> isNotNull = optional ? new ArrayList<Condition>() : null;

			for(final Map.Entry<String, FunctionField> e : templates.entrySet())
			{
				final FunctionField template = e.getValue();
				final FunctionField component = copy(template);
				addSource(component, e.getKey(), ComputedElement.get());
				templateToComponent.put(template, component);
				componentToTemplate.put(component, template);
				if(optional && mandatoryComponent==null && template.isMandatory())
					mandatoryComponent = component;
				if(optional)
				{
					isNull.add(component.isNull());
					if(template.isMandatory())
						isNotNull.add(component.isNotNull());
				}
			}
			if(optional && mandatoryComponent==null)
				throw new IllegalArgumentException("valueClass of optional composite must have at least one mandatory field in " + valueClass.getName());

			this.templateToComponent = templateToComponent;
			this.componentToTemplate = componentToTemplate;
			this.componentList = Collections.unmodifiableList(new ArrayList<FunctionField>(templateToComponent.values()));
			this.mandatoryComponent = mandatoryComponent;
			this.isNullComponent = optional ? mandatoryComponent : componentList.get(0);
			if(optional)
				addSource(this.unison = new CheckConstraint(Cope.and(isNull).or(Cope.and(isNotNull))), "unison");
			else
				this.unison = null;
		}
		else
		{
			this.templateToComponent = null;
			this.componentToTemplate = null;
			this.componentList = null;
			this.mandatoryComponent = null;
			this.isNullComponent = null;
			this.unison = null;
		}
	}

	public static <E extends Composite> CompositeField<E> create(final Class<E> valueClass)
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

	private FunctionField copy(FunctionField f)
	{
		if(isfinal)
			f = (FunctionField)f.toFinal();
		if(optional)
			f = (FunctionField)f.optional();
		f = f.noDefault();
		return f;
	}

	public <X extends FunctionField> X of(final X template)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)templateToComponent.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + toString());
		return result;
	}

	public <X extends FunctionField> X getTemplate(final X component)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)componentToTemplate.get(component);
		if(result==null)
			throw new IllegalArgumentException(component + " is not a component of " + toString());
		return result;
	}

	public List<FunctionField> getTemplates()
	{
		return valueType.getTemplates();
	}

	public List<FunctionField> getComponents()
	{
		return componentList;
	}

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.getByAnnotations(CompositeField.class, this, super.getWrappers());
	}

	@Wrap(order=10, doc="Returns the value of {0}.")
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
		return newValue(initargs);
	}

	public E newValue(final SetValue... setValues)
	{
		return valueType.newValue(setValues);
	}

	@Wrap(order=20,
			doc="Sets a new value for {0}.",
			thrownGetter=Thrown.class,
			hide=FinalGetter.class)
	@SuppressWarnings("unchecked")
	public void set(final Item item, final E value)
	{
		final SetValue[] setValues = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField, FunctionField> e : templateToComponent.entrySet())
			setValues[i++] = e.getValue().map(value!=null ? value.get(e.getKey()) : null);
		item.set(setValues);
	}

	private static final class FinalGetter implements BooleanGetter<CompositeField>
	{
		public boolean get(final CompositeField feature)
		{
			return feature.isFinal();
		}
	}

	private static final class Thrown implements ThrownGetter<CompositeField<?>>
	{
		public Set<Class<? extends Throwable>> get(final CompositeField<?> feature)
		{
			return feature.getInitialExceptions();
		}
	}

	@SuppressWarnings("unchecked")
	public SetValue[] execute(final E value, final Item exceptionItem)
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

	@Deprecated
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

	public Class<E> getValueClass()
	{
		return valueClass;
	}

	public SetValue<E> map(final E value)
	{
		return SetValue.map(this, value);
	}

	// convenience methods for conditions and views ---------------------------------

	public IsNullCondition isNull()
	{
		return isNullComponent.isNull();
	}

	public IsNullCondition isNotNull()
	{
		return isNullComponent.isNotNull();
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

	/**
	 * @deprecated Use {@link #create(Class)} instead
	 */
	@Deprecated
	public static <E extends Composite> CompositeField<E> newComposite(final Class<E> valueClass)
	{
		return create(valueClass);
	}
}

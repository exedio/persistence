/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IsNullCondition;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class CompositeField<E extends Composite> extends Pattern implements Settable<E>, TemplatedField<E>
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final boolean optional;
	private final Class<E> valueClass;

	private final CompositeType<E> valueType;
	private final int componentSize;

	private final LinkedHashMap<FunctionField<?>, FunctionField<?>> templateToComponent;
	private final HashMap<FunctionField<?>, FunctionField<?>> componentToTemplate;
	private final List<? extends FunctionField<?>> componentList;
	private final FunctionField<?> mandatoryComponent;
	private final FunctionField<?> isNullComponent;
	private final CheckConstraint unison;

	private final HashMap<Feature, Feature> templateToComponentFeature;
	private final HashMap<Feature, Feature> componentToTemplateFeature;

	private CompositeField(final boolean isfinal, final boolean optional, final Class<E> valueClass)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		this.valueClass = valueClass;

		this.valueType = CompositeType.newTypeOrExisting(valueClass);
		this.componentSize = valueType.componentSize;

		final LinkedHashMap<FunctionField<?>, FunctionField<?>> templateToComponent = new LinkedHashMap<>();
		final HashMap<FunctionField<?>, FunctionField<?>> componentToTemplate = new HashMap<>();
		FunctionField<?> mandatoryComponent = null;
		final ArrayList<Condition> isNull    = optional ? new ArrayList<>() : null;
		final ArrayList<Condition> isNotNull = optional ? new ArrayList<>() : null;
		final HashMap<Feature, Feature> templateToComponentFeature = new HashMap<>();
		final HashMap<Feature, Feature> componentToTemplateFeature = new HashMap<>();

		final CopyMapper mapper = new CopyMapper();
		for(final Map.Entry<String, FunctionField<?>> e : valueType.getTemplateMap().entrySet())
		{
			final FunctionField<?> template = e.getValue();
			final FunctionField<?> component = copy(template);
			addSourceFeature(component, e.getKey(), new FeatureAnnotatedElementAdapter(template), valueClass);
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
			mapper.put(template, component);
			templateToComponentFeature.put(template, component);
			componentToTemplateFeature.put(component, template);
		}
		if(optional && mandatoryComponent==null)
			throw new IllegalArgumentException("valueClass of optional composite must have at least one mandatory field in " + valueClass.getName());

		for(final Map.Entry<String, CheckConstraint> e : valueType.getConstraintMap().entrySet())
		{
			final CheckConstraint template = e.getValue();
			final CheckConstraint component = template.copy(mapper);
			addSourceFeature(component, e.getKey(), new FeatureAnnotatedElementAdapter(template), valueClass);
			templateToComponentFeature.put(template, component);
			componentToTemplateFeature.put(component, template);
		}

		this.templateToComponent = templateToComponent;
		this.componentToTemplate = componentToTemplate;
		this.componentList = List.copyOf(templateToComponent.values());
		this.mandatoryComponent = mandatoryComponent;
		this.isNullComponent = optional ? mandatoryComponent : componentList.get(0);
		this.unison = optional ? addSourceFeature(new CheckConstraint(Cope.and(isNull).or(Cope.and(isNotNull))), "unison") : null;
		this.templateToComponentFeature = templateToComponentFeature;
		this.componentToTemplateFeature = componentToTemplateFeature;
	}

	public static <E extends Composite> CompositeField<E> create(final Class<E> valueClass)
	{
		return new CompositeField<>(false, false, valueClass);
	}

	public CompositeField<E> toFinal()
	{
		return new CompositeField<>(true, optional, valueClass);
	}

	public CompositeField<E> optional()
	{
		return new CompositeField<>(isfinal, true, valueClass);
	}

	private FunctionField<?> copy(FunctionField<?> f)
	{
		if(isfinal)
			f = f.toFinal();
		if(optional)
			f = f.optional();
		f = f.noDefault();
		return f;
	}

	/**
	 * Returns the component created for the given template within this CompositeField.
	 * The reverse operation is {@link #getTemplate(FunctionField)}.
	 */
	public <X extends FunctionField<?>> X of(final X template)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)templateToComponent.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + this);
		return result;
	}

	/**
	 * Returns the template the given component was created for.
	 * The reverse operation is {@link #of(FunctionField)}.
	 */
	public <X extends FunctionField<?>> X getTemplate(final X component)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)componentToTemplate.get(component);
		if(result==null)
			throw new IllegalArgumentException(component + " is not a component of " + this);
		return result;
	}

	@Override
	public <X extends Feature> X of(final X template)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)templateToComponentFeature.get(template);
		if(result==null)
			throw new IllegalArgumentException(template + " is not a template of " + this);
		return result;
	}

	@Override
	public <X extends Feature> X getTemplate(final X component)
	{
		@SuppressWarnings("unchecked")
		final X result = (X)componentToTemplateFeature.get(component);
		if(result==null)
			throw new IllegalArgumentException(component + " is not a component of " + this);
		return result;
	}

	public List<? extends FunctionField<?>> getTemplates()
	{
		return valueType.templateList;
	}

	@Override
	public List<? extends FunctionField<?>> getComponents()
	{
		return componentList;
	}

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public E get(@Nonnull final Item item)
	{
		if(mandatoryComponent!=null && mandatoryComponent.get(item)==null)
			return null;

		final SetValue[] initargs = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField<?>, FunctionField<?>> e : templateToComponent.entrySet())
		{
			initargs[i++] = SetValue.map(((FunctionField)e.getKey()), e.getValue().get(item));
		}
		return newValue(initargs);
	}

	public E newValue(final SetValue<?>... setValues)
	{
		return valueType.newValue(setValues);
	}

	@Wrap(order=20,
			doc=Wrap.SET_DOC,
			thrownGetter=InitialExceptionsSettableGetter.class,
			hide=FinalSettableGetter.class)
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final E value)
	{
		FinalViolationException.check(this, item);

		final SetValue[] setValues = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField<?>, FunctionField<?>> e : templateToComponent.entrySet())
			setValues[i++] = SetValue.map(((FunctionField) e.getValue()), value!=null ? value.get(e.getKey()) : null);
		item.set(setValues);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public SetValue[] execute(final E value, final Item exceptionItem)
	{
		final SetValue[] result = new SetValue[componentSize];
		int i = 0;
		for(final Map.Entry<FunctionField<?>, FunctionField<?>> e : templateToComponent.entrySet())
			result[i++] = SetValue.map(((FunctionField)e.getValue()), value!=null ? value.get(e.getKey()) : null);
		return result;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isInitial()
	{
		return isfinal || !optional;
	}

	@Override
	public boolean isMandatory()
	{
		return !optional;
	}

	@Override
	public Class<?> getInitialType()
	{
		return valueClass;
	}

	@Override
	public CompositeType<E> getValueType()
	{
		return valueType;
	}

	@Override
	public Class<E> getValueClass()
	{
		return valueClass;
	}

	// convenience methods for conditions and views ---------------------------------

	public IsNullCondition<?> isNull()
	{
		return isNullComponent.isNull();
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition isNull(final Join join)
	{
		return isNull().bind(join);
	}

	public IsNullCondition<?> isNotNull()
	{
		return isNullComponent.isNotNull();
	}

	/**
	 * @deprecated Use {@link Condition#bind(Join)} instead.
	 */
	@Deprecated
	public Condition isNotNull(final Join join)
	{
		return isNotNull().bind(join);
	}
}

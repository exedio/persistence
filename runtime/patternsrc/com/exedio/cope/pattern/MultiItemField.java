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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.misc.Arrays;
import com.exedio.cope.util.Cast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MultiItemField<E> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1L;

	private final Class<E> valueClass;
	private final Class<? extends Item>[] componentClasses;
	private final List<ItemField<?>> components;
	private final boolean optional;
	private final boolean isFinal;
	private final boolean unique;
	private final DeletePolicy policy;
	private final LinkedHashMap<Class<? extends Item>, FunctionField<?>[]> copyToMap;

	private MultiItemField(
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		this(false, false, false, DeletePolicy.FORBID, new LinkedHashMap<Class<? extends Item>, FunctionField<?>[]>(), valueClass, componentClasses);
	}

	private MultiItemField(
			final boolean isFinal,
			final boolean optional,
			final boolean unique,
			final DeletePolicy policy,
			final LinkedHashMap<Class<? extends Item>, FunctionField<?>[]> copyToMap,
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		this.isFinal = isFinal;
		this.optional = optional;
		this.unique = unique;
		this.policy = requireNonNull(policy, "policy");
		this.copyToMap = requireNonNull(copyToMap);
		this.valueClass = requireNonNull(valueClass, "valueClass");
		this.components = createComponents(isFinal, unique, policy, this.copyToMap, valueClass, componentClasses);

		this.componentClasses = Arrays.copyOf(componentClasses);
		for(final ItemField<?> component : components)
		{
			// TODO: simpleName might not be unique
			addSource(component, component.getValueClass().getSimpleName());
		}
		addSource(createXORCheckConstraint(), "xor");
	}

	private static ArrayList<ItemField<?>> createComponents(
			final boolean isFinal,
			final boolean unique,
			final DeletePolicy policy,
			final Map<Class<? extends Item>, FunctionField<?>[]> copyToMap,
			final Class<?> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		if(componentClasses.length<=1)
		{
			throw new IllegalArgumentException("must use at least 2 componentClasses");
		}
		final ArrayList<ItemField<?>> components = new ArrayList<>();
		for(int i = 0; i<componentClasses.length; i++)
		{
			final Class<? extends Item> componentClass = requireNonNull(componentClasses[i], "componentClass");
			if(!valueClass.isAssignableFrom(componentClass))
			{
				throw new IllegalArgumentException("valueClass >" + valueClass + "< must be assignable from componentClass >"
						+componentClass+"<");
			}

			// don't allow different mixin classes to (potentially) share instances
			// because:
			// - unique constraints on ItemFields wouldn't work
			// - searching source would need to be adapted
			for(int j = 0; j<componentClasses.length; j++)
			{
				if(i!=j&&componentClasses[i].isAssignableFrom(componentClasses[j]))
					throw new IllegalArgumentException("componentClasses must not be super-classes of each other: "+componentClasses[i]
							+" is assignable from "+componentClasses[j]);
			}

			ItemField<?> component = ItemField.create(componentClass).optional();
			if(isFinal)
				component = component.toFinal();
			if(unique)
				component = component.unique();

			switch(policy)
			{
				case FORBID:
					// is  by default
					break;
				case CASCADE:
					component = component.cascade();
					break;
				default:
					throw new RuntimeException(); // NULLIFY not yet implemented
			}
			assert policy==component.getDeletePolicy();

			if(copyToMap.get(componentClass) != null)
			{
				for(final FunctionField<?> copy : copyToMap.get(componentClass))
				{
					component = component.copyTo(copy);
				}
			}
			components.add(component);
		}
		return components;
	}

	private CheckConstraint createXORCheckConstraint()
	{
		final List<Condition> ors = new ArrayList<>(components.size());
		for(final ItemField<?> i : components)
		{
			final List<Condition> ands = new ArrayList<>(components.size());
			for(final ItemField<?> j : components)
			{
				if(i==j)
				{
					if(isMandatory())
					{
						ands.add(j.isNotNull());
					}
				}
				else
				{
					ands.add(j.isNull());
				}
			}
			ors.add(Cope.and(ands));
		}
		return new CheckConstraint(Cope.or(ors));
	}

	public static <E> MultiItemField<E> create(
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		return new MultiItemField<>(valueClass, componentClasses);
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: generic array
	public static <E> MultiItemField<E> create(
			final Class<E> valueClass,
			final Class<? extends Item> componentClass1,
			final Class<? extends Item> componentClass2)
	{
		return create(valueClass, new Class[]{componentClass1, componentClass2});
	}

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: generic array
	public static <E> MultiItemField<E> create(
			final Class<E> valueClass,
			final Class<? extends Item> componentClass1,
			final Class<? extends Item> componentClass2,
			final Class<? extends Item> componentClass3)
	{
		return create(valueClass, new Class[]{componentClass1, componentClass2, componentClass3});
	}

	public List<Class<? extends Item>> getComponentClasses()
	{
		return Collections.unmodifiableList(java.util.Arrays.asList(componentClasses));
	}

	public E get(final Item item)
	{
		for(final ItemField<?> component : components)
		{
			final Item value = component.get(item);
			if(value!=null)
			{
				return valueClass.cast(value);
			}
		}

		if(isMandatory())
			throw new NullPointerException("multiItemField " + this + " is mandatory but has no value set");
		else
			return null;
	}

	public void set(final Item item, final E value)
	{
		item.set(map(value));
	}

	public List<ItemField<?>> getComponents()
	{
		return Collections.unmodifiableList(components);
	}

	public <X extends Item> ItemField<X> of(final Class<X> componentClass)
	{
		for(final ItemField<?> component : components)
		{
			if(component.getValueClass()==componentClass)
			{
				return component.as(componentClass);
			}
		}
		throw new IllegalArgumentException("class >"+componentClass+"< is not supported by "+this);
	}

	public Condition equal(final E value)
	{
		if(value == null)
			return isNull();
		for(final ItemField<?> component : components)
		{
			if(component.getValueClass().isInstance(value))
			{
				return Cope.equalAndCast(component, value);
			}
		}
		return Condition.FALSE;
	}

	public SetValue<E> map(final E value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		if(value==null && isMandatory())
		{
			// avoid CheckViolationException
			throw MandatoryViolationException.create(this, exceptionItem);
		}

		final SetValue<?>[] result = new SetValue<?>[components.size()];

		boolean valueSet = false;
		for(int i = 0; i<components.size(); i++)
		{
			final ItemField<?> component = components.get(i);
			if(component.getValueClass().isInstance(value))
			{
				result[i] = Cope.mapAndCast(component, value);
				valueSet = true;
			}
			else
			{
				result[i] = component.mapNull();
			}
		}
		if(value!=null && !valueSet)
		{
			Cast.verboseCast(valueClass, value); // throws ClassCastException

			throw new IllegalInstanceException(this, exceptionItem, value.getClass());
		}
		return result;
	}

	private static final class IllegalInstanceException extends ConstraintViolationException
	{
		private final MultiItemField<?> feature;
		private final Class<?> valueClass;

		IllegalInstanceException(
				final MultiItemField<?> feature,
				final Item item,
				final Class<?> valueClass)
		{
			super(item, null);
			this.feature = feature;
			this.valueClass = valueClass;
		}

		@Override
		public Feature getFeature()
		{
			return feature;
		}

		@Override
		protected String getMessage(final boolean withFeature)
		{
			return
					"illegal instance" + getItemPhrase() +
					", value is " + valueClass.getName() +
					(withFeature ? (" for "+ feature) : "") +
					", must be one of " + feature.getComponentClasses() + '.';
		}

		private static final long serialVersionUID = 1l;
	}

	public Condition isNull()
	{
		return nullCondition(false);
	}

	public Condition isNotNull()
	{
		return nullCondition(true);
	}

	private Condition nullCondition(final boolean not)
	{
		final Condition[] parts = new Condition[components.size()];
		int i = 0;
		for(final ItemField<?> component : components)
		{
			parts[i++] = not
					? component.isNotNull()
					: component.isNull();
		}
		return not
			? Cope.or (parts)
			: Cope.and(parts);
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public boolean isMandatory()
	{
		return !optional;
	}

	public boolean isInitial()
	{
		return isFinal() || isMandatory();
	}

	public Class<E> getInitialType()
	{
		return valueClass;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(isMandatory())
			result.add(MandatoryViolationException.class);
		return result;
	}

	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}

	public MultiItemField<E> toFinal()
	{
		return new MultiItemField<>(true, optional, unique, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> optional()
	{
		return new MultiItemField<>(isFinal, true, unique, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> unique()
	{
		return new MultiItemField<>(isFinal, optional, true, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> cascade()
	{
		return new MultiItemField<>(isFinal, optional, unique, DeletePolicy.CASCADE, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> copyTo(
			final Class<? extends Item> componentClass,
			final FunctionField<?> copy)
	{
		{
			final List<Class<? extends Item>> classes = java.util.Arrays.asList(componentClasses);
			if(!classes.contains(requireNonNull(componentClass, "componentClass")))
				throw new IllegalArgumentException(
						"illegal componentClass " + componentClass + ", " +
						"must be one of " + classes + '.');
		}

		final LinkedHashMap<Class<? extends Item>, FunctionField<?>[]> map = new LinkedHashMap<>(copyToMap);
		if(map.get(componentClass)!=null)
		{
			final FunctionField<?>[] copys = map.get(componentClass);
			final int length = copys.length;
			final FunctionField<?>[] result = new FunctionField<?>[length + 1];
			System.arraycopy(copys, 0, result, 0, length);
			result[length] = copy;
			map.put(componentClass, result);
		}
		else
		{
			map.put(componentClass, new FunctionField<?>[]{copy});
		}
		return new MultiItemField<>(isFinal, optional, unique, policy, map, valueClass, componentClasses);
	}
}

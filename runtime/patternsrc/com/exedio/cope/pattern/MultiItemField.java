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

import static com.exedio.cope.ItemField.DeletePolicy;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.misc.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
	private final boolean mandatory;
	private final boolean isFinal;
	private final boolean unique;
	private final DeletePolicy policy;
	private final Map<Class<? extends Item>, FunctionField<?>[]> copyToMap;

	private MultiItemField(
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		this(false, false, false, DeletePolicy.FORBID, null, valueClass, componentClasses);
	}

	private MultiItemField(
			final boolean isFinal,
			final boolean optional,
			final boolean unique,
			final DeletePolicy policy,
			final Map<Class<? extends Item>, FunctionField<?>[]> copyToMap,
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		this.isFinal = isFinal;
		this.mandatory = !optional;
		this.unique = unique;
		this.policy = requireNonNull(policy, "policy");
		if(copyToMap != null)
		{
			this.copyToMap = copyToMap;
		}
		else
		{
			this.copyToMap = new HashMap<>();
			for(final Class<? extends Item> componentClass : componentClasses)
			{
				this.copyToMap.put(componentClass, null);
			}
		}
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
			if(policy == DeletePolicy.CASCADE)
				component = component.cascade();
			if(copyToMap.get(componentClass) != null)
			{
				for(final FunctionField<?> functionField : copyToMap.get(componentClass))
				{
					component = component.copyTo(functionField);
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
				@SuppressWarnings("unchecked")// checked in constructor
				final E result = (E)value;
				return result;
			}
		}

		if(isMandatory())
			throw new NullPointerException("multiItemField " + this + " is mandatory but has no value set");
		else
			return null;
	}

	public final void set(final Item item, final E value)
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
				return equal(value, component);
			}
		}
		return Condition.FALSE;
	}

	private <X extends Item> Condition equal(final E value, final ItemField<X> component)
	{
		@SuppressWarnings("unchecked")
		final X typedValue = (X) value;
		return component.equal(typedValue);
	}

	public SetValue<E> map(final E value)
	{
		return SetValue.map(this, value);
	}

	@Override
	@SuppressWarnings(
	{"unchecked", "rawtypes"})
	public SetValue<?>[] execute(final E value, final Item exceptionItem)
	{
		final SetValue<?>[] result = new SetValue<?>[components.size()];

		boolean valueSet = false;
		for(int i = 0; i<components.size(); i++)
		{
			final ItemField component = components.get(i);
			if(component.getValueClass().isInstance(value))
			{
				result[i] = component.map(component.getValueClass().cast(value));
				valueSet = true;
			}
			else
			{
				result[i] = component.mapNull();
			}
		}
		if(value!=null&&Item.class.isAssignableFrom(value.getClass())&&!valueSet)
		{
			final StringBuilder sb = new StringBuilder("value class should be on of <");
			for(final Iterator<ItemField<?>> it = components.iterator(); it.hasNext();)
			{
				final ItemField component = it.next();
				sb.append(component.getValueClass().getSimpleName());
				if(it.hasNext())
					sb.append(",");
			}
			sb.append("> but was <");
			sb.append(value.getClass().getSimpleName());
			sb.append(">");
			throw new IllegalArgumentException(sb.toString());
		}
		if(isMandatory()&&!valueSet)
		{
			throw new IllegalArgumentException(this+" is mandatory");
		}
		return result;
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
		Condition c = null;
		for(final ItemField<?> component : components)
		{
			final Condition part = not
					? component.isNotNull()
					: component.isNull();

			c = c==null
					? part
					: not
							? c.or(part)
							: c.and(part);
		}
		return c;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public boolean isMandatory()
	{
		return mandatory;
	}

	public boolean isInitial()
	{
		return true;
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

	public MultiItemField<E> optional()
	{
		return new MultiItemField<>(isFinal, true, unique, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> toFinal()
	{
		return new MultiItemField<>(true, !mandatory, unique, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> unique()
	{
		return new MultiItemField<>(isFinal, !mandatory, true, policy, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> cascade()
	{
		return new MultiItemField<>(isFinal, !mandatory, unique, DeletePolicy.CASCADE, copyToMap, valueClass, componentClasses);
	}

	public MultiItemField<E> copyTo(
			final Class<? extends Item> clazz,
			final FunctionField<?> functionField)
	{
		final Map<Class<? extends Item>, FunctionField<?>[]> map = new HashMap<>(copyToMap);
		if(map.get(clazz) != null)
		{
			final FunctionField<?>[] functionFields = map.get(clazz);
			final int length = functionFields.length;
			final FunctionField<?>[] result = new FunctionField<?>[length + 1];
			System.arraycopy(functionFields, 0, result, 0, length);
			result[length] = functionField;
			map.put(clazz, result);
		}
		else
		{
			map.put(clazz, new FunctionField<?>[]{functionField});
		}
		return new MultiItemField<>(isFinal, !mandatory, unique, policy, map, valueClass, componentClasses);
	}
}

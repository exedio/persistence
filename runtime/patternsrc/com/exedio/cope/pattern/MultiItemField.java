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
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Arrays;
import com.exedio.cope.misc.CopeNameUtil;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@WrapFeature
public final class MultiItemField<E> extends Pattern implements Settable<E>
{
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	public static final Class[] EMPTY_CLASS_ARRAY = {};

	private final Class<E> valueClass;
	private final Class<? extends Item>[] componentClasses;
	private final List<ItemField<?>> components;
	private final boolean optional;
	private final boolean isFinal;
	private final boolean unique;
	private final DeletePolicy policy;
	private final LinkedHashMap<Class<? extends Item>, FunctionField<?>[]> copyToMap;

	@SuppressWarnings("unchecked")
	private MultiItemField(
			final Class<E> valueClass)
	{
		this(false, false, false, DeletePolicy.FORBID, new LinkedHashMap<>(), valueClass, EMPTY_CLASS_ARRAY);
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
		if(policy==DeletePolicy.NULLIFY)
		{
			if(isFinal)
				throw new IllegalArgumentException("final multi-item field cannot have delete policy nullify");
			if(!optional)
				throw new IllegalArgumentException("mandatory multi-item field cannot have delete policy nullify");
		}
		this.copyToMap = requireNonNull(copyToMap);
		this.valueClass = requireNonNull(valueClass, "valueClass");
		this.components = createComponents(isFinal, unique, policy, this.copyToMap, valueClass, componentClasses);

		this.componentClasses = Arrays.copyOf(componentClasses);
		for(final ItemField<?> component : components)
		{
			// TODO: postfix might not be unique
			final Class<? extends Item> componentClass = component.getValueClass();
			final CopeSchemaName schemaName = componentClass.getAnnotation(CopeSchemaName.class);
			addSourceFeature(
					component,
					CopeNameUtil.getAndFallbackToSimpleName(componentClass),
					schemaName!=null ? CustomAnnotatedElement.create(schemaName) : null);
		}
	}

	private static ArrayList<ItemField<?>> createComponents(
			final boolean isFinal,
			final boolean unique,
			final DeletePolicy policy,
			final Map<Class<? extends Item>, FunctionField<?>[]> copyToMap,
			final Class<?> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
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
				case NULLIFY:
					component = component.nullify();
					break;
				default:
					throw new RuntimeException();
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

	@Override
	protected void onMount()
	{
		super.onMount();
		if(componentClasses.length<=1)
		{
			throw new IllegalArgumentException("must use at least 2 componentClasses in "+getID());
		}
		addSourceFeature(createXORCheckConstraint(), "xor");
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

	public static <E> MultiItemField<E> create(final Class<E> valueClass)
	{
		return new MultiItemField<>(valueClass);
	}

	/**
	 * @deprecated use {@link #create(Class)} and {@link #canBe(Class)} instead
	 */
	@Deprecated
	public static <E> MultiItemField<E> create(
			final Class<E> valueClass,
			final Class<? extends Item>[] componentClasses)
	{
		MultiItemField<E> result = create(valueClass);
		for(final Class<? extends Item> componentClass: componentClasses)
		{
			result = result.canBe(componentClass.asSubclass(valueClass));
		}
		return result;
	}

	/**
	 * @deprecated use {@link #create(Class)} and {@link #canBe(Class)} instead
	 */
	@SuppressWarnings({"unchecked","rawtypes","RedundantSuppression"}) // OK: generic array
	@Deprecated
	public static <E> MultiItemField<E> create(
			final Class<E> valueClass,
			final Class<? extends Item> componentClass1,
			final Class<? extends Item> componentClass2)
	{
		return create(valueClass, new Class[]{componentClass1, componentClass2});
	}

	/**
	 * @deprecated use {@link #create(Class)} and {@link #canBe(Class)} instead
	 */
	@SuppressWarnings({"unchecked","rawtypes","RedundantSuppression"}) // OK: generic array
	@Deprecated
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
		return List.of(componentClasses);
	}

	@Wrap(order=10, doc=Wrap.GET_DOC, nullability=NullableIfOptional.class)
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

	@Wrap(order=20, doc=Wrap.SET_DOC, hide=FinalSettableGetter.class, thrownGetter=InitialExceptionsSettableGetter.class)
	public void set(final Item item, @Parameter(nullability=NullableIfOptional.class) final E value)
	{
		item.set(SetValue.map(this, value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @return null if there is no matching item.
	 * @throws NullPointerException if value is null.
	 */
	@Wrap(order=100, name=Wrap.FOR_NAME,
			doc=Wrap.FOR_DOC,
			docReturn=Wrap.FOR_RETURN,
			hide=NonUniqueMultiItemFieldGetter.class)
	@Nullable
	public <P extends Item> P searchUnique(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) @Nonnull final E value)
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		requireNonNull(value, () -> "cannot search uniquely for null on " + getID());
		return type.searchSingleton(equal(value));
	}

	/**
	 * Finds an item by its unique fields.
	 * @throws NullPointerException if value is null.
	 * @throws IllegalArgumentException if there is no matching item.
	 */
	@Wrap(order=110, name=Wrap.FOR_STRICT_NAME,
			doc=Wrap.FOR_DOC,
			hide=NonUniqueMultiItemFieldGetter.class,
			thrown=@Wrap.Thrown(value=IllegalArgumentException.class, doc=Wrap.FOR_STRICT_THROWN))
	@Nonnull
	public <P extends Item> P searchUniqueStrict(
			@Nonnull final Class<P> typeClass,
			@Parameter(doc=Wrap.FOR_PARAM) @Nonnull final E value)
		throws IllegalArgumentException
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		requireNonNull(value, () -> "cannot search uniquely for null on " + getID());
		return type.searchSingletonStrict(equal(value));
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
		return Condition.ofFalse();
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
				result[i] = SetValue.map(component, null);
			}
		}
		if(value!=null && !valueSet)
		{
			valueClass.cast(value); // throws ClassCastException

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
					", must be one of " + feature.getComponentClasses();
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

	@Override
	public boolean isFinal()
	{
		return isFinal;
	}

	@Override
	public boolean isMandatory()
	{
		return !optional;
	}

	@Override
	public boolean isInitial()
	{
		return isFinal() || isMandatory();
	}

	public boolean isUnique()
	{
		return unique;
	}

	@Override
	public Class<E> getInitialType()
	{
		return valueClass;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(isMandatory())
			result.add(MandatoryViolationException.class);
		for (final ItemField<?> component : components)
			result.addAll(component.getInitialExceptions());
		return result;
	}

	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}

	public MultiItemField<E> canBe(final Class<? extends E> componentClass)
	{
		if (!Item.class.isAssignableFrom(requireNonNull(componentClass, "componentClass")))
			throw new IllegalArgumentException("is not a subclass of "+Item.class.getName()+": "+componentClass.getName());
		final Class<? extends Item>[] newComponentClasses = java.util.Arrays.copyOf(componentClasses, componentClasses.length+1);
		newComponentClasses[componentClasses.length] = componentClass.asSubclass(Item.class);
		return new MultiItemField<>(isFinal, optional, unique, policy, copyToMap, valueClass, newComponentClasses);
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

	/**
	 * Additionally makes the field {@link #optional() optional}.
	 */
	public MultiItemField<E> nullify()
	{
		return new MultiItemField<>(isFinal, true, unique, DeletePolicy.NULLIFY, copyToMap, valueClass, componentClasses);
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
			final FunctionField<?>[] result = Arrays.append(copys, copy);
			map.put(componentClass, result);
		}
		else
		{
			map.put(componentClass, new FunctionField<?>[]{copy});
		}
		return new MultiItemField<>(isFinal, optional, unique, policy, map, valueClass, componentClasses);
	}
}

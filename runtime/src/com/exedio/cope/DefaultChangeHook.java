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

package com.exedio.cope;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.Arrays;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Implements a {@link ChangeHook} that calls
 * {@code static beforeNewCopeItem(SetValue[])},
 * {@link Item#afterNewCopeItem() Item.afterNewCopeItem},
 * {@link Item#beforeSetCopeItem(SetValue[]) Item.beforeSetCopeItem}, and
 * {@link Item#beforeDeleteCopeItem() Item.beforeDeleteCopeItem}
 * according to their specifications.
 * Without this hook, these methods are not called at all.
 * If {@link ModelBuilder#changeHooks(ChangeHook.Factory...) ModelBuilder.changeHooks}
 * is not called, this hook is installed by default.
 */
public final class DefaultChangeHook implements ChangeHook
{
	public static Factory factory()
	{
		return FACTORY;
	}

	private static final Factory FACTORY = new Factory()
	{
		@Override public ChangeHook create(final Model model)
		{
			return new DefaultChangeHook(requireNonNull(model, "model"));
		}
		@Override public String toString()
		{
			return DefaultChangeHook.class.getName();
		}
	};

	private final HashMap<Type<?>, Method[]> beforeNewItemMethods;

	private DefaultChangeHook(final Model model)
	{
		final HashSet<Type<?>> done = new HashSet<>(); // just for assert
		final HashMap<Type<?>, Method[]> beforeNewItemMethods = new HashMap<>();
		for(final Type<?> type : model.getTypesSortedByHierarchy())
		{
			// TODO compute once per JavaClass
			final Method declared = getBeforeNewItemMethod(type.getJavaClass());
			final Type<?> supertype = type.getSupertype();
			assert supertype==null || done.contains(supertype) : type;
			final Method[] inherited = supertype!=null ? beforeNewItemMethods.get(supertype) : null;
			final Method[] result;
			if(declared==null)
				result = inherited;
			else if(inherited==null)
				result = new Method[]{declared};
			else
				result = Arrays.prepend(declared, inherited);

			if(beforeNewItemMethods.putIfAbsent(type, result)!=null)
				throw new RuntimeException(type.id);
			//noinspection AssertWithSideEffects OK: done is maintained just for assertions
			assert done.add(type);
		}
		this.beforeNewItemMethods = beforeNewItemMethods;
	}

	private static Method getBeforeNewItemMethod(
			final Class<? extends Item> javaClass)
	{
		final Method result;
		try
		{
			result = javaClass.getDeclaredMethod(BEFORE_NAME, SetValue[].class);
		}
		catch(final NoSuchMethodException ignored)
		{
			return null;
		}

		if(!Modifier.isStatic(result.getModifiers()))
			throw newBeforeException(javaClass, "must be static");
		if(!SetValue[].class.equals(result.getReturnType()))
			throw newBeforeException(javaClass,
					"must return SetValue[], but returns " + result.getReturnType().getName());

		result.setAccessible(true);
		return result;
	}

	private static final String BEFORE_NAME = "beforeNewCopeItem";

	private static IllegalArgumentException newBeforeException(
			final Class<? extends Item> javaClass,
			final String detail)
	{
		return new IllegalArgumentException(
				"method " + BEFORE_NAME + "(SetValue[]) in class " + javaClass.getName() + ' ' + detail);
	}

	@Override
	public SetValue<?>[] beforeNew(final Type<?> type, SetValue<?>[] setValues)
	{
		final Method[] beforeNewItemMethods = this.beforeNewItemMethods.get(type);
		if(beforeNewItemMethods!=null)
		{
			try
			{
				for(final Method m : beforeNewItemMethods)
					setValues = (SetValue<?>[])m.invoke(null, (Object)setValues);
			}
			catch(final InvocationTargetException e)
			{
				final Throwable cause = e.getCause();
				if(cause instanceof RuntimeException)
					throw (RuntimeException)cause;
				throw new RuntimeException(type.id, e);
			}
			catch(final IllegalAccessException e)
			{
				throw new RuntimeException(type.id, e);
			}
		}
		return setValues;
	}

	@Override
	public void afterNew(final Item item)
	{
		item.afterNewCopeItem();
	}

	@Override
	public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] setValues)
	{
		return requireNonNull(
				item.beforeSetCopeItem(setValues),
				"setValues after beforeSetCopeItem");
	}

	@Override
	public void beforeDelete(final Item item)
	{
		item.beforeDeleteCopeItem();
	}

	@Override
	public String toString()
	{
		return DefaultChangeHook.class.getName();
	}
}

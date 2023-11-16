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

import static com.exedio.cope.misc.ReflectionTypes.parameterized;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class JavaView extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final Variant variant;

	private JavaView(final Variant variant)
	{
		this.variant = requireNonNull(variant);
	}

	private abstract static class Variant
	{
		void onMount(final JavaView view) {}
		abstract Class<?> getValueType();
		abstract java.lang.reflect.Type getValueGenericType();
		abstract Object get(final Item item);
	}


	/**
	 * @deprecated
	 * because dependence on Java Reflection API.
	 * Use one of
	 * {@link #createInt(Function)},
	 * {@link #createLong(Function)},
	 * {@link #createString(Function)},
	 * {@link #createDate(Function)},
	 * {@link #create(Class, Function)},
	 * {@link #createList(Class, Function)},
	 * {@link #createSet(Class, Function)}, or
	 * {@link #createMap(Class, Class, Function)}
	 * instead.
	 */
	@Deprecated
	public JavaView()
	{
		this(new ReflectionVariant());
	}

	@Deprecated
	private static final class ReflectionVariant extends Variant
	{
		private Mount mountIfMounted;

		@Override
		void onMount(final JavaView view)
		{
			this.mountIfMounted = new Mount(view);
		}

		private Mount mount()
		{
			return requireMounted(mountIfMounted);
		}

		@Override Class<?> getValueType()
		{
			return mount().valueType;
		}

		@Override java.lang.reflect.Type getValueGenericType()
		{
			return mount().valueGenericType;
		}

		@Override Object get(final Item item)
		{
			return mount().get(item);
		}
	}

	private static final class Mount
	{
		final JavaView view;
		final Method getter;
		final Class<?> valueType;
		final java.lang.reflect.Type valueGenericType;

		Mount(final JavaView view)
		{
			this.view = view;
			final String name = view.getName();
			final String getterName =
				("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1)).replace("-", "");

			final Class<?> javaClass = view.getType().getJavaClass();
			final Method getter;
			try
			{
				getter = javaClass.getDeclaredMethod(getterName, (Class<?>[])null);
			}
			catch(final NoSuchMethodException e)
			{
				throw new IllegalArgumentException("no suitable getter method " + getterName + " found for java view " + name, e);
			}
			getter.setAccessible(true);

			this.getter = getter;
			this.valueType = replacePrimitive(getter.getReturnType());
			this.valueGenericType = replacePrimitive(getter.getGenericReturnType());
		}

		Object get(final Item item)
		{
			try
			{
				return getter.invoke(item, (Object[])null);
			}
			catch(final InvocationTargetException e)
			{
				final Throwable cause = e.getTargetException();
				if(cause instanceof RuntimeException)
					throw (RuntimeException)cause;
				else
					throw new RuntimeException(view.toString(), e);
			}
			catch(final ReflectiveOperationException e)
			{
				throw new RuntimeException(view.toString(), e);
			}
		}
	}


	public static <I extends Item> JavaView createInt(final Function<I,Integer> target)
	{
		return create(Integer.class, target);
	}

	public static <I extends Item> JavaView createLong(final Function<I,Long> target)
	{
		return create(Long.class, target);
	}

	public static <I extends Item> JavaView createString(final Function<I,String> target)
	{
		return create(String.class, target);
	}

	public static <I extends Item> JavaView createDate(final Function<I,Date> target)
	{
		return create(Date.class, target);
	}

	public static <I extends Item, R> JavaView create(
			final Class<R> valueType,
			final Function<I,R> target)
	{
		return new JavaView(new FunctionVariant(valueType, valueType, target));
	}

	public static <I extends Item, E> JavaView createList(
			final Class<E> elementType,
			final Function<I,? extends List<? extends E>> target)
	{
		return create(target, List.class, elementType);
	}

	public static <I extends Item, E> JavaView createSet(
			final Class<E> elementType,
			final Function<I,? extends Set<? extends E>> target)
	{
		return create(target, Set.class, elementType);
	}

	public static <I extends Item, K, V> JavaView createMap(
			final Class<K> keyType,
			final Class<V> valueType,
			final Function<I,? extends Map<? extends K,? extends V>> target)
	{
		return create(target, Map.class, keyType, valueType);
	}

	private static <I extends Item, R> JavaView create(
			final Function<I,? extends R> target,
			final Class<R> rawType,
			final java.lang.reflect.Type... actualTypeArguments)
	{
		return new JavaView(new FunctionVariant(rawType, parameterized(rawType, actualTypeArguments), target));
	}

	private static final class FunctionVariant extends Variant
	{
		private final Class<?> valueType;
		private final java.lang.reflect.Type valueGenericType;
		private final Function<? extends Item,?> target;

		private FunctionVariant(
				final Class<?> valueType,
				final java.lang.reflect.Type valueGenericType,
				final Function<? extends Item,?> target)
		{
			this.valueType = requireNonNull(valueType, "valueType");
			if(valueType.isPrimitive())
				throw new IllegalArgumentException(
						"valueType must not be primitive, but was " + valueType);
			if(valueType==Void.class)
				throw new IllegalArgumentException(
						"valueType must not be " + Void.class);
			this.valueGenericType = valueGenericType;
			this.target = requireNonNull(target, "target");
		}

		@Override Class<?> getValueType()
		{
			return valueType;
		}

		@Override java.lang.reflect.Type getValueGenericType()
		{
			return valueGenericType;
		}

		@SuppressWarnings("unchecked")
		@Override Object get(final Item item)
		{
			return ((Function<Item,?>)target).apply(item);
		}
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		variant.onMount(this);
	}

	public Class<?> getValueType()
	{
		return variant.getValueType();
	}

	public java.lang.reflect.Type getValueGenericType()
	{
		return variant.getValueGenericType();
	}

	public Object get(final Item item)
	{
		return variant.get(item);
	}

	static java.lang.reflect.Type replacePrimitive(final java.lang.reflect.Type type)
	{
		return type instanceof Class<?> ? replacePrimitive((Class<?>)type) : type;
	}

	static Class<?> replacePrimitive(final Class<?> clazz)
	{
		if(clazz==boolean.class) return Boolean  .class;
		if(clazz==char   .class) return Character.class;
		if(clazz==byte   .class) return Byte     .class;
		if(clazz==short  .class) return Short    .class;
		if(clazz==int    .class) return Integer  .class;
		if(clazz==long   .class) return Long     .class;
		if(clazz==float  .class) return Float    .class;
		if(clazz==double .class) return Double   .class;
		return clazz;
	}
}

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

import com.exedio.cope.misc.CopeNameUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import javax.annotation.Nonnull;

public final class TypesBound
{
	private TypesBound()
	{
		// prevent instantiation
	}

	private static final HashMap<Class<? extends Item>, Type<?>> types = new HashMap<>();

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see Type#isBound()
	 */
	public static <T extends Item> Type<T> forClass(final Class<T> javaClass)
	{
		return forClassUnchecked(javaClass).as(javaClass);
	}

	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see Type#isBound()
	 */
	public static Type<?> forClassUnchecked(final Class<? extends Item> javaClass)
	{
		final Type<?> result = types.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}

	/**
	 * @deprecated Use {@link #newType(Class, Function)} or {@link #newTypeAbstract(Class)} instead.
	 */
	@Deprecated
	public static <T extends Item> Type<T> newType(final Class<T> javaClass)
	{
		return newType(javaClass, Type.reflectionActivator(javaClass));
	}

	public static <T extends Item> Type<T> newTypeAbstract(
			@Nonnull final Class<T> javaClass)
	{
		return newType(javaClass, null);
	}

	public static <T extends Item> Type<T> newType(
			@Nonnull final Class<T> javaClass,
			final Function<ActivationParameters,T> activator)
	{
		requireNonNull(javaClass, "javaClass");
		if(types.containsKey(javaClass))
			throw new IllegalArgumentException("class is already bound to a type: " + javaClass.getName());

		// id
		final String id = CopeNameUtil.getAndFallbackToSimpleName(javaClass);

		// supertype
		final Class<?> superclass = javaClass.getSuperclass();

		final Type<? super T> supertype;
		if(superclass.equals(Item.class) || !Item.class.isAssignableFrom(superclass))
			supertype = null;
		else
			supertype = forClass(castSupertype(superclass));

		// features
		final Features features = new Features();
		for(final Map.Entry<Feature, Field> entry : getFeatures(javaClass).entrySet())
		{
			final Field field = entry.getValue();
			features.put(
					CopeNameUtil.getAndFallbackToName(field),
					entry.getKey(),
					field);
		}

		final Type<T> result = new Type<>(
				javaClass,
				activator,
				javaClass, // annotationSource
				true, // bound
				id,
				null, null, // pattern[Postfix]
				supertype,
				features);

		final Type<?> previous = types.putIfAbsent(javaClass, result);
		if(previous!=null)
			throw new RuntimeException(javaClass.getName());

		return result;
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: Class.getSuperclass() does not support generics
	private static Class<Item> castSupertype(final Class o)
	{
		return o;
	}

	public static SortedMap<Feature, Field> getFeatures(final Class<?> clazz)
	{
		final TreeMap<Feature, Field> result = new TreeMap<>(INSTANTIATION_COMPARATOR);
		try
		{
			for(final Field field : clazz.getDeclaredFields())
			{
				if(field.isSynthetic())
					continue;
				if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
					continue;
				if(!Feature.class.isAssignableFrom(field.getType()))
					continue;
				if(field.isAnnotationPresent(CopeIgnore.class))
					continue;

				field.setAccessible(true);
				final Feature feature = (Feature)field.get(null);
				if(feature==null)
					throw new NullPointerException(clazz.getName() + '#' + field.getName());
				{
					final Field duplicate = result.put(feature, field);
					if(duplicate!=null)
						throw new IllegalArgumentException(
								clazz.getName() + '#' + field.getName() +
								" is same as #" + duplicate.getName());
				}
			}
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(clazz.getName(), e);
		}
		return result;
	}

	private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

	/**
	 * Needed for not relying on order of result of {@link Class#getDeclaredFields()}.
	 */
	private static final Comparator<Feature> INSTANTIATION_COMPARATOR =
			(f1, f2) -> (f1==f2) ? 0 : Integer.compare(f1.instantiationOrder, f2.instantiationOrder);


	// TODO reuse futures
	// TODO use some direct future if javaclass is already in types
	static <T extends Item> TypeFuture<T> future(final Class<T> javaClass)
	{
		return new TypeFuture<>()
		{
			@Override
			public Type<T> get()
			{
				return forClass(javaClass);
			}
			@Override
			public String toString()
			{
				return javaClass.getName();
			}
		};
	}
}

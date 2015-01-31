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

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.misc.CopeNameUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class TypesBound
{
	private TypesBound()
	{
		// prevent instantiation
	}

	private static final HashMap<Class<? extends Item>, Type<? extends Item>> types = new HashMap<>();

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
	public static Type<?> forClassUnchecked(final Class<?> javaClass)
	{
		final Type<? extends Item> result = types.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}

	public static <T extends Item> Type<T> newType(final Class<T> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		if(types.containsKey(javaClass))
			throw new IllegalArgumentException("class is already bound to a type: " + javaClass.getName());

		// id
		final String id = CopeNameUtil.getAndFallbackToSimpleName(javaClass);

		// abstract
		final boolean isAbstract = Modifier.isAbstract(javaClass.getModifiers());

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
					(AnnotatedElement)field);
		}

		final Type<T> result = new Type<>(
				javaClass,
				javaClass, // annotationSource
				true,
				id,
				null, // pattern
				isAbstract,
				supertype,
				features);

		final Type<?> previous = types.put(javaClass, result);
		if(previous!=null)
			throw new RuntimeException(javaClass.getName());

		return result;
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: Class.getSuperclass() does not support generics
	private static Class<Item> castSupertype(final Class o)
	{
		return o;
	}

	@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
	public static SortedMap<Feature, Field> getFeatures(final Class<?> clazz)
	{
		// needed for not relying on order of result of Method#getDeclaredFields
		final TreeMap<Feature, Field> result = new TreeMap<>(INSTANTIATION_COMPARATOR);
		try
		{
			for(final Field field : clazz.getDeclaredFields())
			{
				if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
					continue;
				if(!Feature.class.isAssignableFrom(field.getType()))
					continue;

				field.setAccessible(true);
				final Feature feature = (Feature)field.get(null);
				if(feature==null)
					throw new NullPointerException(clazz.getName() + '#' + field.getName());
				result.put(feature, field);
			}
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(clazz.getName(), e);
		}
		return result;
	}

	private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

	private static final Comparator<Feature> INSTANTIATION_COMPARATOR = new Comparator<Feature>()
	{
		@Override
		public int compare(final Feature f1, final Feature f2)
		{
			if(f1==f2)
				return 0;

			final int o1 = f1.instantiationOrder;
			final int o2 = f2.instantiationOrder;

			if(o1<o2)
				return -1;
			else
			{
				assert o1>o2 : f1.toString() + '/' + f2;
				return 1;
			}
		}
	};


	// TODO reuse futures
	// TODO use some direct future if javaclass is already in types
	static final <E extends Item> Future<E> future(final Class<E> javaClass)
	{
		return new Future<>(javaClass);
	}

	private static final class Future<T extends Item> extends TypeFuture<T>
	{
		Future(final Class<T> javaClass)
		{
			super(javaClass);
		}

		@Override
		Type<T> get()
		{
			return forClass(javaClass);
		}

		@Override
		public String toString()
		{
			return javaClass.getName();
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link ItemField#create(Class)} instead
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass)
	{
		return ItemField.create(valueClass);
	}

	/**
	 * @deprecated Use {@link ItemField#create(Class, DeletePolicy)} instead
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		return ItemField.create(valueClass, policy);
	}
}

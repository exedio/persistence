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

package com.exedio.cope;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.exedio.cope.ItemField.DeletePolicy;

public final class TypesBound
{
	private TypesBound()
	{
		// prevent instantiation
	}
	
	private static final HashMap<Class<? extends Item>, Type<? extends Item>> types =
		new HashMap<Class<? extends Item>, Type<? extends Item>>();
	
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
		if(javaClass==null)
			throw new NullPointerException("javaClass");
		if(types.containsKey(javaClass))
			throw new IllegalArgumentException("class is already bound to a type: " + javaClass.getName());
		
		// id
		final String id = id(javaClass, javaClass.getSimpleName());
		
		// abstract
		final boolean isAbstract = Modifier.isAbstract(javaClass.getModifiers());
		
		// supertype
		final Class superclass = javaClass.getSuperclass();
		
		final Type<? super T> supertype;
		if(superclass.equals(Item.class) || !Item.class.isAssignableFrom(superclass))
			supertype = null;
		else
			supertype = forClass(castSupertype(superclass));
		
		// features
		final Features features = new Features();
		try
		{
			for(final java.lang.reflect.Field field : javaClass.getDeclaredFields())
			{
				if((field.getModifiers()&STATIC_FINAL)!=STATIC_FINAL)
					continue;
				if(!Feature.class.isAssignableFrom(field.getType()))
					continue;
				
				field.setAccessible(true);
				final Feature feature = (Feature)field.get(null);
				if(feature==null)
					throw new NullPointerException(javaClass.getName() + '#' + field.getName());
				final String featureName = id(field, field.getName());
				features.put(featureName, feature, (AnnotatedElement)field);
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(javaClass.getName(), e);
		}

		final Type<T> result = new Type<T>(
				javaClass,
				javaClass, // annotationSource
				true,
				id,
				null, // pattern
				isAbstract,
				supertype,
				features);
		
		final Type previous = types.put(javaClass, result);
		if(previous!=null)
			throw new RuntimeException(javaClass.getName());
		
		return result;
	}
	
	@SuppressWarnings("unchecked") // OK: Class.getSuperclass() does not support generics
	private static Class<Item> castSupertype(final Class o)
	{
		return o;
	}
	
	private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;
	
	private static final String id(final AnnotatedElement annotatedElement, final String fallback)
	{
		final CopeID featureAnnotation =
			annotatedElement.getAnnotation(CopeID.class);
		return
			featureAnnotation!=null
			? featureAnnotation.value()
			: fallback;
	}
	
	
	// ItemField
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass)
	{
		return new ItemField<E>(new Future<E>(valueClass));
	}
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		return new ItemField<E>(new Future<E>(valueClass), policy);
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
}

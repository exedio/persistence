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

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;

final class TypesExclusive
{
	private TypesExclusive()
	{
		// prevent instantiation
	}
	
	private static final HashMap<Class<? extends Item>, Type<? extends Item>> typesByClass =
		new HashMap<Class<? extends Item>, Type<? extends Item>>();
	
	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see #isJavaClassExclusive()
	 */
	static <X extends Item> Type<X> forClass(final Class<X> javaClass)
	{
		return forClassUnchecked(javaClass).as(javaClass);
	}
	
	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see #isJavaClassExclusive()
	 */
	static Type<?> forClassUnchecked(final Class<?> javaClass)
	{
		final Type<? extends Item> result = typesByClass.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}
	
	static <C extends Item> Type<C> newType(final Class<C> javaClass)
	{
		if(javaClass==null)
			throw new NullPointerException("javaClass");
		
		// id
		final CopeID annotation = javaClass.getAnnotation(CopeID.class);
		final String id =
			annotation!=null
			? annotation.value()
			: javaClass.getSimpleName();
		
		// abstract
		final boolean isAbstract = Modifier.isAbstract(javaClass.getModifiers());
		
		// supertype
		final Class superclass = javaClass.getSuperclass();
		
		final Type<? super C> supertype;
		if(superclass.equals(Item.class) || !Item.class.isAssignableFrom(superclass))
			supertype = null;
		else
			supertype = forClass(castSupertype(superclass));
		
		// featureMap
		final LinkedHashMap<String, Feature> featureMap = new LinkedHashMap<String, Feature>();
		final java.lang.reflect.Field[] fields = javaClass.getDeclaredFields();
		try
		{
			for(final java.lang.reflect.Field field : fields)
			{
				if((field.getModifiers()&expectedModifier)!=expectedModifier)
					continue;
				if(!Feature.class.isAssignableFrom(field.getType()))
					continue;
				
				field.setAccessible(true);
				final Feature feature = (Feature)field.get(null);
				if(feature==null)
					throw new RuntimeException(javaClass.getName() + '-' + field.getName());
				final CopeID featureAnnotation = field.getAnnotation(CopeID.class);
				final String featureName =
					featureAnnotation!=null
					? featureAnnotation.value()
					: field.getName();
				featureMap.put(featureName, feature);
				feature.setAnnotationField(field);
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}

		final Type<C> result = new Type<C>(
				javaClass,
				true,
				id,
				null, // pattern
				isAbstract,
				supertype,
				featureMap);
		
		typesByClass.put(javaClass, result);
		return result;
	}
	
	@SuppressWarnings("unchecked") // OK: Class.getSuperclass() does not support generics
	private static Class<Item> castSupertype(final Class o)
	{
		return o;
	}
	
	private static final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
}

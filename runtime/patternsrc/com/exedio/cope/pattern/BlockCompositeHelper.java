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

import com.exedio.cope.Pattern;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

final class BlockCompositeHelper
{
	static <T> void assertFinalSubClass(
			final Class<? extends Pattern> fieldClass,
			final Class<T> superClass,
			final Class<? extends T> javaClass)
	{
		requireNonNull(javaClass, "javaClass");
		if(!superClass.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(
					fieldClass.getSimpleName() + " requires a subclass of " + superClass.getName() + ": " + javaClass.getName());
		if(superClass.equals(javaClass))
			throw new IllegalArgumentException(
					fieldClass.getSimpleName() + " requires a subclass of " + superClass.getName() + " but not " + superClass.getSimpleName() + " itself");
		if(!Modifier.isFinal(javaClass.getModifiers()))
			throw new IllegalArgumentException(
					fieldClass.getSimpleName() + " requires a final class: " + javaClass.getName());
	}

	static <E> Constructor<E> getConstructor(final Class<E> valueClass, final Class<?> parameter)
	{
		final Constructor<E> constructor;
		final String classID = valueClass.getName();
		try
		{
			constructor = valueClass.getDeclaredConstructor(parameter);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					classID + " does not have a constructor " +
					valueClass.getSimpleName() + '(' + getName(parameter) + ')', e);
		}
		constructor.setAccessible(true);
		return constructor;
	}

	private static String getName(final Class<?> clazz)
	{
		final Class<?> componentType = clazz.getComponentType();
		return
			componentType!=null
			? componentType.getName() + "[]"
			: clazz.getName();
	}


	private BlockCompositeHelper()
	{
		// prevent instantiation
	}
}

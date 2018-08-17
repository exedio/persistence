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

import com.exedio.cope.Feature;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.Field;

final class SerializedReflectionField implements Serializable
{
	static SerializedReflectionField make(final Feature feature, final Field field)
	{
		requireNonNull(field, "field");

		final Class<?> clazz = field.getDeclaringClass();
		final String fieldName = field.getName();
		try
		{
			if(resolve(clazz, fieldName)!=feature)
				throw new RuntimeException("inconsistent feature " + fieldName + '/' + feature);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(clazz.getName() + '#' + fieldName, e);
		}

		return new SerializedReflectionField(clazz, fieldName);
	}

	private static final long serialVersionUID = 1l;

	private final Class<?> clazz;
	private final String fieldName;

	private SerializedReflectionField(final Class<?> clazz, final String fieldName)
	{
		this.clazz = clazz;
		this.fieldName = fieldName;
	}

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
	 */
	private Object readResolve() throws InvalidObjectException
	{
		try
		{
			return resolve(clazz, fieldName);
		}
		catch(final ReflectiveOperationException ignored)
		{
			throw new InvalidObjectException(clazz.getName() + '#' + fieldName);
		}
	}

	private static Feature resolve(final Class<?> clazz, final String fieldName)
		throws ReflectiveOperationException
	{
		final Field field = clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		return (Feature)field.get(null);
	}
}

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

package com.exedio.cope.misc;

import com.exedio.cope.Model;

public final class ModelByString
{
	private static final char DIVIDER = '#';

	public static Model get(final String name)
	{
		final int pos = name.indexOf(DIVIDER);
		if(pos<=0)
			throw new IllegalArgumentException("does not contain '" + DIVIDER + "', but was " + name);
		final String className = name.substring(0, pos);
		final String fieldName = name.substring(pos+1);

		final Class<?> clazz;
		try
		{
			clazz = Class.forName(className);
		}
		catch(final ClassNotFoundException e)
		{
			throw new IllegalArgumentException("class " + className + " does not exist.", e);
		}

		final java.lang.reflect.Field field;
		try
		{
			field = clazz.getField(fieldName);
		}
		catch(final NoSuchFieldException e)
		{
			throw new IllegalArgumentException("field " + fieldName + " in " + clazz + " does not exist or is not public.", e);
		}

		final Object result;
		try
		{
			result = field.get(null);
		}
		catch(final IllegalAccessException e)
		{
			throw new IllegalArgumentException("accessing " + field, e);
		}

		if(result==null)
			throw new IllegalArgumentException("field " + clazz.getName() + '#' + field.getName() + " is null.");
		if(!(result instanceof Model))
			throw new IllegalArgumentException("field " + clazz.getName() + '#' + field.getName() + " is not a model, but a " + result.getClass().getName() + '.');

		return (Model)result;
	}

	private ModelByString()
	{
		// prevent instantiation
	}
}

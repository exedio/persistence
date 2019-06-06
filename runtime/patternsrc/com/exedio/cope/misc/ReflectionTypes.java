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

import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

public final class ReflectionTypes
{
	private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

	public static ParameterizedType parameterized(
			final Class<?> rawType,
			final Type... actualTypeArguments)
	{
		return new ParameterizedToplevel(actualTypeArguments, rawType);
	}

	private static final class ParameterizedToplevel implements ParameterizedType
	{
		private final Type[] arguments;
		private final Class<?> rawType;

		ParameterizedToplevel(
				final Type[] actualTypeArguments,
				final Class<?> rawType)
		{
			this.arguments = requireNonEmptyAndCopy(actualTypeArguments, "actualTypeArguments");
			this.rawType = requireNonNull(rawType, "rawType");

			{
				final Type ownerType = rawType.getDeclaringClass();
				if(ownerType!=null)
					throw new IllegalArgumentException("ownerType not supported: " + ownerType);
			}
			{
				final TypeVariable<?>[] parameters = rawType.getTypeParameters();
				if(parameters.length!=actualTypeArguments.length)
					throw new MalformedParameterizedTypeException();
			}
		}

		@Override
		public Type[] getActualTypeArguments()
		{
			return com.exedio.cope.misc.Arrays.copyOf(arguments);
		}

		@Override
		public Class<?> getRawType()
		{
			return rawType;
		}

		@Override
		public Type getOwnerType()
		{
			return null;
		}

		@Override
		public boolean equals(final Object other)
		{
			if(this==other)
				return true;

			if(!(other instanceof ParameterizedType))
				return false;

			final ParameterizedType o = (ParameterizedType)other;
			return
					null==o.getOwnerType() &&
					rawType.equals(o.getRawType()) &&
					Arrays.equals(arguments, o.getActualTypeArguments());
		}

		@Override
		public int hashCode()
		{
			return
					Objects.hashCode(rawType) ^
					Arrays.hashCode(arguments);
		}

		@Override
		public String toString()
		{
			final StringBuilder bf = new StringBuilder();

			bf.append(rawType.getName());

			bf.append('<');

			boolean first = true;
			for(final Type argument : arguments)
			{
				if(first)
					first = false;
				else
					bf.append(", ");

				bf.append(argument.getTypeName());
			}

			bf.append('>');

			return bf.toString();
		}
	}

	public static Type sub(final Class<?> clazz)
	{
		return new WildcardType()
		{
			@Override
			public Type[] getUpperBounds()
			{
				return new Type[]{clazz};
			}

			@Override
			public Type[] getLowerBounds()
			{
				return EMPTY_TYPE_ARRAY;
			}
		};
	}

	private ReflectionTypes()
	{
		// prevent instantiation
	}
}

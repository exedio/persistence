/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

class GenericResolver
{
	private final Class interfaze;
	private final Class clazz;
	private final Type[] parameters;

	GenericResolver(
			final Class interfaze,
			final Class clazz,
			final Type... parameters)
	{
		if(!interfaze.isInterface())
			throw new IllegalArgumentException("not an interface: " + interfaze);
		if(clazz.isInterface())
			throw new IllegalArgumentException("not an class: " + clazz);
		if(clazz.getTypeParameters().length!=parameters.length)
			throw new IllegalArgumentException(
					"mismatching parameter count " +
					Arrays.toString(clazz.getTypeParameters()) + ' ' +
					Arrays.toString(parameters));

		this.interfaze = interfaze;
		this.clazz = clazz;
		this.parameters = parameters;

		if(!Arrays.asList(clazz.getInterfaces()).contains(interfaze))
			throw new RuntimeException(); // TODO remove
	}

	List<Type> get()
	{
		final ParameterizedType gi = getGenericInterface(clazz, interfaze);
		final Type[] arguments = gi.getActualTypeArguments();
		assert arguments.length==interfaze.getTypeParameters().length;
		filter(arguments);
		return Arrays.asList(arguments);
	}

	private static ParameterizedType getGenericInterface(
			final Class clazz,
			final Class interfaze)
	{
		for(final Type t : clazz.getGenericInterfaces())
		{
			final ParameterizedType tpt = (ParameterizedType)t;
			if(tpt.getRawType()==interfaze)
				return tpt;
		}
		throw new RuntimeException(clazz.toString() + '/' + interfaze);
	}

	void filter(final Type[] types)
	{
		for(int typeIndex = 0; typeIndex<types.length; typeIndex++)
		{
			final Type type = types[typeIndex];
			types[typeIndex] = filter(type);
		}
	}

	Type filter(final Type type)
	{
		if(type instanceof TypeVariable)
		{
			return filter((TypeVariable)type);
		}
		else if(type instanceof ParameterizedType)
		{
			return filter((ParameterizedType)type);
		}
		else
		{
			return type;
		}
	}

	Type filter(final TypeVariable type)
	{
		final TypeVariable[] typeParameters = clazz.getTypeParameters();
		for(int typeParameterIndex = 0; typeParameterIndex<typeParameters.length; typeParameterIndex++)
		{
			if(typeParameters[typeParameterIndex]==type)
				return parameters[typeParameterIndex];
		}
		return type;
	}

	Type filter(final ParameterizedType parameterizedType)
	{
		return new ParameterizedType()
		{
			public Type getRawType()
			{
				return parameterizedType.getRawType();
			}

			public Type getOwnerType()
			{
				return parameterizedType.getOwnerType();
			}

			public Type[] getActualTypeArguments()
			{
				final Type[] arguments = parameterizedType.getActualTypeArguments();
				filter(arguments);
				return arguments;
			}

			@Override
			public boolean equals(final Object other)
			{
				if(!(other instanceof ParameterizedType))
					return false;

				final ParameterizedType o = (ParameterizedType)other;
				return
					getRawType().equals(o.getRawType()) &&
					getOwnerType().equals(o.getOwnerType()) &&
					java.util.Arrays.equals(getActualTypeArguments(), o.getActualTypeArguments());
			}

			@Override
			public int hashCode()
			{
				return
					getRawType().hashCode() ^
					getOwnerType().hashCode() ^
					java.util.Arrays.hashCode(getActualTypeArguments());
			}

			@Override
			public String toString()
			{
				return
					getRawType().toString() +
					'<' + java.util.Arrays.toString(getActualTypeArguments()) + '>';
			}
		};
	}
}

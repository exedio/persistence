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

	GenericResolver(final Class interfaze)
	{
		if(!interfaze.isInterface())
			throw new IllegalArgumentException("not an interface: " + interfaze);

		this.interfaze = interfaze;
	}

	List<Type> get(final Class clazz, final Type... parameters)
	{
		if(clazz.isInterface())
			throw new IllegalArgumentException("not an class: " + clazz);
		if(clazz.getTypeParameters().length!=parameters.length)
			throw new IllegalArgumentException(
					"mismatching parameter count " +
					Arrays.toString(clazz.getTypeParameters()) + ' ' +
					Arrays.toString(parameters));
		if(!Arrays.asList(clazz.getInterfaces()).contains(interfaze))
			throw new RuntimeException(); // TODO remove

		final ParameterizedType gi = getGenericInterface(clazz, interfaze);
		final Type[] arguments = gi.getActualTypeArguments();
		assert arguments.length==interfaze.getTypeParameters().length;
		filter(clazz, parameters, arguments);
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

	void filter(final Class clazz, final Type[] parameters, final Type[] types)
	{
		for(int i = 0; i<types.length; i++)
			types[i] = filter(clazz, parameters, types[i]);
	}

	private Type filter(final Class clazz, final Type[] parameters, final Type type)
	{
		if(type instanceof TypeVariable)
			return filter(clazz, parameters, (TypeVariable)type);
		else if(type instanceof ParameterizedType)
			return filter(clazz, parameters, (ParameterizedType)type);
		else
			return type;
	}

	private Type filter(final Class clazz, final Type[] parameters, final TypeVariable type)
	{
		final TypeVariable[] typeParameters = clazz.getTypeParameters();
		for(int i = 0; i<typeParameters.length; i++)
		{
			if(typeParameters[i]==type)
				return parameters[i];
		}
		return type;
	}

	private ParameterizedType filter(final Class clazz, final Type[] parameters, final ParameterizedType type)
	{
		return new FilteredParameterizedType(clazz, parameters, type);
	}

	private class FilteredParameterizedType implements ParameterizedType
	{
		private final Class clazz;
		private final Type[] parameters;
		private final ParameterizedType type;

		FilteredParameterizedType(
				final Class clazz,
				final Type[] parameters,
				final ParameterizedType type)
		{
			this.clazz = clazz;
			this.parameters = parameters;
			this.type = type;
		}

		public Type getRawType()
		{
			return type.getRawType();
		}

		public Type getOwnerType()
		{
			return type.getOwnerType();
		}

		public Type[] getActualTypeArguments()
		{
			final Type[] arguments = type.getActualTypeArguments();
			filter(clazz, parameters, arguments);
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

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

final class GenericResolver<T>
{
	static <T> GenericResolver<T> neW(final Class<T> interfaze)
	{
		return new GenericResolver<>(interfaze);
	}


	private final Class<T> interfaze;

	private GenericResolver(final Class<T> interfaze)
	{
		if(!interfaze.isInterface())
			throw new IllegalArgumentException("not an interface: " + interfaze);

		this.interfaze = interfaze;
	}

	Type[] get(final Class<? extends T> clazz, final Type... parameters)
	{
		if(clazz.isInterface())
			throw new IllegalArgumentException("not an class: " + clazz);
		if(clazz.getTypeParameters().length!=parameters.length)
			throw new IllegalArgumentException(
					"mismatching parameter count " +
					Arrays.toString(clazz.getTypeParameters()) + ' ' +
					Arrays.toString(parameters));

		return getX(clazz, parameters);
	}

	private Type[] getX(final Class<?> clazz, final Type[] parameters)
	{
		assert !clazz.isInterface() : clazz;
		assert clazz.getTypeParameters().length==parameters.length : Arrays.toString(clazz.getTypeParameters()) + ' ' + Arrays.toString(parameters);

		final ParameterizedType gi = getGenericInterface(clazz, interfaze);
		if(gi==null)
		{
			final Class<?> superclass = clazz.getSuperclass();
			if(superclass==null)
				throw new RuntimeException(clazz.toString() + '/' + interfaze);

			final Type supertype = clazz.getGenericSuperclass();
			if(supertype instanceof Class<?>)
			{
				assert supertype==superclass;
				final Type[] arguments = getX(superclass, new Type[]{});
				filter(clazz, parameters, arguments);
				return arguments;
			}
			else if(supertype instanceof ParameterizedType)
			{
				final ParameterizedType supertypeParameterized = (ParameterizedType)supertype;
				assert supertypeParameterized.getRawType()==superclass : supertypeParameterized.getRawType().toString()+'/'+superclass;

				final Type[] superTypeArguments = supertypeParameterized.getActualTypeArguments();
				assert superTypeArguments.length==superclass.getTypeParameters().length : Arrays.toString(superTypeArguments)+'/'+Arrays.toString(superclass.getTypeParameters());

				final Type[] arguments = getX(superclass, superTypeArguments);
				filter(clazz, parameters, arguments);
				return arguments;
			}
			else
			{
				throw new RuntimeException(clazz.toString() + '/' + supertype + '/' + interfaze);
			}
		}

		final Type[] arguments = gi.getActualTypeArguments();
		assert arguments.length==interfaze.getTypeParameters().length;
		filter(clazz, parameters, arguments);
		return arguments;
	}

	private static ParameterizedType getGenericInterface(
			final Class<?> clazz,
			final Class<?> interfaze)
	{
		assert interfaze.isInterface() : interfaze;

		for(final Type t : clazz.getGenericInterfaces())
		{
			if(t instanceof ParameterizedType)
			{
				final ParameterizedType tpt = (ParameterizedType)t;
				if(tpt.getRawType()==interfaze)
					return tpt;
			}
			else if(t instanceof Class)
			{
				final Class<?> tc = (Class<?>)t;
				assert tc.isInterface() : tc;
				final ParameterizedType recurse = getGenericInterface(tc, interfaze);
				if(recurse!=null)
					return recurse;
			}
		}
		return null;
	}

	static void filter(final Class<?> clazz, final Type[] parameters, final Type[] types)
	{
		for(int i = 0; i<types.length; i++)
			types[i] = filter(clazz, parameters, types[i]);
	}

	private static Type filter(final Class<?> clazz, final Type[] parameters, final Type type)
	{
		if(type instanceof TypeVariable<?>)
			return filter(clazz, parameters, (TypeVariable<?>)type);
		else if(type instanceof ParameterizedType)
			return filter(clazz, parameters, (ParameterizedType)type);
		else
			return type;
	}

	private static Type filter(final Class<?> clazz, final Type[] parameters, final TypeVariable<?> type)
	{
		final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
		for(int i = 0; i<typeParameters.length; i++)
		{
			if(typeParameters[i]==type)
				return parameters[i];
		}
		return type;
	}

	private static ParameterizedType filter(final Class<?> clazz, final Type[] parameters, final ParameterizedType type)
	{
		return new FilteredParameterizedType(clazz, parameters, type);
	}

	private static class FilteredParameterizedType implements ParameterizedType
	{
		private final Class<?> clazz;
		private final Type[] parameters;
		private final ParameterizedType type;

		FilteredParameterizedType(
				final Class<?> clazz,
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
	}
}

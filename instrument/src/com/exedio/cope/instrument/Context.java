/*
 * Copyright (C) 2000  Ralf Wiebicke
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

import com.exedio.cope.Feature;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.LinkedList;

final class Context
{
	private final CopeFeature feature;
	private final boolean fullyQualified;
	private final WrapperX wrapper;

	Context(final CopeFeature feature, final boolean fullyQualified)
	{
		this.feature = feature;
		this.fullyQualified = fullyQualified;
		this.wrapper = null;
	}

	Context(final CopeFeature feature, final WrapperX wrapper)
	{
		this.feature = feature;
		this.fullyQualified = false;
		this.wrapper = wrapper;
	}

	private String getClassToken()
	{
		return feature.parent.name;
	}

	private String getGenericFieldParameter(final int number)
	{
		return Generics.get(feature.javaField.type).get(number);
	}

	private Class<? extends Feature> getFeatureClass()
	{
		return feature.getInstance().getClass();
	}

	private String write(final ParameterizedType t)
	{
		final StringBuilder bf = new StringBuilder(write(t.getRawType()));
		bf.append('<');
		boolean first = true;
		for(final Type a : t.getActualTypeArguments())
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(write(a));
		}
		bf.append('>');

		return bf.toString();
	}

	private String write(final TypeVariable<?> t)
	{
		if(wrapper.matchesStaticToken(t))
			return getClassToken();

		final Class<? extends Feature> featureClass = getFeatureClass();
		final Class<?> methodClass = wrapper.getMethod().getDeclaringClass();
		int typeParameterPosition = 0;
		for(final TypeVariable<?> methodClassVar : methodClass.getTypeParameters())
		{
			if(methodClassVar==t)
				return dig(featureClass, methodClass, typeParameterPosition);

			typeParameterPosition++;
		}

		throw new RuntimeException(
				t.getName() + '-' +
				Arrays.asList(t.getBounds()) + '-' +
				t.getGenericDeclaration() + '-' +
				featureClass);
	}

	private String dig(
			final Class<?> instanceClass,
			final Class<?> declarationClass,
			final int declarationTypeParameterPosition)
	{
		final LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
		for(Class<?> clazz = instanceClass; clazz!=declarationClass; clazz = clazz.getSuperclass())
			classes.add(0, clazz);

		int parameterPosition = declarationTypeParameterPosition;
		for(final Class<?> clazz : classes)
		{
			final ParameterizedType superType = (ParameterizedType)clazz.getGenericSuperclass();
			assert superType.getRawType()==clazz.getSuperclass() : superType.getRawType().toString()+'/'+clazz.getSuperclass();
			assert superType.getOwnerType()==null : superType.getOwnerType();

			final Type[] superTypeArguments = superType.getActualTypeArguments();
			assert superTypeArguments.length==clazz.getSuperclass().getTypeParameters().length;

			final Type superTypeArgument = superTypeArguments[parameterPosition];
			if(superTypeArgument instanceof Class<?>)
			{
				return ((Class<?>)superTypeArgument).getCanonicalName();
			}
			else if(superTypeArgument instanceof TypeVariable<?>)
			{
				parameterPosition = getPosition(clazz.getTypeParameters(), (TypeVariable<?>)superTypeArgument);
			}
			else
			{
				throw new RuntimeException(superTypeArgument.toString());
			}
		}
		return getGenericFieldParameter(parameterPosition);
	}

	private static int getPosition(
			final TypeVariable<?>[] typeParameters,
			final TypeVariable<?> typeParameter)
	{
		int result = 0;
		for(final TypeVariable<?> tv : typeParameters)
		{
			if(typeParameter==tv)
				return result;

			result++;
		}
		throw new RuntimeException("" + Arrays.asList(typeParameters) + '/' + typeParameter);
	}

	private String write(final WildcardType t)
	{
		final Type[] upper = t.getUpperBounds();
		if(upper.length==1)
		{
			assert t.getLowerBounds().length==0 : Arrays.asList(t.getLowerBounds()).toString();

			if(Object.class.equals(upper[0]))
				return "?";

			return "? extends " + write(upper[0]);
		}

		final Type[] lower = t.getLowerBounds();
		if(lower.length==1)
		{
			assert upper.length==0 : Arrays.asList(upper).toString();
			return "? super " + write(lower[0]);
		}

		throw new RuntimeException(Arrays.asList(upper).toString() + Arrays.asList(lower).toString());
	}

	private String write(final GenericArrayType t)
	{
		return write(t.getGenericComponentType()) + "...";
	}

	private String write(final Generics.SourceType t)
	{
		final String name = t.name;
		if(fullyQualified)
		{
			final JavaFile file = feature.parent.javaClass.file;
			{
				final Class<?> clazz = file.findTypeExternally(name);
				if(clazz!=null)
					return clazz.getCanonicalName();
			}
			{
				final JavaClass javaClass = file.repository.getJavaClass(name);
				if(javaClass!=null)
					return javaClass.getCanonicalName();
			}
			throw new RuntimeException(name);
		}
		else
		{
			return name;
		}
	}

	String write(final Type t)
	{
		if(t instanceof Class<?>)
			return ((Class<?>)t).getCanonicalName();
		else if(t instanceof GenericArrayType)
			return write((GenericArrayType)t);
		else if(t instanceof ParameterizedType)
			return write((ParameterizedType)t);
		else if(t instanceof TypeVariable<?>)
			return write((TypeVariable<?>)t);
		else if(t instanceof WildcardType)
			return write((WildcardType)t);
		else if(t instanceof Generics.SourceType)
			return write((Generics.SourceType)t);
		else
			throw new RuntimeException(t.toString());
	}
}

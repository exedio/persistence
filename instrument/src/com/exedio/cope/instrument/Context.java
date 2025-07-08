/*
 * Copyright (C) 2000  Ralf Wiebicke
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

package com.exedio.cope.instrument;

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
		final CopeType<?> type = feature.parent;
		return nameWithWildcards(type.getName(), type.getTypeParameters());
	}

	static String nameWithWildcards(final String name, final int parameters)
	{
		if(parameters<=0)
			return name;

		final StringBuilder sb = new StringBuilder(name);
		sb.append("<?");
		//noinspection StringRepeatCanBeUsed
		for(int i = 1; i<parameters; i++)
			sb.append(",?");
		sb.append('>');
		return sb.toString();
	}

	private String getGenericFieldParameter(final int number)
	{
		return ((LocalCopeFeature)feature).getTypeParameter(number);
	}

	private Class<?> getFeatureClass()
	{
		return feature.getInstance().getClass();
	}

	private String write(final ParameterizedType t, final boolean varArgs)
	{
		final StringBuilder sb = new StringBuilder(write(false, t.getRawType(), varArgs));
		sb.append('<');
		boolean first = true;
		for(final Type a : t.getActualTypeArguments())
		{
			if(first)
				first = false;
			else
				sb.append(',');

			sb.append(write(true, a, varArgs));
		}
		sb.append('>');

		return sb.toString();
	}

	private String write(final TypeVariable<?> t)
	{
		//noinspection DataFlowIssue
		if(wrapper.matchesStaticToken(t))
			return getClassToken();

		final Class<?> featureClass = getFeatureClass();
		final Class<?> methodClass = wrapper.method.getDeclaringClass();
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
		final LinkedList<Class<?>> classes = new LinkedList<>();
		for(Class<?> clazz = instanceClass; clazz!=declarationClass; clazz = clazz.getSuperclass())
			classes.add(0, clazz);

		int parameterPosition = declarationTypeParameterPosition;
		for(final Class<?> clazz : classes)
		{
			final ParameterizedType superType = (ParameterizedType)clazz.getGenericSuperclass();
			if (superType.getRawType()!=clazz.getSuperclass())
				throw new RuntimeException(superType.getRawType().toString()+'/'+clazz.getSuperclass());
			if (superType.getOwnerType()!=null)
				throw new RuntimeException(superType.getOwnerType().toString());

			final Type[] superTypeArguments = superType.getActualTypeArguments();
			if (superTypeArguments.length!=clazz.getSuperclass().getTypeParameters().length)
				throw new RuntimeException();

			final Type superTypeArgument = superTypeArguments[parameterPosition];
			if(superTypeArgument instanceof Class<?>)
			{
				return name((Class<?>)superTypeArgument);
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
		throw new RuntimeException(Arrays.asList(typeParameters) + "/" + typeParameter);
	}

	private String write(final WildcardType t, final boolean varArgs)
	{
		final Type[] upper = t.getUpperBounds();
		if(upper.length==1)
		{
			if (t.getLowerBounds().length!=0)
				throw new RuntimeException(Arrays.toString(t.getLowerBounds()));

			if(Object.class.equals(upper[0]))
				return "?";

			return "? extends " + write(true, upper[0], varArgs);
		}

		final Type[] lower = t.getLowerBounds();
		if(lower.length==1)
		{
			if (upper.length!=0)
				throw new RuntimeException(Arrays.toString(upper));
			return "? super " + write(true, lower[0], varArgs);
		}

		throw new RuntimeException(Arrays.asList(upper).toString() + Arrays.asList(lower));
	}

	private String write(final boolean withTypeParameters, final Class<?> t, final boolean varArgs)
	{
		if (varArgs && t.isArray())
		{
			return write(withTypeParameters, t.getComponentType(), false)+"...";
		}
		else
		{
			if(withTypeParameters)
				return nameWithWildcards(name(t), t.getTypeParameters().length);

			return name(t);
		}
	}

	private String write(final boolean withTypeParameters, final GenericArrayType t, final boolean varArgs)
	{
		return write(withTypeParameters, t.getGenericComponentType(), varArgs) + (varArgs?"...":"[]");
	}

	String write(final Type t, final boolean varArgs)
	{
		return write(true, t, varArgs);
	}

	private String write(final boolean withTypeParameters, final Type t, final boolean varArgs)
	{
		if(t instanceof Class<?>)
			return write(withTypeParameters, (Class<?>)t, varArgs);
		else if(t instanceof GenericArrayType)
			return write(withTypeParameters, (GenericArrayType)t, varArgs);
		else if(t instanceof ParameterizedType)
			return write((ParameterizedType)t, varArgs);
		else if(t instanceof TypeVariable<?>)
			return write((TypeVariable<?>)t);
		else if(t instanceof WildcardType)
			return write((WildcardType)t, varArgs);
		else
			throw new RuntimeException(t.toString());
	}

	private String name(final Class<?> clazz)
	{
		final String full = clazz.getCanonicalName();
		return fullyQualified ? full : feature.applyTypeShortcuts(full);
	}
}

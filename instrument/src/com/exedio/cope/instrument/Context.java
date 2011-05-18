/*
 * Copyright (C) 2000  Ralf Wiebicke
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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.LinkedList;

import com.exedio.cope.Feature;

final class Context
{
	private final CopeFeature feature;
	private final Wrapper wrapper;

	Context(final CopeFeature feature, final Wrapper wrapper)
	{
		this.feature = feature;
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

	private String write(final Class c)
	{
		if(Wrapper.ClassVariable.class.equals(c))
			return getClassToken();
		else if(Wrapper.TypeVariable0.class.equals(c))
			return getGenericFieldParameter(0);
		else if(Wrapper.TypeVariable1.class.equals(c))
			return getGenericFieldParameter(1);
		else
			return c.getCanonicalName();
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

	private String write(final TypeVariable t)
	{
		if(wrapper.matchesStaticToken(t))
			return getClassToken();

		final Class<? extends Feature> featureClass = getFeatureClass();
		final Method method = wrapper.getMethod();
		final Class methodClass = method.getDeclaringClass();
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
			final Class instanceClass,
			final Class declarationClass,
			final int typeParameterPosition)
	{
		final LinkedList<Class> classes = new LinkedList<Class>();
		for(Class clazz = instanceClass; clazz!=declarationClass; clazz = clazz.getSuperclass())
			classes.add(0, clazz);

		Class superClass = declarationClass;
		int typeParameterPosition2 = typeParameterPosition;
		for(final Class clazz : classes)
		{
			assert clazz.getSuperclass()==superClass : clazz.getSuperclass().toString()+'/'+superClass;

			final Type superType = clazz.getGenericSuperclass();

			final ParameterizedType superTypeP = (ParameterizedType)superType;
			assert superTypeP.getRawType()==superClass : superTypeP.getRawType().toString()+'/'+superClass;
			assert superTypeP.getOwnerType()==null : superTypeP.getOwnerType();

			final Type[] superTypeArguments = superTypeP.getActualTypeArguments();
			assert superTypeArguments.length==declarationClass.getTypeParameters().length;

			final Type superTypeArgument = superTypeArguments[typeParameterPosition2];
			if(superTypeArgument instanceof Class)
			{
				return ((Class)superTypeArgument).getName();
			}
			else if(superTypeArgument instanceof TypeVariable)
			{
				final TypeVariable<?> superTypeArgumentVar = (TypeVariable)superTypeArgument;
				int pos = 0;
				boolean done = false;
				for(final TypeVariable<?> tv : clazz.getTypeParameters())
				{
					if(superTypeArgumentVar==tv)
					{
						typeParameterPosition2 = pos;
						done = true;
						break;
					}
					pos++;
				}
				assert done : "" + Arrays.asList(clazz.getTypeParameters()) + '/' + superTypeArgumentVar;
			}
			else
			{
				throw new RuntimeException(superTypeArgument.toString());
			}

			superClass = clazz;
		}
		return getGenericFieldParameter(typeParameterPosition2);
	}

	private String write(final WildcardType t)
	{
		final Type[] upper = t.getUpperBounds();
		if(upper.length==1)
		{
			assert t.getLowerBounds().length==0 : Arrays.asList(t.getLowerBounds()).toString();
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

	private String write(final Wrapper.ExtendsType t)
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

			bf.append("? extends ");
			bf.append(write(a));
		}
		bf.append('>');

		return bf.toString();
	}

	String write(final Type t)
	{
		if(t instanceof Class)
			return write((Class)t);
		else if(t instanceof Wrapper.ExtendsType)
			return write((Wrapper.ExtendsType)t);
		else if(t instanceof GenericArrayType)
			return write((GenericArrayType)t);
		else if(t instanceof ParameterizedType)
			return write((ParameterizedType)t);
		else if(t instanceof TypeVariable)
			return write((TypeVariable)t);
		else if(t instanceof WildcardType)
			return write((WildcardType)t);
		else
			throw new RuntimeException(t.toString());
	}
}

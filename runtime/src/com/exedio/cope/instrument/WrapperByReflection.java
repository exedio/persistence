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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.misc.Compare;

final class WrapperByReflection
{
	static <F extends Feature> List<Wrapper> make(
			final Class<F> clazz,
			final F feature,
			final List<Wrapper> superResult)
	{
		final WrapperByReflection factory = new WrapperByReflection(clazz, feature);
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(superResult);
		factory.makeAll(result);
		return Collections.unmodifiableList(result);
	}


	private final Class<? extends Feature> clazz;
	private final Feature feature;

	private WrapperByReflection(final Class<? extends Feature> clazz, final Feature instance)
	{
		this.clazz = clazz;
		this.feature = instance;
	}

	private void makeAll(final List<Wrapper> list)
	{
		final TreeMap<Wrap, Method> methods = new TreeMap<Wrap, Method>(ORDER_COMPARATOR);

		for(final Method method : clazz.getDeclaredMethods())
		{
			if(!Modifier.isPublic(method.getModifiers()))
				continue;

			final Wrap annotation = method.getAnnotation(Wrap.class);
			if(annotation==null)
				continue;

			final Method collision = methods.put(annotation, method);
			if(collision!=null)
				throw new IllegalArgumentException(
						"duplicate @" + Wrap.class.getSimpleName() + "(order=" + annotation.order() + ") " +
						"on " + toString(collision) + " and " + toString(method));
		}

		for(final Map.Entry<Wrap, Method> entry : methods.entrySet())
		{
			final Wrapper wrapper = make(entry.getValue(), entry.getKey());
			if(wrapper!=null)
				list.add(wrapper);
		}
	}

	private static String toString(final Method method)
	{
		final StringBuilder bf =
			new StringBuilder(method.getDeclaringClass().getName());

		bf.append('#');
		bf.append(method.getName());

		bf.append('(');

		boolean first = true;
		for(final Class parameter : method.getParameterTypes())
		{
			if(first)
				first = false;
			else
				bf.append(',');

			bf.append(parameter.getName());
		}
		bf.append(')');
		return bf.toString();
	}

	private static final Comparator<Wrap> ORDER_COMPARATOR = new Comparator<Wrap>()
	{
		@Override
		public int compare(final Wrap o1, final Wrap o2)
		{
			if(o1==o2)
				return 0;

			return Compare.compare(o1.order(), o2.order());
		}
	};

	private Wrapper make(final Method method, final Wrap annotation)
	{
		{
			final Class<? extends WrapperSuppressor> suppressorClass = annotation.suppressor();
			if(suppressorClass!=WrapperSuppressorDefault.class)
			{
				final WrapperSuppressor suppressor = instantiate(suppressorClass);
				@SuppressWarnings("unchecked")
				final boolean suppressed = suppressor.isSuppressed(feature);
				if(suppressed)
					return null;
			}
		}

		final Wrapper result = new Wrapper(method);

		final Class<?>[] parameterTypes = method.getParameterTypes();
		final Type[] genericParameterTypes = method.getGenericParameterTypes();
		final int parameterOffset;
		if(parameterTypes[0]==Class.class)
		{
			final Type t = genericParameterTypes[0];
			if(t instanceof ParameterizedType)
			{
				final ParameterizedType pt = (ParameterizedType)t;
				assert pt.getRawType()==Class.class : pt.getRawType(); // because parameterTypes[0]==Class.class
				assert pt.getOwnerType()==null : pt.getOwnerType(); // because Class is not an inner class
				assert pt.getActualTypeArguments().length==1 : pt.getActualTypeArguments(); // because Class has one generic parameter

				final Type argument = pt.getActualTypeArguments()[0];
				if(argument instanceof TypeVariable)
				{
					result.setStatic((TypeVariable)argument);
					parameterOffset = 1;
				}
				else
				{
					result.setStatic(false);
					parameterOffset = 0;
				}
			}
			else
			{
				result.setStatic(false);
				parameterOffset = 0;
			}
		}
		else if(parameterTypes[0]==Item.class)
		{
			parameterOffset = 1;
		}
		else
		{
			result.setStatic(false);
			parameterOffset = 0;
		}

		{
			final Type returnType = method.getGenericReturnType();
			final String comment = annotation.docReturn();
			if(returnType!=void.class)
			{
				if(!comment.isEmpty())
					result.setReturn(returnType, comment);
				else
					result.setReturn(returnType);
			}
			else
			{
				if(!comment.isEmpty())
					throw new IllegalArgumentException('@' + "returns");
			}
		}
		{
			{
				for(final String s : annotation.doc())
					if(!s.isEmpty())
						result.addComment(s);

				final String methodWrapperPattern = annotation.name();
				if(!methodWrapperPattern.isEmpty())
				{
					result.setMethodWrapperPattern(methodWrapperPattern);
				}
				else
				{
					final Class<? extends WrapperName> nameClass = annotation.namex();
					if(nameClass!=WrapperNameDefault.class)
					{
						final WrapperName name = instantiate(nameClass);
						@SuppressWarnings("unchecked")
						final String pattern = name.get(feature);
						result.setMethodWrapperPattern(pattern);
					}
				}
			}
		}
		{
			final Annotation[][] annotations = method.getParameterAnnotations();
			for(int i = parameterOffset; i<parameterTypes.length; i++)
			{
				final Type genericParameterType = genericParameterTypes[i];
				final WrapParam paramAnn = get(WrapParam.class, annotations[i]);
				if(paramAnn==null)
					result.addParameter(genericParameterType);
				else
				{
					final String comment = paramAnn.doc();
					final String paramAnnValue = paramAnn.value();
					final String paramAnnValueFixed = paramAnnValue.isEmpty() ? "{1}" : paramAnnValue;
					if(comment.isEmpty())
						result.addParameter(genericParameterType, paramAnnValueFixed);
					else
						result.addParameter(genericParameterType, paramAnnValueFixed, comment);
				}
			}
		}
		{
			for(final Wrap.Thrown c : annotation.thrown())
			{
				final String v = c.doc();
				if(v.isEmpty())
					result.addThrows(c.value());
				else
					result.addThrows(c.value(), v);
			}
		}
		{
			final Class<? extends WrapperThrown> thrownClass = annotation.thrownx();
			if(thrownClass!=WrapperThrownDefault.class)
			{
				final WrapperThrown thrown = instantiate(thrownClass);
				@SuppressWarnings("unchecked")
				final Set<Class<? extends Throwable>> throwables = thrown.get(feature);
				for(final Class<? extends Throwable> throwable : throwables)
					result.addThrows(throwable);
			}
		}
		return result;
	}

	private static <E> E instantiate(final Class<E> clazz)
	{
		final Constructor<E> constructor;
		try
		{
			constructor = clazz.getDeclaredConstructor();
		}
		catch(final NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}

		constructor.setAccessible(true);

		final E result;
		try
		{
			result = constructor.newInstance();
		}
		catch(final InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(final InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}

		return result;
	}

	private static <A extends Annotation> A get(final Class<A> annotationClass, final Annotation[] annotations)
	{
		for(final Annotation a : annotations)
			if(a.annotationType().equals(annotationClass))
			{
				@SuppressWarnings("unchecked")
				final A result = (A)a;
				return result;
			}
		return null;
	}
}

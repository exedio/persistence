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

package com.exedio.cope.instrument;

import com.exedio.cope.Item;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
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

final class WrapperByAnnotations
{
	static List<WrapperX> make(
			final Class<?> clazz,
			final Object feature,
			final List<WrapperX> superResult,
			final boolean enableNullability)
	{
		final WrapperByAnnotations factory = new WrapperByAnnotations(clazz, feature, enableNullability);
		final ArrayList<WrapperX> result = new ArrayList<>(superResult);
		factory.makeAll(result);
		return Collections.unmodifiableList(result);
	}


	private final Class<?> clazz;
	private final Object feature;
	private final boolean enableNullability;

	private WrapperByAnnotations(final Class<?> clazz, final Object instance, final boolean enableNullability)
	{
		this.clazz = clazz;
		this.feature = instance;
		this.enableNullability = enableNullability;
	}

	private void makeAll(final List<WrapperX> list)
	{
		final TreeMap<Wrap, Method> methods = new TreeMap<>(ORDER_COMPARATOR);

		for(final Method method : clazz.getDeclaredMethods())
		{
			if(method.isSynthetic())
				continue;
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
			if(isNotHidden(entry.getKey()))
				list.add(make(entry.getValue(), entry.getKey()));
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
		for(final Class<?> parameter : method.getParameterTypes())
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

	private static final Comparator<Wrap> ORDER_COMPARATOR =
			(o1, o2) -> (o1==o2) ? 0 : Integer.compare(o1.order(), o2.order());

	private boolean isNotHidden(final Wrap annotation)
	{
		return !isAnyBooleanGetterTrue(annotation.hide());
	}

	private boolean isAnyBooleanGetterTrue(final Class<? extends BooleanGetter<?>>[] booleanGetters)
	{
		for(final Class<? extends BooleanGetter<?>> hideGetterClass : booleanGetters)
		{
			if(getBoolean(hideGetterClass))
				return true;
		}
		return false;
	}

	private Nullability getNullability(final Method method, final Wrap wrap)
	{
		return getNullability(method.getAnnotations(), wrap.nullability());
	}

	private Nullability getNullability(final Annotation[] annotations, final Class<? extends NullabilityGetter<?>> nullabilityClass)
	{
		if (enableNullability)
		{
			if (NullabilityGetterDefault.class.equals(nullabilityClass))
			{
				return Nullability.fromAnnotations(annotations);
			}
			else
			{
				@SuppressWarnings("rawtypes")
				final NullabilityGetter source = instantiate(nullabilityClass);
				@SuppressWarnings("unchecked")
				final Nullability result = source.getNullability(feature);
				return result;
			}
		}
		else
		{
			return Nullability.DEFAULT;
		}
	}

	private WrapperX make(final Method method, final Wrap annotation)
	{
		final WrapperX result = new WrapperX(method, getNullability(method, annotation));

		final Class<?>[] parameterTypes = method.getParameterTypes();
		final Class<?> parameterType0 = parameterTypes.length>0 ? parameterTypes[0] : null;
		final Type[] genericParameterTypes = method.getGenericParameterTypes();
		final int parameterOffset;
		if(parameterType0==Class.class)
		{
			final Type t = genericParameterTypes[0];
			if(t instanceof ParameterizedType)
			{
				final ParameterizedType pt = (ParameterizedType)t;
				assert pt.getRawType()==Class.class : pt.getRawType(); // because parameterTypes[0]==Class.class
				assert pt.getOwnerType()==null : pt.getOwnerType(); // because Class is not an inner class
				assert pt.getActualTypeArguments().length==1 : pt.getActualTypeArguments(); // because Class has one generic parameter

				final Type argument = pt.getActualTypeArguments()[0];
				if(argument instanceof TypeVariable<?>)
				{
					result.setStatic((TypeVariable<?>)argument);
					parameterOffset = 1;
				}
				else
				{
					result.setStatic();
					parameterOffset = 0;
				}
			}
			else
			{
				result.setStatic();
				parameterOffset = 0;
			}
		}
		else if(parameterType0==Item.class)
		{
			parameterOffset = 1;
		}
		else
		{
			result.setStatic();
			parameterOffset = 0;
		}

		{
			final Type returnType = method.getGenericReturnType();
			final String[] comment = annotation.docReturn();
			if(returnType!=void.class)
			{
				result.setReturn(returnType, comment);
			}
			else
			{
				if(comment.length>0)
					throw new IllegalArgumentException('@' + "returns");
			}
		}

		for(final String s : annotation.doc())
			result.addComment(s);

		{
			final String methodWrapperPattern = annotation.name();
			if(!methodWrapperPattern.isEmpty())
			{
				result.setMethodWrapperPattern(methodWrapperPattern);
			}
			else
			{
				final String pattern = getString(annotation.nameGetter());
				if(pattern!=null)
					result.setMethodWrapperPattern(pattern);
			}
		}
		{
			final String optionTagName = annotation.optionTagname();
			if(!optionTagName.isEmpty())
				result.setOptionTagName(optionTagName);
		}
		{
			final List<?> methodVarargs = getFeatures(annotation.varargsFeatures());
			if(methodVarargs!=null && !method.isVarArgs())
				throw new RuntimeException("encountered @Wrap(varargs=xxx) on non-vararg method " + method);

			final Annotation[][] annotations = method.getParameterAnnotations();
			for(int i = parameterOffset; i<parameterTypes.length; i++)
			{
				final Type genericParameterType = genericParameterTypes[i];
				final Parameter paramAnn = get(Parameter.class, annotations[i]);
				final Nullability nullability = getNullability(annotations[i], paramAnn==null?NullabilityGetterDefault.class:paramAnn.nullability());
				final List<?> varargs = ((i+1)==parameterTypes.length) ? methodVarargs : null;
				if(paramAnn==null)
					result.addParameter(parameterTypes[i], genericParameterType, varargs, nullability);
				else
				{
					final String[] comment = paramAnn.doc();
					final String paramAnnValue = paramAnn.value();
					final String paramAnnValueFixed = paramAnnValue.isEmpty() ? "{1}" : paramAnnValue;
					result.addParameter(parameterTypes[i], genericParameterType, paramAnnValueFixed, comment, varargs, nullability);
				}
			}
		}

		for(final Wrap.Thrown c : annotation.thrown())
			result.addThrows(c.value(), c.doc());
		for(final Class<? extends Throwable> throwable : getThrows(annotation.thrownGetter()))
			result.addThrows(throwable);

		return result;
	}

	private boolean getBoolean(final Class<? extends BooleanGetter<?>> clazz)
	{
		@SuppressWarnings("rawtypes")
		final BooleanGetter getter = instantiate(clazz);
		@SuppressWarnings("unchecked")
		final boolean result = getter.get(feature);
		return result;
	}

	private String getString(final Class<? extends StringGetter<?>> clazz)
	{
		if(clazz==StringGetterDefault.class)
			return null;

		@SuppressWarnings("rawtypes")
		final StringGetter getter = instantiate(clazz);
		@SuppressWarnings("unchecked")
		final String result = getter.get(feature);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private Set<Class<? extends Throwable>> getThrows(final Class<? extends ThrownGetter> clazz)
	{
		if(clazz==ThrownGetterDefault.class)
			return Collections.emptySet();

		final ThrownGetter getter = instantiate(clazz);
		@SuppressWarnings("unchecked")
		final Set<Class<? extends Throwable>> result = getter.get(feature);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private List<?> getFeatures(final Class<? extends FeaturesGetter> clazz)
	{
		if(clazz==FeaturesGetterDefault.class)
			return null;

		final FeaturesGetter getter = instantiate(clazz);
		@SuppressWarnings("unchecked")
		final List<?> result = getter.get(feature);
		return result;
	}

	private static <E> E instantiate(final Class<E> clazz)
	{
		try
		{
			final Constructor<E> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
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

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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.misc.Compare;

public final class WrapperByReflection
{
	private final Class<? extends Feature> clazz;
	private final Feature feature;
	private final FunctionField instance; // TODO remove

	public WrapperByReflection(final Class<? extends Feature> clazz, final Feature instance)
	{
		this.clazz = clazz;
		this.feature = instance;
		this.instance = null;
	}

	public WrapperByReflection(final Class<? extends FunctionField> clazz, final FunctionField instance)
	{
		this.clazz = clazz;
		this.feature = instance;
		this.instance = instance;
	}

	public void makeAll(final List<Wrapper> list)
	{
		final TreeMap<Wrapped, Method> methods = new TreeMap<Wrapped, Method>(WRAPPED_COMPARATOR);

		for(final Method method : clazz.getDeclaredMethods())
		{
			if(!Modifier.isPublic(method.getModifiers()))
				continue;

			final Wrapped annotation = getAnnotation(method);
			if(annotation==null)
				continue;

			if(annotation.pos()==-1)
				throw new IllegalArgumentException("must define @Wrapped(pos=n)" + ": " + toString(method));

			final Method collision = methods.put(annotation, method);
			if(collision!=null)
				throw new IllegalArgumentException(
						"duplicate @Wrapped(pos=" + annotation.pos() + ") " +
						"on " + toString(collision) + " and " + toString(method));
		}

		for(final Map.Entry<Wrapped, Method> entry : methods.entrySet())
		{
			final Wrapped annotation = entry.getKey();
			final Method method = entry.getValue();
			final Wrapper wrapper = make(method.getName(), method.getParameterTypes(), method, annotation);
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

	private static final Comparator<Wrapped> WRAPPED_COMPARATOR = new Comparator<Wrapped>()
	{
		@Override
		public int compare(final Wrapped o1, final Wrapped o2)
		{
			if(o1==o2)
				return 0;

			return Compare.compare(o1.pos(), o2.pos());
		}
	};

	public Wrapper makeItem(final String name, final Class<?>... parameterTypes)
	{
		return make(name, prepend(Item.class, parameterTypes));
	}

	public Wrapper makeClass(final String name, final Class<?>... parameterTypes)
	{
		return make(name, prepend(Class.class, parameterTypes));
	}

	private static Class[] prepend(final Class head, final Class[] tail)
	{
		final Class[] result = new Class[tail.length + 1];
		result[0] = head;
		System.arraycopy(tail, 0, result, 1, tail.length);
		return result;
	}

	private Wrapper make(final String name, final Class<?>... parameterTypes)
	{
		final Method method;
		try
		{
			method = clazz.getDeclaredMethod(name, parameterTypes);
		}
		catch(final SecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch(final NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}

		final Wrapped annotation = getAnnotation(method);
		if(annotation==null)
			throw new IllegalArgumentException(method.toString());

		return make(name, parameterTypes, method, annotation);
	}

	private Wrapper make(final String name, final Class<?>[] parameterTypes, final Method method, final Wrapped annotation)
	{
		{
			final Class<? extends WrapperSuppressor> suppressorClass = annotation.suppressor();
			if(suppressorClass!=WrapperSuppressorDefault.class)
			{
				final WrapperSuppressor suppressor = instantiate(suppressorClass, feature);
				final boolean suppressed = suppressor.isSuppressed();
				if(suppressed)
					return null;
			}
		}

		final Wrapper result = new Wrapper(name);

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
			final String comment = annotation.returns();
			if(returnType!=void.class)
			{
				if(!comment.isEmpty())
					result.setReturn(replace(returnType, method), comment);
				else
					result.setReturn(replace(returnType, method));
			}
			else
			{
				if(!comment.isEmpty())
					throw new IllegalArgumentException('@' + "returns");
			}
		}
		{
			{
				for(final String s : annotation.comment())
					if(!s.isEmpty())
						result.addComment(s);

				final String methodWrapperPattern = annotation.name();
				if(!methodWrapperPattern.isEmpty())
					result.setMethodWrapperPattern(methodWrapperPattern);
			}
		}
		{
			final Annotation[][] annotations = method.getParameterAnnotations();
			for(int i = parameterOffset; i<parameterTypes.length; i++)
			{
				final Type genericParameterType = genericParameterTypes[i];
				WrappedParam c = null;
				for(final Annotation a : annotations[i])
					if(a.annotationType().equals(WrappedParam.class))
						c = (WrappedParam)a;
				if(c==null)
					result.addParameter(genericParameterType);
				else
				{
					final String comment = c.comment();
					if(comment.isEmpty())
						result.addParameter(genericParameterType, c.value());
					else
						result.addParameter(genericParameterType, c.value(), comment);
				}
			}
		}
		{
			for(final Wrapped.Thrown c : annotation.thrown())
			{
				final String v = c.comment();
				if(v.isEmpty())
					result.addThrows(c.clazz());
				else
					result.addThrows(c.clazz(), v);
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

	private <E> E instantiate(final Class<E> clazz, final Feature feature)
	{
		final Constructor<E> constructor;
		try
		{
			constructor = clazz.getDeclaredConstructor(this.clazz);
		}
		catch(final NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}

		constructor.setAccessible(true);

		final E result;
		try
		{
			result = constructor.newInstance(feature);
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

	private Wrapped getAnnotation(Method method)
	{
		while(true)
		{
			final Wrapped result = method.getAnnotation(Wrapped.class);
			if(result!=null)
				return result;

			final Class<?> superClass = method.getDeclaringClass().getSuperclass();
			if(superClass==null)
				return null;

			try
			{
				method = superClass.getMethod(method.getName(), method.getParameterTypes());
			}
			catch(final SecurityException e)
			{
				throw new RuntimeException(e);
			}
			catch(final NoSuchMethodException e)
			{
				return null;
			}
		}
	}

	private Type replace(final Type type, final Method method)
	{
		if(type instanceof TypeVariable)
		{
			final TypeVariable arg0Var = (TypeVariable)type;
			if("E".equals(arg0Var.getName())) // TODO make more explicit
			{
				final Class methodClass = method.getDeclaringClass();
				if(FunctionField.class.isAssignableFrom(methodClass)) // TODO do not rely on FunctionField
				{
					final Class valueClass = instance.getValueClass();

					// TODO seems to be weird
					if("com.exedio.cope.instrument.JavaRepository$EnumBeanShellHackClass".equals(valueClass.getName()))
						return Wrapper.TypeVariable0.class;

					return valueClass;
				}
			}
			return type;
		}
		else
		{
			return type;
		}
	}
}

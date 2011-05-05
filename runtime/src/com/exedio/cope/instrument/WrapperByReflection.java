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

	public WrapperByReflection(final Feature instance)
	{
		this.clazz = instance.getClass();
		this.feature = instance;
		this.instance = null;
	}

	public WrapperByReflection(final FunctionField instance)
	{
		this.clazz = instance.getClass();
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

			try
			{
				methods.put(annotation, method);
			}
			catch(final PosBroken e)
			{
				throw new IllegalArgumentException(e.getMessage() + ": " + method.toString());
			}
		}

		for(final Map.Entry<Wrapped, Method> entry : methods.entrySet())
		{
			final Wrapped annotation = entry.getKey();
			final Method method = entry.getValue();
			final Wrapper wrapper = make(method.getName(), method.getParameterTypes(), method, annotation);
			list.add(wrapper);
		}
	}

	private static final Comparator<Wrapped> WRAPPED_COMPARATOR = new Comparator<Wrapped>()
	{
		@Override
		public int compare(final Wrapped o1, final Wrapped o2)
		{
			if(o1==o2)
				return 0;

			final int pos1 = o1.pos();
			final int pos2 = o2.pos();
			if(pos1==-1 || pos2==-1)
				throw new PosBroken("must define @Wrapped(pos=n)");
			final int result = Compare.compare(pos1, pos2);
			if(result==0)
				throw new PosBroken("duplicate @Wrapped(pos=" + pos1 + ')');
			return result;
		}
	};

	private static final class PosBroken extends IllegalArgumentException
	{
		private static final long serialVersionUID = 1l;

		PosBroken(final String message)
		{
			super(message);
		}
	}

	void add(final List<Wrapper> list, final int position, final Wrapper wrapper)
	{
		if(position>=list.size())
			list.add(wrapper);
		else
			list.add(0, wrapper);
	}

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
			method = clazz.getMethod(name, parameterTypes);
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
				final Constructor<? extends WrapperSuppressor> suppressorConstructor;
				try
				{
					suppressorConstructor = suppressorClass.getDeclaredConstructor();
				}
				catch(final NoSuchMethodException e)
				{
					throw new RuntimeException(e);
				}

				suppressorConstructor.setAccessible(true);

				final WrapperSuppressor suppressor;
				try
				{
					suppressor = suppressorConstructor.newInstance();
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

				if(suppressor.isSuppressed(feature))
					return null;
			}
		}

		final Wrapper result = new Wrapper(name);

		final int parameterOffset;
		if(parameterTypes[0]==Class.class)
		{
			result.setStatic();
			parameterOffset = 1;
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
			final Type[] genericParameterTypes = method.getGenericParameterTypes();
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
				final Constructor<? extends WrapperThrown> thrownConstructor;
				try
				{
					thrownConstructor = thrownClass.getDeclaredConstructor();
				}
				catch(final NoSuchMethodException e)
				{
					throw new RuntimeException(e);
				}

				thrownConstructor.setAccessible(true);

				final WrapperThrown thrownx;
				try
				{
					thrownx = thrownConstructor.newInstance();
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

				for(final Class<? extends Throwable> throwable : thrownx.get(feature))
					result.addThrows(throwable);
			}
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
		if(type instanceof Class)
		{
			return type;
		}
		if(type instanceof ParameterizedType)
		{
			final ParameterizedType paramType = (ParameterizedType)type;
			final Type[] args = paramType.getActualTypeArguments();
			if(args.length==1)
			{
				final Type arg0 = args[0];
				if(arg0 instanceof TypeVariable)
				{
					final TypeVariable arg0Var = (TypeVariable)arg0;
					if("P".equals(arg0Var.getName())) // TODO make more explicit
						return Wrapper.generic((Class)paramType.getRawType(), Wrapper.ClassVariable.class);
				}
			}
			return type;
		}
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

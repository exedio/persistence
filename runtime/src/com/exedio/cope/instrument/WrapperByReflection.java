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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import com.exedio.cope.Item;

public final class WrapperByReflection
{
	private final Class<?> clazz;

	public WrapperByReflection(final Class<?> clazz)
	{
		this.clazz = clazz;
		if(clazz==null)
			throw new NullPointerException("clazz");
	}

	public Wrapper makeStatic(final String name, final Class<?>... parameterTypes)
	{
		final Wrapper result = make(name, getMethod(name, prepend(Class.class, parameterTypes)), parameterTypes);
		result.setStatic();
		return result;
	}

	public Wrapper make(final String name, final Class<?>... parameterTypes)
	{
		return make(name, getMethod(name, prepend(Item.class, parameterTypes)), parameterTypes);
	}

	private Wrapper make(final String name, final Method method, final Class<?>... parameterTypes)
	{
		final Wrapper result = new Wrapper(name);
		if(method==null)
			throw new RuntimeException("no such method " + Arrays.asList(parameterTypes));

		{
			final Type returnType = method.getGenericReturnType();
			if(returnType!=void.class)
			{
				final WrapperReturn comment = method.getAnnotation(WrapperReturn.class);
				if(comment!=null)
					result.setReturn(returnType, comment.value());
				else
					result.setReturn(returnType);
			}
		}
		{
			final MethodComment comment = method.getAnnotation(MethodComment.class);
			if(comment!=null)
				result.addComment(comment.value());
		}
		{
			final Annotation[][] annotations = method.getParameterAnnotations();
			int i = 1; // 1 because of leading item
			for(final Class parameterType : parameterTypes)
			{
				ParameterComment c = null;
				for(final Annotation a : annotations[i])
					if(a.annotationType().equals(ParameterComment.class))
						c = (ParameterComment)a;
				if(c==null)
					result.addParameter(parameterType);
				else
					result.addParameter(parameterType, c.value());
				i++;
			}
		}
		return result;
	}

	private static Class[] prepend(final Class prefix, final Class[] list)
	{
		final ArrayList<Class> result = new ArrayList<Class>();
		result.add(prefix);
		result.addAll(Arrays.asList(list));
		return result.toArray(new Class[result.size()]);
	}

	private Method getMethod(final String name, final Class... parameterTypes)
	{
		try
		{
			return clazz.getMethod(name, parameterTypes);
		}
		catch(final SecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch(final NoSuchMethodException e2e)
		{
			return null;
		}
	}
}

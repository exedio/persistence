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

	public Wrapper make(final String name, final Class<?>... parameterTypes)
	{
		return makeIt(name, prepend(Item.class, parameterTypes));
	}

	public Wrapper makeStatic(final String name, final Class<?>... parameterTypes)
	{
		return makeIt(name, prepend(Class.class, parameterTypes));
	}

	private static Class[] prepend(final Class prefix, final Class[] list)
	{
		final ArrayList<Class> result = new ArrayList<Class>();
		result.add(prefix);
		result.addAll(Arrays.asList(list));
		return result.toArray(new Class[result.size()]);
	}

	private Wrapper makeIt(final String name, final Class<?>... parameterTypes)
	{
		final Wrapper result = new Wrapper(name);
		final Method method = getMethod(name, parameterTypes);
		if(method==null)
			throw new RuntimeException("no such method " + Arrays.asList(parameterTypes));

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
				for(final String s : comment.value())
					result.addComment(s);
		}
		{
			final Annotation[][] annotations = method.getParameterAnnotations();
			for(int i = parameterOffset; i<parameterTypes.length; i++)
			{
				final Class parameterType = parameterTypes[i];
				ParameterComment c = null;
				for(final Annotation a : annotations[i])
					if(a.annotationType().equals(ParameterComment.class))
						c = (ParameterComment)a;
				if(c==null)
					result.addParameter(parameterType);
				else
				{
					final String comment = c.comment();
					if("none".equals(comment))
						result.addParameter(parameterType, c.value());
					else
						result.addParameter(parameterType, c.value(), comment);
				}
			}
		}
		{
			final ThrowsComment c = method.getAnnotation(ThrowsComment.class);
			if(c!=null)
			{
				final String v = c.value();
				if("none".equals(v))
					result.addThrows(c.clazz());
				else
					result.addThrows(c.clazz(), v);
			}
		}
		return result;
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

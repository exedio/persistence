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
		final Wrapper result = new Wrapper(name);

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
					if(comment.isEmpty())
						result.addParameter(parameterType, c.value());
					else
						result.addParameter(parameterType, c.value(), comment);
				}
			}
		}
		{
			final ThrowsComment a = method.getAnnotation(ThrowsComment.class);
			if(a!=null)
			{
				for(final ThrowsComment.E c : a.value())
				{
					final String v = c.value();
					if(v.isEmpty())
						result.addThrows(c.clazz());
					else
						result.addThrows(c.clazz(), v);
				}
			}
		}
		return result;
	}
}

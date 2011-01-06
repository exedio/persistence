/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;

public final class JavaView extends Pattern
{
	private static final long serialVersionUID = 1l;

	private Mount mountIfMounted;

	private static final class Mount
	{
		final Method getter;
		final Class valueType;
		final java.lang.reflect.Type valueGenericType;

		Mount(final Type<? extends Item> type, final String name)
		{
			final String nameUpper =
				name.length()==1
				? Character.toString(Character.toUpperCase(name.charAt(0)))
				: (Character.toUpperCase(name.charAt(0)) + name.substring(1));

			final Class<?> javaClass = type.getJavaClass();
			final Method getter;
			try
			{
				getter = javaClass.getDeclaredMethod("get"+nameUpper, (Class[])null);
			}
			catch(final NoSuchMethodException e)
			{
				throw new RuntimeException("no suitable getter method found for java view "+name, e);
			}

			this.getter = getter;
			this.valueType = getter.getReturnType();
			this.valueGenericType = getter.getGenericReturnType();
		}
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		this.mountIfMounted = new Mount(getType(), getName());
	}

	private Mount mount()
	{
		final Mount result = this.mountIfMounted;
		if(result==null)
			throw new IllegalStateException("feature not mounted");
		return result;
	}

	public Class getValueType()
	{
		return mount().valueType;
	}

	public java.lang.reflect.Type getValueGenericType()
	{
		return mount().valueGenericType;
	}

	public Object get(final Item item)
	{
		try
		{
			return mount().getter.invoke(item, (Object[])null);
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(toString(), e);
		}
		catch(final InvocationTargetException e)
		{
			throw new RuntimeException(toString(), e);
		}
	}
}

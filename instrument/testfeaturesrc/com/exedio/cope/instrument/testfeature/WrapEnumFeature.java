/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrap;

public final class WrapEnumFeature<E extends Enum<E>> extends Pattern
{
	public static <E extends Enum<E>> WrapEnumFeature<E> create(final Class<E> clazz)
	{
		return new WrapEnumFeature<>(clazz, "com.exedio.cope.instrument.JavaRepository$EnumBeanShellHackClass");
	}

	public static <E extends Enum<E>> WrapEnumFeature<E> create(final Class<E> clazz, final String className)
	{
		return new WrapEnumFeature<>(clazz, className);
	}

	private WrapEnumFeature(final Class<E> clazz, final String className)
	{
		if(clazz==null)
			throw new IllegalArgumentException("clazz is null");
		if(!clazz.isEnum())
			throw new IllegalArgumentException("clazz is not an enum: " + clazz.getName());

		if(className==null)
			throw new IllegalArgumentException("className is null");
		if(!className.equals(clazz.getName()))
			throw new IllegalArgumentException("className mismatch: " + clazz.getName());
	}

	@Wrap(order=10)
	public E method(@SuppressWarnings("unused") final E p)
	{
		return null;
	}

	private static final long serialVersionUID = 1l;
}

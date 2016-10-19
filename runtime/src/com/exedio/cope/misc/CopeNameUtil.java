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

package com.exedio.cope.misc;

import com.exedio.cope.CopeName;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public final class CopeNameUtil
{
	public static String get(final AnnotatedElement annotatedElement)
	{
		final CopeName name = annotatedElement.getAnnotation(CopeName.class);
		if(name!=null)
			return name.value();

		return getDeprecated(annotatedElement);
	}

	@Deprecated
	private static String getDeprecated(final AnnotatedElement annotatedElement)
	{
		final com.exedio.cope.CopeID id =
				annotatedElement.getAnnotation(com.exedio.cope.CopeID.class);
		return (id!=null) ? id.value() : null;
	}

	public static String getAndFallbackToSimpleName(final Class<?> clazz)
	{
		final String result = get(clazz);
		return (result!=null) ? result : clazz.getSimpleName();
	}

	public static String getAndFallbackToName(final Field field)
	{
		final String result = get(field);
		return (result!=null) ? result : field.getName();
	}

	private CopeNameUtil()
	{
		// prevent instantiation
	}
}

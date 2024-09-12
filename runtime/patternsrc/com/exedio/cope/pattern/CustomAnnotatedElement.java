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

package com.exedio.cope.pattern;

import com.exedio.cope.misc.Arrays;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;

final class CustomAnnotatedElement
{
	static AnnotatedElement create(final Annotation... annotations)
	{
		if(annotations==null)
			throw new NullPointerException("annotations");
		if(annotations.length==0)
			throw new IllegalArgumentException("annotations must not be empty");
		final HashMap<Class<?>, Annotation> annotationMap = new HashMap<>();
		for(int i = 0; i<annotations.length; i++)
		{
			final Annotation a = annotations[i];
			if(a==null)
				throw new NullPointerException("annotations" + '[' + i + ']');
			if(annotationMap.putIfAbsent(a.annotationType(), a)!=null)
				throw new IllegalArgumentException("duplicate " + a.annotationType());
		}

		return new AnnotationSource(Arrays.copyOf(annotations), annotationMap);
	}

	private record AnnotationSource(
			Annotation[] annotations,
			HashMap<Class<?>, Annotation> annotationMap)
			implements AnnotatedElement
	{
		@Override
		public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
		{
			return annotationMap.containsKey(annotationClass);
		}

		@Override
		public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
		{
			return annotationClass.cast(annotationMap.get(annotationClass));
		}

		@Override
		public Annotation[] getAnnotations()
		{
			return Arrays.copyOf(annotations);
		}

		@Override
		public Annotation[] getDeclaredAnnotations()
		{
			return Arrays.copyOf(annotations);
		}

		@Override
		public String toString()
		{
			return java.util.Arrays.toString(annotations);
		}
	}

	private CustomAnnotatedElement()
	{
		// prevent instantiation
	}
}

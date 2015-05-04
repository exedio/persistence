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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Pattern;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

final class MediaPathAnnotationProxy implements AnnotatedElement
{
	private final Pattern source;

	MediaPathAnnotationProxy(final Pattern source)
	{
		this.source = requireNonNull(source);
	}

	public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
	{
		return
			(PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
			? source.isAnnotationPresent(annotationClass)
			: false;
	}

	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
	{
		return
			(PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
			? source.getAnnotation(annotationClass)
			: null;
	}

	public Annotation[] getAnnotations()
	{
		throw new RuntimeException(source.toString());
	}

	public Annotation[] getDeclaredAnnotations()
	{
		throw new RuntimeException(source.toString());
	}

	@Override
	public String toString()
	{
		return source.toString() + "-annotations";
	}
}

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
import com.exedio.cope.Vault;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

final class MediaPathFeatureAnnotationProxy implements AnnotatedElement
{
	private final Pattern source;
	private final boolean computed;

	MediaPathFeatureAnnotationProxy(final Pattern source, final boolean computed)
	{
		this.source = requireNonNull(source);
		this.computed = computed;
	}

	@Override
	public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
	{
		if(computed && Computed.class==annotationClass)
			return true;

		//noinspection SimplifiableConditionalExpression
		return
			(Vault.class==annotationClass || PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
			? source.isAnnotationPresent(annotationClass)
			: false;
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
	{
		if(computed && Computed.class==annotationClass)
			return annotationClass.cast(ComputedElement.get().getAnnotation(Computed.class));

		return
			(Vault.class==annotationClass || PreventUrlGuessing.class==annotationClass || UrlFingerPrinting.class==annotationClass)
			? source.getAnnotation(annotationClass)
			: null;
	}

	@Override
	public Annotation[] getAnnotations()
	{
		throw new RuntimeException(source.toString());
	}

	@Override
	public Annotation[] getDeclaredAnnotations()
	{
		throw new RuntimeException(source.toString());
	}

	@Override
	public String toString()
	{
		return source + "-annotations";
	}
}

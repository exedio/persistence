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

package com.exedio.cope;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;

public interface Feature extends Serializable
{
	void mount(
			final AbstractType<?> type,
			final String name,
			final String string,
			final Serializable serializable,
			final AnnotatedElement annotationSource);

	static <T> T requireMounted(final T result)
	{
		if(result==null)
			throw new IllegalStateException("feature not mounted");
		return result;
	}

	/**
	 * @see #getType()
	 */
	AbstractType<?> getAbstractType();

	/**
	 * @see #getAbstractType()
	 */
	Type<?> getType();

	String getName();

	/**
	 * @see Model#getFeature(String)
	 */
	String getID();

	/**
	 * @see Class#isAnnotationPresent(Class)
	 */
	boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass);

	/**
	 * @see Class#getAnnotation(Class)
	 */
	<A extends Annotation> A getAnnotation(final Class<A> annotationClass);

	/**
	 * @see com.exedio.cope.misc.LocalizationKeys
	 */
	List<String> getLocalizationKeys();

	default Collection<String> getSuspicions()
	{
		return List.of();
	}

	void toString(final StringBuilder sb, final Type<?> defaultType);

	// patterns ------------------

	/**
	 * Returns the pattern, this feature is a source feature of.
	 * NOTE:
	 * Does not return the pattern that created the type
	 * of this feature. For such cases use
	 * {@link #getType()}.{@link Type#getPattern() getPattern()}.
	 *
	 * @see Pattern#getSourceFeatures()
	 */
	@SuppressWarnings("unused") // OK: Methods are tested on implementation classes but never used as a member of this interface.
	Pattern getPattern();

	/**
	 * This method is called before the termination of any constructor of class
	 * {@link Model}.
	 * It allows any initialization of the feature, that cannot be done earlier.
	 * The default implementation is empty.
	 */
	default void afterModelCreated()
	{
		// empty default implementation
	}

	@Serial
	long serialVersionUID = 1l;
}

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

package com.exedio.cope.instrument;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrap
{
	/**
	 * Specifies the order of wrapped methods.
	 * Is needed because the result of {@link Class#getDeclaredMethods()}
	 * is not in any particular order.
	 */
	int order();

	String name() default "";
	String optionTagname() default "";
	Class<? extends StringGetter<?>> nameGetter() default StringGetterDefault.class;

	Class<? extends FeaturesGetter<?>> varargsFeatures() default FeaturesGetterDefault.class;

	@interface Thrown
	{
		Class<? extends Throwable> value();
		String[] doc() default {};
	}
	Thrown[] thrown() default {};
	Class<? extends ThrownGetter<?>> thrownGetter() default ThrownGetterDefault.class;

	String[] doc() default {};
	String[] docReturn() default {};

	Class<? extends BooleanGetter<?>>[] hide() default {};

	Class<? extends NullabilityGetter<?>> nullability() default NullabilityGetterDefault.class;

	// common phrases

	String GET_DOC = "Returns the value of {0}.";
	String SET_DOC = "Sets a new value for {0}.";
	String MAP_KEY = "k";
	String MAP_GET_DOC = "Returns the value mapped to '{@code' " + MAP_KEY + "'}' by the field map {0}.";
	String MAP_SET_DOC = "Associates '{@code' " + MAP_KEY + "'}' to a new value in the field map {0}.";

	String FOR_NAME = "for{0}";
	String FOR_STRICT_NAME = "for{0}Strict";
	String FOR_DOC        = "Finds a {2} by its {0}.";
	/**
	 * @deprecated Use {@link #FOR_DOC} instead.
	 */
	@Deprecated
	String FOR_DOC_BROKEN = "Finds a {2} by it''s {0}.";
	String FOR_RETURN = "null if there is no matching item.";
	String FOR_PARAM = "shall be equal to field {0}.";
	String FOR_STRICT_THROWN = "if there is no matching item.";

	String HASH_CHECK_DOC = "Returns whether the given value corresponds to the hash in {0}.";
	String HASH_BLIND_DOC_1 = "Wastes (almost) as much cpu cycles, as a call to '{@code' check{3}'}' would have needed.";
	String HASH_BLIND_DOC_2 = "Needed to prevent Timing Attacks.";

	String MEDIA_LOCATOR = "Returns a Locator the content of {0} is available under.";
	String MEDIA_URL = "Returns a URL the content of {0} is available under.";
	String MEDIA_CONTENT_TYPE = "Returns the content type of the media {0}.";
	String MEDIA_LAST_MODIFIED = "Returns the last modification date of media {0}.";
	String MEDIA_LENGTH = "Returns the body length of the media {0}.";
	String MEDIA_BODY = "Returns the body of the media {0}.";
}

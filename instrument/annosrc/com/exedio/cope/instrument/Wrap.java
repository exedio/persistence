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
}
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

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface WrapType
{
	/**
	 * @see WrapperType#wildcardClass()
	 */
	Class<?> wildcardClassCaster();

	/**
	 * @see WrapperType#type()
	 */
	Class<?> type() default StringGetterDefault.class; // default means no type
	String typeDoc() default "The type information for {0}.";

	boolean hasGenericConstructor() default true;
	Class<?> activationConstructor() default StringGetterDefault.class;  // default means no activation constructor
	boolean allowStaticClassToken() default true;
	boolean revertFeatureBody() default false;
	String featurePrefix() default "";
	String featurePostfix() default "";
	String featureThis() default "this";
	Class<?> top();
}

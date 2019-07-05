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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Code elements marked with this annotation become part of the 'interim code', which is compiled while the instrumentor
 * runs to instantiate {@link WrapFeature features}.
 *
 * @see WrapImplementsInterim
 * @see WrapAnnotateInterim
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface WrapInterim
{
	/**
	 * Whether or not the body of a method or constructor becomes part of the 'interim code'.
	 * If set to false, the body contains just a {@code throw new RuntimeException()} in the 'interim code'.
	 * For {@link Target targets} other than
	 * {@link ElementType#METHOD methods} or
	 * {@link ElementType#CONSTRUCTOR constructors} it is forbidden to set a non-default value.
	 */
	boolean methodBody() default true;
}

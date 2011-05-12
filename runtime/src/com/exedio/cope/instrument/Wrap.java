/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
	int order();
	Class<? extends WrapperSuppressor> suppressor() default WrapperSuppressorDefault.class;
	String[] doc() default "";
	String docReturn() default "";
	String name() default "";
	Class<? extends WrapperName> namex() default WrapperNameDefault.class;
	Thrown[] thrown() default {};
	Class<? extends WrapperThrown> thrownx() default WrapperThrownDefault.class;

	public @interface Thrown
	{
		Class<? extends Throwable> clazz();
		String doc() default "";
	}
}

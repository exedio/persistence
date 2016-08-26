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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Repeatable(WrapperRepeated.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Wrapper
{
	String wrap();

	/**
	 * Overrides the visibility of the generated wrapper method.
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * the visibility of the feature,
	 * unless {@link #internal() internal} is set.
	 */
	Visibility visibility() default Visibility.DEFAULT;

	/**
	 * Appends "Internal" to the name of the generated wrapper method.
	 * So for instance {@code getName} becomes {@code getNameInternal}.
	 * Additionally changes the visibility to {@code private},
	 * if {@link #visibility() visibility} is set
	 * to {@link Visibility#DEFAULT DEFAULT}.
	 * <p>
	 * This is typically needed if you want to provide your own implementation
	 * of the generated method, but you still want to call the generated code.
	 */
	boolean internal() default false;

	boolean booleanAsIs() default false;

	/**
	 * Makes the generated wrapper method {@code final}.
	 */
	boolean asFinal() default true;

	/**
	 * Annotates the generated wrapper method with @{@link Override}.
	 */
	boolean override() default false;
}

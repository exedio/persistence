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

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface WrapperType
{
	/**
	 * Overrides the visibility of the generated inner class {@code classWildcard}.
	 * This is needed to workaround
	 * <a href="https://bugs.java.com/view_bug.do?bug_id=7101374">rawtypes warnings</a>
	 * in the generated code, if the class has type parameters.
	 * <p>
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * the visibility of the class.
	 */
	Visibility wildcardClass() default Visibility.DEFAULT;

	/**
	 * Overrides the visibility of the generated TYPE constant.
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * the visibility of the class.
	 */
	Visibility type() default Visibility.DEFAULT;

	/**
	 * Annotates the TYPE constant with @{@link SuppressWarnings},
	 * additionally to global parameters of the instrumentor.
	 */
	String[] typeSuppressWarnings() default {};

	/**
	 * Overrides the visibility of the generated <em>initial</em> constructor.
	 * <p>
	 * The <em>initial</em> constructor takes arguments for each <em>initial</em> feature.
	 * Features are <em>initial</em> if they do implement {@link com.exedio.cope.Settable}
	 * and {@link com.exedio.cope.Settable#isInitial()} returns true.
	 * This can be overridden by {@link WrapperInitial}.
	 * <p>
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * the "most private" visibility of all <em>initial</em> features set by
	 * the constructor.
	 * However, the constructor is not "more public" than the class.
	 */
	Visibility constructor() default Visibility.DEFAULT;

	/**
	 * Annotates the <em>initial</em> constructor with @{@link SuppressWarnings},
	 * additionally to global parameters of the instrumentor.
	 */
	String[] constructorSuppressWarnings() default {};

	/**
	 * Overrides the visibility of the generated generic constructor,
	 * that calls {@link com.exedio.cope.Item#Item(com.exedio.cope.SetValue...)}.
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * "private" for final classes, and "protected" for non-final classes.
	 */
	@SuppressWarnings("JavadocReference") // OK: protected member of public class is part of public API
	Visibility genericConstructor() default Visibility.DEFAULT;

	/**
	 * Overrides the visibility of the generated activation constructor,
	 * that calls {@link com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)}.
	 * The visibility {@link Visibility#DEFAULT defaults} to
	 * "private" for final classes, and "protected" for non-final classes.
	 */
	@SuppressWarnings("JavadocReference") // OK: protected member of public class is part of public API
	Visibility activationConstructor() default Visibility.DEFAULT;

	int indent() default 1;

	boolean comments() default true;
}

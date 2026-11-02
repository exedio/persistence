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

import static java.lang.annotation.ElementType.TYPE;

import com.exedio.cope.instrument.WrapAnnotateInterim;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as not to be used as a type.
 * Thus, {@link TypesBound#newType(Class, java.util.function.Function)} cannot be called with it.
 * Within a class hierarchy such a class becomes "transparent".
 * This means a type of any subclass of that class
 * has the type of the superclass of that class as its super type.
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WrapAnnotateInterim
public @interface CopeNoType
{
}

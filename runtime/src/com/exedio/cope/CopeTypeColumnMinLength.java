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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets a lower limit for the length of the type column.
 *
 * In general the length of the type column is determined from the longest
 * possible value in the type column. This annotation overrides this
 * for a longer length.
 *
 * Setting this length may help you to avoid schema changes.
 *
 * @see SchemaInfo#getTypeColumnValue(Type)
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopeTypeColumnMinLength
{
	/**
	 * must be greater zero
	 */
	int value();
}

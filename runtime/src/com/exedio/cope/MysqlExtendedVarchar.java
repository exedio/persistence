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

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables {@code varchar} on MySQL for longer {@link StringField string fields}.
 * <p>
 * In general database columns for string fields with a
 * {@link StringField#getMaximumLength() maximum length} of up to 85 characters
 * do get type {@code varchar}, longer string fields do get {@code text}, {@code mediumtext} etc.
 * This annotation increases the limit from 85 characters to 16382 characters.
 * <p>
 * This annotation is applicable to string fields only.
 * When applying it to other features or
 * when connecting to databases other than MySQL,
 * there is no effect at all.
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MysqlExtendedVarchar
{
	// empty
}

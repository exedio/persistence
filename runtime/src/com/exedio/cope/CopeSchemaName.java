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
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies database table/column names
 * for types/fields different from the default names.
 * This may be useful to
 * <ul>
 *    <li>maintain schema compatibility to
 *        former versions of the application or
 *        to a legacy application.
 *    <li>specify names in traditional uppercase plural.
 *    <li>resolve name conflicts caused by trimming names
 *        to the maximum length allowed by the database.
 * </ul>
 * In particular it does the following:
 * <ul>
 * <li>Wherever a {@link Type#getID() type id} is used to determine names
 *     in the database schema (tables, constraints, sequences},
 *     a @CopeSchemaName at that type (@Target(TYPE)) overrides the type id.
 * <li>Wherever a {@link Feature#getName() feature name} is used to determine names
 *     in the database schema (tables, columns, constraints, sequences},
 *     a @CopeSchemaName at that feature (@Target(FIELD)) overrides the feature name.
 * </ul>
 * Apart from affecting names in the database schema, this annotation does nothing else.
 * In particular it does not affect the type id or the feature name itself.
 */
@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CopeSchemaName
{
	String value();
}

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
 * Specifies the schema value (an integer) of an enum facet
 * stored in the column of an {@link EnumField}.
 * This may be useful to maintain schema compatibility to
 * former versions of the application or to a legacy application.
 * <p>
 * Schema values of enum facets are assigned by the following rules:
 * <ul>
 * <li>An annotated facet (by {@link CopeSchemaValue}) gets the schema value
 *     specified by the annotation.</li>
 * <li>A non-annotated facet, that is the first facet of its Enum,
 *     gets a schema value of 10.</li>
 * <li>Otherwise, a non-annotated facet gets the schema value
 *     of the previous facet, plus one, rounded to full multiples of 10 towards
 *     {@link Integer#MAX_VALUE}.
 *     This holds regardless whether the previous facet is annotated or not.</li>
 * </ul>
 * If, by the rules above, a facet is assigned a schema value,
 * that is not greater than the schema value of its previous facet,
 * {@link EnumField#create(Class) EnumField.create} throws an
 * {@link IllegalArgumentException}.
 * This means you can't change the {@link Query#setOrderBy(Function, boolean) order}
 * of enums with this annotation.
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopeSchemaValue
{
	int value();
}

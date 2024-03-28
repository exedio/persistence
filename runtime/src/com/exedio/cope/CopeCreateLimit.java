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
 * Specifies the maximum number of items (instances) that can be
 * {@link Type#newItem(SetValue[]) created} over the whole live time of the
 * database schema since its {@link Model#createSchema() creation}.
 * For types without this annotation the limit defaults to {@link Integer#MAX_VALUE}.
 * The limit includes
 * <ul>
 * <li>items of {@link Type#getSubtypes() subtypes} of the type and</li>
 * <li>items created by transactions that are subsequently
 *     {@link Model#rollback() rolled back}.</li>
 * </ul>
 * This annotation is allowed on top-level types only.
 * <p>
 * This limit is an upper bound. There is no guarantee, that actually that
 * many items can be created. In particular certain methods of generating
 * primary keys may 'loose' unused ranges of numbers due to caching.
 * <p>
 * The value is actually off by one. So @CopeCreateLimit(0) allows you to create one item.
 * And @CopeCreateLimit(Integer.MAX_VALUE) allows you to create 2<sup>31</sup> items, not just 2<sup>31</sup> - 1.
 * <p>
 * This annotation may affect your database schema, in particular the type of
 * <ul>
 * <li>the {@link SchemaInfo#getPrimaryKeyColumnName(Type) primary column} of the type and its subtypes,</li>
 * <li>foreign key columns of {@link ItemField item fields} referring the type or its subtypes, and</li>
 * <li>the {@link SchemaInfo#getPrimaryKeySequenceName(Type) sequence} for creating primary keys of the type.</li>
 * </ul>
 * <p>
 * This annotation is essentially applicable to types (@Target(TYPE)) only.
 * When applying it to {@link Feature features} (@Target(FIELD)) that are {@link Pattern patterns},
 * the annotation is forwarded to all
 * {@link Pattern#getSourceTypes() source types} of that pattern.
 * When applying it to features that are not patterns,
 * there is no effect at all.
 * <p>
 * If such a source type is annotated as well, the annotation of the pattern has precedence.
 */
@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CopeCreateLimit
{
	long value();
}

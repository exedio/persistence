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
 * Allows a type's instances to be modified (created, updated and deleted) externally, directly in the database.
 *
 * <p>COPE caches are disabled for the type.
 *
 * <p>External modifications of data must obey all consistency rules.
 *
 *	<p>
 * Listeners and hooks such as
 * {@link ChangeListener change listeners},
 * {@link ChangeHook change hooks}
 * and item methods
 * beforeNewCopeItem,
 * {@link Item#afterNewCopeItem() afterNewCopeItem},
 * {@link Item#beforeSetCopeItem beforeSetCopeItem}, and
 * {@link Item#beforeDeleteCopeItem() beforeDeleteCopeItem}
 * may not get called on external modifications.
 *
 * <p>For subtypes, this annotation must be set if and only if it is set for the {@link Type#getSupertype() supertype}.
 *
 * <p>
 * This annotation is essentially applicable to types (@Target(TYPE)) only.
 * When applying it to {@link Feature features} (@Target(FIELD)) that are {@link Pattern patterns},
 * the annotation is forwarded to all
 * {@link Pattern#getSourceTypes() source types} of that pattern.
 * When applying it to features that are not patterns,
 * there is no effect at all.
 */
@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CopeExternal
{
	// no parameters
}

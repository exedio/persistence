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

package com.exedio.cope.misc;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a
 * {@link com.exedio.cope.Type type} or
 * {@link com.exedio.cope.Feature feature}
 * as <i>computed</i>.
 * This means, that the persistent contents of that type or feature
 * are not allowed to be modified by human users directly,
 * using some generic editing facility.
 * Typically such a type or feature is modified (computed)
 * by some application logic instead.
 * <p>
 * <b>Types</b><br>
 * A generic editing facility for human users MUST NOT
 * allow the human user to directly cause
 * creation or deletion of items
 * of any <i>computed</i> type.
 * <p>
 * Making a type <i>computed</i> does not imply, that the type's
 * features are <i>computed</i> as well.
 * <p>
 * <b>Features</b><br>
 * A generic editing facility for human users MUST NOT
 * allow the human user to directly specify the value
 * of any <i>computed</i> feature.
 * This applies both to creation and modification of items.
 * <p>
 * A generic editing facility for human users MAY allow human users
 * to trigger application logic that (indirectly) modifies
 * <i>computed</i> types or features.
 * <p>
 * This annotation does not specify, whether a feature or type shall be
 * shown to a human user.
 */
@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Computed
{
	// no parameters
}

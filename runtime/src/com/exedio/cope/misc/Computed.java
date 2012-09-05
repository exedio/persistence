/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a feature or type as <i>computed</i>.
 * This means, that the persistent contents of that feature or type
 * are typically not modified by human users, but by some computational process.
 * So any generic editing facility is advised to not let a human user
 * modify this type or feature.
 *
 * For types, this means, that items of the type shall no be created or deleted
 * by human users. For features this means, that the state of the feature shall
 * not be modified by a human user. A <i>computed</i> type does not imply, that the type's
 * features are <i>computed</i> as well.
 *
 * This annotation does not specify, whether this feature or type shall be
 * shown to a human user.
 */
@Target({TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Computed
{
	// no parameters
}

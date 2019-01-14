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

package com.exedio.cope.pattern;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a http redirect (moved permanently) to
 * a {@link MediaPath}.
 * <p>
 * Common usage is to maintain old urls after renaming a {@link MediaPath}.
 * For instance, if there is a media {@code picture}:
 *
 * <pre>
 * static final Media picture = new Media();
 * </pre>
 * and this media is renamed to {@code image},
 * then old urls created by {@code picture}
 * can be supported like this:
 *
 * <pre>
 * {@literal @RedirectFrom("picture")}
 * static final Media image = new Media();
 * </pre>
 *
 * @author Ralf Wiebicke
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedirectFrom
{
	String[] value();
}

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

/**
 * Maps a {@link ConnectProperties#getConnectionUrl() connection url}
 * to a {@link Dialect} to be used for
 * {@link Model#connect() connecting} to that url.
 * <p>
 * Implementations suitable for {@link java.util.ServiceLoader}
 * <ul>
 * <li>must be public,
 * <li>must be non-abstract, and
 * <li>must have a public default constructor.
 * </ul>
 * It is highly recommended to override {@link Object#toString() toString} with an
 * informative message.
 */
abstract class DialectUrlMapper // is not an interface, because methods are better not public
{
	/**
	 * Returns a dialect suitable for {@code url}
	 * or null if there is no suitable dialect.
	 * @see java.sql.Driver#acceptsURL(String)
	 */
	abstract Class<? extends Dialect> map(String url);
}

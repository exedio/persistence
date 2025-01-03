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

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional") // OK: is to be implemented by Items only
public interface MediaUrlCatchphraseProvider
{
	/**
	 * The result must contain the following ascii characters only:
	 * <ul>
	 * <li>letters ({@code A}-{@code Z}, {@code a}-{@code z}),
	 * <li>digits ({@code 0}-{@code 9}),
	 * <li>the dash ({@code -}) and
	 * <li>the underscore ({@code _}).
	 * </ul>
	 * Otherwise,
	 * {@link MediaPath#getLocator(com.exedio.cope.Item)} and
	 * {@link MediaPath#getURL(com.exedio.cope.Item)}
	 * will throw an {@link IllegalArgumentException}.
	 * <p>
	 * Returning null or an empty string is equivalent to not implementing interface
	 * {@code MediaUrlCatchphraseProvider}
	 * at all.
	 */
	String getMediaUrlCatchphrase(MediaPath path);
}

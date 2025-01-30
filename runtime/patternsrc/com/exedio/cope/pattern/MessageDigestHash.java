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

import com.exedio.cope.pattern.Hash.Algorithm;

public final class MessageDigestHash
{
	private static final String DEFAULT_DIGEST = "SHA-512";
	private static final int DEFAULT_SALT_LENGTH = 8;

	private MessageDigestHash()
	{
		// prevent instantiation
	}

	@SuppressWarnings("unused")
	public static Algorithm algorithm(final int iterations)
	{
		return new MessageDigestAlgorithm(DEFAULT_DIGEST, DEFAULT_SALT_LENGTH, iterations);
	}
}

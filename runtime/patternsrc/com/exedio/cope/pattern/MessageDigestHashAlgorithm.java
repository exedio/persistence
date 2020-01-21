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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Uses hash algorithms from {@link MessageDigest}.
 *
 * @author Ralf Wiebicke
 */
public final class MessageDigestHashAlgorithm
{
	/**
	 * @param digest an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public static HashAlgorithm create(
			final Charset charset,
			final String digest,
			final int saltLength,
			final SecureRandom saltSource,
			final int iterations)
	{
		return AlgorithmAdapter.wrap(
				new MessageDigestAlgorithm(digest, 0, iterations).
						salt(saltLength, saltSource),
				charset);
	}

	private MessageDigestHashAlgorithm()
	{
		// prevent instantiation
	}
}

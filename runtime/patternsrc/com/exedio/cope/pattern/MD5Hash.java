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

public final class MD5Hash extends Hash
{
	private static final long serialVersionUID = 1l;

	private static final String DIGEST = "MD5";

	/**
	 * @deprecated
	 *    This hash is insecure, because it does neither use salts nor iterations.
	 *    For a full description see https://www.owasp.org/index.php/Hashing_Java.
	 */
	@Deprecated
	public MD5Hash(final String encoding)
	{
		super(new MessageDigestAlgorithm(DIGEST, 0, 1), encoding);
	}

	/**
	 * @deprecated
	 *    This hash is insecure, because it does neither use salts nor iterations.
	 *    For a full description see https://www.owasp.org/index.php/Hashing_Java.
	 */
	@Deprecated
	public MD5Hash()
	{
		super(new MessageDigestAlgorithm(DIGEST, 0, 1));
	}
}
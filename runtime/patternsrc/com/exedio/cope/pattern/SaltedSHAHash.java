/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class SaltedSHAHash extends ByteHash
{
	private static final long serialVersionUID = 1l;
	
	private static final String HASH = "SHA-512";

	private SaltedSHAHash(final boolean optional, final Algorithm algorithm, final String encoding)
	{
		super(optional, algorithm, encoding);
	}

	public SaltedSHAHash(final int iterations, final String encoding)
	{
		super(false, new MessageDigestHash(HASH, 8, iterations), encoding);
	}

	public SaltedSHAHash(final int iterations)
	{
		super(false, new MessageDigestHash(HASH, 8, iterations), "utf8");
	}
	
	@Override
	public SaltedSHAHash optional()
	{
		return new SaltedSHAHash(true, getAlgorithm(), getEncoding());
	}
}

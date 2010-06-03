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

import java.security.MessageDigest;

import com.exedio.cope.util.MessageDigestUtil;

/**
 * Uses hash algorithms from {@link MessageDigest}.
 *
 * @author Ralf Wiebicke
 */
public class MessageDigestHash extends ByteHash
{
	private static final long serialVersionUID = 1l;
	
	private final String algorithm;

	/**
	 * @param algorithm an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestHash(
			final boolean optional,
			final String algorithm,
			final String encoding)
	{
		super(optional, name(algorithm), length(algorithm), encoding);
		this.algorithm = algorithm;
	}
	
	private static final int length(final String algorithm)
	{
		final MessageDigest digest = MessageDigestUtil.getInstance(algorithm);
		
		final int digestLength = digest.getDigestLength();
		if(digestLength<=0)
			throw new IllegalArgumentException("digest length not supported: " + digestLength);
		
		return digestLength;
	}

	private static final String name(final String algorithm)
	{
		return algorithm.replaceAll("-", "");
	}
	
	/**
	 * @param algorithm an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestHash(
			final boolean optional,
			final String algorithm)
	{
		this(optional, algorithm, "utf8");
	}
	
	@Override
	public MessageDigestHash optional()
	{
		return new MessageDigestHash(true, algorithm, getEncoding());
	}
	
	@Override
	public final byte[] hash(final byte[] plainText)
	{
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(algorithm);
		messageDigest.reset();
		messageDigest.update(plainText);
		return messageDigest.digest();
	}
}

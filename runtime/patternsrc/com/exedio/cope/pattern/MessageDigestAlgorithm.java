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

import com.exedio.cope.util.MessageDigestFactory;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Uses hash algorithms from {@link MessageDigest}.
 *
 * @author Ralf Wiebicke
 */
public final class MessageDigestAlgorithm implements Hash.Algorithm
{
	private final MessageDigestFactory digest;
	private final int digestLength;

	private final int saltLength;
	private final SecureRandom saltSource;

	private final int iterations;

	/**
	 * @param digest an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestAlgorithm(
			final String digest,
			final int saltLength,
			final int iterations)
	{
		this(
			new MessageDigestFactory(digest),
			saltLength,
			saltLength>0 ? new SecureRandom() : null,
			iterations);
	}

	private MessageDigestAlgorithm(
			final MessageDigestFactory digest,
			final int saltLength,
			final SecureRandom saltSource,
			final int iterations)
	{
		this.digest = digest;
		this.digestLength = digest.getLength();
		this.saltLength = saltLength;
		this.saltSource = saltSource;
		this.iterations = iterations;
		if(saltLength<0)
			throw new IllegalArgumentException("saltLength must be at least zero, but was " + saltLength);
		if((saltLength==0)!=(saltSource==null))
			throw new IllegalArgumentException("saltLength inconsistent to saltSource");
		if(iterations<1)
			throw new IllegalArgumentException("iterations must be at least one, but was " + iterations);
	}

	public MessageDigestAlgorithm salt(final int length, final SecureRandom source)
	{
		return new MessageDigestAlgorithm(digest, length, source, iterations);
	}

	/**
	 * For tests only !!!
	 */
	SecureRandom getSaltSource()
	{
		return saltSource;
	}

	@Override
	public String name()
	{
		final StringBuilder bf = new StringBuilder();
		bf.append(digest.getAlgorithm().replaceAll("-", ""));
		if(saltLength>0)
			bf.append('s').
				append(saltLength);
		if(iterations>1)
			bf.append('i').
				append(iterations);
		return bf.toString();
	}

	@Override
	public int length()
	{
		return saltLength + digestLength;
	}

	@Override
	public byte[] hash(final byte[] plainText)
	{
		if(plainText==null)
			throw new NullPointerException();

		final MessageDigest messageDigest = digest.newInstance();

		final byte[] result = new byte[saltLength + digestLength];

		// https://www.owasp.org/index.php/Hashing_Java
		if(saltLength>0)
		{
			final byte[] salt = new byte[saltLength];
			saltSource.nextBytes(salt);
			messageDigest.update(salt);
			System.arraycopy(salt, 0, result, 0, saltLength);
		}

		messageDigest.update(plainText);

		try
		{
			messageDigest.digest(result, saltLength, digestLength);

			for(int i = 1; i<iterations; i++)
			{
				messageDigest.update(result, saltLength, digestLength);
				messageDigest.digest(result, saltLength, digestLength);
			}
		}
		catch(final DigestException e)
		{
			throw new RuntimeException(e);
		}

		return result;
	}

	@Override
	public boolean check(final byte[] plainText, final byte[] hash)
	{
		if(plainText==null)
			throw new NullPointerException();
		if(hash==null)
			throw new NullPointerException();
		if(hash.length!=(saltLength+digestLength))
			throw new IllegalArgumentException(String.valueOf(hash.length));

		final MessageDigest messageDigest = digest.newInstance();

		if(saltLength>0)
			messageDigest.update(hash, 0, saltLength);

		messageDigest.update(plainText);

		final byte[] result = messageDigest.digest();

		try
		{
			for(int i = 1; i<iterations; i++)
			{
				messageDigest.update(result);
				messageDigest.digest(result, 0, result.length);
			}
		}
		catch(final DigestException e)
		{
			throw new RuntimeException(e);
		}

		for(int i = 0; i<digestLength; i++)
			if(result[i]!=hash[i+saltLength])
				return false;
		return true;
	}

	@Override
	public boolean compatibleTo(final Hash.Algorithm other)
	{
		if(this==other)
			return true;
		if(other==null)
			throw new NullPointerException();
		if(!(other instanceof MessageDigestAlgorithm))
			return false;

		final MessageDigestAlgorithm o = (MessageDigestAlgorithm)other;
		return
			digest.equals(o.digest) &&
			digestLength==o.digestLength &&
			saltLength==o.saltLength &&
			iterations==o.iterations;
	}

	public int getSaltLength()
	{
		return saltLength;
	}

	public int getIterations()
	{
		return iterations;
	}
}

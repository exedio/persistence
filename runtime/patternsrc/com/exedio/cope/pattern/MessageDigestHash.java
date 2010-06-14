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

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.SecureRandom;

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
	private final int algorithmLength;
	private final int saltLength;
	private java.util.Random saltSource;
	private final int iterations;

	/**
	 * @param algorithm an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestHash(
			final boolean optional,
			final String algorithm,
			final int iterations,
			final String encoding)
	{
		this(optional, algorithm, 0, iterations, encoding);
	}
	
	/**
	 * @param algorithm an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestHash(
			final boolean optional,
			final String algorithm,
			final int saltLength,
			final int iterations,
			final String encoding)
	{
		super(optional, algorithmName(algorithm), hashLength(algorithm)+saltLength, encoding);
		this.algorithm = algorithm;
		this.algorithmLength = hashLength(algorithm);
		this.saltLength = saltLength;
		this.saltSource = saltLength>0 ? new SecureRandom() : null;
		this.iterations = iterations;
		if(saltLength<0)
			throw new IllegalArgumentException("saltLength must be at least zero, but was " + saltLength);
		if(iterations<1)
			throw new IllegalArgumentException("iterations must be at least one, but was " + iterations);
	}
	
	private static final int hashLength(final String algorithm)
	{
		final MessageDigest digest = MessageDigestUtil.getInstance(algorithm);
		
		final int digestLength = digest.getDigestLength();
		if(digestLength<=0)
			throw new IllegalArgumentException("MessageDigest#getDigestLength() not supported: " + digestLength);
		
		return digestLength;
	}

	private static final String algorithmName(final String algorithm)
	{
		return algorithm.replaceAll("-", "");
	}
	
	/**
	 * @param algorithm an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public MessageDigestHash(
			final boolean optional,
			final String algorithm,
			final int iterations)
	{
		this(optional, algorithm, iterations, "utf8");
	}
	
	@Override
	public MessageDigestHash optional()
	{
		return new MessageDigestHash(true, algorithm, iterations, getEncoding());
	}
	
	/**
	 * For tests only !!!
	 */
	java.util.Random setSaltSource(final java.util.Random saltSource)
	{
		if(this.saltSource==null)
			throw new RuntimeException();
		
		final java.util.Random result = this.saltSource;
		this.saltSource = saltSource;
		return result;
	}
	
	public final int getIterations()
	{
		return iterations;
	}
	
	@Override
	public final byte[] hash(final byte[] plainText)
	{
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(algorithm);
		messageDigest.reset();
		
		final byte[] result = new byte[saltLength + algorithmLength];
		
		// http://www.owasp.org/index.php/Hashing_Java
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
			messageDigest.digest(result, saltLength, algorithmLength);
			
			for(int i = 1; i<iterations; i++)
			{
				messageDigest.update(result, saltLength, algorithmLength);
				messageDigest.digest(result, saltLength, algorithmLength);
			}
		}
		catch(DigestException e)
		{
			throw new RuntimeException(e);
		}
		
		return result;
	}
	
	@Override
	public final boolean check(final byte[] plainText, final byte[] hash)
	{
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(algorithm);
		messageDigest.reset();
		
		if(saltLength>0)
			messageDigest.update(hash, 0, saltLength);
		
		messageDigest.update(plainText);
		
		final byte[] result = messageDigest.digest();
		
		for(int i = 1; i<iterations; i++)
		{
			messageDigest.update(result);
			try
			{
				messageDigest.digest(result, 0, result.length);
			}
			catch(DigestException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		for(int i = 0; i<algorithmLength; i++)
			if(result[i]!=hash[i+saltLength])
				return false;
		return true;
	}
}

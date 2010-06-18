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

import java.io.UnsupportedEncodingException;
import java.util.Set;

import com.exedio.cope.StringCharSetViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;

/**
 * Adapts hash algorithms that work with strings
 * to hash algorithms that work with byte arrays.
 *
 * @author Ralf Wiebicke
 */
public final class ByteHash implements Hash.Algorithm // TODO rename
{
	private static final long serialVersionUID = 1l;
	
	private final Algorithm algorithm;
	private final String encoding;

	public ByteHash(
			final Algorithm algorithm,
			final String encoding)
	{
		this.algorithm = algorithm;
		this.encoding = encoding;

		try
		{
			encode("test");
		}
		catch(UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException(e);
		}
	}
	
	public StringField newStorage(final boolean optional)
	{
		return length(optional(new StringField().charSet(CharSet.HEX_LOWER), optional), algorithm.length());
	}
	
	private static StringField optional(final StringField f, final boolean optional)
	{
		return optional ? f.optional() : f;
	}
	
	private static StringField length(final StringField f, final int hashLength)
	{
		return f.lengthExact(2 * hashLength); // factor two is because hex encoding needs two characters per byte
	}
	
	public ByteHash(
			final Algorithm algorithm)
	{
		this(algorithm, "utf8");
	}
	
	public Algorithm getAlgorithm()
	{
		return algorithm;
	}
	
	public String getEncoding()
	{
		return encoding;
	}
	
	private byte[] encode(final String s) throws UnsupportedEncodingException
	{
		return s.getBytes(encoding);
	}
	
	public void reduceInitialExceptions(final Set<Class<? extends Throwable>> result)
	{
		result.remove(StringLengthViolationException.class);
		result.remove(StringCharSetViolationException.class);
	}
	
	public String name()
	{
		return algorithm.name();
	}
	
	public int length()
	{
		return 2 * algorithm.length(); // factor two is because hex encoding needs two characters per byte
	}
	
	public String hash(final String plainText)
	{
		try
		{
			return Hex.encodeLower(algorithm.hash(encode(plainText)));
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(encoding, e);
		}
	}
	
	public boolean check(final String plainText, final String hash)
	{
		try
		{
			return algorithm.check(encode(plainText), Hex.decodeLower(hash));
		}
		catch(UnsupportedEncodingException e)
		{
			throw new RuntimeException(encoding, e);
		}
	}
	
	interface Algorithm
	{
		String name();
		int length();
		byte[] hash(byte[] plainText);
		boolean check(byte[] plainText, byte[] hash);
	}
}

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
 * Uses hash algorithms that work with byte arrays.
 *
 * @author Ralf Wiebicke
 */
public class ByteHash extends Hash // TODO make final
{
	private static final long serialVersionUID = 1l;
	
	private final Algorithm algorithm;
	private final String encoding;

	public ByteHash(
			final boolean optional,
			final Algorithm algorithm,
			final String encoding)
	{
		super(length(optional(new StringField().charSet(CharSet.HEX_LOWER), optional), algorithm.length()), algorithm.name());
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
	
	private static final StringField optional(final StringField f, final boolean optional)
	{
		return optional ? f.optional() : f;
	}
	
	private static final StringField length(final StringField f, final int hashLength)
	{
		return f.lengthExact(2 * hashLength); // factor two is because hex encoding needs two characters per byte
	}
	
	public ByteHash(
			final boolean optional,
			final Algorithm algorithm)
	{
		this(optional, algorithm, "utf8");
	}
	
	@Override
	public ByteHash optional()
	{
		return new ByteHash(true, algorithm, encoding);
	}
	
	public final Algorithm getAlgorithm()
	{
		return algorithm;
	}
	
	public final String getEncoding()
	{
		return encoding;
	}
	
	private final byte[] encode(final String s) throws UnsupportedEncodingException
	{
		return s.getBytes(encoding);
	}
	
	@Override
	public final Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		result.remove(StringLengthViolationException.class);
		result.remove(StringCharSetViolationException.class);
		return result;
	}
	
	@Override
	public final String hash(final String plainText)
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
	
	@Override
	public final boolean check(final String plainText, final String hash)
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

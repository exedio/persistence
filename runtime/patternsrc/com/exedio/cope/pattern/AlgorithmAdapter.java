/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.Hash.Algorithm;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import java.io.UnsupportedEncodingException;

final class AlgorithmAdapter implements HashAlgorithm
{
	static HashAlgorithm wrap(
			final Hash.Algorithm algorithm,
			final String encoding)
	{
		return (algorithm!=null) ? new AlgorithmAdapter(algorithm, encoding) : null;
	}

	private final Hash.Algorithm algorithm;
	private final String encoding;

	private AlgorithmAdapter(
			final Hash.Algorithm algorithm,
			final String encoding)
	{
		if(algorithm==null)
			throw new NullPointerException("algorithm");
		if(encoding==null)
			throw new NullPointerException("encoding");

		this.algorithm = algorithm;

		this.encoding = encoding;
		encode("test");
	}

	private byte[] encode(final String s)
	{
		try
		{
			return s.getBytes(encoding);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String getID()
	{
		return algorithm.name();
	}

	@Override
	public String getDescription()
	{
		return algorithm.name();
	}

	@Override
	public StringField constrainStorage(final StringField storage)
	{
		return storage.
				charSet(CharSet.HEX_LOWER).
				lengthExact(2 * algorithm.length()); // factor two is because hex encoding needs two characters per byte
	}

	@Override
	public String hash(final String plainText)
	{
		return Hex.encodeLower(algorithm.hash(encode(plainText)));
	}

	@Override
	public boolean check(final String plainText, final String hash)
	{
		return algorithm.check(encode(plainText), Hex.decodeLower(hash));
	}


	@Deprecated
	static Hash.Algorithm unwrap(final HashAlgorithm algorithm, final StringField storage)
	{
		if(algorithm instanceof AlgorithmAdapter)
			return ((AlgorithmAdapter)algorithm).algorithm;

		return new Algorithm()
		{
			@Override
			public String name()
			{
				return algorithm.getID();
			}

			@Override
			public int length()
			{
				return storage.getMinimumLength();
			}

			@Override
			public byte[] hash(final byte[] plainText)
			{
				throw new IllegalArgumentException("not implementable");
			}

			@Override
			public boolean check(final byte[] plainText, final byte[] hash)
			{
				throw new IllegalArgumentException("not implementable");
			}

			@Override
			public boolean compatibleTo(final Algorithm other)
			{
				throw new IllegalArgumentException("not implementable");
			}
		};
	}

	@Deprecated
	static String unwrapEncoding(final HashAlgorithm algorithm)
	{
		return
				(algorithm instanceof AlgorithmAdapter)
				? ((AlgorithmAdapter)algorithm).encoding
				: "UNKNOWN";
	}
}

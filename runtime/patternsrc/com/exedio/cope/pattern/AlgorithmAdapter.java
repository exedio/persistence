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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.Hash.Algorithm;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import java.nio.charset.Charset;

final class AlgorithmAdapter implements HashAlgorithm
{
	static HashAlgorithm wrap(
			final Hash.Algorithm algorithm,
			final Charset charset)
	{
		return (algorithm!=null) ? new AlgorithmAdapter(algorithm, charset) : null;
	}

	private final Hash.Algorithm algorithm;
	final Charset charset;

	private AlgorithmAdapter(
			final Hash.Algorithm algorithm,
			final Charset charset)
	{
		this.algorithm = requireNonNull(algorithm, "algorithm");
		this.charset = requireNonNull(charset, "charset");
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
		return Hex.encodeLower(algorithm.hash(plainText.getBytes(charset)));
	}

	@Override
	public boolean check(final String plainText, final String hash)
	{
		return algorithm.check(plainText.getBytes(charset), Hex.decodeLower(hash));
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
}

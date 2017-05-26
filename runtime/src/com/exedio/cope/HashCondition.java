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

package com.exedio.cope;

import static com.exedio.cope.misc.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

final class HashCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	private final StringField hash;
	private final String algorithm;
	private final DataField data;

	HashCondition(
			@Nonnull final StringField hash,
			@Nonnull final String algorithm,
			@Nonnull final DataField data)
	{
		this.hash = requireNonNull(hash, "hash");
		this.algorithm = requireNonEmpty(algorithm, "algorithm");
		this.data = requireNonNull(data, "data");
		// TODO early check for correct algorithm
	}

	@Override
	void append(final Statement bf)
	{
		bf.append('(').
			append(hash).
			append('=');
		bf.dialect.appendBlobHash(bf, data.getColumn(), null, algorithm);
		bf.append(')');
	}

	@Override
	Trilean getTri(final Item item)
	{
		throw new IllegalArgumentException("not supported: " + hash);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(hash, tc, null);
		// Cope.check(data, tc, null); TODO
	}

	@Override
	HashCondition copy(final CopyMapper mapper)
	{
		throw new RuntimeException("not yet implemented"); // TODO
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof HashCondition))
			return false;

		final HashCondition o = (HashCondition)other;

		return
				hash.equals(o.hash) &&
				algorithm.equals(o.algorithm) &&
				data.equals(o.data);
	}

	@Override
	public int hashCode()
	{
		return
				hash.hashCode() ^
				algorithm.hashCode() ^
				data.hashCode() ^
				3456347;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		hash.toString(bf, defaultType);
		bf.append("=").
			append(algorithm).
			append('(');
		data.toString(bf, defaultType);
		bf.append(")");
	}
}

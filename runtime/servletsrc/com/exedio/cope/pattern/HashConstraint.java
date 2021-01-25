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

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.UnsupportedQueryException;
import com.exedio.cope.Vault;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public final class HashConstraint extends Pattern
{
	private static final long serialVersionUID = 1l;

	private final StringField hash;
	private final Supplier<String> algorithm;
	private final DataField data;

	public HashConstraint(
			final StringField hash,
			final String algorithm,
			final DataField data)
	{
		this(hash, () -> algorithm, data);
		requireNonNull(algorithm, "algorithm");
	}

	public HashConstraint(
			final StringField hash,
			final Supplier<String> algorithm,
			final DataField data)
	{
		this.hash = requireNonNull(hash, "hash");
		this.algorithm = requireNonNull(algorithm, "algorithm");
		this.data = requireNonNull(data, "data");
	}

	public StringField getHash()
	{
		return hash;
	}

	public String getAlgorithm()
	{
		return algorithm.get();
	}

	public DataField getData()
	{
		return data;
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault},
	 * or the {@link #getAlgorithm() algorithm} is not supported by the database.
	 */
	@Nonnull
	public Condition hashMatchesIfSupported()
	{
		return hash.hashMatchesIfSupported(algorithm.get(), data);
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault},
	 * or the {@link #getAlgorithm() algorithm} is not supported by the database.
	 */
	@Nonnull
	public Condition hashDoesNotMatchIfSupported()
	{
		return hash.hashDoesNotMatchIfSupported(algorithm.get(), data);
	}
}

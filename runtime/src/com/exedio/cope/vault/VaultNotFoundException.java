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

package com.exedio.cope.vault;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

/**
 * Signals, that an attempt to fetch data by its hash from a {@link VaultService} failed
 * because for this hash nothing was {@link VaultService#put(String, byte[]) put} to the service before.
 * <p>
 * Is thrown for valid hashes only, i.e. hashes for which the service could store data.
 * Should not be thrown for invalid hashes, such as hashes which are either too short or too long
 * or which do contain invalid (non-hex) characters.
 * Must not be thrown for any other error, such as IO errors, authentication failures etc.
 *
 * @author Ralf Wiebicke
 */
public final class VaultNotFoundException extends Exception
{
	private static final long serialVersionUID = 1l;

	private final String hash;

	public VaultNotFoundException(@Nonnull final String hash)
	{
		this.hash = requireNonNull(hash);
	}

	public VaultNotFoundException(@Nonnull final String hash, final Throwable cause)
	{
		super(cause);
		this.hash = requireNonNull(hash);
	}

	/**
	 * @deprecated Use {@link #getHashComplete()} instead
	 */
	@Deprecated
	@Nonnull
	public String getHash()
	{
		return getHashComplete();
	}

	/**
	 * Consider using {@link #getHashAnonymous()} instead to avoid leaking sensitive data.
	 * Hashes may be one of the access control keys for the data hashed -
	 * so putting hashes into log files etc. may be dangerous.
	 */
	@Nonnull
	public String getHashComplete()
	{
		return hash;
	}

	@Nonnull
	public String getHashAnonymous()
	{
		return anonymiseHash(hash);
	}

	@Override
	public String getMessage()
	{
		return "hash not found in vault: " + getHashAnonymous();
	}

	public static String anonymiseHash(final String hash)
	{
		if(hash==null)
			return null;

		final int length = hash.length();
		return
				length>16
				? (hash.substring(0, 16) + "xx" + length)
				: hash;
	}
}

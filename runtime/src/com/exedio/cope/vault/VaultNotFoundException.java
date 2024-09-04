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

import java.io.Serial;
import java.util.IdentityHashMap;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Signals, that an attempt to fetch data by its hash from a {@link VaultService} failed
 * because for this hash nothing was
 * {@link VaultService#put(String, byte[]) put}
 * to the service before.
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
	@Serial
	private static final long serialVersionUID = 1l;

	private final String hash;

	public VaultNotFoundException(@Nonnull final String hash)
	{
		this.hash = requireNonNull(hash);
	}

	public VaultNotFoundException(@Nonnull final String hash, final Throwable cause)
	{
		// Specifying message==null avoids detailMessage computed from cause in
		// Throwable constructor, not needed because getMessage is overridden anyway.
		super(null, anonymiseHash(hash, cause));
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

	private static Throwable anonymiseHash(@Nonnull final String hash, final Throwable throwable)
	{
		if(throwable==null)
			return null;

		final String hashAnon = anonymiseHash(hash);
		if(hash.equals(hashAnon))
			return throwable;

		return anonymiseHash(hash, hashAnon, throwable, new IdentityHashMap<>(), 30);
	}

	private static Throwable anonymiseHash(
			@Nonnull final String hash,
			@Nonnull final String hashAnon,
			final Throwable throwable,
			// IdentityHashMap guards against malicious overrides of Throwable#equals
			final IdentityHashMap<Throwable,Void> mapping,
			final int recursionLimit)
	{
		if(throwable==null || mapping.containsKey(throwable))
			return null;
		mapping.put(throwable, null);

		if(recursionLimit<0)
			throw new RuntimeException(hashAnon, throwable);

		final String messageOrig = throwable.getMessage();
		final String messageAnon = anonymiseHash(hash, hashAnon, messageOrig);

		final Throwable causeOrig = throwable.getCause();
		final Throwable causeAnon = anonymiseHash(hash, hashAnon, causeOrig, mapping, recursionLimit - 1);

		if(Objects.equals(messageOrig, messageAnon) &&
			causeOrig==causeAnon)
			return throwable;

		final Anonymous result = new Anonymous(messageAnon, causeAnon, throwable);
		result.setStackTrace(throwable.getStackTrace());
		return result;
	}

	private static String anonymiseHash(
			@Nonnull final String hash,
			@Nonnull final String hashAnon,
			final String message)
	{
		if(message==null)
			return null;

		final int PREFIX_LENGTH = 10;
		final String hashPart = hash.substring(PREFIX_LENGTH);

		return message.replace(hashPart, hashAnon.substring(PREFIX_LENGTH));
	}

	private static final class Anonymous extends Exception
	{
		private final String messageAnon;
		private final Throwable origin;

		Anonymous(
				final String messageAnon,
				final Throwable cause,
				@Nonnull final Throwable origin)
		{
			// Specifying message==null avoids detailMessage computed from cause in
			// Throwable constructor, not needed because getMessage is overridden anyway.
			super(null, cause);
			this.messageAnon = messageAnon;
			this.origin = origin;
		}

		@Override
		public String getMessage()
		{
			final String originName = origin.getClass().getName();
			return
					messageAnon!=null
					? originName + ": " + messageAnon
					: originName;
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}
}

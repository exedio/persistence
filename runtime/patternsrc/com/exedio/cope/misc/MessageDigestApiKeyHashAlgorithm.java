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

package com.exedio.cope.misc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.HashAlgorithm;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Check;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestFactory;
import java.nio.charset.Charset;
import java.security.MessageDigest;

public final class MessageDigestApiKeyHashAlgorithm implements HashAlgorithm
{
	/**
	 * @param digest an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public static MessageDigestApiKeyHashAlgorithm create(
			final int minimumPlainTextLength,
			final String digest)
	{
		return create(minimumPlainTextLength, UTF_8, new MessageDigestFactory(digest));
	}

	/**
	 * @param digest an algorithm name suitable for {@link MessageDigest#getInstance(String)}.
	 */
	public static MessageDigestApiKeyHashAlgorithm create(
			final int minimumPlainTextLength,
			final Charset charset,
			final String digest)
	{
		return create(minimumPlainTextLength, charset, new MessageDigestFactory(digest));
	}

	public static MessageDigestApiKeyHashAlgorithm create(
			final int minimumPlainTextLength,
			final MessageDigestFactory digest)
	{
		return create(minimumPlainTextLength, UTF_8, digest);
	}

	public static MessageDigestApiKeyHashAlgorithm create(
			final int minimumPlainTextLength,
			final Charset charset,
			final MessageDigestFactory digest)
	{
		return new MessageDigestApiKeyHashAlgorithm(minimumPlainTextLength, charset, digest);
	}

	private final int minimumPlainTextLength;
	private final Charset charset;
	private final MessageDigestFactory digest;
	private final int digestLengthHex;
	private final String id;

	private MessageDigestApiKeyHashAlgorithm(
			final int minimumPlainTextLength,
			final Charset charset,
			final MessageDigestFactory digest)
	{
		this.minimumPlainTextLength = Check.requireAtLeast(minimumPlainTextLength, "minimumPlainTextLength", 50);
		this.charset = requireNonNull(charset, "charset");
		this.digest = requireNonNull(digest, "digest");
		this.digestLengthHex = digest.getLengthHex();
		this.id = digest.getAlgorithm().replaceAll("-", "") + '-' + charset.name().replaceAll("-", "");
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public String getDescription()
	{
		return id + '(' + minimumPlainTextLength + ')';
	}

	@Override
	public StringField constrainStorage(final StringField storage)
	{
		return storage.
				charSet(CharSet.HEX_LOWER).
				lengthExact(digestLengthHex);
	}

	@Override
	public String hash(final String plainText)
	{
		requireNonNull(plainText, "plainText");
		final int plainTextLength = plainText.length();
		if(plainTextLength<minimumPlainTextLength)
			throw new IllegalArgumentException(
					"plainText must have at least " + minimumPlainTextLength + " characters, " +
					"but had " + plainTextLength);

		return Hex.encodeLower(digest.newInstance().digest(plainText.getBytes(charset)));
	}

	@Override
	public boolean check(final String plainText, final String hash)
	{
		requireNonNull(hash, "hash");
		return hash.equals(hash(plainText));
	}
}

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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static com.exedio.cope.vault.VaultProperties.checkBucket;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.MessageDigestFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.annotation.Nonnull;

final class VaultResilientServiceProxy implements VaultResilientService
{
	final VaultService service;
	private final int hashLength;
	private final String hashEmpty;
	private volatile boolean closed = false;

	VaultResilientServiceProxy(
			final VaultService service,
			final MessageDigestFactory messageDigestFactory)
	{
		if(service instanceof VaultResilientService)
			throw new IllegalArgumentException();

		this.service = requireNonNull(service, "service");
		this.hashLength = messageDigestFactory.getLengthHex();
		this.hashEmpty = messageDigestFactory.getDigestForEmptyByteSequenceHex();
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		requireNonNull(ctx, "ctx");
		requireNonClosed();

		service.purgeSchema(ctx);
	}

	@Override
	public void close()
	{
		closed = true;
		service.close();
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		requireHash(hash);
		requireNonClosed();

		if(isEmptyHash(hash))
			return EMPTY_BYTES;

		return service.get(hash);
	}

	private static final byte[] EMPTY_BYTES = {};

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		requireHash(hash);
		requireNonNull(sink, "sink");
		requireNonClosed();

		if(isEmptyHash(hash))
			return;

		service.get(hash, sink);
	}

	@Override
	public boolean put(final String hash, final byte[] value)
	{
		requireHash(hash);
		requireNonNull(value, "value");
		requireNonClosed();

		if(isEmptyHash(hash))
			return false;

		if(value.length==0)
			throw new IllegalArgumentException(
					"hash >" + anonymiseHash(hash) + "< put with empty value, " +
					"but empty hash is >" + hashEmpty + '<');

		return service.put(hash, value);
	}

	@Override
	public boolean put(final String hash, final InputStream value) throws IOException
	{
		requireHash(hash);
		requireNonNull(value, "value");
		requireNonClosed();

		if(isEmptyHash(hash))
			return false;

		return service.put(hash, value);
	}

	@Override
	public boolean put(final String hash, final Path value) throws IOException
	{
		requireHash(hash);
		requireNonNull(value, "value");
		requireNonClosed();

		if(isEmptyHash(hash))
			return false;

		return service.put(hash, value);
	}

	private void requireHash(@Nonnull final String hash)
	{
		requireNonNull(hash, "hash");

		final int actualLength = hash.length();
		if(hashLength!=actualLength)
			throw new IllegalArgumentException(
					"hash >" + anonymiseHash(hash) + "< must have length " + hashLength + ", but has " + actualLength);

		final int charSetViolation = CharSet.HEX_LOWER.indexOfNotContains(hash);
		if(charSetViolation>=0)
			throw new IllegalArgumentException(
					"hash >" + anonymiseHash(hash) + "< contains illegal character >" + hash.charAt(charSetViolation) + "< " +
					"at position " + charSetViolation);
	}

	private boolean isEmptyHash(@Nonnull final String hash)
	{
		return hash.equals(hashEmpty);
	}

	@Override
	public Object probeBucketTag(final String bucket) throws Exception
	{
		checkBucket(bucket, message ->
				new IllegalArgumentException("bucket " + message));
		requireNonClosed();

		return service.probeBucketTag(bucket);
	}

	private void requireNonClosed()
	{
		if(closed)
			throw new IllegalStateException("closed");
	}

	@Override
	public String toString()
	{
		return service.toString();
	}
}

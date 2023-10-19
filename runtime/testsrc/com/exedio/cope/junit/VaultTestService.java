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

package com.exedio.cope.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.MessageDigestFactory;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vault.VaultServiceParameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * An implementation of {@link VaultService} to be used in junit tests.
 * Stores data transiently for the lifetime of the service instance,
 * which corresponds to the time of the cope
 * {@link com.exedio.cope.Model#connect(ConnectProperties) connect}.
 */
public final class VaultTestService implements VaultService
{
	private final MessageDigestFactory messageDigestFactory;
	private final HashMap<String, byte[]> store = new HashMap<>();

	public VaultTestService(final VaultServiceParameters parameters)
	{
		this.messageDigestFactory = parameters.getMessageDigestFactory();
		assertNotNull(messageDigestFactory);
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		assertNotNull(ctx);
	}


	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		return store(hash).length;
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		return store(hash);
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		assertNotNull(sink);

		sink.write(store(hash));
	}

	private byte[] store(final String hash) throws VaultNotFoundException
	{
		assertHash(hash);

		final byte[] result = store.get(hash);
		if(result==null)
			throw new VaultNotFoundException(hash);
		return result;
	}


	@Override
	public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
	{
		assertHash(hash);
		assertNotNull(value);
		assertNotNull(info);
		assertFalse(
				value.length==0,
				"empty byte sequence is not handled by service implementations");

		assertEquals(hash, Hex.encodeLower(
				messageDigestFactory.
						digest(value)));

		return store.put(hash, value)==null;
	}

	@Override
	public boolean put(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		assertHash(hash);
		assertNotNull(value);
		assertNotNull(info);

		final byte[] bytes;
		final byte[] b = new byte[55];
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			for(int len = value.read(b); len>=0; len = value.read(b))
				baos.write(b, 0, len);
			bytes = baos.toByteArray();
		}

		return put(hash, bytes, info);
	}

	@Override
	public boolean put(final String hash, final Path value, final VaultPutInfo info) throws IOException
	{
		assertHash(hash);
		assertNotNull(value);
		assertNotNull(info);

		try(InputStream s = Files.newInputStream(value))
		{
			return put(hash, s, info);
		}
	}


	private void assertHash(final String hash)
	{
		assertNotNull(hash);
		assertEquals(messageDigestFactory.getLengthHex(), hash.length(), hash);
		assertEquals(-1, CharSet.HEX_LOWER.indexOfNotContains(hash), hash);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}

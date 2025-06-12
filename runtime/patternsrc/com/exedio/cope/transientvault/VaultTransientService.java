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

package com.exedio.cope.transientvault;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vault.VaultServiceParameters;
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
public final class VaultTransientService implements VaultService
{
	private final HashMap<String, byte[]> store = new HashMap<>();

	VaultTransientService(@SuppressWarnings("unused") final VaultServiceParameters parameters)
	{
	}


	@Override
	public boolean contains(final String hash)
	{
		return store.containsKey(hash);
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		return store(hash);
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		sink.write(store(hash));
	}

	private byte[] store(final String hash) throws VaultNotFoundException
	{
		final byte[] result = store.get(hash);
		if(result==null)
			throw new VaultNotFoundException(hash);
		return result;
	}


	@Override
	public boolean put(final String hash, final byte[] value)
	{
		requireNonNull(value);
		return store.put(hash, value)==null;
	}

	@Override
	public boolean put(final String hash, final InputStream value) throws IOException
	{
		return put(hash, value.readAllBytes());
	}

	@Override
	public boolean put(final String hash, final Path value) throws IOException
	{
		try(InputStream s = Files.newInputStream(value))
		{
			return put(hash, s);
		}
	}


	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}

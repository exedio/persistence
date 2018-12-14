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

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;

import com.exedio.cope.util.JobContext;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultPutInfo;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vault.VaultServiceParameters;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * An implementation of {@link VaultService} where
 * all methods do fail with an
 * {@link AssertionError}.
 *
 * You may want to subclass this class instead of
 * implementing {@link VaultService} directly
 * to make your subclass cope with new methods
 * in {@link VaultService}.
 */
@SuppressWarnings("RedundantThrows") // RedundantThrows: allow subclasses to throw exceptions
public class AssertionErrorVaultService implements VaultService
{
	public AssertionErrorVaultService()
	{
		// empty constructor allows default constructor in subclasses
	}

	public AssertionErrorVaultService(@SuppressWarnings("unused") final VaultServiceParameters parameters)
	{
	}

	@Override
	public void purgeSchema(final JobContext ctx)
	{
		throw new AssertionError();
	}

	@Override
	public void close()
	{
		throw new AssertionError();
	}

	@Override
	public long getLength(final String hash) throws VaultNotFoundException
	{
		throw new AssertionError(anonymiseHash(hash));
	}

	@Override
	public byte[] get(final String hash) throws VaultNotFoundException
	{
		throw new AssertionError(anonymiseHash(hash));
	}

	@Override
	public void get(final String hash, final OutputStream sink) throws VaultNotFoundException, IOException
	{
		throw new AssertionError(anonymiseHash(hash));
	}


	@Override
	public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
	{
		throw new AssertionError(anonymiseHash(hash) + '/' + info);
	}

	@Override
	public boolean put(final String hash, final InputStream value, final VaultPutInfo info) throws IOException
	{
		throw new AssertionError(anonymiseHash(hash) + '/' + info);
	}

	@Override
	public boolean put(final String hash, final Path value, final VaultPutInfo info) throws IOException
	{
		throw new AssertionError(anonymiseHash(hash) + '/' + info);
	}

	@Override
	public String toString()
	{
		throw new AssertionError();
	}
}

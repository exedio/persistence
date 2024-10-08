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

import java.io.InputStream;
import java.nio.file.Path;

public abstract class VaultNonWritableService implements VaultService
{
	protected VaultNonWritableService(
			final VaultServiceParameters parameters)
	{
		if(parameters.isWritable())
			throw new IllegalArgumentException(
					getClass().getName() + " does not support isWritable");
	}

	@Override
	public final boolean put(final String hash, final byte[] value)
	{
		throw newPutException(hash);
	}

	@Override
	public final boolean put(final String hash, final InputStream value)
	{
		throw newPutException(hash);
	}

	@Override
	public final boolean put(final String hash, final Path value)
	{
		throw newPutException(hash);
	}

	private IllegalStateException newPutException(final String hash)
	{
		throw new IllegalStateException(
				"not writable: " + anonymiseHash(hash) + ' ' + getClass().getName());
	}
}

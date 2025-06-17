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

import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class VaultMinimalServiceTest extends VaultServiceTest
{
	@Override
	protected Class<? extends VaultService> getServiceClass()
	{
		return Service.class;
	}

	@Override
	protected boolean supportsContains()
	{
		return false;
	}

	@ServiceProperties(VaultMockService.Props.class)
	private static final class Service implements VaultService
	{
		private final VaultMockService nested;

		Service(
				final VaultServiceParameters parameters,
				final VaultMockService.Props properties)
		{
			nested = new VaultMockService(parameters, properties);
		}

		@Override
		public byte[] get(
				@Nonnull final String hash)
				throws VaultNotFoundException
		{
			return nested.get(hash);
		}

		@Override
		public void get(
				@Nonnull final String hash,
				@Nonnull final OutputStream sink)
				throws VaultNotFoundException, IOException
		{
			nested.get(hash, sink);
		}

		@Override
		public boolean put(
				@Nonnull final String hash,
				@Nonnull final byte[] value)
		{
			return nested.put(hash, value);
		}

		@Override
		public boolean put(
				@Nonnull final String hash,
				@Nonnull final InputStream value)
				throws IOException
		{
			return nested.put(hash, value);
		}

		@Override
		public boolean put(
				@Nonnull final String hash,
				@Nonnull final Path value)
				throws IOException
		{
			return nested.put(hash, value);
		}
	}
}

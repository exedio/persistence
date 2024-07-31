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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class VaultServiceDefaultTest
{
	@Test void probeBucketTag()
	{
		@SuppressWarnings("resource")
		final VaultDefaultService s = new VaultDefaultService();
		assertFails(
				() -> s.probeGenuineServiceKey(null),
				BucketTagNotSupported.class,
				"not supported by com.exedio.cope.vault.VaultServiceDefaultTest$VaultDefaultService");
	}

	private static final class VaultDefaultService implements VaultService
	{
		@Override
		public byte[] get(final String hash)
		{
			throw new AssertionError();
		}
		@Override
		public void get(final String hash, final OutputStream sink)
		{
			throw new AssertionError();
		}
		@Override
		public boolean put(final String hash, final byte[] value, final VaultPutInfo info)
		{
			throw new AssertionError();
		}
		@Override
		public boolean put(final String hash, final InputStream value, final VaultPutInfo info)
		{
			throw new AssertionError();
		}
		@Override
		public boolean put(final String hash, final Path value, final VaultPutInfo info)
		{
			throw new AssertionError();
		}
	}

	@Test void newProbeAborter()
	{
		final Exception aborter = VaultService.newProbeAborter("myMessage");
		assertEquals("myMessage", aborter.getMessage());
		assertEquals("com.exedio.cope.vault.BucketTagNotSupported", aborter.getClass().getName());
	}
}

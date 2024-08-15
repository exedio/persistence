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

package com.exedio.cope.vaultmock;

import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultService;
import com.exedio.cope.vaulttest.VaultServiceTest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultMockServicePutTest extends VaultServiceTest
{
	@Override
	protected Class<? extends VaultService> getServiceClass()
	{
		return VaultMockService.class;
	}

	@Override
	protected VaultService maskService(final VaultService service)
	{
		return new VaultServiceMask(service);
	}

	private static class VaultServiceMask implements VaultService
	{
		private final VaultService service;

		VaultServiceMask(final VaultService service)
		{
			this.service = service;
		}
		@Override
		public byte[] get(@Nonnull final String hash) throws VaultNotFoundException
		{
			return service.get(hash);
		}
		@Override
		public void get(@Nonnull final String hash, @Nonnull final OutputStream sink) throws VaultNotFoundException, IOException
		{
			service.get(hash, sink);
		}
		@Override
		public boolean put(@Nonnull final String hash, @Nonnull final byte[] value)
		{
			throw new AssertionFailedError(anonymiseHash(hash));
		}
		@Override
		public boolean put(
				@Nonnull final String hash,
				@Nonnull final InputStream value)
		{
			throw new AssertionFailedError(anonymiseHash(hash));
		}
		@Override
		public boolean put(
				@Nonnull final String hash,
				@Nonnull final Path value)
		{
			throw new AssertionFailedError(anonymiseHash(hash));
		}
		@Override
		public void close()
		{
			service.close();
		}
		@Override
		public Object probeBucketTag(final String bucket) throws Exception
		{
			return service.probeBucketTag(bucket);
		}
	}

	@Override
	protected VaultService maskServicePut(final VaultService service)
	{
		return new VaultService()
		{
			@Override
			public byte[] get(@Nonnull final String hash)
			{
				throw new AssertionFailedError(anonymiseHash(hash));
			}
			@Override
			public void get(@Nonnull final String hash, @Nonnull final OutputStream sink)
			{
				throw new AssertionFailedError(anonymiseHash(hash));
			}
			@Override
			public boolean put(@Nonnull final String hash, @Nonnull final byte[] value)
			{
				assertFalse(closed);
				return service.put(hash, value);
			}
			@Override
			public boolean put(
					@Nonnull final String hash,
					@Nonnull final InputStream value) throws IOException
			{
				assertFalse(closed);
				return service.put(hash, value);
			}
			@Override
			public boolean put(
					@Nonnull final String hash,
					@Nonnull final Path value) throws IOException
			{
				assertFalse(closed);
				return service.put(hash, value);
			}

			private boolean closed = false;

			@Override
			public void close()
			{
				// must not forward close to service, otherwise service is closed twice
				assertFalse(closed);
				closed = true;
			}
		};
	}

	@Override
	protected Properties getServiceProperties()
	{
		final Properties result = new Properties();
		result.setProperty("example", "exampleValue");
		return result;
	}

	@Test void serviceProperties()
	{
		final VaultMockService service = (VaultMockService)((VaultServiceMask)getService()).service;
		assertSame(getProperties().bucket("default"), service.bucketProperties);
		assertEquals("exampleValue", service.serviceProperties.example);
		assertEquals("default", service.bucket);
		assertEquals(true, service.writable);
	}

	@Override
	@Test protected void probeBucketTag() throws Exception
	{
		assertEquals("mock:default(my-Bucket)", getService().probeBucketTag("my-Bucket"));
	}
}

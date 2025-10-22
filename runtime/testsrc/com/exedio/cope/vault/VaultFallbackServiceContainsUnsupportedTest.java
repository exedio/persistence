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
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultFallbackServiceContainsUnsupportedTest
{
	@Test void testSupported()
	{
		//noinspection resource OK: does not allocate resources
		final var service = newService(VaultMockService.class, VaultMockService.class);

		final ArrayList<String> result = new ArrayList<>();
		service.addToAncestryPath(HASH, result::add);
		assertEquals(List.of("reference1"), result);
	}

	@Test void testMain()
	{
		//noinspection resource OK: does not allocate resources
		final var service = newService(Service.class, VaultMockService.class);

		final IllegalArgumentException e = assertFails(
				() -> service.addToAncestryPath(HASH, SINK),
				IllegalArgumentException.class,
				"main service (com.exedio.cope.vault.VaultFallbackServiceContainsUnsupportedTest$Service) " +
				"does not support contains");
		assertNotNull(e.getCause());
		assertEquals(VaultServiceUnsupportedOperationException.class, e.getCause().getClass());
		assertEquals("com.exedio.cope.vault.VaultFallbackServiceContainsUnsupportedTest$1", e.getCause().getMessage());
	}

	@Test void testReference()
	{
		//noinspection resource OK: does not allocate resources
		final var service = newService(VaultMockService.class, Service.class);

		final IllegalArgumentException e = assertFails(
				() -> service.addToAncestryPath(HASH, SINK),
				IllegalArgumentException.class,
				"reference service 0 (com.exedio.cope.vault.VaultFallbackServiceContainsUnsupportedTest$Service) " +
				"does not support contains");
		assertNotNull(e.getCause());
		assertEquals(VaultServiceUnsupportedOperationException.class, e.getCause().getClass());
		assertEquals("com.exedio.cope.vault.VaultFallbackServiceContainsUnsupportedTest$1", e.getCause().getMessage());
	}

	private static VaultFallbackService newService(
			final Class<? extends VaultService> main,
			final Class<? extends VaultService> reference)
	{
		final Source source = cascade(
				single("service", VaultFallbackService.class),
				single("service.referenceCount", 2),
				single("service.main", main),
				single("service.reference", reference),
				single("service.reference1", VaultMockService.class)
		);
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		return (VaultFallbackService)props.newServiceNonResilient(() -> false);
	}

	private static final class Service extends AssertionErrorVaultService
	{
		Service(
				@SuppressWarnings("unused") final VaultServiceParameters parameters)
		{
		}

		@Override
		public boolean contains(@Nonnull final String hash) throws VaultServiceUnsupportedOperationException
		{
			return MINIMAL_SERVICE.contains(hash);
		}
	}

	private static final VaultService MINIMAL_SERVICE = new VaultService()
	{
		@Override
		public byte[] get(final String hash)
		{
			throw new AssertionError(anonymiseHash(hash));
		}
		@Override
		public void get(final String hash, final OutputStream sink)
		{
			throw new AssertionError(anonymiseHash(hash));
		}
		@Override
		public boolean put(final String hash, final byte[] value)
		{
			throw new AssertionError(anonymiseHash(hash));
		}
		@Override
		public boolean put(final String hash, final InputStream value)
		{
			throw new AssertionError(anonymiseHash(hash));
		}
		@Override
		public boolean put(final String hash, final Path value)
		{
			throw new AssertionError(anonymiseHash(hash));
		}
	};

	private static final Consumer<String> SINK = path ->
	{
		throw new AssertionFailedError(path);
	};

	private static final String HASH =
			"0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" +
			"0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
}

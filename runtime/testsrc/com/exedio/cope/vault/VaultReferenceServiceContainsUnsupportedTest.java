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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultReferenceServiceContainsUnsupportedTest
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

		assertFails(
				() -> service.addToAncestryPath(HASH, SINK),
				IllegalArgumentException.class,
				"main service (com.exedio.cope.vault.VaultReferenceServiceContainsUnsupportedTest$Service) " +
				"does not support VaultServiceContains");
	}

	@Test void testReference()
	{
		//noinspection resource OK: does not allocate resources
		final var service = newService(VaultMockService.class, Service.class);

		assertFails(
				() -> service.addToAncestryPath(HASH, SINK),
				IllegalArgumentException.class,
				"reference service 0 (com.exedio.cope.vault.VaultReferenceServiceContainsUnsupportedTest$Service) " +
				"does not support VaultServiceContains");
	}

	private static VaultReferenceService newService(
			final Class<? extends VaultService> main,
			final Class<? extends VaultService> reference)
	{
		final Source source = cascade(
				single("service", VaultReferenceService.class),
				single("service.referenceCount", 2),
				single("service.main", main),
				single("service.reference", reference),
				single("service.reference1", VaultMockService.class)
		);
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		return (VaultReferenceService)props.newServiceNonResilient(() -> false);
	}

	private static final class Service extends AssertionErrorVaultService
	{
		Service(
				@SuppressWarnings("unused") final VaultServiceParameters parameters)
		{
		}
	}

	private static final Consumer<String> SINK = path ->
	{
		throw new AssertionFailedError(path);
	};

	private static final String HASH =
			"0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef" +
			"0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
}

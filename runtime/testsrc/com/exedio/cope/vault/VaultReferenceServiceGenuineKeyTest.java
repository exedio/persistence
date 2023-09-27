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

import static com.exedio.cope.Vault.DEFAULT;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource")
public class VaultReferenceServiceGenuineKeyTest
{
	@Test void succeedSucceed() throws Exception
	{
		final VaultReferenceService service = service("mainGenuine", "refrGenuine");

		assertEquals(
				"mock:mainGenuine(my-Bucket)",
				service.probeGenuineServiceKey("my-Bucket"));
	}
	@Test void succeedAbort()
	{
		final VaultReferenceService service = service("mainGenuine", "ABORT refrGenuine");

		assertStackTrace(true, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT refrGenuine(my-Bucket)"));
	}
	@Test void succeedFail()
	{
		final VaultReferenceService service = service("mainGenuine", "FAIL refrGenuine");

		assertStackTrace(true, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				"FAIL refrGenuine(my-Bucket)"));
	}
	@Test void abortSucceed()
	{
		final VaultReferenceService service = service("ABORT mainGenuine", "refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void abortAbort()
	{
		final VaultReferenceService service = service("ABORT mainGenuine", "ABORT refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void abortFail()
	{
		final VaultReferenceService service = service("ABORT mainGenuine", "FAIL refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void failSucceed()
	{
		final VaultReferenceService service = service("FAIL mainGenuine", "refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}
	@Test void failAbort()
	{
		final VaultReferenceService service = service("FAIL mainGenuine", "ABORT refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}
	@Test void failFail()
	{
		final VaultReferenceService service = service("FAIL mainGenuine", "FAIL refrGenuine");

		assertStackTrace(false, assertFails(
				() -> service.probeGenuineServiceKey("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}

	private static VaultReferenceService service(final String main, final String reference)
	{
		final Source source = cascade(
				single("default.service", VaultReferenceService.class),
				single("default.service.main", VaultMockService.class),
				single("default.service.main.bucketTagAction", main),
				single("default.service.reference", VaultMockService.class),
				single("default.service.reference.bucketTagAction", reference)
		);
		return (VaultReferenceService)VaultProperties.factory().create(source).newServicesNonResilient(DEFAULT).get(DEFAULT);
	}
	private static void assertStackTrace(final boolean expected, final Exception actual)
	{
		assertEquals(expected, stream(actual.getStackTrace()).
				map(StackTraceElement::getMethodName).
				anyMatch("REFERENCE"::equals));
	}
}

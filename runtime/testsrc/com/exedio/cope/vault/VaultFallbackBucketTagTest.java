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
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource")
public class VaultFallbackBucketTagTest
{
	@Test void succeedSucceed() throws Exception
	{
		final var service = service("mainGenuine", "fallGenuine");

		assertEquals(
				"mock:mainGenuine(my-Bucket)",
				service.probeBucketTag("my-Bucket"));
	}
	@Test void succeedSucceedSucceed() throws Exception
	{
		final var service = service("mainGenuine", "fallGenuine0", "fallGenuine1");

		assertEquals(
				"mock:mainGenuine(my-Bucket)",
				service.probeBucketTag("my-Bucket"));
	}
	@Test void succeedAbort()
	{
		final var service = service("mainGenuine", "ABORT fallGenuine");

		assertStackTrace(1, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT fallGenuine(my-Bucket)"));
	}
	@Test void succeedSucceedAbort()
	{
		final var service = service("mainGenuine", "fall0Genuine", "ABORT fall1Genuine");
		assertEquals(2, service.getFallbackServices().size());

		assertStackTrace(2, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT fall1Genuine(my-Bucket)"));
	}
	@Test void succeedAbortSucceed()
	{
		final var service = service("mainGenuine", "ABORT fall0Genuine", "fall1Genuine");

		assertStackTrace(1, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT fall0Genuine(my-Bucket)"));
	}
	@Test void succeedFail()
	{
		final var service = service("mainGenuine", "FAIL fallGenuine");

		assertStackTrace(1, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				"FAIL fallGenuine(my-Bucket)"));
	}
	@Test void abortSucceed()
	{
		final var service = service("ABORT mainGenuine", "fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void abortAbort()
	{
		final var service = service("ABORT mainGenuine", "ABORT fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void abortFail()
	{
		final var service = service("ABORT mainGenuine", "FAIL fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				BucketTagNotSupported.class,
				"ABORT mainGenuine(my-Bucket)"));
	}
	@Test void failSucceed()
	{
		final var service = service("FAIL mainGenuine", "fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}
	@Test void failAbort()
	{
		final var service = service("FAIL mainGenuine", "ABORT fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}
	@Test void failFail()
	{
		final var service = service("FAIL mainGenuine", "FAIL fallGenuine");

		assertStackTrace(0, assertFails(
				() -> service.probeBucketTag("my-Bucket"),
				IllegalStateException.class,
				"FAIL mainGenuine(my-Bucket)"));
	}

	private static VaultFallbackService service(final String main, final String... fallbacks)
	{
		final List<Source> sources = new ArrayList<>(List.of(
				single("service", VaultFallbackService.class),
				single("service.main", VaultMockService.class),
				single("service.main.bucketTagAction", main)
		));
		sources.add(single("service.fallbacks.count", fallbacks.length));
		for (int i=0; i<fallbacks.length; i++)
		{
			final String key = "service.fallbacks." + i;
			sources.add(single(key, VaultMockService.class));
			sources.add(single(key + ".bucketTagAction", fallbacks[i]));
		}
		final Source source = cascade(sources.toArray(Source[]::new));
		return (VaultFallbackService)BucketProperties.factory("myKey").create(source).newServiceNonResilient(() -> false);
	}
	private static void assertStackTrace(final int expected, final Exception actual)
	{
		assertEquals(expected, stream(actual.getStackTrace()).
				map(StackTraceElement::getMethodName).
				filter("FALLBACK"::equals).count());
	}
}

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

import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class VaultFallbackServiceNestedTest
{
	private VaultFallbackService service;
	private VaultFallbackService serviceNested;
	private VaultMockService main;
	private VaultMockService falN;
	private VaultMockService fall;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultFallbackService.class),
						single("service.main", VaultFallbackService.class),
						single("service.main.main", VaultMockService.class),
						single("service.main.main.example", "mainEx"),
						single("service.main.reference", VaultMockService.class),
						single("service.main.reference.example", "ref1Ex"),
						single("service.reference", VaultMockService.class),
						single("service.reference.example", "ref2Ex")
				));
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		final VaultFallbackService service = (VaultFallbackService)props.newServiceNonResilient(() -> false);
		this.service = service;
		serviceNested = (VaultFallbackService)service.getMainService();
		fall = (VaultMockService)service.getFallbackServices().get(0);
		main = (VaultMockService)serviceNested.getMainService();
		falN = (VaultMockService)serviceNested.getFallbackServices().get(0);
	}


	@Test void testWritable()
	{
		assertEquals(true,  main.writable);
		assertEquals(false, falN.writable);
		assertEquals(false, fall.writable);
	}

	@Test void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("ref1Ex", falN.serviceProperties.example);
		assertEquals("ref2Ex", fall.serviceProperties.example);
	}

	@Test void testToString()
	{
		assertEquals(
				"VaultMockService:mainEx (fallback VaultMockService:ref1Ex) (fallback VaultMockService:ref2Ex)",
				service.toString());
		assertEquals(
				"VaultMockService:mainEx (fallback VaultMockService:ref1Ex)",
				serviceNested.toString());
	}

	@Test void testPurge()
	{
		main.assertIt("");
		falN.assertIt("");
		fall.assertIt("");

		service.purgeSchema(new AssertionErrorJobContext());
		main.assertIt("purgeSchema");
		falN.assertIt("purgeSchema");
		fall.assertIt("purgeSchema");
	}

	@Test void testClose()
	{
		main.assertIt("");
		falN.assertIt("");
		fall.assertIt("");

		service.close();
		main.assertIt("close");
		falN.assertIt("close");
		fall.assertIt("close");
	}

	private final LogRule log = new LogRule(VaultFallbackService.class);

	@Test void testLog()
	{
		log.assertError(
				"do not nest another VaultFallbackService in main, " +
				"use multiple fallback services instead");
		log.assertEmpty();
	}
}

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

import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultFallbackServiceMultipleRefsTest
{
	private VaultFallbackService service;
	private VaultMockService main;
	private VaultMockService fal0;
	private VaultMockService fal1;
	private VaultMockService fal2;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultFallbackService.class),
						single("service.main", VaultMockService.class),
						single("service.main.example", "mainEx"),
						single("service.fallbacks.count", "3"),
						single("service.fallbacks.0", VaultMockService.class),
						single("service.fallbacks.0.example", "refrEx0"),
						single("service.fallbacks.1", VaultMockService.class),
						single("service.fallbacks.1.example", "refrEx1"),
						single("service.fallbacks.2", VaultMockService.class),
						single("service.fallbacks.2.example", "refrEx2")
				));
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		final VaultFallbackService service = (VaultFallbackService)props.newServiceNonResilient(() -> false);
		this.service = service;
		main = (VaultMockService)service.getMainService();
		fal0 = (VaultMockService)service.getFallbackServices().get(0);
		fal1 = (VaultMockService)service.getFallbackServices().get(1);
		fal2 = (VaultMockService)service.getFallbackServices().get(2);
		assertEquals(3, service.getFallbackServices().size());
	}

	@Test void testModifyReferenceServices()
	{
		assertUnmodifiable(service.getFallbackServices());
	}

	@Test void testWritable()
	{
		assertEquals(true,  main.writable);
		assertEquals(false, fal0.writable);
		assertEquals(false, fal1.writable);
		assertEquals(false, fal2.writable);
	}

	@Test void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("refrEx0", fal0.serviceProperties.example);
		assertEquals("refrEx1", fal1.serviceProperties.example);
		assertEquals("refrEx2", fal2.serviceProperties.example);
	}

	@Test void testToString()
	{
		assertEquals(
				"VaultMockService:mainEx (fallbacks VaultMockService:refrEx0 VaultMockService:refrEx1 VaultMockService:refrEx2)",
				service.toString());
	}

	@Test void testPurge()
	{
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt("");
		fal2.assertIt("");

		service.purgeSchema(new AssertionErrorJobContext());
		main.assertIt("purgeSchema");
		fal0.assertIt("purgeSchema");
		fal1.assertIt("purgeSchema");
		fal2.assertIt("purgeSchema");
	}

	@Test void testClose()
	{
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt("");
		fal2.assertIt("");

		service.close();
		main.assertIt("close");
		fal0.assertIt("close");
		fal1.assertIt("close");
		fal2.assertIt("close");
	}
}

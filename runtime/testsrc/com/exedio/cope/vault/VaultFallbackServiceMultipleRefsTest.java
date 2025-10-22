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
	private VaultMockService refr0;
	private VaultMockService refr1;
	private VaultMockService refr2;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultFallbackService.class),
						single("service.main", VaultMockService.class),
						single("service.main.example", "mainEx"),
						single("service.referenceCount", "3"),
						single("service.reference", VaultMockService.class),
						single("service.reference.example", "refrEx0"),
						single("service.reference1", VaultMockService.class),
						single("service.reference1.example", "refrEx1"),
						single("service.reference2", VaultMockService.class),
						single("service.reference2.example", "refrEx2")
				));
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		final VaultFallbackService service = (VaultFallbackService)props.newServiceNonResilient(() -> false);
		this.service = service;
		main = (VaultMockService)service.getMainService();
		refr0 = (VaultMockService)service.getFallbackServices().get(0);
		refr1 = (VaultMockService)service.getFallbackServices().get(1);
		refr2 = (VaultMockService)service.getFallbackServices().get(2);
		assertEquals(3, service.getFallbackServices().size());
	}

	@Test void testModifyReferenceServices()
	{
		assertUnmodifiable(service.getFallbackServices());
	}

	@Deprecated // OK test deprecated api
	@Test void getReferenceService()
	{
		assertFails(
				service::getReferenceService,
				IllegalStateException.class,
				"there are 3 reference services - use getReferenceServices()"
		);
	}

	@Test void testWritable()
	{
		assertEquals(true,  main.writable);
		assertEquals(false, refr0.writable);
		assertEquals(false, refr1.writable);
		assertEquals(false, refr2.writable);
	}

	@Test void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("refrEx0", refr0.serviceProperties.example);
		assertEquals("refrEx1", refr1.serviceProperties.example);
		assertEquals("refrEx2", refr2.serviceProperties.example);
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
		refr0.assertIt("");
		refr1.assertIt("");
		refr2.assertIt("");

		service.purgeSchema(new AssertionErrorJobContext());
		main.assertIt("purgeSchema");
		refr0.assertIt("purgeSchema");
		refr1.assertIt("purgeSchema");
		refr2.assertIt("purgeSchema");
	}

	@Test void testClose()
	{
		main.assertIt("");
		refr0.assertIt("");
		refr1.assertIt("");
		refr2.assertIt("");

		service.close();
		main.assertIt("close");
		refr0.assertIt("close");
		refr1.assertIt("close");
		refr2.assertIt("close");
	}
}

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

import com.exedio.cope.util.AssertionErrorJobContext;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultReferenceServiceTest
{
	private VaultReferenceService service;
	private VaultMockService main;
	private VaultMockService refr;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", VaultMockService.class),
						single("service.main.example", "mainEx"),
						single("service.reference", VaultMockService.class),
						single("service.reference.example", "refrEx")
				));
		final VaultProperties props = VaultProperties.factory().create(source);
		@SuppressWarnings("deprecation") final VaultReferenceService
		service = (VaultReferenceService)props.newService();
		this.service = service;
		main = (VaultMockService)service.getMainService();
		refr = (VaultMockService)service.getReferenceService();
	}


	@Test void testWritable()
	{
		assertEquals(true,  main.writable);
		assertEquals(false, refr.writable);
	}

	@Test void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("refrEx", refr.serviceProperties.example);
	}

	@Test void testToString()
	{
		assertEquals(
				"VaultMockService:mainEx (reference VaultMockService:refrEx)",
				service.toString());
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testPurge()
	{
		main.assertIt("");
		refr.assertIt("");

		service.purgeSchema(new AssertionErrorJobContext());
		main.assertIt("purgeSchema\n");
		refr.assertIt("purgeSchema\n");
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testClose()
	{
		main.assertIt("");
		refr.assertIt("");

		service.close();
		main.assertIt("close\n");
		refr.assertIt("close\n");
	}
}

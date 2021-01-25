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

public class VaultReferenceServiceNestedTest
{
	private VaultReferenceService service;
	private VaultReferenceService serviceNested;
	private VaultMockService main;
	private VaultMockService ref1;
	private VaultMockService ref2;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", VaultReferenceService.class),
						single("service.main.main", VaultMockService.class),
						single("service.main.main.example", "mainEx"),
						single("service.main.reference", VaultMockService.class),
						single("service.main.reference.example", "ref1Ex"),
						single("service.reference", VaultMockService.class),
						single("service.reference.example", "ref2Ex")
				));
		final VaultProperties props = VaultProperties.factory().create(source);
		@SuppressWarnings("deprecation") final VaultReferenceService
		service = (VaultReferenceService)props.newService();
		this.service = service;
		serviceNested = (VaultReferenceService)service.getMainService();
		ref2 = (VaultMockService)service.getReferenceService();
		main = (VaultMockService)serviceNested.getMainService();
		ref1 = (VaultMockService)serviceNested.getReferenceService();
	}


	@Test void testWritable()
	{
		assertEquals(true,  main.writable);
		assertEquals(false, ref1.writable);
		assertEquals(false, ref2.writable);
	}

	@Test void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("ref1Ex", ref1.serviceProperties.example);
		assertEquals("ref2Ex", ref2.serviceProperties.example);
	}

	@Test void testToString()
	{
		assertEquals(
				"VaultMockService:mainEx (reference VaultMockService:ref1Ex) (reference VaultMockService:ref2Ex)",
				service.toString());
		assertEquals(
				"VaultMockService:mainEx (reference VaultMockService:ref1Ex)",
				serviceNested.toString());
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testPurge()
	{
		main.assertIt("");
		ref1.assertIt("");
		ref2.assertIt("");

		service.purgeSchema(new AssertionErrorJobContext());
		main.assertIt("purgeSchema\n");
		ref1.assertIt("purgeSchema\n");
		ref2.assertIt("purgeSchema\n");
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testClose()
	{
		main.assertIt("");
		ref1.assertIt("");
		ref2.assertIt("");

		service.close();
		main.assertIt("close\n");
		ref1.assertIt("close\n");
		ref2.assertIt("close\n");
	}
}

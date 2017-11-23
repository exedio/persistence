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
import static org.junit.Assert.assertEquals;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Before;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({"UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", "BC_UNCONFIRMED_CAST_OF_RETURN_VALUE"})
public class VaultReferenceServiceTest
{
	private VaultReferenceService service;
	private VaultMockService main;
	private VaultMockService refr;

	@Before public void before()
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
		service = (VaultReferenceService)props.newService();
		main = (VaultMockService)service.getMainService();
		refr = (VaultMockService)service.getReferenceService();
	}


	@Test public void testGetters()
	{
		assertEquals("mainEx", main.serviceProperties.example);
		assertEquals("refrEx", refr.serviceProperties.example);
	}

	@Test public void testToString()
	{
		assertEquals(
				"VaultMockService:mainEx (reference VaultMockService:refrEx)",
				service.toString());
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test public void testClose()
	{
		main.assertIt("");
		refr.assertIt("");

		service.close();
		main.assertIt("close\n");
		refr.assertIt("close\n");
	}
}

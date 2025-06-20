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

import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Properties.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultReferenceServiceContainsTest
{
	private VaultReferenceService service;
	private Service main;
	private Service refr;

	@BeforeEach void before()
	{
		final Source source = cascade(
				single("service", VaultReferenceService.class),
				single("service.main", Service.class),
				single("service.reference", Service.class)
		);
		final BucketProperties props = BucketProperties.factory("myKey").create(source);
		final VaultReferenceService service = (VaultReferenceService)props.newServiceNonResilient(() -> false);
		this.service = service;
		main = (Service)service.getMainService();
		refr = (Service)service.getReferenceServices().get(0);
	}

	@Test void testContainsNone() throws VaultServiceUnsupportedOperationException
	{
		assertEquals(false, service.contains(HASH));
		assertEquals(1, main.count);
		assertEquals(1, refr.count);
	}

	@Test void testContainsMain() throws VaultServiceUnsupportedOperationException
	{
		main.result = true;
		assertEquals(true, service.contains(HASH));
		assertEquals(1, main.count);
		assertEquals(0, refr.count);
	}

	@Test void testContainsRef() throws VaultServiceUnsupportedOperationException
	{
		refr.result = true;
		assertEquals(true, service.contains(HASH));
		assertEquals(1, main.count);
		assertEquals(1, refr.count);
	}

	@Test void testContainsBoth() throws VaultServiceUnsupportedOperationException
	{
		main.result = true;
		refr.result = true;
		assertEquals(true, service.contains(HASH));
		assertEquals(1, main.count);
		assertEquals(0, refr.count);
	}

	private static final String HASH = "abcdef";

	private static final class Service extends AssertionErrorVaultService
	{
		boolean result = false;
		int count = 0;

		Service(@SuppressWarnings("unused") final VaultServiceParameters parameters) { }

		@Override
		public boolean contains(final String hash)
		{
			assertEquals(HASH, hash);
			count++;
			return result;
		}
	}
}

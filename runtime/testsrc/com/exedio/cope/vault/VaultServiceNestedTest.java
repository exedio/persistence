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
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultServiceNestedTest
{
	private MyService service;
	private MyService writ;
	private MyService read;
	private VaultMockService writWrit;
	private VaultMockService writRead;
	private VaultMockService readWrit;
	private VaultMockService readRead;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", MyService.class),
						single("default.service.writ", MyService.class),
						single("default.service.writ.writ", VaultMockService.class),
						single("default.service.writ.writ.example", "writWritEx"),
						single("default.service.writ.read", VaultMockService.class),
						single("default.service.writ.read.example", "writReadEx"),
						single("default.service.read", MyService.class),
						single("default.service.read.writ", VaultMockService.class),
						single("default.service.read.writ.example", "readWritEx"),
						single("default.service.read.read", VaultMockService.class),
						single("default.service.read.read.example", "readReadEx")
				));
		final VaultProperties props = VaultProperties.factory().create(source);
		final MyService service = (MyService)props.newServicesNonResilient(DEFAULT).get(DEFAULT);
		this.service = service;
		writ = (MyService)service.writ;
		read = (MyService)service.read;
		writWrit = (VaultMockService)writ.writ;
		writRead = (VaultMockService)writ.read;
		readWrit = (VaultMockService)read.writ;
		readRead = (VaultMockService)read.read;
	}


	@Test void testWritable()
	{
		assertEquals(true,  writWrit.writable);
		assertEquals(false, writRead.writable);
		assertEquals(false, readWrit.writable);
		assertEquals(false, readRead.writable);
	}

	@Test void testGetters()
	{
		assertEquals("writWritEx", writWrit.serviceProperties.example);
		assertEquals("writReadEx", writRead.serviceProperties.example);
		assertEquals("readWritEx", readWrit.serviceProperties.example);
		assertEquals("readReadEx", readRead.serviceProperties.example);
	}

	@Test void testToString()
	{
		assertEquals(
				"(" +
				"(VaultMockService:writWritEx-VaultMockService:writReadEx)-" +
				"(VaultMockService:readWritEx-VaultMockService:readReadEx))",
				service.toString());
		assertEquals(
				"(VaultMockService:writWritEx-VaultMockService:writReadEx)",
				writ.toString());
		assertEquals(
				"(VaultMockService:readWritEx-VaultMockService:readReadEx)",
				read.toString());
	}

	@ServiceProperties(MyService.Props.class)
	private static final class MyService extends AssertionErrorVaultService
	{
		private final VaultService writ, read;

		MyService(final VaultServiceParameters parameters, final Props properties)
		{
			writ = properties.writ.newService(parameters);
			read = properties.read.newService(parameters);
		}

		@Override
		public String toString()
		{
			return "(" + writ + "-" + read + ")";
		}

		static final class Props extends AbstractVaultProperties
		{
			private final Service writ = valueService("writ", true);
			private final Service read = valueService("read", false);

			Props(final Source source)
			{
				super(source);
			}
		}
	}
}

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
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.vault.VaultNotFoundException.anonymiseHash;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.transientvault.VaultTransientService;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VaultReferenceServiceCopyFailTest
{
	private VaultReferenceService service;
	private VaultMockService main;
	private String hash;

	@BeforeEach void before()
	{
		final Source source =
				describe("DESC", cascade(
						single("algorithm", "MD5"),
						single("default.service", VaultReferenceService.class),
						single("default.service.main", PutFailService.class),
						single("default.service.main.example", "mainEx"),
						single("default.service.reference", VaultTransientService.class),
						single("default.service.reference.example", "refrEx")
				));
		final VaultProperties props = VaultProperties.factory().create(source);
		final VaultReferenceService service = (VaultReferenceService)props.newServicesNonResilient(DEFAULT).get(DEFAULT);
		this.service = service;
		main = (VaultMockService)service.getMainService();
		final byte[] value = {1,2,3,4,5};
		hash = Hex.encodeLower(props.bucket("default").getAlgorithmFactory().digest(value));
		service.getReferenceService().put(hash, value);
		main.assertIt("");
	}

	private static final class PutFailService extends VaultMockService
	{
		private PutFailService(final VaultServiceParameters pa, final Props po) { super(pa, po); }

		@Override
		public boolean put(final String hash, final byte[] value)
		{
			throw new IllegalArgumentException("deliberately fail in put(byte[])");
		}

		@Override
		public boolean put(final String hash, final Path value)
		{
			throw new IllegalArgumentException("deliberately fail in put(Path)");
		}
	}

	@Test void testBytes()
	{
		final IllegalArgumentException e = assertFails(
				() -> service.get(hash),
				IllegalArgumentException.class,
				"deliberately fail in put(byte[])");

		main.assertIt("getBytes");
		assertNull(e.getCause());
		assertEquals(
				"[com.exedio.cope.vault.VaultNotFoundException: hash not found in vault: " + anonymiseHash(hash) + "]",
				List.of(e.getSuppressed()).toString());
	}

	@Test void testStream()
	{
		final ByteArrayOutputStream sink = new ByteArrayOutputStream();
		final IllegalArgumentException e = assertFails(
				() -> service.get(hash, sink),
				IllegalArgumentException.class,
				"deliberately fail in put(Path)");

		main.assertIt("getStream");
		assertArrayEquals(new byte[0], sink.toByteArray());
		assertNull(e.getCause());
		assertEquals(
				"[com.exedio.cope.vault.VaultNotFoundException: hash not found in vault: " + anonymiseHash(hash) + "]",
				List.of(e.getSuppressed()).toString());
	}
}

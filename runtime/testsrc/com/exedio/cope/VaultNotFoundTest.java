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

package com.exedio.cope;

import static com.exedio.cope.VaultTest.HASH1;
import static com.exedio.cope.VaultTest.HASH1A;
import static com.exedio.cope.VaultTest.MODEL;
import static com.exedio.cope.VaultTest.VALUE1;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class VaultNotFoundTest
{
	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("SHA-512", service.vaultProperties.getAlgorithm());
		assertEquals("mainExampleValue", service.serviceProperties.example);
		assertEquals(true, service.writable);
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");

		service.clear();
		service.assertIt("");

		try
		{
			item.getFieldLength();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue, " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		service.assertIt("getLength\n");
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");

		service.clear();
		service.assertIt("");

		try
		{
			item.getFieldBytes();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue, " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		service.assertIt("getBytes\n");
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		service.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");

		service.clear();
		service.assertIt("");

		try
		{
			item.getFieldStream();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue, " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		service.assertIt("getStream\n");
	}


	private VaultMockService service;

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultMockService.class),
				single("dataField.vault.service.example", "mainExampleValue"),
				single("dataField.vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		service = (VaultMockService)MODEL.connect().vault;
		MODEL.tearDownSchema();
		MODEL.createSchema();
		MODEL.startTransaction("VaultTest");
	}

	@AfterEach void tearDown()
	{
		MODEL.rollback();
		MODEL.disconnect();
	}
}

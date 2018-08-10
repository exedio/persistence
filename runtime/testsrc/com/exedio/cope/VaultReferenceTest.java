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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultReferenceService;
import com.exedio.cope.vaultmock.VaultMockService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("HardcodedLineSeparator")
@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class VaultReferenceTest
{
	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("VaultMockService:mainExampleValue (reference VaultMockService:referenceExampleValue)", service.toString());

		assertNotNull(main);
		assertEquals("SHA-512", main.vaultProperties.getAlgorithm());
		assertEquals("mainExampleValue", main.serviceProperties.example);
		assertEquals(true, main.writable);
		assertEquals("VaultMockService:mainExampleValue", main.toString());

		assertNotNull(refr);
		assertEquals("SHA-512", refr.vaultProperties.getAlgorithm());
		assertEquals("referenceExampleValue", refr.serviceProperties.example);
		assertEquals(false, refr.writable);
		assertEquals("VaultMockService:referenceExampleValue", refr.toString());

		assertSame(main.vaultProperties, refr.vaultProperties);
		assertNotSame(main.serviceProperties, refr.serviceProperties);
	}

	@Test void mainGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1.length(), item.getFieldLength());
		main.assertIt(HASH1, VALUE1, "getLength\n");
		refr.assertIt(HASH1, VALUE1, "");
	}

	@Test void mainGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldBytes());
		main.assertIt(HASH1, VALUE1, "getBytes\n");
		refr.assertIt(HASH1, VALUE1, "");
	}

	@Test void mainGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldStream());
		main.assertIt(HASH1, VALUE1, "getStream\n");
		refr.assertIt(HASH1, VALUE1, "");
	}

	@Test void referenceGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1.length(), item.getFieldLength());
		main.assertIt(HASH1, VALUE1, "getLength\nputFile com.exedio.cope.vault.VaultReferenceService\n");
		refr.assertIt(HASH1, VALUE1, "getStream\n");
	}

	@Test void referenceGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldBytes());
		main.assertIt(HASH1, VALUE1, "getBytes\nputBytes com.exedio.cope.vault.VaultReferenceService\n");
		refr.assertIt(HASH1, VALUE1, "getBytes\n");
	}

	@Test void referenceGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldStream());
		main.assertIt(HASH1, VALUE1, "getStream\nputFile com.exedio.cope.vault.VaultReferenceService\n");
		refr.assertIt(HASH1, VALUE1, "getStream\n");
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");

		try
		{
			item.getFieldLength();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue (reference VaultMockService:referenceExampleValue), " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		main.assertIt("getLength\n");
		refr.assertIt("getStream\n");
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");

		try
		{
			item.getFieldBytes();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue (reference VaultMockService:referenceExampleValue), " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		main.assertIt("getBytes\n");
		refr.assertIt("getBytes\n");
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	@Test void notFoundGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes VaultItem.field " + item + "\n");
		refr.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");

		try
		{
			item.getFieldStream();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"vault data missing on " + item + " for VaultItem.field, " +
					"service: VaultMockService:mainExampleValue (reference VaultMockService:referenceExampleValue), " +
					"hash(SHA-512): " + HASH1A,
					e.getMessage());
			final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
			assertEquals(HASH1, cause.getHashComplete());
			assertEquals(HASH1A, cause.getHashAnonymous());
			assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		}
		main.assertIt("getStream\n");
		refr.assertIt("getStream\n");
	}


	private VaultReferenceService service;
	private VaultMockService main, refr;

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultReferenceService.class),
				single("dataField.vault.service.main", VaultMockService.class),
				single("dataField.vault.service.main.example", "mainExampleValue"),
				single("dataField.vault.service.reference", VaultMockService.class),
				single("dataField.vault.service.reference.example", "referenceExampleValue"),
				single("dataField.vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		this.service = (VaultReferenceService)MODEL.connect().vault;
		main = (VaultMockService)service.getMainService();
		refr = (VaultMockService)service.getReferenceService();
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

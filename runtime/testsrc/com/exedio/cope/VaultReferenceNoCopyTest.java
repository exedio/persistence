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
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultReferenceService;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see VaultReferenceTest
 */
@MainRule.Tag
public class VaultReferenceNoCopyTest
{
	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("VaultMockService:mainExampleValue (reference VaultMockService:referenceExampleValue)", service.toString());

		assertNotNull(main);
		assertEquals("SHA-512", main.bucketProperties.getAlgorithm());
		assertEquals("mainExampleValue", main.serviceProperties.example);
		assertEquals("default", main.bucket);
		assertEquals(true, main.writable);
		assertEquals("VaultMockService:mainExampleValue", main.toString());

		assertNotNull(refr);
		assertEquals("SHA-512", refr.bucketProperties.getAlgorithm());
		assertEquals("referenceExampleValue", refr.serviceProperties.example);
		assertEquals("default", refr.bucket);
		assertEquals(false, refr.writable);
		assertEquals("VaultMockService:referenceExampleValue", refr.toString());

		assertSame(main.bucketProperties, refr.bucketProperties);
		assertNotSame(main.serviceProperties, refr.serviceProperties);

		log.assertEmpty();
	}

	@Test void mainGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1.length(), item.getFieldLength());
		main.assertIt(HASH1, VALUE1, ""); // DataField#getLength no longer calls VaultService#getLength
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
	}

	@Test void mainGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldBytes());
		main.assertIt(HASH1, VALUE1, "getBytes");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
	}

	@Test void mainGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldStream());
		main.assertIt(HASH1, VALUE1, "getStream");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
	}

	@Test void referenceGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
		assertEquals(VALUE1.length(), item.getFieldLength());
		log.assertEmpty(); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(               ""); // DataField#getLength no longer calls VaultService#getLength
		refr.assertIt(HASH1, VALUE1, ""); // DataField#getLength no longer calls VaultService#getLength

		log.assertEmpty();
	}

	@Test void referenceGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldBytes());
		log.assertDebug("get from reference 0 in default: " + HASH1A);
		main.assertIt(               "getBytes");
		refr.assertIt(HASH1, VALUE1, "getBytes");

		log.assertEmpty();
	}

	@Test void referenceGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldStream());
		log.assertDebug("get from reference 0 in default: " + HASH1A);
		main.assertIt(               "getStream");
		refr.assertIt(HASH1, VALUE1, "getStream");

		log.assertEmpty();
	}

	@Test void notFoundGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");

		assertEquals(6, item.getFieldLength()); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		refr.assertIt(""); // DataField#getLength no longer calls VaultService#getLength

		log.assertEmpty();
	}

	@Test void notFoundGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
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
		main.assertIt("getBytes");
		refr.assertIt("getBytes");

		log.assertEmpty();
	}

	@Test void notFoundGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
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
		main.assertIt("getStream");
		refr.assertIt("getStream");

		log.assertEmpty();
	}


	private final LogRule log = new LogRule(VaultReferenceService.class);
	private VaultReferenceService service;
	private VaultMockService main, refr;

	@BeforeEach void setUp()
	{
		log.setLevelDebug();
		MODEL.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.default.service", VaultReferenceService.class),
				single("vault.default.service.main", VaultMockService.class),
				single("vault.default.service.main.example", "mainExampleValue"),
				single("vault.default.service.reference", VaultMockService.class),
				single("vault.default.service.reference.example", "referenceExampleValue"),
				single("vault.default.service.copyReferenceToMain", false),
				single("vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		this.service = (VaultReferenceService)VaultTest.vaultService(MODEL);
		main = (VaultMockService)service.getMainService();
		refr = (VaultMockService)service.getReferenceServices().get(0);
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
	}

	@AfterEach void tearDown()
	{
		MODEL.rollback();
		MODEL.tearDownSchema();
		MODEL.disconnect();
	}
}

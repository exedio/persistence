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

import static com.exedio.cope.DataTest.assertAncestry;
import static com.exedio.cope.VaultItem.field;
import static com.exedio.cope.VaultTest.HASH1;
import static com.exedio.cope.VaultTest.HASH1A;
import static com.exedio.cope.VaultTest.MODEL;
import static com.exedio.cope.VaultTest.VALUE1;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vault.VaultReferenceService;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.IOException;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @see VaultReferenceNoCopyTest
 */
@MainRule.Tag
public class VaultReferenceTest
{
	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("VaultMockService:mainExampleValue (references VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue)", service.toString());

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

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		refr.assertIt("");

		refr.put(HASH1, VALUE1);
		refr.assertIt(HASH1, VALUE1, "");

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		refr.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldBytes());
		main.assertIt(HASH1, VALUE1, "getBytes");
		refr.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
	}

	@Test void errorInMain()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");

		main.clear();
		main.failOnGet(HASH1, new IllegalStateException("error in main"));
		final IllegalStateException exception = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"error in main"
		);
		assertEquals(0, exception.getSuppressed().length);
		main.assertIt("getBytes");
		refr.assertIt("");
	}

	@Test void errorInReference()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		refr.failOnGet(HASH1, new IllegalStateException("error in reference"));
		final IllegalStateException exception = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"error in reference"
		);
		checkSuppressed(
				exception,
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				}
		);
		main.assertIt("getBytes");
		refr.assertIt("getBytes");
		refr1.assertIt("");
	}

	@Test void errorInReference1()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		refr1.failOnGet(HASH1, new IllegalStateException("error in reference"));
		final IllegalStateException exception = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"error in reference"
		);
		checkSuppressed(
				exception,
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				},
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				}
		);
		main.assertIt("getBytes");
		refr.assertIt("getBytes");
		refr1.assertIt("getBytes");
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
		refr1.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");
		refr1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1.length(), item.getFieldLength());
		log.assertEmpty(); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		refr.assertIt(HASH1, VALUE1, ""); // DataField#getLength no longer calls VaultService#getLength
		refr1.assertIt("");

		log.assertEmpty();
	}

	@Test void referenceGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();

		assertAncestry(field, item, HASH1, "reference1", "myReference1Ancestry");
		main.assertIt("contains");
		refr.assertIt("contains");
		refr1.assertIt("addToAncestryPath");

		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");
		refr1.assertIt("");

		assertAncestry(field, item, HASH1, "reference", "myReferenceAncestry");
		main.assertIt("contains");
		refr.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		refr1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldBytes());
		log.assertDebug("get from reference 0 in default: " + HASH1A);
		main.assertIt(HASH1, VALUE1, "getBytes putBytes");
		refr.assertIt(HASH1, VALUE1, "getBytes");
		refr1.assertIt("");

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		refr.assertIt(HASH1, VALUE1, "");
		refr1.assertIt("");

		log.assertEmpty();
	}

	@Test void reference1GetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();

		assertAncestry(field, item, HASH1, "reference1", "myReference1Ancestry");
		main.assertIt("contains");
		refr.assertIt("contains");
		refr1.assertIt("addToAncestryPath");

		refr1.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt("");
		refr1.assertIt(HASH1, VALUE1, "");

		assertAncestry(field, item, HASH1, "reference1", "myReference1Ancestry");
		main.assertIt("contains");
		refr.assertIt("contains");
		refr1.assertIt(HASH1, VALUE1, "addToAncestryPath");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldBytes());
		log.assertDebug("get from reference 1 in default: " + HASH1A);
		main.assertIt(HASH1, VALUE1, "getBytes putBytes");
		refr.assertIt("getBytes");
		refr1.assertIt(HASH1, VALUE1, "getBytes");

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		refr.assertIt("");
		refr1.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
	}

	@Test void referenceGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		refr.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt(HASH1, VALUE1, "");
		refr1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldStream());
		log.assertDebug("get from reference 0 in default: " + HASH1A);
		main.assertIt(HASH1, VALUE1, "getStream putFile");
		refr.assertIt(HASH1, VALUE1, "getStream");
		refr1.assertIt("");

		log.assertEmpty();
	}

	@Test void reference1GetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		refr1.put(HASH1, VALUE1);
		main.assertIt("");
		refr.assertIt("");
		refr1.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldStream());
		log.assertDebug("get from reference 1 in default: " + HASH1A);
		main.assertIt(HASH1, VALUE1, "getStream putFile");
		refr.assertIt("getStream");
		refr1.assertIt(HASH1, VALUE1, "getStream");

		log.assertEmpty();
	}

	@Test void notFoundGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");
		refr1.assertIt("");

		assertEquals(6, item.getFieldLength()); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		refr.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		refr1.assertIt(""); // DataField#getLength no longer calls VaultService#getLength

		log.assertEmpty();
	}

	@Test void notFoundGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");
		refr1.assertIt("");

		final IllegalStateException e = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"vault data missing on " + item + " for VaultItem.field, " +
				"service: VaultMockService:mainExampleValue (references VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue), " +
				"hash(SHA-512): " + HASH1A);
		final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
		assertEquals(HASH1, cause.getHashComplete());
		assertEquals(HASH1A, cause.getHashAnonymous());
		assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		checkSuppressed(
				cause,
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				},
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				}
		);

		main.assertIt("getBytes");
		refr.assertIt("getBytes");
		refr1.assertIt("getBytes");

		log.assertEmpty();
	}

	@Test void notFoundGetStream()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		refr.assertIt("");
		refr1.assertIt("");

		main.clear();
		main.assertIt("");
		refr.assertIt("");
		refr1.assertIt("");

		final IllegalStateException e = assertFails(
				item::getFieldStream,
				IllegalStateException.class,
				"vault data missing on " + item + " for VaultItem.field, " +
				"service: VaultMockService:mainExampleValue (references VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue), " +
				"hash(SHA-512): " + HASH1A);
		final VaultNotFoundException cause = (VaultNotFoundException)e.getCause();
		assertEquals(HASH1, cause.getHashComplete());
		assertEquals(HASH1A, cause.getHashAnonymous());
		assertEquals("hash not found in vault: " + HASH1A, cause.getMessage());
		checkSuppressed(
				cause,
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				},
				suppressed ->
				{
					assertEquals(HASH1, suppressed.getHashComplete());
					assertEquals(HASH1A, suppressed.getHashAnonymous());
					assertEquals("hash not found in vault: " + HASH1A, suppressed.getMessage());
				}
		);

		main.assertIt("getStream");
		refr.assertIt("getStream");
		refr1.assertIt("getStream");

		log.assertEmpty();
	}


	private final LogRule log = new LogRule(VaultReferenceService.class);
	private VaultReferenceService service;
	private VaultMockService main, refr, refr1;

	@BeforeEach void setUp()
	{
		log.setLevelDebug();
		MODEL.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.default.service", VaultReferenceService.class),
				single("vault.default.service.main", VaultMockService.class),
				single("vault.default.service.main.example", "mainExampleValue"),
				single("vault.default.service.referenceCount", 2),
				single("vault.default.service.reference", VaultMockService.class),
				single("vault.default.service.reference.example", "referenceExampleValue"),
				single("vault.default.service.reference1", VaultMockService.class),
				single("vault.default.service.reference1.example", "reference1ExampleValue"),
				single("vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		this.service = (VaultReferenceService)VaultTest.vaultService(MODEL);
		main = (VaultMockService)service.getMainService();
		refr = (VaultMockService)service.getReferenceServices().get(0);
		refr1 = (VaultMockService)service.getReferenceServices().get(1);
		assertEquals(2, service.getReferenceServices().size());
		main.ancestryPath = "myMainAncestry";
		refr.ancestryPath = "myReferenceAncestry";
		refr1.ancestryPath = "myReference1Ancestry";
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction("VaultTest");
	}

	@AfterEach void tearDown()
	{
		MODEL.rollback();
		MODEL.tearDownSchema();
		MODEL.disconnect();
	}

	@SafeVarargs
	static void checkSuppressed(final Exception cause, final Consumer<VaultNotFoundException>... check)
	{
		final Throwable[] suppressedAll = cause.getSuppressed();
		assertEquals(check.length, suppressedAll.length);
		for (int i=0; i<check.length; i++)
		{
			check[i].accept((VaultNotFoundException) suppressedAll[i]);
		}
	}
}

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
import com.exedio.cope.vault.VaultFallbackService;
import com.exedio.cope.vault.VaultNotFoundException;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.IOException;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public abstract class VaultFallbackTest
{
	protected boolean copyFallbackToMain()
	{
		return true;
	}

	@Test void connect()
	{
		assertNotNull(service);
		assertEquals("VaultMockService:mainExampleValue (fallbacks VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue)", service.toString());

		assertNotNull(main);
		assertEquals("SHA-512", main.bucketProperties.getAlgorithm());
		assertEquals("mainExampleValue", main.serviceProperties.example);
		assertEquals("default", main.bucket);
		assertEquals(true, main.writable);
		assertEquals("VaultMockService:mainExampleValue", main.toString());

		assertNotNull(fal0);
		assertEquals("SHA-512", fal0.bucketProperties.getAlgorithm());
		assertEquals("referenceExampleValue", fal0.serviceProperties.example);
		assertEquals("default", fal0.bucket);
		assertEquals(false, fal0.writable);
		assertEquals("VaultMockService:referenceExampleValue", fal0.toString());

		assertSame(main.bucketProperties, fal0.bucketProperties);
		assertNotSame(main.serviceProperties, fal0.serviceProperties);

		log.assertEmpty();
	}

	@Test void mainGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		fal0.put(HASH1, VALUE1);
		fal0.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1.length(), item.getFieldLength());
		main.assertIt(HASH1, VALUE1, ""); // DataField#getLength no longer calls VaultService#getLength
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		log.assertEmpty();
	}

	@Test void mainGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		fal0.assertIt("");
		fal1.assertIt("");

		fal0.put(HASH1, VALUE1);
		fal0.assertIt(HASH1, VALUE1, "");

		assertAncestry(field, item, HASH1, "main", "myMainAncestry");
		main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		assertEquals(VALUE1, item.getFieldBytes());
		main.assertIt(HASH1, VALUE1, "getBytes");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		log.assertEmpty();
	}

	@Test void errorInMain()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		main.failOnGet(HASH1, new IllegalStateException("error in main"));
		final IllegalStateException exception = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"error in main"
		);
		assertEquals(0, exception.getSuppressed().length);
		main.assertIt("getBytes");
		fal0.assertIt("");
		fal1.assertIt("");
	}

	@Test void errorInReference()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		fal0.failOnGet(HASH1, new IllegalStateException("error in reference"));
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
		fal0.assertIt("getBytes");
		fal1.assertIt("");
	}

	@Test void errorInReference1()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		fal1.failOnGet(HASH1, new IllegalStateException("error in reference"));
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
		fal0.assertIt("getBytes");
		fal1.assertIt("getBytes");
	}

	@Test void mainGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		fal0.put(HASH1, VALUE1);
		fal0.assertIt(HASH1, VALUE1, "");

		assertEquals(VALUE1, item.getFieldStream());
		main.assertIt(HASH1, VALUE1, "getStream");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		log.assertEmpty();
	}

	@Test void referenceGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		fal0.put(HASH1, VALUE1);
		main.assertIt("");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1.length(), item.getFieldLength());
		log.assertEmpty(); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		fal0.assertIt(HASH1, VALUE1, ""); // DataField#getLength no longer calls VaultService#getLength
		fal1.assertIt("");

		log.assertEmpty();
	}

	@Test void referenceGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();

		assertAncestry(field, item, HASH1, "fallback1", "myReference1Ancestry");
		main.assertIt("contains");
		fal0.assertIt("contains");
		fal1.assertIt("addToAncestryPath");

		fal0.put(HASH1, VALUE1);
		main.assertIt("");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		assertAncestry(field, item, HASH1, "fallback0", "myReferenceAncestry");
		main.assertIt("contains");
		fal0.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
		fal1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldBytes());
		log.assertDebug("get from fallback 0 in default: " + HASH1A);
		if(copyFallbackToMain())
			main.assertIt(HASH1, VALUE1, "getBytes putBytes");
		else
			main.assertIt("getBytes");
		fal0.assertIt(HASH1, VALUE1, "getBytes");
		fal1.assertIt("");

		if(copyFallbackToMain())
		{
			assertAncestry(field, item, HASH1, "main", "myMainAncestry");
			main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
			fal0.assertIt(HASH1, VALUE1, "");
			fal1.assertIt("");
		}

		log.assertEmpty();
	}

	@Test void reference1GetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();

		assertAncestry(field, item, HASH1, "fallback1", "myReference1Ancestry");
		main.assertIt("contains");
		fal0.assertIt("contains");
		fal1.assertIt("addToAncestryPath");

		fal1.put(HASH1, VALUE1);
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt(HASH1, VALUE1, "");

		assertAncestry(field, item, HASH1, "fallback1", "myReference1Ancestry");
		main.assertIt("contains");
		fal0.assertIt("contains");
		fal1.assertIt(HASH1, VALUE1, "addToAncestryPath");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldBytes());
		log.assertDebug("get from fallback 1 in default: " + HASH1A);
		if(copyFallbackToMain())
			main.assertIt(HASH1, VALUE1, "getBytes putBytes");
		else
			main.assertIt("getBytes");
		fal0.assertIt("getBytes");
		fal1.assertIt(HASH1, VALUE1, "getBytes");

		if(copyFallbackToMain())
		{
			assertAncestry(field, item, HASH1, "main", "myMainAncestry");
			main.assertIt(HASH1, VALUE1, "contains addToAncestryPath");
			fal0.assertIt("");
			fal1.assertIt(HASH1, VALUE1, "");
		}

		log.assertEmpty();
	}

	@Test void referenceGetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		fal0.put(HASH1, VALUE1);
		main.assertIt("");
		fal0.assertIt(HASH1, VALUE1, "");
		fal1.assertIt("");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldStream());
		log.assertDebug("get from fallback 0 in default: " + HASH1A);
		if(copyFallbackToMain())
			main.assertIt(HASH1, VALUE1, "getStream putFile");
		else
			main.assertIt("getStream");
		fal0.assertIt(HASH1, VALUE1, "getStream");
		fal1.assertIt("");

		log.assertEmpty();
	}

	@Test void reference1GetStream() throws IOException
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		fal1.put(HASH1, VALUE1);
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt(HASH1, VALUE1, "");

		log.assertEmpty();
		assertEquals(VALUE1, item.getFieldStream());
		log.assertDebug("get from fallback 1 in default: " + HASH1A);
		if(copyFallbackToMain())
			main.assertIt(HASH1, VALUE1, "getStream putFile");
		else
			main.assertIt("getStream");
		fal0.assertIt("getStream");
		fal1.assertIt(HASH1, VALUE1, "getStream");

		log.assertEmpty();
	}

	@Test void notFoundGetLength()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt("");

		assertEquals(6, item.getFieldLength()); // DataField#getLength no longer calls VaultService#getLength
		main.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		fal0.assertIt(""); // DataField#getLength no longer calls VaultService#getLength
		fal1.assertIt(""); // DataField#getLength no longer calls VaultService#getLength

		log.assertEmpty();
	}

	@Test void notFoundGetBytes()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt("");

		final IllegalStateException e = assertFails(
				item::getFieldBytes,
				IllegalStateException.class,
				"vault data missing on " + item + " for VaultItem.field, " +
				"service: VaultMockService:mainExampleValue (fallbacks VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue), " +
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
		fal0.assertIt("getBytes");
		fal1.assertIt("getBytes");

		log.assertEmpty();
	}

	@Test void notFoundGetStream()
	{
		final VaultItem item = new VaultItem(VALUE1);
		main.assertIt(HASH1, VALUE1, "putBytes");
		fal0.assertIt("");
		fal1.assertIt("");

		main.clear();
		main.assertIt("");
		fal0.assertIt("");
		fal1.assertIt("");

		final IllegalStateException e = assertFails(
				item::getFieldStream,
				IllegalStateException.class,
				"vault data missing on " + item + " for VaultItem.field, " +
				"service: VaultMockService:mainExampleValue (fallbacks VaultMockService:referenceExampleValue VaultMockService:reference1ExampleValue), " +
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
		fal0.assertIt("getStream");
		fal1.assertIt("getStream");

		log.assertEmpty();
	}


	private final LogRule log = new LogRule(VaultFallbackService.class);
	private VaultFallbackService service;
	private VaultMockService main, fal0, fal1;

	@BeforeEach void setUp()
	{
		log.setLevelDebug();
		MODEL.connect(ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.default.service", VaultFallbackService.class),
				single("vault.default.service.main", VaultMockService.class),
				single("vault.default.service.main.example", "mainExampleValue"),
				single("vault.default.service.fallbacks.count", 2),
				single("vault.default.service.fallbacks.0", VaultMockService.class),
				single("vault.default.service.fallbacks.0.example", "referenceExampleValue"),
				single("vault.default.service.fallbacks.1", VaultMockService.class),
				single("vault.default.service.fallbacks.1.example", "reference1ExampleValue"),
				single("vault.default.service.copyFallbackToMain", copyFallbackToMain()),
				single("vault.isAppliedToAllFields", true),
				TestSources.minimal()
		)));
		this.service = (VaultFallbackService)VaultTest.vaultService(MODEL);
		main = (VaultMockService)service.getMainService();
		fal0 = (VaultMockService)service.getFallbackServices().get(0);
		fal1 = (VaultMockService)service.getFallbackServices().get(1);
		assertEquals(2, service.getFallbackServices().size());
		main.ancestryPath = "myMainAncestry";
		fal0.ancestryPath = "myReferenceAncestry";
		fal1.ancestryPath = "myReference1Ancestry";
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
	private static void checkSuppressed(final Exception cause, final Consumer<VaultNotFoundException>... check)
	{
		final Throwable[] suppressedAll = cause.getSuppressed();
		assertEquals(check.length, suppressedAll.length);
		for (int i=0; i<check.length; i++)
		{
			check[i].accept((VaultNotFoundException) suppressedAll[i]);
		}
	}
}

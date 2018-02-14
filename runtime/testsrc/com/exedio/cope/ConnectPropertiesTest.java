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

import static com.exedio.cope.ConnectProperties.factory;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.erase;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ConnectProperties.Factory;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultFileService;
import com.exedio.cope.vault.VaultReferenceService;
import com.exedio.cope.vaultmock.VaultMockService;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;

public class ConnectPropertiesTest
{
	/**
	 * This tests makes sure, that no properties are changed by accident.
	 * Adapt if necessary.
	 */
	@Test void testRegression()
	{
		final HashMap<String,Object> notOnDefault = new HashMap<>();
		notOnDefault.put("connection.url", "xxxurl");
		notOnDefault.put("connection.username", "xxxusername");
		notOnDefault.put("connection.password", "xxxpassword");
		notOnDefault.put("dialect", HsqldbDialect.class.getName());
		notOnDefault.put("schema.primaryKeyGenerator", "sequence");
		notOnDefault.put("dataField.vault", true);
		notOnDefault.put("dataField.vault.service", VaultReferenceService.class.getName());
		notOnDefault.put("dataField.vault.service.main", VaultFileService.class.getName());
		notOnDefault.put("dataField.vault.service.main.root", new File("vaultFileRoot"));
		notOnDefault.put("dataField.vault.service.reference", VaultMockService.class.getName());
		notOnDefault.put("cluster", true);
		notOnDefault.put("cluster.secret", 1234);
		final ConnectProperties p = ConnectProperties.create(loadProperties());

		for(final Field field : p.getFields())
		{
			final String key = field.getKey();
			assertTrue(field.isSpecified(), "not specified: " + key);
			assertEquals(
					notOnDefault.containsKey(key) ? notOnDefault.get(key) : field.getDefaultValue(), field.getValue(),
					key);
		}

		p.ensureValidity();
	}

	@Test void testConnectionUrlMissingPrefix()
	{
		assertConnectionUrlFailure(
				"someUrl",
				"property dialect in DESC must be specified as there is no default",
				null);
	}

	@Test void testConnectionUrlMissingColon()
	{
		assertConnectionUrlFailure(
				"jdbc:someCode",
				"property dialect in DESC must be specified as there is no default",
				null);
	}

	@Test void testConnectionUrlOneCharacter()
	{
		assertConnectionUrlFailure(
				"jdbc:a:",
				"property dialect in DESC must be specified as there is no default",
				null);
	}

	@Test void testConnectionUrlTwoCharacters()
	{
		assertConnectionUrlFailure(
				"jdbc:ab:",
				"property dialect in DESC must be specified as there is no default",
				null);
	}

	@Test void testConnectionUrlThreeCharacters()
	{
		assertConnectionUrlFailure(
				"jdbc:abc:",
				"property dialect in DESC must name a class, " +
				"but was 'com.exedio.cope.AbcDialect'",
				ClassNotFoundException.class);
	}

	@Test void testDialectClassNotFound()
	{
		assertDialectFailure(
				"com.exedio.cope.ClassNotFoundDialect",
				"property dialect in DESC must name a class, " +
				"but was 'com.exedio.cope.ClassNotFoundDialect'",
				ClassNotFoundException.class);
	}

	@Test void testDialectClassAbstract()
	{
		assertDialectFailure(
				ConnectPropertiesTestClassAbstractDialect.class.getName(),
				"property dialect in DESC must name a non-abstract class, " +
				"but was " + ConnectPropertiesTestClassAbstractDialect.class.getName(),
				null);
	}

	@Test void testDialectClassNotDialect()
	{
		assertDialectFailure(
				ConnectPropertiesTestClassNotDialectDialect.class.getName(),
				"property dialect in DESC must name a subclass of com.exedio.cope.Dialect, " +
				"but was " + ConnectPropertiesTestClassNotDialectDialect.class.getName(),
				ClassCastException.class);
	}

	@Test void testDialectClassNoConstructor()
	{
		assertDialectFailure(
				ConnectPropertiesTestClassNoConstructorDialect.class.getName(),
				"property dialect in DESC must name a class with a constructor with parameter com.exedio.cope.CopeProbe, "+
				"but was " + ConnectPropertiesTestClassNoConstructorDialect.class.getName(),
				NoSuchMethodException.class);
	}

	private static void assertConnectionUrlFailure(
			final String url,
			final String message,
			final Class<? extends Exception> cause)
	{
		final Source source =
				describe("DESC", erase("dialect", cascade(
						single("connection.url", url),
						loadProperties()
				)));
		try
		{
			ConnectProperties.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					message,
					e.getMessage());

			final Throwable actualCause = e.getCause();
			assertEquals(cause, actualCause!=null ? actualCause.getClass() : null);
		}
	}

	private static void assertDialectFailure(
			final String dialect,
			final String message,
			final Class<? extends Exception> cause)
	{
		final Source source =
				describe("DESC", cascade(
						single("dialect", dialect),
						loadProperties()
				));
		try
		{
			ConnectProperties.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(message, e.getMessage());

			final Throwable actualCause = e.getCause();
			assertEquals(cause, actualCause!=null ? actualCause.getClass() : null);
		}
	}

	@Test void testSupportDisabledForNativeDateStandard()
	{
		final ConnectProperties p = factory().
				create(TestSources.minimal());

		assertEquals(false, p.isSupportDisabledForNativeDate());
	}

	@Test void testSupportDisabledForNativeDateSet()
	{
		final ConnectProperties p = factory().
				disableNativeDate().
				create(TestSources.minimal());

		assertEquals(true, p.isSupportDisabledForNativeDate());
	}

	@Test void testPrimaryKeyGeneratorDefaultStandard()
	{
		final ConnectProperties p = factory().
				create(TestSources.minimal());

		assertEquals(PrimaryKeyGenerator.memory, p.primaryKeyGenerator);
	}

	@Test void testPrimaryKeyGeneratorDefaultSequence()
	{
		final ConnectProperties p = factory().
				primaryKeyGeneratorSequence().
				create(TestSources.minimal());

		assertEquals(PrimaryKeyGenerator.sequence, p.primaryKeyGenerator);
	}

	@Test void testConnectionPoolIdleInitial()
	{
		final String propKey = "connectionPool.idleInitial";
		final Source source =
				describe("DESC", cascade(
						single(propKey, 51),
						loadProperties()
				));
		try
		{
			ConnectProperties.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property " + propKey + " in DESC must be less or equal idleLimit=50, "+
					"but was 51",
					e.getMessage());
		}
	}

	@Test void testItemCacheLimit()
	{
		final Source source =
				cascade(
						single("cache.item.globalLimit", 123),
						TestSources.minimal()
				);
		assertEquals(123, ConnectProperties.create(source).getItemCacheLimit());
	}

	@Test void testItemCacheLimitFallback()
	{
		final Source source =
				cascade(
						single("cache.item.limit", 123),
						TestSources.minimal()
				);
		assertEquals(123, ConnectProperties.create(source).getItemCacheLimit());
	}

	@Test void testItemCacheLimitOverride()
	{
		final Source source =
				cascade(
						single("cache.item.limit", 123),
						single("cache.item.globalLimit", 456),
						TestSources.minimal()
				);
		assertEquals(456, ConnectProperties.create(source).getItemCacheLimit());
	}

	@Test void testVaultAlgorithmDefault()
	{
		final Source source =
				cascade(
						single("dataField.vault", true),
						single("dataField.vault.service", VaultMockService.class),
						TestSources.minimal()
				);
		assertEquals("SHA-512", ConnectProperties.create(source).getVaultAlgorithm());
	}

	@Test void testVaultAlgorithmSet()
	{
		final Source source =
				cascade(
						single("dataField.vault", true),
						single("dataField.vault.service", VaultMockService.class),
						single("dataField.vault.algorithm", "MD5"),
						TestSources.minimal()
				);
		assertEquals("MD5", ConnectProperties.create(source).getVaultAlgorithm());
	}

	@Test void testVaultAlgorithmDisabled()
	{
		final Source source =
				cascade(
						single("dataField.vault", false),
						TestSources.minimal()
				);
		assertEquals(null, ConnectProperties.create(source).getVaultAlgorithm());
	}

	@Test void testMediaRootUrlStandard()
	{
		final ConnectProperties p = factory().
				create(TestSources.minimal());

		assertEquals("media/", p.getMediaRootUrl());
	}

	@Test void testMediaRootUrlCustom()
	{
		final ConnectProperties p = factory().
				mediaRootUrl("/custom/").
				create(TestSources.minimal());

		assertEquals("/custom/", p.getMediaRootUrl());
	}

	@Test void testMediaRootUrlNull()
	{
		final Factory f = factory().mediaRootUrl(null);
		final Source s = TestSources.minimal();

		try
		{
			f.create(s);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals("media.rooturl", e.getKey());
			assertEquals("must be specified as there is no default", e.getDetail());
		}
	}


	private static Source loadProperties()
	{
		return Sources.load(ConnectPropertiesTest.class.getResource("connectPropertiesTest.properties"));
	}

	@Test void testProbe() throws Exception
	{
		final ConnectProperties p = ConnectProperties.create(TestSources.minimal());
		final String expected =
				"HSQL Database Engine 2.4.0 " +
				"HSQL Database Engine Driver 2.4.0 " +
				"PUBLIC";

		assertEquals(expected, p.probe());

		final Callable<?> test = p.getProbeTest();
		assertEquals(expected, test.call());
		assertEquals("probe", test.toString());
	}

	@Test void testProbeVault() throws Exception
	{
		final ConnectProperties p = ConnectProperties.create(cascade(
				single("dataField.vault", true),
				single("dataField.vault.service", VaultMockService.class),
				single("dataField.vault.service.example", "probeExampleValue"),
				TestSources.minimal()));
		final String expected =
				"HSQL Database Engine 2.4.0 " +
				"HSQL Database Engine Driver 2.4.0 " +
				"PUBLIC " +
				"VaultMockService:probeExampleValue";

		assertEquals(expected, p.probe());

		final Callable<?> test = p.getProbeTest();
		assertEquals(expected, test.call());
		assertEquals("probe", test.toString());
	}
}

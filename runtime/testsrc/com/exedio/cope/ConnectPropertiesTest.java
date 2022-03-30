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
import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.erase;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConnectProperties.Factory;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import com.exedio.cope.vault.VaultFileService;
import com.exedio.cope.vault.VaultReferenceService;
import com.exedio.cope.vaultmock.VaultMockService;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
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
		notOnDefault.put("dialect", HsqldbDialect.class);
		notOnDefault.put("schema.primaryKeyGenerator", PrimaryKeyGenerator.sequence);
		notOnDefault.put("vault", true);
		notOnDefault.put("vault.service", VaultReferenceService.class);
		notOnDefault.put("vault.service.main", VaultFileService.class);
		notOnDefault.put("vault.service.main.root", Paths.get("vaultFileRoot"));
		notOnDefault.put("vault.service.reference", VaultMockService.class);
		notOnDefault.put("cluster", true);
		notOnDefault.put("cluster.secret", 1234);
		final ConnectProperties p = ConnectProperties.create(loadProperties());

		for(final Field<?> field : p.getFields())
		{
			final String key = field.getKey();
			assertTrue(field.isSpecified(), "not specified: " + key);
			assertEquals(
					notOnDefault.containsKey(key) ? notOnDefault.get(key) : field.getDefaultValue(), field.getValue(),
					key);
		}

		p.ensureValidity();
	}

	@Test void testOrder()
	{
		final ConnectProperties p = ConnectProperties.create(TestSources.minimal());

		assertEquals(asList(
				"connection.url",
				"connection.username",
				"connection.password",
				"connection.isValidOnGetTimeout",
				"dialect",
				"dialect.approximate",
				"disableSupport.preparedStatements",
				"disableSupport.nativeDate",
				"disableSupport.uniqueViolation",
				"disableSupport.semicolon",
				"fulltextIndex",
				"deleteSchemaForTest",
				"schema.primaryKeyGenerator",
				"schema.tableInNames",
				"schema.revision.table",
				"schema.revision.unique",
				"schema.mysql.lower_case_table_names",
				"revise.auto.enabled",
				"revise.savepoint",
				"connectionPool.idleInitial",
				"connectionPool.idleLimit",
				"query.searchSizeLimit",
				"cache.item.limit",
				"cache.item.globalLimit",
				"cache.query.limit",
				"cache.query.sizeLimit",
				"cache.stamps",
				"dataField.bufferSizeDefault",
				"dataField.bufferSizeLimit",
				"vault-short-key",
				"vault",
				"comparableCheck",
				"changeListeners.queueCapacity",
				"changeListeners.threads.initial",
				"changeListeners.threads.max",
				"changeListeners.threads.priority.set",
				"changeListeners.threads.priority.value",
				"cluster",
				"media.rooturl",
				"media.offsetExpires",
				"media.fingerprintOffset",
				"media.url.secret"),
				p.getFields().stream().map(Field::getKey).collect(Collectors.toList()));
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
				"property dialect in DESC must be specified as there is no default",
				null);
	}

	@Test void testIsValidOnGetTimeoutMinimumExceeded()
	{
		final Source source = describe("DESC", cascade(
				single("connection.isValidOnGetTimeout", ofSeconds(1).minus(ofNanos(1))),
				loadProperties()
		));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property connection.isValidOnGetTimeout in DESC " +
				"must be a duration between PT1S and P24855DT3H14M7S, " +
				"but was PT0.999999999S");
	}

	@Test void testIsValidOnGetTimeoutMaximumExceeded()
	{
		final Source source = describe("DESC", cascade(
				single("connection.isValidOnGetTimeout", ofSeconds(Integer.MAX_VALUE).plus(ofNanos(1))),
				loadProperties()
		));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property connection.isValidOnGetTimeout in DESC " +
				"must be a duration between PT1S and P24855DT3H14M7S, " +
				"but was P24855DT3H14M7.000000001S");
	}

	@Test void testIsValidOnGetTimeoutNano()
	{
		final Source source = describe("DESC", cascade(
				single("connection.isValidOnGetTimeout", ofSeconds(1).plus(ofNanos(1))),
				loadProperties()
		));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property connection.isValidOnGetTimeout in DESC " +
				"must be a duration of whole seconds, " +
				"but was PT1.000000001S");
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


	@Test void testDialectProbeInfoEmpty()
	{
		final Source source =
				describe("DESC", cascade(
						single("dialect", DialectProbeInfoEmpty.class),
						loadProperties()
				));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property dialect in DESC specifies " + DialectProbeInfoEmpty.class.getName() + " " +
				"with @DialectProbeInfo being empty");
	}
	@DialectProbeInfo({})
	static class DialectProbeInfoEmpty extends AssertionFailedDialect
	{
		DialectProbeInfoEmpty(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
		}
	}

	@Test void testDialectProbeInfoOdd()
	{
		final Source source =
				describe("DESC", cascade(
						single("dialect", DialectProbeInfoOdd.class),
						loadProperties()
				));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property dialect in DESC specifies " + DialectProbeInfoOdd.class.getName() + " " +
				"with @DialectProbeInfo containing an odd (1) number of elements");
	}
	@DialectProbeInfo("elem1")
	static class DialectProbeInfoOdd extends AssertionFailedDialect
	{
		DialectProbeInfoOdd(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
		}
	}

	@Test void testDialectProbeInfoEmptyElement()
	{
		final Source source =
				describe("DESC", cascade(
						single("dialect", DialectProbeInfoEmptyElement.class),
						loadProperties()
				));
		assertFails(
				() -> ConnectProperties.create(source),
				IllegalPropertiesException.class,
				"property dialect in DESC specifies " + DialectProbeInfoEmptyElement.class.getName() + " " +
				"with @DialectProbeInfo containing an empty element at position 1");
	}
	@DialectProbeInfo({"elem1", ""})
	static class DialectProbeInfoEmptyElement extends AssertionFailedDialect
	{
		DialectProbeInfoEmptyElement(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
		}
	}

	@Test void testDialectProbeInfoOk()
	{
		final java.util.Properties expected = new java.util.Properties();
		expected.setProperty("user",     "xxxusername");
		expected.setProperty("password", "xxxpassword");
		expected.setProperty("key1", "val1");
		expected.setProperty("key2", "val2");
		final java.util.Properties actual = ConnectProperties.create(cascade(
				single("dialect", DialectProbeInfoOk.class),
				loadProperties()
		)).newInfo();
		assertEquals(expected, actual);
	}
	@DialectProbeInfo({
			"key1", "val1",
			"key2", "val2"})
	static class DialectProbeInfoOk extends AssertionFailedDialect
	{
		DialectProbeInfoOk(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
		}
	}

	@Test void testDialectProbeInfoNone()
	{
		final java.util.Properties expected = new java.util.Properties();
		expected.setProperty("user",     "xxxusername");
		expected.setProperty("password", "xxxpassword");
		final java.util.Properties actual = ConnectProperties.create(cascade(
				single("dialect", DialectProbeInfoNone.class),
				loadProperties()
		)).newInfo();
		assertEquals(expected, actual);
	}
	static class DialectProbeInfoNone extends AssertionFailedDialect
	{
		DialectProbeInfoNone(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
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

	@Test void testRevisionDefaultStandard()
	{
		final ConnectProperties p = factory().
				create(TestSources.minimal());

		assertEquals("while",     p.revisionTableName);
		assertEquals("protected", p.revisionPrimaryKeyName);
	}

	@Test void testRevisionDefault()
	{
		final ConnectProperties p = factory().
				revisionTable("revTab", "revPk").
				create(TestSources.minimal());

		assertEquals("revTab", p.revisionTableName);
		assertEquals("revPk",  p.revisionPrimaryKeyName);
	}

	@Test void testConnectionPool()
	{
		final ConnectProperties p = ConnectProperties.create(cascade(
				single("connectionPool.idleInitial", 55),
				single("connectionPool.idleLimit", 66),
				loadProperties()));
		assertEquals(55, p.getConnectionPoolIdleInitial());
		assertEquals(66, p.getConnectionPoolIdleLimit());
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
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("vault", true),
						single("vault.service", VaultMockService.class),
						TestSources.minimal()
				));
		assertEquals("SHA-512", p.getVaultProperties().getAlgorithm());
		assertEquals("SHA-512", p.getVaultAlgorithm());
		assertSame(p.getVaultProperties(), p.getVaultPropertiesStrict());
	}

	@Test void testVaultAlgorithmSet()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("vault", true),
						single("vault.service", VaultMockService.class),
						single("vault.algorithm", "MD5"),
						TestSources.minimal()
				));
		assertEquals("MD5", p.getVaultProperties().getAlgorithm());
		assertEquals("MD5", p.getVaultAlgorithm());
		assertSame(p.getVaultProperties(), p.getVaultPropertiesStrict());
	}

	@Test void testVaultAlgorithmDisabled()
	{
		final ConnectProperties p = ConnectProperties.create(
				describe("DESC", cascade(
						single("vault", false),
						TestSources.minimal()
				)));
		assertEquals(null, p.getVaultProperties());
		assertEquals(null, p.getVaultAlgorithm());
		assertFails(
				p::getVaultPropertiesStrict,
				IllegalArgumentException.class,
				"vaults are disabled (vault=false) in DESC");
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

	@Test void testMediaMaxAgeStandard()
	{
		final ConnectProperties p = factory().
				create(TestSources.minimal());

		assertEquals(5*1000, getMediaOffsetExpires(p));
		assertEquals(5, p.getMediaMaxAge());
		assertEquals(ofSeconds(5), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeWholeSecond()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", 77000),
						TestSources.minimal()
				));
		assertEquals(77000, getMediaOffsetExpires(p));
		assertEquals(77, p.getMediaMaxAge());
		assertEquals(ofSeconds(77), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeWholeSecondDuration()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", "PT77.000S"),
						TestSources.minimal()
				));
		assertEquals(77000, getMediaOffsetExpires(p));
		assertEquals(77, p.getMediaMaxAge());
		assertEquals(ofSeconds(77), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeRound()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", 77999),
						TestSources.minimal()
				));
		assertEquals(77999, getMediaOffsetExpires(p));
		assertEquals(77, p.getMediaMaxAge());
		assertEquals(ofSeconds(77), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeRoundDuration()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", "PT77.999S"),
						TestSources.minimal()
				));
		assertEquals(77999, getMediaOffsetExpires(p));
		assertEquals(77, p.getMediaMaxAge());
		assertEquals(ofSeconds(77), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeZero()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", 0),
						TestSources.minimal()
				));
		assertEquals(0, getMediaOffsetExpires(p));
		assertEquals(0, p.getMediaMaxAge());
		assertEquals(null, p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeSecondAlmost()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", 999),
						TestSources.minimal()
				));
		assertEquals(999, getMediaOffsetExpires(p));
		assertEquals(0, p.getMediaMaxAge());
		assertEquals(null, p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeSecond()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", 1000),
						TestSources.minimal()
				));
		assertEquals(1000, getMediaOffsetExpires(p));
		assertEquals(1, p.getMediaMaxAge());
		assertEquals(ofSeconds(1), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeMaximum()
	{
		final ConnectProperties p = ConnectProperties.create(
				cascade(
						single("media.offsetExpires", Integer.MAX_VALUE),
						TestSources.minimal()
				));
		assertEquals(Integer.MAX_VALUE, getMediaOffsetExpires(p));
		assertEquals(Integer.MAX_VALUE/1000, p.getMediaMaxAge());
		assertEquals(ofSeconds(Integer.MAX_VALUE/1000), p.getMediaServletMaximumAge());
	}

	@Test void testMediaMaxAgeMaximumExceeded()
	{
		final Source s =
				describe("DESC", cascade(
						single("media.offsetExpires", 1l + Integer.MAX_VALUE),
						TestSources.minimal()
				));
		assertFails(
				() -> ConnectProperties.create(s),
				IllegalPropertiesException.class,
				"property media.offsetExpires in DESC " +
				"must be a duration less or equal PT596H31M23.647S, " +
				"but was "+                      "PT596H31M23.648S");
	}

	@Test void testMediaMaxAgeNegative()
	{
		final Source s =
				describe("DESC", cascade(
						single("media.offsetExpires", -1),
						TestSources.minimal()
				));
		assertFails(
				() -> ConnectProperties.create(s),
				IllegalPropertiesException.class,
				"property media.offsetExpires in DESC " +
				"must be a duration greater or equal PT0S, " +
				"but was PT-0.001S");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	private static int getMediaOffsetExpires(final ConnectProperties p)
	{
		return p.getMediaOffsetExpires();
	}


	private static Source loadProperties()
	{
		return Sources.load(ConnectPropertiesTest.class.getResource("connectPropertiesTest.properties"));
	}

	@Test void testProbe() throws Exception
	{
		final ConnectProperties p = ConnectProperties.create(TestSources.minimal());

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList("Connect"), new ArrayList<>(probes.keySet()));
		assertIt("Connect", HSQLDB_PROBE, EnvironmentInfo.class, probes);

		assertEquals(HSQLDB_PROBE, probe(p));
		assertIt("probe", HSQLDB_PROBE, String.class, getProbeTest(p));
	}

	@Test void testProbeVault() throws Exception
	{
		final ConnectProperties p = ConnectProperties.create(cascade(
				single("vault", true),
				single("vault.service", VaultMockService.class),
				single("vault.service.example", "probeExampleValue"),
				single("vault.service.probe.result", "probeMockResultOverride"),
				TestSources.minimal()));
		final String VAULT = "VaultMockService:probeExampleValue";

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"Connect",
				"vault.default",
				"vault.default.genuineServiceKey",
				"vault.service.Mock"),
				new ArrayList<>(probes.keySet()));
		assertIt("Connect", HSQLDB_PROBE, EnvironmentInfo.class, probes);
		assertIt("vault.default", VAULT, String.class, probes);
		assertIt("vault.default.genuineServiceKey", "mock:default", String.class, probes);
		assertIt("vault.service.Mock", "probeMockResultOverride", String.class, probes);

		assertEquals(HSQLDB_PROBE + " [" + VAULT + ", mock:default]", probe(p));
		assertIt("probe", HSQLDB_PROBE + " [" + VAULT + ", mock:default]", String.class, getProbeTest(p));
	}

	public static final String HSQLDB_PROBE =
			"HSQL Database Engine 2.5.1 " +
			"HSQL Database Engine Driver 2.5.1 " +
			"org.hsqldb.jdbc.JDBCDriver " +
			"PUBLIC";

	static void assertIt(
			final String expectedName,
			final String expectedResultString,
			final Class<?> expectedResultClass,
			final Map<String,Callable<?>> probes) throws Exception
	{
		final Callable<?> actual = probes.get(expectedName);
		assertNotNull(actual, expectedName);
		assertIt(expectedName, expectedResultString, expectedResultClass, actual);
	}

	static void assertIt(
			final String expectedName,
			final String expectedResultString,
			final Class<?> expectedResultClass,
			final Callable<?> actual) throws Exception
	{
		assertEquals(expectedName, actual.toString());
		final Object actualResult = actual.call();
		assertEquals(expectedResultString, actualResult.toString());
		assertSame(expectedResultClass, actualResult.getClass());
	}

	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	static String probe(final ConnectProperties p)
	{
		return p.probe();
	}

	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	static Callable<?> getProbeTest(final ConnectProperties p)
	{
		return p.getProbeTest();
	}


	@Test void testProbeDialectConstructorFails()
	{
		final ConnectProperties p = ConnectProperties.create(cascade(
				single("dialect", DialectConstructorFails.class),
				TestSources.minimal()));
		final RuntimeException re = assertFails(
				p::probeConnect,
				RuntimeException.class,
				DialectConstructorFails.class.getName() + "(com.exedio.cope.CopeProbe)");
		final Throwable ite = re.getCause();
		assertEquals(InvocationTargetException.class, ite.getClass());
		assertEquals(null, ite.getMessage());
		final Throwable iae = ite.getCause();
		assertEquals(IllegalArgumentException.class, iae.getClass());
		assertEquals("DialectConstructorFails constructor", iae.getMessage());
	}
	static class DialectConstructorFails extends AssertionFailedDialect
	{
		DialectConstructorFails(@SuppressWarnings("unused") final CopeProbe probe)
		{
			super(null);
			throw new IllegalArgumentException("DialectConstructorFails constructor");
		}
	}
}

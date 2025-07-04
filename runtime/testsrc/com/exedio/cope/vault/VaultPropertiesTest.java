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

import static com.exedio.cope.ConnectPropertiesTest.assertMatches;
import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.Vault.DEFAULT;
import static com.exedio.cope.Vault.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static com.exedio.cope.vault.VaultProperties.checkBucket;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.IllegalAlgorithmException;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.ProbeAbortedException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultPropertiesTest
{
	@Test void checkBucketNull()
	{
		assertFails(
				() -> checkBucket(null, null),
				NullPointerException.class,
				"bucket");
	}
	@Test void checkBucketExceptionNull()
	{
		assertFails(
				() -> checkBucket("", null),
				NullPointerException.class,
				"exception");
	}
	@Test void checkBucketEmpty()
	{
		assertFails(
				() -> checkBucket("", ArithmeticException::new),
				ArithmeticException.class,
				"must not be empty");
	}
	@Test void checkBucketInvalidChar()
	{
		assertFails(
				() -> checkBucket("abc.efg", ArithmeticException::new),
				ArithmeticException.class,
				"must contain just [---,0-9,A-Z,a-z], " +
				"but was >abc.efg< containing a forbidden character at position 3");
	}
	@Test void checkBucketNone()
	{
		assertFails(
				() -> checkBucket(NONE, ArithmeticException::new),
				ArithmeticException.class,
				"must not be special value >/none/< from Vault.NONE");
	}
	@Test void checkBucketOk()
	{
		checkBucket("a", m -> { throw new AssertionFailedError(); });
	}
	@Test void probe() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultMockService.class),
						single("default.service.example", "probeExampleValue")
				));
		final VaultProperties props = factory.create(source);
		assertMatches("VaultMockService:probeExampleValue [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("\\[VaultMockService:probeExampleValue [0-9a-f]{16}xx128, mock:default]", probeDeprecated(props));
	}
	@Test void probeFailGet()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", FailGetService.class)
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probe(props),
				IllegalStateException.class,
				"deliberately fail in FailGetService#get");
		assertFails(
				() -> probeDeprecated(props),
				IllegalStateException.class,
				"deliberately fail in FailGetService#get");
	}
	private static final class FailGetService extends VaultMockService
	{
		private FailGetService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public byte[] get(final String hash)
		{
			throw new IllegalStateException("deliberately fail in FailGetService#get");
		}
	}
	@Test void probeFailPut()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", FailPutService.class)
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probe(props),
				IllegalStateException.class,
				"deliberately fail in FailPutService#put");
		assertFails(
				() -> probeDeprecated(props),
				IllegalStateException.class,
				"deliberately fail in FailPutService#put");
	}
	private static final class FailPutService extends VaultMockService
	{
		private FailPutService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public boolean put(final String hash, final byte[] value)
		{
			throw new IllegalStateException("deliberately fail in FailPutService#put");
		}
	}
	@Test void probeFailGetNotFound()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailGetNotFoundService.class));
		assertFails(
				() -> probe(props),
				RuntimeException.class,
				"FailGetNotFoundService:exampleDefault: " +
				"get should have thrown VaultNotFoundException, but got 010203");
		assertFails(
				() -> probeDeprecated(props),
				RuntimeException.class,
				"FailGetNotFoundService:exampleDefault: " +
				"get should have thrown VaultNotFoundException, but got 010203");
	}
	private static final class FailGetNotFoundService extends VaultMockService
	{
		private FailGetNotFoundService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public byte[] get(final String hash)
		{
			return new byte[]{1,2,3};
		}
	}
	@Test void probeFailGetNotFoundNonMatch()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailGetNotFoundNonMatchService.class));
		final RuntimeException e = assertThrows(
				RuntimeException.class,
				() -> probe(props));
		assertMatches(
				"FailGetNotFoundNonMatchService:exampleDefault: " +
				"VaultNotFoundException should have matching hash " +
				"[0-9a-f]{16}xx128 vs\\. 334455",
				e.getMessage());
	}
	private static final class FailGetNotFoundNonMatchService extends VaultMockService
	{
		private FailGetNotFoundNonMatchService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public byte[] get(final String hash) throws VaultNotFoundException
		{
			throw new VaultNotFoundException("334455");
		}
	}
	@Test void probeFailPutResult()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailPutNotFoundService.class));
		assertFails(
				() -> probe(props),
				RuntimeException.class,
				"FailPutNotFoundService:exampleDefault: " +
				"put should have returned true");
		assertFails(
				() -> probeDeprecated(props),
				RuntimeException.class,
				"FailPutNotFoundService:exampleDefault: " +
				"put should have returned true");
	}
	private static final class FailPutNotFoundService extends VaultMockService
	{
		private FailPutNotFoundService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public boolean put(final String hash, final byte[] valuue)
		{
			return false;
		}
	}
	@Test void probeFailGetMissing()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailGetMissingService.class));
		assertFails(
				() -> probe(props),
				RuntimeException.class,
				"FailGetMissingService:exampleDefault: " +
				"get should have returned value");
		assertFails(
				() -> probeDeprecated(props),
				RuntimeException.class,
				"FailGetMissingService:exampleDefault: " +
				"get should have returned value");
	}
	private static final class FailGetMissingService extends VaultMockService
	{
		private FailGetMissingService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public byte[] get(final String hash) throws VaultNotFoundException
		{
			super.get(hash);
			throw new VaultNotFoundException("778899");
		}
	}
	@Test void probeFailGetMismatch()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailGetMismatchService.class));
		final RuntimeException e = assertThrows(
				RuntimeException.class,
				() -> probe(props));
		assertMatches(
				"FailGetMismatchService:exampleDefault: " +
				"get should have returned matching value " +
				"[0-9a-f]*\\.\\.\\.\\([0-9]*\\) vs\\. 040506",
				e.getMessage());
	}
	private static final class FailGetMismatchService extends VaultMockService
	{
		private FailGetMismatchService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public byte[] get(final String hash) throws VaultNotFoundException
		{
			super.get(hash);
			return new byte[]{4,5,6};
		}
	}
	@Test void probeFailContainsMismatch()
	{
		final VaultProperties props =
				factory.create(single("default.service", FailContainsMismatchService.class));
		final RuntimeException e = assertThrows(
				RuntimeException.class,
				() -> probe(props));
		assertMatches(
				"FailContainsMismatchService:exampleDefault: " +
				"contains should have returned false",
				e.getMessage());
	}
	private static final class FailContainsMismatchService extends VaultMockService
	{
		private FailContainsMismatchService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public boolean contains(final String hash) throws VaultServiceUnsupportedOperationException
		{
			return !super.contains(hash);
		}
	}
	@Test void probeContainsUnsupported() throws Exception
	{
		final VaultProperties props =
				factory.create(single("default.service", ContainsUnsupportedService.class));
		probe(props);
	}
	private static final class ContainsUnsupportedService extends VaultMockService
	{
		private ContainsUnsupportedService(final VaultServiceParameters pa, final Props po) { super(pa, po); }
		@Override public boolean contains(final String hash) throws VaultServiceUnsupportedOperationException
		{
			//noinspection resource OK: no resources allocated
			return new VaultServiceDefaultTest.VaultDefaultService().contains(hash);
		}
	}
	private static Object probe(final VaultProperties p) throws Exception
	{
		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"default.Contract",
				"default.BucketTag",
				"default.service.Mock"),
				new ArrayList<>(probes.keySet()));
		final Callable<?> probe = requireNonNull(probes.get("default.Contract"));
		return probe.call();
	}
	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	private static String probeDeprecated(final VaultProperties p)
	{
		return p.probe();
	}


	@Test void probeBucketTag() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultMockService.class),
						single("default.service.bucketTagAction", "default")
				));
		final VaultProperties props = factory.create(source);
		assertEquals("mock:default", probeBucketTag(props));
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("\\[VaultMockService:exampleDefault [0-9a-f]{16}xx128, mock:default]", probeDeprecated(props));
	}
	@Test void probeBucketTagAbort() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultMockService.class),
						single("default.service.bucketTagAction", "ABORT in test")
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probeBucketTag(props),
				ProbeAbortedException.class,
				"ABORT in test(default)");
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", probeDeprecated(props));
	}
	@Test void probeBucketTagFail() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultMockService.class),
						single("default.service.bucketTagAction", "FAIL in test")
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probeBucketTag(props),
				IllegalStateException.class,
				"FAIL in test(default)");
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertFails(() -> probeDeprecated(props), IllegalStateException.class, "FAIL in test(default)");
	}
	private static Object probeBucketTag(final VaultProperties p) throws Exception
	{
		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"default.Contract",
				"default.BucketTag",
				"default.service.Mock"),
				new ArrayList<>(probes.keySet()));
		final Callable<?> probe = probes.get("default.BucketTag");
		return probe.call();
	}


	@Test void algorithmNotFound()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.algorithm", "NIXUS")
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.algorithm in DESC must specify a digest, but was 'NIXUS'");
		assertTrue(e.getCause() instanceof IllegalPropertiesException);
		assertTrue(e.getCause().getCause() instanceof IllegalAlgorithmException);
	}


	@Test void buckets()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "alpha beta gamma"),
						single("alpha.service", VaultMockService.class),
						single("beta.service",  VaultMockService.class),
						single("gamma.service", VaultMockService.class),
						single("alpha.service.example", "alphaEx"),
						single("beta.service.example" , "betaEx" ),
						single("gamma.service.example", "gammaEx")
				));
		final VaultProperties p = factory.create(source);
		assertEqualsUnmodifiable(
				new HashSet<>(asList("alpha", "beta", "gamma")),
				p.buckets.keySet());

		assertNotNull(p.bucket("alpha"));
		assertNotNull(p.bucket("beta"));
		assertNotNull(p.bucket("gamma"));
		assertFails(
				() -> p.bucket(null),
				NullPointerException.class,
				"key");
		assertFails(
				() -> p.bucket("delta"),
				IllegalArgumentException.class,
				"key must be one of [alpha, beta, gamma], but was >delta<");

		assertServices(deresiliate(p.newServices("alpha", "beta", "gamma")), "alpha", "beta", "gamma");
		assertServices(deresiliate(p.newServices("alpha", "gamma", "beta")), "alpha", "gamma", "beta");
		assertServices(deresiliate(p.newServices("alpha", "gamma")), "alpha", "gamma");
		assertServices(deresiliate(p.newServices("beta")), "beta");
		assertServices(deresiliate(p.newServices(new String[]{})));
		assertFails(
				() -> p.newServices("alpha", "beta", "gamma", "delta"),
				IllegalArgumentException.class,
				"keys[3] must be one of [alpha, beta, gamma], but was >delta<");
		assertFails(
				() -> p.newServices("x"),
				IllegalArgumentException.class,
				"keys[0] must be one of [alpha, beta, gamma], but was >x<");
		assertFails(
				() -> p.newServices(""),
				IllegalArgumentException.class,
				"keys[0] must be one of [alpha, beta, gamma], but was ><");
		assertFails(
				() -> p.newServices("alpha", null),
				NullPointerException.class, "keys[1]");
		assertFails(
				() -> p.newServices((String[])null),
				NullPointerException.class, "keys");
		assertFails(
				() -> p.newServices("alpha", "beta", "alpha"),
				IllegalArgumentException.class,
				"keys[2] is a duplicate of index 0: >alpha<");
		assertServices(p.newServicesNonResilient("alpha", "beta", "gamma"), "alpha", "beta", "gamma");
		assertServices(p.newServicesNonResilient(new String[]{}));

		assertServices(deresiliate(p.newServices()), "alpha", "beta", "gamma");
	}
	@Test void bucketDeprecated()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "only"),
						single("only.service", VaultMockService.class),
						single("only.service.example", "onlyEx")
				));
		final VaultProperties p = factory.create(source);
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		final VaultService service = deresiliate(p.newService());
		assertEquals(VaultMockService.class, service.getClass());
		assertEquals("onlyEx", ((VaultMockService)service).serviceProperties.example);

		assertServices(p.newServicesNonResilient("only"), "only");
		assertServices(p.newServicesNonResilient(new String[]{}));
		assertServices(deresiliate(p.newServices("only")), "only");
		assertServices(deresiliate(p.newServices(new String[]{})));
		assertServices(deresiliate(p.newServices()), "only");
	}
	private static void assertServices(final Map<String, VaultService> s, final String... expectedKeys)
	{
		assertUnmodifiable(s);
		assertEquals(asList(expectedKeys), new ArrayList<>(s.keySet()));
		for(final Map.Entry<String, VaultService> e : s.entrySet())
		{
			assertEquals(VaultMockService.class, e.getValue().getClass(), e.getKey());
			assertEquals(e.getKey() + "Ex", ((VaultMockService)e.getValue()).serviceProperties.example, e.getKey());
		}
	}
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void bucketDeprecatedNotAllowed()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "alpha beta"),
						single("alpha.service", VaultMockService.class),
						single("beta.service",  VaultMockService.class)
				));
		final VaultProperties p = factory.create(source);
		assertFails(
				p::newService,
				IllegalArgumentException.class,
				"is not allowed for more than one service: [alpha, beta]");
	}
	@Test void bucketsWithSpaces()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "  alpha  beta  gamma  "),
						single("alpha.service", VaultMockService.class),
						single("beta.service",  VaultMockService.class),
						single("gamma.service", VaultMockService.class)
				));
		final VaultProperties p = factory.create(source);
		assertEqualsUnmodifiable(
				new HashSet<>(asList("alpha", "beta", "gamma")),
				p.buckets.keySet());
	}
	@Test void bucketsEmpty()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "")
				));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property buckets in DESC must not be empty");
	}
	@Test void bucketsCharSet()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "alpha be.ta gamma")
				));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property buckets in DESC " +
				"must contain a space-separated list of buckets, " +
				"but bucket >be.ta< is illegal: " +
				"must contain just [---,0-9,A-Z,a-z], but was >be.ta< containing a forbidden character at position 2");
	}
	@Test void bucketsDuplicate()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "a dup dup b")
				));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property buckets in DESC must not contain duplicates");
	}
	@Test void serviceEmpty()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", "")
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.service in DESC must name a class, but was ''");
		final Throwable outerCause = e.getCause();
		assertNotNull(outerCause);
		assertTrue(outerCause instanceof IllegalPropertiesException, outerCause.getClass().getName());
		assertEquals("property service in DESC (prefix default.) must name a class, but was ''", outerCause.getMessage());
		final Throwable cause = outerCause.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof ClassNotFoundException, cause.getClass().getName());
		assertEquals("", cause.getMessage());
	}


	@Test void servicePropertiesMissing()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", ServicePropertiesMissing.class)
				));

		final VaultProperties props = factory.create(source);
		final ServicePropertiesMissing service = (ServicePropertiesMissing)deresiliate(props.newServices(DEFAULT)).get(DEFAULT);
		assertSame(props.bucket("default"), service.parameters.getBucketProperties());
	}
	@Test void servicePropertiesMissingReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultReferenceService.class),
						single("default.service.main", ServicePropertiesMissing.class),
						single("default.service.reference", ServicePropertiesMissing.class)
				));
		final VaultProperties props = factory.create(source);
		final VaultReferenceService service = (VaultReferenceService)deresiliate(props.newServices(DEFAULT)).get(DEFAULT);
		final ServicePropertiesMissing main = (ServicePropertiesMissing)service.getMainService();
		assertEquals(1, service.getReferenceServices().size());
		final ServicePropertiesMissing ref  = (ServicePropertiesMissing)service.getReferenceServices().get(0);
		assertSame(props.bucket("default"), main.parameters.getBucketProperties());
		assertSame(props.bucket("default"), ref .parameters.getBucketProperties());
		assertNotSame(main, ref);
	}
	static class ServicePropertiesMissing extends AssertionErrorVaultService
	{
		final VaultServiceParameters parameters;

		ServicePropertiesMissing(final VaultServiceParameters parameters)
		{
			this.parameters = parameters;
		}
	}


	@Test void servicePropertiesNoConstructor()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", ServicePropertiesNoConstructor.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.service in DESC names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName());
		final Throwable causeOuter = e.getCause();
		assertNotNull(causeOuter);
		assertTrue(causeOuter instanceof IllegalPropertiesException, causeOuter.getClass().getName());
		assertEquals(
				"property service in DESC (prefix default.) names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName(),
				causeOuter.getMessage());
		final Throwable cause2 = causeOuter.getCause();
		assertNotNull(cause2);
		assertTrue(cause2 instanceof NoSuchMethodException, cause2.getClass().getName());
		assertEquals(ServicePropertiesNoConstructorProps.class.getName() + ".<init>(" + Source.class.getName() + ")", cause2.getMessage());
	}
	@Test void servicePropertiesNoConstructorReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultReferenceService.class),
						single("default.service.main", ServicePropertiesNoConstructor.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.service.main in DESC names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName());
		final Throwable nestOuter = e.getCause();
		assertNotNull(nestOuter);
		assertTrue(nestOuter instanceof IllegalPropertiesException, nestOuter.getClass().getName());
		assertEquals(
				"property service.main in DESC (prefix default.) names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName(),
				nestOuter.getMessage());
		final Throwable nested = nestOuter.getCause();
		assertNotNull(nested);
		assertTrue(nested instanceof IllegalPropertiesException, nested.getClass().getName());
		assertEquals(
				"property main in DESC (prefix default.service.) names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName(),
				nested.getMessage());
		final Throwable cause2 = nested.getCause();
		assertNotNull(cause2);
		assertTrue(cause2 instanceof NoSuchMethodException, cause2.getClass().getName());
		assertEquals(ServicePropertiesNoConstructorProps.class.getName() + ".<init>(" + Source.class.getName() + ")", cause2.getMessage());
	}
	static class ServicePropertiesNoConstructorProps extends Properties
	{
		ServicePropertiesNoConstructorProps() { super(null); }
	}
	@ServiceProperties(ServicePropertiesNoConstructorProps.class)
	static class ServicePropertiesNoConstructor extends AssertionErrorVaultService
	{
		ServicePropertiesNoConstructor(@SuppressWarnings("unused") final VaultServiceParameters p) {}
	}


	@Test void servicePropertiesFails()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", ServicePropertiesFails.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalArgumentException.class,
				"property default in DESC invalid, see nested exception");
		final Throwable outerCause = e.getCause();
		assertNotNull(outerCause);
		assertTrue(outerCause instanceof IllegalArgumentException, outerCause.getClass().getName());
		assertEquals("property service in DESC (prefix default.) invalid, see nested exception", outerCause.getMessage());
		final Throwable cause = outerCause.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof IllegalStateException, cause.getClass().getName());
		assertEquals("exception from ServicePropertiesFailsProps", cause.getMessage());
		assertNull(cause.getCause());
	}
	@Test void servicePropertiesFailsReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("default.service", VaultReferenceService.class),
						single("default.service.main", ServicePropertiesFails.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalArgumentException.class,
				"property default in DESC invalid, see nested exception");
		final Throwable nestedOuter = e.getCause();
		assertTrue(nestedOuter instanceof IllegalArgumentException, nestedOuter.getClass().getName());
		assertEquals(
				"property service in DESC (prefix default.) invalid, see nested exception",
				nestedOuter.getMessage());
		final Throwable nested = nestedOuter.getCause();
		assertTrue(nested instanceof IllegalArgumentException, nested.getClass().getName());
		assertEquals(
				"property main in DESC (prefix default.service.) invalid, see nested exception",
				nested.getMessage());
		final Throwable cause = nested.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof IllegalStateException, cause.getClass().getName());
		assertEquals("exception from ServicePropertiesFailsProps", cause.getMessage());
		assertNull(cause.getCause());
	}
	static class ServicePropertiesFailsProps extends Properties
	{
		ServicePropertiesFailsProps(final Source source)
		{
			super(source);
			throw new IllegalStateException("exception from ServicePropertiesFailsProps");
		}
	}
	@ServiceProperties(ServicePropertiesFailsProps.class)
	static class ServicePropertiesFails extends AssertionErrorVaultService
	{
		ServicePropertiesFails(@SuppressWarnings("unused") final VaultServiceParameters p) {}
	}


	@Test void trailDefault()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class)
		));
		final Bucket props = factory.create(source).bucket("default");
		assertEquals(20, props.getTrailStartLimit());
		assertEquals(80, props.getTrailFieldLimit());
		assertEquals(80, props.getTrailOriginLimit());
	}
	@Test void trailCustom()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.startLimit", 66),
				single("default.trail.fieldLimit", 77),
				single("default.trail.originLimit", 88)
		));
		final Bucket props = factory.create(source).bucket("default");
		assertEquals(66, props.getTrailStartLimit());
		assertEquals(77, props.getTrailFieldLimit());
		assertEquals(88, props.getTrailOriginLimit());
	}
	@Test void trailCustomDefault()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("trail.startLimit", 66),
				single("trail.fieldLimit", 77),
				single("trail.originLimit", 88)
		));
		final Bucket props = factory.create(source).bucket("default");
		assertEquals(66, props.getTrailStartLimit());
		assertEquals(77, props.getTrailFieldLimit());
		assertEquals(88, props.getTrailOriginLimit());
	}
	@Test void trailCustomOverride()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.startLimit", 166),
				single("default.trail.fieldLimit", 177),
				single("default.trail.originLimit", 188),
				single("trail.startLimit", 66),
				single("trail.fieldLimit", 77),
				single("trail.originLimit", 88)
		));
		final Bucket props = factory.create(source).bucket("default");
		assertEquals(166, props.getTrailStartLimit());
		assertEquals(177, props.getTrailFieldLimit());
		assertEquals(188, props.getTrailOriginLimit());
	}
	@Test void trailMinimum()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.startLimit", 4),
				single("default.trail.fieldLimit", 4),
				single("default.trail.originLimit", 4)
		));
		final Bucket props = factory.create(source).bucket("default");
		assertEquals(4, props.getTrailStartLimit());
		assertEquals(4, props.getTrailFieldLimit());
		assertEquals(4, props.getTrailOriginLimit());
	}
	@Test void trailStartTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.startLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.trail.startLimit in DESC " +
				"must be an integer greater or equal 4, " +
				"but was 3");
	}
	@Test void trailFieldTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.fieldLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.trail.fieldLimit in DESC " +
				"must be an integer greater or equal 4, " +
				"but was 3");
	}
	@Test void trailOriginTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("default.service", VaultMockService.class),
				single("default.trail.originLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property default.trail.originLimit in DESC " +
				"must be an integer greater or equal 4, " +
				"but was 3");
	}



	private static final Properties.Factory<VaultProperties> factory = VaultProperties.factory();


	public static final Map<String, VaultService> deresiliate(final Map<String, VaultResilientService> services)
	{
		assertUnmodifiable(services);
		final LinkedHashMap<String, VaultService> result = new LinkedHashMap<>();
		for(final Map.Entry<String, VaultResilientService> e : services.entrySet())
		{
			result.put(e.getKey(), deresiliate(e.getValue()));
		}
		return Collections.unmodifiableMap(result);
	}

	public static VaultService deresiliate(final VaultResilientService service)
	{
		assertEquals(VaultResilientServiceProxy.class, service.getClass());
		return ((VaultResilientServiceProxy)service).service;
	}
}

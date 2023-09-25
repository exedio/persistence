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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.assertUnmodifiable;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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

public class VaultPropertiesTest
{
	@Test void probe() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.example", "probeExampleValue")
				));
		final VaultProperties props = factory.create(source);
		assertMatches("VaultMockService:probeExampleValue [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("\\[VaultMockService:probeExampleValue [0-9a-f]{16}xx128, mock:default]", probeDeprecated(props));
	}
	@Test void probeFailGet()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.fail.get", true)
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probe(props),
				IllegalStateException.class,
				"deliberately fail in VaultMockService#get");
		assertFails(
				() -> probeDeprecated(props),
				IllegalStateException.class,
				"deliberately fail in VaultMockService#get");
	}
	@Test void probeFailPut()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.fail.put", true)
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probe(props),
				IllegalStateException.class,
				"deliberately fail in VaultMockService#put");
		assertFails(
				() -> probeDeprecated(props),
				IllegalStateException.class,
				"deliberately fail in VaultMockService#put");
	}
	private static Object probe(final VaultProperties p) throws Exception
	{
		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"default",
				"default.genuineServiceKey",
				"service.Mock"),
				new ArrayList<>(probes.keySet()));
		final Callable<?> probe = requireNonNull(probes.get("default"));
		return probe.call();
	}
	@SuppressWarnings("deprecation") // OK, wrapping deprecated API
	private static String probeDeprecated(final VaultProperties p)
	{
		return p.probe();
	}


	@Test void probeGenuineServiceKey() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.genuineServiceKey", "default")
				));
		final VaultProperties props = factory.create(source);
		assertEquals("mock:default", probeGenuineServiceKey(props));
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("\\[VaultMockService:exampleDefault [0-9a-f]{16}xx128, mock:default]", probeDeprecated(props));
	}
	@Test void probeGenuineServiceKeyAbort() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.genuineServiceKey", "ABORT in test")
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probeGenuineServiceKey(props),
				ProbeAbortedException.class,
				"ABORT in test(default)");
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", probeDeprecated(props));
	}
	@Test void probeGenuineServiceKeyFail() throws Exception
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultMockService.class),
						single("service.genuineServiceKey", "FAIL in test")
				));
		final VaultProperties props = factory.create(source);
		assertFails(
				() -> probeGenuineServiceKey(props),
				IllegalStateException.class,
				"FAIL in test(default)");
		assertMatches("VaultMockService:exampleDefault [0-9a-f]{16}xx128", (String)probe(props));
		assertFails(() -> probeDeprecated(props), IllegalStateException.class, "FAIL in test(default)");
	}
	private static Object probeGenuineServiceKey(final VaultProperties p) throws Exception
	{
		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"default",
				"default.genuineServiceKey",
				"service.Mock"),
				new ArrayList<>(probes.keySet()));
		final Callable<?> probe = probes.get("default.genuineServiceKey");
		return probe.call();
	}


	@Test void algorithmNotFound()
	{
		final Source source =
				describe("DESC", cascade(
						single("algorithm", "NIXUS")
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property algorithm in DESC must specify a digest, but was 'NIXUS'");
		assertTrue(e.getCause() instanceof IllegalAlgorithmException);
	}


	@Test void services()
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
				p.services.keySet());

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
	@Test void serviceDeprecated()
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
	@Test void serviceDeprecatedNotAllowed()
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
	@Test void servicesWithSpaces()
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
				p.services.keySet());
	}
	@Test void servicesEmpty()
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
	@Test void servicesCharSet()
	{
		final Source source =
				describe("DESC", cascade(
						single("buckets", "alpha be.ta gamma")
				));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property buckets in DESC " +
				"must contain a space separates list of services " +
				"containing just [---,0-9,A-Z,a-z], " +
				"but service >be.ta< contained a forbidden character at position 2.");
	}
	@Test void servicesDuplicate()
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
	@Test void servicePropertiesEmpty()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", "")
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property service in DESC must name a class, but was ''");
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof ClassNotFoundException, cause.getClass().getName());
		assertEquals("", cause.getMessage());
	}


	@Test void servicePropertiesMissing()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", ServicePropertiesMissing.class)
				));

		final VaultProperties props = factory.create(source);
		final ServicePropertiesMissing service = (ServicePropertiesMissing)deresiliate(props.newServices(DEFAULT)).get(DEFAULT);
		assertSame(props, service.parameters.getVaultProperties());
	}
	@Test void servicePropertiesMissingReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", ServicePropertiesMissing.class),
						single("service.reference", ServicePropertiesMissing.class)
				));
		final VaultProperties props = factory.create(source);
		final VaultReferenceService service = (VaultReferenceService)deresiliate(props.newServices(DEFAULT)).get(DEFAULT);
		final ServicePropertiesMissing main = (ServicePropertiesMissing)service.getMainService();
		final ServicePropertiesMissing ref  = (ServicePropertiesMissing)service.getReferenceService();
		assertSame(props, main.parameters.getVaultProperties());
		assertSame(props, ref .parameters.getVaultProperties());
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
						single("service", ServicePropertiesNoConstructor.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property service in DESC names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName());
		final Throwable cause2 = e.getCause();
		assertNotNull(cause2);
		assertTrue(cause2 instanceof NoSuchMethodException, cause2.getClass().getName());
		assertEquals(ServicePropertiesNoConstructorProps.class.getName() + ".<init>(" + Source.class.getName() + ")", cause2.getMessage());
	}
	@Test void servicePropertiesNoConstructorReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", ServicePropertiesNoConstructor.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property service.main in DESC names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
				"annotated by @ServiceProperties(" + ServicePropertiesNoConstructorProps.class.getName() + "), " +
				"which must have a constructor with parameter " + Source.class.getName());
		final Throwable nested = e.getCause();
		assertNotNull(nested);
		assertTrue(nested instanceof IllegalPropertiesException, nested.getClass().getName());
		assertEquals(
				"property main in DESC (prefix service.) names a class " + ServicePropertiesNoConstructor.class.getName() + " " +
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
						single("service", ServicePropertiesFails.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalArgumentException.class,
				"property service in DESC invalid, see nested exception");
		final Throwable cause = e.getCause();
		assertNotNull(cause);
		assertTrue(cause instanceof IllegalStateException, cause.getClass().getName());
		assertEquals("exception from ServicePropertiesFailsProps", cause.getMessage());
		assertNull(cause.getCause());
	}
	@Test void servicePropertiesFailsReference()
	{
		final Source source =
				describe("DESC", cascade(
						single("service", VaultReferenceService.class),
						single("service.main", ServicePropertiesFails.class)
				));
		final Exception e = assertFails(
				() -> factory.create(source),
				IllegalArgumentException.class,
				"property service in DESC invalid, see nested exception");
		final Throwable nested = e.getCause();
		assertTrue(nested instanceof IllegalArgumentException, nested.getClass().getName());
		assertEquals(
				"property main in DESC (prefix service.) invalid, see nested exception",
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
				single("service", VaultMockService.class)
		));
		final VaultProperties props = factory.create(source);
		assertEquals(20, props.getTrailStartLimit());
		assertEquals(80, props.getTrailFieldLimit());
		assertEquals(80, props.getTrailOriginLimit());
	}
	@Test void trailCustom()
	{
		final Source source = describe("DESC", cascade(
				single("service", VaultMockService.class),
				single("trail.startLimit", 66),
				single("trail.fieldLimit", 77),
				single("trail.originLimit", 88)
		));
		final VaultProperties props = factory.create(source);
		assertEquals(66, props.getTrailStartLimit());
		assertEquals(77, props.getTrailFieldLimit());
		assertEquals(88, props.getTrailOriginLimit());
	}
	@Test void trailMinimum()
	{
		final Source source = describe("DESC", cascade(
				single("service", VaultMockService.class),
				single("trail.startLimit", 4),
				single("trail.fieldLimit", 4),
				single("trail.originLimit", 4)
		));
		final VaultProperties props = factory.create(source);
		assertEquals(4, props.getTrailStartLimit());
		assertEquals(4, props.getTrailFieldLimit());
		assertEquals(4, props.getTrailOriginLimit());
	}
	@Test void trailStartTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("service", VaultMockService.class),
				single("trail.startLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property trail.startLimit in DESC " +
				"must be an integer greater or equal 4, " +
				"but was 3");
	}
	@Test void trailFieldTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("service", VaultMockService.class),
				single("trail.fieldLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property trail.fieldLimit in DESC " +
				"must be an integer greater or equal 4, " +
				"but was 3");
	}
	@Test void trailOriginTooSmall()
	{
		final Source source = describe("DESC", cascade(
				single("service", VaultMockService.class),
				single("trail.originLimit", 3)
		));
		assertFails(
				() -> factory.create(source),
				IllegalPropertiesException.class,
				"property trail.originLimit in DESC " +
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

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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
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
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.ServiceProperties;
import com.exedio.cope.vaultmock.VaultMockService;
import java.util.List;
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
		assertEquals("VaultMockService:probeExampleValue", probe(props));
		assertEquals("VaultMockService:probeExampleValue", probeDeprecated(props));
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
		final List<? extends Callable<?>> probes = p.getProbes();
		assertEquals(2, probes.size());
		assertEquals("service.Mock", probes.get(1).toString());
		final Callable<?> probe = probes.get(0);
		assertEquals("probe", probe.toString());
		return probe.call();
	}
	private static String probeDeprecated(final VaultProperties p)
	{
		return p.probe();
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
		@SuppressWarnings("resource")
		final ServicePropertiesMissing service = (ServicePropertiesMissing)props.newService();
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
		@SuppressWarnings("resource")
		final VaultReferenceService service = (VaultReferenceService)props.newService();
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



	private static final Properties.Factory<VaultProperties> factory = VaultProperties.factory();
}
